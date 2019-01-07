package com.example.kamill.githubtest;

import java.util.ArrayList;

public class Opiekun {


    String login;
    String imie;
    String nazwisko;
    String nr_telefonu;
    ArrayList lista_dzieci;

   public Opiekun(){

   }

    public Opiekun(String login, String imie, String nazwisko, String nr_telefonu, ArrayList lista_dzieci) {
        this.login = login;
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.nr_telefonu = nr_telefonu;
        this.lista_dzieci = lista_dzieci;
    }


    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getImie() {
        return imie;
    }

    public void setImie(String imie) {
        this.imie = imie;
    }

    public String getNazwisko() {
        return nazwisko;
    }

    public void setNazwisko(String nazwisko) {
        this.nazwisko = nazwisko;
    }

    public String getNr_telefonu() {
        return nr_telefonu;
    }

    public void setNr_telefonu(String nr_telefonu) {
        this.nr_telefonu = nr_telefonu;
    }

    public ArrayList getLista_dzieci() {
        return lista_dzieci;
    }

    public void setLista_dzieci(ArrayList lista_dzieci) {
        this.lista_dzieci = lista_dzieci;
    }
}
