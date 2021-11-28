package com.binmadhi.motivatdo.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.binmadhi.motivatdo.Activities.MainActivity;
import com.binmadhi.motivatdo.FirebaseHelper.FirebaseDatabaseHelper;
import com.binmadhi.motivatdo.FirebaseHelper.PreferencesManager;
import com.binmadhi.motivatdo.Models.User;
import com.binmadhi.motivatdo.R;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddUser extends AppCompatActivity implements FirebaseDatabaseHelper.OnLoginSignupAttemptCompleteListener {
    private static final String TAG = "TAG";
    private static final int PICK_PHOTO_FOR_AVATAR = 123;
    private Button btnRegister, btnSelectLocation;
    private CircleImageView ivUser;
    private EditText etNameUser, etFamilyNameUser, etEmailDriver, etPhoneNoDriver, etConfirmPassword, etPassword;
    private Uri mainImageUri = null;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private ProgressDialog progressDialog;
    private TextView tvLocation;
    private PreferencesManager preferencesManager;
    private DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    String type;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);


        try {
            //inflate views and assign ids
            initView();
            //initialize and set up progress Dialogue
            setupProgressDialog();
            preferencesManager=new PreferencesManager(this);
            currentUser=preferencesManager.getCurrentUser();
            btnRegister.setOnClickListener(v -> {
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                User model = new User();
                model.setName(etNameUser.getText().toString());
                model.setFamilyName(etFamilyNameUser.getText().toString());
                model.setEmail(etEmailDriver.getText().toString());
                model.setPhoneNo(etPhoneNoDriver.getText().toString());
                model.setType("user");
                model.setDate(date);
                model.setCreatedBy(currentUser.getUid());
                model.setPoints("0");

                if (validate(model)) {
                    progressDialog.show();
                    if (mainImageUri == null) {
                        firebaseDatabaseHelper.attemptSignUpWithOutImages(model, etPassword.getText().toString(), AddUser.this);
                    } else {
                        firebaseDatabaseHelper.attemptSignUp(model, etPassword.getText().toString(), mainImageUri, AddUser.this);
                    } }

            });
            ivUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(AddUser.this, Manifest.permission.READ_EXTERNAL_STORAGE) != getPackageManager().PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(AddUser.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                            if (ContextCompat.checkSelfPermission(AddUser.this, Manifest.permission.READ_EXTERNAL_STORAGE) == getPackageManager().PERMISSION_GRANTED) {
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

        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e.toString());
        }
    }

    //progress dialogue
    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(AddUser.this);
        progressDialog.setTitle("Creating Your Account");
        progressDialog.setMessage("Please Wait While We Setup Your Information");
        progressDialog.setCancelable(false);
    }

    private void attemptSignUp(User user, String password, Uri dpUri) {
        try {
            final StorageReference folderProfilePics = FirebaseStorage.getInstance().getReference().child("profile_image");
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(user.getEmail(), password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        user.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());

                        uploadFile(dpUri, folderProfilePics.child(user.getUid() + ".jpg"), new FirebaseDatabaseHelper.OnUploadFileCompleteListener() {
                            @Override
                            public void onUploadFileComplete(String url) {
                                user.setImage(url);

                                saveUserInfo(user, new FirebaseDatabaseHelper.OnSaveUserCompleteListener() {
                                    @Override
                                    public void onSaveUserComplete(boolean isSuccessful) {
                                        subToTopic(user.getUid());
                                        Toast.makeText(AddUser.this, getResources().getString(R.string.agent), Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            }
                        });
                    } else
                        Toast.makeText(AddUser.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "attemptSignUp: " + e.toString());
        }


    }

    //upload any type of file like pdf,image etc To Firebase Database
    private void saveUserInfo(User user, final FirebaseDatabaseHelper.OnSaveUserCompleteListener listener) {
        try {
            //firebase FireStore
            db.child("Users").child(user.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        listener.onSaveUserComplete(true);
                    } else {
                        listener.onSaveUserComplete(false);
                        Log.e(TAG, "onComplete: Failure FireStore" + task.getException());
                    }
                }
            });
            //
        } catch (Exception e) {
            Log.e(TAG, "saveUserInfo: " + e.toString());
            Toast.makeText(AddUser.this, "" + e.toString(), Toast.LENGTH_SHORT).show();

        }
    }

    private void uploadFile(Uri fileUri, final StorageReference path, final FirebaseDatabaseHelper.OnUploadFileCompleteListener listener) {

        path.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    Log.e(TAG, "onUploadFileComplete: Now uploadiong file");

                    path.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            listener.onUploadFileComplete(uri.toString());
                        }
                    });
                }
            }
        });
    }


    //inflate views like image,textView etc and assign id's
    private void initView() {
        try {
            firebaseDatabaseHelper = new FirebaseDatabaseHelper(this);
            preferencesManager = new PreferencesManager(this);
            btnRegister = findViewById(R.id.finish);
            etPhoneNoDriver = findViewById(R.id.etPhone);
            etEmailDriver = findViewById(R.id.etEmail);
            etNameUser = findViewById(R.id.etName);
            etFamilyNameUser = findViewById(R.id.etFName);
            ivUser = findViewById(R.id.ivUser);
            etPassword = findViewById(R.id.etPass);
            etConfirmPassword = findViewById(R.id.etConfirmPassword);


        } catch (Exception e) {
            Log.e(TAG, "initView: " + e.toString());
        }
    }

    //when  login & signUp is successful this function runs and move to main screen
    @Override
    public void onLoginSignupSuccess(User user) {
        progressDialog.dismiss();
        startActivity(new Intent(AddUser.this, MainActivity.class));
        finish();
        Toast.makeText(AddUser.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
    }

    //when  login & signUp is not successful this function runs it shows the  exact error message
    @Override
    public void onLoginSignupFailure(String failureMessage) {
        progressDialog.dismiss();
        Toast.makeText(this, "Failed to create account: " + failureMessage, Toast.LENGTH_SHORT).show();
    }

    //check the empty fields and shows the error message to fill the fields
    public boolean validate(User user) {
      /*  if (mainImageUri == null) {
            Toast.makeText(AddUser.this, "Please Choose Image First", Toast.LENGTH_SHORT).show();
            return false;
        }*/
        if (user.getName().isEmpty()) {
            etNameUser.setError("Must Fill Field");
            etNameUser.requestFocus();
            return false;
        }
        if (user.getFamilyName().isEmpty()) {
            etFamilyNameUser.setError("Must Fill Field");
            etFamilyNameUser.requestFocus();
            return false;
        }
        if (user.getEmail().isEmpty()) {
            etEmailDriver.setError("Must Fill Field");
            etEmailDriver.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(user.getEmail()).matches()) {
            etEmailDriver.setError("Must enter valid Email");
            etEmailDriver.requestFocus();
            return false;
        }
        if (user.getPhoneNo().isEmpty()) {
            user.setPhoneNo("1234567");
        }
        if (etPassword.getText().toString().isEmpty()) {
            etPassword.setError("Must Fill Field");
            etPassword.requestFocus();
            return false;
        }

        if (!etPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
            etPassword.setError("Plz Match the passwords");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    //receive the selected image by user and set on the Image View
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageUri = result.getUri();
                Glide.with(this).load(mainImageUri).into(ivUser);
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
                mainImageUri=uri;
                ivUser.setImageURI(uri);
                Log.e(TAG, "onActivityResult: " + uri);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error:"+e.toString(), Toast.LENGTH_SHORT).show();
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
                        .start(AddUser.this);

            }
        }
    }
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

}