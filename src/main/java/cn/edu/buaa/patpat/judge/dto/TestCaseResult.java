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

    public static TestCaseResult of(TestResultEnum flag, String result) {
        return new TestCaseResult(flag.name(), result);
    }

    public static TestCaseResult of(TestResultEnum flag) {
        return of(flag, null);
    }
}