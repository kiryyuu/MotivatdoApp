package com.binmadhi.motivatdo.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.binmadhi.motivatdo.FirebaseHelper.PreferencesManager;
import com.binmadhi.motivatdo.Fragments.HomeFragment;
import com.binmadhi.motivatdo.Fragments.NotificationFragment;
import com.binmadhi.motivatdo.Fragments.ProfileFragment;
import com.binmadhi.motivatdo.Activities.MainActivity;
import com.binmadhi.motivatdo.Models.User;

import java.util.ArrayList;
import java.util.List;

public class ServiceAdapter extends FragmentPagerAdapter {
    PreferencesManager preferencesManager;
    User currentUser;
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public ServiceAdapter(FragmentManager manager, MainActivity mainActivity) {
        super(manager);
        preferencesManager = new PreferencesManager(mainActivity);
        currentUser = preferencesManager.getCurrentUser();
        //if (currentUser.getType().equals("customer")) {
            mFragmentList.add(new HomeFragment());
            mFragmentTitleList.add("Home");
        //}
        mFragmentList.add(new NotificationFragment());
        mFragmentTitleList.add("Alerts");
        mFragmentList.add(new ProfileFragment());
        mFragmentTitleList.add("Profile");
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

}

