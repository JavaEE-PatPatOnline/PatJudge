/*
 * Copyright (C) Patpat Online 2024
 * Made with love by Tony Skywalker
 */

package cn.edu.buaa.patpat.judge.extensions.judge;

import cn.edu.buaa.patpat.judge.config.Globals;
import cn.edu.buaa.patpat.judge.config.JudgeOptions;
import cn.edu.buaa.patpat.judge.dto.*;
import cn.edu.buaa.patpat.judge.extensions.judge.exceptions.JudgeErrorException;
import cn.edu.buaa.patpat.judge.extensions.judge.exceptions.JudgeFailedException;
import cn.edu.buaa.patpat.judge.models.ProblemDescriptor;
import cn.edu.buaa.patpat.judge.utils.Medias;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * The Juggernaut is a powerful judger that can judge the submitted code.
 * <br>
 * "I am the Juggernaut!" - Juggernaut
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class Juggernaut {
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(Globals.CONCURRENT_JUDGE_LIMIT);

    private final JudgeOptions options;
    private final ObjectMapper yamlMapper;
    private final ModelMapper modelMapper;
    private final ICompiler compiler;

    @Value("${judge.timeout}")
    private long timeout;

    public JudgeResponse judge(JudgeRequest request) {
        String sandbox = options.getSandBoxPath();

        // initialize problem
        ProblemDescriptor descriptor;
        try {
            String binary = checkVersion(request.getLanguage());
            descriptor = initProblem(request.getProblemId());
            compiler.compileCode(binary, sandbox);
        } catch (JudgeErrorException e) {
            return formatResponse(request, e.getResult());
        } catch (JudgeFailedException e) {
            return formatResponse(request, e.getResult());
        }

        // prepare testcase runners
        List<Avatar> avatars = new ArrayList<>();
        var cases = descriptor.getCases();
        for (int i = 0; i < cases.size(); i++) {
            var testCase = cases.get(i);
            var workingDirectory = getTestcaseWorkingDirectory(request.getProblemId(), i + 1);
            var avatar = new Avatar(
                    options.getProblemPath(request.getProblemId()).toString(),
                    workingDirectory.toString(),
                    options.getBinPath(request.getLanguage()),
                    descriptor,
                    testCase
            );
            avatars.add(avatar);
        }

        // run test cases
        List<TestCaseResult> results = new ArrayList<>();
        try {
            List<Future<TestCaseResult>> futures = EXECUTOR_SERVICE.invokeAll(avatars);
            for (Future<TestCaseResult> future : futures) {
                results.add(future.get(timeout, TimeUnit.SECONDS));
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error(toErrorLogMessage("Failed to run test cases: {}", request), e.getMessage());
            return formatResponse(request, TestCaseResult.of(TestResultEnum.JE, "Failed to run test cases."));
        } catch (TimeoutException e) {
            log.error(toErrorLogMessage("One or more testcases timed out: {}", request), e.getMessage());
            return formatResponse(request, TestCaseResult.of(TestResultEnum.TLE, e.getMessage()));
        } catch (Exception e) {
            log.error(toErrorLogMessage("Unexpected exception when running test cases", request), e);
            return formatResponse(request, TestCaseResult.of(TestResultEnum.JE, "Contact T.A. for more information."));
        }

        // format response
        var result = TestResult.builder();
        for (TestCaseResult testCaseResult : results) {
            result.addScore(testCaseResult.getScore());
            result.addResult(testCaseResult);
        }

        return formatResponse(request, result.build());
    }

    private String checkVersion(String language) throws JudgeErrorException {
        String binary = options.getBinPath(language);
        if (binary == null) {
            throw new JudgeErrorException(TestCaseResult.of(TestResultEnum.CE, "Java version " + language + " is not supported."));
        }
        return binary;
    }

    private ProblemDescriptor initProblem(int problemId) throws JudgeErrorException {
        ProblemDescriptor descriptor;
        try {
            descriptor = yamlMapper.readValue(options.getProblemYamlPath(problemId).toFile(), ProblemDescriptor.class);

            // inject source code if needed
            Path problemInjectPath = options.getProblemInjectPath(problemId);
            if (problemInjectPath.toFile().exists()) {
                Medias.copyContent(problemInjectPath, Path.of(options.getSandBoxPath(), "src"));
            }

            // initialize testcase folders
            int count = descriptor.getCases().size();
            for (int i = 1; i <= count; i++) {
                var path = getTestcaseWorkingDirectory(problemId, i);
                Medias.ensureEmptyPath(path);
                initSecurityPolicy(path.toString());
            }

            // add init files if needed
            // WARNING: security policy can be overwritten by the init files,
            //         it can be used to bypass the security policy.
            if (descriptor.isInit()) {
                for (int i = 1; i <= count; i++) {
                    var path = getTestcaseWorkingDirectory(problemId, i);
                    Path problemInitPath = options.getProblemInitPath(problemId);
                    Medias.copyContent(problemInitPath, path);
                }
            }

            return descriptor;
        } catch (IOException e) {
            log.error("Failed to initialize problem {}: {}.", problemId, e.getMessage());
            throw new JudgeErrorException(TestCaseResult.of(TestResultEnum.JE, "Failed to initialize problem"));
        }
    }

    private JudgeResponse formatResponse(JudgeRequest request, TestCaseResult result) {
        JudgeResponse response = modelMapper.map(request, JudgeResponse.class);
        response.setScore(result.getScore());
        response.setResult(TestResult.builder().fatalError(result).build());
        return response;
    }

    private JudgeResponse formatResponse(JudgeRequest request, TestResult result) {
        JudgeResponse response = modelMapper.map(request, JudgeResponse.class);
        response.setScore(result.getScore());
        response.setResult(result);
        return response;
    }

    private void initSecurityPolicy(String path) throws IOException {
        String content = String.format("""
                        grant {
                            permission java.io.FilePermission "%s", "read, write, execute, delete";
                        };
                        """,
                Path.of(path, "-"));
        Files.writeString(Path.of(path, Globals.POLICY_FILENAME), content);
    }

    private Path getTestcaseWorkingDirectory(int problemId, int testcaseId) {
        return Path.of(options.getSandBoxPath(), "test" + testcaseId);
    }

    private String toErrorLogMessage(String message, JudgeRequest request) {
        return "[Submission " + request.getId() + "] " + message;
    }
}
