package com.gft.node;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@RequiredArgsConstructor
public class DirNode implements Node<Path> {
    @NonNull private final Path path;
    private List<Node<Path>> children = new ArrayList<>();
    private static final Logger LOGGER = Logger.getLogger(DirNode.class.getName());

    private void setChildren() {
        if (path.toFile().isDirectory()) {
            try (val dirStream = Files.newDirectoryStream(path)){
                for (val entry : dirStream) {
                    children.add(new DirNode(entry));
                }
            } catch (IOException e) {
                LOGGER.info(e.getMessage());
            }
        }
    }

    @Override
    public String toString() {
        return "DirNode{" +
                "path=" + path +
                '}';
    }

    @Override
    public Path getPayload() {
        return this.path;
    }

    @Override
    public Iterable<Node<Path>> getChildren() {
        setChildren();
        return children;
    }
}
