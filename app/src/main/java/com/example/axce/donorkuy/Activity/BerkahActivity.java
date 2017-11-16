package com.example.axce.donorkuy.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.axce.donorkuy.Adapter.BerkahAdapter;
import com.example.axce.donorkuy.Barang;
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
import java.util.Map;

public class BerkahActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private BerkahAdapter adapter;
    private ArrayList<Barang> dataSet;
    private TextView mainCoins;
    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private User user;
    private FirebaseAuth mAuth;
    private ImageButton basket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_berkah);
        getSupportActionBar().setTitle("Berkah");
        mAuth = FirebaseAuth.getInstance();
        basket = findViewById(R.id.berkah_basket);
        mainCoins = (TextView) findViewById(R.id.berkah_coin_main);
        dataSet = new ArrayList<>();

        //Firestore
        db.collection("BerkahList").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("error", "listen:error", e);
                    return;
                }
                for (DocumentChange document : documentSnapshots.getDocumentChanges()) {
                    Berkah berkah = document.getDocument().toObject(Berkah.class);
                    switch (document.getType()) {
                        case ADDED:
                            dataSet.add(new Barang(berkah.getNama(), berkah.getHarga(), berkah.getUrl()));
                            break;
                        case MODIFIED:
                            break;
                        case REMOVED:
                            break;
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

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

        //Recycler View
        recyclerView = (RecyclerView) findViewById(R.id.berkah_recyclerview);
        recyclerView.setHasFixedSize(true);

        Log.d("dataset_TAG", "onCreate : dataSet: " + dataSet.size());

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new BerkahAdapter(this);
        adapter.setList(dataSet);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new BerkahAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final int position, Barang object) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(BerkahActivity.this);
                dialogBuilder.setTitle(dataSet.get(position).getNama())
                        .setMessage("Anda akan membeli " + dataSet.get(position).getNama() + " dengan "
                                + dataSet.get(position).getKoin() + " point")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if ((user.getKoin() - dataSet.get(position).getKoin()) >= 0) {
                                    long koin = user.getKoin() - dataSet.get(position).getKoin();
                                    Map<String, Object> update = new HashMap<>();
                                    update.put("id", user.getId());
                                    update.put("nama", user.getNama());
                                    update.put("noTlp", user.getNoTlp());
                                    update.put("koin", koin);
                                    update.put("email", user.getEmail());
                                    update.put("url", user.getUrl());
                                    update.put("golDarah", user.getGolDarah());
                                    db.collection("User").document(user.getId()).set(update);
                                } else {
                                    Toast.makeText(BerkahActivity.this, "Koin tidak cukup", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("No", null);

                dialogBuilder.show();
            }
        });

        basket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BerkahActivity.this, SakuActivity.class);
                startActivity(intent);
            }
        });
    }
}
