package cn.edu.buaa.patpat.judge.services.impl;

import java.nio.file.Path;

public class LinuxCompiler extends BaseCompiler {
    @Override
    protected String[] getCompileCommand(String bin, String path) {
        String exe = Path.of(bin, "javac").toString();
        return new String[]{ "bash", "-c", exe + " -encoding UTF-8 -cp ./src -d ./out ./src/*.java" };
    }
}
