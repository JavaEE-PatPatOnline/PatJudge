package cn.edu.buaa.patpat.judge.utils;

import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Medias {
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

    public static void ensurePath(Path path) throws IOException {
        Files.createDirectories(path);
    }
}
