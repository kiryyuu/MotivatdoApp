package com.binmadhi.motivatdo.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.binmadhi.motivatdo.FirebaseHelper.FirebaseDatabaseHelper;
import com.binmadhi.motivatdo.FirebaseHelper.PreferencesManager;
import com.binmadhi.motivatdo.Models.User;
import com.binmadhi.motivatdo.R;

import org.jetbrains.annotations.NotNull;

public class LogInActivity extends AppCompatActivity implements FirebaseDatabaseHelper.OnLoginSignupAttemptCompleteListener {
    private static final String TAG = "TAG";
    TextInputEditText etpassword;
    EditText etemail;
    Button tvLogin;
    TextView mforgerpassword, tvvLogin;
    ImageButton mCreateBtn;
    ProgressBar pbLogin;
    FirebaseDatabaseHelper firebaseDatabaseHelper;
    PreferencesManager pref;
    User currentUser;
    private final DatabaseReference tableUser = FirebaseDatabase.getInstance().getReference().child("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        try {
            firebaseDatabaseHelper = new FirebaseDatabaseHelper(this);
            pref = new PreferencesManager(this);
            currentUser = pref.getCurrentUser();
            //Initialize Views
            etemail = (EditText) findViewById(R.id.EmailLogin);
            etpassword = findViewById(R.id.PasswordLogin);
            tvvLogin = findViewById(R.id.tvLogin);
            tvLogin = (Button) findViewById(R.id.Login);
            mCreateBtn = (ImageButton) findViewById(R.id.SignUPtext);
            pbLogin = (ProgressBar) findViewById(R.id.progressBar1);
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            /*  if (currentUser.getType().equals("admin")) {
                    startActivity(new Intent(this, AdminDashboard.class));
                    Log.e(TAG, "onCreate: Redirecting to Admin Panel");
                    finish();
                } else {*/
                //Log.e(TAG, "onStart: Redirecting....");
                startActivity(new Intent(LogInActivity.this, MainActivity.class));
                LogInActivity.this.finish();
                // }
            }

            //Forget Password Click
            mforgerpassword = (TextView) findViewById(R.id.ForgetPassword);
            mforgerpassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Here we will send a verification message
                    startActivity(new Intent(LogInActivity.this, ForgetPassword.class));
                }
            });
            //create account
            mCreateBtn.setOnClickListener(v -> {
                startActivity(new Intent(LogInActivity.this, RegisterActivity.class));

            });
            //login Click
            tvLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pbLogin.setVisibility(View.VISIBLE);
                    tvLogin.setVisibility(View.GONE);
                    String email = etemail.getText().toString();
                    String password = etpassword.getText().toString();

                    if ((!TextUtils.isEmpty(email)) && (!TextUtils.isEmpty(password))) {
                        attemptLogin(email, password);
                    } else {
                        pbLogin.setVisibility(View.GONE);
                        tvLogin.setVisibility(View.VISIBLE);
                        Toast.makeText(LogInActivity.this, "Please fill username and password fields", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "onCreate:Login Activity exc  " + e.toString());
        }

    }

    //login success
    @Override
    public void onLoginSignupSuccess(User user) {
        Log.e(TAG, "onLoginSignupSuccess: Login Success");
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            pbLogin.setVisibility(View.GONE);
            startActivity(new Intent(LogInActivity.this, MainActivity.class));
            LogInActivity.this.finish();
            Log.e(TAG, "onLoginSignupSuccess: Sucess listenet redirecting");
            //tvLogin.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    //on login failure
    @Override
    public void onLoginSignupFailure(String failureMessage) {
        tvLogin.setVisibility(View.VISIBLE);
        pbLogin.setVisibility(View.GONE);
        Log.e(TAG, "onLoginSignupFailure: " + failureMessage.toLowerCase());
        Toast.makeText(this, "Login Failed: " + failureMessage, Toast.LENGTH_SHORT).show();
    }

    //Doing well here
    public void attemptLogin(String email, String password) {
        try {
            Log.e(TAG, "attemptLogin: Try to login");
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        etemail.setText("");
                        etpassword.setText("");
                        tableUser.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    User user = dataSnapshot.getValue(User.class);
                                    new PreferencesManager(LogInActivity.this).saveCurrentUser(user);
                                    pbLogin.setVisibility(View.GONE);
                                    Log.e(TAG, "onLoadUserInfoComplete:Redirecting ");
                                    startActivity(new Intent(LogInActivity.this, MainActivity.class));
                                    LogInActivity.this.finish();
                                    tableUser.removeEventListener(this);
                                } else {
                                    Toast.makeText(LogInActivity.this, "There is No Data For Account", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });
                    /*addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e(TAG, "onCancelled: " + databaseError);
                            }
                        });*/

                    } else {
                        tvLogin.setVisibility(View.VISIBLE);
                        pbLogin.setVisibility(View.GONE);
                        Toast.makeText(LogInActivity.this, "Error: " + task.getException(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onComplete: " + task.getException());
                    }
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "attemptLogin: " + e.toString());
            Toast.makeText(this, "" + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    //load current user info  from database by User I'd
    private void loadUserInfo(String uid, final FirebaseDatabaseHelper.OnLoadUserInfoCompleteListener listener) {
        try {
        } catch (Exception e) {
            Log.e(TAG, "loadUserInfo: " + e.toString());
            Toast.makeText(this, "" + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: Back to Login");
    }
}