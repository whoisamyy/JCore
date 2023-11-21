package ru.whoisamyy.core;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorld {
    @RequestMapping("/")
    public String helloWorld() {
        return "Hello World!";
    }

    @RequestMapping("/wtf")
    public String wtf(String username, String num) {
        return "Hwtfd! "+ username + num;
    }

    @PostMapping("/helowirld")
    public String hii(@RequestParam int a) {
        return ""+a*3;
    }
}