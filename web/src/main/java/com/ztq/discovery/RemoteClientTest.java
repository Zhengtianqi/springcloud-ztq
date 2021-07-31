package com.ztq.discovery;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 远程调用测试
 */
@RestController
public class RemoteClientTest {
    @Resource
    private RemoteClientForBaseDms remoteClientForBaseDms;

    @RequestMapping("/remote/test")
    @ResponseBody
    public String test(@RequestParam String cndc, HttpServletRequest request) {
        Map<String, String> params = new HashMap<>(8);
        params.put("nationalIdentityCode", cndc);
        String s = this.remoteClientForBaseDms.get(params, "cookieStr");
        return s;
    }

}
