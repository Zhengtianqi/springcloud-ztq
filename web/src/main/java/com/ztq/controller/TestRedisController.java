package com.ztq.controller;

import com.ztq.service.impl.RedisLock;
import com.ztq.utils.redis.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhengtianqi
 */
@RestController
@RequestMapping(value = "/api/v1")
public class TestRedisController {

    @GetMapping(value = "/hello")
    public String test() {
        return "hello";
    }

    @Autowired
    private RedisLock redisLock;

    @GetMapping("/lock")
    @ResponseBody
    public Object lock() {
        redisLock.tryLock("id-20210712", "value-zhengtianqi");
        return "lock success~";
    }

    @GetMapping("/unlock")
    @ResponseBody
    public Object unlock() {
        redisLock.unlock("id-20210712", "value-zhengtianqi");
        return "unlock success~";
    }

    @Autowired
    private RedisUtil redisUtil;

    @GetMapping("/set")
    @ResponseBody
    public Object set() {

        List<Object> strings = new ArrayList<>(3);
        strings.add("哈哈哈");
        strings.add("嘤嘤嘤");
        strings.add("嘎嘎嘎");

        redisUtil.lSetList("666", strings);

        get();

        return "set success~";
    }

    @GetMapping("/get")
    @ResponseBody
    public Object get() {

        System.out.println("list size: " + redisUtil.lGetListSize("666"));
        System.out.println(redisUtil.lGet("666", 0, -1));

        return "get success";
    }

    @GetMapping("/del")
    @ResponseBody
    public Object del() {

        redisUtil.lRemove("666", 0, "哈哈哈");
        get();

        return "delete success";
    }


}
