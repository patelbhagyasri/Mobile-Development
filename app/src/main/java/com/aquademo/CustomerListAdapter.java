package com.aquademo;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class CustomerListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] name;
    private final String[] id;
    private final String[] contact;
    private final String[] address;

    public CustomerListAdapter(Activity context, String[]name, String[] id,String[] contact,String[] address) {
        super(context, R.layout.customer_name_list,name);
        this.context=context;
        this.name=name;
        this.id=id;
        this.contact=contact;
        this.address=address;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.customer_name_list, null, true);
        // String fontPath = "fonts/marathi_saras.ttf";

        TextView txtName = (TextView) rowView.findViewById(R.id.textView3);
        TextView txtId = (TextView) rowView.findViewById(R.id.textView6);
        TextView txtContact = (TextView) rowView.findViewById(R.id.textView4);
        TextView txtAddress = (TextView) rowView.findViewById(R.id.textView5);
        //  Typeface tf = Typeface.createFromAsset(context.getAssets(), fontPath);


        txtName.setText(name[position]);
//txtName.setTypeface(tf);
        txtId.setText(id[position]);
        txtContact.setText(contact[position]);
        txtAddress.setText(address[position]);


        return rowView;
    }

}

