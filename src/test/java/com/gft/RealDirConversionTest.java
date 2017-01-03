package com.gft;

import com.gft.iterable.IterableNode;
import com.gft.node.DirNode;
import org.junit.Test;
import rx.Observable;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RealDirConversionTest {

    @Test
    public void dirNodeObservableTest() throws Exception {
        DirNode root = new DirNode(Paths.get("C:\\Users\\ankt\\Desktop\\JMS Consumer App"));
        List<String> elements = new ArrayList<>();

        Observable.from(new IterableNode<>(root)).subscribe(s-> elements.add(s.getPath().toString()));

        assertThat(elements).hasSize(30);
    }
}
