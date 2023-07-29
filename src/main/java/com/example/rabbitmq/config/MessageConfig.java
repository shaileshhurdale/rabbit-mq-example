package com.example.rabbitmq.config;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@Configuration
public class MessageConfig {

	@Value("${rabbitmq.queue.name}")
	private String queueName;
	
	@Value("${rabbitmq.queue.ttl}")
	private Object queueTtl;

	@Value("${rabbitmq.queue.topic.exchange}")
	String topicExchange;
	
	@Value("${rabbitmq.queue.routing.key}")
	String routingKey;
	
	@Bean
	public Queue getMessagingQueue() {
		return QueueBuilder.durable(queueName)
                .withArgument("x-message-ttl", queueTtl)
                .build();
	}

	@Bean
	public TopicExchange getTopicExchange() {
		return new TopicExchange(topicExchange);
	}

	@Bean
	public Binding binding(Queue queue, TopicExchange exchange) {
		return BindingBuilder.bind(queue).to(exchange).with(routingKey);
	}

	@Bean
	public MessageConverter converter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public AmqpTemplate template(ConnectionFactory connectionFactory) {
		final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(converter());
		return rabbitTemplate;
	}

}
