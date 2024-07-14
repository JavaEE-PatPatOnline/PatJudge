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
                return String.format("Line %d: expected `%s`, but got `%s`", line, expectedLine, actualLine);
            }
            line++;
        }

        if (expectedIt.hasNext()) {
            return "Output ends prematurely";
        }
        if (actualIt.hasNext()) {
            return "Output has extra lines";
        }

        return null;
    }
}
