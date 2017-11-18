package com.example.axce.donorkuy.Model;

import android.util.Log;

import java.util.List;

/**
 * Created by AXCE on 17/11/2017.
 */

public class BarangBasket {
    private String nama, url, codeQR, id;
    private long koin;

    public BarangBasket(String nama, int koin, String url, String codeQR, String id) {
        this.nama = nama;
        this.koin = koin;
        this.url = url;
        this.codeQR = codeQR;
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public String getNama() {
        return nama;
    }

    public long getKoin() {
        return koin;
    }

    public String getCodeQR() {
        return codeQR;
    }

    public String getId() {
        return id;
    }
}
