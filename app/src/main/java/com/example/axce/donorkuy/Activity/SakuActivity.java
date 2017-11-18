package com.example.axce.donorkuy.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.axce.donorkuy.Adapter.BasketAdapter;
import com.example.axce.donorkuy.Adapter.BerkahAdapter;
import com.example.axce.donorkuy.Model.Barang;
import com.example.axce.donorkuy.Model.BarangBasket;
import com.example.axce.donorkuy.Model.Basket;
import com.example.axce.donorkuy.Model.Berkah;
import com.example.axce.donorkuy.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SakuActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<BarangBasket> dataSet;
    private BasketAdapter adapter;
    private FirebaseAuth mAuth;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private BarangBasket barangBasket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saku);
        getSupportActionBar().setTitle("Berkah");
        dataSet = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //firestore
        dbBasket();

        //Recycler View
        recyclerView = (RecyclerView) findViewById(R.id.basket_recyclerview);
        recyclerView.setHasFixedSize(true);

        Log.d("dataset_TAG", "onCreate : dataSet: " + dataSet.size());

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new BasketAdapter(this);
        adapter.setList(dataSet);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BasketAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, BarangBasket object) {
                QrActivity.setCode(object.getCodeQR());
                Log.d("getID", object.getId());
                barangBasket = object;
                Intent intent = new Intent(SakuActivity.this, QrActivity.class);
                startActivity(intent);
            }

            @Override
            public void onItemClearClick(int position, final BarangBasket object) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SakuActivity.this);
                dialogBuilder.setTitle("Hapus Item")
                        .setMessage("Anda yakin untuk menghapus item ini?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteBasket(object);
                            }
                        }).setNegativeButton("No", null);
                dialogBuilder.show();
            }
        });
    }

    public void deleteBasket(BarangBasket object) {
        db.collection("User").document(mAuth.getCurrentUser().getUid()).collection("Berkah").document(object.getId()).delete().addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                dbBasket();
            }
        });
    }

    private void dbBasket() {
        db.collection("User").document(mAuth.getCurrentUser().getUid()).collection("Berkah").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                dataSet.clear();
                for (DocumentChange document : documentSnapshots.getDocumentChanges()) {
                    Basket berkah = document.getDocument().toObject(Basket.class);
                    switch (document.getType()) {
                        case ADDED:
                            dataSet.add(new BarangBasket(berkah.getNama(), berkah.getHarga(), berkah.getUrl(), berkah.getCodeQR(), document.getDocument().getId()));
                            break;
                        case MODIFIED:
                            dataSet.add(new BarangBasket(berkah.getNama(), berkah.getHarga(), berkah.getUrl(), berkah.getCodeQR(), document.getDocument().getId()));
                            break;
                        case REMOVED:
                            dataSet.add(new BarangBasket(berkah.getNama(), berkah.getHarga(), berkah.getUrl(), berkah.getCodeQR(), document.getDocument().getId()));
                            break;
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        dbBasket();
    }
}

