package com.example.personalbudgetingapp;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class TodayItemAdapter extends RecyclerView.Adapter<TodayItemAdapter.ViewHolder> {

//    import android.content.Context;
    private Context mContext;
    private List<Data> myDataList;
    private String post_key = "";
    private String item = "";
    private String note = "";
    private int amount = 0;
    private String itemDate = "";
    private String sDay, sMonth, sYear;

    private DateTime specificDateSelected;

    public TodayItemAdapter(Context mContext, List<Data> myDataList) {
        this.mContext = mContext;
        this.myDataList = myDataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.retrieve_layout,parent,false);
        return new TodayItemAdapter.ViewHolder(view);
    }

//    Set the value to ViewHolder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Data data = myDataList.get(position);
        Drawable backg;

        Log.d("TodayItemAdapter", String.valueOf(data.getItem()));
        holder.amount.setText("$" + data.getAmount());
        holder.date.setText("On " + data.getDate());
        holder.notes.setText("Note: " + data.getNotes());

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

        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                post_key = data.getId();
                item = data.getItem();
                amount = data.getAmount();
                note = data.getNotes();
                itemDate = data.getDate();
                updateData();
            }
        });

    }

    private void updateData() {

        AlertDialog.Builder myDialog = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View mView = inflater.inflate(R.layout.update_layout,null);

        myDialog.setView(mView);
        final AlertDialog dialog = myDialog.create();

        final TextView mItem = mView.findViewById(R.id.itemName);
        final EditText mAmount = mView.findViewById(R.id.amount);
        final EditText mNotes = mView.findViewById(R.id.note);
        final TextView mPeriode = mView.findViewById(R.id.tvPeriode);
        String[] arr = itemDate.split("-");
        sDay = arr[0];
        sMonth = arr[1];
        sYear = arr[2];

        mPeriode.setText("Periode: " + itemDate);
        mPeriode.setVisibility(View.VISIBLE);

        mItem.setText(item);

        mAmount.setText(String.valueOf(amount));
        mAmount.setSelection(String.valueOf(amount).length());

        mNotes.setText(note);
        mNotes.setSelection(note.length());

        Button btnDel = mView.findViewById(R.id.btnDelete);
        Button btnUpdate = mView.findViewById(R.id.btnUpdate);

        btnUpdate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                amount = Integer.parseInt(mAmount.getText().toString());
                note = mNotes.getText().toString();


                specificDateSelected = new DateTime(sYear+"-"+sMonth+"-"+sDay);
                String date = sDay+"-"+sMonth+"-"+sYear;

                MutableDateTime epoch = new MutableDateTime();
                epoch.setDate(0);

                Months months = Months.monthsBetween(epoch,specificDateSelected);
                Weeks weeks = Weeks.weeksBetween(epoch,specificDateSelected);

                String itemDay = "";
                String itemWeek = "";
                String itemMonth = "";
                Integer getMonth ,getWeek;

                if(sDay.equals("1")){
                    itemDay = item+date;
                    itemWeek = item+(weeks.getWeeks()+1);
                    itemMonth = item+(months.getMonths()+1);
                    getMonth = months.getMonths()+1;
                    getWeek = weeks.getWeeks()+1;
                } else {
                    itemDay = item+date;
                    itemWeek = item+weeks.getWeeks();
                    itemMonth = item+months.getMonths();
                    getMonth = months.getMonths();
                    getWeek = weeks.getWeeks();
                }

                Data data = new Data(item, date, post_key, itemDay, itemWeek, itemMonth, amount,getMonth,getWeek,note);

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                reference.child(post_key).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(mContext, "Updated successfully", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(mContext, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                dialog.dismiss();

            }
        });

        btnDel.setOnClickListener(new View.OnClickListener(){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            @Override
            public void onClick(View view) {
                reference.child(post_key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(mContext, "Deleted successfully", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(mContext, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                dialog.dismiss();
            }
        });

        dialog.show();
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
            frameLayout= itemView.findViewById(R.id.frameLayout);
        }

    }
}
