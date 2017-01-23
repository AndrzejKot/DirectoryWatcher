package com.gft.message;

public class GreetingRest {

    private final long id;
    private String content;

    public GreetingRest(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

}
