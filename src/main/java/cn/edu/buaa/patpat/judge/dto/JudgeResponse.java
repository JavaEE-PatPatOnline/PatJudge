/*
 * Copyright (C) Patpat Online 2024
 * Made with love by Tony Skywalker
 */

package cn.edu.buaa.patpat.judge.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JudgeResponse {
    private int id;
    private int problemId;
    private String language;

    private int score;
    private TestResult result;

    private LocalDateTime submitTime;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private Object payload;
}
