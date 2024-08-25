package cn.edu.buaa.patpat.judge.services;

import cn.edu.buaa.patpat.judge.extensions.judge.JudgeErrorException;
import cn.edu.buaa.patpat.judge.extensions.judge.JudgeFailedException;

public interface ICompiler {
    void compileCode(String bin, String path) throws JudgeErrorException, JudgeFailedException;
}
