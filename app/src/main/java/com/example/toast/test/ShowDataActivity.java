package com.example.toast.test;


import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.toast.test.app.CareService;
import com.example.toast.test.dataHandler.Client;
import com.example.toast.test.dataHandler.Login;

import java.util.ArrayList;
import java.util.List;

public class ShowDataActivity extends AppCompatActivity {
    private ListView lv;
    private ActionBar ab;
    private Button button;
    private String[] list = {"notdata","test","test","test","test","test","test","test","test","test","test"};
    private ArrayAdapter<String> arrayAdapter;
    private MyAdapter myAdapter;
    private Intent ServiceIntent;
    private List<Data> showdata_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ServiceIntent = new Intent(this, CareService.class);
        ab = getSupportActionBar();
        if(Client.getIsLogin()) {
            ab.setTitle("you are already AutoLogin");
            if(!isWorked("CareService")){
                startService(ServiceIntent);
            }

        }
        else {
            ab.setTitle("TTTTT");
            if(!isWorked("CareService")){
                startService(ServiceIntent);
            }
        }
        showdata_list = new ArrayList<Data>();
        showdata_list.add(new Data("test1", "123"));
        showdata_list.add(new Data("test2", "456"));
        showdata_list.add(new Data("test3", "789"));
        lv = (ListView)findViewById(R.id.listView);
        //arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,list);
        myAdapter = new MyAdapter(ShowDataActivity.this, showdata_list);
        lv.setAdapter(myAdapter); //待測試

        button = (Button)findViewById(R.id.buttontest);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Client.saveLogin(ShowDataActivity.this,null,null,false);
                Client.setIsLogin(false);
                stopService(ServiceIntent);
                Intent intent = new Intent();
                intent.setClass(ShowDataActivity.this, Login.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private boolean isWorked(String className) {
        ActivityManager myManager = (ActivityManager) ShowDataActivity.this
                .getApplicationContext().getSystemService(
                        Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
                .getRunningServices(30);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString()
                    .equals(className)) {
                return true;
            }
        }
        return false;
    }
}

