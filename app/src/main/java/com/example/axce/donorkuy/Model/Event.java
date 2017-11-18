package com.example.axce.donorkuy.Model;

import com.google.firebase.firestore.GeoPoint;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by AXCE on 11/11/2017.
 */

public class Event {

    private String alamat, deskripsi, jamKerja, nama, id,url;
    private GeoPoint location;
    private Date waktuMulai, waktuSelesai;

    public String getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public String getJamKerja() {
        return jamKerja;
    }

    public String getNama() {
        return nama;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public Date getWaktuMulai() {
        return waktuMulai;
    }

    public Date getWaktuSelesai() {
        return waktuSelesai;
    }
}
