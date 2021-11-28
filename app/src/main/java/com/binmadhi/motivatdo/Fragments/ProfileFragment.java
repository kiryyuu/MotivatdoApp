package com.binmadhi.motivatdo.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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

import org.jetbrains.annotations.NotNull;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {
    private static final String TAG = "TAG";
    private CardView cvLogout;
    private TextView tvNameProfile,tvEmailUser,tvPoints,tvFamilyName,tvContactNo;
    CircleImageView ivStdProfile;
    PreferencesManager pref;
    DatabaseReference tableUsers;
    User currentUser;
    public ProfileFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        //initialize views
        tableUsers = FirebaseDatabase.getInstance().getReference().child("Users");
        pref=new PreferencesManager(getContext());
        currentUser=pref.getCurrentUser();
        tvContactNo = view.findViewById(R.id.tvContactNo);
        tvFamilyName = view.findViewById(R.id.tvFamilyName);
        tvEmailUser = view.findViewById(R.id.tvEmailUser);
        tvPoints = view.findViewById(R.id.tvPoints);
        tvNameProfile = view.findViewById(R.id.tvNameProfile);
        cvLogout = view.findViewById(R.id.cvLogout);
        ivStdProfile=view.findViewById(R.id.ivStdProfile);
        //logout button
        cvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SweetAlertDialog(getContext(), SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                        .setTitleText("Are you sure")
                        .setContentText("you want to log out")
                        .setConfirmButton("Confirm", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                                try {
                                    FirebaseAuth.getInstance().signOut();
                                    startActivity(new Intent(getContext(), LogInActivity.class));
                                    getActivity().finish();
                                    Log.e("TAG", "onClick: logout");
                                } catch (Exception e) {
                                    Log.e("TAG", "onClick: error while login");
                                    e.printStackTrace();
                                }

                            }
                        })
                        .setCancelButton("Cancel", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        }).show();

            }
        });
        if (currentUser.getImage().equals("null")) {
            ivStdProfile.setImageDrawable(getResources().getDrawable(R.drawable.default_profile));
        }
        else {
            Glide.with(getContext()).load(currentUser.getImage()).into(ivStdProfile);
        }
        tvNameProfile.setText(currentUser.getName());
        tvFamilyName.setText(currentUser.getFamilyName());
        tvContactNo.setText(currentUser.getPhoneNo());
        tvEmailUser.setText(currentUser.getEmail());
        updatePoints(currentUser.getUid());
        return view;
    }

    private void updatePoints(String uid) {
        try{
            tableUsers.child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    User user=snapshot.getValue(User.class);
                    pref.saveCurrentUser(user);
                    assert user != null;
                    tvPoints.setText(user.getPoints());
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    Log.e(TAG, "onCancelled: "+ error.toString() );
                }
            });
        }catch (Exception e){
            Log.e(TAG, "updatePoints: "+e.toString() );
        }
    }

}