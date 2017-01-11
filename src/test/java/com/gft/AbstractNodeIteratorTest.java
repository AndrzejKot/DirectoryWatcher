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
        AbstractNode root = new AbstractNode("root");
        Iterator<String> iterator = new IterableNode<String>(root).iterator();

        iterator.next();
        iterator.next();
    }

    @Test
    public void shouldReturnFalse() throws IOException {
        AbstractNode root = new AbstractNode("root");
        Iterator<String> iterator = new IterableNode<String>(root).iterator();

        Assert.assertFalse(iterator.hasNext());
    }

    @Test
    public void shouldReturnTrue() throws IOException {
        AbstractNode root = new AbstractNode("root");
        AbstractNode branchOne = new AbstractNode("branchOne");
        root.addChild(branchOne);
        Iterator<String> iterator = new IterableNode<String>(root).iterator();

        Assert.assertTrue(iterator.hasNext());
    }

    @Test
    public void shouldReturnThreeNodes() throws IOException {
        //Arrange
        AbstractNode root = new AbstractNode("root");
        AbstractNode branchOne = new AbstractNode("branchOne");
        AbstractNode branchTwo = new AbstractNode("branchTwo");
        root.addChild(branchOne);
        root.addChild(branchTwo);
        final Iterator<String> nodeIterator = new IterableNode<String>(root).iterator();
        //Act

        //Assert
        assertThat(nodeIterator).containsExactly("branchOne","branchTwo");
    }
}
