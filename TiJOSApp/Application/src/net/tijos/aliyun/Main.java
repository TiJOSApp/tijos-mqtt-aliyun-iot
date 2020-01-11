package net.tijos.aliyun;

import java.io.IOException;
import java.util.Formatter;

import tijos.framework.networkcenter.dns.TiDNS;
import tijos.framework.platform.wlan.TiWiFi;
import tijos.framework.networkcenter.mqtt.esp8266.*;

//"product_key":"b1ijiAZqEdV","device_name":"tikit-demo","device_secret":"iEANyI5Id9alYDNe2K6dBD6omsGWWKLY",
//only available for ESP8266
public class Main {

    private static final long timestamp = System.currentTimeMillis();
    private static final String PRODUCT_KEY = "b1ijiAZqEdV";
    private static final String DEVICE_NAME = "tikit-demo";//System.getProperty("host.name");
    private static final String CLIENT_ID = format("%s|securemode=3,signmethod=hmacsha1,timestamp=%d|", DEVICE_NAME, timestamp);
    private static final String BROKER = format("tcp://%s.iot-as-mqtt.cn-shanghai.aliyuncs.com:1883", PRODUCT_KEY);
    private static final String USER_NAME = format("%s&%s", DEVICE_NAME, PRODUCT_KEY);
    private static final String USER_KEY = "iEANyI5Id9alYDNe2K6dBD6omsGWWKLY";
    private static final String USER_PASS = HmacSHA1.getHmacSHA1(USER_KEY, format("clientId%sdeviceName%sproductKey%stimestamp%d", DEVICE_NAME, DEVICE_NAME, PRODUCT_KEY, timestamp));

    private static final String TOPIC = "/" + PRODUCT_KEY + "/" + DEVICE_NAME + "/data";


    public static void main(String[] args) throws IOException {

        TiWiFi.getInstance().startup(10000);
        TiDNS.getInstance().startup();

        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(false);
        connOpts.setKeepAliveInterval(80);
        connOpts.setUserName(USER_NAME);
        connOpts.setPassword(USER_PASS);


        System.err.println(BROKER);
        System.err.println(CLIENT_ID);
        System.err.println(USER_NAME);
        System.err.println(USER_PASS);


        connOpts.setAutomaticReconnect(true);

        MqttClient mqttClient = new MqttClient(BROKER, CLIENT_ID);
        mqttClient.SetMqttClientListener(new MqttClientListener() {

            @Override
            public void unsubscribeCompleted(Object arg0, int arg1, String arg2, int arg3) {
                System.err.println("unsubscribeCompleted");
            }

            @Override
            public void subscribeCompleted(Object arg0, int arg1, String arg2, int arg3) {
                System.err.println("subscribeCompleted: " + arg2);
            }

            @Override
            public void publishCompleted(Object arg0, int arg1, String arg2, int arg3) {
                System.err.println("publishCompleted: " + arg2);
            }

            @Override
            public void onMqttConnectSuccess(Object arg0) {
                System.err.println("onMqttConnectSuccess");
            }

            @Override
            public void onMqttConnectFailure(Object arg0, int arg1) {
                System.err.println("onMqttConnectFailure");
            }

            @Override
            public void messageArrived(Object arg0, String arg1, byte[] payload) {
                System.err.println("messageArrived: " + new String(payload));
            }

            @Override
            public void connectionLost(Object arg0) {
                System.err.println("connectionLost");
            }

            @Override
            public void connectComplete(Object arg0, boolean arg1) {
                System.err.println("connectComplete");

            }
        });

        try {
            mqttClient.connect(connOpts, mqttClient);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mqttClient.subscribe(TOPIC, 1);


        String message = "{\r\n" +
                "\"temperature\":25.1\r\n" +
                "\"humidity\":65\r\n" +
                "\"pressure\":101.5\r\n" +
                "\"location\":\"100, 200\"\r\n" +
                "}";

        while (true) {
            try {
                mqttClient.publish(TOPIC, message.getBytes(), 1, false);
                Thread.sleep(5000);
            } catch (InterruptedException e) {
            }
        }


    }


    public static String format(String format, Object... args) {
        return new Formatter().format(format, args).toString();
    }

}
