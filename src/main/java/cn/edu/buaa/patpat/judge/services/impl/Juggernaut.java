package cn.edu.buaa.patpat.judge.services.impl;

import cn.edu.buaa.patpat.judge.dto.JudgeRequest;
import cn.edu.buaa.patpat.judge.dto.JudgeResponse;
import cn.edu.buaa.patpat.judge.services.IJudger;
import org.springframework.stereotype.Component;

/**
 * The Juggernaut is a powerful judger that can judge the submitted code.
 * <br>
 * "I am the Juggernaut!" - Juggernaut
 */
@Component
public class Juggernaut implements IJudger {
    @Override
    public JudgeResponse judge(JudgeRequest request) {
        return null;
    }
}
