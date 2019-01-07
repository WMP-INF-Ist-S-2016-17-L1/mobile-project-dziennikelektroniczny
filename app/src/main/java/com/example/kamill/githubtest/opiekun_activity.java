package com.example.kamill.githubtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class opiekun_activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView ImieNazwisko;
    private FirebaseAuth firebaseAuth;
    private TextView EmailNauczyciela;
    private DatabaseReference Baza;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opiekun_activity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        firebaseAuth = FirebaseAuth.getInstance();
        Baza = FirebaseDatabase.getInstance().getReference();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        if(!drawer.isDrawerOpen(GravityCompat.START)){
            drawer.openDrawer(GravityCompat.START);
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View v = navigationView.getHeaderView(0);
        EmailNauczyciela = v.findViewById(R.id.EmailNauczyciela);
        ImieNazwisko = v.findViewById(R.id.ImieNazwiskoNauczyciela);
        Baza.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String Uid = firebaseAuth.getCurrentUser().getUid();
                String Imie = (String)dataSnapshot.child("Users").child("Opiekun").child(Uid).child("imie").getValue();
                String Nazwisko = (String)dataSnapshot.child("Users").child("Opiekun").child(Uid).child("nazwisko").getValue();
                ImieNazwisko.setText(Imie + " " + Nazwisko);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        EmailNauczyciela.setText(firebaseAuth.getCurrentUser().getEmail());
    }







    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.opiekun_activity, menu);
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

        if (id == R.id.nav_oceny) {
            setTitle("Oceny");
            oceny_o ob = new oceny_o();
            FragmentManager f = getSupportFragmentManager();
            f.beginTransaction().replace(R.id.fragment,ob).commit();
        } else if (id == R.id.nav_wyloguj) {
            firebaseAuth.signOut();
            Intent i = new Intent(getApplicationContext(),LoginActivity.class);
            finish();
            startActivity(i);
            Toast.makeText(getApplicationContext(),"Wylogowano",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_uwagi) {
            setTitle("Uwagi");
            uwagi_o ob = new uwagi_o();
            FragmentManager f = getSupportFragmentManager();
            f.beginTransaction().replace(R.id.fragment,ob).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
