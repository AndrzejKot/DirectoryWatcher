package com.gft.node;

import java.util.Iterator;

@FunctionalInterface
public interface Node<T> {
    Iterator<T> getPayload();
}
