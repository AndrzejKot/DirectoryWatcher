package com.gft.node;

public interface Node<T> {
    T getPayload();
    Iterable<Node<T>> getChildren();
}
