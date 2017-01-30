package com.gft.node;

import com.gft.iterable.IterableNode;
import org.junit.Test;
import rx.Observable;
import rx.observers.TestSubscriber;

import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;

public class AbstractNodeSubscriberTest {
    @Test
    public void shouldReturnTwoNodes() throws Exception {
        AbstractNode root = new AbstractNode("root");
        AbstractNode branchOne = new AbstractNode("branchOne");
        AbstractNode branchTwo = new AbstractNode("branchTwo");
        final Observable<String> from = Observable.from(new IterableNode<>(root));
        TestSubscriber<String> testSubscriber = new TestSubscriber<>();

        root.addChild(branchOne);
        root.addChild(branchTwo);
        from.subscribe(testSubscriber);

        testSubscriber.assertNoErrors();
        assertEquals(2, testSubscriber.getOnNextEvents().size());
        testSubscriber.assertReceivedOnNext(Arrays.asList("branchOne","branchTwo"));
    }
}
