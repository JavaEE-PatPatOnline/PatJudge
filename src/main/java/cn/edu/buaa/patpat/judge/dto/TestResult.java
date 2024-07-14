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
     * If some error occurs that prevent test cases from running, this field is set to true.
     * In this case, the {@code results} field contains only one element.
     */
    private boolean fatal;

    private List<TestCaseResult> results;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<TestCaseResult> results = new ArrayList<>();
        private int score;
        private boolean fatal;

        public Builder addScore(int score) {
            this.score += score;
            return this;
        }

        public Builder fatalError(TestCaseResult result) {
            this.fatal = true;
            return addResult(result);
        }

        public Builder addResult(TestCaseResult result) {
            results.add(result);
            return this;
        }

        public TestResult build() {
            return new TestResult(score, fatal, results);
        }
    }
}
