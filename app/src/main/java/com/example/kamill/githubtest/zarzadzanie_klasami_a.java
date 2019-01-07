package com.example.kamill.githubtest;


import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class zarzadzanie_klasami_a extends Fragment {

    //zmienne
    private DatabaseReference baza;
    private EditText nazwa_nowej_klasy;
    private Button dodaj_nowa_klase_btn;
    private Spinner spinner_wyboru_klasy;
    private ArrayList lista_klas;
    private ArrayList lista_przedmiotow;
    private ArrayList lista_przedmiotow_dla_list_view;
    private ArrayList lista_uczniow;
    private ArrayList lista_uczniow_UID;
    private RadioButton dodawanie_przedmiotu_radio_button;
    private Spinner spinner_wybieranie_przedmiotu;
    private Button dodawanie_przedmiotu_btn;
    private String przedmiot;
    private ListView lista_przemiotow_ListView;
    private String wybrana_klasa;
    private TextView lista_przedmiotow_dla;
    private TextView wybierz_przedmiot_textView;
    private ListView lista_uczniow_ListView;
    private Spinner spinner_uczniow_bez_klasy;
    private Button dodaj_ucznia_btn;
    private TextView pesel_textView;
    private ArrayList lista_uczniow_bez_klasy;
    private ArrayList lista_UID_uczniow_bez_klasy;
    private ArrayList lista_pesel_uczniow_bez_klasy;
    private String uczen_bez_klasy;
    private TextView text_nad_pinnerem;


    public zarzadzanie_klasami_a() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_zarzadzanie_klasami_a, container, false);
        baza = FirebaseDatabase.getInstance().getReference();
        nazwa_nowej_klasy = v.findViewById(R.id.nazwa_nowej_klasy_edit_text);
        dodaj_nowa_klase_btn = v.findViewById(R.id.dodaj_klase_btn);
        spinner_wyboru_klasy = v.findViewById(R.id.wybor_klasy_spinner);
        spinner_wybieranie_przedmiotu = v.findViewById(R.id.wybor_przedmiotu_spinner);
        lista_przemiotow_ListView = v.findViewById(R.id.lista_przedmiotow_list_view);
        dodawanie_przedmiotu_btn = v.findViewById(R.id.dodaj_przedmiot_btn);
        dodawanie_przedmiotu_radio_button = v.findViewById(R.id.dodaj_przedmiot_radio_button);
        lista_przedmiotow_dla = v.findViewById(R.id.lista_przedmiotow_dla);
        wybierz_przedmiot_textView = v.findViewById(R.id.wybierz_przedmiot_textView);
        lista_uczniow_ListView = v.findViewById(R.id.lista_uczniow_listView);
        spinner_uczniow_bez_klasy = v.findViewById(R.id.spinner_uczniow_bez_klasy);
        dodaj_ucznia_btn = v.findViewById(R.id.dodaj_ucznia_btn);
        pesel_textView = v.findViewById(R.id.pesel_textView);
        text_nad_pinnerem = v.findViewById(R.id.text_nad_pinnerem);



        //radiobutton "dodawanie przedmiotu" zaznaczony jako domyślny
        dodawanie_przedmiotu_radio_button.setChecked(true);
        lista_uczniow_ListView.setVisibility(View.INVISIBLE);
        spinner_uczniow_bez_klasy.setVisibility(View.INVISIBLE);
        dodaj_ucznia_btn.setVisibility(View.INVISIBLE);
        pesel_textView.setVisibility(View.INVISIBLE);
        text_nad_pinnerem.setVisibility(View.INVISIBLE);
        // sprawdzanie, który radiobutton jest wciśnięty(przełączanie między przedmiotami i uczniami)
        ktory_radiobutton();

        //spinner wyboru przedmiotu
        stworz_spinnera_z_przedmiotami();

        //spinner wyboru klasy
        aktualizacja_spinnera_z_klasami();

        //wyświetlanie spinnera z uczniami bez klasy
        wyświetl_uczniow_bez_klasy_w_spinnerze();


        
        //onClickListener dodawanie przedmiotu do listy
        dodawanie_przedmiotu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dodaj_przedmiot_do_listy(przedmiot,wybrana_klasa);
            }
        });



        // onClickListener dodawanie nowej klasy
        dodaj_nowa_klase_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dodaj_klase();
            }
        });

        //onClickListener dodawanie ucznia do klasy
        dodaj_ucznia_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dodaj_ucznia_do_klasy();
            }
        });

        return v;
    }


    //metody-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    //metoda dodająca nową klase do bazy
    public void dodaj_klase(){
        String nazwa_klasy = nazwa_nowej_klasy.getText().toString();
        if(TextUtils.isEmpty(nazwa_klasy)){
            Toast.makeText(getContext(),"Wpisz nazwę klasy", Toast.LENGTH_SHORT).show();
        }else if(!lista_klas.contains(nazwa_klasy)) {
            baza.child("Klasy").child(nazwa_klasy).setValue("a");
            aktualizacja_spinnera_z_klasami();
            Toast.makeText(getContext(), "Dodano '" + nazwa_klasy + "' do bazy", Toast.LENGTH_SHORT).show();
          }else{
           Toast.makeText(getContext(),"'"+nazwa_klasy+"' już istnieje", Toast.LENGTH_SHORT).show();
        }
    }
//----------------------------------------------------------------
    //metoda dodająca wszystkie przedmioty do spinnera z przedmiotami
    public void stworz_spinnera_z_przedmiotami(){
        baza.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lista_przedmiotow = new ArrayList<>();

                for (DataSnapshot przedmiot : dataSnapshot.child("Przedmioty").getChildren()) {
                    lista_przedmiotow.add(przedmiot.getKey());
                    }

                ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, lista_przedmiotow);
                spinner_wybieranie_przedmiotu.setAdapter(adapter);
                spinner_wybieranie_przedmiotu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        for (int i = 1; i <= lista_przedmiotow.size(); i++) {
                            przedmiot = (String)lista_przedmiotow.get(position);
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
//----------------------------------------------------------------
    //metoda dodająca nowy przedmiot dla klasy
    public void dodaj_przedmiot_do_listy(final String przedmiot, final String klasa){

        if(!lista_przedmiotow_dla_list_view.contains(przedmiot)){
            baza.child("Klasy").child(wybrana_klasa).child("Przedmioty").child(przedmiot).setValue(przedmiot);
            baza.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot uczen : dataSnapshot.child("Klasy").child(klasa).child("Uczniowie").getChildren()){
                        baza.child("Klasy").child(klasa).child("Uczniowie").child(uczen.getKey()).child("Oceny").child(przedmiot).setValue("null");

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            wyświetl_liste_przedmiotow_w_List_View(wybrana_klasa);
            Toast.makeText(getContext(),"Dodano '"+przedmiot+"' do listy",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getContext(),"Przedmiot '"+przedmiot+"' znajduje się już w liście",Toast.LENGTH_SHORT).show();
        }
    }
//----------------------------------------------------------------
    //metoda pobierająca z bazy i wyświetlająca listę przedmiotów w ListView dla danej klasy
    public void wyświetl_liste_przedmiotow_w_List_View(final String klasa){
        baza.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lista_przedmiotow_dla_list_view = new ArrayList<>();
                for(DataSnapshot przedmiot: dataSnapshot.child("Klasy").child(klasa).child("Przedmioty").getChildren()){
                    lista_przedmiotow_dla_list_view.add(przedmiot.getValue());
                }
                ArrayAdapter adapter = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_dropdown_item,lista_przedmiotow_dla_list_view);
                lista_przemiotow_ListView.setAdapter(adapter);
                lista_przemiotow_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                        final String przedmiot = ((TextView)view).getText().toString();
                        final AlertDialog.Builder alert_dialog = new AlertDialog.Builder(getContext());
                        alert_dialog.setMessage("Czy na pewno chcesz usunąć '"+przedmiot+"' z '"+wybrana_klasa+"' ?")
                                .setCancelable(false)
                                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        usun_przedmiot(przedmiot,wybrana_klasa);

                                        Toast.makeText(getContext(),"Usunięto '"+przedmiot+"' z '"+wybrana_klasa+"'", Toast.LENGTH_SHORT).show();
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //---------------------------------------------------------------------------
    //metoda tworząca spinnera z wszystkimi klasami
        public void aktualizacja_spinnera_z_klasami() {
            baza.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    lista_klas = new ArrayList<>();
                    for (DataSnapshot przedmiot : dataSnapshot.child("Klasy").getChildren()) {
                        lista_klas.add(przedmiot.getKey());
                    }
                    ArrayAdapter adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, lista_klas);
                    spinner_wyboru_klasy.setAdapter(adapter);
                    spinner_wyboru_klasy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            for (int i = 1; i <= lista_klas.size(); i++) {
                                wybrana_klasa = (String) lista_klas.get(position);
                            }
                            wyświetl_liste_przedmiotow_w_List_View(wybrana_klasa);
                            lista_przedmiotow_dla.setText("Lista przedmiotów dla: "+wybrana_klasa);
                            wyswietl_liste_uczniow_dla_klasy();

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

        //----------------------------------------------------------------------------
            //metoda usuwająca wybrany przedmiot z wybranej klasy
    public void usun_przedmiot(final String przedmiot,final String klasa){
        baza.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                baza.child("Klasy").child(klasa).child("Przedmioty").child(przedmiot).removeValue();
                for(DataSnapshot uczen: dataSnapshot.child("Klasy").child(klasa).child("Uczniowie").getChildren()){
                    baza.child("Klasy").child(klasa).child("Uczniowie").child(uczen.getKey()).child("Oceny").child(przedmiot).removeValue();

                }
                wyświetl_liste_przedmiotow_w_List_View(wybrana_klasa);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    //---------------------------------------------------------------------------------
    //metoda sprawdzająca, który radiobutton jest wciśnięty
    public void ktory_radiobutton(){
        dodawanie_przedmiotu_radio_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(dodawanie_przedmiotu_radio_button.isChecked()){
                    wybierz_przedmiot_textView.setVisibility(View.VISIBLE);
                    lista_przedmiotow_dla.setVisibility(View.VISIBLE);
                    spinner_wybieranie_przedmiotu.setVisibility(View.VISIBLE);
                    dodawanie_przedmiotu_btn.setVisibility(View.VISIBLE);
                    lista_przemiotow_ListView.setVisibility(View.VISIBLE);
                    spinner_uczniow_bez_klasy.setVisibility(View.INVISIBLE);
                    dodaj_ucznia_btn.setVisibility(View.INVISIBLE);
                    pesel_textView.setVisibility(View.INVISIBLE);
                    text_nad_pinnerem.setVisibility(View.INVISIBLE);
                    lista_uczniow_ListView.setVisibility(View.INVISIBLE);
                }else{
                    wybierz_przedmiot_textView.setVisibility(View.INVISIBLE);
                    lista_przedmiotow_dla.setVisibility(View.INVISIBLE);
                    spinner_wybieranie_przedmiotu.setVisibility(View.INVISIBLE);
                    dodawanie_przedmiotu_btn.setVisibility(View.INVISIBLE);
                    lista_przemiotow_ListView.setVisibility(View.INVISIBLE);
                    lista_uczniow_ListView.setVisibility(View.VISIBLE);
                    spinner_uczniow_bez_klasy.setVisibility(View.VISIBLE);
                    dodaj_ucznia_btn.setVisibility(View.VISIBLE);
                    pesel_textView.setVisibility(View.VISIBLE);
                    text_nad_pinnerem.setVisibility(View.VISIBLE);
                    wyswietl_liste_uczniow_dla_klasy();


                }
            }
        });
    }

    //-----------------------------------------------------------------------------------
    //metoda pobierająca listę uczniów
    public void wyswietl_liste_uczniow_dla_klasy(){
            baza.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                       lista_uczniow = new ArrayList();
                       lista_uczniow_UID = new ArrayList();
                        for(DataSnapshot uczen : dataSnapshot.child("Klasy").child(wybrana_klasa).child("Uczniowie").getChildren()){
                           String UID = uczen.getKey();

                           String imie =(String) dataSnapshot.child("Users").child("Uczen").child(UID).child("imie").getValue();
                           String nazwisko = (String) dataSnapshot.child("Users").child("Uczen").child(UID).child("nazwisko").getValue();
                           String pesel = (String) dataSnapshot.child("Users").child("Uczen").child(UID).child("pesel").getValue();
                           lista_uczniow.add(imie+" "+nazwisko+"  | Pesel: "+pesel);
                           lista_uczniow_UID.add(UID);
                       }
                       ArrayAdapter adapter = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_dropdown_item, lista_uczniow);
                        lista_uczniow_ListView.setAdapter(adapter);
                        lista_uczniow_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                final String uczen = ((TextView)view).getText().toString();
                                final String UID = (String)lista_uczniow_UID.get(position);
                                final AlertDialog.Builder alert_dialog = new AlertDialog.Builder(getContext());
                                alert_dialog.setMessage("Czy na pewno chcesz usunąć '"+uczen+"' z '"+wybrana_klasa+"' ?")
                                        .setCancelable(false)
                                        .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                usun_ucznia(UID,wybrana_klasa);
                                                wyświetl_uczniow_bez_klasy_w_spinnerze();
                                                Toast.makeText(getContext(),"Usunięto '"+uczen+"' z '"+wybrana_klasa+"'", Toast.LENGTH_SHORT).show();
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

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }


    //--------------------------------------------------------------------------------------------
    // usuwanie ucznia z klasy
    public void usun_ucznia(String UID, String wybrana_klasa){
        baza.child("Klasy").child(wybrana_klasa).child("Uczniowie").child(UID).removeValue();
        baza.child("Users").child("Uczen").child(UID).child("klasa").setValue("null");
        wyswietl_liste_uczniow_dla_klasy();
    }

    //--------------------------------------------------------------------------------------------
    //wyświetlanie uczniów bez klasy w spinnerze
    public void wyświetl_uczniow_bez_klasy_w_spinnerze(){
        baza.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                lista_uczniow_bez_klasy = new ArrayList();
                lista_UID_uczniow_bez_klasy = new ArrayList();
                lista_pesel_uczniow_bez_klasy = new ArrayList();
                for(DataSnapshot uczen : dataSnapshot.child("Users").child("Uczen").getChildren()){
                    String UID = uczen.getKey();
                    Uczen uczenn = uczen.getValue(Uczen.class);
                    String imie = uczenn.imie;
                    String nazwisko = uczenn.nazwisko;
                    String pesel = uczenn.pesel;
                    if(uczenn.klasa.equals("null")){
                         lista_UID_uczniow_bez_klasy.add(UID);
                         lista_pesel_uczniow_bez_klasy.add(pesel);
                         lista_uczniow_bez_klasy.add(imie+" "+nazwisko);
                    }
                }
                ArrayAdapter adapter = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_dropdown_item, lista_uczniow_bez_klasy);
                spinner_uczniow_bez_klasy.setAdapter(adapter);
                spinner_uczniow_bez_klasy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        uczen_bez_klasy = (String)lista_UID_uczniow_bez_klasy.get(position);
                        pesel_textView.setText("Pesel: " + lista_pesel_uczniow_bez_klasy.get(position));

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

    //--------------------------------------------------------------------------------------------
    // dodawanie ucznia bez klasy do wybranej klasy
    public void dodaj_ucznia_do_klasy(){

        if(spinner_uczniow_bez_klasy.getAdapter().isEmpty()){
            Toast.makeText(getContext(),"Lista jest pusta",Toast.LENGTH_SHORT).show();
        }else {
            baza.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    baza.child("Users").child("Uczen").child(uczen_bez_klasy).child("klasa").setValue(wybrana_klasa);
                    baza.child("Klasy").child(wybrana_klasa).child("Uczniowie").child(uczen_bez_klasy).setValue("null");
                    Toast.makeText(getContext(),"Dodano ucznia do klasy", Toast.LENGTH_SHORT).show();

                    wyświetl_uczniow_bez_klasy_w_spinnerze();
                    wyswietl_liste_uczniow_dla_klasy();
                    pesel_textView.setText("");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }









}
