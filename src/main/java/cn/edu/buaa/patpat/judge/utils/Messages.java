package cn.edu.buaa.patpat.judge.utils;

import cn.edu.buaa.patpat.judge.config.Globals;

import java.io.BufferedReader;
import java.io.IOException;

public class Messages {
    private Messages() {}

    public static String truncate(BufferedReader reader) throws IOException {
        return truncate(reader, null);
    }

    public static String truncate(BufferedReader reader, String ignore) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            if ((ignore != null) && (ignore.contains(line))) {
                continue;
            }
            if (sb.length() + line.length() <= Globals.MAX_MESSAGE_LENGTH) {
                sb.append(line).append("\n");
            } else {
                sb.append(line, 0, Globals.MAX_MESSAGE_LENGTH - sb.length());
                sb.append("...");
                break;
            }
        }
        return sb.toString();
    }

    public static String truncate(String message) {
        return truncate(message, null);
    }

    public static String truncate(String message, String ignore) {
        if ((ignore != null) && message.startsWith(ignore)) {
            message = message.substring(ignore.length());
        }
        if (message.length() <= Globals.MAX_MESSAGE_LENGTH) {
            return message;
        }
        return message.substring(0, Globals.MAX_MESSAGE_LENGTH) + "...";
    }
}
