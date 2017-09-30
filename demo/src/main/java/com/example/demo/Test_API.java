package com.example.demo;



import net.minidev.json.JSONObject;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@RestController
@RequestMapping(value = "/c1")
public class Test_API {
    private static final String test_string = "Hello World";
    private static final String err = "Error page";
    int mqtt_connection=-1;

    @GetMapping("/test1")
    public HashMap<String, String> test_method1() {
        HashMap<String, String> return_object = new HashMap<>();
        try {
            MqttPublishSubscribe mqtt_client_object = new MqttPublishSubscribe();
            MqttClient client;
            client=mqtt_client_object.getclient();
            if (client!=null) {
                mqtt_connection=mqtt_client_object.mqttConnect(client);
                if (mqtt_connection!=-1) {

                    /*Subscribing code */
                    mqtt_client_object.mqttSubscribe(client,"/test_topic");

                    /* Mqtt Publishing code */
                    HashMap<String, String> temp_hashmap = new HashMap<>();
                    temp_hashmap.put("Testing", "Mqtt");


                    JSONObject payload = new JSONObject(temp_hashmap);
                    mqtt_client_object.mqttPublish(client, "/test_topic", payload);


                }
            }
            return_object.put("value", "works");

        } catch (Exception e) {
            System.out.println("Exception in building Json Object=" + e.toString());
        }
        return return_object;
    }

    @PostMapping("/Plot_Points")
    public void Plot_Points()
    {
        System.out.println("Request Receieved");
        final String uri ="http://localhost:3000/co-ordinates";
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("lat","37.336732");
            jsonObject.put("lon","-121.881099");
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForLocation(uri,jsonObject);

            jsonObject.remove("lat");
            jsonObject.remove("lon");

            jsonObject.put("lat","37.336702");
            jsonObject.put("lon","-121.881069");
            //RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForLocation(uri,jsonObject);

            jsonObject.remove("lat");
            jsonObject.remove("lon");

            jsonObject.put("lat","37.336712");
            jsonObject.put("lon","-121.881079");
            //RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForLocation(uri,jsonObject);

            jsonObject.remove("lat");
            jsonObject.remove("lon");

            jsonObject.put("lat","37.336722");
            jsonObject.put("lon","-121.881089");
            //RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForLocation(uri,jsonObject);

            jsonObject.remove("lat");
            jsonObject.remove("lon");



            jsonObject.put("lat","37.336742");
            jsonObject.put("lon","-121.881109");
            //RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForLocation(uri,jsonObject);






            /*
            {lat: 37.336712, lng: -121.881079},
            {lat: 37.336722, lng: -121.881089},
            {lat: 37.336732, lng: -121.881099},
            {lat: 37.336742, lng: -121.881109},
            */
        }
        catch (Exception e)
        {
            System.out.println("Exception:"+e.toString());
        }



    }
}
