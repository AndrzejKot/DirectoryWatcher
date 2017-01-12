package com.gft;

import com.gft.watcher.WatcherThread;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.Test;
import rx.Subscriber;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

public class WatcherThreadTest {

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

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerException() {
        new WatcherThread(null,null,null);
    }

    @Test
    public void shouldBeInterrupted() throws Exception {
        FileSystem fs = Jimfs.newFileSystem(Configuration.windows());
        Path rootPath = fs.getPath("C:\\Users");
        Files.createDirectory(rootPath);

        List<Path> paths = new ArrayList<>();
        final Subscriber<Path> subscriber = TestCaseHelper.initSubscriber(paths);
        WatcherThread watcherThread = new WatcherThread(rootPath, fs.newWatchService(), subscriber);
        watcherThread.start();
        watcherThread.interrupt();

        assertTrue(watcherThread.isInterrupted());
    }

    @Test
    public void shouldReturnTwoNodes() throws IOException, InterruptedException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.windows());
        Path rootPath = fs.getPath("C:\\Users");
        Files.createDirectory(rootPath);

        List<Path> paths = new ArrayList<>();
        final Subscriber<Path> subscriber = TestCaseHelper.initSubscriber(paths);
        WatcherThread watcherThread = new WatcherThread(rootPath, fs.newWatchService(), subscriber);
        watcherThread.start();

        Thread.sleep(100);

        Files.createDirectory(rootPath.resolve("world"));
        Files.createDirectory(rootPath.resolve("test"));
//        Files.delete(one);

        Thread.sleep(5000);

        assertThat(paths).hasSize(2);
    }
}
