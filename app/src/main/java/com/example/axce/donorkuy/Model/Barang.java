package com.example.axce.donorkuy.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AXCE on 12/10/2017.
 */

public class Barang {
    private String nama, url,id;
    private List<String> codeQR;
    private long koin;

    public Barang(String nama, int koin, String url, List<String> codeQR,String id){
        this.nama = nama;
        this.koin = koin;
        this.url = url;
        this.codeQR = codeQR;
        this.id = id;
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

    public List<String> getCodeQR() {
        return codeQR;
    }

    public String getId() {
        return id;
    }
}
