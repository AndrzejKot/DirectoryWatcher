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
import static org.junit.Assert.assertTrue;

//@RunWith(PowerMockRunner.class)
//@PrepareForTest(DirWatcher.class)
public class DirWatcherTest {

//    @Test
//    public void shouldThrowIllegalAccessError() {
//        new DirWatcher();
//    }
//
//    @Test
//    public void shouldThrowIllegalAccessError() {
//        new DirWatcher();
//    }

    @Test
    public void shouldReturnThreeNodes() throws IOException, InterruptedException {
        val fs = Jimfs.newFileSystem(Configuration.windows().toBuilder()
                .setWatchServiceConfiguration(WatchServiceConfiguration.polling(100, TimeUnit.MILLISECONDS)).build());
        val rootPath = fs.getPath("C:\\Users");
        val world = rootPath.resolve("world");
        val test = rootPath.resolve("test");
        val subTest = test.resolve("subTest");
        val hello = rootPath.resolve("hello.txt");
        val testSubscriber = new TestSubscriber<Path>();
        val doneRegistering = new CountDownLatch(1);

        Files.createDirectory(rootPath);
        Files.createDirectory(world);
        DirWatcher.watch(rootPath, fs.newWatchService(), doneRegistering).subscribeOn(Schedulers.newThread()).subscribe(testSubscriber);
        doneRegistering.await(1000, TimeUnit.MILLISECONDS);
        Files.createDirectory(test);
        testSubscriber.awaitValueCount(1, 100, TimeUnit.MILLISECONDS);
        Files.createDirectory(subTest);
        Files.write(hello, ImmutableList.of("hello world"), StandardCharsets.UTF_8);

        assertTrue(testSubscriber.awaitValueCount(3, 1000, TimeUnit.MILLISECONDS));
        testSubscriber.assertNoErrors();
        assertEquals(3, testSubscriber.getOnNextEvents().size());
        assertThat(testSubscriber.getOnNextEvents(), containsInAnyOrder(hello, test, subTest));
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
