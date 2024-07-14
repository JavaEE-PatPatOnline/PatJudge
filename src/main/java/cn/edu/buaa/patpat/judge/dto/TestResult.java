package cn.edu.buaa.patpat.judge.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Complete judge result for a problem.
 */
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TestResult {
    private int score;

    /**
     * Compilation error is a special case, in which all testcases are skipped.
     * In this case, the {@code results} field contains only one element whose flag is "CE".
     */
    private boolean compileError;

    private List<TestCaseResult> results;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int score;
        private boolean compileError;
        private List<TestCaseResult> results = new ArrayList<>();

        public Builder addScore(int score) {
            this.score += score;
            return this;
        }

        public Builder compileError(TestCaseResult result) {
            this.compileError = true;
            return addResult(result);
        }

        public Builder addResult(TestCaseResult result) {
            results.add(result);
            return this;
        }

        public TestResult build() {
            return new TestResult(score, compileError, results);
        }
    }
}
