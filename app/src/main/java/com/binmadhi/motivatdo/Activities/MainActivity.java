package com.binmadhi.motivatdo.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.binmadhi.motivatdo.Adapters.ServiceAdapter;
import com.binmadhi.motivatdo.Admin.AdminDashboard;
import com.binmadhi.motivatdo.FirebaseHelper.FirebaseDatabaseHelper;
import com.binmadhi.motivatdo.FirebaseHelper.PreferencesManager;
import com.binmadhi.motivatdo.Fragments.NotificationFragment;
import com.binmadhi.motivatdo.Models.User;
import com.binmadhi.motivatdo.R;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "TAG";
    private User currentUser;
    private PreferencesManager prefs;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    public BottomNavigationView navigation;
    ViewPager viewPager;
    ServiceAdapter mViewPagerAdapter;
    //language
    String currentLanguage = "en", currentLang;
    DatabaseReference tableUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            tableUsers = FirebaseDatabase.getInstance().getReference().child("Users");
            // overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up);
            viewPager = findViewById(R.id.viewpager);
            //initialize view and assign id's
            navigation = findViewById(R.id.nav_view12);
            //get shared preference data of current User
            firebaseDatabaseHelper = new FirebaseDatabaseHelper(MainActivity.this);
            prefs = new PreferencesManager(this);
            currentUser = prefs.getCurrentUser();
            if (currentUser.getType().equals("admin")) {
                startActivity(new Intent(this, AdminDashboard.class));
                Log.e(TAG, "onCreate: Redirecting to Admin Panel");
                MainActivity.this.finish();
            }

            /* if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                Log.e(TAG, "onStart: Redirecting....");
                startActivity(new Intent(MainActivity.this, LogInActivity.class));
                finish();
            }*/
            //set badge on the notification
            int badgeCount = 0;
            if (badgeCount > 0) {
                navigation.getOrCreateBadge(R.id.nav_alerts).setNumber(badgeCount);
                //totalBudgetCount.setVisibility(View.VISIBLE);
                //totalBudgetCount.setText(""+badgeCount);
            } else {
                navigation.removeBadge(R.id.nav_alerts);
                //totalBudgetCount.setVisibility(View.GONE);
            }
            //End Of Badge
            //Bottom Navigation
            try {
                @SuppressLint("CutPasteId") BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view12);
                bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
            } catch (Exception e) {
                Log.e(TAG, "onCreate: Bottom EXP" + e.toString());
            }
            /*  if (currentUser.getType().equals("provider")) {
                navigation.getMenu().removeItem(R.id.nav_home);
            }*/
            mViewPagerAdapter = new ServiceAdapter(getSupportFragmentManager(), MainActivity.this);
            viewPager.setAdapter(mViewPagerAdapter);
            //end of Bottom Navigation
            //updatePreferencesLatLong();
            NotificationFragment fragment = new NotificationFragment();
            //
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {

                    switch (position) {
                        case 0:
                            navigation.getMenu().findItem(R.id.nav_home).setChecked(true);
                            break;
                        case 1:
                            navigation.getMenu().findItem(R.id.nav_alerts).setChecked(true);
                            break;
                        case 2:
                            navigation.getMenu().findItem(R.id.nav_setting).setChecked(true);
                            break;
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            //checkPoints();
            //openPrivacyPolicyUrl(1000);
        } catch (Exception e) {
            Log.e(TAG, "onCreate: MainActivity EXC " + e.toString());
        }
    }

    private void checkPoints() {
        try {
            tableUsers.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    assert user != null;
                    if (user.getCelebrate().equals("no")) {
                        float points = Float.valueOf(user.getPoints().toString());
                        if (points >= 1000) {
                            openPrivacyPolicyUrl(points, user);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    Log.e(TAG, "onCancelled: " + error.toString());
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "updatePoints: " + e.toString());
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
                //Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.nav_alerts:
                        viewPager.setCurrentItem(1);

                        break;
                    case R.id.nav_setting:
                        viewPager.setCurrentItem(2);

                        break;
                }
                return true;
            };

    public void subToTopic(String title) {
        Log.e(TAG, "subToTopic: " + title);
        title = title.replaceAll("\\s", "");
        FirebaseMessaging.getInstance().subscribeToTopic(title)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Log.e(TAG, "onComplete: success " + task.toString());
                        } else {
                            Log.e(TAG, "onComplete: EXP " + task.getException());
                        }
                        Log.d(TAG, task.toString());
                    }
                });

    }

    private void openPrivacyPolicyUrl(float points, User user) {
        try {
            user.setCelebrate("yes");
            final Dialog dialog = new Dialog(this, R.style.Theme_Dialog);
            dialog.setTitle("Terms & Conditions");
            Window window = dialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.success);

            Button dialogButton = (Button) dialog.findViewById(R.id.cvDone);
            TextView tv = (TextView) dialog.findViewById(R.id.tv);
            tv.setText(getResources().getString(R.string.success_message) + points);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tableUsers.child(currentUser.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                dialog.dismiss();
                            }
                        }
                    });
                }
            });
            dialog.show();

        } catch (Exception e) {
            Log.e(TAG, "openPrivacyPolicyUrl: " + e.toString());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }
}