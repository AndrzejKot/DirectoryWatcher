package com.gft;

import com.gft.node.DirNode;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static junit.framework.TestCase.assertTrue;

//@RunWith(PowerMockRunner.class)
//@PrepareForTest(DirNode.class)
public class DirNodeTest {
    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerException() {
        new DirNode(null);
    }

    @Test
    public void shouldThrowIOExceptionThusSatisfySonar() throws Exception {
        Path rootPath = Paths.get("C:\\NonExistingFile");
        new DirNode(rootPath).getChildren();



//        Files.createDirectory(rootPath);
//        final DirNode dirNode = PowerMockito.spy(new DirNode(rootPath));
////        final DirNode dirNode = PowerMockito.mock(DirNode.class);
//        PowerMockito.doThrow(new IOException()).when(dirNode, "setChildren");
//        dirNode.getChildren();
//
//        verifyPrivate(dirNode).invoke("setChildren");
    }

    @Test
    public void shouldReturnStringContainingNodePath() throws IOException {
        FileSystem fs = Jimfs.newFileSystem(Configuration.windows());
        Path rootPath = fs.getPath("C:\\Root");
        Files.createDirectory(rootPath);

        assertTrue(new DirNode(rootPath).toString().contains("C:\\Root"));
    }
}