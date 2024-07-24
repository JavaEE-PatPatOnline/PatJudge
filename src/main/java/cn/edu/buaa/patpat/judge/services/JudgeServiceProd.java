package cn.edu.buaa.patpat.judge.services;

import cn.edu.buaa.patpat.judge.config.RabbitMqConfigProd;
import cn.edu.buaa.patpat.judge.dto.JudgeRequest;
import cn.edu.buaa.patpat.judge.dto.JudgeResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@Profile({ "stag", "prod" })
public class JudgeServiceProd extends JudgeService {
    @RabbitListener(queues = RabbitMqConfigProd.PENDING)
    public void receive(JudgeRequest request) {
        receiveImpl(request);
    }

    @Override
    protected void sendImpl(JudgeResponse response) {
        rabbitTemplate.convertAndSend(RabbitMqConfigProd.RESULT, response);
    }
}
