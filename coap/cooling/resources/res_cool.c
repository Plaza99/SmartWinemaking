#include <stdlib.h>
#include <string.h>
#include "contiki.h"
#include "coap-engine.h"
#include "dev/leds.h"

/* Log configuration */
#include "sys/log.h"
#define LOG_MODULE "res_bypass"
#define LOG_LEVEL LOG_LEVEL_DBG


static void bypass_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset);

RESOURCE(res_bypass,
         "title=\"Bypass notifier\";rt=\"Control\"",
         NULL,
         NULL,
         bypass_put_handler,
         NULL);

bool bypass_up = false;

static void bypass_put_handler(coap_message_t *request, coap_message_t *response, uint8_t *buffer, uint16_t preferred_size, int32_t *offset) {
	size_t len = 0;
	const char *text = NULL;

	len = coap_get_payload(request, (const uint8_t**)&text);
	if(len <= 0 || len > 4)
		goto error;
	
	if(strncmp(text, "UP", len) == 0 && !bypass_up) {
		bypass_up = true;
		leds_set(LEDS_GREEN);
		LOG_INFO("BYPASS UP\n");
	} else if(strncmp(text, "DOWN", len) == 0 && bypass_up) {
		bypass_up = false;
		leds_set(LEDS_RED);
		LOG_INFO("BYPASS DOWN\n");
	}
	else
		goto error;

	return;
error:
	coap_set_status_code(response, BAD_REQUEST_4_00);
}
