package com.binmadhi.motivatdo.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.binmadhi.motivatdo.Models.User;
import com.binmadhi.motivatdo.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

public class SpinnerrAdapter extends ArrayAdapter<User> {
    private Context context;

    public SpinnerrAdapter(Context context, ArrayList<User> arrayList1) {
        super(context,0, arrayList1);
        this.context=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent) {
        View view;
        try {
            view = convertView;
            if (null == convertView) {
                view = LayoutInflater.from(context).inflate(R.layout.single_item_spinner,
                        parent, false);
            }
            User item = getItem(position);
            ((TextView) view.findViewById(R.id.tvName)).setText(item.getName());
            ((TextView) view.findViewById(R.id.tvFamilyName)).setText(item.getFamilyName());
            CircleImageView ivUser=view.findViewById(R.id.ivSpinner);
            Glide.with(context).load(item.getImage()).into(ivUser);
            return view;

        } catch (Exception ex) {
            Log.e(TAG, "createItemView: EXP : " + ex.toString());
            return null;
        }
    }

}
