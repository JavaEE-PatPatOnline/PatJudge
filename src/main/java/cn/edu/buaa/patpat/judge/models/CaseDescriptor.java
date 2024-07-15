package cn.edu.buaa.patpat.judge.models;

import lombok.Data;

import java.util.List;
import java.util.stream.Stream;

@Data
public class CaseDescriptor {
    private int id;
    private String description;
    private int score;
}
