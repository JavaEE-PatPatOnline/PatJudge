package cn.edu.buaa.patpat.judge.services;

import cn.edu.buaa.patpat.judge.config.RabbitMqConfigDev;
import cn.edu.buaa.patpat.judge.dto.JudgeRequest;
import cn.edu.buaa.patpat.judge.dto.JudgeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({ "dev" })
public class JudgeServiceDev extends JudgeService {
    @RabbitListener(queues = RabbitMqConfigDev.PENDING)
    public void receive(JudgeRequest request) {
        receiveImpl(request);
    }

    @Override
    protected void sendImpl(JudgeResponse response) {
        rabbitTemplate.convertAndSend(RabbitMqConfigDev.RESULT, response);
    }
}
