package com.binmadhi.motivatdo.Admin;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.binmadhi.motivatdo.Adapters.SpinnerrAdapter;
import com.binmadhi.motivatdo.FirebaseHelper.FirebaseDatabaseHelper;
import com.binmadhi.motivatdo.FirebaseHelper.PreferencesManager;
import com.binmadhi.motivatdo.Models.TaskModel;
import com.binmadhi.motivatdo.Models.User;
import com.binmadhi.motivatdo.R;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class AssignTaskActivity extends AppCompatActivity implements FirebaseDatabaseHelper.OnAssignTaskCompleteListener {
    private static final int PICK_PHOTO_FOR_AVATAR = 123;
    private User selected;
    private static final String TAG = "TAG";
    private Uri mainImageUri = null;
    private Spinner spAssignTo;
    private SpinnerAdapter adapter;
    private final DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    private final ArrayList<User> arrayList = new ArrayList<>();
    ProgressBar animLoading;
    private LinearLayout llButtons;
    TextView tvsurprise, tvSelectDate;
    private EditText etTaskName, etRewardName, etTaskPoints;
    TextView tvadd_pic;
    private ImageView ivAttachment, ivSelectedImage;
    CheckBox cbSurprise;
    Button btnCancel;
    Button btnAssign;
    FirebaseDatabaseHelper firebaseDatabaseHelper;
    private LinearLayout llPoints, llReward;
    RadioButton rbReward, rbPoints;
    private PreferencesManager pref;
    User currentUser;
    private String radioSelected="reward";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assign_task);
        try {
            //initialize View and Bind with ID's
            pref=new PreferencesManager(this);
            currentUser=pref.getCurrentUser();
            firebaseDatabaseHelper = new FirebaseDatabaseHelper(this);
            rbReward = findViewById(R.id.rbReward);
            llReward = findViewById(R.id.llReward);
            rbPoints = findViewById(R.id.rbPoints);
            llPoints = findViewById(R.id.llPoints);
            llButtons = findViewById(R.id.llButtons);
            etTaskPoints = findViewById(R.id.etTaskPoints);
            etRewardName = findViewById(R.id.etRewardName);
            ivSelectedImage = findViewById(R.id.ivSelectedImage);
            etTaskName = findViewById(R.id.etTaskName);
            tvSelectDate = findViewById(R.id.tvSelectDate);
            tvadd_pic = findViewById(R.id.tvadd_pic);
            tvsurprise = findViewById(R.id.tvsurprise);
            ivAttachment = findViewById(R.id.ivattachment);
            cbSurprise = findViewById(R.id.cbSurprise);
            btnAssign = findViewById(R.id.btn_assign);
            btnCancel = findViewById(R.id.btn_cancel);
            spAssignTo = findViewById(R.id.spAssignTo);
            animLoading = findViewById(R.id.pbLoading);
            //Support Toolbar
            Toolbar toolbarToolbar = findViewById(R.id.toolbarAssign);
            getSupportActionBar();
            setSupportActionBar(toolbarToolbar);
            Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.assign_task);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            toolbarToolbar.setNavigationOnClickListener(v -> finish());
            //End Of toolbar text

            //get User List For Spinner
            getUserListForSinner();
            btnAssign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(arrayList.isEmpty()){
                        Toast.makeText(AssignTaskActivity.this, "No members to assign task", Toast.LENGTH_SHORT).show();
                        return;    
                    }
                    TaskModel taskModel = new TaskModel();
                    String mGroupId = db.child("tasks").push().getKey();
                    taskModel.setTid(mGroupId);
                    taskModel.setTaskName(etTaskName.getText().toString());
                    taskModel.setRewardName(etRewardName.getText().toString());
                    taskModel.setAssignTo(selected.getUid());
                    taskModel.setDate(tvSelectDate.getText().toString());
                    taskModel.setStatus("Active");
                    taskModel.setType("normal");
                    taskModel.setRewardType(radioSelected);
                    taskModel.setPoints(etTaskPoints.getText().toString());
                    if (cbSurprise.isChecked()) {
                        taskModel.setSurprise("Surprise");
                    } else {
                        taskModel.setSurprise("Not Surprise");
                    }
                    if (validate(taskModel)) {
                        animLoading.setVisibility(View.VISIBLE);
                        llButtons.setVisibility(View.GONE);
                        if (mainImageUri == null) {
                            firebaseDatabaseHelper.assignTaskWithoutImage(taskModel, mainImageUri, AssignTaskActivity.this);

                        } else {
                            firebaseDatabaseHelper.assignTask(taskModel, mainImageUri, AssignTaskActivity.this);
                        }
                    }
                }
            });
            ivAttachment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(AssignTaskActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != getPackageManager().PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(AssignTaskActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                            if (ContextCompat.checkSelfPermission(AssignTaskActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == getPackageManager().PERMISSION_GRANTED) {
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                                intent.setType("image/*");
                                startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);

                            }
                        } else {
                            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent, PICK_PHOTO_FOR_AVATAR);
                        }
                    }

                }
            });
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            tvSelectDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDate();
                }
            });
            spAssignTo.setOnItemSelectedListener(
                    new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            //selected =(adapter.getItem(position),User.class);
                            selected = arrayList.get(position);
                            // Toast.makeText(AssignTaskActivity.this, "ID: " + adapter.getItem(position).getId(), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "onItemSelected: selected" + selected.getName());
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            Log.e(TAG, "onNothingSelected: Nothing is Selected");
                        }
                    }
            );
            rbPoints.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rbReward.setChecked(false);
                    llPoints.setVisibility(View.VISIBLE);
                    llReward.setVisibility(View.GONE);
                    radioSelected = "points";
                }
            });
            rbReward.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rbPoints.setChecked(false);
                    llPoints.setVisibility(View.GONE);
                    llReward.setVisibility(View.VISIBLE);
                    radioSelected = "reward";
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e.toString());
        }
    }

    private void getUserListForSinner() {
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
                    adapter = new SpinnerrAdapter(AssignTaskActivity.this, arrayList);
                    spAssignTo.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    animLoading.setVisibility(View.GONE);
                    Log.e(TAG, "onCancelled: Error" + error.toString());
                    //  Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "getUserListForSinner: " + e.toString());
        }
    }

    //show date dialogue
    public void showDate() {
        final Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd MMMM yyyy"; // your format
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());

                tvSelectDate.setText(sdf.format(myCalendar.getTime()));
            }

        };
        new DatePickerDialog(this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();

        /*final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> btnSelectDate.setText(year + ":" + (monthOfYear + 1) + ":" + dayOfMonth), mYear, mMonth, mDay);
        datePickerDialog.show();*/
    }

    //receive the selected image by user and set on the Image View
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageUri = result.getUri();
                Glide.with(this).load(mainImageUri).into(ivSelectedImage);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.e(TAG, "onActivityResult: " + error.toString());
            }
        }
        if (requestCode == PICK_PHOTO_FOR_AVATAR && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                Toast.makeText(this, "something went wrong Retry", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                Uri uri = data.getData();
                mainImageUri = uri;
                ivSelectedImage.setImageURI(uri);
                Log.e(TAG, "onActivityResult: " + uri);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
    }

    //show permission manager dialogue to ask for Run time permissions
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //when permission granted
                //call method
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(AssignTaskActivity.this);

            }
        }

    }

    //check the empty fields and shows the error message to fill the fields
    public boolean validate(TaskModel user) {
       /* if (mainImageUri == null) {
            Toast.makeText(AssignTaskActivity.this, "Please Choose Image First", Toast.LENGTH_SHORT).show();
            return false;
        }*/
        if (user.getTaskName().isEmpty()) {
            etTaskName.setError("Must Fill Field");
            etTaskName.requestFocus();
            return false;
        }
        if (radioSelected.equals("reward")) {
            user.setPoints("0");
            if (user.getRewardName().isEmpty()) {
                etRewardName.setError("Must Fill Field");
                etRewardName.requestFocus();
                return false;
            }
        }
        if (radioSelected.equals("points")) {
            user.setRewardName("NULL");
            if (user.getPoints().isEmpty()) {
                etTaskPoints.setError("Must Fill Field");
                etTaskPoints.requestFocus();
                return false;
            }
        }
        if (user.getDate().equals(getResources().getString(R.string.due_date_dd_mm_yyyy))) {
            tvSelectDate.setError("Must Fill Field");
            tvSelectDate.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    public void onAssignTaskCompleted(String isSuccessful) {
        animLoading.setVisibility(View.GONE);
        llButtons.setVisibility(View.VISIBLE);
        if (isSuccessful.equals("success")) {
            Toast.makeText(this, "Task Assigned", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error" + isSuccessful, Toast.LENGTH_SHORT).show();
        }
    }
}