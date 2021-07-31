package com.ztq;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


/**
 * @author zhengtianqi
 */
@Configuration
public class Beans {


    @Bean(name = "restTemplate")
    public static RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
