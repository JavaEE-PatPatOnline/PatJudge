package cn.edu.buaa.patpat.judge.utils.diff;

import cn.edu.buaa.patpat.judge.config.Globals;
import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRowGenerator;

import java.util.List;

public class AdvancedDiffProvider implements IDiffProvider {
    @Override
    public String diff(List<String> expected, List<String> actual) {
        // Remove empty lines at the beginning and end, and strip each line.
        expected = expected.stream().map(String::strip).filter(s -> !s.isEmpty()).toList();
        actual = actual.stream().map(String::strip).filter(s -> !s.isEmpty()).toList();

        DiffRowGenerator generator = DiffRowGenerator.create()
                .showInlineDiffs(true)
                .inlineDiffByWord(true)
                .oldTag(f -> "**")
                .newTag(f -> "~~")
                .build();
        List<DiffRow> rows = generator.generateDiffRows(expected, actual);

        StringBuilder sb = new StringBuilder();
        sb.append("|Line|Expected|Actual|\n");
        sb.append("|:--:|:------:|:----:|\n");
        int line = 0;
        int count = 0;
        for (DiffRow row : rows) {
            if (!row.getOldLine().equals(row.getNewLine())) {
                sb.append('|').append(line).append('|')
                        .append(row.getOldLine()).append('|')
                        .append(row.getNewLine().length() > Globals.MAX_MESSAGE_LENGTH ? "*Line too long!*" : row.getNewLine())
                        .append('|').append('\n');
                count++;
                if (count > Globals.MAX_DIFF_ROWS) {
                    sb.append("|...|...|...|\n");
                    break;
                }
            }
            line++;
        }

        return count == 0 ? null : sb.toString();
    }
}
