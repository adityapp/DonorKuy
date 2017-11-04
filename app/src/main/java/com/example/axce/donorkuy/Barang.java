package com.example.axce.donorkuy;

/**
 * Created by AXCE on 12/10/2017.
 */

public class Barang {
    private String nama;
    private int koin;

    public Barang(String nama, int koin){
        this.nama = nama;
        this.koin = koin;
    }

    public String getNama(){
        return nama;
    }

    public int getKoin(){
        return koin;
    }
}
