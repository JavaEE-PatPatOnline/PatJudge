/*
 * Copyright (C) Patpat Online 2024
 * Made with love by Tony Skywalker
 */

package cn.edu.buaa.patpat.judge.utils.diff;

import java.util.List;

public class BasicDiffProvider implements IDiffProvider {
    @Override
    public String diff(List<String> expected, List<String> actual) {
        expected = expected.stream().map(String::strip).filter(s -> !s.isEmpty()).toList();
        actual = actual.stream().map(String::strip).filter(s -> !s.isEmpty()).toList();

        var expectedIt = expected.iterator();
        var actualIt = actual.iterator();
        int line = 1;

        while (expectedIt.hasNext() && actualIt.hasNext()) {
            var actualLine = actualIt.next();
            var expectedLine = expectedIt.next();
            if (!expectedLine.equals(actualLine)) {
                // Line %d: expected `%s`, but got `%s`
                return String.format("第 %d 行: 期望输出 `%s`, 实际输出 `%s`", line, expectedLine, actualLine);
            }
            line++;
        }

        if (expectedIt.hasNext()) {
            return "输出过少";
        }
        if (actualIt.hasNext()) {
            return "输出过多";
        }

        return null;
    }
}
