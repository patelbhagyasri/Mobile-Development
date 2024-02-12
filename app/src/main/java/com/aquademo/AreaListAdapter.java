package com.aquademo;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AreaListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] name;
    private final String[] id;
    public AreaListAdapter(Activity context, String[] name, String[] id ) {
        super(context, R.layout.area_name_list, name);
        this.context=context;
        this.name=name;
        this.id=id;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.area_name_list, null, true);
        // String fontPath = "fonts/marathi_saras.ttf";


        TextView txtName = (TextView) rowView.findViewById(R.id.txtAreaName);
        TextView txtId = (TextView) rowView.findViewById(R.id.txtAreaId);
        //  Typeface tf = Typeface.createFromAsset(context.getAssets(), fontPath);


        txtName.setText(name[position]);
//txtName.setTypeface(tf);
        txtId.setText(id[position]);


        return rowView;
    }
}