package com.aquademo;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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



public class DbDashboard extends AppCompatActivity {
    CardView cardView1,cardView2,cardView3,cardView4,cardViewRemain;
    TextView txtFullJar,txtEmptyJar;
    String myJSON;
    String myJSONFullJar;
    String myJSONEmptyJar;
    JSONArray product ;
   JSONArray productFullJar ;
   JSONArray productEmptyJar ;
    SharedPreferences sp;

    WebIp webip;


    Button btnScan;
    //int totalJar;
 //   int fullJarQty;
   // int emptyJarQty;
    int backpress;


    String getDriverID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_db_dashboard);
        webip=new WebIp();
        sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());


        btnScan = (Button) findViewById(R.id.btnScan);
        final Activity activity = this;
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                //integrator.setBeepEnabled(false);
                //integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });


     
        getDriverID=sp.getString("TruckId", "");
        cardView1=(CardView)findViewById(R.id.cardView1);
        cardView2=(CardView)findViewById(R.id.cardView2);
        cardView3=(CardView)findViewById(R.id.cardView3);
        cardView4=(CardView)findViewById(R.id.cardView4);
        cardViewRemain=(CardView)findViewById(R.id.cardViewRemain);

        //txt1=(TextView) findViewById(R.id.txt1);
        // txtSmallJar=(TextView)findViewById(R.id.txtSmallJar);
        //  txtBigJar=(TextView)findViewById(R.id.txtBigJar);
        txtFullJar=(TextView)findViewById(R.id.txtFullJar);
        txtEmptyJar=(TextView)findViewById(R.id.txtEmptyJar);



        cardView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtFullJar.getText().toString().equals("0"))
                {
                    Toast.makeText(DbDashboard.this,"Full Bottle Not Available",Toast.LENGTH_LONG).show();
                }
                else {
                    Intent cv1 = new Intent(getApplicationContext(), CashSale.class);
                    startActivity(cv1);
                }
            }
        });
        cardView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cv2=new Intent(getApplicationContext(),CustomerList.class);
                startActivity(cv2);
                //Toast.makeText(getApplicationContext(),getDriverID.toString(),Toast.LENGTH_SHORT).show();
               // Toast.makeText(DeliveryBoyLogin.this,result.toString(), Toast.LENGTH_LONG).show();

            }
        });
        cardView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cv3=new Intent(getApplicationContext(),FullBottlesActivity.class);
                startActivity(cv3);
            }
        });
        cardView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cv4=new Intent(getApplicationContext(),EmptyBottles.class);
                startActivity(cv4);
            }
        });
        cardViewRemain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cvr=new Intent(getApplicationContext(),ShowRemainClients.class);
                startActivity(cvr);
            }
        });


        getData();
        getProductQty();
        getEmptyProductQty();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        getData();
        getProductQty();
        getEmptyProductQty();
    }

    public void onBackPressed(){
        backpress = (backpress + 1);
        Toast.makeText(getApplicationContext(), " Press Back again to Exit ", Toast.LENGTH_SHORT).show();

        if (backpress>1) {

            sp= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            sp.edit().clear().commit();
            Intent ilogin=new Intent(getApplicationContext(),ChooseActivity.class);
            startActivity(ilogin);
            finish();
            this.finish();
        }
    }

    protected void ShowData() {

        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            product = jsonObj.getJSONArray("result");
            String[] cust_name_arr = new String[product.length()];


            for (int i = 0; i < product.length(); i++) {
                JSONObject c = product.getJSONObject(i);
                String qty = c.getString("product_qty");


                cust_name_arr[i] = qty;


            }
            // txtSmallJar.setText(cust_name_arr[0]);
            //txtBigJar.setText(cust_name_arr[1]);




        } catch (JSONException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
    public void getData() {
        class GetDataJSON extends AsyncTask<String, Void, String> {
            private Dialog loadingDialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = ProgressDialog.show(DbDashboard.this, "Please wait", "Loading...");
            }
            @Override
            protected String doInBackground(String... params) {

                DefaultHttpClient httpclient = new DefaultHttpClient(
                        new BasicHttpParams());
                HttpPost httppost = new HttpPost(webip.mywebip+"FetchDriverProduct.php?truck_id="+getDriverID);

                // Depends on your web service
                httppost.setHeader("Content-type", "application/json");

                InputStream inputStream = null;

                String result = null;
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();

                    inputStream = entity.getContent();
                    // json is UTF-8 by default
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(inputStream, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                } catch (Exception e) {
                    // Oops
                } finally {
                    try {
                        if (inputStream != null)
                            inputStream.close();
                    } catch (Exception squish) {
                    }
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result) {
                myJSON = result;
                ShowData();
                loadingDialog.dismiss();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute();
    }


    protected void ShowProductQty() {

        try {
            JSONObject jsonObj = new JSONObject(myJSONFullJar);
            productFullJar = jsonObj.getJSONArray("resultQty");

            String[] total_product_qty = new String[productFullJar.length()];

            for (int i = 0; i < productFullJar.length(); i++) {
                JSONObject c = productFullJar.getJSONObject(i);

                String total_qty = c.getString("product_qty");



                total_product_qty[i]=total_qty;

            }

            txtFullJar.setText(total_product_qty[0]);



        } catch (JSONException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
    public void getProductQty() {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {


                DefaultHttpClient httpclient = new DefaultHttpClient(
                        new BasicHttpParams());
                HttpPost httppost = new HttpPost(webip.mywebip+"FetchFullJar.php?truck_id="+getDriverID);

                // Depends on your web service
                httppost.setHeader("Content-type", "application/json");

                InputStream inputStream = null;

                String resultQty = null;
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();

                    inputStream = entity.getContent();
                    // json is UTF-8 by default
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(inputStream, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    resultQty = sb.toString();
                } catch (Exception e) {
                    // Oops
                } finally {
                    try {
                        if (inputStream != null)
                            inputStream.close();
                    } catch (Exception squish) {
                    }
                }
                return resultQty;
            }

            @Override
            protected void onPostExecute(String result) {
                myJSONFullJar = result;
                ShowProductQty();
            }
        }
        GetDataJSON gq = new GetDataJSON();
        gq.execute();
    }

    protected void ShowEmptyProductQty() {

        try {
            JSONObject jsonObj = new JSONObject(myJSONEmptyJar);
            productEmptyJar = jsonObj.getJSONArray("resultEmpty");

            String[] total_product_qty = new String[productEmptyJar.length()];

            for (int i = 0; i < productEmptyJar.length(); i++) {
                JSONObject c = productEmptyJar.getJSONObject(i);

                String total_qty = c.getString("product_qty");



                total_product_qty[i]=total_qty;

            }

            txtEmptyJar.setText(total_product_qty[0]);



        } catch (JSONException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }
    public void getEmptyProductQty() {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {


                DefaultHttpClient httpclient = new DefaultHttpClient(
                        new BasicHttpParams());
                HttpPost httppost = new HttpPost(webip.mywebip+"FetchEmptyJar.php?truck_id="+getDriverID);

                // Depends on your web service
                httppost.setHeader("Content-type", "application/json");

                InputStream inputStream = null;

                String resultEmptyQty = null;
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();

                    inputStream = entity.getContent();
                    // json is UTF-8 by default
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(inputStream, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    resultEmptyQty = sb.toString();
                } catch (Exception e) {
                    // Oops
                } finally {
                    try {
                        if (inputStream != null)
                            inputStream.close();
                    } catch (Exception squish) {
                    }
                }
                return resultEmptyQty;
            }

            @Override
            protected void onPostExecute(String result) {
                myJSONEmptyJar = result;
                ShowEmptyProductQty();
            }
        }
        GetDataJSON geq = new GetDataJSON();
        geq.execute();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents()==null){
                Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(this, result.getContents(),Toast.LENGTH_LONG).show();
                Intent intent=new Intent(getApplicationContext(),CustomerDetails.class);
                intent.putExtra("CustomerID", result.getContents().toString());
                startActivity(intent);

            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
