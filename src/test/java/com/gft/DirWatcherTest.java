package com.gft;

import com.gft.watcher.WatcherThread;
import com.google.common.collect.ImmutableList;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.Test;
import rx.Subscriber;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DirWatcherTest {

        private Subscriber<Path> initSubscriber(List<Path> paths) {
        return new Subscriber<Path>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Path path) {
//                System.out.println(path);
                paths.add(path);
            }
        };
    }

//    @Test
//    public void simpleDirWatcherTest() throws IOException, InterruptedException {
//        final Path root = Paths.get("C:\\Users\\ankt\\Desktop\\challenge");
//        final WatchService watchService = FileSystems.getDefault().newWatchService();
//        List<Path> paths = new ArrayList<>();
//        final Subscriber<Path> subscriber = initSubscriber(paths);
//        WatcherThread watcherThread = new WatcherThread(root, watchService, subscriber);
//        DirWatcher dirWatcher = new DirWatcher(root, watchService, watcherThread);
//
//        dirWatcher.watch(subscriber);
//    }

    @Test
    public void shouldReturnFourNodes() throws IOException, InterruptedException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.windows());
        Path rootPath = fs.getPath("C:\\Users");
        Files.createDirectory(rootPath);

        Path hello = rootPath.resolve("hello.txt");
        Files.write(hello, ImmutableList.of("hello world"), StandardCharsets.UTF_8);

        final Path one = rootPath.resolve("one");
        Files.createDirectory(one);

        final Path two = rootPath.resolve("two");
        Files.createDirectory(two);

        List<Path> paths = new ArrayList<>();
        final Subscriber<Path> subscriber = initSubscriber(paths);
        WatcherThread watcherThread = new WatcherThread(rootPath, fs.newWatchService(), subscriber);
        watcherThread.start();

        Thread.sleep(5000);

        Files.createDirectory(rootPath.resolve("world"));
        Thread.sleep(5000);
        Files.createDirectory(rootPath.resolve("test"));
//        Files.delete(one);

        Thread.sleep(500);

        assertThat(paths).hasSize(2);
    }
}
