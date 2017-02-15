package com.gft.listener;

import lombok.extern.log4j.Log4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import rx.Subscriber;
import rx.Subscription;
import rx.observables.ConnectableObservable;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.nio.file.Path;

@Log4j
@Component
public class SessionListener implements HttpSessionListener {

    private Subscription subscription;
    @Autowired
    private ConnectableObservable<Path> observable;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        val subscriber = initSubscriber();
        observable.connect();
        subscription = observable.subscribe(subscriber);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        subscription.unsubscribe();
        messagingTemplate.convertAndSend("/topic/endSession", "Session has ended!");
    }

    private Subscriber<Path> initSubscriber() {
        return new Subscriber<Path>() {
            @Override
            public void onCompleted() {
                //This stream should have no end.
            }

            @Override
            public void onError(Throwable e) {
                log.error(e);
            }

            @Override
            public void onNext(Path path) {
                messagingTemplate.convertAndSend("/topic/paths", path.toString());
            }
        };
    }
}
