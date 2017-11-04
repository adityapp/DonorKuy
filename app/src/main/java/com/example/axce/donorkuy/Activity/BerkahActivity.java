package com.example.axce.donorkuy.Activity;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.axce.donorkuy.Adapter.BerkahAdapter;
import com.example.axce.donorkuy.Barang;
import com.example.axce.donorkuy.R;

import java.util.ArrayList;

public class BerkahActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private BerkahAdapter adapter;
    private ArrayList<Barang> dataSet;
    private TextView mainCoins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_berkah);
        getSupportActionBar().setTitle("Berkah");

        mainCoins = (TextView) findViewById(R.id.berkah_coin_main);

        //data dummy
        dataSet = new ArrayList<>();
        dataSet.add(new Barang("Tahu Telor", 9000));
        dataSet.add(new Barang("Rujak", 10000));
        dataSet.add(new Barang("Ayam Geprek", 12000));
        dataSet.add(new Barang("Lalapan", 9000));
        dataSet.add(new Barang("Gado-Gado", 9000));
        dataSet.add(new Barang("Es Teler", 10000));
        dataSet.add(new Barang("Ayam Nelongso", 12000));
        dataSet.add(new Barang("Ketoprak", 9000));


        //Recycler View
        recyclerView = (RecyclerView) findViewById(R.id.berkah_recyclerview);
        recyclerView.setHasFixedSize(true);

        Log.d("dataset_TAG", "onCreate : dataSet: " + dataSet.size());

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new BerkahAdapter();
        adapter.setList(dataSet);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BerkahAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, Barang object) {
                Toast.makeText(BerkahActivity.this, "pos: " + position, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(BerkahActivity.this);
                dialogBuilder.setTitle("Judul")
                        .setMessage("Pesan")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setNegativeButton("No", null);

                dialogBuilder.show();
            }
        });
    }
}
