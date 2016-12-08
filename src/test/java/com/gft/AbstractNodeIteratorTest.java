package com.gft;

import com.gft.iterable.ItrerableNode;
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
        Iterator<AbstractNode> iterator = new ItrerableNode<AbstractNode>(null).iterator();
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldThrowNoSuchElementExceptionAfterSecondNext() throws IOException {
        AbstractNode root = new AbstractNode();
        Iterator<AbstractNode> iterator = new ItrerableNode<>(root).iterator();

        iterator.next();
        iterator.next();
    }

    @Test
    public void shouldReturnTrue() throws IOException {
        AbstractNode root = new AbstractNode();
        Iterator<AbstractNode> iterator = new ItrerableNode<>(root).iterator();

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
        final Iterator<AbstractNode> nodeIterator = new ItrerableNode<>(root).iterator();
        //Act

        //Assert
        assertThat(nodeIterator).containsExactly(branchOne,branchTwo);
    }
}
