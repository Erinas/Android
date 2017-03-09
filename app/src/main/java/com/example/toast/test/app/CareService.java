package com.example.toast.test.app;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.toast.test.R;
import com.example.toast.test.ShowDataActivity;
import com.example.toast.test.dataHandler.Client;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Toast on 2016/11/16.
 */
public class CareService extends Service {
    private String TAG = "CareService";
    private Handler handler = new Handler();
    private Thread conT;
    private boolean TBreak;
    private Care care;
    private JSONObject jsonObject;
    private JSONArray jsonArray;
    private boolean first = true;
    private String userId;
    String[] capIdtmp = {"",""};
    private String Care_URL;
    private Date Server_updateTime,updateTime;
    @Override
    public void onCreate(){
        super.onCreate();
        Log.d(TAG,"Create");
        Care_URL = getString(R.string.Care_URL);
        userId = this.getSharedPreferences("loginsave",0).getString("user",null);
        care = new Care();
        conT = new Thread(getCare);
        TBreak = false;
        conT.start();
        TBreak = true;
        Log.d(TAG,"Start");

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startID){
        super.onStartCommand(intent, flags, startID);

        return START_REDELIVER_INTENT;  //被系統殺掉嘗試自動重啟並傳入Intent
        //return START_REDELIVER_INTENT;  //被系統殺掉嘗試自動重啟並傳入Intent
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        TBreak = false;
        Log.d(TAG,"Destory");
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private Runnable getCare = new Runnable() {
        @Override
        public void run() {
            while (TBreak) {
                Log.d(TAG, "Start");
                HashMap<String, String> data = new HashMap<String, String>();
                data.put("userId", userId);
                //data.put("userId", "1234"); //test
                try {
                    jsonArray = new JSONArray(care.sendPostRequest(Care_URL, data));
                    //ToDo get Server_updateTime
                    if(updateTime != Server_updateTime) {
                        doCare(jsonArray);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    conT.sleep(60000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }

        }
    };
    private String[] getArrayValue(JSONArray ja,String keyValue){
        String[] array = new String[ja.length()];
        try{
            for(int i = 0; i < ja.length(); i++){
                array[i] = ja.getString(i);
                JSONObject jo = new JSONObject(array[i]);
                array[i] = jo.getString(keyValue);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        return array;
    }
    private Date[] getDate(JSONArray ja, String keyValue){
        String[] array = getArrayValue(ja, keyValue);
        Date[] date = new Date[ja.length()];
        String format = "yyyy-MM-dd hh:mm:ss";
        try{
            for(int i = 0; i < ja.length(); i++){
                date[i] = new SimpleDateFormat(format).parse(array[i]);
            }
        }catch (ParseException e){
            e.printStackTrace();
        }
        return date;
    }
    private boolean doCare(JSONArray ja){
            Date[] expires = getDate(ja, "expires");
            Date[] effective = getDate(ja, "effective");
            String[] address = getArrayValue(ja, "address");
            String[] event = getArrayValue(ja, "event");
            String[] headline = getArrayValue(ja ,"headline");
            //String[] valueName = getArrayValue(ja, "valueName");
            //String[] value = getArrayValue(ja, "value");
            String[] capIdentifier = getArrayValue(ja, "capIdentifier");
            SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
            if(first){

                capIdtmp = capIdentifier;
                String tmp = "";
                boolean diff,diffirst;
                StringBuilder sba = new StringBuilder();
                //通知所有
                diffirst = true;
                diff = false;
                for(int i = 0 ;i < capIdentifier.length ;i++){
                    if(!capIdentifier[i].equals(tmp)) {
                        diff = true;
                        Log.d(TAG, capIdentifier[i]);
                        sba.append(address[i] + ",");
                    }else {
                        sba.append(address[i] + ",");
                    }
                    if(diff && !diffirst){
                        sba.deleteCharAt(sba.lastIndexOf(","));
                        String datetmp = "生效日期:" + sdFormat.format(effective[i]) + "\n結束日期:" + sdFormat.format(expires[i]);
                        notifications(i,headline[i],sba.toString(),datetmp);
                        sba = new StringBuilder();
                    }
                    tmp = capIdentifier[i];
                    Log.d("address", address[i]);
                    diffirst = false;
                }

                first = false;
            }else{
                boolean check;
                for(int i = 0 ;i < capIdentifier.length ;i++){  //檢查，不一樣的才通知
                    check = true;
                    for(int j = 0 ; j < capIdtmp.length ; j++){
                        if (capIdentifier[i].equals(capIdtmp[j])) {
                            check = false;
                            break;  //忽略
                        }
                    }
                    if(check){
                        Log.d(TAG, capIdentifier[i]);   //通知
                        String datetmp = "生效日期:" + sdFormat.format(effective[i]) + "\n結束日期:" + sdFormat.format(expires[i]);
                        notifications(i,headline[i],address[i],datetmp);
                    }
                }
                capIdtmp = capIdentifier;
            }


            return true;
    }
    private void notifications(int i ,String... s){
        final int notifyID = i; // 通知的識別號碼

        final Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle(); // 建立BigTextStyle
        bigTextStyle.setBigContentTitle(s[0]); // 當BigTextStyle顯示時，用BigTextStyle的setBigContentTitle覆蓋setContentTitle的設定
        bigTextStyle.bigText(s[1] + "\n" + s[2]); // 設定BigTextStyle的文字內容

        final Intent intent = new Intent(getApplicationContext(), ShowDataActivity.class);
        final NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE); // 取得系統的通知服務
        final Notification notification = new Notification.Builder(getApplicationContext()).setSmallIcon(R.mipmap.ic_launcher).setContentTitle(s[0]).setContentText(s[1] + "\n" + s[2]).setStyle(bigTextStyle).build(); // 建立通知
        notificationManager.notify(notifyID, notification); // 發送通知
    }
}
