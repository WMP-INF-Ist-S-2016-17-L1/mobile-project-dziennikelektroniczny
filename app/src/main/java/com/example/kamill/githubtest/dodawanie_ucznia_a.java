package com.example.kamill.githubtest;


import android.app.ProgressDialog;
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
import android.widget.LinearLayout;
import android.widget.NumberPicker;
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
import java.util.List;


public class dodawanie_ucznia_a extends Fragment {

    private DatabaseReference baza;
    private FirebaseAuth firebaseAuth;
    private Button btn;
    private EditText login;
    private EditText imie;
    private EditText nazwisko;
    private EditText pesel;
    private Spinner spinner;
    private String klasa;





    public dodawanie_ucznia_a() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        baza = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        View v = inflater.inflate(R.layout.fragment_dodawanie_ucznia_a, container, false);
        spinner = v.findViewById(R.id.spinner);
        login = v.findViewById(R.id.login_ucznia);
        imie = v.findViewById(R.id.imie_ucznia);
        nazwisko = v.findViewById(R.id.nazwisko_ucznia);



        pesel = v.findViewById(R.id.pesel_ucznia);
        btn = v.findViewById(R.id.dodaj_ucznia_btn);
        // onClickListener dla przycisku
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dodaj_ucznia_a();
            }
        });



        // wyszukanie wszystkich klas w bazie i dodanie ich do listy
        baza.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final ArrayList<String> lista_klas = new ArrayList<>();
                for (DataSnapshot klasa : dataSnapshot.child("Klasy").getChildren()) {
                    lista_klas.add(klasa.getKey());
                }
                //tworzenie spinnera
                ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_item, lista_klas);
                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        for (int i = 1; i <= lista_klas.size(); i++) {
                            klasa = lista_klas.get(position);
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

        return v;
        }

        public void dodaj_ucznia_a(){
        final String login_ucznia = login.getText().toString();
        final String imie_ucznia = imie.getText().toString();
        final String nazwisko_ucznia = nazwisko.getText().toString();
        final String pesel_ucznia = pesel.getText().toString();
        String haslo = randomString(10);
        final String login_admina = firebaseAuth.getCurrentUser().getEmail();

        if(TextUtils.isEmpty(login_ucznia)){
            Toast.makeText(getContext(),"Wpisz e-mail ucznia",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(imie_ucznia)){
            Toast.makeText(getContext(),"Wpisz imię ucznia", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(nazwisko_ucznia)){
            Toast.makeText(getContext(),"Wpisz nazwisko ucznia", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(pesel_ucznia)){
            Toast.makeText(getContext(),"Wpisz PESEL ucznia",Toast.LENGTH_SHORT).show();
        }else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(login_ucznia).matches()){
            Toast.makeText(getContext(),"Niepoprawny format e-mail ucznia", Toast.LENGTH_SHORT).show();
        }else if(pesel_ucznia.length() != 11){
            Toast.makeText(getContext(), "PESEL musi zawierać 11 cyfr", Toast.LENGTH_SHORT).show();
        }else if(!sprawdz_pesel(pesel_ucznia)){
            Toast.makeText(getContext(), "PESEL niepoprawny", Toast.LENGTH_SHORT).show();
        }else{
            firebaseAuth.createUserWithEmailAndPassword(login_ucznia, haslo)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                Uczen uczen = new Uczen(login_ucznia, imie_ucznia, nazwisko_ucznia, pesel_ucznia, klasa, "null");
                                baza.child("Users").child("Uczen").child(firebaseAuth.getCurrentUser().getUid()).setValue(uczen);
                                baza.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        for (DataSnapshot przedmiot : dataSnapshot.child("Klasy").child(klasa).child("Przedmioty").getChildren()) {
                                            baza.child("Klasy").child(klasa).child("Uczniowie").child(firebaseAuth.getCurrentUser().getUid()).child("Oceny").child(przedmiot.getKey()).setValue("null");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                firebaseAuth.sendPasswordResetEmail(login_ucznia);
                                firebaseAuth.signInWithEmailAndPassword(login_admina, "admin123");
                                Toast.makeText(getContext(), "Dodano ucznia do bazy", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(), "Nie udało się dodać do bazy", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

            login.setText("");
            imie.setText("");
            nazwisko.setText("");
            pesel.setText("");
            }
        }

        //metoda sprawdzająca poprawność numeru pesel
    public boolean sprawdz_pesel(String pesel){
        int first = pesel.charAt(0) - '0';
        int second = pesel.charAt(1) - '0';
        int third= pesel.charAt(2) - '0';
        int fourth= pesel.charAt(3) - '0';
        int fifth= pesel.charAt(4) - '0';
        int sixth= pesel.charAt(5) - '0';
        int seventh= pesel.charAt(6) - '0';
        int eight= pesel.charAt(7) - '0';
        int nineth= pesel.charAt(8) - '0';
        int tenth= pesel.charAt(9) - '0';
        int eleventh= pesel.charAt(10) - '0';

        int suma = 9*first + 7*second + 3*third + fourth + 9*fifth + 7*sixth
                + 3*seventh + eight + 9*nineth + 7*tenth;
        int reszta = suma%10;
        if(reszta == eleventh){
            return true;
        }else{
            return false;
        }



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
