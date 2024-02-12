package com.aquademo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;

public class OwnDetails extends AppCompatActivity {
    String myJSON;
    EditText etTotalAmount,billamount;
    JSONArray product = null;
    String CustId, msg = "";
    ProgressDialog pDialog;
    private String TAG = OwnDetails.class.getSimpleName();
    private static String url="";
    private static String url_data_update="";
    HashMap<String, String> contact;
    TextView txtCustName, txtCustAddress, txtCustCntc;
    Button btnSaveDetails;
    WebIp webip;
    SharedPreferences spc;
    int backpress;
    AquaDemp ad;
    String PlanType;
    Spinner spinnerPlan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        setContentView(R.layout.activity_own_details);
        Intent i = getIntent();
        String name="";
        ad=(AquaDemp)getApplicationContext();
        Log.e("nirmalid",ad.get_cust_id());
      //  String Custm_id = i.getStringExtra("Cust_id");
        //Toast.makeText(OwnDetails.this,delivery_name.getText().toString(), Toast.LENGTH_LONG).show();
        spc = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        webip = new WebIp();
        spinnerPlan=(Spinner)findViewById(R.id.spinnerPlan);
        CustId = spc.getString("CustomerID", "");
        Log.e("id",CustId);
        url=webip.mywebip+"FetchCustomerInfobyid.php?customer_id="+ad.get_cust_id();
        new custDetail().execute();
       // Toast.makeText(getApplicationContext(), CustId, Toast.LENGTH_SHORT).show();
        billamount=(EditText)findViewById(R.id.billamount);
        txtCustName = (TextView) findViewById(R.id.txtCustName);
        etTotalAmount = (EditText) findViewById(R.id.etTotalAmount);
        txtCustAddress = (TextView) findViewById(R.id.txtCustAddress);
        txtCustCntc = (TextView) findViewById(R.id.txtCustCntc);
        btnSaveDetails = (Button) findViewById(R.id.btnSaveDetails);
      /*  uplan=(TextView)findViewById(R.id.plan);
        paid=(Button)findViewById(R.id.btnPaid);
        editTextSupply=(EditText)findViewById(R.id.editTextSupply);
        editTextReturn=(EditText)findViewById(R.id.editTextReturn);
        editTextDeposit=(EditText)findViewById(R.id.editTextDeposit);
        btnDelivered=(Button)findViewById(R.id.btnDelivered);
        getDriverID=sp.getString("TruckId", "");
        Toast.makeText(getApplicationContext(),CustId,Toast.LENGTH_SHORT).show();
        getData(); */
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.Type_array,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPlan.setAdapter(adapter);
        spinnerPlan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

//                Toast.makeText(OwnDetails.this, ""+position,Toast.LENGTH_LONG).show();
                try {
                    PlanType=URLEncoder.encode(parent.getItemAtPosition(position).toString(),"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                url=webip.mywebip+"pricebyplanname.php?plan_id="+(position+1);
                new PriceData().execute();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        btnSaveDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String amount=etTotalAmount.getText().toString();
                url=webip.mywebip+"updateCustomerDataById.php?customer_id="+ad.get_cust_id()+"&planname="+PlanType+"&rate="+amount;
                new UpdateData().execute();
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
                                    Toast.LENGTH_SHORT)
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
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                });

            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(OwnDetails.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(contact.get("success").toString().equals("1")) {
                txtCustName.setText(contact.get("name"));
                txtCustAddress.setText(contact.get("address"));
                txtCustCntc.setText(contact.get("contact"));
                if(contact.get("plan").equals("NORMAL WATER")) {
                    spinnerPlan.setSelection(0);

                }
                else if(contact.get("plan").equals("COLD WATER"))
                {
                    spinnerPlan.setSelection(1);
                }else if(contact.get("plan").equals("COLD JAR"))
                {
                    spinnerPlan.setSelection(2);
                }
                billamount.setText(contact.get("amount"));
                Log.e("custname",contact.get("name"));
            }
            //  Toast.makeText(MainActivity.this, contact.get("message").toString(),Toast.LENGTH_LONG).show();

            //Toast.makeText(RegistrationActivity.this,contact.get("success").toString() +" "+ contact.get("message").toString(),Toast.LENGTH_LONG).show();
            pDialog.dismiss();

        }
    } private class PriceData extends AsyncTask<Void,Void,Void>
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
                                    Toast.LENGTH_SHORT)
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
                                Toast.LENGTH_SHORT)
                                .show();
                    }
                });

            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(OwnDetails.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
          //  pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(contact.get("success").toString().equals("1")) {
             etTotalAmount.setText(contact.get("rate"));
               // pDialog.dismiss();
            }
            //  Toast.makeText(MainActivity.this, contact.get("message").toString(),Toast.LENGTH_LONG).show();

            //Toast.makeText(RegistrationActivity.this,contact.get("success").toString() +" "+ contact.get("message").toString(),Toast.LENGTH_LONG).show();
            //pDialog.dismiss();

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
            pDialog = new ProgressDialog(OwnDetails.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if(contact.get("success").toString().equals("1")) {
                Toast.makeText(OwnDetails.this, contact.get("message").toString(),Toast.LENGTH_SHORT).show();

            }
            //  Toast.makeText(MainActivity.this, contact.get("message").toString(),Toast.LENGTH_LONG).show();

            //Toast.makeText(RegistrationActivity.this,contact.get("success").toString() +" "+ contact.get("message").toString(),Toast.LENGTH_LONG).show();
            pDialog.dismiss();

        }
    }


        @Override
        protected void onRestart () {
            super.onRestart();
        }


        @Override
        public void onBackPressed () {
            backpress = (backpress + 1);
            Toast.makeText(getApplicationContext(), " Press Back again to Exit ", Toast.LENGTH_SHORT).show();

            if (backpress > 1) {

                spc = PreferenceManager.
                        getDefaultSharedPreferences(getApplicationContext());
                spc.edit().clear().commit();
                Intent ilogin = new Intent(getApplicationContext(), ChooseActivity.class);
                startActivity(ilogin);
                finish();
                this.finish();
            }
        }
    }


