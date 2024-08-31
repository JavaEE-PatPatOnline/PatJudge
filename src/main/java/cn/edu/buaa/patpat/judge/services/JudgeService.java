/*
 * Copyright (C) Patpat Online 2024
 * Made with love by Tony Skywalker
 */

package cn.edu.buaa.patpat.judge.services;

import cn.edu.buaa.patpat.judge.config.Globals;
import cn.edu.buaa.patpat.judge.config.JudgeOptions;
import cn.edu.buaa.patpat.judge.dto.*;
import cn.edu.buaa.patpat.judge.extensions.judge.Juggernaut;
import cn.edu.buaa.patpat.judge.utils.Medias;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

@Slf4j
public abstract class JudgeService {
    @Autowired
    protected RabbitTemplate rabbitTemplate;
    @Autowired
    private Juggernaut judger;
    @Autowired
    private JudgeOptions options;
    @Autowired
    private ModelMapper modelMapper;

    protected abstract void sendImpl(JudgeResponse response);

    public void send(JudgeResponse response) {
        log.info("Send {}:{}", response.getId(), response.getProblemId());
        sendImpl(response);
    }

    protected void receiveImpl(JudgeRequest request) {
        log.info("Received {}:{}", request.getId(), request.getProblemId());
        var startTime = LocalDateTime.now();
        JudgeResponse response;
        try {
            prepareJudge(request);
            response = judger.judge(request);
        } catch (Exception e) {
            log.error("Failed to judge {}: {}", request.getId(), e.getMessage());
            response = modelMapper.map(request, JudgeResponse.class);
            response.setScore(0);
            response.setResult(TestResult.builder().fatalError(TestCaseResult.of(TestResultEnum.JE, "N/A")).build());
        }
        response.setStartTime(startTime);
        response.setEndTime(LocalDateTime.now());
        send(response);
    }

    private void prepareJudge(JudgeRequest request) throws IOException {
        String sandbox = options.getSandBoxPath();
        Medias.ensureEmptyPath(sandbox);

        Path submissionPath = Path.of(options.getSubmissionRoot(), request.getRecord());
        Path sourcePath = Path.of(sandbox, "src");
        Medias.copyDirectory(submissionPath, sourcePath);

        initSecurityPolicy(sandbox);
    }

    private void initSecurityPolicy(String path) throws IOException {
        String content = String.format("""
                        grant {
                            permission java.io.FilePermission "%s", "read, write";
                        };
                        """,
                Path.of(path, "-"));
        Files.writeString(Path.of(path, Globals.POLICY_FILENAME), content);
    }
}
