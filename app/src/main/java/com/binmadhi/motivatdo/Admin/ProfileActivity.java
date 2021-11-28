package com.binmadhi.motivatdo.Admin;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.binmadhi.motivatdo.Models.User;
import com.binmadhi.motivatdo.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "TAG";
    private TextView tvNameProfile, tvEmailUser, tvPoints, tvFamilyName, tvContactNo;
    CircleImageView ivStdProfile;
    String uid;
    private DatabaseReference tableUser;
    private Toolbar toolbarToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        try {
            //initialize views
            tableUser = FirebaseDatabase.getInstance().getReference().child("Users");
            //Support Toolbar
            toolbarToolbar = findViewById(R.id.toolbarToolbar);
            getSupportActionBar();
            setSupportActionBar(toolbarToolbar);
            // Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.amount_payable);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbarToolbar.setNavigationOnClickListener(v -> finish());
            //End Of toolbar text
            uid = getIntent().getStringExtra("UID");
            tvContactNo = findViewById(R.id.tvContactNo);
            tvFamilyName = findViewById(R.id.tvFamilyName);
            tvEmailUser = findViewById(R.id.tvEmailUser);
            tvPoints = findViewById(R.id.tvPoints);
            tvNameProfile = findViewById(R.id.tvNameProfile);
            ivStdProfile = findViewById(R.id.ivStdProfile);
            getuserDetails();
        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e.toString());
        }
    }

    private void getuserDetails() {
        try {
            tableUser.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User userModel = snapshot.getValue(User.class);
                    if (userModel.getImage().equals("null")) {
                        ivStdProfile.setImageDrawable(getResources().getDrawable(R.drawable.default_profile));
                    } else {
                        Glide.with(ProfileActivity.this).load(userModel.getImage()).into(ivStdProfile);
                    }
                    tvEmailUser.setText(userModel.getEmail());
                    tvNameProfile.setText(userModel.getName());
                    tvFamilyName.setText(userModel.getFamilyName());
                    tvContactNo.setText(userModel.getPhoneNo());
                    tvPoints.setText(userModel.getPoints());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "getuserDetails: " + e.toString());
        }
    }
}