package org.jeecg.modules.zjc.device.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.websocket.Session;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: BugMaker
 * @Date: 2025/5/11 14:33
 * @Description:
 **/
@Configuration
public class MyBean {

    @Bean
    public Map<String, Session> sessionPool() {
        return new ConcurrentHashMap<>();
    }
}
