package com.example.kamill.githubtest;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Nauczyciel_Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener  {

    private TextView login;
    private TextView imie_naziwko;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference baza;
    private String imie;
    private String nazwisko;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nauczyciel_);

        firebaseAuth = FirebaseAuth.getInstance();
        imie_naziwko = findViewById(R.id.imie_nazwisko_nauczyciela);
        baza = FirebaseDatabase.getInstance().getReference();

        baza.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 imie = (String)dataSnapshot.child("Users").child("Nauczyciel").child(firebaseAuth.getCurrentUser().getUid()).child("imie").getValue();
                 nazwisko = (String)dataSnapshot.child("Users").child("Nauczyciel").child(firebaseAuth.getCurrentUser().getUid()).child("nazwisko").getValue();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(!drawer.isDrawerOpen(GravityCompat.START)){
            drawer.openDrawer(GravityCompat.START);
        }
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        // zmiana tekstu w headerze paska bocznego
        NavigationView navigationView =  findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View v = navigationView.getHeaderView(0);
        imie_naziwko = v.findViewById(R.id.imie_nazwisko_nauczyciela);
        login = v.findViewById(R.id.login_nauczyciela);
        login.setText(firebaseAuth.getCurrentUser().getEmail().toString());
        baza.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                imie = (String)dataSnapshot.child("Users").child("Nauczyciel").child(firebaseAuth.getCurrentUser().getUid()).child("imie").getValue();
                nazwisko = (String)dataSnapshot.child("Users").child("Nauczyciel").child(firebaseAuth.getCurrentUser().getUid()).child("nazwisko").getValue();
                imie_naziwko.setText(imie+" "+nazwisko);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if(!drawer.isDrawerOpen(GravityCompat.START)){
            drawer.openDrawer(GravityCompat.START);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.nauczyciel_activity, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
       if (id == R.id.przedmioty_nauczyciela) {
            setTitle("Moje przedmioty");
            wyswietlenie_uczonych_przedmiotow_n wysw_uczo_przed = new wyswietlenie_uczonych_przedmiotow_n();
            FragmentManager f = getSupportFragmentManager();
            f.beginTransaction().replace(R.id.fragment,wysw_uczo_przed).commit();
        } else if (id == R.id.klasy_nauczyciela) {
            setTitle("Moje klasy");
            wyswietlenie_uczonych_klas_n wysw_uczo_klas = new wyswietlenie_uczonych_klas_n();
            FragmentManager f = getSupportFragmentManager();
            f.beginTransaction().replace(R.id.fragment, wysw_uczo_klas).commit();
        } else if (id == R.id.wyloguj){
            firebaseAuth.signOut();
            Intent i = new Intent(getApplicationContext(),LoginActivity.class);
            finish();
            startActivity(i);
            Toast.makeText(getApplicationContext(),"Wylogowano",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.dodaj_ocene) {
           setTitle("Dodaj ocenę");
           dodawanie_oceny_n dodajocene = new dodawanie_oceny_n();
           FragmentManager f = getSupportFragmentManager();
           f.beginTransaction().replace(R.id.fragment, dodajocene).commit();
       } else if (id == R.id.dodaj_uwage) {
           setTitle("Dodaj uwagę");
           dodawanie_uwagi_n dodajuwage = new dodawanie_uwagi_n();
           FragmentManager f = getSupportFragmentManager();
           f.beginTransaction().replace(R.id.fragment, dodajuwage).commit();
       }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
