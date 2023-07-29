package com.example.rabbitmq.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.rabbitmq.dto.Employee;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/employee")
@Slf4j
public class EmployeeRegistrationController {

	@Autowired
	private RabbitTemplate template;

	@Value("${sampleapp.rabbitmq.queue.topic.exchange}")
	String topicExchange;
	
	@Value("${sampleapp.rabbitmq.queue.routing.key}")
	String routingKey;

	
	@PostMapping()
	public String addEmployee(@RequestBody Employee employee) {
		template.convertAndSend(topicExchange,routingKey, employee);
		log.info("Writing message to queue");
		return "Employee added!";
		
		
	}

}
