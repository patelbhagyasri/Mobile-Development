package com.aquademo;

import android.util.Log;
import android.app.Application;
public class AquaDemp extends Application{
    private String cust_id;
    public String get_cust_id()
    {
        return cust_id;
    }
    public void set_cust_id(String cust_id)
    {
        Log.e("id",cust_id);
        this.cust_id=cust_id;
    }
}
