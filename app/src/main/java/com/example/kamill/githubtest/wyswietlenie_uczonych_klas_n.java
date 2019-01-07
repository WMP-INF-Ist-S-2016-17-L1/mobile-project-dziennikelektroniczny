package com.example.kamill.githubtest;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class wyswietlenie_uczonych_klas_n extends Fragment {

    private ListView lista_klas;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference baza;

    public wyswietlenie_uczonych_klas_n() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_wyswietlenie_uczonych_klas_n, container, false);

        lista_klas = v.findViewById(R.id.lista_klas_list_view);
        baza = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        wyswietl_klasy();

        return v;
    }


    //wyświetlanie klas nauczyciela
    public void wyswietl_klasy(){
        baza.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList lista = new ArrayList();
                for(DataSnapshot przemdit: dataSnapshot.child("Users").child("Nauczyciel").child(firebaseAuth.getCurrentUser().getUid()).child("lista_klas").getChildren()){
                    lista.add(przemdit.getValue());
                }
                ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, lista);
                lista_klas.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
