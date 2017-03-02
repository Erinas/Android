package com.example.toast.test.app;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.example.toast.test.dataHandler.Client;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Toast on 2016/11/24.
 */
public class Care {
    private String answer;
    private JSONArray ja;
    private JSONObject jo;
    public String sendPostRequest(String requestURL, HashMap<String, String> postData) {
        URL url;
        String response = "";

        try {
            url = new URL(requestURL);
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setReadTimeout(15000);
            huc.setConnectTimeout(15000);
            huc.setRequestMethod("POST");
            huc.setDoInput(true);
            huc.setDoOutput(true);
            huc.connect();

            OutputStream os = huc.getOutputStream();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            bw.write(getPostDataString(postData));
            bw.flush();
            bw.close();
            os.close();
            int responseCode = huc.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                BufferedReader br = new BufferedReader(new InputStreamReader(huc.getInputStream()));
                response = br.readLine();
            } else {
                response = "error";
                return response;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (response == "")
            return "請檢查網路狀態";

        if(response != null)
            response.trim();
        if(response.startsWith("\ufeff"))
            response = response.substring(1);
        return response;
    }
    private String GetCare (String res){
        try {
            ja = new JSONArray(res);
        }catch (JSONException e){
            e.printStackTrace();
            return "";
        }
        return ja.toString();
    }
    private String getPostDataString(HashMap<String,String> data) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String,String> entry : data.entrySet()){
            if (first){
                first = false;
            }else {
                result.append("&");
            }
            result.append(URLEncoder.encode(entry.getKey(),"UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(),"UTF-8"));
        }
        return result.toString();
    }
}
