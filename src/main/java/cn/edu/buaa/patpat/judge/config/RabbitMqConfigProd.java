/*
 * Copyright (C) Patpat Online 2024
 * Made with love by Tony Skywalker
 */

package cn.edu.buaa.patpat.judge.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@RequiredArgsConstructor
@Profile({ "stag", "prod" })
public class RabbitMqConfigProd {
    /**
     * All tasks will be sent to this queue.
     */
    public static final String PENDING = "q.judge.pending";

    /**
     * All results will be read from this queue.
     */
    public static final String RESULT = "q.judge.result";
    private final CachingConnectionFactory connectionFactory;

    @Bean
    public Queue getPendingQueue() {
        return new Queue(PENDING, true);
    }

    @Bean
    public Queue getResultQueue() {
        return new Queue(RESULT, true);
    }

    @Bean
    public RabbitTemplate getRabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(getConverter());
        return template;
    }

    @Bean
    public Jackson2JsonMessageConverter getConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
