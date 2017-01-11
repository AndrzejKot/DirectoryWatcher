package com.gft.watcher;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import rx.Subscriber;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchService;

@RequiredArgsConstructor
public final class WatcherThread extends Thread {
    @NonNull private final Path root;
    @NonNull private final WatchService watchService;
    @NonNull private final Subscriber<Path> subscriber;

    @Override
    public void run() {
        try {
            new DirWatcher(root, watchService, this).watch(subscriber);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
