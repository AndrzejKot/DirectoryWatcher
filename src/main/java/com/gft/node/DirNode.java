package com.gft.node;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.val;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Log4j
public class DirNode implements Node<Path> {
    @NonNull private final Path path;
    private List<Node<Path>> children = new ArrayList<>();

    private void setChildren() throws Exception{
            try (val dirStream = Files.newDirectoryStream(path)){
                for (val entry : dirStream) {
                    children.add(new DirNode(entry));
                }
            } catch (IOException e) {
                log.debug(e);
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
        try {
            setChildren();
        } catch (Exception e) {
            log.info("Poszlo");
        }
        return children;
    }
}
