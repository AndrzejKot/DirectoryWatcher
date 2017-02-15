package com.gft.config;

import com.gft.watcher.DirWatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class AppConfig {

    @Value("${root.dir}")
    private String root;

    @Bean
    public ConnectableObservable<Path> observable() {
        return DirWatcher.watch(Paths.get(File.separator + root)).subscribeOn(Schedulers.newThread()).publish();
    }
}
