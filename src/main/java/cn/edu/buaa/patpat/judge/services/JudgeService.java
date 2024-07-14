package cn.edu.buaa.patpat.judge.services;

import cn.edu.buaa.patpat.judge.config.RabbitMqConfig;
import cn.edu.buaa.patpat.judge.dto.JudgeRequest;
import cn.edu.buaa.patpat.judge.dto.JudgeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class JudgeService {
    private final RabbitTemplate rabbitTemplate;
    private final IJudger judger;

    @RabbitListener(queues = RabbitMqConfig.PENDING)
    public void receive(JudgeRequest request) {
        log.info("Received {}:{}", request.getId(), request.getProblemId());
        request.setJudgeTime(LocalDateTime.now());
        JudgeResponse response = judger.judge(request);
        response.setCompleteTime(LocalDateTime.now());
        send(response);
    }

    public void send(JudgeResponse response) {
        rabbitTemplate.convertAndSend(RabbitMqConfig.RESULT, response);
    }
}
