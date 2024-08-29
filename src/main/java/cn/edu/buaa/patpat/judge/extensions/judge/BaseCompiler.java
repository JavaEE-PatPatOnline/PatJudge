/*
 * Copyright (C) Patpat Online 2024
 * Made with love by Tony Skywalker
 */

package cn.edu.buaa.patpat.judge.extensions.judge;

import cn.edu.buaa.patpat.judge.dto.TestCaseResult;
import cn.edu.buaa.patpat.judge.dto.TestResultEnum;
import cn.edu.buaa.patpat.judge.services.ICompiler;
import cn.edu.buaa.patpat.judge.utils.process.IProcessDescriptor;
import cn.edu.buaa.patpat.judge.utils.process.ProcessDescriptor;

import java.io.BufferedReader;
import java.io.IOException;

public abstract class BaseCompiler implements ICompiler {
    protected abstract String[] getCompileCommand(String bin, String path);

    @Override
    public void compileCode(String bin, String path) throws JudgeErrorException, JudgeFailedException {
        String[] command = getCompileCommand(bin, path);
        compile(command, path);
    }

    private void compile(String[] command, String path) throws JudgeErrorException, JudgeFailedException {
        int exitValue;
        IProcessDescriptor.ProcessError error = new IProcessDescriptor.ProcessError();

        try {
            exitValue = ProcessDescriptor.create()
                    .exec(command)
                    .setWorkingDirectory(path)
                    .redirectError(error)
                    .waitFor();
        } catch (IOException e) {
            throw new JudgeErrorException(TestCaseResult.of(TestResultEnum.CE, "Compilation I/O error"));
        } catch (InterruptedException e) {
            throw new JudgeFailedException(TestCaseResult.of(TestResultEnum.CE, "Compilation interrupted"));
        }

        if (exitValue != 0) {
            try (BufferedReader reader = error.getStdErr()) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                throw new JudgeErrorException(TestCaseResult.of(TestResultEnum.CE, sb.toString()));
            } catch (IOException ex) {
                throw new JudgeFailedException(TestCaseResult.of(TestResultEnum.CE, "Compilation error, failed to read error output"));
            }
        }
    }
}
