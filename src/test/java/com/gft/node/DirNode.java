package com.gft.node;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DirNode implements Node<DirNode> {
    //TODO lombok
    private final Path path;
    private List<DirNode> children = new ArrayList<>();

    public DirNode(Path root) {
        this.path = root;
    }

    public Path getPath() {
        return path;
    }

    private void setChildren() {
        try {
            if (Files.isDirectory(path)) {
                DirectoryStream<Path> dirStream = Files.newDirectoryStream(path);
                for (Path entry : dirStream) {
                    children.add(new DirNode(entry));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "DirNode{" +
                "path=" + path +
                '}';
    }

    @Override
    public Iterator<DirNode> getPayload() {
        setChildren();
        return children.iterator();
    }
}
