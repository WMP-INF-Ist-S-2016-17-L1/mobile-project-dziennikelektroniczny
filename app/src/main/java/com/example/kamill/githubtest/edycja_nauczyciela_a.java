package com.example.kamill.githubtest;

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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class edycja_nauczyciela_a extends Fragment {

    private DatabaseReference baza;
    private Spinner spinner_nauczycieli;
    private Spinner spinner_przedmiotow;
    private Spinner spinner_klas;
    private EditText imie_nauczyciela;
    private EditText nazwisko_nauczyciela;
    private ListView lista_przedmiotow_ListView;
    private ListView lista_klas_ListView;
    private Button dodaj_przedmiot_do_listy;
    private Button dodaj_klase_do_listy;
    private Button zapisz_dane;
    private ArrayList lista_UID_nauczycieli;
    private ArrayList lista_nauczycieli;
    private ArrayList lista_przedmiotow_ArrayList;
    private ArrayList lista_klas_ArrayList;
    private ArrayList lista_przedmiotow_nauczyciela;
    private ArrayList lista_klas_nauczyciela;
    private String UID_wybranego_nauczyciela;
    private String wybrany_nauczyciel;
    private String wybrany_przedmiot;
    private String wybrana_klasa;


    public edycja_nauczyciela_a() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_edycja_nauczyciela_a, container, false);
        baza = FirebaseDatabase.getInstance().getReference();
        spinner_nauczycieli = v.findViewById(R.id.spinner_nauczycieli);
        spinner_przedmiotow = v.findViewById(R.id.spinner_lista_przedmiotow);
        spinner_klas = v.findViewById(R.id.spinner_lista_klas);
        imie_nauczyciela = v.findViewById(R.id.imie_nauczyciela_EditText);
        nazwisko_nauczyciela = v.findViewById(R.id.nazwisko_nauczyciela_EditText);
        lista_przedmiotow_ListView = v.findViewById(R.id.ListView_lista_przedmiotow);
        lista_klas_ListView = v.findViewById(R.id.LiesView_lista_klas);
        dodaj_przedmiot_do_listy = v.findViewById(R.id.dodaj_przedmiot_btn);
        dodaj_klase_do_listy = v.findViewById(R.id.dodaj_klase_btn);
        zapisz_dane = v.findViewById(R.id.zapisz_btn);

        //tworzenie spinnera z nauczycielami, przedmiotami i klasami
        stworz_spinner_z_nauczycielami();
        stworz_spinnera_z_klasami();
        stworz_spinnera_z_przedmiotami();

        //OnClickListener dla przycisku dodawania przedmiotów
        dodaj_przedmiot_do_listy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dodaj_przedmiot_do_listy();
            }
        });

        //OnClickListener dla przycisku dodawania klas
        dodaj_klase_do_listy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dodaj_klase_do_listy();
            }
        });

        //OnClickListener dla przycisku dodawania zapisywania danych
        zapisz_dane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zapisz_dane();
            }
        });

        return v;
    }

    // tworzenie spinnera z nauczycielami
    public void stworz_spinner_z_nauczycielami() {
        baza.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                lista_UID_nauczycieli = new ArrayList();
                lista_nauczycieli = new ArrayList();
                for (DataSnapshot nauczyciel : dataSnapshot.child("Users").child("Nauczyciel").getChildren()) {
                    lista_UID_nauczycieli.add(nauczyciel.getKey());
                    String imie = (String) nauczyciel.child("imie").getValue();
                    String nazwisko = (String) nauczyciel.child("nazwisko").getValue();
                    lista_nauczycieli.add(imie + " " + nazwisko);
                }
                stworz_adapter_dla_spinnera(lista_nauczycieli, spinner_nauczycieli);
                spinner_nauczycieli.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        UID_wybranego_nauczyciela = (String) lista_UID_nauczycieli.get(position);
                        wybrany_nauczyciel = (String) lista_nauczycieli.get(position);
                        wyswietl_klasy_wybranego_nayczyciela_w_listView();
                        wyswietl_przedmioty_wybranego_nayczyciela_w_listView();
                        String imie = (String)dataSnapshot.child("Users").child("Nauczyciel").child(UID_wybranego_nauczyciela).child("imie").getValue();
                        String nazwisko = (String)dataSnapshot.child("Users").child("Nauczyciel").child(UID_wybranego_nauczyciela).child("nazwisko").getValue();
                        imie_nauczyciela.setText(imie);
                        nazwisko_nauczyciela.setText(nazwisko);
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

    //tworzenie adaptera dla spinnera
    public void stworz_adapter_dla_spinnera(ArrayList lista, Spinner spinner) {
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_checked, lista);
        spinner.setAdapter(adapter);
    }

    //tworzenie spinnera z przedmiotami
    public void stworz_spinnera_z_przedmiotami(){
        baza.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lista_przedmiotow_ArrayList = new ArrayList();
                for(DataSnapshot przedmiot: dataSnapshot.child("Przedmioty").getChildren()){
                    lista_przedmiotow_ArrayList.add(przedmiot.getKey());
                }
                stworz_adapter_dla_spinnera(lista_przedmiotow_ArrayList, spinner_przedmiotow);
                spinner_przedmiotow.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        wybrany_przedmiot = (String)lista_przedmiotow_ArrayList.get(position);
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

    //tworzenie spinnera z klasami
    public void stworz_spinnera_z_klasami(){
        baza.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lista_klas_ArrayList = new ArrayList();
                for(DataSnapshot klasa: dataSnapshot.child("Klasy").getChildren()){
                    lista_klas_ArrayList.add(klasa.getKey());
                }
                stworz_adapter_dla_spinnera(lista_klas_ArrayList, spinner_klas);
                spinner_klas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        wybrana_klasa = (String) lista_klas_ArrayList.get(position);
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

    //wyświetlanie listy przedmiotów wybranego nauczyciela
    public void wyswietl_przedmioty_wybranego_nayczyciela_w_listView(){
        baza.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lista_przedmiotow_nauczyciela = new ArrayList();
                for(DataSnapshot przedmiot: dataSnapshot.child("Users").child("Nauczyciel").child(UID_wybranego_nauczyciela).child("lista_przedmiotów").getChildren()){
                    lista_przedmiotow_nauczyciela.add(przedmiot.getValue());
                }
                stworz_adapter_dla_listView(lista_przedmiotow_nauczyciela, lista_przedmiotow_ListView);
                lista_przedmiotow_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(getContext(), "Usunięto '"+lista_przedmiotow_nauczyciela.get(position).toString()+"' z listy", Toast.LENGTH_SHORT).show();
                        lista_przedmiotow_nauczyciela.remove(position);
                        stworz_adapter_dla_listView(lista_przedmiotow_nauczyciela, lista_przedmiotow_ListView);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //wyświetlanie listy klas wybranego nauczyciela
    public void wyswietl_klasy_wybranego_nayczyciela_w_listView(){
        baza.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lista_klas_nauczyciela = new ArrayList();
                for(DataSnapshot klasa: dataSnapshot.child("Users").child("Nauczyciel").child(UID_wybranego_nauczyciela).child("lista_klas").getChildren()){
                    lista_klas_nauczyciela.add(klasa.getValue());
                }
                stworz_adapter_dla_listView(lista_klas_nauczyciela, lista_klas_ListView);
                lista_klas_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(getContext(), "Usunięto '"+lista_klas_nauczyciela.get(position).toString()+"' z listy", Toast.LENGTH_SHORT).show();
                        lista_klas_nauczyciela.remove(position);
                        stworz_adapter_dla_listView(lista_klas_nauczyciela, lista_klas_ListView);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    //tworzenie adaptera dla ListView
    public void stworz_adapter_dla_listView(ArrayList lista, ListView listaview){
        ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.preference_category, lista);
        listaview.setAdapter(adapter);
    }


    //dodawanie przedmiotu do listy
    public void dodaj_przedmiot_do_listy(){
        if(!lista_przedmiotow_nauczyciela.contains(wybrany_przedmiot)) {
            lista_przedmiotow_nauczyciela.add(wybrany_przedmiot);
            stworz_adapter_dla_listView(lista_przedmiotow_nauczyciela, lista_przedmiotow_ListView);
        }else {
            Toast.makeText(getContext(), "'"+wybrany_przedmiot+"' już jest na liście", Toast.LENGTH_SHORT).show();
        }
    }


    //dodawanie klasy do listy
    public void dodaj_klase_do_listy(){
        if(!lista_klas_nauczyciela.contains(wybrana_klasa)) {
            lista_klas_nauczyciela.add(wybrana_klasa);
            stworz_adapter_dla_listView(lista_klas_nauczyciela, lista_klas_ListView);
        }else {
            Toast.makeText(getContext(), "'"+wybrana_klasa+"' już jest na liście", Toast.LENGTH_SHORT).show();
        }
    }

    //zapisz dane nauczyciela w bazie danych
    public void zapisz_dane(){
        String imie = imie_nauczyciela.getText().toString();
        String nazwisko = nazwisko_nauczyciela.getText().toString();

        if(TextUtils.isEmpty(imie)){
            Toast.makeText(getContext(), "Wpisz imię", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(nazwisko)){
            Toast.makeText(getContext(), "Wpisz nazwisko", Toast.LENGTH_SHORT).show();
        }else if (lista_przedmiotow_nauczyciela.isEmpty()){
            Toast.makeText(getContext(), "Lista przedmiotów jest pusta", Toast.LENGTH_SHORT).show();
        }else if(lista_klas_nauczyciela.isEmpty()){
            Toast.makeText(getContext(), "Lista klas jest pusta", Toast.LENGTH_SHORT).show();
        }else {
            baza.child("Users").child("Nauczyciel").child(UID_wybranego_nauczyciela).child("imie").setValue(imie);
            baza.child("Users").child("Nauczyciel").child(UID_wybranego_nauczyciela).child("nazwisko").setValue(nazwisko);
            baza.child("Users").child("Nauczyciel").child(UID_wybranego_nauczyciela).child("lista_przedmiotów").setValue(lista_przedmiotow_nauczyciela);
            baza.child("Users").child("Nauczyciel").child(UID_wybranego_nauczyciela).child("lista_klas").setValue(lista_klas_nauczyciela);
            Toast.makeText(getContext(),"Zapisano zmiany", Toast.LENGTH_SHORT).show();
        }
    }




}







