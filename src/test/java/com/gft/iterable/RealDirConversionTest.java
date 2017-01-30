package com.gft.iterable;

import com.gft.node.DirNode;
import org.junit.Test;
import rx.Observable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RealDirConversionTest {

    @Test
    public void shouldReturnThirtyNodes() throws Exception {
        DirNode root = new DirNode(Paths.get("C:\\Users\\ankt\\Desktop\\JMS Consumer App"));
        List<Path> elements = new ArrayList<>();

        Observable.from(new IterableNode<Path>(root)).subscribe(elements::add);

        assertThat(elements).hasSize(30);
    }
}
