#include "contiki.h"
#include "net/routing/routing.h"
#include "mqtt.h"
#include "net/ipv6/uip.h"
#include "net/ipv6/uip-icmp6.h"
#include "net/ipv6/sicslowpan.h"
#include "sys/etimer.h"
#include "sys/ctimer.h"
#include "lib/sensors.h"
#include "dev/button-hal.h"
#include "dev/leds.h"
#include "os/sys/log.h"
#include <string.h>
#include <strings.h>

#include <sys/node-id.h>
#include <time.h>

#define LOG_MODULE "co2"
#ifdef  MQTT_CLIENT_CONF_LOG_LEVEL
#define LOG_LEVEL MQTT_CLIENT_CONF_LOG_LEVEL
#else
#define LOG_LEVEL LOG_LEVEL_DBG
#endif

/* MQTT broker address. */
#define MQTT_CLIENT_BROKER_IP_ADDR "fd00::1"

static const char *broker_ip = MQTT_CLIENT_BROKER_IP_ADDR;

// Defaukt config values
#define DEFAULT_BROKER_PORT         1883
#define DEFAULT_PUBLISH_INTERVAL    (30 * CLOCK_SECOND)
#define PUBLISH_INTERVAL	    (5 * CLOCK_SECOND)

// We assume that the broker does not require authentication

/* Various states */
static uint8_t state;

#define STATE_INIT    		0	// initial state
#define STATE_NET_OK    	1	// Network is initialized
#define STATE_CONNECTING      	2	// Connecting to MQTT broker
#define STATE_CONNECTED       	3	// Connection successful
#define STATE_SUBSCRIBED      	4	// Topics subscription done
#define STATE_DISCONNECTED    	5	// Disconnected from MQTT broker

PROCESS_NAME(co2_analyzer_process);
AUTOSTART_PROCESSES(&co2_analyzer_process);

/* Maximum TCP segment size for outgoing segments of our socket */
#define MAX_TCP_SEGMENT_SIZE    32
#define CONFIG_IP_ADDR_STR_LEN  64

/*
 * Buffers for Client ID and Topics.
 */
#define BUFFER_SIZE 64

static char client_id[BUFFER_SIZE];
static char pub_topic[BUFFER_SIZE];
//static char sub_topic[BUFFER_SIZE];

// Periodic timer to check the state of the MQTT client
#define STATE_MACHINE_PERIODIC     (CLOCK_SECOND >> 1)
static struct etimer periodic_timer;

/*
 * The main MQTT buffers.
 * We will need to increase if we start publishing more data.
 */
#define APP_BUFFER_SIZE 512
static char app_buffer[APP_BUFFER_SIZE];

static struct mqtt_connection conn;

PROCESS(co2_analyzer_process, "co2 analyzer process");

#define MIN_CO2 0
#define MAX_CO2 100
static int co2_percentage = 50; //starting halfway
static int variation = 0;

// This function is called each time occurs a MQTT event
static void mqtt_event(struct mqtt_connection *m, mqtt_event_t event, void *data)
{
	switch(event) 
	{
		case MQTT_EVENT_CONNECTED: 
			LOG_INFO("MQTT connection acquired\n");
			state = STATE_CONNECTED;
			break;
		case MQTT_EVENT_DISCONNECTED: 
			printf("MQTT connection disconnected. Reason: %u\n", *((mqtt_event_t *)data));
			state = STATE_DISCONNECTED;
			process_poll(&co2_analyzer_process);
			break;
		case MQTT_EVENT_SUBACK: 
			#if MQTT_311
			mqtt_suback_event_t *suback_event = (mqtt_suback_event_t *)data;
			if(suback_event->success) 
				LOG_INFO("Application has subscribed to the topic\n");
			else 
				LOG_ERR("Application failed to subscribe to topic (ret code %x)\n", suback_event->return_code);
			#else
			LOG_INFO("Application has subscribed to the topic\n");
			#endif
			break;
		case MQTT_EVENT_UNSUBACK: 
			LOG_INFO("Application is unsubscribed to topic successfully\n");
			break;
		case MQTT_EVENT_PUBACK: 
			LOG_INFO("Publishing complete.\n");
			break;
		default:
			LOG_INFO("Application got a unhandled MQTT event: %i\n", event);
			break;
	}
}

static bool have_connectivity(void)
{
	return !(uip_ds6_get_global(ADDR_PREFERRED) == NULL || uip_ds6_defrt_choose() == NULL);
}

PROCESS_THREAD(co2_analyzer_process, ev, data)
{

	PROCESS_BEGIN();

	static char broker_address[CONFIG_IP_ADDR_STR_LEN];
	
	LOG_INFO("Avvio...");
	leds_set(LEDS_YELLOW);	

	// Initialize the ClientID as MAC address
	snprintf(client_id, BUFFER_SIZE, "%02x%02x%02x%02x%02x%02x",
		     linkaddr_node_addr.u8[0], linkaddr_node_addr.u8[1],
		     linkaddr_node_addr.u8[2], linkaddr_node_addr.u8[5],
		     linkaddr_node_addr.u8[6], linkaddr_node_addr.u8[7]);

	// Broker registration					 
	mqtt_register(&conn, &co2_analyzer_process, client_id, mqtt_event, MAX_TCP_SEGMENT_SIZE);
			
	state=STATE_INIT;
				    
	// Initialize periodic timer to check the status 
	etimer_set(&periodic_timer, PUBLISH_INTERVAL);

	while(true) 
	{
		PROCESS_YIELD();

		if(!((ev == PROCESS_EVENT_TIMER && data == &periodic_timer) || ev == PROCESS_EVENT_POLL))
			continue;
		
		switch(state){
			case STATE_INIT:
				LOG_INFO("state init..");
				if(have_connectivity())
					state = STATE_NET_OK;
				else
					break;
			// no break
			case STATE_NET_OK:
				LOG_INFO("Connecting to MQTT server\n"); 
			  	memcpy(broker_address, broker_ip, strlen(broker_ip));
			  
			  	mqtt_connect(&conn, broker_address, DEFAULT_BROKER_PORT,
						   (DEFAULT_PUBLISH_INTERVAL * 3) / CLOCK_SECOND,
						   MQTT_CLEAN_SESSION_ON);
			  	state = STATE_CONNECTING;
			break;
			case STATE_CONNECTED:
			case STATE_SUBSCRIBED:	
			sprintf(pub_topic, "%s", "co2");

			if (co2_percentage > 70) {
				leds_set(LEDS_RED);
			} else if (co2_percentage < 30) {
				leds_set(LEDS_GREEN);
			}
			else{
				leds_set(LEDS_YELLOW);
			}

			variation = (rand()%4)+1; 	// variation with value between [1,4]
			if((rand()%10) < 5) { 		// 50% probability of variation being positive or negative
				co2_percentage = co2_percentage + variation;
			}				
			else{
				co2_percentage = co2_percentage - variation;
			}
			
			//setting limits
			if (co2_percentage > MAX_CO2) 
				co2_percentage = MAX_CO2; 
			else if (co2_percentage < MIN_CO2)
				co2_percentage = MIN_CO2;

			LOG_INFO("New value of co2: %d%%\n", co2_percentage);
			
			sprintf(app_buffer, "{\"node\": %d, \"co2\": %d}", 50, co2_percentage);
			mqtt_publish(&conn, NULL, pub_topic, (uint8_t *)app_buffer,
			strlen(app_buffer), MQTT_QOS_LEVEL_0, MQTT_RETAIN_OFF);

			break; 
			
			case STATE_DISCONNECTED:
			LOG_ERR("Disconnected from MQTT broker\n");	
			state = STATE_INIT;
			break;
		}

		etimer_set(&periodic_timer, PUBLISH_INTERVAL);
	}
	PROCESS_END();
}
