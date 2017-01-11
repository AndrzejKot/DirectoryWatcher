package com.gft.iterable;

import com.gft.node.Node;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

@RequiredArgsConstructor
public class IterableNode<T> implements Iterable<T>{

    @NonNull private final Node root;

    @Override
    public Iterator<T> iterator() {
        return new TreeIterator(root);
    }

    private final class TreeIterator implements Iterator<T> {

        private Node current;
        private Queue<Node> nodeQueue = new LinkedList<>();

        private TreeIterator(Node root) {
            this.current = root;
            getNodeAndSetCurrent();
        }

        private void fillQueueWithNodeChildren(Node<?> node) {
            for (Node element : node.getChildren()) {
                nodeQueue.add(element);
            }
        }

        private T getNodeAndSetCurrent() {
            Node<?> node = current;

            fillQueueWithNodeChildren(node);

            if (nodeQueue.isEmpty()) {
                current = null;
            }
            return (T) node.getPayload();
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public T next() {
            if(!hasNext()){
                throw new NoSuchElementException();
            }
            current = nodeQueue.remove();
            return getNodeAndSetCurrent();
        }
    }
}
