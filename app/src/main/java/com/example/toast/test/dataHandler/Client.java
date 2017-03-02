package com.example.toast.test.dataHandler;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Toast on 2016/10/25.
 */
public class Client {
    private static boolean IsLogin = false;
    private String answer;
    private JSONObject jo;
    private static SharedPreferences sp;
    private static SharedPreferences.Editor editor;
    public String sendPostRequest(String requestURL, HashMap<String, String> postData) {
        URL url;
        String response = "";

        try {
            url = new URL(requestURL);
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            huc.setReadTimeout(15000);
            huc.setConnectTimeout(1500);
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
            return CheckLogin(response);
    }
    public String CheckLogin(String res){
        String answer = "";
        try {
            jo = new JSONObject(res);
            answer = jo.getString("Answer");
            IsLogin = jo.getBoolean("IsLogin");
        } catch (JSONException e) {
        e.printStackTrace();
        }
        return answer;
    }

    private String getPostDataString(HashMap<String,String> data) throws UnsupportedEncodingException{
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
    public static boolean getIsLogin(){
        return IsLogin;
    }
    public static void setIsLogin(boolean isLogin){
        IsLogin = isLogin;
    }
    public static void saveLogin(Context _Context, String user, String pas, boolean saveLogin){
        sp = _Context.getSharedPreferences("loginsave",0);
        editor = sp.edit();
        editor.putString("user", user).putString("pas", pas).putBoolean("autologin", saveLogin).commit();
    }
}
