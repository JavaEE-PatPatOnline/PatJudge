package cn.edu.buaa.patpat.judge.utils.diff;

import java.util.List;

public interface IDiffProvider {
    /**
     * If the two lists are equal, return null.
     * Otherwise, return a Markdown text that describes the difference.
     * <p>
     * Will skip whitespaces at the beginning and end of each line.
     * And will ignore empty lines in actual list.
     * </p>
     *
     * @param expected The expected lines of answer.
     * @param actual   The actual lines of answer.
     * @return The Markdown text that describes the difference. null if the two lists are equal.
     */
    String diff(List<String> expected, List<String> actual);
}
