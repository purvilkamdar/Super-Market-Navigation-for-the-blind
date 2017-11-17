package com.example.parthpachchigar.hardwaredevice;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Base64;
import android.util.Log;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.pubsub.Pubsub;
import com.google.api.services.pubsub.PubsubScopes;
import com.google.api.services.pubsub.model.PublishRequest;
import com.google.api.services.pubsub.model.PubsubMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by parth.pachchigar on 10/29/17.
 */

class Mqtt_Publish_subscribe extends AsyncTask<String, String, Void> {
    private static final String TAG = Mqtt_Publish_subscribe.class.getSimpleName();

    private final Context mContext;
    private final String mAppname;
    private final String mTopic;

    private Pubsub mPubsub;
    private HttpTransport mHttpTransport;

    private Handler mHandler;
    private HandlerThread mHandlerThread;

    private static final long PUBLISH_INTERVAL_MS = TimeUnit.MINUTES.toMillis(1);

    Mqtt_Publish_subscribe(Context context, String appname, String project, String topic,
                           int credentialResourceId) throws IOException {
        mContext = context;
        mAppname = appname;
        mTopic = "projects/" + project + "/topics/" + topic;


        InputStream jsonCredentials = mContext.getResources().openRawResource(credentialResourceId);
        final GoogleCredential credentials;
        try {
            credentials = GoogleCredential.fromStream(jsonCredentials).createScoped(
                    Collections.singleton(PubsubScopes.PUBSUB));
        } finally {
            try {
                jsonCredentials.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing input stream", e);
            }
        }

        mHttpTransport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        mPubsub = new Pubsub.Builder(mHttpTransport, jsonFactory, credentials)
                .setApplicationName(mAppname).build();
    }



    public void Mqtt_Publish(String lat, String lon) {

    }

    private ArrayList<JSONObject> createMessagePayload(String lat, String lon)
            throws JSONException {
        ArrayList<JSONObject> arrayList = new ArrayList<JSONObject>();
        JSONObject sensorData = new JSONObject();
        sensorData.put("latitude", String.valueOf(lat));
        sensorData.put("longitude", String.valueOf(lon));
        //Log.w("MQTT Paylod:",sensorData.toString());
        JSONObject messagePayload = new JSONObject();
        messagePayload.put("deviceId", Build.DEVICE);
        messagePayload.put("channel", "pubsub");
        messagePayload.put("timestamp", System.currentTimeMillis());
        arrayList.add(0,messagePayload);
        arrayList.add(1,sensorData);
        Log.w("MQTT Sending:",messagePayload.toString());
        return arrayList;

    }


    @Override
    protected Void doInBackground(String... params) {
        try {
            ArrayList<JSONObject> arrayList = new ArrayList<JSONObject>();
            arrayList = createMessagePayload(params[0],params[1]);

            Log.d(TAG, "publishing message data: " + arrayList.get(0).toString());
            Log.d(TAG, "publishing message attributes: " + arrayList.get(1).toString());
            HashMap<String,String> payload = new HashMap<String, String>();
            payload.put("lat",params[0]);
            payload.put("lon",params[1]);
            PubsubMessage m = new PubsubMessage();
            m.setData(Base64.encodeToString(arrayList.toString().getBytes(),
                    Base64.NO_WRAP));
            m.setAttributes(payload);
            PublishRequest request = new PublishRequest();
            request.setMessages(Collections.singletonList(m));
            mPubsub.projects().topics().publish(mTopic, request).execute();
            Log.w("MQTT","Printed on Mqtt");
        } catch (JSONException | IOException e) {
            Log.e(TAG, "Error publishing message"+ e.toString());
        }
        return null;
    }
}
