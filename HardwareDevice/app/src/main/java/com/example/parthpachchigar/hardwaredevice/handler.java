package com.example.parthpachchigar.hardwaredevice;

import java.util.ArrayList;

/**
 * Created by parth.pachchigar on 10/30/17.
 */

public class handler {
    static ArrayList<String> message_queue = new ArrayList<String>();
    static ArrayList<String> high_priority_message_queue = new ArrayList<String>();

    public void high_priority_put(String string)
    {
        high_priority_message_queue.add(string);
    }

    public void pop_high_priority()
    {
        if(!high_priority_message_queue.isEmpty())
        {
            high_priority_message_queue.remove(0);
        }
    }

    public void put(String string)
    {
        message_queue.add(string);
    }
    public void pop()
    {
        if (!message_queue.isEmpty())
        {
            message_queue.remove(0);
        }
    }
}