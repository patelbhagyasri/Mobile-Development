package com.aquademo;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;

import java.util.jar.Manifest;

public class ChooseActivity extends AppCompatActivity {
    Button btnCall,btnMsg;
    String smsText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        btnCall = (Button) findViewById(R.id.btnCall);
        btnMsg = (Button) findViewById(R.id.btnMsg);

        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String phone_no= tv_phone.getText().toString().replaceAll("-","");
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:+917405238714"));
                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                startActivity(callIntent);

            }
        });


        btnMsg.setOnClickListener(new View.OnClickListener() {
            @Override
          public void onClick(View v) {
                Intent intent = new Intent("android.intent.action.VIEW");

                /** creates an sms uri */
                Uri data = Uri.parse("sms:7405238714");

                /** Setting sms uri to the intent */
                intent.setData(data);

                /** Initiates the SMS compose screen, because the activity contain ACTION_VIEW and sms uri */
                startActivity(intent);

            }
        });



     CardView cv = (CardView) findViewById(R.id.cardViewUser); // creating a CardView and assigning a value.
        cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            Intent i = new Intent(ChooseActivity.this, CustomerLogin.class);
                startActivity(i);
       }
        });

        CardView cvd = (CardView) findViewById(R.id.cardViewDriver); // creating a CardView and assigning a value.
        cvd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent id = new Intent(ChooseActivity.this, DeliveryBoyLogin.class);
                startActivity(id);
            }
        });



    }
}

