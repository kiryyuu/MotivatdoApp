package com.binmadhi.motivatdo.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.annotations.NotNull;
import com.binmadhi.motivatdo.Activities.ViewTaskActivity;
import com.binmadhi.motivatdo.Models.NotificationModel;
import com.binmadhi.motivatdo.R;

import java.util.ArrayList;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {
    private ArrayList<NotificationModel> arrayList;
    private Context context;


    public NotificationsAdapter(Context context, ArrayList<NotificationModel> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        NotificationModel model = arrayList.get(position);
        holder.tvName.setText(model.getNotificationTitle());
        holder.tvMessage.setText(model.getMessage());
        if (model.getImage().equals("null")) {
            holder.ivUserImage.setImageDrawable(context.getResources().getDrawable(R.drawable.app_icon));
        } else {
            Glide.with(context).load(model.getImage()).into(holder.ivUserImage);
        }
        /*
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderDetailsActivity.class);
            intent.putExtra("OD",model.getOrder_id());
            context.startActivity(intent);
        });
*/
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ViewTaskActivity.class);
            intent.putExtra("OD", model.getTask_id());
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvMessage, tvName;
        private ImageView ivUserImage;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            ivUserImage = itemView.findViewById(R.id.ivUserImage);
        }
    }
}
