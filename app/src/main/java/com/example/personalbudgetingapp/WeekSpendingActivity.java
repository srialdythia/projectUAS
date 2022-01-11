package com.example.personalbudgetingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dewinjm.monthyearpicker.MonthYearPickerDialog;
import com.github.dewinjm.monthyearpicker.MonthYearPickerDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.MutableDateTime;
import org.joda.time.Weeks;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class WeekSpendingActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView totalWeekAmountTv;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    private WeekSpendingAdapter weekSpendingAdapter;
    private List<Data> myDataLIst;

    private FirebaseAuth mAuth;
    private String onlineUserId = "";
    private DatabaseReference expensesRef;
    private Button btnSMonth;
    private String ySelected, mSelected;
    private DateTime specificDateSelected;

    private String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_spending);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        totalWeekAmountTv = findViewById(R.id.totalWeekAmountTv);
        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();
        expensesRef = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);

        myDataLIst = new ArrayList<>();
        weekSpendingAdapter = new WeekSpendingAdapter(WeekSpendingActivity.this,myDataLIst);
        recyclerView.setAdapter(weekSpendingAdapter);

        if(getIntent().getExtras()!=null){
            type = getIntent().getStringExtra("type");
            if(type.equals("week")){
                getSupportActionBar().setTitle("Week Spending");
                readWeekSpendingItems();
            }else if (type.equals("month")){
                getSupportActionBar().setTitle("Month Spending");

            }
        }
        
        btnSMonth = findViewById(R.id.btn_sMonth);
        btnSMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int yearSelected;
                int monthSelected;

                //Set default values
                Calendar calendar = Calendar.getInstance();
                yearSelected = calendar.get(Calendar.YEAR);
                monthSelected = calendar.get(Calendar.MONTH);

                MonthYearPickerDialogFragment dialogFragment = MonthYearPickerDialogFragment
                        .getInstance(monthSelected, yearSelected);

                dialogFragment.show(getSupportFragmentManager(), null);
                dialogFragment.setOnDateSetListener(new MonthYearPickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(int year, int monthOfYear) {
                        // do something
                        ySelected = String.valueOf(year);
                        mSelected = String.valueOf(monthOfYear+1);
                        specificDateSelected = new DateTime(ySelected+"-"+mSelected+"-"+"2");
                        Toast.makeText(getApplicationContext(), specificDateSelected.toString(), Toast.LENGTH_SHORT).show();

                        TextView tvPeriode = findViewById(R.id.tvPeriode);

                        tvPeriode.setText("Periode: " + mSelected + "/" + ySelected);
                        readMonthSpendingItems();
                    }
                });
            }
        });

    }

    private void readMonthSpendingItems() {

        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        Months months = Months.monthsBetween(epoch,specificDateSelected);

        Log.d("months", String.valueOf(months.getMonths()));

        expensesRef = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = expensesRef.orderByChild("month").equalTo(months.getMonths());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myDataLIst.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Data data = dataSnapshot.getValue(Data.class);
                    myDataLIst.add(data);
                }

                weekSpendingAdapter.notifyDataSetChanged();;
                progressBar.setVisibility(View.GONE);

                int totalAmount = 0;
                for(DataSnapshot ds: snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;

                    totalWeekAmountTv.setText("Total Month's Spending: $" + totalAmount);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readWeekSpendingItems() {

        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0);
        DateTime now = new DateTime();
        Weeks weeks = Weeks.weeksBetween(epoch,now);

        expensesRef = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = expensesRef.orderByChild("week").equalTo(weeks.getWeeks());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myDataLIst.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Data data = dataSnapshot.getValue(Data.class);
                    myDataLIst.add(data);
                }

                weekSpendingAdapter.notifyDataSetChanged();;
                progressBar.setVisibility(View.GONE);

                int totalAmount = 0;
                for(DataSnapshot ds: snapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;

                    totalWeekAmountTv.setText("Total Month's Spending: $" + totalAmount);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}