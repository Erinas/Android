package com.example.toast.test.dataHandler;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.toast.test.ShowDataActivity;
import com.example.toast.test.R;

import java.util.HashMap;

/**
 * Created by Toast on 2016/10/31.
 */
public class Login extends AppCompatActivity implements View.OnClickListener {
    private EditText e5,e6;
    private Button b2,goRegister;
    private ActionBar ab;
    private String Login_URL;
    private boolean check = false;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        Login_URL = getString(R.string.Login_URL);
        if(this.getSharedPreferences("loginsave",0).getBoolean("autologin", false)){
            check = false;
            check = AutologinUser();
            if(check) {
                startActivity(new Intent().setClass(Login.this, ShowDataActivity.class));
                finish();
            }
        }
        ab = getSupportActionBar();
        ab.setTitle("Login");
        e5 = (EditText)findViewById(R.id.editText5);
        e6 = (EditText)findViewById(R.id.editText6);
        b2 = (Button)findViewById(R.id.button2);
        goRegister = (Button)findViewById(R.id.goRegisterbutton);
        b2.setOnClickListener(this);
        goRegister.setOnClickListener(this);
    }
    @Override
    public void onClick(View v){
        if(v == b2){
            loginUser();
        }
        if (v == goRegister){
            startActivity(new Intent().setClass(Login.this, Register.class));
        }
    }
    private void loginUser(){
            String username = e5.getText().toString().trim().toLowerCase();
            String password = e6.getText().toString().trim().toLowerCase();
            if(username != "" && password != "")
                goLogin(username, password);
            else
                Toast.makeText(getApplicationContext(),"帳號密碼不得為空",Toast.LENGTH_LONG).show();
    }
    private boolean AutologinUser(){
        Client client = new Client();
        String username = this.getSharedPreferences("loginsave",0).getString("user",null);
        String password = this.getSharedPreferences("loginsave",0).getString("pas",null);
        HashMap<String,String> data = new HashMap<String, String>();
        data.put("userId", username);
        data.put("pas", password);
        String answer = client.sendPostRequest(Login_URL, data);
        client.CheckLogin(answer);
        if(Client.getIsLogin()){
            client.saveLogin(Login.this, username, password, true);
            return true;
        }
        return false;
    }
    private void goLogin(String username, String pas){
        new AsyncTask<String,Void,String>(){
            ProgressDialog loading;
            Client ruc = new Client();
            @Override
            protected String doInBackground(String... params){
                HashMap<String,String> data = new HashMap<String, String>();
                data.put("userId", params[0]);
                data.put("pas", params[1]);
                String answer = ruc.sendPostRequest(Login_URL, data);
                if(Client.getIsLogin())
                    ruc.saveLogin(Login.this, params[0], params[1], true);
                return answer;
            }
            @Override
            protected void onPreExecute(){
                super.onPreExecute();
                loading = ProgressDialog.show(Login.this, "Please Wait", "Loading", true, true);
            }
            @Override
            protected void onPostExecute(String s){
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
                if(Client.getIsLogin()) {
                    startActivity(new Intent().setClass(Login.this, ShowDataActivity.class));
                    finish();
                }
            }
        }.execute(username, pas);
    }


}
