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
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

public class WatcherThreadTest {

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerException() {
        new WatcherThread(null,null,null,null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerException2() {
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
        final Path rootPath = fs.getPath("C:\\Users");
        final Path world = rootPath.resolve("world");
        final Path test = rootPath.resolve("test");
        List<Path> paths = new ArrayList<>();
        final Subscriber<Path> subscriber = TestCaseHelper.initSubscriber(paths);
        final CountDownLatch doneRegistering = new CountDownLatch(1);
        WatcherThread watcherThread = new WatcherThread(rootPath, fs.newWatchService(), subscriber, doneRegistering);

        Files.createDirectory(rootPath);
        watcherThread.start();
        doneRegistering.await(1, TimeUnit.SECONDS);
        Files.createDirectory(world);
        Files.createDirectory(test);
        await().until(newPathIsAdded(paths));

        assertThat(paths, hasSize(2));
        assertThat(paths, containsInAnyOrder(world,test));
    }

    private Callable<Boolean> newPathIsAdded(List<Path> paths) {
        return () -> paths.size() == 2;
    }
}
