package cn.edu.buaa.patpat.judge.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SubmissionDto {
    private int id;
    private int taskId;
    private String result;
}
