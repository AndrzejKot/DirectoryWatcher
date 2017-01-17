package com.gft;

import com.gft.node.DirNode;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static junit.framework.TestCase.assertTrue;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DirNode.class)
public class DirNodeTest {
    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerException() {
        new DirNode(null);
    }

    @Test//(expected = IOException.class)
    public void shouldThrowIOException() throws Exception {
        FileSystem fs = Jimfs.newFileSystem(Configuration.windows());
        Path rootPath = fs.getPath("C:\\Root");
        Files.createDirectory(rootPath);
        final DirNode dirNode = PowerMockito.spy(new DirNode(rootPath));
//        final DirNode dirNode = PowerMockito.mock(DirNode.class);
        PowerMockito.doThrow(new IOException()).when(dirNode, "setChildren");
        dirNode.getChildren();

        verifyPrivate(dirNode).invoke("setChildren");
    }

    @Test
    public void shouldReturnStringContainingNodePath() throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.windows());
        Path rootPath = fs.getPath("C:\\Root");
        Files.createDirectory(rootPath);

        assertTrue(new DirNode(rootPath).toString().contains("C:\\Root"));
    }
}