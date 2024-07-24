package cn.edu.buaa.patpat.judge.services;

import cn.edu.buaa.patpat.judge.config.RabbitMqConfigDev;
import cn.edu.buaa.patpat.judge.dto.JudgeRequest;
import cn.edu.buaa.patpat.judge.dto.JudgeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

@Slf4j
public abstract class JudgeService {
    @Autowired
    protected RabbitTemplate rabbitTemplate;
    @Autowired
    protected IJudger judger;

    @RabbitListener(queues = RabbitMqConfigDev.PENDING)
    public void receive(JudgeRequest request) {
    }

    public void send(JudgeResponse response) {
        log.info("Send {}:{}", response.getId(), response.getProblemId());
        sendImpl(response);
    }

    protected abstract void sendImpl(JudgeResponse response);

    protected void receiveImpl(JudgeRequest request) {
        log.info("Received {}:{}", request.getId(), request.getProblemId());
        var startTime = LocalDateTime.now();
        JudgeResponse response = judger.judge(request);
        response.setStartTime(startTime);
        response.setEndTime(LocalDateTime.now());
        send(response);
    }
}
