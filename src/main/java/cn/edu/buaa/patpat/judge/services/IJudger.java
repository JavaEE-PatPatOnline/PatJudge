package cn.edu.buaa.patpat.judge.services;

import cn.edu.buaa.patpat.judge.dto.JudgeRequest;
import cn.edu.buaa.patpat.judge.dto.JudgeResponse;

public interface IJudger {
    JudgeResponse judge(JudgeRequest request);
}
