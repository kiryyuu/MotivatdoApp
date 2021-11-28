package com.binmadhi.motivatdo.Admin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.annotations.NotNull;
import com.binmadhi.motivatdo.FirebaseHelper.PreferencesManager;
import com.binmadhi.motivatdo.Models.User;
import com.binmadhi.motivatdo.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {
    private ArrayList<User> arrayList;
    private Context context;
    private PreferencesManager pref;
    String panel;

    public UsersAdapter(Context context, ArrayList<User> arrayList, String panel) {
        this.arrayList = arrayList;
        this.context = context;
        this.panel = panel;
    }


    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_item_technician, parent, false);
        pref = new PreferencesManager(context);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        User user = arrayList.get(position);
        holder.tvName.setText(user.getName());
        holder.tvLocation.setText(user.getFamilyName());
        if (user.getImage().equals("null")) {
            holder.ivTechnician.setImageDrawable(context.getResources().getDrawable(R.drawable.default_profile));
        }
        else {
            Glide.with(context).load(user.getImage()).into(holder.ivTechnician);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,ProfileActivity.class);
                intent.putExtra("UID",user.getUid());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvLocation, tvReviewsCount;
        private CircleImageView ivTechnician;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            //tvReviewsCount = itemView.findViewById(R.id.tvReviewsCount);
            tvName = itemView.findViewById(R.id.tvName);
            ivTechnician = itemView.findViewById(R.id.ivTechnician);
            tvLocation = itemView.findViewById(R.id.tvLocation);
        }
    }
}
