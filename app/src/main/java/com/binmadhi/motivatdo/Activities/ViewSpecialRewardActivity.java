package com.binmadhi.motivatdo.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.binmadhi.motivatdo.Models.TaskModel;
import com.binmadhi.motivatdo.Models.User;
import com.binmadhi.motivatdo.R;

import org.jetbrains.annotations.NotNull;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ViewSpecialRewardActivity extends AppCompatActivity {
    private static final String TAG = "TAG";
    private TextView tvEmailUser, tvPoints, tvFamilyName, tvContactNo;
    // CircleImageView ivStdProfile;
    private CardView cvCompleteTask;
    String tid;
    private DatabaseReference tableTasks, tableUsers;
    private Toolbar toolbarToolbar;
    private ProgressDialog progressDialog;
    TaskModel taskModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_special_reward);
        try {
            setupProgressDialog();
            //initialize views
            tableTasks = FirebaseDatabase.getInstance().getReference().child("tasks");
            tableUsers = FirebaseDatabase.getInstance().getReference().child("Users");
            //Support Toolbar
            toolbarToolbar = findViewById(R.id.toolbarToolbar);
            getSupportActionBar();
            setSupportActionBar(toolbarToolbar);
            // Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.amount_payable);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbarToolbar.setNavigationOnClickListener(v -> finish());
            //End Of toolbar text
            tid = getIntent().getStringExtra("OD");
            cvCompleteTask = findViewById(R.id.cvLogout);
            tvContactNo = findViewById(R.id.tvContactNo);
            tvFamilyName = findViewById(R.id.tvFamilyName);
            tvPoints = findViewById(R.id.tvPoints);
            tvEmailUser = findViewById(R.id.tvEmailUser);
            // ivStdProfile = findViewById(R.id.ivStdProfile);
            getTaskDetails(tid);
            cvCompleteTask.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (taskModel.getStatus().equals("Completed")) {
                        Toast.makeText(ViewSpecialRewardActivity.this, "Task Completed Already", Toast.LENGTH_SHORT).show();
                    } else {
                        new SweetAlertDialog(ViewSpecialRewardActivity.this, SweetAlertDialog.BUTTON_POSITIVE)
                                .setTitleText("Are you sure")
                                .setContentText("you have completed your task?")
                                .setConfirmButton("Confirm", new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();
                                        progressDialog.show();
                                        taskModel.setStatus("Completed");
                                        tableTasks.child(tid).setValue(taskModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    tableUsers.child(taskModel.getAssignTo()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                            User user = snapshot.getValue(User.class);
                                                            double points = Double.valueOf(user.getPoints());
                                                            double plus = Double.valueOf(taskModel.getDate());
                                                            double total=points-plus;
                                                            user.setPoints(String.valueOf(total));
                                                            //user.setPoints("10");
                                                            tableUsers.child(taskModel.getAssignTo()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                                    progressDialog.dismiss();
                                                                    if (task.isSuccessful()) {
                                                                        sweetAlertDialog.dismissWithAnimation();
                                                                        progressDialog.dismiss();
                                                                        openPrivacyPolicyUrl(taskModel.getRewardName());
                                                                    } else {
                                                                        Log.e(TAG, "onComplete: Else EXception" + task.getException());
                                                                        Toast.makeText(ViewSpecialRewardActivity.this, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();

                                                                    }
                                                                }
                                                            });

                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull @NotNull DatabaseError error) {
                                                            Log.e(TAG, "onCancelled: Database Error" + error.toString());
                                                        }
                                                    });

                                                }
                                            }
                                        });
                                    }
                                })
                                .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        sweetAlertDialog.dismissWithAnimation();
                                    }
                                }).show();

                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e.toString());
        }
    }

    private void getTaskDetails(String tid) {
        try {
            tableTasks.child(tid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    taskModel = snapshot.getValue(TaskModel.class);
                    //tvEmailUser.setText(taskModel.getTaskName());
                    tvContactNo.setText("Required points: " + taskModel.getDate());
                    if (taskModel.getRewardType().equals("points")) {
                        tvFamilyName.setText(taskModel.getPoints());
                    } else {
                        tvFamilyName.setText(taskModel.getRewardName());
                    }
                    //tvPoints.setText(taskModel.getPoints());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e(TAG, "onCancelled: " + error.toString());
                    Toast.makeText(ViewSpecialRewardActivity.this, "Error:" + error.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "getTaskDetails: " + e.toString());
        }
    }

    //progress dialogue
    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(ViewSpecialRewardActivity.this);
        progressDialog.setTitle("Completing Your Task");
        progressDialog.setMessage("Please Wait While We Setup Your Information");
        progressDialog.setCancelable(false);
    }

    private void openPrivacyPolicyUrl(String rewardName) {
        try {
            final Dialog dialog = new Dialog(ViewSpecialRewardActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.reward_layout);

            Button dialogButton = (Button) dialog.findViewById(R.id.btnOk);
            TextView tv = (TextView) dialog.findViewById(R.id.tvRewardName);
            tv.setText("You earned " + rewardName);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // dialog.dismiss();
                    finish();
                }
            });
            dialog.show();

        } catch (Exception e) {
            Log.e(TAG, "openPrivacyPolicyUrl: EXC " + e.toString());
        }
    }

}