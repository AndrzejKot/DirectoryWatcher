package com.gft.watcher;

import rx.Observable;
import rx.Subscriber;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.StandardWatchEventKinds.*;

public class DirWatcher {

    private final Path root;
//    private final Observable<Path> observable;
//    private final Subscriber<Path> subscriber;
    private final WatchService watchService;
    private final WatcherThread watcherThread;

    public DirWatcher(Path root, WatchService watchService, WatcherThread watcherThread) throws IOException {
        this.root = root;
        this.watchService = watchService;
        this.watcherThread = watcherThread;
//        this.subscriber = initSubscriber();
//        this.observable = initObservable();
    }

//    private Subscriber<Path> initSubscriber() {
//        return new Subscriber<Path>() {
//            @Override
//            public void onCompleted() {
//                //System.out.println("Unsubscribed.");
//                //this.unsubscribe();
//            }
//
//            @Override
//            public void onError(Throwable e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onNext(Path path) {
//                System.out.println(path);
//            }
//        };
//    }
//    private Observable<Path> initObservable() {
//        return Observable.create(new Observable.OnSubscribe<Path>() {
//            @Override
//            public void call(Subscriber<? super Path> subscriber) {
//                subscriber.onNext();
//            }
//        })
//    }

//    private Observable operation() {
//        return Observable.create(s -> {
//            System.out.println("Start: Executing slow task in Service 1");
//            s.onNext(new IterableNode<Path>(root).iterator().next());
//            System.out.println("End: Executing slow task in Service 1");
//            s.onCompleted();
//        }).subscribeOn(Schedulers.computation());
//    }

    private void registerRecursive(Path root) throws IOException {
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
//                    Path fileName = ev.context();

                    Path fullPath = dir.resolve(ev.context());

//                    System.out.println(kind.name() + ": " + fullPath);

                    if (kind == OVERFLOW) {
                        System.out.println("Overflow event.");
                    } else if (kind == ENTRY_CREATE) {
                        registerRecursive(fullPath);
//                        Observable.create((Observable.OnSubscribe<Path>) observer -> observer.onNext(fullPath)).toBlocking().subscribe(subscriber);
                        subscriber.onNext(Observable.just(fullPath).toBlocking().single());
                    } else if (kind == ENTRY_DELETE) {
                        System.out.println("File deleted: " + fullPath);
                        //fullTree.filter(path -> !path.equals(fullPath));//.subscribe(System.out::println);
                        //connectableObservable.connect();
//                    observable.subscribe(subscriber);
                    }/* else if (kind == ENTRY_MODIFY) {
                    observable.subscribe(onNextAction);
                }*/
                }

//                subscription.unsubscribe();
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
