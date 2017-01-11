package com.gft;

import com.gft.node.DirNode;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static junit.framework.TestCase.assertTrue;

public class DirNodeTest {
    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerException() {
        new DirNode(null);
    }

    @Test
    public void shouldReturnStringContainingNodePath() throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.windows());
        Path rootPath = fs.getPath("C:\\Root");
        Files.createDirectory(rootPath);

        assertTrue(new DirNode(rootPath).toString().contains("C:\\Root"));
    }
}