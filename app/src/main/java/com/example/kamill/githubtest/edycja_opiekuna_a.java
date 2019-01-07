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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class edycja_opiekuna_a extends Fragment {

    private Spinner spinner_opiekunow;
    private EditText imie_o;
    private EditText nazwisko_o;
    private EditText telefon_o;
    private Button zapisz_btn;
    private DatabaseReference baza;
    private ArrayList lista_opiekunow;
    private ArrayList lista_UID_opiekunow;
    private ArrayList lista_dzieci_bez_opiekuna;
    private ArrayList lista_UID_dzieci_bez_opiekuna;
    private ArrayList lista_dzieci_opiekuna;
    private ArrayList lista_UID_dzieci_opiekuna;
    private String UID;
    private ListView lista_podopiecznych_ListView;
    private Button dodaj_do_listy_btn;
    private Spinner spinner_uczniow_bez_opiekuna;
    private String wybrany_uczen;
    private String wybrany_uczen_UID;
    private TextView pole_pesel;
    private ArrayList lista_UID_usunietych;

    public edycja_opiekuna_a() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edycja_opiekuna_a, container, false);

        spinner_opiekunow = v.findViewById(R.id.spinner_opiekunow);
        imie_o = v.findViewById(R.id.imie_opiekuna_EditText);
        nazwisko_o = v.findViewById(R.id.nazwisko_opiekuna_EditText);
        telefon_o = v.findViewById(R.id.telefon_opiekuna_EditText);
        zapisz_btn = v.findViewById(R.id.zapisz_btn);
        lista_podopiecznych_ListView = v.findViewById(R.id.lista_uczniow_ListView);
        dodaj_do_listy_btn = v.findViewById(R.id.dodaj_ucznia_do_listy_btn);
        spinner_uczniow_bez_opiekuna = v.findViewById(R.id.spinner_uczniow);
        baza = FirebaseDatabase.getInstance().getReference();
        pole_pesel = v.findViewById(R.id.pesel_textView);
        lista_UID_usunietych = new ArrayList();


        aktualizuj_liste_opiekunow();

        stworz_spinner_z_dziecmi_bez_opiekuna();

        zapisz_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zapisz_zmiany();
            }
        });

        dodaj_do_listy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dodaj_dziecko_do_listy();
            }
        });

        return v;
    }

    //metoda ładująca aktualną liste opiekunów do spinnera
    public void aktualizuj_liste_opiekunow(){
        baza.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                     lista_opiekunow = new ArrayList();
                     lista_UID_opiekunow = new ArrayList();
                     for (DataSnapshot opiekun: dataSnapshot.child("Users").child("Opiekun").getChildren()){

                            String imie =  (String)opiekun.child("imie").getValue();
                            String nazwisko =  (String)opiekun.child("nazwisko").getValue();
                            String UID = opiekun.getKey();
                            lista_UID_opiekunow.add(UID);
                            lista_opiekunow.add(imie +" "+nazwisko);
                     }
                ArrayAdapter adapter = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_dropdown_item,lista_opiekunow);
                spinner_opiekunow.setAdapter(adapter);
                spinner_opiekunow.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        UID = (String)lista_UID_opiekunow.get(position);
                        String imie = (String)dataSnapshot.child("Users").child("Opiekun").child(UID).child("imie").getValue();
                        String nazwisko = (String)dataSnapshot.child("Users").child("Opiekun").child(UID).child("nazwisko").getValue();
                        String telefon = (String)dataSnapshot.child("Users").child("Opiekun").child(UID).child("nr_telefonu").getValue();
                        imie_o.setText(imie);
                        nazwisko_o.setText(nazwisko);
                        telefon_o.setText(telefon);
                        wyswietl_liste_uczniow();
                        lista_UID_usunietych.clear();
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



    //metoda zapisująca zmiany
    public void zapisz_zmiany(){
        String imie = imie_o.getText().toString();
        String nazwisko = nazwisko_o.getText().toString();
        String telefon = telefon_o.getText().toString();
        baza.child("Users").child("Opiekun").child(UID).child("imie").setValue(imie);
        baza.child("Users").child("Opiekun").child(UID).child("nazwisko").setValue(nazwisko);
        baza.child("Users").child("Opiekun").child(UID).child("nr_telefonu").setValue(telefon);
        if(lista_UID_dzieci_opiekuna.isEmpty()){
            baza.child("Users").child("Opiekun").child(UID).child("lista_dzieci").setValue("null");
            for(int i=0; i<lista_UID_usunietych.size();i++){
                baza.child("Users").child("Uczen").child(lista_UID_usunietych.get(i).toString()).child("opiekun").setValue("null");
            }
        }else {
            baza.child("Users").child("Opiekun").child(UID).child("lista_dzieci").setValue(lista_UID_dzieci_opiekuna);
            for(int i=0;i<lista_UID_dzieci_opiekuna.size();i++){
                baza.child("Users").child("Uczen").child(lista_UID_dzieci_opiekuna.get(i).toString()).child("opiekun").setValue(UID);
            }
            for(int i=0; i<lista_UID_usunietych.size();i++){
                baza.child("Users").child("Uczen").child(lista_UID_usunietych.get(i).toString()).child("opiekun").setValue("null");
            }

        }
        lista_UID_usunietych.clear();

        Toast.makeText(getContext(), "Zapisano zmiany", Toast.LENGTH_SHORT).show();
        wyswietl_liste_uczniow();
        stworz_spinner_z_dziecmi_bez_opiekuna();


    }

    //metoda wyświetlająca liste uczniów w ListView
    public void wyswietl_liste_uczniow() {
        baza.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lista_dzieci_opiekuna = new ArrayList();
                lista_UID_dzieci_opiekuna = new ArrayList();
                for (DataSnapshot UID_dzieci : dataSnapshot.child("Users").child("Opiekun").child(UID).child("lista_dzieci").getChildren()) {
                    String UID_dziecka = (String)UID_dzieci.getValue();
                    String imie = (String)dataSnapshot.child("Users").child("Uczen").child(UID_dziecka).child("imie").getValue();
                    String nazwisko = (String)dataSnapshot.child("Users").child("Uczen").child(UID_dziecka).child("nazwisko").getValue();
                    lista_dzieci_opiekuna.add(imie+" "+nazwisko);
                    lista_UID_dzieci_opiekuna.add(UID_dziecka);

                }
                if (lista_dzieci_opiekuna.isEmpty()) {
                    Toast.makeText(getContext(), "Ten opiekun nie posiada podopiecznych", Toast.LENGTH_LONG).show();
                }
                    ArrayAdapter adapter = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_dropdown_item, lista_dzieci_opiekuna);
                    lista_podopiecznych_ListView.setAdapter(adapter);
                    lista_podopiecznych_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String uczen = (String)lista_dzieci_opiekuna.get(position);
                            lista_dzieci_opiekuna.remove(lista_dzieci_opiekuna.get(position));
                            lista_UID_usunietych.add(lista_UID_dzieci_opiekuna.get(position));
                            lista_UID_dzieci_opiekuna.remove(lista_UID_dzieci_opiekuna.get(position));
                            ArrayAdapter adapter = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_dropdown_item, lista_dzieci_opiekuna);
                            lista_podopiecznych_ListView.setAdapter(adapter);
                            Toast.makeText(getContext(),"Usunięto '"+uczen+"' z listy",Toast.LENGTH_SHORT).show();
                        }
                    });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }


        });
    }

    //tworzenie spinnera z uczniami bez opiekuna
    public void stworz_spinner_z_dziecmi_bez_opiekuna(){
        baza.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lista_dzieci_bez_opiekuna = new ArrayList();
                lista_UID_dzieci_bez_opiekuna = new ArrayList();
                final ArrayList lista_pesel = new ArrayList();
                for(DataSnapshot uczen : dataSnapshot.child("Users").child("Uczen").getChildren()){
                    if(uczen.child("opiekun").getValue().equals("null")){
                        String imie = (String)uczen.child("imie").getValue();
                        String nazwisko = (String)uczen.child("nazwisko").getValue();
                        String pesel= (String)uczen.child("pesel").getValue();
                        lista_UID_dzieci_bez_opiekuna.add(uczen.getKey());
                        lista_dzieci_bez_opiekuna.add(imie+" "+nazwisko);
                        lista_pesel.add(pesel);
                    }
                }
                ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, lista_dzieci_bez_opiekuna);
                spinner_uczniow_bez_opiekuna.setAdapter(adapter);
                spinner_uczniow_bez_opiekuna.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        wybrany_uczen = (String)lista_dzieci_bez_opiekuna.get(position);
                        wybrany_uczen_UID = (String)lista_UID_dzieci_bez_opiekuna.get(position);
                        pole_pesel.setText("Pesel: "+lista_pesel.get(position).toString());
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

    //dodawanie ucznia do listy dzieci opiekuna
    public void dodaj_dziecko_do_listy(){
        if(lista_UID_dzieci_opiekuna.contains(wybrany_uczen_UID)){
            Toast.makeText(getContext(), "Na liście znajduje się już ten uczeń", Toast.LENGTH_SHORT).show();
        }else {
            lista_dzieci_opiekuna.add(wybrany_uczen);
            lista_UID_dzieci_opiekuna.add(wybrany_uczen_UID);
            Toast.makeText(getContext(), "Dodano ucznia do listy", Toast.LENGTH_SHORT).show();
            ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, lista_dzieci_opiekuna);
            lista_podopiecznych_ListView.setAdapter(adapter);
        }
    }

}

// dodać walidacje pesel w innych klasach
// dodać sprawdzanie na małą litere (toLowerCase)