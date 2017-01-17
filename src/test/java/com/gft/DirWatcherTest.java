package com.gft;

import com.gft.watcher.DirWatcher;
import com.google.common.collect.ImmutableList;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.google.common.jimfs.WatchServiceConfiguration;
import lombok.val;
import org.junit.Test;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class DirWatcherTest {

    @Test
    public void shouldReturnThreeNodes() throws IOException, InterruptedException {
        val fs = Jimfs.newFileSystem(Configuration.windows().toBuilder()
                .setWatchServiceConfiguration(WatchServiceConfiguration.polling(10, TimeUnit.MILLISECONDS)).build());
        val rootPath = fs.getPath("C:\\Users");
        val world = rootPath.resolve("world");
        val test = rootPath.resolve("test");
        val hello = rootPath.resolve("hello.txt");
        val testSubscriber = new TestSubscriber<Path>();
        val doneRegistering = new CountDownLatch(1);

        Files.createDirectory(rootPath);
        DirWatcher.watch(rootPath, fs.newWatchService(), doneRegistering).subscribeOn(Schedulers.newThread()).subscribe(testSubscriber);
        doneRegistering.await(100, TimeUnit.MILLISECONDS);
        Files.createDirectory(world);
        Files.createDirectory(test);
        Files.write(hello, ImmutableList.of("hello world"), StandardCharsets.UTF_8);
        testSubscriber.awaitValueCount(3, 1000, TimeUnit.MILLISECONDS);

        testSubscriber.assertNoErrors();
        assertEquals(3, testSubscriber.getOnNextEvents().size());
        assertThat(testSubscriber.getOnNextEvents(), containsInAnyOrder(hello, world, test));
    }
}
