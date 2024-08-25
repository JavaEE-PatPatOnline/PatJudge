package cn.edu.buaa.patpat.judge.extensions.judge;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@Profile({ "stag", "prod" })
public class LinuxCompiler extends BaseCompiler {
    @Override
    protected String[] getCompileCommand(String bin, String path) {
        String exe = Path.of(bin, "javac").toString();
        return new String[]{ "bash", "-c", exe + " -encoding UTF-8 -cp ./src -d ./out ./src/*.java" };
    }
}
