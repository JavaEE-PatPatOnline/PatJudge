/*
 * Copyright (C) Patpat Online 2024
 * Made with love by Tony Skywalker
 */

package cn.edu.buaa.patpat.judge.utils;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Medias {
    private Medias() {}

    public static void copyContent(String source, String target) throws IOException {
        copyContent(Path.of(source), Path.of(target));
    }

    public static void copyContent(Path source, Path target) throws IOException {
        ensurePath(target);
        List<Path> content;
        try (var stream = Files.list(source)) {
            content = stream.toList();
        }
        for (var path : content) {
            FileSystemUtils.copyRecursively(path, Path.of(target.toString(), path.getFileName().toString()));
        }
    }

    public static void ensurePath(String path) throws IOException {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        ensurePath(Path.of(path));
    }

    public static void ensurePath(Path path) throws IOException {
        Files.createDirectories(path);
    }

    public static void ensureEmptyPath(String path) throws IOException {
        remove(path);
        ensurePath(path);
    }

    public static void ensureEmptyPath(Path path) throws IOException {
        remove(path);
        ensurePath(path);
    }

    public static void remove(String path) throws IOException {
        remove(Path.of(path));
    }

    public static void remove(Path path) throws IOException {
        // delete file or directory recursively
        if (Files.isDirectory(path)) {
            FileUtils.deleteDirectory(path.toFile());
        } else {
            Files.deleteIfExists(path);
        }
    }

    public static void removeSilently(String path) {
        removeSilently(Path.of(path));
    }

    public static void removeSilently(Path path) {
        try {
            remove(path);
        } catch (IOException e) {
            // ignore
        }
    }

    public static boolean exists(String path) {
        return exists(Path.of(path));
    }

    public static boolean exists(Path path) {
        return Files.exists(path);
    }

    public static void copyFile(String src, String dest) throws IOException {
        copyFile(Path.of(src), Path.of(dest));
    }

    /**
     * Copy file from src to target directory.
     *
     * @param src  source file path
     * @param dest target directory path
     * @throws IOException if an I/O error occurs
     */
    public static void copyFile(Path src, Path dest) throws IOException {
        ensurePath(dest);
        Files.copy(src, dest.resolve(src.getFileName()));
    }

    public static void copyDirectory(String src, String dest) throws IOException {
        copyDirectory(Path.of(src), Path.of(dest));
    }

    public static void copyDirectory(Path src, Path dest) throws IOException {
        FileUtils.copyDirectory(src.toFile(), dest.toFile());
    }

    public static Resource loadAsResource(String path) throws IOException {
        return loadAsResource(Path.of(path));
    }

    public static Resource loadAsResource(Path path) throws IOException {
        try {
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new IOException("Resource not found: " + path);
            }
        } catch (MalformedURLException e) {
            throw new IOException("Failed to load resource: " + path);
        }
    }
}
