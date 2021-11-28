package com.binmadhi.motivatdo.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.annotations.NotNull;
import com.binmadhi.motivatdo.Activities.ViewSpecialRewardActivity;
import com.binmadhi.motivatdo.Activities.ViewTaskActivity;
import com.binmadhi.motivatdo.FirebaseHelper.PreferencesManager;
import com.binmadhi.motivatdo.Models.TaskModel;
import com.binmadhi.motivatdo.Models.User;
import com.binmadhi.motivatdo.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private ArrayList<TaskModel> arrayList;
    private Context context;
    PreferencesManager preferencesManager;
    User currentUser;

    public TaskAdapter(Context context, ArrayList<TaskModel> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.signle_item_task
                , parent, false);
        preferencesManager = new PreferencesManager(context);
        currentUser = preferencesManager.getCurrentUser();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        TaskModel model = arrayList.get(position);
        holder.tvName.setText(model.getTaskName());
        boolean flag=true;
        if (model.getTaskName().equals("Special reward")) {
            float userPoints = Float.parseFloat(currentUser.getPoints());
            float requiredPoints = Float.parseFloat(model.getDate());
            holder.tvDate.setText("Required points: " + requiredPoints);
            if (userPoints >= requiredPoints) {
                flag =true;
                holder.tvStatus.setText("You are eligible for special reward");
            } else {
                flag=false;
                holder.tvStatus.setText("You are not eligible for special reward");
            }
        } else {
            holder.tvDate.setText("Due Date:" + model.getDate());
            holder.tvStatus.setText(model.getStatus());
        }
        if (model.getImage().equals("null")) {
            holder.ivTechnician.setImageDrawable(context.getResources().getDrawable(R.drawable.app_icon));
        } else {
            Glide.with(context).load(model.getImage()).into(holder.ivTechnician);
        }
        boolean finalFlag = flag;
        holder.itemView.setOnClickListener(v -> {
            if (model.getTaskName().equals("Special reward")) {
                if(finalFlag){
                    Intent intent = new Intent(context, ViewSpecialRewardActivity.class);
                    intent.putExtra("OD", model.getTid());
                    context.startActivity(intent);
                }else{
                    Toast.makeText(context, "You are not yet eligible", Toast.LENGTH_LONG).show();
                }
            } else {
                Intent intent = new Intent(context, ViewTaskActivity.class);
                intent.putExtra("OD", model.getTid());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName, tvDate, tvStatus;
        private CircleImageView ivTechnician;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            ivTechnician = itemView.findViewById(R.id.ivTechnician);
            tvName = itemView.findViewById(R.id.tvName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
