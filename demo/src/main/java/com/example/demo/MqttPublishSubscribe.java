package com.example.demo;

import net.minidev.json.JSONObject;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Properties;

class Mqtt_callback implements MqttCallback {
    public void connectionLost(Throwable arg0)
    {
        System.out.println("Connection lost on broker");
    }

    public void messageArrived(String topic, MqttMessage message) throws Exception{

        if(topic=="/co-ordinates")
        {
            
        }

        System.out.println("New Message arrived on:");
        System.out.println("Topic:"+ topic);
        System.out.println("Message:"+ new String(message.getPayload()));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        System.out.println("Delivery is complete");
    }

}

public class MqttPublishSubscribe {
    private final static String Properties_File = "mqtt.properties";
    Properties properties = new Properties();

    public MqttClient getclient()
    {
        try
        {

            //File file = new File("mqtt.properties");
            //System.out.println(file.getAbsolutePath());
            MemoryPersistence persistence = new MemoryPersistence();
            InputStream is = this.getClass().getClassLoader().getResourceAsStream(Properties_File);
            if (is!=null)
                properties.load(is);
            else
                System.out.println("Input Stream is null");
            //MqttClient sampleClient = new MqttClient("tcp://127.0.0.1:1883","8882211",persistence);
            MqttClient sampleClient = new MqttClient(properties.getProperty("BROKER_URL").toString(), properties.getProperty("CLIENT_ID").toString(), persistence);
            return sampleClient;
        }
        catch (Exception e)
        {
            System.out.println("Error while forming Mqtt client");
            e.printStackTrace();

        }
        return null;
    }

    public int mqttConnect(MqttClient client) throws MqttException {

        try {
            System.out.println("About to connect to MQTT broker with the following parameters: - BROKER_URL=" + properties.getProperty("BROKER_URL") + " CLIENT_ID=" + properties.getProperty("CLIENT_ID"));
            //MqttClient sampleClient = new MqttClient(properties.getProperty("BROKER_URL"), properties.getProperty("CLIENT_ID"), persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            client.connect(connOpts);

            System.out.println("Connected");
            return 0;
        }
        catch (Exception e)
        {
            System.out.println("Error while connecting to broker:"+ e.toString());
        }
        return -1;
    }

    public void mqttPublish(MqttClient client,String topic, JSONObject payload)
    {
        System.out.println("Topic:"+topic);
        System.out.println("Publishing message="+ payload.toString());
        try
        {
            MqttMessage message = new MqttMessage(payload.toString().getBytes(Charset.forName("UTF-8")));
            client.publish(topic,message);
        }
        catch (Exception e)
        {
            System.out.println("Exception while publishing message:"+ e.toString());
        }

    }

    public void mqttSubscribe(MqttClient client, String topic)
    {
        client.setCallback(new Mqtt_callback());
        try {
            client.subscribe(topic, 1);
        }
        catch (Exception e)
        {
            System.out.println("Error while subscribing:"+e.toString());
        }
    }

    public void mqttDisconnect(MqttClient client)
    {
        try {
            client.disconnect();
        }
        catch (Exception e)
        {
            System.out.println("Error while disconnecting"+e.toString());
        }
    }
}
