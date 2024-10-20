/*
 * Copyright (C) Patpat Online 2024
 * Made with love by Tony Skywalker
 */

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
                // Warning: If we entered the true branch with ==, then there will be
                //  sb.length() + line.length() == Globals.MAX_MESSAGE_LENGTH + 1
                //  which will cause end index to be out of bounds, so we need to subtract 1
                //  or simply use Math.max(0, ...)
                sb.append(line, 0, Math.max(0, Globals.MAX_MESSAGE_LENGTH - sb.length()));
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

    public static String truncateIfTooLong(String message, int maxLength) {
        if (message.length() <= maxLength) {
            return message;
        }
        return message.substring(0, maxLength) + "...";
    }
}
