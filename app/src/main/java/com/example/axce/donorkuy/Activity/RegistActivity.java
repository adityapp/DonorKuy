package com.example.axce.donorkuy.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.axce.donorkuy.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RegistActivity extends AppCompatActivity {

    private Button btnRegis, pickDate;
    private TextView email;
    private EditText nama, nomorTlp;
    private ImageView profileImage;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private RadioGroup radioGroup;
    private String golDarah;
    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        getSupportActionBar().setTitle("Profile");

        btnRegis = (Button) findViewById(R.id.profile_btn);
        email = (TextView) findViewById(R.id.profile_email);
        nama = (EditText) findViewById(R.id.input_nama);
        nomorTlp = (EditText) findViewById(R.id.input_tlp);
        profileImage = (ImageView) findViewById(R.id.profil_image);
        mAuth = FirebaseAuth.getInstance();
        email.setText(mAuth.getCurrentUser().getEmail());
        Glide.with(this).load(mAuth.getCurrentUser().getPhotoUrl().toString()).into(profileImage);
        radioGroup = (RadioGroup) findViewById(R.id.radio_button);
        pickDate = (Button) findViewById(R.id.pick_date);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radio_ab) {
                    golDarah = "AB";
                } else if (checkedId == R.id.radio_a) {
                    golDarah = "A";
                } else if (checkedId == R.id.radio_o) {
                    golDarah = "O";
                } else if (checkedId == R.id.radio_b) {
                    golDarah = "B";
                } else if (checkedId == R.id.radio_non) {
                    golDarah = null;
                }
            }
        });

        myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(RegistActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //tambah user ke firestore
                Map<String, Object> user = new HashMap<>();
                user.put("id", mAuth.getCurrentUser().getUid());
                user.put("nama", nama.getText().toString());
                user.put("noTlp", nomorTlp.getText().toString());
                user.put("koin", 100000);
                user.put("tglLahir",pickDate.getText().toString());
                user.put("email", mAuth.getCurrentUser().getEmail());
                user.put("url", mAuth.getCurrentUser().getPhotoUrl().toString());
                user.put("golDarah", golDarah);
                db.collection("User").document(mAuth.getCurrentUser().getUid()).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(RegistActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        });
    }

    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        pickDate.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mAuth.signOut();
    }
}
