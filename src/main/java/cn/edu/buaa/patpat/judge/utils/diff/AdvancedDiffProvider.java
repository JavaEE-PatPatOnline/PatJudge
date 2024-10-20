/*
 * Copyright (C) Patpat Online 2024
 * Made with love by Tony Skywalker
 */

package cn.edu.buaa.patpat.judge.utils.diff;

import cn.edu.buaa.patpat.judge.config.Globals;
import cn.edu.buaa.patpat.judge.utils.Messages;
import com.github.difflib.text.DiffRow;
import com.github.difflib.text.DiffRowGenerator;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
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
        sb.append("| 行号 | 期望输出 | 实际输出 |\n");
        sb.append("|:----:|:-------:|:-------:|\n");
        int line = 0;
        int count = 0;
        for (DiffRow row : rows) {
            if (!row.getOldLine().equals(row.getNewLine())) {
                sb.append("| ").append(line).append(" | ")
                        .append(postProcessDiff(row.getOldLine(), "**")).append(" | ")
                        .append(Messages.truncateIfTooLong(postProcessDiff(row.getNewLine(), "~~"), Globals.MAX_DIFF_CHARS))
                        .append(" |\n");
                count++;
                if (count > Globals.MAX_DIFF_ROWS) {
                    sb.append("| ... | ... | ... |\n");
                    break;
                }
            }
            line++;
        }

        return count == 0 ? null : sb.toString();
    }

    /**
     * Move space in paired ** and ~~ to the outside.
     * e.g. a**  b **c to a  **b** c
     *
     * @param line The line to be processed.
     * @return The processed line.
     * @apiNote This method is used to post-process the diff result.
     * Because the frontend cannot handle bold that contains spaces, we need to
     * move the spaces outside the bold.
     * <p>
     * And to add unit test for this method, I made it public, which is not a
     * good practice.
     */
    public String postProcessDiff(String line, String tag) {
        try {
            return postProcessDiffImpl(line, tag);
        } catch (Exception e) {
            log.error("Failed to post-process diff ({}): {}\n{}", tag, e.getMessage(), line);
            return line;
        }
    }

    private String postProcessDiffImpl(String line, String tag) {
        StringBuilder sb = new StringBuilder();
        int left = line.indexOf(tag);
        int right = 0;
        while (left != -1) {
            sb.append(line, right, left);
            right = line.indexOf(tag, left + tag.length());
            if (right == -1) {
                sb.append(line, left, line.length());
                break;
            }

            int start = left + tag.length();
            while (start < right && line.charAt(start) == ' ') {
                sb.append(' ');
                start++;
            }
            sb.append(tag);
            int end = right - 1;
            while (end > start && line.charAt(end) == ' ') {
                end--;
            }
            sb.append(line, start, end + 1);
            sb.append(tag);
            sb.append(" ".repeat(Math.max(0, right - end - 1)));

            right += tag.length();
            left = line.indexOf(tag, right);
        }
        if (sb.length() != line.length()) {
            sb.append(line, sb.length(), line.length());
        }

        return sb.toString().replace(tag + tag, "");
    }
}
