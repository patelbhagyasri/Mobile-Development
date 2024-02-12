package com.aquademo;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
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
import java.util.List;

import static android.view.WindowManager.*;

public class CustomerList extends AppCompatActivity {
    String myJSON;
    JSONArray product = null;
    SharedPreferences sp;
    ListView lst;
    String getTruckID;
    WebIp webip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_customer_list);
        sp= PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());
        webip=new WebIp();
        lst=(ListView)findViewById(R.id.listViewArea);
        getTruckID=sp.getString("TruckId", "");
   // Toast.makeText(getApplicationContext(),getTruckID,Toast.LENGTH_SHORT).show();
        getData();
    }
    protected void ShowData() {

        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            product = jsonObj.getJSONArray("result");
            String[] cust_name_arr = new String[product.length()];
            String[] cust_contact_arr = new String[product.length()];
            String[] cust_address_arr = new String[product.length()];
            final String[] cust_id_arr = new String[product.length()];
            for (int i = 0; i < product.length(); i++) {
                JSONObject c = product.getJSONObject(i);
                String cust_name = c.getString("customer_name");
                String cust_id = c.getString("customer_id");
                String cust_contact = c.getString("contact");
                String cust_address = c.getString("address");

                cust_name_arr[i] = cust_name;
                cust_id_arr[i] = cust_id;
                cust_contact_arr[i] = cust_contact;
                cust_address_arr[i] = cust_address;

            }
            CustomerListAdapter adapter=new CustomerListAdapter(CustomerList.this,cust_name_arr,cust_id_arr,cust_contact_arr,cust_address_arr);
            // AreaListAdapter adapter=new AreaListAdapter(SelectArea.this,cust_name_arr,cust_id_arr);

            lst.setAdapter(adapter);

            lst.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    view.setTag("cust_id"+position);
                    // deleteAlertMessage(cust_id[+ position]);
                    //Toast.makeText(getApplicationContext(), "You Clicked at " +cust_id_arr[+ position], Toast.LENGTH_SHORT).show();
                    SharedPreferences.Editor e = sp.edit();

                    e.putString("CustomerID",cust_id_arr[+ position]);

                    e.commit();
                    Intent intent=new Intent(getApplicationContext(),CustomerDetails.class);

                    startActivity(intent);
                    finish();
                }
            });

            lst.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                               int pos, long id) {
                    // TODO Auto-generated method stub

                    // Toast.makeText(getApplicationContext(),cust_id_arr[+ pos].toString(),Toast.LENGTH_SHORT).show();
                    insertProduct(cust_id_arr[+ pos].toString(),getTruckID);
                    getData();
                    return true;
                }
            });

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
                loadingDialog = ProgressDialog.show(CustomerList.this, "Please wait", "Loading...");
            }
            @Override
            protected String doInBackground(String... params) {


                DefaultHttpClient httpclient = new DefaultHttpClient(
                        new BasicHttpParams());
                HttpPost httppost = new HttpPost(webip.mywebip+"FetchCustomer.php?truck_id="+getTruckID);

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

    //-------code to insert data-----------

    private void insertProduct(final String cust_id,final String truck_id) {

        class InProduct extends AsyncTask<String, Void, String>{



            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected String doInBackground(String... params) {

                String CustId = params[0];
                String TruckId = params[1];


                InputStream is = null;
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("customer_id", CustId));
                nameValuePairs.add(new BasicNameValuePair("truck_id", TruckId));


                String result = null;

                try{
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(webip.mywebip+"insertTempData.php");
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
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return result;
            }

            @Override
            protected void onPostExecute(String result){
                String s = result.trim();
                int i=Integer.parseInt(s);
                if(i>=1){
                    Toast.makeText(getApplicationContext(), "No bottles Needed", Toast.LENGTH_LONG).show();

                }else {
                    Toast.makeText(getApplicationContext(), "Not Inserted", Toast.LENGTH_LONG).show();
                }
            }
        }

        InProduct insp = new InProduct();
        insp.execute(cust_id,truck_id);

    }

    //---------- insert code finish
}

