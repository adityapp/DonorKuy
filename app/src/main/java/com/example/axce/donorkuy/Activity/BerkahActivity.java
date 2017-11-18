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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.axce.donorkuy.Adapter.BerkahAdapter;
import com.example.axce.donorkuy.Model.Barang;
import com.example.axce.donorkuy.Model.BarangBasket;
import com.example.axce.donorkuy.Model.Berkah;
import com.example.axce.donorkuy.Model.User;
import com.example.axce.donorkuy.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class BerkahActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private BerkahAdapter adapter;
    private List<Barang> dataSet;
    private TextView mainCoins;
    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private User user;
    private FirebaseAuth mAuth;
    private ImageButton berkahBtn;
    private Berkah berkah;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_berkah);
        getSupportActionBar().setTitle("Berkah");
        mAuth = FirebaseAuth.getInstance();
        mainCoins = (TextView) findViewById(R.id.berkah_coin_main);
        dataSet = new ArrayList<Barang>();
        berkahBtn = findViewById(R.id.berkah_basket);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        berkahBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BerkahActivity.this, SakuActivity.class);
                startActivity(intent);
            }
        });

        //Firestore
        dbBerkahList();
        dbUser();

        //Recycler View
        recyclerView = (RecyclerView) findViewById(R.id.berkah_recyclerview);
        recyclerView.setHasFixedSize(true);

        Log.d("dataset_TAG", "onCreate : dataSet: " + dataSet.size());

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new BerkahAdapter(this);
        adapter.setList((ArrayList<Barang>) dataSet);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BerkahAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final int position, final Barang object) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(BerkahActivity.this);
                dialogBuilder.setTitle(dataSet.get(position).getNama())
                        .setMessage("Anda akan membeli " + dataSet.get(position).getNama() + " dengan "
                                + dataSet.get(position).getKoin() + " point")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if ((user.getKoin() - dataSet.get(position).getKoin()) >= 0) {
                                    if (object.getCodeQR().size() != 0) {
                                        updateUser(object);
                                        showQRDialog(object.getCodeQR().get(0));
                                        addToBasket(object);
                                        updateBarang(object);
                                    }else{
                                        Toast.makeText(BerkahActivity.this,"Barang habis",Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    Toast.makeText(BerkahActivity.this, "Koin tidak cukup", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("No", null);

                dialogBuilder.show();
            }
        });
    }

    private void showQRDialog(String qrCode) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(BerkahActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.code_view, null);
        dialog.setTitle("QR Code").setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dbBerkahList();
            }
        }).setView(dialogView);

        ImageView code = dialogView.findViewById(R.id.qr_code);
        Glide.with(BerkahActivity.this).load("http://api.qrserver.com/v1/create-qr-code/?size=300x300&data=" + qrCode).into(code);
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void dbBerkahList() {
        db.collection("BerkahList").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("error", "listen:error", e);
                    return;
                }
                dataSet.clear();
                for (DocumentChange document : documentSnapshots.getDocumentChanges()) {
                    berkah = document.getDocument().toObject(Berkah.class);
                    switch (document.getType()) {
                        case ADDED:
                            dataSet.add(new Barang(berkah.getNama(), berkah.getHarga(), berkah.getUrl(), berkah.getCodeQR(), document.getDocument().getId()));
                            break;
                        case MODIFIED:
                            dataSet.add(new Barang(berkah.getNama(), berkah.getHarga(), berkah.getUrl(), berkah.getCodeQR(), document.getDocument().getId()));
                            break;
                        case REMOVED:
                            dataSet.add(new Barang(berkah.getNama(), berkah.getHarga(), berkah.getUrl(), berkah.getCodeQR(), document.getDocument().getId()));
                            break;
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void dbUser() {
        db.collection("User").whereEqualTo("id", mAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("error", "listen:error", e);
                    return;
                }
                for (DocumentChange document : documentSnapshots.getDocumentChanges()) {
                    user = document.getDocument().toObject(User.class);
                }
                mainCoins.setText(String.valueOf(user.getKoin()));
            }
        });
    }

    public void updateBarang(Barang object) {
        object.getCodeQR().remove(0);
        List<String> qrList = object.getCodeQR();
        Map<String, Object> update = new HashMap<>();
        update.put("nama", object.getNama());
        update.put("harga", object.getKoin());
        update.put("url", object.getUrl());
        update.put("codeQR", qrList);
        db.collection("BerkahList").document(object.getId()).set(update);
    }

    public void addToBasket(Barang object){
        Map<String,Object> update = new HashMap<>();
        update.put("nama", object.getNama());
        update.put("harga", object.getKoin());
        update.put("url", object.getUrl());
        update.put("codeQR", object.getCodeQR().get(0));
        db.collection("User").document(user.getId()).collection("Berkah").document(UUID.randomUUID().toString()).set(update);
    }

    public void updateUser(Barang object) {
        long koin = user.getKoin() - object.getKoin();
        Map<String, Object> update = new HashMap<>();
        update.put("id", user.getId());
        update.put("nama", user.getNama());
        update.put("noTlp", user.getNoTlp());
        update.put("koin", koin);
        update.put("email", user.getEmail());
        update.put("url", user.getUrl());
        update.put("golDarah", user.getGolDarah());
        db.collection("User").document(user.getId()).set(update);
    }
}
