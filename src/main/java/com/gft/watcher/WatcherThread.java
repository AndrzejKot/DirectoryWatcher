package com.gft.watcher;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import rx.Subscriber;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchService;

@RequiredArgsConstructor
public class WatcherThread extends Thread {
    @NonNull private final Path root;
    @NonNull private final WatchService watchService;
    @NonNull private final Subscriber<Path> subscriber;
    private static final Logger LOGGER = Logger.getLogger(WatcherThread.class.getName());

    @Override
    public void run() {
        try {
            new DirWatcher(root, watchService, this).watch(subscriber);
        } catch (IOException e) {
            LOGGER.error(e);
        }
    }
}
