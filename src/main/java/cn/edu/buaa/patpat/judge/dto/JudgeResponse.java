package cn.edu.buaa.patpat.judge.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class JudgeResponse {
    private int id;
    private int problemId;
    private String language;

    private TestResult result;

    private LocalDateTime submitTime;
    private LocalDateTime judgeTime;
    private LocalDateTime completeTime;

    private Object payload;
}
