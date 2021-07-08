package com.ztq.discovery;

import feign.Headers;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

/**
 * 服务代理类
 */
@FeignClient(name = "base-dms", fallbackFactory = HystrixClientFallbackFactoryForRemoteClientForBaseDms.class)
public interface RemoteClientForBaseDms {

    @Headers({"Content-Type: application/json"})
    @LoadBalanced
    @PostMapping(value = "/inner/national-code/get-drug-identity-codes")
    String get(@RequestBody Map<String, String> parameters, @RequestHeader("Cookie") String token);
}
