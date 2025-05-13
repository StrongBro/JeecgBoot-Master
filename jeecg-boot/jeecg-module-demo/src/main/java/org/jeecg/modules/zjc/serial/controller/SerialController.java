package org.jeecg.modules.zjc.serial.controller;

import org.jeecg.common.api.vo.Result;
import org.jeecg.config.shiro.IgnoreAuth;
import org.jeecg.modules.zjc.serial.service.SerialPortManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: BugMaker
 * @Date: 2025/5/5 23:12
 * @Description:
 **/

@RestController
@RequestMapping("/serial")
public class SerialController {
    private static final Logger log = LoggerFactory.getLogger(SerialController.class);
    private static SerialPortManager serialPortManager;

    public SerialController(SerialPortManager serialPortManager) {
        SerialController.serialPortManager = serialPortManager;
    }

    /**
     * 获取所有可用串口
     * @return 可用串口列表
     */
    @IgnoreAuth
    @GetMapping("/getAvailablePorts")
    public Result<List<String>> getAvailablePorts() {
        return Result.OK(SerialPortManager.findPorts());
    }

}
