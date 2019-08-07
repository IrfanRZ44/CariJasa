package com.exomatik.carijasa.Featured;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by IrfanRZ on 04/08/2019.
 */

public class DataKategori {

    public DataKategori() {
    }

    public ArrayList<String> DataKategori(String listPertama){
        ArrayList<String> listKategori = new ArrayList<String>();
        ArrayList<String> listCek = new ArrayList<String>();

        if (listPertama != null){
            listKategori.add(listPertama);
        }

        listCek.add("Studio");
        listCek.add("Wedding");
        listCek.add("Pre Wedding");
        listCek.add("Food");

        if (listKategori.size() != 0){
            for (int a = 0; a < listCek.size(); a++){
                if (!listKategori.get(0).equals(listCek.get(a))){
                    listKategori.add(listCek.get(a));
                }
            }
        }
        else {
            listKategori = listCek;
        }

        return listKategori;
    }
}
