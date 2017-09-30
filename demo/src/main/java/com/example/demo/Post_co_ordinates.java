package com.example.demo;

import net.minidev.json.JSONObject;
import org.springframework.web.client.RestTemplate;

public class Post_co_ordinates {
    private static final String uri = "http://localhost:3000/co-ordinates";

    public void post_data(String lat, String lon)
    {
        RestTemplate restTemplate =  new RestTemplate();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("lat",lat);
        jsonObject.put("lon",lon);
        restTemplate.postForLocation(uri,jsonObject);
    }

}
