package com.example.axce.donorkuy;

/**
 * Created by AXCE on 12/10/2017.
 */

public class Barang {
    private String nama, url;
    private long koin;

    public Barang(String nama, int koin, String url){
        this.nama = nama;
        this.koin = koin;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getNama(){
        return nama;
    }

    public long getKoin(){
        return koin;
    }
}
