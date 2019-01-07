package com.example.kamill.githubtest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class zarzadzanie_przedmiotami_a extends Fragment {

    //zmienne
    private EditText nazwa_nowego_przedmiotu;
    private Button nowy_przedmiot_btn;
    private ListView lista_przedmiotow_list_view;
    private DatabaseReference baza;
    private ArrayList lista_przedmiotow_array_list;

    public zarzadzanie_przedmiotami_a() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_zarzadzanie_przedmiotami_a, container, false);
        nazwa_nowego_przedmiotu = v.findViewById(R.id.nowy_przedmiot_edit_text);
        nowy_przedmiot_btn = v.findViewById(R.id.dodaj_przedmiot_btn);
        lista_przedmiotow_list_view = v.findViewById(R.id.lista_przedmiotow_list_view);
        baza = FirebaseDatabase.getInstance().getReference();

        aktualizuj_liste();

        nowy_przedmiot_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dodaj_przedmiot();
            }
        });

        lista_przedmiotow_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String przedmiot = ((TextView)view).getText().toString();
                final AlertDialog.Builder alert_dialog = new AlertDialog.Builder(getContext());
                alert_dialog.setMessage("Czy na pewno chcesz usunąć '"+przedmiot+"' z bazy danych ?")
                        .setCancelable(false)
                        .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                baza.child("Przedmioty").child(przedmiot).removeValue();
                                baza.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for(DataSnapshot klasa: dataSnapshot.child("Klasy").getChildren()){
                                            baza.child("Klasy").child(klasa.getKey()).child("Przedmioty").child(przedmiot).removeValue();
                                            for(DataSnapshot uczen: dataSnapshot.child("Klasy").child(klasa.getKey()).child("Uczniowie").getChildren()){
                                                baza.child("Klasy").child(klasa.getKey()).child("Uczniowie").child(uczen.getKey()).child("Oceny").child(przedmiot).removeValue();
                                            }
                                        }


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                aktualizuj_liste();
                                Toast.makeText(getContext(),"Usunięto '"+przedmiot+"' z bazy danych", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = alert_dialog.create();
                alert.setTitle("Usuń");
                alert.show();

            }
        });



        return v;
    }

    // dodawanie nowego przedmiotu do bazy danych
    public void dodaj_przedmiot(){
        Boolean czy_zawiera = false;
        String nazwa_przedmiotu = nazwa_nowego_przedmiotu.getText().toString();
        if(lista_przedmiotow_array_list.contains(nazwa_przedmiotu)){
            czy_zawiera = true;
        }
        if(czy_zawiera == false) {
            baza.child("Przedmioty").child(nazwa_przedmiotu).setValue(nazwa_przedmiotu);
            aktualizuj_liste();
            nazwa_nowego_przedmiotu.setText("");
            Toast.makeText(getContext(), "Dodano nowy przedmiot", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(),"Taki przedmiot już istnieje w bazie", Toast.LENGTH_SHORT).show();
        }
    }


    //metoda aktualizująca liste przedmiotów w ListView
    public void aktualizuj_liste(){
        baza.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lista_przedmiotow_array_list = new ArrayList<>();
                for(DataSnapshot klasa: dataSnapshot.child("Przedmioty").getChildren()){
                    lista_przedmiotow_array_list.add(klasa.getKey());
                }

                ArrayAdapter adapter_lista_przedmiotow = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_dropdown_item, lista_przedmiotow_array_list);
                lista_przedmiotow_list_view.setAdapter(adapter_lista_przedmiotow);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }





}
