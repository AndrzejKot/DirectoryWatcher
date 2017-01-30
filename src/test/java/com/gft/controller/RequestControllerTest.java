package com.gft.controller;

import com.gft.Application;
import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RequestControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate testRestTemplate;

    @MockBean
    private RequestController controller;

    @Test
    public void shouldReturnString() throws IOException {
        given(this.controller.addFile(any())).willReturn("file.txt");
        val entity = testRestTemplate.getForEntity("http://localhost:" + this.port + "/addFile?name=file.txt", String.class);

        assertThat(entity.getBody()).isEqualTo("file.txt");
    }

    @Test
    public void shouldReturnEmptyList() throws Exception {
        val dummy = new LinkedList<String>();

        given(this.controller.getInitialDirStructure(any())).willReturn(dummy);
        val entity = this.testRestTemplate.getForEntity("http://localhost:" + this.port + "/init", List.class);

        assertThat(entity.getBody().size()).isEqualTo(0);
        assertThat(entity.getBody()).isEqualTo(dummy);
    }
}