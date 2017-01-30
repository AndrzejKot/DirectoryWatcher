package com.gft.node;

import java.util.ArrayList;
import java.util.List;

class AbstractNode implements Node<String> {

    private String name;
    private List<Node<String>> children = new ArrayList<>();

    AbstractNode(String name) {
        this.name = name;
    }

    void addChild(Node<String> child) {
        this.children.add(child);
    }

    @Override
    public String getPayload() {
        return this.name;
    }

    @Override
    public Iterable<Node<String>> getChildren() {
        return children;
    }
}
