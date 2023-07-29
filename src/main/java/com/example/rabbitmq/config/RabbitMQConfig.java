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

@Configuration
public class RabbitMQConfig {

	@Value("${sampleapp.rabbitmq.queue.name}")
	private String queueName;

	@Value("${sampleapp.rabbitmq.queue.ttl}")
	private Integer queueTtl;

	@Value("${sampleapp.rabbitmq.queue.topic.exchange}")
	String topicExchange;

	@Value("${sampleapp.rabbitmq.queue.routing.key}")
	String routingKey;

	@Value("${sampleapp.rabbitmq.dead.letter.queue.topic.exchange}")
	String deadLetterTopicExchange;

	@Value("${sampleapp.rabbitmq.dead.letter.queue.routing.key}")
	String deadLetterRoutingKey;

	@Value("${sampleapp.rabbitmq.dead.letter.queue.name}")
	String deadLetterQueueName;

	@Bean
	TopicExchange deadLetterExchange() {
		return new TopicExchange(deadLetterTopicExchange);
	}

	/*
	 * @Bean DirectExchange deadLetterExchange() { return new
	 * DirectExchange(deadLetterTopicExchange); }
	 */

	/*
	 * @Bean DirectExchange exchange() { return new DirectExchange(topicExchange); }
	 */

	@Bean
	public TopicExchange exchange() {
		return new TopicExchange(topicExchange);
	}

	@Bean
	Queue dlq() {
		return QueueBuilder.durable(deadLetterQueueName).build();
	}

	@Bean
	Queue queue() {
		return QueueBuilder.durable(queueName).withArgument("x-dead-letter-exchange", deadLetterTopicExchange)
				.withArgument("x-dead-letter-routing-key", deadLetterRoutingKey)
				.withArgument("x-message-ttl", queueTtl).build();
	}

	@Bean
	Binding DLQbinding() {
		return BindingBuilder.bind(dlq()).to(deadLetterExchange()).with(deadLetterRoutingKey);
	}

	@Bean
	Binding binding() {
		return BindingBuilder.bind(queue()).to(exchange()).with(routingKey);
	}

	@Bean
	public MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(jsonMessageConverter());
		return rabbitTemplate;
	}
}