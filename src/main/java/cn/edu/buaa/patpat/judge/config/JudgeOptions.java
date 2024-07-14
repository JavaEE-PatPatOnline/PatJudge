package cn.edu.buaa.patpat.judge.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "judge")
@Data
public class JudgeOptions {
    private List<String> languages;

    private String problemRoot;
    private String judgeRoot;

    private Map<String, String> binMap;

    /**
     * Get the binary path of the java with a specific version.
     *
     * @param language The version of the java.
     * @return The binary path of the java. null if the version is not supported.
     */
    public String getBinPath(String language) {
        if (binMap == null) {
            binMap = new HashMap<>();
            for (String lang : languages) {
                String[] split = lang.split(";");
                binMap.put(split[0], split[1]);
            }
        }
        return binMap.get(language);
    }

    public Path getProblemPath(int problemId) {
        return Path.of(problemRoot, String.valueOf(problemId));
    }

    public Path getProblemYamlPath(int problemId) {
        return Path.of(problemRoot, String.valueOf(problemId), "problem.yaml");
    }

    public Path getProblemInitPath(int problemId) {
        return Path.of(problemRoot, String.valueOf(problemId), "init");
    }

    public Path getJudgePath(int submissionId) {
        return Path.of(judgeRoot, String.valueOf(submissionId));
    }

    public Path getJudgeClassPath(int submissionId) {
        return Path.of(judgeRoot, String.valueOf(submissionId), "out");
    }
}
