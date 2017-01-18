package com.gft.watcher;

import com.google.common.collect.ImmutableList;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.google.common.jimfs.WatchServiceConfiguration;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertTrue;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DirWatcher.class)
public class DirWatcherTest {

//    @Test
//    public void shouldThrowIllegalAccessError() {
//        new DirWatcher();
//    }
//
//    @Test
//    public void shouldThrowIllegalAccessError() throws Exception {
//        val fs = Jimfs.newFileSystem(Configuration.windows().toBuilder()
//                .setWatchServiceConfiguration(WatchServiceConfiguration.polling(100, TimeUnit.MILLISECONDS)).build());
//        final WatchService watchService = fs.newWatchService();
//        val rootPath = fs.getPath("C:\\Users");
//
//        PowerMockito.spy(DirWatcher.class);
//        PowerMockito.spy(Observable.class);
//        PowerMockito.doThrow(new IOException()).when(DirWatcher.class, "listenForEvents", watchService, new TestSubscriber<Path>());
//
//        DirWatcher.watch(rootPath);
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

//    @Test
//    public void shouldReturnOneNode() throws Exception {
//        FileSystem fs = Jimfs.newFileSystem(Configuration.windows());
//        Path rootPath = fs.getPath("C:\\Users");
//        Files.createDirectory(rootPath);
//        final WatchService watchService = Mockito.mock(WatchService.class);
//        final WatchEvent event = Mockito.mock(WatchEvent.class);
////        final List<Path> paths = new ArrayList<>();
////        final Subscriber<Path> subscriber = TestCaseHelper.initSubscriber(paths);
//
//        PowerMockito.spy(DirWatcher.class);
//
//        when(event.context()).thenReturn(rootPath);
////        when(event.kind()).thenReturn(ENTRY_CREATE);
////        PowerMockito.doNothing().when(DirWatcher.class, "registerRecursive");
////        when(watchService.take()).thenReturn(new WatchKey() {
////            @Override
////            public boolean isValid() {
////                return false;
////            }
////
////            @Override
////            public List<WatchEvent<?>> pollEvents() {
////                return null;
////            }
////
////            @Override
////            public boolean reset() {
////                return false;
////            }
////
////            @Override
////            public void cancel() {
////
////            }
////
////            @Override
////            public Watchable watchable() {
////                return rootPath;
////            }
////        });
//
//        DirWatcher.watch(rootPath,watchService);
//
////        spy.watch(subscriber);
//
////        assertThat(paths).hasSize(1);
//    }
}
