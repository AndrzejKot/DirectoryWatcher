package com.gft.helper;

import com.google.common.collect.ImmutableList;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestCaseHelper {
    public static Path createDirStructure() throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.windows());
        Path rootPath = fs.getPath("C:\\Users");
        Files.createDirectory(rootPath);

        Path hello = rootPath.resolve("hello.txt");
        Files.write(hello, ImmutableList.of("hello world"), StandardCharsets.UTF_8);

        final Path one = rootPath.resolve("one");
        Files.createDirectory(one);

        final Path two = rootPath.resolve("two");
        Files.createDirectory(two);

        return rootPath;
    }
}
