package com.ztq.discovery;

import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Hystrix的断路器工厂类
 */
@Component()
public class HystrixClientFallbackFactoryForRemoteClientForBaseDms implements FallbackFactory<RemoteClientForBaseDms> {

    private static final Logger logger = LoggerFactory.getLogger(HystrixClientFallbackFactoryForRemoteClientForBaseDms.class);

    @Override
    public RemoteClientForBaseDms create(Throwable throwable) {
        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        logger.info("remoteClient fallback; reason was: " + throwable.getMessage());
        return (parameters, token) -> null;
    }
}
