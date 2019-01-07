package com.example.kamill.githubtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Uczen_activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView ImieNazwisko;
    private FirebaseAuth firebaseAuth;
    private TextView EmailUczen;
    private DatabaseReference Baza;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uczen_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        Baza = FirebaseDatabase.getInstance().getReference();

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
        EmailUczen = v.findViewById(R.id.EmailUczen);
        ImieNazwisko = v.findViewById(R.id.ImieNazwiskoUczen);
        Baza.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String Uid = firebaseAuth.getCurrentUser().getUid();
               String Imie = (String)dataSnapshot.child("Users").child("Uczen").child(Uid).child("imie").getValue();
               String Nazwisko = (String)dataSnapshot.child("Users").child("Uczen").child(Uid).child("nazwisko").getValue();
                ImieNazwisko.setText(Imie + " " + Nazwisko);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        EmailUczen.setText(firebaseAuth.getCurrentUser().getEmail());
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
        getMenuInflater().inflate(R.menu.uczen_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.OcenyID) {
            setTitle("Oceny");
            Oceny_u obiekt = new Oceny_u();
            FragmentManager f = getSupportFragmentManager();
            f.beginTransaction().replace(R.id.fragment,obiekt).commit();
        } else if (id == R.id.UwagiID) {
            setTitle("Uwagi");
            Uwagi_u obiekt = new Uwagi_u();
            FragmentManager f = getSupportFragmentManager();
            f.beginTransaction().replace(R.id.fragment,obiekt).commit();

        } else if (id == R.id.wyloguj){
            firebaseAuth.signOut();
            Intent i = new Intent(getApplicationContext(),LoginActivity.class);
            finish();
            startActivity(i);
            Toast.makeText(getApplicationContext(),"Wylogowano",Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



}
