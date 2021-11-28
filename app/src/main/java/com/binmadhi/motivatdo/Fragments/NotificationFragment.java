package com.binmadhi.motivatdo.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.binmadhi.motivatdo.Adapters.NotificationsAdapter;
import com.binmadhi.motivatdo.FirebaseHelper.FirebaseDatabaseHelper;
import com.binmadhi.motivatdo.FirebaseHelper.PreferencesManager;
import com.binmadhi.motivatdo.Models.NotificationModel;
import com.binmadhi.motivatdo.Models.User;
import com.binmadhi.motivatdo.R;

import java.util.ArrayList;


public class NotificationFragment extends Fragment {
    private RecyclerView rvTechnicianList;
    private NotificationsAdapter adapter;
    private ArrayList<NotificationModel> arrayList;
    private Toolbar toolbarToolbar;
    private LinearLayout llNoDataFound;
    private LottieAnimationView animLoading;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private DatabaseReference tableNotifications = FirebaseDatabase.getInstance().getReference();
    private PreferencesManager pref;
    String type;
    private User currentUser;
    public static final String TAG = "TAG";

    public static NotificationFragment newInstance() {
        return new NotificationFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        try {
            //initialize views and assign id's
            pref = new PreferencesManager(getContext());
            currentUser = pref.getCurrentUser();
            arrayList = new ArrayList<>();
            animLoading = view.findViewById(R.id.animLoading);
            llNoDataFound = view.findViewById(R.id.llNoDataFound);
            rvTechnicianList = view.findViewById(R.id.rvTechnicianList);

            rvTechnicianList.setHasFixedSize(true);
            rvTechnicianList.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new NotificationsAdapter(getContext(), arrayList);
            rvTechnicianList.setAdapter(adapter);
            //get Technician List Actvity
            getNotifications();
        } catch (Exception e) {
            Log.e(TAG, "onCreate: TechnicianListActivity: EXC " + e.toString());
        }
        return view;
    }

    private void getNotifications() {
        try {
            animLoading.setVisibility(View.VISIBLE);
            tableNotifications.child("notifications").orderByChild("uid").equalTo(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    animLoading.setVisibility(View.GONE);
                    ArrayList<NotificationModel> arrayList1 = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        final NotificationModel notificationModel = snapshot.getValue(NotificationModel.class);
                        arrayList1.add(notificationModel);
                    }
                    arrayList.addAll(arrayList1);
                    arrayList1.clear();
                    adapter.notifyDataSetChanged();
                    if(arrayList.isEmpty()){
                        llNoDataFound.setVisibility(View.VISIBLE);
                    }else{
                        llNoDataFound.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    animLoading.setVisibility(View.GONE);

                    Log.e(TAG, "onCancelled: Error"+ error.toString() );
                  //  Toast.makeText(getContext(), "", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "getNotifications: " + e.toString());
        }
    }

}