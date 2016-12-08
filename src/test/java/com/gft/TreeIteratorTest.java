package com.gft;

import com.gft.iterable.ItrerableNode;
import com.gft.node.DirNode;
import com.gft.node.Node;
import com.google.common.collect.ImmutableList;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class TreeIteratorTest {

    private static Path createDirStructure() throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.windows());
        Path rootPath = fs.getPath("C:\\Users");
        Files.createDirectory(rootPath);

        Path hello = rootPath.resolve("hello.txt"); // /rootPath/hello.txt
        Files.write(hello, ImmutableList.of("hello world"), StandardCharsets.UTF_8);

        final Path one = rootPath.resolve("one");
        Files.createDirectory(one);

        final Path two = rootPath.resolve("two");
        Files.createDirectory(two);

        return rootPath;
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerException() {
        Iterator<DirNode> iterator = new ItrerableNode<DirNode>(null).iterator();
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldThrowNoSuchElementExceptionAfterSecondNext() throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.forCurrentPlatform());
        Path rootPath = fs.getPath("C:\\Root");
        Files.createDirectory(rootPath);
        DirNode rootNode = new DirNode(rootPath);
        Iterator<DirNode> iterator = new ItrerableNode<>(rootNode).iterator();

        iterator.next();
        iterator.next();
    }

    @Test
    public void shouldReturnTrue() throws IOException {
        final Path root = createDirStructure();
        DirNode rootNode = new DirNode(root);
        Iterator<DirNode> iterator = new ItrerableNode<>(rootNode).iterator();

        Assert.assertTrue(iterator.hasNext());
    }

    @Test
    public void shouldReturnAllNodes() throws IOException {
        final Path root = createDirStructure();
        DirNode rootNode = new DirNode(root);
        ItrerableNode<DirNode> itrerableNode = new ItrerableNode<>(rootNode);
        List<String> receivedPaths = new ArrayList<>();
//TODO to sie da jedna linijka
        for (Node node : itrerableNode) {
            receivedPaths.add(((DirNode)node).getPath().toString());
        }

        assertThat(receivedPaths, containsInAnyOrder("C:\\Users\\hello.txt","C:\\Users\\one","C:\\Users\\two"));
    }
}
