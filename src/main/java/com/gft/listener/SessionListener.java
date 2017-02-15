package com.gft.listener;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

@Log4j
@Component
public class SessionListener implements HttpSessionListener {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        log.info("Session Created!");
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        log.info("Session Destroyed!");
        messagingTemplate.convertAndSend("/topic/endSession", "Session has ended!");
    }
}
