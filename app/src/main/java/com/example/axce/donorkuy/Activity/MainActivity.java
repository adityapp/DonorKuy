package com.example.axce.donorkuy.Activity;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.axce.donorkuy.Model.Berkah;
import com.example.axce.donorkuy.Model.Event;
import com.example.axce.donorkuy.Model.RumahSakit;
import com.example.axce.donorkuy.Model.User;
import com.example.axce.donorkuy.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private ImageButton direct;
    private RelativeLayout coins;
    private ImageView profile, imageLocation;
    private CardView cardView;
    private TextView locationName, alamat, userName, koin;
    private MapFragment mMapFragment;
    private FusedLocationProviderClient lastKnownLocation;
    private GoogleApiClient googleApiClient;
    private int REQUEST_PERMISSION_LOCATION = 9001;
    private GoogleMap gMap;
    private boolean anim = false;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ArrayList<RumahSakit> rSakit;
    private ArrayList<Event> dbEvent;
    private int size = 0;
    private User user;
    private FirebaseAuth mAuth;
    private GeoPoint markerSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cardView = (CardView) findViewById(R.id.card_view);
        direct = (ImageButton) findViewById(R.id.button_direct);
        coins = findViewById(R.id.coin_btn);
        locationName = (TextView) findViewById(R.id.locationName);
        alamat = (TextView) findViewById(R.id.alamat);
        userName = (TextView) findViewById(R.id.username);
        profile = (ImageView) findViewById(R.id.profile);
        koin = (TextView) findViewById(R.id.coin);
        mAuth = FirebaseAuth.getInstance();
        rSakit = new ArrayList<>();
        dbEvent = new ArrayList<>();
        imageLocation = findViewById(R.id.profile2);

        profile.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                dialogBuilder.setTitle("Log Out")
                        .setMessage("Anda yakin untuk Log Out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mAuth.signOut();
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }).setNegativeButton("No", null);
                dialogBuilder.show();
                return true;
            }
        });

        //Firestore realtime
        dbUser();

        mMapFragment = MapFragment.newInstance();
        mMapFragment.getMapAsync(this);

        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map, mMapFragment);
        fragmentTransaction.commit();

        //main -> berkah
        coins.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BerkahActivity.class);
                startActivity(intent);
            }
        });

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onBackPressed() {
        if (!anim) {
            super.onBackPressed();
        } else {
            cardView.animate().translationY(cardView.getHeight() / 2).setDuration(300).setListener(null);
            anim = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        gMap = googleMap;
        db.collection("RumahSakit").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("error", "listen:error", e);
                    return;
                }
                for (DocumentChange document : documentSnapshots.getDocumentChanges()) {
                    RumahSakit rs = document.getDocument().toObject(RumahSakit.class);
                    gMap.addMarker(new MarkerOptions().position(new LatLng(rs.getLocation().getLatitude(), rs.getLocation().getLongitude()))
                            .title(rs.getNama()).icon(BitmapDescriptorFactory.fromResource(R.mipmap.rs)));
                    rSakit.add(rs);
                    size++;
                }
            }
        });

        db.collection("Event").whereGreaterThanOrEqualTo("waktuSelesai", Calendar.getInstance().getTime())
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("error", "listen:error", e);
                            return;
                        }
                        for (DocumentChange document : documentSnapshots.getDocumentChanges()) {
                            Event event = document.getDocument().toObject(Event.class);
                            googleMap.addMarker(new MarkerOptions().position(new LatLng(event.getLocation().getLatitude(), event.getLocation().getLongitude()))
                                    .title(event.getNama()).icon(BitmapDescriptorFactory.fromResource(R.mipmap.event)));
                            dbEvent.add(event);
                            size++;
                        }
                    }
                });

        //set Marker RS - Event
        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (cari(marker.getTitle())) {
                    cardView.animate().translationY(0).setDuration(300).setListener(null);
                    anim = true;
                    Log.d("marker", "dari marker");
                }
                return false;
            }
        });
        updateLastLocation();
    }

    private void dbUser() {
        db.collection("User").whereEqualTo("id", mAuth.getCurrentUser().getUid()).addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("error", "listen:error", e);
                    return;
                }
                for (DocumentChange document : documentSnapshots.getDocumentChanges()) {
                    user = document.getDocument().toObject(User.class);
                    switch (document.getType()) {
                        case ADDED:
                            userName.setText(user.getNama());
                            koin.setText(String.valueOf(user.getKoin()));
                            Glide.with(MainActivity.this).load(user.getUrl()).into(profile);
                            break;
                        case MODIFIED:
                            userName.setText(user.getNama());
                            koin.setText(String.valueOf(user.getKoin()));
                            Glide.with(MainActivity.this).load(user.getUrl()).into(profile);
                            break;
                        case REMOVED:
                            userName.setText(user.getNama());
                            koin.setText(String.valueOf(user.getKoin()));
                            Glide.with(MainActivity.this).load(user.getUrl()).into(profile);
                            break;
                    }
                }
            }
        });
    }

    private boolean cari(String marker) {
        boolean b = false;
        for (int i = 0; i < size; i++) {
            if (rSakit.size() != 0 && rSakit.get(i).getNama().equals(marker)) {
                b = true;
                locationName.setText(rSakit.get(i).getNama());
                alamat.setText(rSakit.get(i).getAlamat());
                Glide.with(MainActivity.this).load(rSakit.get(i).getUrl()).into(imageLocation);
                markerSelect = rSakit.get(i).getLocation();
                break;
            } else if (dbEvent.size() != 0 && dbEvent.get(i).getNama().equals(marker)) {
                b = true;
                locationName.setText(dbEvent.get(i).getNama());
                alamat.setText(dbEvent.get(i).getAlamat());
                Glide.with(MainActivity.this).load(dbEvent.get(i).getUrl()).into(imageLocation);
                markerSelect = dbEvent.get(i).getLocation();
                break;
            } else {
                b = false;
            }
        }
        return b;
    }

    private void updateLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION_LOCATION);
        }
        lastKnownLocation = LocationServices.getFusedLocationProviderClient(this);
        lastKnownLocation.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                if (location != null) {
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                            new LatLng(location.getLatitude(), location.getLongitude()), 15)
                    );
                    gMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
                    direct.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + markerSelect.getLatitude() + "," + markerSelect.getLongitude());
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            startActivity(mapIntent);
                        }
                    });
                }
            }
        });
        gMap.setMyLocationEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                updateLastLocation();
            } else {
                // TODO: 10/15/2016 Tell user to use GPS
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //updateLastLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        dbUser();
    }
}
