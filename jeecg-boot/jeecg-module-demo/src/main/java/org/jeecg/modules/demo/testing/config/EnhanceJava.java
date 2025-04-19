package org.jeecg.modules.demo.testing.config;


import com.alibaba.fastjson.JSONObject;
import org.jeecg.modules.online.cgform.enhance.CgformEnhanceJavaInter;
import org.jeecg.modules.online.config.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Author: BugMaker
 * @Date: 2025/4/14 22:03
 * @Description:
 **/

@Component(value = "TestingEnhanceJava")
public class EnhanceJava implements CgformEnhanceJavaInter {
    private static final Logger log = LoggerFactory.getLogger(EnhanceJava.class);
    @Override
    public void execute(String s, JSONObject jsonObject) throws BusinessException {
        log.info("EnhanceJava execute method called");
        log.info("Received parameter: {}", s);
        log.info("Received JSON object: {}", jsonObject.toJSONString());
    }
}
