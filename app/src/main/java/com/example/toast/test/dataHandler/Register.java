package com.example.toast.test.dataHandler;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.toast.test.R;

import java.util.HashMap;

public class Register extends AppCompatActivity implements View.OnClickListener{
    private EditText e1, e2, e3, e4;
    private Button b1;
    private Spinner spinner;
    private String REGISTER_URL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);
        REGISTER_URL = getString(R.string.Register_URL);
        spinner = (Spinner)findViewById(R.id.spinner);
        final ArrayAdapter<CharSequence> addressList = ArrayAdapter.createFromResource(Register.this, R.array.address, android.R.layout.simple_list_item_1);
        spinner.setAdapter(addressList);
        e1 = (EditText)findViewById(R.id.editText1);
        e2 = (EditText)findViewById(R.id.editText2);
        e3 = (EditText)findViewById(R.id.editText3);
        e4 = (EditText)findViewById(R.id.editText4);
        b1 = (Button)findViewById(R.id.button1);
        b1.setOnClickListener(this);
        /*e1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    if(e1.getText().equals(""))
                    e1.setText("");
                }
            }
        });*/
    }
        @Override
        public void  onClick(View v){
            if(v == b1)
                registerUser();
        }
        private void registerUser() {
            String name = e1.getText().toString().trim().toLowerCase();
            String username =e2.getText().toString().trim().toLowerCase();
            String password = e3.getText().toString().trim().toLowerCase();
            String email = e4.getText().toString().trim().toLowerCase();
            String address = spinner.getSelectedItem().toString();
            if(name != "" && username != "" && password != "" && email != "" && address != "")
                register(name,username,password,email,address);
            else
                Toast.makeText(getApplicationContext(),"尚有未填資料",Toast.LENGTH_LONG).show();
        }
    private void register(String name, String username, String password, String email, String address) {
        new AsyncTask<String, Void, String>() {
            ProgressDialog loading;
            Client ruc = new Client();


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Register.this, "Please Wait", "Loading", true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            protected String doInBackground(String... params) {

                HashMap<String, String> data = new HashMap<String,String>();
                data.put("name",params[0]);
                data.put("userId",params[1]);
                data.put("pas",params[2]);
                data.put("email",params[3]);
                data.put("address",params[4]);

                String answer = ruc.sendPostRequest(REGISTER_URL,data);

                return  answer;
            }
        }.execute(name,username,password,email,address);
    }
}
