package com.example.admin.supermarketnav;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.ViewDebug;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;

import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

/**
 * Created by purvilkamdar on 5/10/17.
 */

public class UploadDatatoGraph extends AsyncTask<String, Void, String> {
    String url;
    HttpURLConnection conn = null;
    String counter;
    String ax;
    String ay;
    String az;
    DataOutputStream dos = null;
    String lineEnd = "\r\n";
    String twoHyphens = "--";
    String boundary = "*****";


    @Override
    protected String doInBackground(String... params) {
        String type=params[0];
        //Log.w("Purvil","Params="+params[0]);
        if(params[0].equals("clear"))
        {
            try {


                String url = "http://www.parthpachchigar.com/clear.php";
                URL urls = new URL(url);
                conn = (HttpURLConnection) urls.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false);
                int ResponseCode= conn.getResponseCode();
                if(ResponseCode==200)
                {
                    Log.w("Purvil:","Called clear php");
                }
                else
                {
                    Log.w("Purvil:","Not called clear php");
                }
            }
            catch (Exception e)
            {
                Log.w("Purvil:","Unable to call clear.php",e);
            }
        }

        else if(params[0].equals("a")) {
            String counter = params[1];
            String ax = params[2];
            String ay = params[3];
            String az = params[4];
            //url="www.parthpachchigar.com/accel.php?c="+counter+"&aX="+ax+"&aY="+ay+"&aZ="+az;
            //Log.w("Purvil:","url="+url);
            url="http://www.parthpachchigar.com/accel.php";
            // 1) Connect via HTTP. 2) Encode data. 3) Send data.
            try {
                URL urls = new URL(url);
                conn = (HttpURLConnection) urls.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("c", counter);
                postDataParams.put("aX", ax);
                postDataParams.put("aY",ay);
                postDataParams.put("aZ",az);
                Log.e("params",postDataParams.toString());

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int ResponseCode = conn.getResponseCode();
                //Log.w("Purvil:","Responsecode="+ ResponseCode);
                if (ResponseCode == 200) {
                    Log.w("Purvil", "Accelerometer Datasent to server");
                } else {
                    Log.w("Purvil", "Data not sent Response code=" + ResponseCode);
                }
                //Could do something better with response.
            } catch (Exception e) {
                Log.e("log_tag", "Error:  " + e.toString());
            }
        }
        else if(params[0].equals("g")) {
            String counter = params[1];
            String gx = params[2];
            String gy = params[3];
            String gz = params[4];
            url="http://www.parthpachchigar.com/gyro.php";

            // 1) Connect via HTTP. 2) Encode data. 3) Send data.
            try {
                URL urls = new URL(url);
                conn = (HttpURLConnection) urls.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("c", counter);
                postDataParams.put("gX", gx);
                postDataParams.put("gY",gy);
                postDataParams.put("gZ",gz);
                Log.e("params",postDataParams.toString());

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();int ResponseCode = conn.getResponseCode();
                if (ResponseCode == 200) {
                    Log.w("Purvil", "Accelerometer Datasent to server");
                } else {
                    Log.w("Purvil", "Data not sent Response code=" + ResponseCode);
                }
                //Could do something better with response.
            } catch (Exception e) {
                Log.e("log_tag", "Error:  " + e.toString());
            }
        }
        else if(params[0].equals("m")) {
            String counter = params[1];
            String motion = params[2];
            url="http://www.parthpachchigar.com/motion.php";

            // 1) Connect via HTTP. 2) Encode data. 3) Send data.
            try {
                URL urls = new URL(url);
                conn = (HttpURLConnection) urls.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                //conn.setRequestMethod("POST");
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("c", counter);
                postDataParams.put("v", motion);

                Log.e("params",postDataParams.toString());

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();int ResponseCode = conn.getResponseCode();
                if (ResponseCode == 200) {
                    Log.w("Purvil", "Accelerometer Datasent to server");
                } else {
                    Log.w("Purvil", "Data not sent Response code=" + ResponseCode);
                }
                //Could do something better with response.
            } catch (Exception e) {
                Log.e("log_tag", "Error:  " + e.toString());
            }
        }

        else if(params[0].equals("u")) {
            String counter = params[1];
            String ultra = params[2];
            url="http://www.parthpachchigar.com/ultra.php";

            // 1) Connect via HTTP. 2) Encode data. 3) Send data.
            try {
                URL urls = new URL(url);
                conn = (HttpURLConnection) urls.openConnection();
                conn.setDoInput(true); // Allow Inputs
                conn.setDoOutput(true); // Allow Outputs
                conn.setUseCaches(false); // Don't use a Cached Copy
                //conn.setRequestMethod("POST");
                JSONObject postDataParams = new JSONObject();
                postDataParams.put("c", counter);
                postDataParams.put("v", ultra);

                Log.e("params",postDataParams.toString());

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();
                int ResponseCode = conn.getResponseCode();
                if (ResponseCode == 200) {
                    Log.w("Purvil", "Accelerometer Datasent to server");
                } else {
                    Log.w("Purvil", "Data not sent Response code=" + ResponseCode);
                }
                //Could do something better with response.
            } catch (Exception e) {
                Log.e("log_tag", "Error:  " + e.toString());
            }
        }

        return "Executed";
    }

    @Override
    protected void onPostExecute(String result) {

    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }
}


