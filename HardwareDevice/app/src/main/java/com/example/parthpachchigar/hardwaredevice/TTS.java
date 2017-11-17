package com.example.parthpachchigar.hardwaredevice;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * Created by parth.pachchigar on 10/30/17.
 */

public class TTS extends Thread{

    public TextToSpeech tts;
    private boolean TTS_init = false;
    public handler mqueue = new handler();

    public void init_TTS(Context context)
    {
        tts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    Log.w("TTS: ","Initialized");
                    tts.setLanguage(Locale.US);
                    tts.setSpeechRate(1f);
                    TTS_init = true;
                }
            }
        });
    }

    @Override
    public void run()
    {
        while (true)
        {
            if(TTS_init) {
                if(!mqueue.high_priority_message_queue.isEmpty())
                {
                    while(!mqueue.high_priority_message_queue.isEmpty())
                    {
                        try {
                            tts.speak(mqueue.high_priority_message_queue.get(0), TextToSpeech.QUEUE_ADD, null, null);
                            Log.w("TTS spoken:",mqueue.high_priority_message_queue.get(0));
                            mqueue.pop_high_priority();
                            Thread.sleep(500);
                        }
                        catch (Exception e)
                        {
                            Log.w("Error in TTS: ",e.toString());
                        }
                    }
                }
                else
                {
                    if(!mqueue.message_queue.isEmpty())
                    {
                        try {
                            tts.speak(mqueue.message_queue.get(0), TextToSpeech.QUEUE_ADD, null, null);
                            Log.w("TTS spoken:",mqueue.message_queue.get(0));
                            mqueue.pop();
                            Thread.sleep(500);
                        }
                        catch (Exception e)
                        {
                            Log.w("Error in TTS: ",e.toString());
                        }
                    }
                }
            }
        }
    }

}