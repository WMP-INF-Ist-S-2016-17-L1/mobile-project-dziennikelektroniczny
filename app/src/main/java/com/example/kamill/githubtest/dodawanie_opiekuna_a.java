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
import android.widget.LinearLayout;
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
import java.util.List;


public class dodawanie_opiekuna_a extends Fragment {

    private EditText login;
    private EditText imie;
    private EditText nazwisko;
    private EditText telefon;
    private Spinner spinner_lista_uczniow;
    private ListView lista_uczniow_ListView;
    private ArrayList lista_uczniow_w_spinnerze;
    private ArrayList lista_UID_uczniow_w_spinnerze;
    private ArrayList lista_uczniow_w_ListView;
    private ArrayList lista_UID_uczniow_w_ListView;
    private DatabaseReference baza;
    private FirebaseAuth firebaseAuth;
    private Button dodaj_do_ListView_btn;
    private Button dodaj_opiekuna__btn;
    private String wybrany_uczen;
    private String wybrany_uczen_UID;



    public dodawanie_opiekuna_a() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_dodawanie_opiekuna_a, container, false);

        login = v.findViewById(R.id.login_opiekuna);
        spinner_lista_uczniow = v.findViewById(R.id.spinner_lista_uczniow);
        lista_uczniow_ListView = v.findViewById(R.id.lista_uczniow_ListView);
        dodaj_do_ListView_btn = v.findViewById(R.id.dodaj_ucznia_do_listy_btn);
        dodaj_opiekuna__btn = v.findViewById(R.id.dodaj_opiekuna_btn);
        imie = v.findViewById(R.id.imie_opiekuna);
        nazwisko = v.findViewById(R.id.nazwisko_opiekuna);
        telefon = v.findViewById(R.id.telefon_opiekuna);
        baza = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        lista_uczniow_w_ListView = new ArrayList();
        lista_UID_uczniow_w_ListView = new ArrayList();


        //tworzenie spinnera z uczniami
        wrzuc_uczniow_do_spinnera();

        dodaj_opiekuna__btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dodaj_opiekuna(lista_UID_uczniow_w_ListView);
            }
        });

        dodaj_do_ListView_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dodaj_ucznia_do_ListView();
            }
        });

        return v;
    }



    public void dodaj_opiekuna(final ArrayList lista){
        final String login_opiekuna = login.getText().toString();
        final String imie_opiekuna = imie.getText().toString();
        final String nazwisko_opiekuna = nazwisko.getText().toString();
        final String telefon_opiekuna = telefon.getText().toString();
        final String haslo_opiekuna = randomString(10);
        final String login_admina = firebaseAuth.getCurrentUser().getEmail();

        if(TextUtils.isEmpty(login_opiekuna)){
            Toast.makeText(getContext(),"Wpisz e-mail opiekuna", Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(imie_opiekuna)){
            Toast.makeText(getContext(),"Wpisz imię opiekuna", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(nazwisko_opiekuna)){
            Toast.makeText(getContext(),"Wpisz nazwisko opiekuna", Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(telefon_opiekuna)){
            Toast.makeText(getContext(),"Wpisz telefon do opiekuna", Toast.LENGTH_SHORT).show();
        }else if(lista_uczniow_w_ListView.isEmpty()){
            Toast.makeText(getContext(),"Lista uczniów jest pusta",Toast.LENGTH_SHORT).show();
        }else if(!android.util.Patterns.EMAIL_ADDRESS.matcher(login_opiekuna).matches()){
            Toast.makeText(getContext(),"Niepoprawny format e-mail opiekuna",Toast.LENGTH_SHORT).show();
        }else {
            firebaseAuth.createUserWithEmailAndPassword(login_opiekuna, haslo_opiekuna)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Opiekun opiekun = new Opiekun(login_opiekuna, imie_opiekuna, nazwisko_opiekuna, telefon_opiekuna, lista);
                            if (task.isSuccessful()) {

                                baza.child("Users").child("Opiekun").child(firebaseAuth.getCurrentUser().getUid()).setValue(opiekun);
                                for(int i = 0;i<lista_UID_uczniow_w_ListView.size();i++){
                                    baza.child("Users").child("Uczen").child((String)lista_UID_uczniow_w_ListView.get(i)).child("opiekun").setValue(firebaseAuth.getCurrentUser().getUid());
                                }
                                firebaseAuth.sendPasswordResetEmail(login_opiekuna);
                                firebaseAuth.signInWithEmailAndPassword(login_admina, "admin123");
                                Toast.makeText(getContext(), "Dodano opiekuna", Toast.LENGTH_SHORT).show();
                                ArrayList pusta_lista = new ArrayList();
                                ArrayAdapter adapter = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_dropdown_item, pusta_lista);
                                lista_uczniow_ListView.setAdapter(adapter);
                                wrzuc_uczniow_do_spinnera();
                            } else {
                                Toast.makeText(getContext(), "Nie udało się dodać opiekuna", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            login.setText("");
            imie.setText("");
            nazwisko.setText("");
            telefon.setText("");

        }
    }

    //metoda wrzucająca listę uczniow bez opiekuna do spinnera

    public void wrzuc_uczniow_do_spinnera(){
        baza.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lista_uczniow_w_spinnerze = new ArrayList();
                lista_UID_uczniow_w_spinnerze = new ArrayList();
                for(DataSnapshot uczen: dataSnapshot.child("Users").child("Uczen").getChildren()){
                    if(uczen.child("opiekun").getValue().equals("null")){
                        lista_UID_uczniow_w_spinnerze.add(uczen.getKey());
                        lista_uczniow_w_spinnerze.add(uczen.child("imie").getValue().toString()+" "+uczen.child("nazwisko").getValue().toString());
                    }
                }
                if(lista_uczniow_w_spinnerze.isEmpty()){
                    lista_uczniow_w_spinnerze.add("Brak wyników");
                    ArrayAdapter adapter = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_dropdown_item, lista_uczniow_w_spinnerze);
                    spinner_lista_uczniow.setAdapter(adapter);
                }else {
                    ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, lista_uczniow_w_spinnerze);
                    spinner_lista_uczniow.setAdapter(adapter);
                    spinner_lista_uczniow.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            wybrany_uczen = (String)lista_uczniow_w_spinnerze.get(position);
                            wybrany_uczen_UID = (String)lista_UID_uczniow_w_spinnerze.get(position);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //metoda dodająca ucznia do ListView
    public void dodaj_ucznia_do_ListView(){

        if(lista_uczniow_w_ListView.contains(wybrany_uczen)){
            Toast.makeText(getContext(),"Uczeń już jest na liście",Toast.LENGTH_SHORT).show();
        }else {
            lista_uczniow_w_ListView.add(wybrany_uczen);
            lista_UID_uczniow_w_ListView.add(wybrany_uczen_UID);
            ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item,lista_uczniow_w_ListView);
            lista_uczniow_ListView.setAdapter(adapter);
            lista_uczniow_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String txt = ((TextView)view).getText().toString();
                    lista_uczniow_w_ListView.remove(txt);
                    ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, lista_uczniow_w_ListView);
                    lista_uczniow_ListView.setAdapter(adapter);
                    Toast.makeText(getContext(),"Usunięto '"+txt+"' z listy",Toast.LENGTH_SHORT).show();
                }
            });
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
