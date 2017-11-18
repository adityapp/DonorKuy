package com.example.axce.donorkuy.Activity;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.axce.donorkuy.R;

public class QrActivity extends AppCompatActivity {
    private ImageView qrCode;
    private static String code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        qrCode = findViewById(R.id.qr_code);
        Glide.with(QrActivity.this).load("http://api.qrserver.com/v1/create-qr-code/?size=300x300&data=" + code).into(qrCode);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public static void setCode(String qrcode){
        code = qrcode;
    }
}
