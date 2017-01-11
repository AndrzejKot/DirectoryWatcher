package com.gft;

import com.gft.iterable.IterableNode;
import com.gft.node.DirNode;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class DirIteratorTest {

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerException() {
        Iterator<DirNode> iterator = new IterableNode<DirNode>(null).iterator();
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldThrowNoSuchElementException() throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.windows());
        Path rootPath = fs.getPath("C:\\Root");
        Files.createDirectory(rootPath);
        DirNode rootNode = new DirNode(rootPath);
        Iterator<Path> iterator = new IterableNode<Path>(rootNode).iterator();

        iterator.next();
    }

    @Test
    public void shouldReturnTrue() throws IOException {
        final Path root = TestCaseHelper.createDirStructure();
        DirNode rootNode = new DirNode(root);
        Iterator<Path> iterator = new IterableNode<Path>(rootNode).iterator();

        Assert.assertTrue(iterator.hasNext());
    }

    @Test
    public void shouldReturnThreeNodes() throws IOException {
        final Path root = TestCaseHelper.createDirStructure();
        DirNode rootNode = new DirNode(root);
        IterableNode<Path> iterableNode = new IterableNode<>(rootNode);
        List<String> receivedPaths = new ArrayList<>();
//TODO to sie da jedna linijka
        for (Path path : iterableNode) {
            receivedPaths.add(path.toString());
        }

        assertThat(receivedPaths, containsInAnyOrder("C:\\Users\\hello.txt","C:\\Users\\one","C:\\Users\\two"));
    }
}
