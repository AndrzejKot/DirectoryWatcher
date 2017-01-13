package com.gft;

import com.gft.watcher.DirWatcher;
import com.gft.watcher.WatcherThread;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.Test;
import org.mockito.Mockito;
import rx.Subscriber;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class MockedDirWatcherTest {

@Test(expected = IOException.class)
public void shouldThrowIOException() throws IOException {
    final DirWatcher dirWatcher = Mockito.mock(DirWatcher.class);
    Mockito.doThrow(new IOException()).when(dirWatcher).watch(null);
    dirWatcher.watch(null);
}

    @Test
    public void shouldReturnOneNode() throws IOException, InterruptedException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.windows());
        Path rootPath = fs.getPath("C:\\Users");
        Files.createDirectory(rootPath);
        final WatchService watchService = Mockito.mock(WatchService.class);
        final WatchEvent event = Mockito.mock(WatchEvent.class);
        final WatcherThread watcherThread = Mockito.mock(WatcherThread.class);
        final List<Path> paths = new ArrayList<>();
        final Subscriber<Path> subscriber = TestCaseHelper.initSubscriber(paths);
        final DirWatcher dirWatcher = new DirWatcher(rootPath,watchService,watcherThread);
        final DirWatcher spy = Mockito.spy(dirWatcher);

        when(event.context()).thenReturn(rootPath);
        when(event.kind()).thenReturn(ENTRY_CREATE);
        Mockito.doNothing().when(spy).registerRecursive(rootPath);
        when(watchService.take()).thenReturn(new WatchKey() {
            @Override
            public boolean isValid() {
                return false;
            }

            @Override
            public List<WatchEvent<?>> pollEvents() {
                final LinkedList<WatchEvent<?>> watchEvents = new LinkedList<>();
                watchEvents.add(event);
                return watchEvents;
            }

            @Override
            public boolean reset() {
                return false;
            }

            @Override
            public void cancel() {

            }

            @Override
            public Watchable watchable() {
                return rootPath;
            }
        });

        spy.watch(subscriber);

        assertThat(paths).hasSize(1);
    }
}
