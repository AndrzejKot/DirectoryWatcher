package com.gft.node;

import java.util.Iterator;

public interface Node<T> {
    Iterator<T> getPayload();
}
