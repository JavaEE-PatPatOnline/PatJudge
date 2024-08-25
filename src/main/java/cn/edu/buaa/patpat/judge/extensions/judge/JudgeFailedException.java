package cn.edu.buaa.patpat.judge.extensions.judge;

import cn.edu.buaa.patpat.judge.dto.TestCaseResult;
import lombok.Getter;

@Getter
public class JudgeFailedException extends Exception {
    private final TestCaseResult result;

    public JudgeFailedException(TestCaseResult result) {
        this.result = result;
    }
}
