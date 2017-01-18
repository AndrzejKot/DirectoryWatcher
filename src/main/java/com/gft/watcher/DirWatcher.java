package com.gft.watcher;

import com.gft.iterable.IterableNode;
import com.gft.node.DirNode;
import lombok.extern.log4j.Log4j;
import rx.Observable;
import rx.Subscriber;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.FileSystems.getDefault;
import static java.nio.file.StandardWatchEventKinds.*;

@Log4j
public class DirWatcher implements Closeable {

    private static WatchService watchService;

    static {
        try {
            watchService = getDefault().newWatchService();
//            watchService = Jimfs.newFileSystem(Configuration.windows()).newWatchService();
        } catch (IOException e) {
            log.error(e);
        }
    }

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

    public static Observable<Path> watch(Path root) {
        return watch(root, watchService);
    }

    static Observable<Path> watch(Path root, WatchService watchService) {
        return Observable.create(subscriber -> {
            try {
                registerRecursive(root, watchService);
                listenForEvents(watchService, subscriber);
            } catch (IOException e) {
                log.error(e);
            }
        });
    }

    private static void listenForEvents(WatchService watchService, Subscriber<? super Path> subscriber) {
        while (!Thread.currentThread().isInterrupted()) {
            try {
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
            } catch (IOException e) {
                subscriber.onError(e);
            } catch (InterruptedException e) {
                log.info(e);
                log.info("Ending directory watcher thread.");
                subscriber.onCompleted();
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void close() throws IOException {
        watchService.close();
    }
}
