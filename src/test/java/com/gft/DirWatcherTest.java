package com.gft;

import com.gft.watcher.DirWatcher;
import org.junit.Test;

import java.nio.file.FileSystems;
import java.nio.file.Paths;
import java.nio.file.WatchService;

public class DirWatcherTest {

    private WatchService watchService;

//    private static Path createDirStructure() throws IOException {
//        FileSystem fs = Jimfs.newFileSystem(Configuration.windows());
//        watchService = fs.newWatchService();
//        Path rootPath = fs.getPath("C:\\Users");
//        final WatchKey watchKey = rootPath.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
//        Files.createDirectory(rootPath);
//
//        Path hello = rootPath.resolve("hello.txt");
//        Files.write(hello, ImmutableList.of("hello world"), StandardCharsets.UTF_8);
//
//        final Path one = rootPath.resolve("one");
//        Files.createDirectory(one);
//
//        final Path two = rootPath.resolve("two");
//        Files.createDirectory(two);
//
//        return rootPath;
//    }

    @Test
    public void directoryWatcherTest() throws Exception {
        DirWatcher watcher = new DirWatcher(FileSystems.getDefault().newWatchService());
        watcher.watch(Paths.get("C:\\Users\\ankt\\Desktop\\challenge"));
    }
}
