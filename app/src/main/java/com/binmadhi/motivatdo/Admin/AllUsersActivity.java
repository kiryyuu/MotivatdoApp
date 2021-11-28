package com.binmadhi.motivatdo.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.binmadhi.motivatdo.FirebaseHelper.FirebaseDatabaseHelper;
import com.binmadhi.motivatdo.FirebaseHelper.PreferencesManager;
import com.binmadhi.motivatdo.Models.User;
import com.binmadhi.motivatdo.R;

import java.util.ArrayList;

public class AllUsersActivity extends AppCompatActivity {
    private RecyclerView rvTechnicianList;
    private UsersAdapter adapter;
    private ArrayList<User> arrayList;
    private Toolbar toolbarToolbar;
    private LinearLayout llNoDataFound;
    private LottieAnimationView animLoading;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    private PreferencesManager pref;
    User currentUser;
    public static final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        try {
            //initialize views and assign id's
            firebaseDatabaseHelper = new FirebaseDatabaseHelper(this);
            pref = new PreferencesManager(this);
            currentUser=pref.getCurrentUser();
            arrayList = new ArrayList<>();
            animLoading = findViewById(R.id.animLoading);
            llNoDataFound = findViewById(R.id.llNoDataFound);
            rvTechnicianList = findViewById(R.id.rvTechnicianList);
            //Support Toolbar
            toolbarToolbar = findViewById(R.id.toolbarToolbar);
            getSupportActionBar();
            setSupportActionBar(toolbarToolbar);
            // Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.amount_payable);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbarToolbar.setNavigationOnClickListener(v -> finish());
            //End Of toolbar text
            arrayList = new ArrayList<>();
            rvTechnicianList.setHasFixedSize(true);
            rvTechnicianList.setLayoutManager(new LinearLayoutManager(this));
            adapter = new UsersAdapter(this, arrayList, "panel");
            rvTechnicianList.setAdapter(adapter);
            //get All Users
            getRemoteWorkers();
        } catch (
                Exception e) {
            Log.e(TAG, "onCreate: TechnicianListActivity: EXC " + e.toString());
        }

    }

    private void getRemoteWorkers() {
        animLoading.setVisibility(View.VISIBLE);
        try {
            animLoading.setVisibility(View.VISIBLE);
            db.child("Users").orderByChild("createdBy").equalTo(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    animLoading.setVisibility(View.GONE);
                    ArrayList<User> arrayList1 = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        final User notificationModel = snapshot.getValue(User.class);
                        arrayList1.add(notificationModel);
                        Log.e(TAG, "onDataChange: Adding");
                    }
                    arrayList.addAll(arrayList1);
                    arrayList1.clear();
                    adapter.notifyDataSetChanged();
                    if (arrayList.isEmpty()) {
                        llNoDataFound.setVisibility(View.VISIBLE);
                    } else {
                        llNoDataFound.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    animLoading.setVisibility(View.GONE);
                    Log.e(TAG, "onCancelled: Error" + error.toString());
                    //  Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "getNotifications: " + e.toString());
        }
    }

}