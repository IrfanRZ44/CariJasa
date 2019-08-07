package com.exomatik.carijasa.Model;

import java.util.ArrayList;

/**
 * Created by IrfanRZ on 03/08/2019.
 */

public class ModelJasa {
    String id, namaJasa, gaji, kategori, detailJasa, syarat, kontrak, uid;
    ArrayList<String> foto = new ArrayList<String>();

    public ModelJasa() {
    }

    public ModelJasa(String id, String namaJasa, String gaji, String kategori, String detailJasa, String syarat, String kontrak, String uid, ArrayList<String> foto) {
        this.id = id;
        this.namaJasa = namaJasa;
        this.gaji = gaji;
        this.kategori = kategori;
        this.detailJasa = detailJasa;
        this.syarat = syarat;
        this.kontrak = kontrak;
        this.uid = uid;
        this.foto = foto;
    }

    public ArrayList<String> getFoto() {
        return foto;
    }

    public void setFoto(ArrayList<String> foto) {
        this.foto = foto;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNamaJasa() {
        return namaJasa;
    }

    public void setNamaJasa(String namaJasa) {
        this.namaJasa = namaJasa;
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

    public String getDetailJasa() {
        return detailJasa;
    }

    public void setDetailJasa(String detailJasa) {
        this.detailJasa = detailJasa;
    }

    public String getSyarat() {
        return syarat;
    }

    public void setSyarat(String syarat) {
        this.syarat = syarat;
    }

    public String getKontrak() {
        return kontrak;
    }

    public void setKontrak(String kontrak) {
        this.kontrak = kontrak;
    }
}
