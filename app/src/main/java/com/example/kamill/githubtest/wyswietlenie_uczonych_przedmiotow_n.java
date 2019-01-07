package com.example.kamill.githubtest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class wyswietlenie_uczonych_przedmiotow_n extends Fragment {

    private ListView lista_przedmiotow;
    private DatabaseReference baza;
    private FirebaseAuth firebaseAuth;

    public wyswietlenie_uczonych_przedmiotow_n() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_wyswietlenie_uczonych_przedmiotow_n, container, false);

        lista_przedmiotow = v.findViewById(R.id.lista_przedmiotow_list_view);
        baza = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        wyswietl_liste();

        return v;
    }


//wyświetrlanie listy przedmiotów
public void wyswietl_liste() {
        baza.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList lista = new ArrayList();
                for(DataSnapshot przemdit: dataSnapshot.child("Users").child("Nauczyciel").child(firebaseAuth.getCurrentUser().getUid()).child("lista_przedmiotów").getChildren()){
                    lista.add(przemdit.getValue());
                }
                ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, lista);
                lista_przedmiotow.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}