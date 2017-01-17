package com.gft.watcher;

import com.gft.iterable.IterableNode;
import com.gft.node.DirNode;
import lombok.extern.log4j.Log4j;
import rx.Observable;
import rx.Subscriber;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.CountDownLatch;

import static java.nio.file.StandardWatchEventKinds.*;

@Log4j
public class DirWatcher {

    private DirWatcher() {
        throw new IllegalAccessError("Utility class");
    }

    private static void registerRecursive(Path root, WatchService watchService) throws IOException {
        try {
            root.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        } catch (FileSystemException e) {
            log.debug(e);
        }
        for (Path path : new IterableNode<Path>(new DirNode(root))) {
            try {
                path.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
            } catch (FileSystemException e) {
                log.debug(e);
            }
        }
    }

    public static Observable<Path> watch(Path root, WatchService watchService) {
        return watch(root, watchService, null);
    }

    public static Observable<Path> watch(Path root, WatchService watchService, CountDownLatch latch) {
        return Observable.create(subscriber -> {
            try {
                registerRecursive(root, watchService);
                if (latch != null) {
                    latch.countDown();
                }
                listenForEvents(watchService, subscriber);
            } catch (IOException | InterruptedException e) {
                log.error(e);
            }
        });
    }

    private static void listenForEvents(WatchService watchService, Subscriber<? super Path> subscriber)
            throws InterruptedException, IOException {
        while (!Thread.currentThread().isInterrupted()) {
            WatchKey key = watchService.take();
            Path dir = (Path) key.watchable();

            for (WatchEvent event : key.pollEvents()) {
                final WatchEvent.Kind kind = event.kind();

                @SuppressWarnings("unchecked")
                WatchEvent<Path> ev = (WatchEvent<Path>) event;

                Path fullPath = dir.resolve(ev.context());

                if (kind == ENTRY_CREATE) {
                    registerRecursive(fullPath, watchService);
                    subscriber.onNext(fullPath);
                }
            }
            boolean valid = key.reset();
            if (!valid) {
                log.error("DirWatcher key is invalid!");
            }
        }
    }
}
