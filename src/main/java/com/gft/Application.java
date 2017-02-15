package com.gft;

import com.gft.watcher.DirWatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import rx.schedulers.Schedulers;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Paths;

@SpringBootApplication
public class Application {

    @Value("${root.dir}")
    private String root;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @PostConstruct
    public void startWatching() {
        DirWatcher.watch(Paths.get(File.separator + root)).subscribeOn(Schedulers.newThread())
                .subscribe(path -> messagingTemplate.convertAndSend("/topic/paths", path.toString()));
    }
}
