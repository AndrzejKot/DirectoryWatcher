package com.gft.controller;

import com.gft.Application;
import com.gft.iterable.IterableNode;
import com.gft.node.DirNode;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RequestControllerTest {

    private static Path file = Paths.get("file.txt");

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @AfterClass
    public static void tearDown() throws Exception {
        FileUtils.deleteDirectory(new File("tmp.txt"));
        FileUtils.deleteDirectory(new File("dir"));
    }

    @Test
    public void shouldReturnString() {
        val entity = testRestTemplate.getForEntity("http://localhost:" + this.port + "/addFile?name=" + file, String.class);

        assertThat(entity.getBody()).isEqualTo("File: " + file + " created successfully.");
    }

    @Test
    public void shouldCatchFileAlreadyExistsException() {
        testRestTemplate.getForEntity("http://localhost:" + this.port + "/addFile?name=tmp.txt", String.class);
        val entity = testRestTemplate.getForEntity("http://localhost:" + this.port + "/addFile?name=tmp.txt", String.class);

        assertThat(entity.getBody()).isEqualTo("File: tmp.txt already exists! Try with a different name.");
    }

    @Test
    public void shouldReturnList() throws Exception {
        val root = Paths.get(File.separator + "tmp");
        val paths = new LinkedList<String>();

        for(val element : new IterableNode<Path>(new DirNode(root))) {
            paths.add(element.toString());
        }
        val entity = this.testRestTemplate.getForEntity("http://localhost:" + this.port + "/init?root=tmp", List.class);

        assertThat(entity.getBody().size()).isEqualTo(paths.size());
        assertThat(entity.getBody()).containsAll(paths);
    }
}