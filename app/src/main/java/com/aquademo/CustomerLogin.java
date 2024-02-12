package com.aquademo;

import android.app.ProgressDialog;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomerLogin extends AppCompatActivity{
    EditText Username,password;
    Button btnCustLogin;
    SharedPreferences spc;
    public static String cust_id="";
    ProgressDialog pDialog;
    private String TAG = CustomerLogin.class.getSimpleName();
    private static String url="";
    HashMap<String, String> contact;
   // public static final String MyPREFFRENCE = "Mypref";

    WebIp wa = new WebIp();
    String WebURL=wa.mywebip;
    WebIp webip;
    AquaDemp ad;
    String ServerURL=WebURL+"CustLogin.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getSupportActionBar().hide();
        ad=(AquaDemp)getApplicationContext();
        setContentView(R.layout.activity_customer_login);
        Username = (EditText) findViewById(R.id.etUsername);
        password = (EditText) findViewById(R.id.Password);
        webip = new WebIp();
        spc =  PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        String ucid = spc.getString("customer_id","");
        Log.e("cid",ucid);
//        if(ucid != "")
//        {
//            Intent intent = new Intent(getApplicationContext(),OwnDetails.class);
//            CustomerLogin.this.finish();
//            intent.putExtra("Cust_id",cust_id);
//            startActivity(intent);
//        }
        //-----Button--------
        btnCustLogin = (Button) findViewById(R.id.btnCustLogin);
        btnCustLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strUsername = Username.getText().toString();
                String strPassword = password.getText().toString();

                if (TextUtils.isEmpty(strUsername)) {
                    Username.setError("Enter Username");
                    return;
                } else if (TextUtils.isEmpty(strPassword)) {
                    password.setError("Enter Password");
                    return;
                } else {

                        url = webip.mywebip+"CustLogin.php?Username=" + Username.getText().toString() + "&password=" + password.getText().toString();
                        new EmployeeLogin().execute();


                }
            }
        });
    }

    private class EmployeeLogin extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected Void doInBackground(Void... voids)
        {
            Log.e("Automed",url);
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null)
            {
                try
                {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    Log.e("Automed1=",jsonObj.toString());

                    String success = jsonObj.getString("success");
                    String message = jsonObj.getString("message");

                    Log.e("Automed3=",success);
                    Log.e("Automed4=",message);



                    // tmp hash map for single contact
                    contact = new HashMap<>();

                    // adding each child node to HashMap key => value
                    contact.put("success", success);
                    contact.put("message", message);

                }
                catch (final JSONException e)
                {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run()
                        {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            }
            else
            {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });

            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CustomerLogin.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.e("Automed5=",contact.get("success").toString() + contact.get("message").toString());
            //  Toast.makeText(MainActivity.this, contact.get("message").toString(),Toast.LENGTH_LONG).show();
            if(contact.get("success").toString().equals("1"))
            {
                ad.set_cust_id(contact.get("message").toString());
                Toast.makeText(CustomerLogin.this, "Successfully Login",Toast.LENGTH_LONG).show();
                Intent i=new Intent(CustomerLogin.this,OwnDetails.class);
                i.putExtra("Cust_id",contact.get("message").toString());
                startActivity(i);
                finish();
            }
            else
            {
                Toast.makeText(CustomerLogin.this, contact.get("message").toString(),Toast.LENGTH_LONG).show();
            }
            //Toast.makeText(RegistrationActivity.this,contact.get("success").toString() +" "+ contact.get("message").toString(),Toast.LENGTH_LONG).show();
            pDialog.dismiss();



        }
    }

}
