package cn.edu.buaa.patpat.judge.utils.process;


import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.TimeoutException;

public class ProcessDescriptor implements IProcessDescriptor {
    private final ProcessBuilder builder = new ProcessBuilder();
    private ProcessOutput output;
    private ProcessError error;

    private ProcessDescriptor() {
    }

    public static IProcessDescriptor create() {
        return new ProcessDescriptor();
    }

    @Override
    public IProcessDescriptor exec(String... cmd) {
        builder.command(cmd);
        return this;
    }

    @Override
    public IProcessDescriptor setWorkingDirectory(String cwd) {
        builder.directory(Paths.get(cwd).toFile());
        return this;
    }

    @Override
    public IProcessDescriptor redirectInput(String input) {
        builder.redirectInput(Paths.get(input).toFile());
        return this;
    }

    @Override
    public IProcessDescriptor redirectOutput(ProcessOutput output) {
        this.output = output;
        return this;
    }

    @Override
    public IProcessDescriptor redirectError(ProcessError error) {
        this.error = error;
        return this;
    }

    @Override
    public int waitFor() throws IOException, InterruptedException {
        Process process = builder.start();
        process.waitFor();

        if (output != null) {
            output.setStdOut(process.inputReader());
        }
        if (error != null) {
            error.setStdErr(process.errorReader());
        }

        return process.exitValue();
    }

    @Override
    public int waitFor(long timeout) throws IOException, InterruptedException, TimeoutException {
        Process process = builder.start();
        if (!process.waitFor(timeout, java.util.concurrent.TimeUnit.MILLISECONDS)) {
            process.destroy();
            throw new TimeoutException("Timed out after " + timeout + "ms");
        }

        if (output != null) {
            output.setStdOut(process.inputReader());
        }
        if (error != null) {
            error.setStdErr(process.errorReader());
        }

        return process.exitValue();
    }
}
