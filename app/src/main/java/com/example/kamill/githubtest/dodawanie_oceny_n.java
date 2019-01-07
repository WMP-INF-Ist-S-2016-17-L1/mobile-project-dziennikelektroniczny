package com.example.kamill.githubtest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.kamill.githubtest.R.color.Red;
import static com.example.kamill.githubtest.R.color.colorPrimaryDark;

public class dodawanie_oceny_n extends Fragment {

    private DatabaseReference baza;
    private Spinner spinner_klas;
    private Spinner spinner_przedmiotow;
    private Spinner spinner_uczniow;
    private Spinner spinner_za_co;
    private FirebaseAuth firebaseAuth;
    private String wybrana_klasa;
    private String wybrany_przedmiot;
    private String wybrany_uczen;
    private String wybrane_za_co;
    private Button dodaj_ocene_btn;
    private EditText ocena;
    private String id;


    public dodawanie_oceny_n() {
        // Required empty public constructor
    }


    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dodawanie_oceny_n, container, false);

        baza = FirebaseDatabase.getInstance().getReference();
        spinner_klas = v.findViewById(R.id.spinner_klasy);
        spinner_przedmiotow = v.findViewById(R.id.spinner_przedmioty);
        spinner_uczniow = v.findViewById(R.id.spinner_uczniowie);
        spinner_za_co = v.findViewById(R.id.spinner_zaco2);
        firebaseAuth = FirebaseAuth.getInstance();
        dodaj_ocene_btn = v.findViewById(R.id.button5);
        ocena = v.findViewById(R.id.editText2);


        stworz_spinner_klas();
        stworz_spinnera_z();

        dodaj_ocene_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dodaj_ocene();
            }
        });

        return v;
    }

    //tworzenie spinnera z klasami nauczyciela
    public void stworz_spinner_klas(){
        baza.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final ArrayList lista_klas = new ArrayList();
                for(DataSnapshot klasa: dataSnapshot.child("Users").child("Nauczyciel").child(firebaseAuth.getCurrentUser().getUid()).child("lista_klas").getChildren()){
                        lista_klas.add(klasa.getValue());
                }
                ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_activated_1, lista_klas);
                spinner_klas.setAdapter(adapter);
                spinner_klas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        wybrana_klasa = (String)lista_klas.get(position);
                        stworz_spinner_przedmiotow();
                        stworz_spinner_uczniow();
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

    //Tworzenie spinnera z przedmiotami
    public void stworz_spinner_przedmiotow(){
        baza.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                final ArrayList lista_przedmiotow = new ArrayList();
                for(DataSnapshot przedmiot: dataSnapshot.child("Klasy").child(wybrana_klasa).child("Przedmioty").getChildren()){
                    lista_przedmiotow.add(przedmiot.getValue());
                }
                ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_activated_1, lista_przedmiotow);
                spinner_przedmiotow.setAdapter(adapter);
                spinner_przedmiotow.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        wybrany_przedmiot = (String)lista_przedmiotow.get(position);
                        ArrayList lista_przedmiotow_nauczyciela = new ArrayList();
                        for(DataSnapshot przedmiot: dataSnapshot.child("Users").child("Nauczyciel").child(firebaseAuth.getCurrentUser().getUid()).child("lista_przedmiotów").getChildren()){
                            lista_przedmiotow_nauczyciela.add(przedmiot.getValue());
                        }
                        if(!lista_przedmiotow_nauczyciela.contains(wybrany_przedmiot)){
                            dodaj_ocene_btn.setEnabled(false);
                            dodaj_ocene_btn.setBackgroundColor(Color.LTGRAY);
                            dodaj_ocene_btn.setTextColor(Color.WHITE);

                        }else{
                            dodaj_ocene_btn.setEnabled(true);
                            dodaj_ocene_btn.setBackgroundColor(Color.RED);
                            dodaj_ocene_btn.setTextColor(Color.WHITE);

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

    //Tworzenie spinnera z uczniami
    public void stworz_spinner_uczniow(){
        baza.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final ArrayList lista_uczniow_UID = new ArrayList();
                final ArrayList lista_uczniow = new ArrayList();
                for(DataSnapshot uczen: dataSnapshot.child("Klasy").child(wybrana_klasa).child("Uczniowie").getChildren()){
                    lista_uczniow_UID.add(uczen.getKey());
                    String imie = (String)dataSnapshot.child("Users").child("Uczen").child(uczen.getKey()).child("imie").getValue();
                    String nazwisko = (String)dataSnapshot.child("Users").child("Uczen").child(uczen.getKey()).child("nazwisko").getValue();
                    String pesel = (String)dataSnapshot.child("Users").child("Uczen").child(uczen.getKey()).child("pesel").getValue();
                    lista_uczniow.add(imie + " " + nazwisko);
                }
                ArrayAdapter adapter = new ArrayAdapter(getContext(),android.R.layout.simple_list_item_activated_1, lista_uczniow);
                spinner_uczniow.setAdapter(adapter);
                spinner_uczniow.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        wybrany_uczen = (String)lista_uczniow_UID.get(position);
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

    //tworzenie spinnera z (sprawdzian, kartkówka, odpowiedź)
    public void stworz_spinnera_z(){
        final ArrayList lista = new ArrayList();
        lista.add("Sprawdzian");
        lista.add("Kartkówka");
        lista.add("Odpowiedź");
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_activated_1, lista);
        spinner_za_co.setAdapter(adapter);
        spinner_za_co.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                wybrane_za_co = (String)lista.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    //dodawanie oceny
    public void dodaj_ocene(){
       final String wpisana_ocena = ocena.getText().toString();
       if (Integer.parseInt(ocena.getText().toString()) < -6 || Integer.parseInt(ocena.getText().toString()) > 6){
           Toast.makeText(getContext(),"Ocena jest niepoprawna", Toast.LENGTH_SHORT).show();
       }
       else if(TextUtils.isEmpty(wybrany_przedmiot) || TextUtils.isEmpty(wybrany_uczen) || TextUtils.isEmpty(wybrana_klasa) || TextUtils.isEmpty(ocena.getText())){
           Toast.makeText(getContext(),"Jedno z pól jest puste", Toast.LENGTH_SHORT).show();
       }else {
           baza.addListenerForSingleValueEvent(new ValueEventListener() {
               @Override
               public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                   for (DataSnapshot idd : dataSnapshot.child("Klasy").child(wybrana_klasa).child("Uczniowie").child(wybrany_uczen).child("Oceny").child(wybrany_przedmiot).child(wybrane_za_co).getChildren()) {
                       id = idd.getKey();
                   }
                   if (TextUtils.isEmpty(id)) {
                       id = "1";
                   } else {
                       id = String.valueOf(Integer.parseInt(id) + 1);
                   }
                   baza.child("Klasy").child(wybrana_klasa).child("Uczniowie").child(wybrany_uczen).child("Oceny").child(wybrany_przedmiot).child(wybrane_za_co).child(id).setValue(wpisana_ocena);
                   Toast.makeText(getContext(),"Dodano ocenę", Toast.LENGTH_SHORT).show();
                   ocena.setText("");
               }

               @Override
               public void onCancelled(@NonNull DatabaseError databaseError) {
               }
           });
       }

    }




}
