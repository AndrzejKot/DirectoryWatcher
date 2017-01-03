package com.gft.watcher;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.StandardWatchEventKinds.*;

public class DirWatcher {

    private final WatchService watchService;

    public DirWatcher(WatchService watchService) throws IOException {
        this.watchService = watchService;
    }

    private void registerRecursive(final Path root) throws IOException {
        // register all subfolders
        Files.walkFileTree(root, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                dir.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public void watch(Path root) throws IOException {
        registerRecursive(root);

        while (true) {
            final WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException e) {
                return;
            }

            for (WatchEvent event : key.pollEvents()) {
                final WatchEvent.Kind kind = event.kind();

                @SuppressWarnings("unchecked")
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                Path fileName = ev.context();

                System.out.println(kind.name() + ": " + fileName);

                if (kind == OVERFLOW) {
                    System.out.println("Overflow event.");
                } else if (kind == ENTRY_CREATE) {
                    // process create event
                    registerRecursive(root);
                    System.out.println("File created.");
                } else if (kind == ENTRY_DELETE) {
                    // process delete event
                    System.out.println("File deleted.");
                } else if (kind == ENTRY_MODIFY) {
                    // process modify event
                    System.out.println("File modified.");
                }
            }

//            System.out.println("Some event has been detected!");

            // IMPORTANT: The key must be reset after processed
            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }

    }

}
