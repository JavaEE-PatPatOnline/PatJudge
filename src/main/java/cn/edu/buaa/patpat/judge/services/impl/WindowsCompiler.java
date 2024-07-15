package cn.edu.buaa.patpat.judge.services.impl;


import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@Profile({ "dev" })
public class WindowsCompiler extends BaseCompiler {
    @Override
    protected String[] getCompileCommand(String bin, String path) {
        String exe = Path.of(bin, "javac.exe").toString();
        return new String[]{ exe, "-encoding", "UTF-8", "-cp", "./src", "-d", "./out", "./src/*.java" };
    }
}
