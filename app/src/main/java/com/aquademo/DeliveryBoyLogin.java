package com.aquademo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class DeliveryBoyLogin extends AppCompatActivity {

    EditText etTruckNo,password;
    Button btnDriverLogin;
    SharedPreferences sp;


    WebIp wa = new WebIp();
    String WebURL=wa.mywebip;

    String ServerURL=WebURL+"login.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_delivery_boy_login);

        etTruckNo = (EditText) findViewById(R.id.etTruckNo);
        password = (EditText) findViewById(R.id.Password);

        sp= PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        String uid = sp.getString("TruckId","");
        if(uid != "")
        {
            Intent intent = new Intent(getApplicationContext(),DbDashboard.class);
            DeliveryBoyLogin.this.finish();
            startActivity(intent);
        }
        //-----Button--------
        btnDriverLogin = (Button) findViewById(R.id.btnDriverLogin);
        btnDriverLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strUsername = etTruckNo.getText().toString();
                String strPassword = password.getText().toString();

                if (TextUtils.isEmpty(strUsername)) {
                    etTruckNo.setError("Enter Username");
                    return;
                } else if (TextUtils.isEmpty(strPassword)) {
                    password.setError("Enter Password");
                    return;
                } else {
                    login(etTruckNo.getText().toString(), password.getText().toString());
                }
            }
        });
    }

    public void login(final String name,final String email){
    class SendPostReqAsyncTask extends AsyncTask<String, Void, String>{
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            //  startProgress();
    }
        @Override
        protected String doInBackground(String... params) {

            String NameHolder = name ;
            String EmailHolder = email ;
            InputStream is = null;
            String result = null;
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            nameValuePairs.add(new BasicNameValuePair("etTruckNo", NameHolder));
            nameValuePairs.add(new BasicNameValuePair("password", EmailHolder));
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(ServerURL);
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = httpClient.execute(httpPost);

                HttpEntity entity = response.getEntity();
                is = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                result = sb.toString();

            } catch (ClientProtocolException e) {

            } catch (IOException e) {

            }
            return result;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //stopProgress();
            try{
                String s = result.trim();
                int i=Integer.parseInt(s);
                if(i!=0)
                {

                    SharedPreferences.Editor editor=sp.edit();

                    editor.putString("TruckId",result.trim());
                    editor.putString("truck_number",etTruckNo.getText().toString());
                    editor.putString("password",password.getText().toString());
                    editor.commit();

                    //Toast.makeText(MainActivity.this,delivery_name.getText().toString(), Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(DeliveryBoyLogin.this,DbDashboard.class);
                    DeliveryBoyLogin.this.finish();
                    startActivity(intent);
                    Toast.makeText(DeliveryBoyLogin.this,"Wel Come ", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(DeliveryBoyLogin.this,"Invalid Username and Password",Toast.LENGTH_LONG).show();

                }
            }catch (Exception e){}


            //Toast.makeText(DeliveryBoyLogin.this,result.toString(), Toast.LENGTH_LONG).show();
        }
    }

        SendPostReqAsyncTask sendPostReqAsyncTask = new SendPostReqAsyncTask();

        sendPostReqAsyncTask.execute(name, email);
    }
}