package com.example.axce.donorkuy.Model;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.GeoPoint;

/**
 * Created by AXCE on 11/11/2017.
 */

public class RumahSakit {
    private String nama;
    private String jamKerja;
    private GeoPoint location;
    private String alamat;
    private String noTlp;
    private String urgency;
    private String id;

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getJamKerja() {
        return jamKerja;
    }

    public GeoPoint getLocation() {
        return location;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getNoTlp() {
        return noTlp;
    }

    public String getUrgency() {
        return urgency;
    }
}
