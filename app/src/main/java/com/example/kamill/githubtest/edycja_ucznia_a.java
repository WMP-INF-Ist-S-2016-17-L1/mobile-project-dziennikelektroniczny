package com.example.kamill.githubtest;

import android.content.Context;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class edycja_ucznia_a extends Fragment {

    private EditText imie;
    private EditText nazwisko;
    private EditText pesel;
    private Spinner spinner_uczniow;
    private Button zapisz_btn;
    private DatabaseReference baza;
    private ArrayList lista_uczniow;
    private ArrayList lista_UID_uczniow;
    private String wybrany_uczen_uid;



    public edycja_ucznia_a() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_edycja_ucznia_a, container, false);
        imie = v.findViewById(R.id.imie_ucznia_EditText);
        nazwisko = v.findViewById(R.id.nazwisko_ucznia_EditText);
        pesel = v.findViewById(R.id.pesel_ucznia_Edit_text);
        spinner_uczniow = v.findViewById(R.id.spinner_lista_uczniow);
        zapisz_btn = v.findViewById(R.id.zapisz_btn);
        baza = FirebaseDatabase.getInstance().getReference();

        //załadowanie wszystkich uczniów do spinnera
        dodaj_uczniow_do_spinnera();


        zapisz_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zapisz_dane();
            }
        });


        return v;
    }

    public void dodaj_uczniow_do_spinnera(){

        baza.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                lista_uczniow = new ArrayList();
                lista_UID_uczniow = new ArrayList();
                for(DataSnapshot uczen: dataSnapshot.child("Users").child("Uczen").getChildren()){
                    Uczen uczenn = uczen.getValue(Uczen.class);
                    String UID = uczen.getKey();
                    String imie = uczenn.imie;
                    String nazwisko = uczenn.nazwisko;
                    String pesel = uczenn.pesel;
                    lista_uczniow.add(imie+" "+nazwisko+"  | Pesel: "+pesel);
                    lista_UID_uczniow.add(UID);

                }
                ArrayAdapter adapter = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_dropdown_item,lista_uczniow);
                spinner_uczniow.setAdapter(adapter);
                spinner_uczniow.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        for(DataSnapshot uczen : dataSnapshot.child("Users").child("Uczen").getChildren()){
                            if(uczen.getKey() == lista_UID_uczniow.get(position)){
                                wybrany_uczen_uid = (String)lista_UID_uczniow.get(position);
                                Uczen uczenn = uczen.getValue(Uczen.class);
                                String imie_ucznia = uczenn.imie;
                                String nazwisko_ucznia = uczenn.nazwisko;
                                String pesel_ucznia = uczenn.pesel;

                                imie.setText(imie_ucznia);
                                nazwisko.setText(nazwisko_ucznia);
                                pesel.setText(pesel_ucznia);
                            }
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    //zapisywanie danych
    public void zapisz_dane(){
        baza.child("Users").child("Uczen").child(wybrany_uczen_uid).child("imie").setValue(imie.getText().toString());
        baza.child("Users").child("Uczen").child(wybrany_uczen_uid).child("nazwisko").setValue(nazwisko.getText().toString());
        baza.child("Users").child("Uczen").child(wybrany_uczen_uid).child("pesel").setValue(pesel.getText().toString());
        final int pozycja = spinner_uczniow.getSelectedItemPosition();
        dodaj_uczniow_do_spinnera();
        spinner_uczniow.setSelection(pozycja);
        Toast.makeText(getContext(),"Zapisano",Toast.LENGTH_SHORT).show();
}


}
