package com.exomatik.carijasa.Model;

/**
 * Created by IrfanRZ on 02/08/2019.
 */

public class ModelUser {
    String namaLengkap, email, uid, jenisAkun, foto, noHp, alamat, locationPoint;

    public ModelUser() {
    }

    public ModelUser(String namaLengkap, String email, String uid, String jenisAkun, String foto, String noHp, String alamat, String locationPoint) {
        this.namaLengkap = namaLengkap;
        this.email = email;
        this.uid = uid;
        this.jenisAkun = jenisAkun;
        this.foto = foto;
        this.noHp = noHp;
        this.alamat = alamat;
        this.locationPoint = locationPoint;
    }

    public String getLocationPoint() {
        return locationPoint;
    }

    public void setLocationPoint(String locationPoint) {
        this.locationPoint = locationPoint;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getJenisAkun() {
        return jenisAkun;
    }

    public void setJenisAkun(String jenisAkun) {
        this.jenisAkun = jenisAkun;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getNoHp() {
        return noHp;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }
}
