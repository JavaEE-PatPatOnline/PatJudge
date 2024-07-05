package cn.edu.buaa.patpat.judge.services;

import cn.edu.buaa.patpat.judge.config.RabbitMqConfig;
import cn.edu.buaa.patpat.judge.dto.SubmissionDto;
import cn.edu.buaa.patpat.judge.utils.Generator;
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

    @RabbitListener(queues = RabbitMqConfig.PENDING)
    public void receive(SubmissionDto dto) {
        dto.setResult(Generator.randomWord(10));
        rabbitTemplate.convertAndSend(RabbitMqConfig.RESULT, dto);
        log.info("Judged submission {}", dto.getTaskId());
    }
}
