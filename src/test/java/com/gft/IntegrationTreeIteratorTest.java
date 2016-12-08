package com.gft;

import com.gft.iterable.ItrerableNode;
import com.gft.node.DirNode;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.Iterator;

import static org.assertj.core.api.Assertions.assertThat;

public class IntegrationTreeIteratorTest {
    @Test
    public void shouldReturnAllNodes() {
        DirNode root = new DirNode(Paths.get("C:\\Users\\ankt\\Desktop\\JMS Consumer App"));
        Iterator<DirNode> iterator = new ItrerableNode<>(root).iterator();

        assertThat(iterator).hasSize(30);
    }
}
