package com.gft.controller;

import com.gft.Application;
import com.gft.iterable.IterableNode;
import com.gft.node.DirNode;
import lombok.val;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RequestControllerTest {
    //Needed to increase possibility of unique file names.
    private static Path file1 = Paths.get("file1" + (int)(Math.random()*100) + ".txt");
    private static Path file2 = Paths.get("file2" + (int)(Math.random()*100) + ".txt");

    private static String root;
    @Value("${root.dir}")
    private String temporaryRoot;
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    //Needed to inject @value into static member.
    @PostConstruct
    public void setRoot() {
        root = temporaryRoot;
    }

    @AfterClass
    public static void tearDown() throws Exception {
        new File(File.separator + root + File.separator + file1.getFileName().toString()).delete();
        new File(File.separator + root + File.separator + file2.getFileName().toString()).delete();
    }

    @Test
    public void shouldReturnString() {
        val entity = testRestTemplate.getForEntity("http://localhost:" + this.port + "/addFile?name=" + file1, String.class);

        assertThat(entity.getBody()).isEqualTo("File: " + file1 + " created successfully.");
    }

    @Test
    public void shouldCatchFileAlreadyExistsException() {
        testRestTemplate.getForEntity("http://localhost:" + this.port + "/addFile?name=" + file2, String.class);
        val entity = testRestTemplate.getForEntity("http://localhost:" + this.port + "/addFile?name=" + file2, String.class);

        assertThat(entity.getBody()).isEqualTo("File: " + file2 + " already exists! Try with a different name.");
    }

    @Test
    public void shouldReturnList() throws Exception {
        val paths = new LinkedList<String>();

        for(val element : new IterableNode<Path>(new DirNode(Paths.get(File.separator + root)))) {
            paths.add(element.toString());
        }
        val entity = this.testRestTemplate.getForEntity("http://localhost:" + this.port + "/init?root=" + root, List.class);

        assertThat(entity.getBody().size()).isEqualTo(paths.size());
        assertThat(entity.getBody()).containsAll(paths);
    }
}