package com.gft.watcher;

import com.gft.node.DirNode;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.Logger;
import rx.Observable;
import rx.Subscriber;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.StandardWatchEventKinds.*;

@RequiredArgsConstructor
public class DirWatcher {

    private final Path root;
    private final WatchService watchService;
    private final WatcherThread watcherThread;
    private static final Logger LOGGER = Logger.getLogger(DirNode.class.getName());


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
        registerRecursive(root);

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
                        LOGGER.info("File deleted: " + fullPath);
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
