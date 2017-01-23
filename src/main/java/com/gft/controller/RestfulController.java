package com.gft.controller;

import com.gft.message.GreetingRest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.atomic.AtomicLong;

@Controller
@RequestMapping("/hello-world")
public class RestfulController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping(method=RequestMethod.GET)
    public @ResponseBody
    GreetingRest sayHello(@RequestParam(value="name", required=false, defaultValue="Stranger") String name) {
        return new GreetingRest(counter.incrementAndGet(), String.format(template, name));
    }

}
