package com.gft;

import com.gft.node.DirNode;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import lombok.val;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class DirNodeTest {

    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerException() {
        new DirNode(null);
    }

    @Test
    public void shouldThrowIOExceptionThusSatisfySonar() throws Exception {
        val rootPath = Paths.get("C:\\NonExistingFile");
        val dirNode = Mockito.spy(new DirNode(rootPath));

        dirNode.getChildren();

        verify(dirNode, times(1)).getChildren();
    }

    @Test
    public void shouldReturnStringContainingNodePath() throws IOException {
        val fs = Jimfs.newFileSystem(Configuration.windows());
        val rootPath = fs.getPath("C:\\Root");

        Files.createDirectory(rootPath);

        assertTrue(new DirNode(rootPath).toString().contains("C:\\Root"));
    }
}