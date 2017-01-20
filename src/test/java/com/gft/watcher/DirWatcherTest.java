package com.gft.watcher;

import com.gft.iterable.IterableNode;
import com.gft.node.DirNode;
import com.google.common.collect.ImmutableList;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.google.common.jimfs.WatchServiceConfiguration;
import lombok.val;
import org.junit.Test;
import org.mockito.Mockito;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.spi.FileSystemProvider;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;

public class DirWatcherTest {

    private class FakeDirStream implements DirectoryStream<Path> {

        @Override
        public Iterator<Path> iterator() {
            return new IterableNode<Path>(new DirNode(Paths.get("C:\\NonExistingFile"))).iterator();
        }

        @Override
        public void close() throws IOException {

        }
    }

    @Test(timeout = 1000)
    public void shouldCatchNoSuchFileExceptionAndEndWatchingWithinTimeout() {
        val rootPath = Paths.get("C:\\NonExistingFile");

        DirWatcher.watch(rootPath).subscribe(System.out::println);
    }

//    @Test(timeout = 1000)
//    public void shouldCatchIOExceptionAndEndWatchingWithinTimeout() throws IOException, InterruptedException {
//        val rootPath = Mockito.spy(Path.class);
//        val fileSystem = Mockito.spy(FileSystem.class);
//        val fileSystemProvider = Mockito.spy(FileSystemProvider.class);
//        val fakeDirStream = new FakeDirStream();
//        val testSubscriber = new TestSubscriber<Path>();
//        val watchService = Mockito.mock(WatchService.class);
//
//        Mockito.doReturn(fakeDirStream).when(fileSystemProvider).newDirectoryStream(any(), any());
//        Mockito.doReturn(fileSystemProvider).when(fileSystem).provider();
//        Mockito.doReturn(fileSystem).when(rootPath).getFileSystem();
//        Mockito.doReturn(null).doThrow(new IOException()).when(rootPath).register(any(), any());
//        Mockito.doThrow(new IOException()).when(watchService).take();
//        DirWatcher.watch(rootPath, watchService).subscribe(testSubscriber);
//
//        testSubscriber.assertNoErrors();
//    }

    @Test(timeout = 1000)
    public void shouldCatchInterruptedExceptionAndEndWatchingWithinTimeout() throws IOException, InterruptedException {
        val rootPath = Mockito.spy(Path.class);
        val fileSystem = Mockito.spy(FileSystem.class);
        val fileSystemProvider = Mockito.spy(FileSystemProvider.class);
        val fakeDirStream = new FakeDirStream();
        val testSubscriber = new TestSubscriber<Path>();
        val watchService = Mockito.mock(WatchService.class);

        Mockito.doReturn(fakeDirStream).when(fileSystemProvider).newDirectoryStream(any(), any());
        Mockito.doReturn(fileSystemProvider).when(fileSystem).provider();
        Mockito.doReturn(fileSystem).when(rootPath).getFileSystem();
        Mockito.doReturn(null).doReturn(null).when(rootPath).register(any(), any());
        Mockito.doThrow(new InterruptedException()).when(watchService).take();
        DirWatcher.watch(rootPath, watchService).subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
    }

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
