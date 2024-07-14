package cn.edu.buaa.patpat.judge.config;

import cn.edu.buaa.patpat.judge.services.ICompiler;
import cn.edu.buaa.patpat.judge.services.impl.LinuxCompiler;
import cn.edu.buaa.patpat.judge.services.impl.WindowsCompiler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Bean
    @ConditionalOnExpression("${os} == 'windows'")
    public ICompiler getWindowsCompiler() {
        return new WindowsCompiler();
    }

    @Bean
    @ConditionalOnExpression("${os} == 'linux'")
    public ICompiler getLinuxCompiler() {
        return new LinuxCompiler();
    }
}
