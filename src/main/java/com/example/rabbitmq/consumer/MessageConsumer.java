package com.example.rabbitmq.consumer;

import java.io.IOException;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.example.rabbitmq.dto.Employee;
import com.rabbitmq.client.Channel;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MessageConsumer {

	private static final String SAMPLE_MAIN_QUEUE_NAME = "sample-main-queue";
	
	@Value("${sampleapp.rabbitmq.dead.letter.queue.topic.exchange}")
	String deadLetterTopicExchange;

	@Value("${sampleapp.rabbitmq.dead.letter.queue.routing.key}")
	String deadLetterRoutingKey;
	
	@Autowired
	private RabbitTemplate template;

	@RabbitListener(queues = SAMPLE_MAIN_QUEUE_NAME)
	public void consumeNewStudentRecord(Employee emp, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag)
			throws IOException {

		if (emp != null) {
			if (null != emp.getEmpCode() && emp.getEmpCode().isEmpty()) {
				channel.basicAck(tag, false);
				template.convertAndSend(deadLetterTopicExchange, deadLetterRoutingKey, emp);
			} else {
				log.info("new Employee record has been processed: " + emp);
			}
		}

	}

}
