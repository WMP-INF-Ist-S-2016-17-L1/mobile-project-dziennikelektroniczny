package com.example.kamill.githubtest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;


import java.util.List;

public class GridView_Adapter_o extends BaseAdapter {
    List<String> lstSource;
    Context mContext;
    List<String> zaco;


    public GridView_Adapter_o(List<String> lstSource, Context mContext, List<String> zaco) {
        this.lstSource = lstSource;
        this.mContext = mContext;
        this.zaco = zaco;
    }


    @Override
    public int getCount() {
        return lstSource.size();
    }

    @Override
    public Object getItem(int position) {
        return lstSource.get(position);
    }

    public Object getItem_za_co(int position){
        return zaco.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Button btn;
        if(convertView == null){
            btn = new Button(mContext);
            btn.setWidth(400);

            btn.setBackgroundResource(R.drawable.button_oceny);
            btn.setTextSize(25);
            btn.setText(lstSource.get(position));

            if(zaco.get(position).equals("Sprawdzian")){
                btn.setBackgroundResource(R.drawable.button_oceny_sprawdzian);
            }
            if(zaco.get(position).equals("Kartkówka")){
                btn.setBackgroundResource(R.drawable.button_oceny_kartk);
            }
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder alert_dialog = new AlertDialog.Builder(mContext);
                    alert_dialog.setMessage(zaco.get(position))
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });

                    AlertDialog alert = alert_dialog.create();
                    alert.setTitle("Ocena: "+lstSource.get(position));
                    alert.show();

                }
            });

                } else {
            btn = (Button) convertView;
        }
        return btn;
    }
}
