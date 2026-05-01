package com.ssu.ongi.common.mqtt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MqttPublisher {

    private final MqttPahoMessageHandler mqttMessageHandler;

    public void publish(String topic, String payload) {
        log.info("[MQTT] publish topic={} payload={}", topic, payload);
        mqttMessageHandler.handleMessage(
                MessageBuilder.withPayload(payload)
                        .setHeader("mqtt_topic", topic)
                        .build()
        );
    }
}
