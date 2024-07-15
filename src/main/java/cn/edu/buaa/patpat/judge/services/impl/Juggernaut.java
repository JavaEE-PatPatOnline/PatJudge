package cn.edu.buaa.patpat.judge.services.impl;

import cn.edu.buaa.patpat.judge.config.JudgeOptions;
import cn.edu.buaa.patpat.judge.dto.*;
import cn.edu.buaa.patpat.judge.models.ProblemDescriptor;
import cn.edu.buaa.patpat.judge.services.ICompiler;
import cn.edu.buaa.patpat.judge.services.IJudger;
import cn.edu.buaa.patpat.judge.utils.Medias;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * The Juggernaut is a powerful judger that can judge the submitted code.
 * <br>
 * "I am the Juggernaut!" - Juggernaut
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class Juggernaut implements IJudger {
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(3);

    private final JudgeOptions judgeOptions;
    private final ObjectMapper yamlMapper;
    private final ModelMapper modelMapper;
    private final ICompiler compiler;

    @Override
    public JudgeResponse judge(JudgeRequest request) {
        // initialize problem
        ProblemDescriptor descriptor;
        try {
            String binary = checkVersion(request.getLanguage());
            descriptor = initProblem(request.getProblemId(), request.getId());
            compiler.compileCode(binary, judgeOptions.getJudgePath(request.getId()).toString());
        } catch (JudgeErrorException e) {
            return formatResponse(request, e.getResult());
        } catch (JudgeFailedException e) {
            return formatResponse(request, e.getResult());
        }

        // run test cases
        List<Avatar> avatars = descriptor.getCases().stream()
                .map(testCase -> new Avatar(
                        judgeOptions.getProblemPath(request.getProblemId()).toString(),
                        judgeOptions.getJudgeClassPath(request.getId()).toString(),
                        judgeOptions.getBinPath(request.getLanguage()),
                        descriptor,
                        testCase)
                ).toList();
        List<TestCaseResult> results = new ArrayList<>();
        try {
            List<Future<TestCaseResult>> futures = EXECUTOR_SERVICE.invokeAll(avatars);
            for (Future<TestCaseResult> future : futures) {
                results.add(future.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            return formatResponse(request, TestCaseResult.of(TestResultEnum.JE, "Failed to run test cases."));
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
        String binary = judgeOptions.getBinPath(language);
        if (binary == null) {
            throw new JudgeErrorException(TestCaseResult.of(TestResultEnum.CE, "Java version " + language + " is not supported."));
        }
        return binary;
    }

    private ProblemDescriptor initProblem(int problemId, int submissionId) throws JudgeErrorException {
        ProblemDescriptor descriptor;
        try {
            descriptor = yamlMapper.readValue(judgeOptions.getProblemYamlPath(problemId).toFile(), ProblemDescriptor.class);
            if (descriptor.isInit()) {
                Path problemInitPath = judgeOptions.getProblemInitPath(problemId);
                Path judgePath = judgeOptions.getJudgeSourcePath(submissionId);
                Medias.copyContent(problemInitPath, judgePath);
            }
            return descriptor;
        } catch (IOException e) {
            log.error("Failed to initialize problem {}: {}.", problemId, e.getMessage());
            throw new JudgeErrorException(TestCaseResult.of(TestResultEnum.JE, "Failed to initialize problem."));
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
}
