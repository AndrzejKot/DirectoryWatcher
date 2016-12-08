package com.gft.iterable;

import com.gft.node.Node;
import lombok.NonNull;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

public class ItrerableNode<T extends Node<T>> implements Iterable<T>{

    private final T root;

    public ItrerableNode(@NonNull T root){
        this.root = root;
    }

    @Override
    public Iterator<T> iterator() {
        return new TreeIterator(root);
    }

    private final class TreeIterator implements Iterator<T> {

        private T current;
        private Queue<T> nodeQueue = new LinkedList<>();

        private TreeIterator(@NonNull T root) {
            this.current = root;
            initCurrentChildren();
        }

        private void initCurrentChildren() {
            final Iterator<T> iterator = current.getPayload();
            while (iterator.hasNext()) {
                nodeQueue.add( iterator.next());
            }
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public T next() {
            if (!hasNext()) throw new NoSuchElementException();

            current = nodeQueue.remove();

            initCurrentChildren();

            if (nodeQueue.isEmpty()) {
                T node = current;
                current = null;
                return node;
            }

            return current;
        }
    }
}
