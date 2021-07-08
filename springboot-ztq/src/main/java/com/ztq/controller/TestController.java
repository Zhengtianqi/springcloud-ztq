package com.ztq.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhengtianqi
 */
@RestController
@RequestMapping(value = "/api/v1")
public class TestController {
    @GetMapping(value = "/hello")
    public String test() {
        return "hello";
    }
}
