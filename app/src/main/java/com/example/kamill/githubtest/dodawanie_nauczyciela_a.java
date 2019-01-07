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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class dodawanie_nauczyciela_a extends Fragment {

    private EditText login_nauczyciela;
    private EditText imie_nauczyciela;
    private EditText nazwisko_nauczyciela;
    private Spinner spinner_przedmioty;
    private Spinner spinner_klasy;
    private Button dodaj_nauczyciela_btn;
    private Button dodaj_przedmiot_do_listy_btn;
    private Button dodaj_klase_do_listy_btn;
    private ListView lista_przedmiotow;
    private ArrayList array_lista_przedmiotow;
    private ArrayList array_lista_klas;
    private ListView lista_klas;
    private DatabaseReference baza;
    private FirebaseAuth firebaseAuth;
    private String przedmiot;
    private String klasa;
    private ArrayList<String> lista_przedmiotow_do_listy;
    private ArrayList<String> lista_klas_do_listy;



    public dodawanie_nauczyciela_a() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_dodawanie_nauczyciela_a, container, false);
        login_nauczyciela = v.findViewById(R.id.login_nauczyciela);
        imie_nauczyciela = v.findViewById(R.id.imie_nauczyciela);
        nazwisko_nauczyciela = v.findViewById(R.id.nazwisko_nauczyciela);
        spinner_przedmioty = v.findViewById(R.id.spinner_przedmiot);
        spinner_klasy = v.findViewById(R.id.spinner_klasa);
        baza = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        lista_przedmiotow = v.findViewById(R.id.lista_przedmiotow);
        lista_klas = v.findViewById(R.id.lista_klas);
        dodaj_przedmiot_do_listy_btn = v.findViewById(R.id.dodaj_przedmiot_do_listy_btn);
        dodaj_klase_do_listy_btn = v.findViewById(R.id.dodaj_klase_do_listy_btn);
        dodaj_nauczyciela_btn = v.findViewById(R.id.dodaj_nauczyciela_btn);
        lista_klas_do_listy = new ArrayList<>();
        lista_przedmiotow_do_listy = new ArrayList<>();

        //tworzenie listy przedmiotów
        baza.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 array_lista_przedmiotow = new ArrayList<String>();
                 for(DataSnapshot przedmiot: dataSnapshot.child("Przedmioty").getChildren()){
                     array_lista_przedmiotow.add(przedmiot.getKey());
                 }
                //tworzenie spinnera z listą przedmiotów
                ArrayAdapter adapter_lista_przedmiotow_spinner = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item, array_lista_przedmiotow);
                spinner_przedmioty.setAdapter(adapter_lista_przedmiotow_spinner);
                spinner_przedmioty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        for (int i = 1; i <= array_lista_przedmiotow.size(); i++) {
                            przedmiot = (String) array_lista_przedmiotow.get(position);
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

        //tworzenie listy klas
        baza.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                array_lista_klas = new ArrayList<String>();
                for(DataSnapshot klasa: dataSnapshot.child("Klasy").getChildren()){
                    array_lista_klas.add(klasa.getKey());
                }
                //tworzenie spinnera z listą przedmiotów
                ArrayAdapter adapter_lista_klas_spinner = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item, array_lista_klas);
                spinner_klasy.setAdapter(adapter_lista_klas_spinner);
                spinner_klasy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        for (int i = 1; i <= array_lista_klas.size(); i++) {
                            klasa = (String) array_lista_klas.get(position);
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

        dodaj_przedmiot_do_listy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dodaj_przedmiot_do_listy();
            }
        });

        dodaj_klase_do_listy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dodaj_klase_do_listy();
            }
        });

        dodaj_nauczyciela_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dodaj_nauczyciela();
            }
        });

        lista_klas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String txt = ((TextView)view).getText().toString();
                lista_klas_do_listy.remove(txt);
                ArrayAdapter adapter_listy_klas = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1,lista_klas_do_listy);
                lista_klas.setAdapter(adapter_listy_klas);
                Toast.makeText(getContext(),"Usunięto '"+txt+"' z listy",Toast.LENGTH_SHORT).show();
            }
        });

        lista_przedmiotow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String txt = ((TextView)view).getText().toString();
                lista_przedmiotow_do_listy.remove(txt);
                ArrayAdapter adapter_listy_przedmiotow = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, lista_przedmiotow_do_listy);
                lista_przedmiotow.setAdapter(adapter_listy_przedmiotow);
                Toast.makeText(getContext(),"Usunięto '"+txt+"' z listy",Toast.LENGTH_SHORT).show();
            }
        });





        return v;
    }

    //metoda tworząca nowego nauczyciela
    public void dodaj_nauczyciela() {

        final String login = login_nauczyciela.getText().toString();
        final String imie = imie_nauczyciela.getText().toString();
        final String nazwisko = nazwisko_nauczyciela.getText().toString();
        final String haslo = randomString(10);
        final String login_admina = firebaseAuth.getCurrentUser().getEmail();

        if (TextUtils.isEmpty(login)) {
            Toast.makeText(getContext(), "Wpisz login", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(imie)) {
            Toast.makeText(getContext(), "Wpisz imię", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(nazwisko)) {
            Toast.makeText(getContext(), "Wpisz nazwisko", Toast.LENGTH_SHORT).show();
        } else if (lista_przedmiotow_do_listy.isEmpty()) {
            Toast.makeText(getContext(), "Lista przedmiotów jest pusta", Toast.LENGTH_SHORT).show();
        } else if (lista_klas_do_listy.isEmpty()) {
            Toast.makeText(getContext(), "Lista klas jest pusta", Toast.LENGTH_SHORT).show();
        } else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(login).matches()){
            Toast.makeText(getContext(),"Nieprawidłowy format e-mail", Toast.LENGTH_SHORT).show();
        }else {

            firebaseAuth.createUserWithEmailAndPassword(login, haslo)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Nauczyciel nauczyciel = new Nauczyciel(login, imie, nazwisko, lista_przedmiotow_do_listy, lista_klas_do_listy);
                                baza.child("Users").child("Nauczyciel").child(firebaseAuth.getCurrentUser().getUid()).setValue(nauczyciel);
                                firebaseAuth.sendPasswordResetEmail(login);
                                firebaseAuth.signInWithEmailAndPassword(login_admina, "admin123");
                                Toast.makeText(getContext(), "Dodano nauczyciela", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Nie udało się dodać nauczyciela", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            login_nauczyciela.setText("");
            imie_nauczyciela.setText("");
            nazwisko_nauczyciela.setText("");
        }






    }
    //metoda dodająca przedmioty do listy
    public void dodaj_przedmiot_do_listy() {

            if (!lista_przedmiotow_do_listy.contains(przedmiot)) {
                lista_przedmiotow_do_listy.add(przedmiot);

            } else {
                Toast.makeText(getContext(), "'" + przedmiot + "' już znajduje się na liście", Toast.LENGTH_SHORT).show();
            }
            ArrayAdapter adapter_listy_przedmiotow = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, lista_przedmiotow_do_listy);
            lista_przedmiotow.setAdapter(adapter_listy_przedmiotow);


        }


    //metoda dodająca klase do listy
    public void dodaj_klase_do_listy(){

        if(!lista_klas_do_listy.contains(klasa)){
            lista_klas_do_listy.add(klasa);

        }else{
            Toast.makeText(getContext(), "'" +klasa+"' już znajduje się na liście",Toast.LENGTH_SHORT).show();
        }
        ArrayAdapter adapter_listy_klas = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item,lista_klas_do_listy);
        lista_klas.setAdapter(adapter_listy_klas);
    }

    // metoda generująca losowe hasło
    public String randomString(int len)
    {
        char[] str = new char[100];

        for (int i = 0; i < len; i++)
        {
            str[i] = (char) (((int)(Math.random() * 26)) + (int)'A');
        }

        return (new String(str, 0, len));
    }


}
