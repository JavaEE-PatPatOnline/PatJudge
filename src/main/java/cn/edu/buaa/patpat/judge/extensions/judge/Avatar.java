/*
 * Copyright (C) Patpat Online 2024
 * Made with love by Tony Skywalker
 */

package cn.edu.buaa.patpat.judge.extensions.judge;

import cn.edu.buaa.patpat.judge.config.Globals;
import cn.edu.buaa.patpat.judge.dto.TestCaseResult;
import cn.edu.buaa.patpat.judge.dto.TestResultEnum;
import cn.edu.buaa.patpat.judge.extensions.judge.exceptions.JudgeFailedException;
import cn.edu.buaa.patpat.judge.models.CaseDescriptor;
import cn.edu.buaa.patpat.judge.models.ProblemDescriptor;
import cn.edu.buaa.patpat.judge.utils.Messages;
import cn.edu.buaa.patpat.judge.utils.diff.AdvancedDiffProvider;
import cn.edu.buaa.patpat.judge.utils.diff.BasicDiffProvider;
import cn.edu.buaa.patpat.judge.utils.diff.IDiffProvider;
import cn.edu.buaa.patpat.judge.utils.process.IProcessDescriptor;
import cn.edu.buaa.patpat.judge.utils.process.ProcessDescriptor;
import lombok.AllArgsConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

@AllArgsConstructor
public class Avatar implements Callable<TestCaseResult> {
    /**
     * The message will be prompted when the Security Manager is enabled.
     * FIXME: I don't know how to disable this in Java 17.
     */
    private static final String POLICY_WARNING = """
            WARNING: A command line option has enabled the Security Manager
            WARNING: The Security Manager is deprecated and will be removed in a future release
            """;

    /**
     * The path of the test case data.
     */
    private final String problemPath;
    /**
     * The path to the project to be judged.
     */
    private final String judgePath;
    /**
     * The path to the Java runtime.
     */
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
                    .exec(exe,
                            "-Djava.security.manager",
                            "-Djava.security.policy=" + Globals.POLICY_FILENAME,
                            "-classpath", "out",
                            descriptor.getMainClass())
                    .setWorkingDirectory(judgePath)
                    .redirectInput(inputPath.toString())
                    .redirectOutput(output)
                    .redirectError(error)
                    .waitFor(descriptor.getTimeLimit());
        } catch (IOException | InterruptedException e) {
            return TestCaseResult.of(TestResultEnum.JE, "Failed to run the test case " + testCase.getId());
        } catch (TimeoutException e) {
            return TestCaseResult.of(TestResultEnum.TLE, e.getMessage());
        } catch (Exception e) {
            return TestCaseResult.of(TestResultEnum.RE, Messages.truncate(e.getMessage(), POLICY_WARNING));
        }

        if (exitValue != 0) {
            return TestCaseResult.of(TestResultEnum.RE, Messages.truncate(error.getStdErr(), POLICY_WARNING));
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

    private IDiffProvider getDiffProvider(String mode) {
        if ("advanced".equalsIgnoreCase(mode)) {
            return new AdvancedDiffProvider();
        }
        return new BasicDiffProvider();
    }
}
