package org.jeecg.modules.zjc.serial.service;

import com.alibaba.fastjson2.JSONObject;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.DateUtils;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 串口管理服务
 * springboot + redis + websocket + jserialcomm 实现串口实时通信
 * 支持前端选择串口，可同时连接多个串口，实时发送消息和接收串口返回的消息
 *
 * @author BugMaker
 */
@Slf4j
@Service
public class SerialPortManager {

    private static final int DATA_BITS = 8;
    private static final int READ_TIMEOUT = 1000;
    private static final int WRITE_TIMEOUT = 1000;
    private static final int OPEN_TIMEOUT = 1000;

    private final Map<String, SerialPort> serialPortPool = new ConcurrentHashMap<>();
    private final Map<String, Session> sessionPool;

    public SerialPortManager(Map<String, Session> sessionPool) {
        this.sessionPool = sessionPool;
    }

    /**
     * 查找所有可用串口
     */
    public static List<String> findPorts() {
        return Stream.of(SerialPort.getCommPorts())
                .map(SerialPort::getSystemPortName)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 打开串口
     *
     * @param portName 端口名称
     * @param baudRate 波特率
     * @return 串口对象
     */
    public SerialPort openPort(String portName, Integer baudRate) {
        log.info("serialPortPool={}", serialPortPool);
        log.info("sessionPool={}", sessionPool);
        SerialPort serialPort = SerialPort.getCommPort(portName);
        if (serialPort.isOpen()) {
            log.info("串口 {} 已经打开", portName);
            return serialPort;
        }

        configureSerialPort(serialPort, baudRate);

        try {
            if (serialPort.openPort(OPEN_TIMEOUT)) {
                    serialPortPool.put(portName, serialPort);
                    addListener(portName);
                log.info("串口 {} 打开成功", portName);
            } else {
                log.error("串口 {} 打开失败", portName);
            }
        }catch (Exception e){
            log.info("串口 {} 打开失败时发生异常,{}", portName, e.getMessage());
            Session session = sessionPool.get(portName);
            if (session != null) {
                try {
                    session.getBasicRemote().sendText("串口["+ portName +"]打开失败");
                } catch (Exception e1) {
                    log.error("发送数据到WebSocket失败", e1.getMessage());
                }
            }
            serialPort.removeDataListener();
            serialPort.closePort();
        }

        return serialPort;
    }

    private void configureSerialPort(SerialPort serialPort, Integer baudRate) {
        serialPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
        serialPort.setComPortParameters(
                baudRate,
                DATA_BITS,
                SerialPort.ONE_STOP_BIT,
                SerialPort.NO_PARITY
        );
        serialPort.setComPortTimeouts(
                SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING,
                READ_TIMEOUT,
                WRITE_TIMEOUT
        );
    }

    /**
     * 关闭串口
     */
    public void closePort(String portName) {
        SerialPort serialPort = serialPortPool.get(portName);
        if (serialPort != null && serialPort.isOpen()) {
            serialPort.removeDataListener();
            serialPort.closePort();
            serialPortPool.remove(portName);
            log.info("串口 {} 已关闭", portName);
        }
    }

    /**
     * 发送数据到串口
     */
    public void sendToPort(String portName, String content) {
        sendToPort(portName, content.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 发送数据到串口
     */
    public void sendToPort(String portName, byte[] content) {
        SerialPort serialPort = serialPortPool.get(portName);
        if (serialPort == null || !serialPort.isOpen()) {
            log.error("串口 {} 未打开，无法发送数据", portName);
            return;
        }
        serialPort.writeBytes(content, content.length);
    }

    /**
     * 从串口读取数据
     */
    private byte[] readFromPort(SerialPort serialPort) {
        if (!serialPort.isOpen()) {
            return null;
        }

        try {
            int bytesAvailable = serialPort.bytesAvailable();
            if (bytesAvailable <= 0) {
                return null;
            }

            byte[] buffer = new byte[bytesAvailable];
            int numRead = serialPort.readBytes(buffer, buffer.length);
            return numRead > 0 ? buffer : null;
        } catch (Exception e) {
            log.error("从串口读取数据失败,{}", e.getMessage());
            return null;
        }
    }

    /**
     * 添加串口监听器
     */
    private void addListener(String portName) {

        SerialPort serialPort = serialPortPool.get(portName);
        if (serialPort == null || !serialPort.isOpen()) {
            log.error("串口 {} 未打开，无法添加监听器", portName);
            return;
        }

        serialPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                    return;
                }

                byte[] data = readFromPort(serialPort);
                if (data == null) {
                    return;
                }

                String message = new String(data, StandardCharsets.UTF_8);
                log.info("串口 {} 收到数据: {}", portName, message);

                Session session = sessionPool.get(portName);
                if (session != null) {
                    JSONObject msg = new JSONObject();
                    msg.put("portName", portName);
                    msg.put("msg", message);
                    msg.put("time", System.currentTimeMillis());
//                    Result<Object> result = Result.ok(message);
                    try {
                        session.getBasicRemote().sendText(msg.toJSONString());
                    } catch (Exception e) {
                        log.error("发送数据到WebSocket失败", e);
                    }
                }
            }
        });
    }
}