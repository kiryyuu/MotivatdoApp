package com.binmadhi.motivatdo.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.binmadhi.motivatdo.Activities.LogInActivity;
import com.binmadhi.motivatdo.FirebaseHelper.PreferencesManager;
import com.binmadhi.motivatdo.Models.User;
import com.binmadhi.motivatdo.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class AdminDashboard extends AppCompatActivity {
    private static final String TAG = "TAG";
    //Admin Dashboard view
    TextView textViewAdminToday;
    TextView textViewAdminYear;
    TextView textViewAdminMonth;

    ArrayList<String> arrayListToday = new ArrayList<>();
    ArrayList<String> arrayListMonth = new ArrayList<>();
    ArrayList<String> arrayListYear = new ArrayList<>();
    PreferencesManager pref;
    User currentUser;

    private LinearLayout llLogout, llAlUsers,llAddTask,llAddUser,llAddSpecialReward;
    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
        //set up Toolbar
        pref=new PreferencesManager(this);
        currentUser=pref.getCurrentUser();
        Log.e(TAG, "onCreate: Prefe"+pref.getLang() );
        Toolbar toolbar = findViewById(R.id.toolbarAdminToolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.dashboard);
        //initialize views
        //text views
        llAddSpecialReward=findViewById(R.id.llAddSpecialReward);
        llAddUser=findViewById(R.id.llAddUser);
        llAddTask = findViewById(R.id.llAddTask);
        textViewAdminToday = findViewById(R.id.textViewAdminToday);
        textViewAdminYear = findViewById(R.id.textViewAdminYear);
        textViewAdminMonth = findViewById(R.id.textViewAdminMonth);
        llLogout = findViewById(R.id.llLogout);
        llAlUsers = findViewById(R.id.llAlUsers);
        llLogout.setOnClickListener(
                v -> new SweetAlertDialog(AdminDashboard.this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText("Are you sure")
                .setContentText("you want to log out")
                .setConfirmButton("Confirm", sweetAlertDialog -> {
                    sweetAlertDialog.dismissWithAnimation();
                    try {
                        FirebaseAuth.getInstance().signOut();
                        Log.e("TAG", "onClick: logout");
                    } catch (Exception e) {
                        Log.e("TAG", "onClick: error while login");
                        e.printStackTrace();
                    }
                    startActivity(new Intent(AdminDashboard.this, LogInActivity.class));
                    finish();
                })
                .setCancelButton("Cancel", sweetAlertDialog -> sweetAlertDialog.dismissWithAnimation()).show());
        llAddTask.setOnClickListener(v -> startActivity(new Intent(AdminDashboard.this, AssignTaskActivity.class)));
        llAlUsers.setOnClickListener(v -> startActivity(new Intent(AdminDashboard.this, AllUsersActivity.class)));
        llAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboard.this,AddUser.class));
            }
        });
        llAddSpecialReward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminDashboard.this,SpecialRewardAssignActivity.class));
            }
        });
        //Count All Users Details
        UsersCount();

    }
    private void UsersCount() {
        Log.e(TAG, "UsersCount: Runnign User Count");
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd");
        SimpleDateFormat sdfMonth = new SimpleDateFormat("MM");
        SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");

        String stringTodayDate = sdfDate.format(new Date());
        String stringTodayMonth = sdfMonth.format(new Date());
        String stringTodayYear = sdfYear.format(new Date());


        db.child("Users").orderByChild("createdBy").equalTo(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                for (DataSnapshot doc : snapshot.getChildren()) {
                    Log.e(TAG, "UsersCount: Running For Loop");
                    // if (doc.getType() == DocumentChange.Type.ADDED) {
                    User usersClass = doc.getValue(User.class);
                    String stringUserDate;
                    String stringUserMonth;
                    String stringUserYear;
                    // Note, MM is months, not mm
                    SimpleDateFormat outputFormat1 = new SimpleDateFormat("dd", Locale.US);
                    SimpleDateFormat outputFormat2 = new SimpleDateFormat("MM", Locale.US);
                    SimpleDateFormat outputFormat3 = new SimpleDateFormat("yyyy", Locale.US);
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

                    Date date = new Date();
                    Log.e(TAG, "UsersCount: Its Date On Whom Its Crashing" + usersClass.getDate());
                    try {
                        date = inputFormat.parse(usersClass.getDate());
                    } catch (ParseException parseException) {
                        parseException.printStackTrace();
                    }
                    stringUserDate = outputFormat1.format(date);
                    stringUserMonth = outputFormat2.format(date);
                    stringUserYear = outputFormat3.format(date);
                    Log.e(TAG, "onEvent: Date" + stringUserDate);
                    Log.e(TAG, "onEvent: Month" + stringUserMonth);
                    Log.e(TAG, "onEvent: Year" + stringUserYear);
                    if (stringUserYear.equals(stringTodayYear)) {
                        if (stringUserMonth.equals(stringTodayMonth)) {
                            if (stringUserDate.equals(stringTodayDate)) {
                                arrayListToday.add(stringUserDate);
                            }
                        }
                    }
                    if (stringUserYear.equals(stringTodayYear)) {
                        if (stringUserMonth.equals(stringTodayMonth)) {
                            arrayListMonth.add(stringUserMonth);
                        }
                    }
                    if (stringUserYear.equals(stringTodayYear)) {
                        arrayListYear.add(stringTodayYear);
                    }
                    //  }

                    textViewAdminToday.setText(String.valueOf(arrayListToday.size()));
                    textViewAdminMonth.setText(String.valueOf(arrayListMonth.size()));
                    textViewAdminYear.setText(String.valueOf(arrayListYear.size()));
                }

            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });
    }

}