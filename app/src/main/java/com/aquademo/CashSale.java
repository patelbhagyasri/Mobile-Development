package com.aquademo;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

public class CashSale extends AppCompatActivity {

    EditText etsell,ettotal,etCash,etFullbottle,etEmptybottle;
    String getTruckID;
    SharedPreferences sp;
    ProgressDialog pDialog;
    String PlanType;
    WebIp webip ;
   // Spinner spinnerPlanCash;
    private String TAG = CashSale.class.getSimpleName();
    private static String url="";
    private static String url_data_update="";
    HashMap<String, String> contact;
    Button btnSaveCashSale;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_sale);
        sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        getTruckID=sp.getString("TruckId", "");
        etsell=(EditText) findViewById(R.id.etsell);
        ettotal=(EditText) findViewById(R.id.ettotal);
        etFullbottle=(EditText) findViewById(R.id.etFullbottle);
        etEmptybottle=(EditText) findViewById(R.id.etEmptybottle);
        etCash=(EditText) findViewById(R.id.etCash);
      //  spinnerPlanCash=(Spinner)findViewById(R.id.spinnerPlanCash);
        btnSaveCashSale=(Button)findViewById(R.id.btnSaveCashSale);
        webip=new WebIp();
        url=webip.mywebip+"CaseSellData.php?truck_id="+getTruckID;
        new FatchData().execute();
      // ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.Type_array,android.R.layout.simple_spinner_item);
      //  adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //spinnerPlanCash.setAdapter(adapter);
   //    spinnerPlanCash.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
     //      @Override
    //    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//                Toast.makeText(OwnDetails.this, ""+position,Toast.LENGTH_LONG).show();
      //          try {
       //             PlanType=URLEncoder.encode(parent.getItemAtPosition(position).toString(),"UTF-8");
       //         } catch (UnsupportedEncodingException e) {
       //             e.printStackTrace();
        //        }
        //        url=webip.mywebip+"pricebyplanname.php?plan_id="+(position+1);
        //        new PriceData().execute();

      //      } @Override
        //    public void onNothingSelected(AdapterView<?> parent) {

       //     }
       // });
        btnSaveCashSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Full=etFullbottle.getText().toString();
                String Empty=etEmptybottle.getText().toString();
                if(Full.equals(""))
                {
                    Toast.makeText(CashSale.this, "Enter ",Toast.LENGTH_LONG).show();
                }
                else if(Empty.equals(""))
                {
                    Toast.makeText(CashSale.this, "Enter ",Toast.LENGTH_LONG).show();
                }
                else
                {
                    Double amount=Double.parseDouble(etCash.getText().toString())*Double.parseDouble(Full);
                    url=webip.mywebip+"CaseSellDataInsert.php?truck_id="+getTruckID+"&sup_qty="+Full+"&ret_qty="+Empty+"&amount="+amount;
                    new UpdateData().execute();
                }
            }
        });
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
                    String success = jsonObj.getString("success");
                    String message = jsonObj.getString("message");

                     // tmp hash map for single contact
                    contact = new HashMap<>();

                    contact.put("success", success);
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
                                    "Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
            pDialog = new ProgressDialog(CashSale.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(contact.get("success").toString().equals("1")) {
                Toast.makeText(CashSale.this, contact.get("message").toString(),Toast.LENGTH_LONG).show();
                pDialog.dismiss();
            }
            //  Toast.makeText(MainActivity.this, contact.get("message").toString(),Toast.LENGTH_LONG).show();

            //Toast.makeText(RegistrationActivity.this,contact.get("success").toString() +" "+ contact.get("message").toString(),Toast.LENGTH_LONG).show();
            pDialog.dismiss();
            url=webip.mywebip+"CaseSellData.php?truck_id="+getTruckID;
            new FatchData().execute();

        }
    }
    private class PriceData extends AsyncTask<Void,Void,Void>
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
                    String rate=jsonObj.getString("rate");
                    String success = jsonObj.getString("success");
                    String message = jsonObj.getString("message");

                    // tmp hash map for single contact
                    contact = new HashMap<>();

                    contact.put("rate", rate);
                    contact.put("success", success);
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
                                    Toast.LENGTH_LONG).show();
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
            pDialog = new ProgressDialog(CashSale.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(contact.get("success").toString().equals("1")) {
                etCash.setText(contact.get("rate"));
                pDialog.dismiss();
            }
            //  Toast.makeText(MainActivity.this, contact.get("message").toString(),Toast.LENGTH_LONG).show();

            //Toast.makeText(RegistrationActivity.this,contact.get("success").toString() +" "+ contact.get("message").toString(),Toast.LENGTH_LONG).show();
            pDialog.dismiss();

        }
    }
    private class FatchData extends AsyncTask<Void,Void,Void>
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
                    String Sup=jsonObj.getString("Sup");
                    String amt=jsonObj.getString("amt");
                    String success = jsonObj.getString("success");
                    String message = jsonObj.getString("message");

                    // tmp hash map for single contact
                    contact = new HashMap<>();
                    contact.put("Sup", Sup);
                    contact.put("amt", amt);
                    contact.put("success", success);
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
                                    Toast.LENGTH_LONG).show();
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
            pDialog = new ProgressDialog(CashSale.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(contact.get("success").toString().equals("1")) {

                if(contact.get("Sup").toString().equals("null"))
                {
                    etsell.setText("0");
                    ettotal.setText("0");
                }
                else {
                    etsell.setText(contact.get("Sup"));
                    ettotal.setText(contact.get("amt"));

                }
                pDialog.dismiss();

            }
            //  Toast.makeText(MainActivity.this, contact.get("message").toString(),Toast.LENGTH_LONG).show();

            //Toast.makeText(Reg      istrationActivity.this,contact.get("success").toString() +" "+ contact.get("message").toString(),Toast.LENGTH_LONG).show();
            pDialog.dismiss();

        }
    }
}
