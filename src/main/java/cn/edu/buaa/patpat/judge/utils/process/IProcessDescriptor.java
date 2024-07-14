package cn.edu.buaa.patpat.judge.utils.process;

import lombok.Data;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public interface IProcessDescriptor {
    IProcessDescriptor exec(String... cmd);

    IProcessDescriptor setWorkingDirectory(String cwd);

    IProcessDescriptor redirectInput(String input);

    IProcessDescriptor redirectOutput(ProcessOutput output);

    IProcessDescriptor redirectError(ProcessError error);

    /**
     * Wait for the process to finish.
     *
     * @return The exit value of the process.
     * @throws IOException          If an I/O error occurs.
     * @throws InterruptedException If the current thread is interrupted.
     */
    int waitFor() throws IOException, InterruptedException;

    /**
     * Wait for the process to finish with a timeout.
     *
     * @param timeout The timeout in milliseconds. If <= 0, wait indefinitely.
     * @return The exit value of the process.
     * @throws IOException          If an I/O error occurs.
     * @throws InterruptedException If the current thread is interrupted.
     */
    int waitFor(long timeout) throws IOException, InterruptedException, TimeoutException;

    @Data
    class ProcessOutput {
        private BufferedReader stdOut;
    }

    @Data
    class ProcessError {
        private BufferedReader stdErr;
    }
}
