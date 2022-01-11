package com.example.personalbudgetingapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class WeekSpendingAdapter extends RecyclerView.Adapter<WeekSpendingAdapter.ViewHolder>{
    private Context mContext;
    private List<Data> myDataList;

    public WeekSpendingAdapter(Context mContext, List<Data> myDataList) {
        this.mContext = mContext;
        this.myDataList = myDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.retrieve_layout,parent,false);
        return new WeekSpendingAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Data data = myDataList.get(position);

        Drawable backg;

        holder.amount.setText("$ " + data.getAmount());
        holder.date.setText("On " + data.getDate());
        holder.notes.setText("" + data.getNotes());

        switch (data.getItem()){
            case "Transport":
                holder.imageView.setImageResource(R.drawable.ic_transport);
                backg = mContext.getResources().getDrawable(R.drawable.bg_transport);
                holder.frameLayout.setBackground(backg);
                break;
            case "Food":
                holder.imageView.setImageResource(R.drawable.ic_food);
                backg = mContext.getResources().getDrawable(R.drawable.bg_food);
                holder.frameLayout.setBackground(backg);
                break;
            case "House":
                holder.imageView.setImageResource(R.drawable.ic_house);
                backg = mContext.getResources().getDrawable(R.drawable.bg_house);
                holder.frameLayout.setBackground(backg);
                break;
            case "Entertainment":
                holder.imageView.setImageResource(R.drawable.ic_entertainment);
                backg = mContext.getResources().getDrawable(R.drawable.bg_enter);
                holder.frameLayout.setBackground(backg);
                break;
            case "Education":
                holder.imageView.setImageResource(R.drawable.ic_education);
                backg = mContext.getResources().getDrawable(R.drawable.bg_education);
                holder.frameLayout.setBackground(backg);
                break;
            case "Charity":
                holder.imageView.setImageResource(R.drawable.ic_consultancy);
                backg = mContext.getResources().getDrawable(R.drawable.bg_charity);
                holder.frameLayout.setBackground(backg);
                break;
            case "Apparel":
                holder.imageView.setImageResource(R.drawable.ic_shirt);
                backg = mContext.getResources().getDrawable(R.drawable.bg_apparel);
                holder.frameLayout.setBackground(backg);
                break;
            case "Personal":
                holder.imageView.setImageResource(R.drawable.ic_personalcare);
                backg = mContext.getResources().getDrawable(R.drawable.bg_personal);
                holder.frameLayout.setBackground(backg);
                break;
            case "Other":
                holder.imageView.setImageResource(R.drawable.ic_other);
                backg = mContext.getResources().getDrawable(R.drawable.bg_other);
                holder.frameLayout.setBackground(backg);
                break;
            case "Health":
                holder.imageView.setImageResource(R.drawable.ic_health);
                backg = mContext.getResources().getDrawable(R.drawable.bg_health);
                holder.frameLayout.setBackground(backg);
        }
    }

    @Override
    public int getItemCount() {
        return myDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView item,amount,date,notes;
        public ImageView imageView;
        public FrameLayout frameLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item =itemView.findViewById(R.id.item);
            amount =itemView.findViewById(R.id.amount);
            date =itemView.findViewById(R.id.date);
            notes =itemView.findViewById(R.id.note);
            imageView = itemView.findViewById(R.id.imageView);
            frameLayout = itemView.findViewById(R.id.frameLayout);
        }

    }
}
