package com.gft;

import com.gft.iterable.IterableNode;
import com.gft.node.AbstractNode;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractNodeIteratorTest {


    @Test(expected = NullPointerException.class)
    public void shouldThrowNullPointerException() {
        Iterator<AbstractNode> iterator = new IterableNode<AbstractNode>(null).iterator();
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldThrowNoSuchElementExceptionAfterSecondNext() throws IOException {
        AbstractNode root = new AbstractNode();
        Iterator<AbstractNode> iterator = new IterableNode<>(root).iterator();

        iterator.next();
        iterator.next();
    }

    @Test
    public void shouldReturnFalse() throws IOException {
        AbstractNode root = new AbstractNode();
        Iterator<AbstractNode> iterator = new IterableNode<>(root).iterator();

        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void shouldReturnTrue() throws IOException {
        AbstractNode root = new AbstractNode();
        AbstractNode branchOne = new AbstractNode();
        root.addChild(branchOne);
        Iterator<AbstractNode> iterator = new IterableNode<>(root).iterator();

        Assert.assertTrue(iterator.hasNext());
    }

    @Test
    public void shouldReturnAllNodes() throws IOException {
        //Arrange
        AbstractNode root = new AbstractNode();
        AbstractNode branchOne = new AbstractNode();
        AbstractNode branchTwo = new AbstractNode();
        root.addChild(branchOne);
        root.addChild(branchTwo);
        final Iterator<AbstractNode> nodeIterator = new IterableNode<>(root).iterator();
        //Act

        //Assert
        assertThat(nodeIterator).containsExactly(branchOne,branchTwo);
    }
}
