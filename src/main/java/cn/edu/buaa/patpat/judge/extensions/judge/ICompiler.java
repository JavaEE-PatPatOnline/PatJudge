/*
 * Copyright (C) Patpat Online 2024
 * Made with love by Tony Skywalker
 */

package cn.edu.buaa.patpat.judge.extensions.judge;

import cn.edu.buaa.patpat.judge.extensions.judge.exceptions.JudgeErrorException;
import cn.edu.buaa.patpat.judge.extensions.judge.exceptions.JudgeFailedException;

public interface ICompiler {
    void compileCode(String bin, String path) throws JudgeErrorException, JudgeFailedException;
}
