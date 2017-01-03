package com.gft;

import com.gft.iterable.IterableNode;
import com.gft.node.DirNode;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;

public class RealDirIteratorTest {
    @Test
    public void shouldReturnAllNodes() {
        DirNode root = new DirNode(Paths.get("C:\\Users\\ankt\\Desktop\\JMS Consumer App"));
        Iterator<DirNode> iterator = new IterableNode<>(root).iterator();

        assertThat(iterator).hasSize(30);
    }
}
