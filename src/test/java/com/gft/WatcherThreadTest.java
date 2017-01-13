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
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.jayway.awaitility.Awaitility.await;
import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

public class WatcherThreadTest {

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerException() {
        new WatcherThread(null,null,null,null);
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
        final CountDownLatch doneRegistering = new CountDownLatch(1);
        WatcherThread watcherThread = new WatcherThread(rootPath, fs.newWatchService(), subscriber, doneRegistering);
        watcherThread.start();

        doneRegistering.await(1, TimeUnit.SECONDS);
        Files.createDirectory(rootPath.resolve("world"));
        Files.createDirectory(rootPath.resolve("test"));
        await().until(newPathIsAdded(paths));

        assertThat(paths).hasSize(2);
    }

    private Callable<Boolean> newPathIsAdded(List<Path> paths) {
        return () -> paths.size() == 2;
    }
}
