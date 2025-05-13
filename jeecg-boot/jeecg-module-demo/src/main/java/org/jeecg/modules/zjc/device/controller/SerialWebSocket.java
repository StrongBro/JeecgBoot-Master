package org.jeecg.modules.zjc.device.controller;

import org.jeecg.common.base.BaseMap;
import org.jeecg.common.constant.WebsocketConst;
import org.jeecg.common.modules.redis.client.JeecgRedisClient;
import org.jeecg.modules.zjc.serial.service.SerialPortManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: BugMaker
 * @Date: 2025/4/27 21:52
 * @Description:
 **/

@Component
@ServerEndpoint(value = "/SerialWebSocket/{portName}/{baudRate}")
public class SerialWebSocket {
    private static final Logger log = LoggerFactory.getLogger(SerialWebSocket.class);
    private static Map<String, Session> sessionPool;
    private static SerialPortManager serialPortManager;

    /**
     * Redis触发监听名字
     */
    public static final String REDIS_TOPIC_NAME = "socketHandler";

    //避免初次调用出现空指针的情况
    private static JeecgRedisClient redisClient;

    @Autowired
    public void setSessionPool(Map<String, Session> sessionPool) {
        SerialWebSocket.sessionPool = sessionPool;
    }

    @Autowired
    public void setSerialPortManager(SerialPortManager serialPortManager) {
        SerialWebSocket.serialPortManager = serialPortManager;
    }

    @Autowired
    public void setRedisClient(JeecgRedisClient jeecgRedisClient) {
        SerialWebSocket.redisClient = jeecgRedisClient;
    }



    //==========【websocket接受、推送消息等方法 —— 具体服务节点推送ws消息】========================================================================================
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "portName") String portName,@PathParam(value = "baudRate") Integer baudRate) {
        log.info("【系统 deviceWebSocket】有新的连接，portName:{}", portName);
        try {
            sessionPool.put(portName, session);
            serialPortManager.openPort(portName, baudRate);
//            log.debug("【系统 deviceWebSocket】有新的连接，总数为:{}", sessionPool.size());
        } catch (Exception e) {
            log.error("websocket onOpen发生异常(异常发生会关闭连接池资源):{}", e.getMessage());
            sessionPool.remove(portName);
            serialPortManager.closePort(portName);
        }
    }

    @OnClose
    public void onClose(@PathParam("portName") String portName) {
        try {
            sessionPool.remove(portName);
            serialPortManager.closePort(portName);
            log.info("【系统 deviceWebSocket】连接断开，总数为:{}", sessionPool.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ws推送消息
     *
     * @param portName
     * @param message
     */
    public void pushMessage(String portName, String message) {
//        for (Map.Entry<String, Session> item : sessionPool.entrySet()) {
//            //portName key值= {用户id + "_"+ 登录token的md5串}
//            //TODO vue2未改key新规则，暂时不影响逻辑
//            if (item.getKey().contains(portName)) {
//                Session session = item.getValue();
//                try {
//                    //update-begin-author:taoyan date:20211012 for: websocket报错 https://gitee.com/jeecg/jeecg-boot/issues/I4C0MU
//                    synchronized (session){
//                        log.debug("【系统 deviceWebSocket】推送单人消息:{}", message);
//                        session.getBasicRemote().sendText(message);
//                    }
//                    //update-end-author:taoyan date:20211012 for: websocket报错 https://gitee.com/jeecg/jeecg-boot/issues/I4C0MU
//                } catch (Exception e) {
//                    log.error(e.getMessage(),e);
//                }
//            }
//        }
        Session session = sessionPool.get(portName);
        if (session != null) {
            try {
                log.info("【系统 deviceWebSocket】推送单人消息:{}", message);
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        } else {
            log.info("【系统 deviceWebSocket】推送单人消息失败,没有找到对应的session:{}", portName);
        }
    }

    /**
     * ws遍历群发消息
     */
    public void pushMessage(String message) {
        try {
            for (Map.Entry<String, Session> item : sessionPool.entrySet()) {
                try {
                    item.getValue().getAsyncRemote().sendText(message);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            log.info("【系统 deviceWebSocket】群发消息:{}", message);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    /**
     * ws接受客户端消息
     */
    @OnMessage
    public void onMessage(String message, @PathParam(value = "portName") String portName) {
        if(!"ping".equals(message) && !WebsocketConst.CMD_CHECK.equals(message)){
            log.info("【系统 deviceWebSocket】收到客户端消息:{}", message);
//            this.sendMessage(portName, "your message is received");
//            this.pushMessage(portName, "your message is received");
            serialPortManager.sendToPort(portName,message.getBytes(StandardCharsets.UTF_8));
        }else{
            log.info("【系统deviceWebSocket】收到客户端心跳检测消息:{}", message);
            //update-begin---author:wangshuai---date:2024-05-07---for:【issues/1161】前端websocket因心跳导致监听不起作用---
//            this.sendMessage(portName, "I hear you and I am alive");
            this.pushMessage(portName, "I hear you and I am alive");
            //update-end---author:wangshuai---date:2024-05-07---for:【issues/1161】前端websocket因心跳导致监听不起作用---
        }

//        //------------------------------------------------------------------------------
//        JSONObject obj = new JSONObject();
//        //业务类型
//        obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_CHECK);
//        //消息内容
//        obj.put(WebsocketConst.MSG_TXT, "心跳响应");
//        this.pushMessage(userId, obj.toJSONString());
//        //------------------------------------------------------------------------------
    }

    /**
     * 配置错误信息处理
     *
     * @param session
     * @param t
     */
    @OnError
    public void onError(Session session, Throwable t) {
        log.warn("【系统 WebSocket】消息出现错误");
        t.printStackTrace();
    }
    //==========【系统 WebSocket接受、推送消息等方法 —— 具体服务节点推送ws消息】========================================================================================


    //==========【采用redis发布订阅模式——推送消息】========================================================================================
    /**
     * 后台发送消息到redis
     *
     * @param message
     */
    public void sendMessage(String message) {
        //log.debug("【系统 WebSocket】广播消息:" + message);
        BaseMap baseMap = new BaseMap();
        baseMap.put("portName", "");
        baseMap.put("message", message);
        redisClient.sendMessage(SerialWebSocket.REDIS_TOPIC_NAME, baseMap);
    }

    /**
     * 此为单点消息 redis
     *
     * @param portName
     * @param message
     */
    public void sendMessage(String portName, String message) {
        BaseMap baseMap = new BaseMap();
        baseMap.put("portName", portName);
        baseMap.put("message", message);
        redisClient.sendMessage(SerialWebSocket.REDIS_TOPIC_NAME, baseMap);
    }

    /**
     * 此为单点消息(多人) redis
     *
     * @param portNames
     * @param message
     */
    public void sendMessage(String[] portNames, String message) {
        for (String portName : portNames) {
            sendMessage(portName, message);
        }
    }
}
