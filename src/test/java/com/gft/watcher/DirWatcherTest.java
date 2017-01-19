package com.gft.watcher;

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
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertTrue;

public class DirWatcherTest {

    @Test(timeout = 1000)
    public void shouldCatchNoSuchFileExceptionAndEndWatchingWithinTimeout() {
        val rootPath = Paths.get("C:\\NonExistingFile");

        DirWatcher.watch(rootPath).subscribe(System.out::println);
    }
//
//    @Test
//    public void shouldThrowIllegalAccessError() throws Exception {
//        val fs = Jimfs.newFileSystem(Configuration.windows().toBuilder()
//                .setWatchServiceConfiguration(WatchServiceConfiguration.polling(100, TimeUnit.MILLISECONDS)).build());
//        val rootPath = fs.getPath("C:\\Users");
//        val watchService = Mockito.mock(WatchService.class);
//        val testSubscriber = new TestSubscriber<Path>();
//        PowerMockito.mock(DirWatcher.class);
////        PowerMockito.spy(Observable.class);
////        PowerMockito.doThrow(new IOException()).when(DirWatcher.class, "listenForEvents", watchService, new TestSubscriber<Path>());
//        when(watchService.take()).thenThrow(new InterruptedException());
//        PowerMockito.doNothing().when(DirWatcher.class, "registerRecursive", rootPath);
//
//        DirWatcher.watch(rootPath, watchService).subscribe(System.out::println);
//    }


    @Test
    public void shouldReturnThreeNodes() throws IOException, InterruptedException {
        val fs = Jimfs.newFileSystem(Configuration.windows().toBuilder()
                .setWatchServiceConfiguration(WatchServiceConfiguration.polling(100, TimeUnit.MILLISECONDS)).build());
        val rootPath = fs.getPath("C:\\Users");
        val dirA = rootPath.resolve("dirA");
        val dirB = rootPath.resolve("dirB");
        val dirBA = dirB.resolve("dirBA");
        val fileA = rootPath.resolve("fileA.txt");
        val fileB = rootPath.resolve("fileB.txt");
        val testSubscriber = new TestSubscriber<Path>();
        val expectedItemsCount = 3;

        Files.createDirectory(rootPath);
        Files.createDirectory(dirA);
        Files.write(fileA, ImmutableList.of("fileA"), StandardCharsets.UTF_8);
        DirWatcher.watch(rootPath, fs.newWatchService()).subscribeOn(Schedulers.newThread()).subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent(100, TimeUnit.MILLISECONDS);
        Files.createDirectory(dirB);
        testSubscriber.awaitValueCount(1, 100, TimeUnit.MILLISECONDS);
        Files.createDirectory(dirBA);
        Files.write(fileB, ImmutableList.of("fileB"), StandardCharsets.UTF_8);

        assertTrue(testSubscriber.awaitValueCount(expectedItemsCount, 1000, TimeUnit.MILLISECONDS));
        testSubscriber.assertNoErrors();
        assertEquals(expectedItemsCount, testSubscriber.getOnNextEvents().size());
        assertThat(testSubscriber.getOnNextEvents(), containsInAnyOrder(fileB, dirB, dirBA));
    }
}
