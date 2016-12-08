package com.gft.iterable;

import com.gft.node.Node;
import lombok.NonNull;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;


public class TreeIterable implements Iterable<Node> {

    private final Node root;

    public TreeIterable(Node root) {
        this.root = root;
    }

    @Override
    public Iterator<Node> iterator() {
        return new TreeIterator(root);
    }

    private class TreeIterator implements Iterator<Node> {

        private Node current;
        private Queue<Node> nodeQueue = new LinkedList<>();

        private TreeIterator(@NonNull Node root) {
            nodeQueue.add(root);
            this.current = root;
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public Node next() {
            if (!hasNext()) throw new NoSuchElementException();

            current = nodeQueue.remove();

            final Iterator iterator = current.getPayload();
            while (iterator.hasNext()) {
                nodeQueue.add((Node) iterator.next());
            }

            if (nodeQueue.size() == 0) {
                Node node = current;
                current = null;
                return node;
            }

            return current;
        }
    }
}
