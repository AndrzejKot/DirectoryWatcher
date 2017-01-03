package com.gft;

import com.gft.iterable.IterableNode;
import com.gft.node.AbstractNode;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

public class AbstractNodeSubscriberTest {
    @Test
    public void abstractNodeSubscriberTest() throws Exception {
        AbstractNode root = new AbstractNode();
        AbstractNode branchOne = new AbstractNode();
        AbstractNode branchTwo = new AbstractNode();
        final Observable<AbstractNode> from = Observable.from(new IterableNode<>(root));
        TestSubscriber<AbstractNode> testSubscriber = new TestSubscriber<>();

        root.addChild(branchOne);
        root.addChild(branchTwo);
        from.subscribe(testSubscriber);
        final List<AbstractNode> abstractNodes = testSubscriber.getOnNextEvents();

        testSubscriber.assertNoErrors();
        assertEquals(abstractNodes.size(), 2);
        assertThat(abstractNodes).containsExactly(branchOne,branchTwo);
    }
}
