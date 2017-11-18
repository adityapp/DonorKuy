package com.example.axce.donorkuy.Model;

import java.util.List;

/**
 * Created by AXCE on 17/11/2017.
 */

public class Basket {

    private String nama, url, codeQR;
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

    public String getCodeQR() {
        return codeQR;
    }
}
