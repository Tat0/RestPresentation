package controllers;

import entities.Greeting;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

@RestController
public class GreetingController {

    private static final String template = "Hello %s";
    private final AtomicLong counter = new AtomicLong();

    @GetMapping(value = "/greeting", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String value) {
        return new Greeting(counter.incrementAndGet(), String.format(template, value));
    }
}
