#include <stdlib.h>
#include <string.h>
#include "contiki.h"
#include "coap-engine.h"
#include "dev/leds.h"

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "res_cooling"
#define LOG_LEVEL LOG_LEVEL_DBG


static void bypass_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);

RESOURCE(res_cooling,
         "title=\"Bypass notifier\";rt=\"Control\"",
         NULL,
         NULL,
         bypass_put_handler,
         NULL);

bool cool_on = false;

static void bypass_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset) {
	size_t len = 0;
	const char *text = NULL;

	len = coap_get_payload(request, (const uint8_t**)&text);
	if(len <= 0 || len > 4)
		goto error;
	
	if(strncmp(text, "ON", len) == 0 && !cool_on) {
		cool_on = true;
		leds_set(LEDS_GREEN);
		LOG_INFO("COOLING ON\n");
	} else if(strncmp(text, "OFF", len) == 0 && cool_on) {
		cool_on = false;
		leds_set(LEDS_RED);
		LOG_INFO("COOLING OFF\n");
	}
	else
		goto error;

	return;
error:
	coap_set_status_code(response, BAD_REQUEST_4_00);
}
