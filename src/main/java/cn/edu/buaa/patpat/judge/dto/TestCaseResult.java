/*
 * Copyright (C) Patpat Online 2024
 * Made with love by Tony Skywalker
 */

package cn.edu.buaa.patpat.judge.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Result for a single test case in a problem.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TestCaseResult {
    /**
     * @see TestResultEnum
     */
    private String flag;

    /**
     * Test result in Markdown.
     */
    private String result;

    private int score;

    public static TestCaseResult of(TestResultEnum flag, String result) {
        return new TestCaseResult(flag.name(), result, 0);
    }

    public static TestCaseResult of(TestResultEnum flag, String result, int score) {
        return new TestCaseResult(flag.name(), result, score);
    }

    public static TestCaseResult of(TestResultEnum flag) {
        return of(flag, null);
    }

    public static TestCaseResult ac(int score) {
        return of(TestResultEnum.AC, null, score);
    }

    public static TestCaseResult wa(String message) {
        return of(TestResultEnum.WA, message);
    }
}