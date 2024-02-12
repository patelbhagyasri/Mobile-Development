package com.aquademo;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomerDetails extends AppCompatActivity {
 String myJSON;
 JSONArray product= null;
    TextView uname,uaddress,ucontact, uplan,total;
    Button btnPaid;
    SharedPreferences sp;
    String CustId,msg="";
    ProgressDialog pDialog;
    private String TAG = CustomerDetails.class.getSimpleName();
    private static String url="";
    private static String url_data_update="";
    HashMap<String, String> contact;
    String getDriverID;
    Button btnDelivered;
    EditText editTextSupply,editTextReturn,editTextDeposit;
    TextView txtDays,txtAmt,txtOld,txtTotal;

    WebIp webip ;
    Double amount ,totalamount;
    //String WebURL=webip.mywebip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle= getIntent().getExtras();
        setContentView(R.layout.activity_customer_details);
        sp= PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        webip=new WebIp();
        CustId = sp.getString("CustomerID", "");

        // getTruckID=sp.getString("truck_id", "");
       Toast.makeText(getApplicationContext(),CustId,Toast.LENGTH_SHORT).show();

        uname=(TextView)findViewById(R.id.name);
        uaddress=(TextView)findViewById(R.id.address);
        ucontact=(TextView)findViewById(R.id.mobile);
        uplan=(TextView)findViewById(R.id.plan);
        btnPaid=(Button)findViewById(R.id.btnPaid);
        total=(TextView) findViewById(R.id.total);
        editTextSupply=(EditText)findViewById(R.id.editTextSupply);
        editTextReturn=(EditText)findViewById(R.id.editTextReturn);
        editTextDeposit=(EditText)findViewById(R.id.editTextDeposit);
        btnDelivered=(Button)findViewById(R.id.btnDelivered);
        getDriverID=sp.getString("TruckId", "");

        Toast.makeText(getApplicationContext(),getDriverID,Toast.LENGTH_SHORT).show();
        url=webip.mywebip+"FetchUser.php?customer_id="+CustId;
        new custDetail().execute();
        btnDelivered.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if(editTextSupply.getText().toString().equals(""))
                {
                    amount =Double.parseDouble(contact.get("rate"));
                }
                else {
                     amount = Double.parseDouble(editTextSupply.getText().toString()) * Double.parseDouble(contact.get("rate"));
                }
                Toast.makeText(getApplicationContext(), amount.toString(),Toast.LENGTH_SHORT).show();
                totalamount=Double.parseDouble(total.getText().toString())+amount;
                total.setText(totalamount.toString());
                //Toast.makeText(getApplicationContext(),"hiiii",Toast.LENGTH_LONG).show();

                url=webip.mywebip+"insertDataCustomer.php?customer_id="+CustId+"&supply_qty="+editTextSupply.getText().toString()+"&return_qty="+editTextReturn.getText().toString()+"&truck_id="+getDriverID+"&total_amount="+total.getText().toString();
                new UpdateData().execute();
                btnDelivered.setEnabled(false);


            }
        });
        btnPaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextDeposit.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Please Insert Amount".toString(),Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Double amt=Double.parseDouble(total.getText().toString())-Double.parseDouble(editTextDeposit.getText().toString());
                    url=webip.mywebip+"insertData.php?customer_id="+CustId+"&total_amount="+amt;
                    new UpdateData().execute();
                }
            }
        });
    }

    private class custDetail extends AsyncTask<Void,Void,Void>
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
                    String name=jsonObj.getString("name");
                    String address=jsonObj.getString("address");
                    String cont=jsonObj.getString("contact");
                    String plan=jsonObj.getString("planname");
                    String rate=jsonObj.getString("rate");
                    String amount=jsonObj.getString("amount");

                    String success = jsonObj.getString("success");
                    String message = jsonObj.getString("message");
                    Log.e("name",name);


                    // tmp hash map for single contact
                    contact = new HashMap<>();
                    contact.put("name", name);
                    contact.put("address", address);
                    contact.put("contact", cont);
                    contact.put("plan", plan);
                    contact.put("rate", rate);
                    contact.put("amount", amount);
                    contact.put("success", success);
                    contact.put("message", message);
                    // adding each child node to HashMap key => value

                    Log.e("cusname",contact.get("name"));

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
            pDialog = new ProgressDialog(CustomerDetails.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(contact.get("success").toString().equals("1")) {
                uname.setText(contact.get("name"));
                uaddress.setText(contact.get("address"));
                ucontact.setText(contact.get("contact"));
                uplan.setText(contact.get("plan"));
                total.setText(contact.get("amount"));
                Log.e("custname",contact.get("name"));
            }
            //  Toast.makeText(MainActivity.this, contact.get("message").toString(),Toast.LENGTH_LONG).show();

            //Toast.makeText(RegistrationActivity.this,contact.get("success").toString() +" "+ contact.get("message").toString(),Toast.LENGTH_LONG).show();
            pDialog.dismiss();

        }
    }

    private class UpdateData extends AsyncTask<Void,Void,Void>
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
                    String amount = jsonObj.getString("amount");
                    String success = jsonObj.getString("success");
                    String message = jsonObj.getString("message");



                    // tmp hash map for single contact
                    contact = new HashMap<>();

                    contact.put("success", success);
                    contact.put("amount", amount);
                    contact.put("message", message);
                    // adding each child node to HashMap key => value


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
                                Toast.LENGTH_LONG).show();
                    }
                });

            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(CustomerDetails.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(contact.get("success").toString().equals("1")) {

                total.setText(contact.get("amount"));
                Toast.makeText(CustomerDetails.this, contact.get("message").toString(),Toast.LENGTH_LONG).show();
            }
            //  Toast.makeText(MainActivity.this, contact.get("message").toString(),Toast.LENGTH_LONG).show();

            //Toast.makeText(RegistrationActivity.this,contact.get("success").toString() +" "+ contact.get("message").toString(),Toast.LENGTH_LONG).show();
            pDialog.dismiss();

        }
    }

    //---------- insert code finish
}
