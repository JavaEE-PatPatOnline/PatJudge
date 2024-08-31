/*
 * Copyright (C) Patpat Online 2024
 * Made with love by Tony Skywalker
 */

package cn.edu.buaa.patpat.judge.extensions.judge.exceptions;

import cn.edu.buaa.patpat.judge.dto.TestCaseResult;
import lombok.Getter;

@Getter
public class JudgeFailedException extends Exception {
    private final TestCaseResult result;

    public JudgeFailedException(TestCaseResult result) {
        this.result = result;
    }
}
