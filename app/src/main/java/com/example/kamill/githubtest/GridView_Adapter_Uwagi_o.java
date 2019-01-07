package com.example.kamill.githubtest;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;

import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class GridView_Adapter_Uwagi_o extends BaseAdapter {
    List<String> za_co;
    List<String> kto;
    Context mContext;
    List<String> uwaga;
    private DatabaseReference baza = FirebaseDatabase.getInstance().getReference();

    public GridView_Adapter_Uwagi_o(List<String> za_co, List<String> kto, Context mContext, List<String> uwaga) {
        this.za_co = za_co;
        this.kto = kto;
        this.mContext = mContext;
        this.uwaga = uwaga;
    }

    @Override
    public int getCount() {
        return za_co.size();
    }

    @Override
    public Object getItem(int position) {
        return za_co.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final TextView btn;
        if(convertView == null){
            btn = new TextView(mContext);
            btn.setTextSize(20);
            btn.setBackgroundResource(R.drawable.border_uwagi);
            btn.setText(za_co.get(position));
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder alert_dialog = new AlertDialog.Builder(mContext);
                    alert_dialog.setMessage(uwaga.get(position))
                            .setCancelable(false)
                            .setPositiveButton("Zamknij", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });


                    AlertDialog alert = alert_dialog.create();
                    alert.setTitle(za_co.get(position));
                    alert.show();

                }
            });

        } else {
            btn = (TextView) convertView;
        }
        return btn;
    }
}

