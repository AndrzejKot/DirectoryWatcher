package com.gft.watcher;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import rx.Subscriber;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchService;
import java.util.concurrent.CountDownLatch;

@RequiredArgsConstructor
@Log4j
public class WatcherThread extends Thread {
    @NonNull private final Path root;
    @NonNull private final WatchService watchService;
    @NonNull private final Subscriber<Path> subscriber;
    private final CountDownLatch latch;

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
            log.error(e);
        }
    }
}
