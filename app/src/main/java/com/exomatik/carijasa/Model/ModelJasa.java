package com.exomatik.carijasa.Model;

import java.util.ArrayList;

/**
 * Created by IrfanRZ on 03/08/2019.
 */

public class ModelJasa {
    String id, gaji, kategori, uid;
    ArrayList<String> foto = new ArrayList<String>();

    public ModelJasa() {
    }

    public ModelJasa(String id, String gaji, String kategori, String uid, ArrayList<String> foto) {
        this.id = id;
        this.gaji = gaji;
        this.kategori = kategori;
        this.uid = uid;
        this.foto = foto;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGaji() {
        return gaji;
    }

    public void setGaji(String gaji) {
        this.gaji = gaji;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ArrayList<String> getFoto() {
        return foto;
    }

    public void setFoto(ArrayList<String> foto) {
        this.foto = foto;
    }
}
