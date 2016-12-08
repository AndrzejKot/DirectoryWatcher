package com.gft.node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AbstractNode implements Node<AbstractNode> {

    private List<AbstractNode> children = new ArrayList<>();

    private List<AbstractNode> getChildren() {
        return children;
    }

    public void addChild(AbstractNode child) {
        this.children.add(child);
    }

    @Override
    public Iterator<AbstractNode> getPayload() {
        return getChildren().iterator();
    }
}
