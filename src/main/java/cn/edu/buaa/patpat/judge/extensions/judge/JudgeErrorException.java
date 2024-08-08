package cn.edu.buaa.patpat.judge.extensions.judge;

import cn.edu.buaa.patpat.judge.dto.TestCaseResult;
import lombok.Getter;

@Getter
public class JudgeErrorException extends Exception {
    private final TestCaseResult result;

    public JudgeErrorException(TestCaseResult result) {
        this.result = result;
    }
}
