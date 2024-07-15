package cn.edu.buaa.patpat.judge.services.impl;

import cn.edu.buaa.patpat.judge.config.Globals;
import cn.edu.buaa.patpat.judge.dto.TestCaseResult;
import cn.edu.buaa.patpat.judge.dto.TestResultEnum;
import cn.edu.buaa.patpat.judge.models.CaseDescriptor;
import cn.edu.buaa.patpat.judge.models.ProblemDescriptor;
import cn.edu.buaa.patpat.judge.utils.diff.AdvancedDiffProvider;
import cn.edu.buaa.patpat.judge.utils.diff.BasicDiffProvider;
import cn.edu.buaa.patpat.judge.utils.diff.IDiffProvider;
import cn.edu.buaa.patpat.judge.utils.process.IProcessDescriptor;
import cn.edu.buaa.patpat.judge.utils.process.ProcessDescriptor;
import lombok.AllArgsConstructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

@AllArgsConstructor
public class Avatar implements Callable<TestCaseResult> {
    /**
     * The path of the test case data.
     */
    private final String problemPath;

    /**
     * The path of the compiled class.
     */
    private final String classPath;

    private final String binPath;

    private final ProblemDescriptor descriptor;
    private final CaseDescriptor testCase;

    @Override
    public TestCaseResult call() throws Exception {
        String exe = Path.of(binPath, "java").toString();

        Path inputPath = Path.of(problemPath, "tests", testCase.getId() + ".in");
        Path outputPath = Path.of(problemPath, "tests", testCase.getId() + ".out");

        IProcessDescriptor.ProcessOutput output = new IProcessDescriptor.ProcessOutput();
        IProcessDescriptor.ProcessError error = new IProcessDescriptor.ProcessError();
        int exitValue;

        try {
            exitValue = ProcessDescriptor.create()
                    .exec(exe, "-classpath", ".", descriptor.getMainClass())
                    .setWorkingDirectory(classPath)
                    .redirectInput(inputPath.toString())
                    .redirectOutput(output)
                    .redirectError(error)
                    .waitFor(descriptor.getTimeLimit());
        } catch (IOException | InterruptedException e) {
            return TestCaseResult.of(TestResultEnum.JE, "Failed to run the test case " + testCase.getId());
        } catch (TimeoutException e) {
            return TestCaseResult.of(TestResultEnum.TLE, e.getMessage());
        }

        if (exitValue != 0) {
            return TestCaseResult.of(TestResultEnum.RE, truncateMessage(error.getStdErr()));
        }

        List<String> expected;
        List<String> actual;
        try {
            expected = Files.readAllLines(outputPath);
            actual = output.getStdOut().lines().toList();
        } catch (IOException e) {
            throw new JudgeFailedException(TestCaseResult.of(TestResultEnum.JE, "Unable to read output file"));
        }

        // Compare the output.
        String diff = getDiffProvider(descriptor.getMode()).diff(expected, actual);
        if (diff == null) {
            return TestCaseResult.ac(testCase.getScore());
        } else {
            return TestCaseResult.wa(diff);
        }
    }

    private String truncateMessage(BufferedReader reader) {
        StringBuilder sb = new StringBuilder();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (sb.length() + line.length() <= Globals.MAX_MESSAGE_LENGTH) {
                    sb.append(line).append("\n");
                } else {
                    sb.append(line, 0, Globals.MAX_MESSAGE_LENGTH - sb.length());
                    sb.append("...");
                    break;
                }
            }
        } catch (IOException e) {
            return "Failed to read error message";
        }

        return sb.toString();
    }

    private IDiffProvider getDiffProvider(String mode) throws JudgeFailedException {
        if ("advanced".equalsIgnoreCase(mode)) {
            return new AdvancedDiffProvider();
        }
        return new BasicDiffProvider();
    }
}