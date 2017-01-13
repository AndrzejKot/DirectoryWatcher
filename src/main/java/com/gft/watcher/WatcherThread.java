package com.gft.watcher;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import rx.Subscriber;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchService;
import java.util.concurrent.CountDownLatch;

@RequiredArgsConstructor
public class WatcherThread extends Thread {
    @NonNull private final Path root;
    @NonNull private final WatchService watchService;
    @NonNull private final Subscriber<Path> subscriber;
    private final CountDownLatch latch;
    private static final Logger LOGGER = Logger.getLogger(WatcherThread.class.getName());

    public WatcherThread(@NonNull Path root,@NonNull WatchService watchService,@NonNull Subscriber<Path> subscriber) {
        this.root = root;
        this.watchService = watchService;
        this.subscriber = subscriber;
        this.latch = null;
    }

    @Override
    public void run() {
        try {
            new DirWatcher(root, watchService, this).watch(subscriber,latch);
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }
}
