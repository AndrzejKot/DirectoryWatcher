package com.gft.watcher;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import rx.Observable;
import rx.Subscriber;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.CountDownLatch;

import static java.nio.file.StandardWatchEventKinds.*;

@RequiredArgsConstructor
@Log4j
public class DirWatcher {

    private final Path root;
    private final WatchService watchService;
    private final WatcherThread watcherThread;

    public void registerRecursive(Path root) throws IOException {
        // register all subfolders
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                dir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
                return FileVisitResult.CONTINUE;
            }
        });
    }
    public void watch(Subscriber<Path> subscriber) throws IOException {
        watch(subscriber,null);
    }

    void watch(Subscriber<Path> subscriber, CountDownLatch latch) throws IOException {
        registerRecursive(root);
        if (latch != null) {
            latch.countDown();
        }
        while (!watcherThread.isInterrupted()) {
            final WatchKey key;
            try {
                key = watchService.take();
                Path dir = (Path) key.watchable();

                for (WatchEvent event : key.pollEvents()) {
                    final WatchEvent.Kind kind = event.kind();

                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;

                    Path fullPath = dir.resolve(ev.context());

                    if (kind == ENTRY_CREATE) {
                        registerRecursive(fullPath);
//                        Observable.create((Observable.OnSubscribe<Path>) observer -> observer.onNext(fullPath)).toBlocking().subscribe(subscriber);
                        subscriber.onNext(Observable.just(fullPath).toBlocking().single());
                    } else if (kind == ENTRY_DELETE) {
                        log.info("File deleted: " + fullPath);
                        //fullTree.filter(path -> !path.equals(fullPath));//.subscribe(System.out::println);
                    }
                }
                // IMPORTANT: The key must be reset after processed
                boolean valid = key.reset();
                if (!valid) {
                    break;
                }
            } catch (InterruptedException e) {
                watcherThread.interrupt();
            }
        }

    }

}
