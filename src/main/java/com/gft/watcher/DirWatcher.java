package com.gft.watcher;

import com.gft.iterable.IterableNode;
import com.gft.node.DirNode;
import com.sun.nio.file.SensitivityWatchEventModifier;
import lombok.extern.log4j.Log4j;
import rx.Observable;
import rx.Subscriber;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.FileSystems.getDefault;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

@Log4j
public final class DirWatcher implements Closeable {

    private static WatchService watchService;

    static {
        try {
            watchService = getDefault().newWatchService();
        } catch (IOException e) {
            log.error(e);
        }
    }

    private DirWatcher() {
        throw new IllegalAccessError("Utility class");
    }

    /**
     * Returns stream of changes in root folder structure.
     * @param root root folder
     * @return stream that represents changes in root folder structure.
     */
    public static Observable<Path> watch(Path root) {
        return watch(root, watchService);
    }


    static Observable<Path> watch(Path root, WatchService watchService) {
        return Observable.create(subscriber -> {
            try {
                registerRecursive(root, watchService);
                listenForEvents(watchService, subscriber);
            } catch (NoSuchFileException e) {
                log.info("Ending directory watcher thread.\n" + e);
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                log.error(e);
            }
        });
    }

    private static void registerRecursive(Path root, WatchService watchService) throws IOException {
        try {
            root.register(watchService, new WatchEvent.Kind[]{StandardWatchEventKinds.ENTRY_CREATE}, SensitivityWatchEventModifier.HIGH);
        } catch (NoSuchFileException e) {
            throw e;
        }catch (FileSystemException e) {
            log.debug(e);
        }
        for (Path path : new IterableNode<Path>(new DirNode(root))) {
            try {
                path.register(watchService, new WatchEvent.Kind[]{StandardWatchEventKinds.ENTRY_CREATE}, SensitivityWatchEventModifier.HIGH);
            } catch (FileSystemException e) {
                log.debug(e);
            }
        }
    }

    private static void listenForEvents(WatchService watchService, Subscriber<? super Path> subscriber) {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                WatchKey key = watchService.take();
                Path dir = (Path) key.watchable();

                handleEvents(watchService, subscriber, key, dir);
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

    private static void handleEvents(WatchService watchService, Subscriber<? super Path> subscriber, WatchKey key, Path dir) throws IOException {
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
    }

    @Override
    public void close() throws IOException {
        watchService.close();
    }
}
