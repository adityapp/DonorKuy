package com.example.axce.donorkuy.Model;

import java.util.List;

/**
 * Created by AXCE on 11/11/2017.
 */

public class Berkah {

    private String nama, url;
    private List<String> codeQR;
    private int harga;

    public String getUrl() {
        return url;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    public List<String> getCodeQR() {
        return codeQR;
    }
}
