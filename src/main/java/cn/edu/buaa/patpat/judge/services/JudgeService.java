package cn.edu.buaa.patpat.judge.services;

import cn.edu.buaa.patpat.judge.config.RabbitMqConfig;
import cn.edu.buaa.patpat.judge.dto.JudgeRequest;
import cn.edu.buaa.patpat.judge.dto.JudgeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class JudgeService {
    private final RabbitTemplate rabbitTemplate;
    private final IJudger judger;

    @RabbitListener(queues = RabbitMqConfig.PENDING)
    public void receive(JudgeRequest request) {
        log.info("Received {}:{}", request.getId(), request.getProblemId());
        JudgeResponse response = judger.judge(request);
        send(response);
    }

    public void send(JudgeResponse response) {
        rabbitTemplate.convertAndSend(RabbitMqConfig.RESULT, response);
    }
}
