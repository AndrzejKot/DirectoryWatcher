package com.gft;

import com.gft.iterable.IterableNode;
import com.gft.node.AbstractNode;
import org.junit.Test;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AbstractNodeConversionTest {
    @Test
    public void abstractNodeObservableTest() throws Exception {
        AbstractNode root = new AbstractNode("root");
        AbstractNode branchOne = new AbstractNode("branchOne");
        AbstractNode branchTwo = new AbstractNode("branchTwo");
        List<String> elements = new ArrayList<>();

        root.addChild(branchOne);
        root.addChild(branchTwo);
        Observable.from(new IterableNode<String>(root)).subscribe(elements::add);

        assertThat(elements).containsExactly("branchOne","branchTwo");
    }
}
