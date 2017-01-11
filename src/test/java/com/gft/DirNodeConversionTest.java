package com.gft;

import com.gft.iterable.IterableNode;
import com.gft.node.DirNode;
import org.junit.Test;
import rx.Observable;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class DirNodeConversionTest {

    @Test
    public void dirNodeObservableTest() throws Exception {
        final Path root = TestCaseHelper.createDirStructure();
        DirNode rootNode = new DirNode(root);
        List<String> elements = new ArrayList<>();

        Observable.from(new IterableNode<Path>(rootNode)).subscribe(s-> elements.add(s.toString()));

        assertThat(elements).containsExactly("C:\\Users\\hello.txt","C:\\Users\\one","C:\\Users\\two");
    }
}
