package cn.edu.buaa.patpat.judge.services;

import cn.edu.buaa.patpat.judge.services.impl.JudgeErrorException;
import cn.edu.buaa.patpat.judge.services.impl.JudgeFailedException;
import cn.edu.buaa.patpat.judge.utils.process.IProcessDescriptor;

public interface ICompiler {
    void compileCode(String bin, String path) throws JudgeErrorException, JudgeFailedException;
}
