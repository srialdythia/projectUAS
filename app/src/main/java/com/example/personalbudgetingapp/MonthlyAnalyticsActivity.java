package com.example.personalbudgetingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.anychart.enums.Align;
import com.anychart.enums.LegendLayout;
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

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class MonthlyAnalyticsActivity extends AppCompatActivity {

    private Toolbar settingsToolbar;

    private FirebaseAuth mAuth;
    private String onlineUserId = "";
    private DatabaseReference expensesRef,personalRef;

    private TextView totalBudgetTextView,totalSpendingTextView, analyticsTransportAmount,analyticsFoodAmount,analyticsHouseExpensesAmount,analyticsEntertainmentAmount;
    private TextView analyticsEducationAmount,analyticsCharityAmount,analyticsApparelAmount,analyticsHealthAmount,analyticsPersonalExpensesAmount,analyticsOtherAmount, monthSpentAmount;

    private RelativeLayout linearLayoutFood,linearLayoutTransport,linearLayoutHouse,linearLayoutEntertainment,linearLayoutEducation;
    private RelativeLayout linearLayoutCharity,linearLayoutApparel,linearLayoutHealth,linearLayoutPersonalExp,linearLayoutOther, linearLayoutAnalysis;

    private AnyChartView anyChartView;
    private TextView progress_ratio_transport,progress_ratio_food,progress_ratio_house,progress_ratio_ent,progress_ratio_edu,progress_ratio_cha, progress_ratio_app,progress_ratio_hea,progress_ratio_per,progress_ratio_oth, monthRatioSpending;
    private TextView status_Image_house,status_Image_ent,status_Image_edu,status_Image_cha,status_Image_app,status_Image_hea,status_Image_per,status_Image_oth;
    private ImageView monthRatioSpending_Image;
    private TextView status_Image_transport,status_Image_food;
    private Button btnSelectMonth;
    private ScrollView scrollView;

    private String ySelected, mSelected;
    private DateTime specificDateSelected;
    private ProgressBar progressBarTrans, progressBarFood, progressBarEducation, progressBarCharity, progressBarApparel,
            progressBarHouse, progressBarEnter, progressBarOther, progressBarPersonal, progressBarHealth;

    private TextView saveTrans, saveFood, saveEducation, saveCharity, saveApparel, saveHouse, saveEnter, saveOther, savePersonal, saveHealth;

    private DatabaseReference budgetRef;
    public String s;
    double d;
    int num;

    Pie pie;

    DecimalFormat df = new DecimalFormat("#.#");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_analytics);

        settingsToolbar = findViewById(R.id.my_Feed_Toolbar);
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Month Analytics");

        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();
        expensesRef = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        personalRef = FirebaseDatabase.getInstance().getReference("personal").child(onlineUserId);
        budgetRef = FirebaseDatabase.getInstance().getReference().child("budget").child(onlineUserId);

        progressBarTrans = findViewById(R.id.progressBarTrans);
        progressBarFood = findViewById(R.id.progressBarFood);
        progressBarEducation = findViewById(R.id.progressBarEducation);
        progressBarCharity = findViewById(R.id.progressBarCharity);
        progressBarApparel = findViewById(R.id.progressBarApparel);
        progressBarHouse = findViewById(R.id.progressBarHouse);
        progressBarEnter = findViewById(R.id.progressBarEnter);
        progressBarOther = findViewById(R.id.progressBarOther);
        progressBarPersonal = findViewById(R.id.progressBarPersonal);
        progressBarHealth = findViewById(R.id.progressBarHealth);

        saveTrans = findViewById(R.id.saveTrans);
        saveFood = findViewById(R.id.saveFood);
        saveEducation = findViewById(R.id.saveEducation);
        saveCharity = findViewById(R.id.saveCharity);
        saveEnter = findViewById(R.id.saveEnter);
        saveApparel = findViewById(R.id.saveApparel);
        saveHouse = findViewById(R.id.saveHouse);
        saveOther = findViewById(R.id.saveOther);
        savePersonal = findViewById(R.id.savePersonal);
        saveHealth = findViewById(R.id.saveHealth);



        totalBudgetTextView = findViewById(R.id.totalBudgetTextView);
        totalSpendingTextView = findViewById(R.id.totalSpendingTextView);

        //general analytic



        monthSpentAmount = findViewById(R.id.monthSpentAmount);
        linearLayoutAnalysis = findViewById(R.id.linearLayoutAnalysis);
        monthRatioSpending = findViewById(R.id.monthRatioSpending);
        monthRatioSpending_Image = findViewById(R.id.monthRatioSpending_Image);

        analyticsTransportAmount = findViewById(R.id.analyticsTransportAmount);
        analyticsFoodAmount = findViewById(R.id.analyticsFoodAmount);
        analyticsHouseExpensesAmount = findViewById(R.id.analyticsHouseAmount);
        analyticsEntertainmentAmount = findViewById(R.id.analyticsEntertainmentAmount);
        analyticsEducationAmount = findViewById(R.id.analyticsEducationAmount);
        analyticsCharityAmount = findViewById(R.id.analyticsCharityAmount);
        analyticsApparelAmount = findViewById(R.id.analyticsApparelAmount);
        analyticsHealthAmount = findViewById(R.id.analyticsHealthAmount);
        analyticsPersonalExpensesAmount = findViewById(R.id.analyticsPersonalAmount);
        analyticsOtherAmount = findViewById(R.id.analyticsOtherAmount);

        //Relative layouts views
        linearLayoutTransport = findViewById(R.id.relativeLayoutTransport);
        linearLayoutFood = findViewById(R.id.relativeLayoutFood);
        linearLayoutHouse = findViewById(R.id.relativeLayoutHouse);
        linearLayoutEntertainment = findViewById(R.id.relativeLayoutEntertainment);
        linearLayoutEducation = findViewById(R.id.relativeLayoutEducation);
        linearLayoutCharity = findViewById(R.id.relativeLayoutCharity);
        linearLayoutApparel = findViewById(R.id.relativeLayoutApparel);
        linearLayoutHealth = findViewById(R.id.relativeLayoutHealth);
        linearLayoutPersonalExp = findViewById(R.id.relativeLayoutPersonal);
        linearLayoutOther = findViewById(R.id.relativeLayoutOther);

        //textviews
        progress_ratio_transport = findViewById(R.id.progress_ratio_transport);
        progress_ratio_food = findViewById(R.id.progress_ratio_food);
        progress_ratio_house = findViewById(R.id.progress_ratio_house);
        progress_ratio_ent = findViewById(R.id.progress_ratio_entertainment);
        progress_ratio_edu = findViewById(R.id.progress_ratio_education);
        progress_ratio_cha = findViewById(R.id.progress_ratio_charity);
        progress_ratio_app = findViewById(R.id.progress_ratio_apparel);
        progress_ratio_hea = findViewById(R.id.progress_ratio_health);
        progress_ratio_per = findViewById(R.id.progress_ratio_personal);
        progress_ratio_oth = findViewById(R.id.progress_ratio_other);

        //imageviews
        status_Image_transport = findViewById(R.id.transportStatus);
        status_Image_food = findViewById(R.id.foodStatus);
        status_Image_house = findViewById(R.id.houseStatus);
        status_Image_ent = findViewById(R.id.entertainmentStatus);
        status_Image_edu = findViewById(R.id.educationStatus);
        status_Image_cha = findViewById(R.id.charityStatus);
        status_Image_app = findViewById(R.id.apparelStatus);
        status_Image_hea = findViewById(R.id.healthStatus);
        status_Image_per = findViewById(R.id.personalStatus);
        status_Image_oth = findViewById(R.id.otherStatus);
        scrollView = findViewById(R.id.scrollView);


        //anyChartView
        pie = AnyChart.pie();
        anyChartView = findViewById(R.id.anyChartView);
        anyChartView.setChart(pie);

        btnSelectMonth = findViewById(R.id.btnSearchMonth);

        btnSelectMonth.setOnClickListener(new View.OnClickListener() {
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
                        scrollView.setVisibility(View.VISIBLE);

                        // do something
                        ySelected = String.valueOf(year);
                        mSelected = String.valueOf(monthOfYear+1);
                        specificDateSelected = new DateTime(ySelected+"-"+mSelected+"-"+"2");
                        Toast.makeText(getApplicationContext(), specificDateSelected.toString(), Toast.LENGTH_SHORT).show();

                        TextView tvPeriode = findViewById(R.id.tvPeriode);

                        tvPeriode.setText("Periode: " + mSelected + "/" + ySelected);
                        getBudgetAmount();
                        getTotalMonthsTransportExpense();
                        getTotalMonthsFoodExpense();
                        getTotalMonthsHouseExpense();
                        getTotalMonthsEntertainmentExpenses();
                        getTotalMonthsEducationExpenses();
                        getTotalMonthsCharityExpenses();
                        getTotalMonthsApparelExpenses();
                        getTotalMonthsHealthExpenses();
                        getTotalMonthsPersonalExpenses();
                        getTotalMonthsOtherExpenses();
                        getTotalMonthsSpending();

                        getMonthTransportBudgetRatios();
                        getMonthFoodBudgetRatios();
                        getMonthHouseBudgetRatios();
                        getMonthEntBudgetRatios();
                        getMonthEduBudgetRatios();
                        getMonthCharityBudgetRatios();
                        getMonthAppBudgetRatios();
                        getMonthHealthBudgetRatios();
                        getMonthPerBudgetRatios();
                        getMonthOtherBudgetRatios();


                        new java.util.Timer().schedule(
                                new java.util.TimerTask() {
                                    @Override
                                    public void run() {
                                        Log.d("Timer","10S");
                                        loadGraph();
                                        setStatusAndImageResource();
                                    }
                                },
                                2000
                        );

                    }
                });
            }
        });

    }

    private void getBudgetAmount() {

        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        Months months = Months.monthsBetween(epoch, specificDateSelected);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("budget").child(onlineUserId);
        Query query = reference.orderByChild("month").equalTo(months.getMonths());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0) {
                    int totalAmount = 0;
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;

                    }
                    personalRef.child("budget").setValue(totalAmount);
                    Log.d("totalBudget", String.valueOf(totalAmount));

                    totalBudgetTextView.setText("$"+ totalAmount);
                    anyChartView.setVisibility(View.VISIBLE);
                } else{
                    totalBudgetTextView.setText("$"+ 0);
                    anyChartView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//        budgetRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                if (snapshot.exists() && snapshot.getChildrenCount()>0){
//                    for (DataSnapshot ds :  snapshot.getChildren()){
//                        Map<String, Object> map = (Map<String, Object>)ds.getValue();
//                        Object total = map.get("amount");
//                        int pTotal = Integer.parseInt(String.valueOf(total));
//                        totalAmountBudget+=pTotal;
//                        budgetTv.setText("$ "+String.valueOf(totalAmountBudget));
//                    }
//                }else {
//                    totalAmountBudget=0;
//                    budgetTv.setText("$ "+String.valueOf(0));
//                    finalBudget = totalAmountBudget;
//                }
//                personalRef.child("budget").setValue(totalAmountBudget);
//                Log.d("totalAmountBudget",String.valueOf(totalAmountBudget));
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
    }

    private void getTotalMonthsTransportExpense() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        Months months = Months.monthsBetween(epoch, specificDateSelected);

        String itemMonth = "Transport"+months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemMonth").equalTo(itemMonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int totalAmount = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsTransportAmount.setText("Spent: $" + totalAmount);
                    }
                    linearLayoutTransport.setVisibility(View.VISIBLE);
                    personalRef.child("monthTrans").setValue(totalAmount);


                }
                else {
                    linearLayoutTransport.setVisibility(View.GONE);
                    personalRef.child("monthTrans").setValue(0);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MonthlyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void getTotalMonthsFoodExpense(){
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        Months months = Months.monthsBetween(epoch, specificDateSelected);

        String itemMonth = "Food"+months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemMonth").equalTo(itemMonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int totalAmount = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsFoodAmount.setText("Spent: $" + totalAmount);
                    }
                    personalRef.child("monthFood").setValue(totalAmount);
                    linearLayoutFood.setVisibility(View.VISIBLE);
                }else {
                    linearLayoutFood.setVisibility(View.GONE);
                    personalRef.child("monthFood").setValue(0);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MonthlyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getTotalMonthsHouseExpense(){
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        Months months = Months.monthsBetween(epoch, specificDateSelected);

        String itemMonth = "House"+months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemMonth").equalTo(itemMonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int totalAmount = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsHouseExpensesAmount.setText("Spent: $" + totalAmount);
                    }
                    personalRef.child("monthHouse").setValue(totalAmount);
                    linearLayoutHouse.setVisibility(View.VISIBLE);
                }else {
                    linearLayoutHouse.setVisibility(View.GONE);
                    personalRef.child("monthHouse").setValue(0);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MonthlyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getTotalMonthsEntertainmentExpenses(){
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        Months months = Months.monthsBetween(epoch, specificDateSelected);

        String itemMonth = "Entertainment"+months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemMonth").equalTo(itemMonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int totalAmount = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsEntertainmentAmount.setText("Spent: $" + totalAmount);
                    }
                    personalRef.child("monthEnt").setValue(totalAmount);
                    linearLayoutEntertainment.setVisibility(View.VISIBLE);
                }else {
                    linearLayoutEntertainment.setVisibility(View.GONE);
                    personalRef.child("monthEnt").setValue(0);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MonthlyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getTotalMonthsEducationExpenses(){
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        Months months = Months.monthsBetween(epoch, specificDateSelected);

        String itemMonth = "Education"+months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemMonth").equalTo(itemMonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int totalAmount = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsEducationAmount.setText("Spent: $" + totalAmount);
                    }
                    personalRef.child("monthEdu").setValue(totalAmount);
                    linearLayoutEducation.setVisibility(View.VISIBLE);
                }else {
                    linearLayoutEducation.setVisibility(View.GONE);
                    personalRef.child("monthEdu").setValue(0);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MonthlyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getTotalMonthsCharityExpenses(){
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        Months months = Months.monthsBetween(epoch, specificDateSelected);

        String itemMonth = "Charity"+months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemMonth").equalTo(itemMonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int totalAmount = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsCharityAmount.setText("Spent: $" + totalAmount);
                    }
                    personalRef.child("monthChar").setValue(totalAmount);
                    linearLayoutCharity.setVisibility(View.VISIBLE);
                }else {
                    linearLayoutCharity.setVisibility(View.GONE);
                    personalRef.child("monthChar").setValue(0);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MonthlyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getTotalMonthsApparelExpenses(){
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        Months months = Months.monthsBetween(epoch, specificDateSelected);

        String itemMonth = "Apparel"+months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemMonth").equalTo(itemMonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int totalAmount = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsApparelAmount.setText("Spent: $" + totalAmount);
                    }
                    personalRef.child("monthApp").setValue(totalAmount);
                    linearLayoutApparel.setVisibility(View.VISIBLE);
                }else {
                    linearLayoutApparel.setVisibility(View.GONE);
                    personalRef.child("monthApp").setValue(0);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MonthlyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getTotalMonthsHealthExpenses(){
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        Months months = Months.monthsBetween(epoch, specificDateSelected);

        String itemMonth = "Health"+months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemMonth").equalTo(itemMonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int totalAmount = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsHealthAmount.setText("Spent: $" + totalAmount);
                    }
                    personalRef.child("monthHea").setValue(totalAmount);
                    linearLayoutHealth.setVisibility(View.VISIBLE);
                }else {
                    linearLayoutHealth.setVisibility(View.GONE);
                    personalRef.child("monthHea").setValue(0);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MonthlyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getTotalMonthsPersonalExpenses(){
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        Months months = Months.monthsBetween(epoch, specificDateSelected);

        String itemMonth = "Personal"+months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemMonth").equalTo(itemMonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int totalAmount = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsPersonalExpensesAmount.setText("Spent: $" + totalAmount);
                    }
                    personalRef.child("monthPer").setValue(totalAmount);
                    linearLayoutPersonalExp.setVisibility(View.VISIBLE);
                }else {
                    linearLayoutPersonalExp.setVisibility(View.GONE);
                    personalRef.child("monthPer").setValue(0);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MonthlyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getTotalMonthsOtherExpenses(){
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        Months months = Months.monthsBetween(epoch, specificDateSelected);

        String itemMonth = "Other"+months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("itemMonth").equalTo(itemMonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int totalAmount = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount += pTotal;
                        analyticsOtherAmount.setText("Spent: $" + totalAmount);
                    }
                    personalRef.child("monthOther").setValue(totalAmount);
                    linearLayoutOther.setVisibility(View.VISIBLE);
                }else {
                    personalRef.child("monthOther").setValue(0);
                    linearLayoutOther.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MonthlyAnalyticsActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void getTotalMonthsSpending(){
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        Months months = Months.monthsBetween(epoch, specificDateSelected);
        final RelativeLayout rvSummary = findViewById(R.id.rvSummary);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("month").equalTo(months.getMonths());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists() && dataSnapshot.getChildrenCount()>0){
                    int totalAmount = 0;
                    for (DataSnapshot ds :  dataSnapshot.getChildren()){
                        Map<String, Object> map = (Map<String, Object>)ds.getValue();
                        Object total = map.get("amount");
                        int pTotal = Integer.parseInt(String.valueOf(total));
                        totalAmount+=pTotal;

                    }


                    totalSpendingTextView.setText("$"+ totalAmount);
                    monthSpentAmount.setText("Total Spent: $ "+totalAmount);
                    anyChartView.setVisibility(View.VISIBLE);
                }else {
                    totalSpendingTextView.setText("$"+ 0);
                    anyChartView.setVisibility(View.GONE);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getMonthTransportBudgetRatios(){
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        Months months = Months.monthsBetween(epoch, specificDateSelected);

        String itemMonth = "Transport"+months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("budget").child(onlineUserId);
        Query query = reference.orderByChild("itemMonth").equalTo(itemMonth);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int pTotal = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }

                    int dayTransRatio = pTotal/30;
                    int weekTransRatio = pTotal/4;
                    int monthTransRatio = pTotal;

                    personalRef.child("dayTransRatio").setValue(dayTransRatio);
                    personalRef.child("weekTransRatio").setValue(weekTransRatio);
                    personalRef.child("monthTransRatio").setValue(monthTransRatio);

                }else {
                    personalRef.child("dayTransRatio").setValue(0);
                    personalRef.child("weekTransRatio").setValue(0);
                    personalRef.child("monthTransRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getMonthFoodBudgetRatios() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        Months months = Months.monthsBetween(epoch, specificDateSelected);

        String itemMonth = "Food"+months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("budget").child(onlineUserId);
        Query query = reference.orderByChild("itemMonth").equalTo(itemMonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int pTotal = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }

                    int dayFoodRatio = pTotal/30;
                    int weekFoodRatio = pTotal/4;
                    int monthFoodRatio = pTotal;

                    personalRef.child("dayFoodRatio").setValue(dayFoodRatio);
                    personalRef.child("weekFoodRatio").setValue(weekFoodRatio);
                    personalRef.child("monthFoodRatio").setValue(monthFoodRatio);

                }else {
                    personalRef.child("dayFoodRatio").setValue(0);
                    personalRef.child("weekFoodRatio").setValue(0);
                    personalRef.child("monthFoodRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getMonthHouseBudgetRatios(){
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        Months months = Months.monthsBetween(epoch, specificDateSelected);

        String itemMonth = "House"+months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("budget").child(onlineUserId);
        Query query = reference.orderByChild("itemMonth").equalTo(itemMonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int pTotal = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }

                    int dayHouseRatio = pTotal/30;
                    int weekHouseRatio = pTotal/4;
                    int monthHouseRatio = pTotal;

                    personalRef.child("dayHouseRatio").setValue(dayHouseRatio);
                    personalRef.child("weekHouseRatio").setValue(weekHouseRatio);
                    personalRef.child("monthHouseRatio").setValue(monthHouseRatio);

                }else {
                    personalRef.child("dayHouseRatio").setValue(0);
                    personalRef.child("weekHouseRatio").setValue(0);
                    personalRef.child("monthHouseRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getMonthEntBudgetRatios(){
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        Months months = Months.monthsBetween(epoch, specificDateSelected);

        String itemMonth = "Entertainment"+months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("budget").child(onlineUserId);
        Query query = reference.orderByChild("itemMonth").equalTo(itemMonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int pTotal = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }

                    int dayEntRatio = pTotal/30;
                    int weekEntRatio = pTotal/4;
                    int monthEntRatio = pTotal;

                    personalRef.child("dayEntRatio").setValue(dayEntRatio);
                    personalRef.child("weekEntRatio").setValue(weekEntRatio);
                    personalRef.child("monthEntRatio").setValue(monthEntRatio);

                }else {
                    personalRef.child("dayEntRatio").setValue(0);
                    personalRef.child("weekEntRatio").setValue(0);
                    personalRef.child("monthEntRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getMonthEduBudgetRatios(){
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        Months months = Months.monthsBetween(epoch, specificDateSelected);

        String itemMonth = "Education"+months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("budget").child(onlineUserId);
        Query query = reference.orderByChild("itemMonth").equalTo(itemMonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int pTotal = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }

                    int dayEduRatio = pTotal/30;
                    int weekEduRatio = pTotal/4;
                    int monthEduRatio = pTotal;

                    personalRef.child("dayEduRatio").setValue(dayEduRatio);
                    personalRef.child("weekEduRatio").setValue(weekEduRatio);
                    personalRef.child("monthEduRatio").setValue(monthEduRatio);

                }else {
                    personalRef.child("dayEduRatio").setValue(0);
                    personalRef.child("weekEduRatio").setValue(0);
                    personalRef.child("monthEduRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getMonthCharityBudgetRatios(){
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        Months months = Months.monthsBetween(epoch, specificDateSelected);

        String itemMonth = "Charity"+months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("budget").child(onlineUserId);
        Query query = reference.orderByChild("itemMonth").equalTo(itemMonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int pTotal = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }

                    int dayCharRatio = pTotal/30;
                    int weekCharRatio = pTotal/4;
                    int monthCharRatio = pTotal;

                    personalRef.child("dayCharRatio").setValue(dayCharRatio);
                    personalRef.child("weekCharRatio").setValue(weekCharRatio);
                    personalRef.child("monthCharRatio").setValue(monthCharRatio);

                }else {
                    personalRef.child("dayCharRatio").setValue(0);
                    personalRef.child("weekCharRatio").setValue(0);
                    personalRef.child("monthCharRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getMonthAppBudgetRatios() {
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        Months months = Months.monthsBetween(epoch, specificDateSelected);

        String itemMonth = "Apparel"+months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("budget").child(onlineUserId);
        Query query = reference.orderByChild("itemMonth").equalTo(itemMonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int pTotal = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }

                    int dayAppRatio = pTotal/30;
                    int weekAppRatio = pTotal/4;
                    int monthAppRatio = pTotal;

                    personalRef.child("dayAppRatio").setValue(dayAppRatio);
                    personalRef.child("weekAppRatio").setValue(weekAppRatio);
                    personalRef.child("monthAppRatio").setValue(monthAppRatio);

                }else {
                    personalRef.child("dayAppRatio").setValue(0);
                    personalRef.child("weekAppRatio").setValue(0);
                    personalRef.child("monthAppRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getMonthHealthBudgetRatios(){
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        Months months = Months.monthsBetween(epoch, specificDateSelected);

        String itemMonth = "Health"+months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("budget").child(onlineUserId);
        Query query = reference.orderByChild("itemMonth").equalTo(itemMonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int pTotal = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }

                    int dayHealthRatio = pTotal/30;
                    int weekHealthRatio = pTotal/4;
                    int monthHealthRatio = pTotal;

                    personalRef.child("dayHealthRatio").setValue(dayHealthRatio);
                    personalRef.child("weekHealthRatio").setValue(weekHealthRatio);
                    personalRef.child("monthHealthRatio").setValue(monthHealthRatio);

                }else {
                    personalRef.child("dayHealthRatio").setValue(0);
                    personalRef.child("weekHealthRatio").setValue(0);
                    personalRef.child("monthHealthRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getMonthPerBudgetRatios(){
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        Months months = Months.monthsBetween(epoch, specificDateSelected);

        String itemMonth = "Personal"+months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("budget").child(onlineUserId);
        Query query = reference.orderByChild("itemMonth").equalTo(itemMonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int pTotal = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }

                    int dayPerRatio = pTotal/30;
                    int weekPerRatio = pTotal/4;
                    int monthPerRatio = pTotal;

                    personalRef.child("dayPerRatio").setValue(dayPerRatio);
                    personalRef.child("weekPerRatio").setValue(weekPerRatio);
                    personalRef.child("monthPerRatio").setValue(monthPerRatio);

                }else {
                    personalRef.child("dayPerRatio").setValue(0);
                    personalRef.child("weekPerRatio").setValue(0);
                    personalRef.child("monthPerRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void getMonthOtherBudgetRatios(){
        MutableDateTime epoch = new MutableDateTime();
        epoch.setDate(0); //Set to Epoch time
        Months months = Months.monthsBetween(epoch, specificDateSelected);

        String itemMonth = "Other"+months.getMonths();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("budget").child(onlineUserId);
        Query query = reference.orderByChild("itemMonth").equalTo(itemMonth);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    int pTotal = 0;
                    for (DataSnapshot ds :  snapshot.getChildren()) {
                        Map<String, Object> map = (Map<String, Object>) ds.getValue();
                        Object total = map.get("amount");
                        pTotal = Integer.parseInt(String.valueOf(total));
                    }

                    int dayOtherRatio = pTotal/30;
                    int weekOtherRatio = pTotal/4;
                    int monthOtherRatio = pTotal;

                    personalRef.child("dayOtherRatio").setValue(dayOtherRatio);
                    personalRef.child("weekOtherRatio").setValue(weekOtherRatio);
                    personalRef.child("monthOtherRatio").setValue(monthOtherRatio);

                }else {
                    personalRef.child("dayOtherRatio").setValue(0);
                    personalRef.child("weekOtherRatio").setValue(0);
                    personalRef.child("monthOtherRatio").setValue(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadGraph(){
        personalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){

                    int traTotal;
                    if (snapshot.hasChild("monthTrans")){
                        traTotal = Integer.parseInt(snapshot.child("monthTrans").getValue().toString());
                    }else {
                        traTotal = 0;
                    }

                    int foodTotal;
                    if (snapshot.hasChild("monthFood")){
                        foodTotal = Integer.parseInt(snapshot.child("monthFood").getValue().toString());
                    }else {
                        foodTotal = 0;
                    }

                    int houseTotal;
                    if (snapshot.hasChild("monthHouse")){
                        houseTotal = Integer.parseInt(snapshot.child("monthHouse").getValue().toString());
                    }else {
                        houseTotal = 0;
                    }

                    int entTotal;
                    if (snapshot.hasChild("monthEnt")){
                        entTotal = Integer.parseInt(snapshot.child("monthEnt").getValue().toString());
                    }else {
                        entTotal=0;
                    }

                    int eduTotal;
                    if (snapshot.hasChild("monthEdu")){
                        eduTotal = Integer.parseInt(snapshot.child("monthEdu").getValue().toString());
                    }else {
                        eduTotal = 0;
                    }

                    int chaTotal;
                    if (snapshot.hasChild("monthChar")){
                        chaTotal = Integer.parseInt(snapshot.child("monthChar").getValue().toString());
                    }else {
                        chaTotal = 0;
                    }

                    int appTotal;
                    if (snapshot.hasChild("monthApp")){
                        appTotal = Integer.parseInt(snapshot.child("monthApp").getValue().toString());
                    }else {
                        appTotal = 0;
                    }

                    int heaTotal;
                    if (snapshot.hasChild("monthHea")){
                        heaTotal = Integer.parseInt(snapshot.child("monthHea").getValue().toString());
                    }else {
                        heaTotal =0;
                    }

                    int perTotal;
                    if (snapshot.hasChild("monthPer")){
                        perTotal = Integer.parseInt(snapshot.child("monthPer").getValue().toString());
                    }else {
                        perTotal=0;
                    }
                    int othTotal;
                    if (snapshot.hasChild("monthOther")){
                        othTotal = Integer.parseInt(snapshot.child("monthOther").getValue().toString());
                    }else {
                        othTotal = 0;
                    }


                    List<DataEntry> data = new ArrayList<>();
                    data.add(new ValueDataEntry("Transport", traTotal));
                    data.add(new ValueDataEntry("House exp", houseTotal));
                    data.add(new ValueDataEntry("Food", foodTotal));
                    data.add(new ValueDataEntry("Entertainment", entTotal));
                    data.add(new ValueDataEntry("Education", eduTotal));
                    data.add(new ValueDataEntry("Charity", chaTotal));
                    data.add(new ValueDataEntry("Apparel", appTotal));
                    data.add(new ValueDataEntry("Health", heaTotal));
                    data.add(new ValueDataEntry("Personal", perTotal));
                    data.add(new ValueDataEntry("other", othTotal));
                    Log.d("tratotal", String.valueOf(traTotal));
                    Log.d("houseTotal", String.valueOf(houseTotal));
                    Log.d("chaTotal", String.valueOf(chaTotal));
                    Log.d("appTotal", String.valueOf(appTotal));
                    Log.d("heaTotal", String.valueOf(heaTotal));
                    Log.d("perTotal", String.valueOf(perTotal));
                    Log.d("othTotal", String.valueOf(othTotal));


                    pie.data(data);

                    pie.title("Month Analytics");

                    pie.labels().position("outside");

                    pie.legend().title().enabled(true);
                    pie.legend().title()
                            .text("Items Spent On")
                            .padding(0d, 0d, 10d, 0d);

                    pie.legend()
                            .position("center-bottom")
                            .itemsLayout(LegendLayout.HORIZONTAL)
                            .align(Align.CENTER);


                }
                else {
                    Toast.makeText(MonthlyAnalyticsActivity.this,"Child does not exist", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void setStatusAndImageResource(){
        personalRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    float traTotal;
                    if (snapshot.hasChild("monthTrans")){
                        traTotal = Integer.parseInt(snapshot.child("monthTrans").getValue().toString());
                    }else {
                        traTotal = 0;
                    }

                    float foodTotal;
                    if (snapshot.hasChild("monthFood")){
                        foodTotal = Integer.parseInt(snapshot.child("monthFood").getValue().toString());
                    }else {
                        foodTotal = 0;
                    }

                    float houseTotal;
                    if (snapshot.hasChild("monthHouse")){
                        houseTotal = Integer.parseInt(snapshot.child("monthHouse").getValue().toString());
                    }else {
                        houseTotal = 0;
                    }

                    float entTotal;
                    if (snapshot.hasChild("monthEnt")){
                        entTotal = Integer.parseInt(snapshot.child("monthEnt").getValue().toString());
                    }else {
                        entTotal=0;
                    }

                    float eduTotal;
                    if (snapshot.hasChild("monthEdu")){
                        eduTotal = Integer.parseInt(snapshot.child("monthEdu").getValue().toString());
                    }else {
                        eduTotal = 0;
                    }

                    float chaTotal;
                    if (snapshot.hasChild("monthChar")){
                        chaTotal = Integer.parseInt(snapshot.child("monthChar").getValue().toString());
                    }else {
                        chaTotal = 0;
                    }

                    float appTotal;
                    if (snapshot.hasChild("monthApp")){
                        appTotal = Integer.parseInt(snapshot.child("monthApp").getValue().toString());
                    }else {
                        appTotal = 0;
                    }

                    float heaTotal;
                    if (snapshot.hasChild("monthHea")){
                        heaTotal = Integer.parseInt(snapshot.child("monthHea").getValue().toString());
                    }else {
                        heaTotal =0;
                    }

                    float perTotal;
                    if (snapshot.hasChild("monthPer")){
                        perTotal = Integer.parseInt(snapshot.child("monthPer").getValue().toString());
                    }else {
                        perTotal=0;
                    }
                    float othTotal;
                    if (snapshot.hasChild("monthOther")){
                        othTotal = Integer.parseInt(snapshot.child("monthOther").getValue().toString());
                    }else {
                        othTotal = 0;
                    }

                    float monthTotalSpentAmount;
                    if (snapshot.hasChild("month")){
                        monthTotalSpentAmount = Integer.parseInt(snapshot.child("month").getValue().toString());
                    }else {
                        monthTotalSpentAmount = 0;
                    }


                    //GETTING RATIOS

                    float traRatio;
                    if (snapshot.hasChild("monthTransRatio")){
                        traRatio = Integer.parseInt(snapshot.child("monthTransRatio").getValue().toString());
                    }else {
                        traRatio=0;
                    }

                    float foodRatio;
                    if (snapshot.hasChild("monthFoodRatio")){
                        foodRatio = Integer.parseInt(snapshot.child("monthFoodRatio").getValue().toString());
                    }else {
                        foodRatio = 0;
                    }

                    float houseRatio;
                    if (snapshot.hasChild("monthHouseRatio")){
                        houseRatio = Integer.parseInt(snapshot.child("monthHouseRatio").getValue().toString());
                    }else {
                        houseRatio = 0;
                    }

                    float entRatio;
                    if (snapshot.hasChild("monthEntRatio")){
                        entRatio= Integer.parseInt(snapshot.child("monthEntRatio").getValue().toString());
                    }else {
                        entRatio = 0;
                    }

                    float eduRatio;
                    if (snapshot.hasChild("monthEduRatio")){
                        eduRatio= Integer.parseInt(snapshot.child("monthEduRatio").getValue().toString());
                    }else {
                        eduRatio=0;
                    }

                    float chaRatio;
                    if (snapshot.hasChild("monthCharRatio")){
                        chaRatio = Integer.parseInt(snapshot.child("monthCharRatio").getValue().toString());
                    }else {
                        chaRatio = 0;
                    }

                    float appRatio;
                    if (snapshot.hasChild("monthAppRatio")){
                        appRatio = Integer.parseInt(snapshot.child("monthAppRatio").getValue().toString());
                    }else {
                        appRatio =0;
                    }

                    float heaRatio;
                    if (snapshot.hasChild("monthHealthRatio")){
                        heaRatio = Integer.parseInt(snapshot.child("monthHealthRatio").getValue().toString());
                    }else {
                        heaRatio=0;
                    }

                    float perRatio;
                    if (snapshot.hasChild("monthPerRatio")){
                        perRatio = Integer.parseInt(snapshot.child("monthPerRatio").getValue().toString());
                    }else {
                        perRatio = 0;
                    }

                    float othRatio;
                    if (snapshot.hasChild("monthOtherRatio")){
                        othRatio = Integer.parseInt(snapshot.child("monthOtherRatio").getValue().toString());
                    }else {
                        othRatio=0;
                    }

                    float monthTotalSpentAmountRatio;
                    if (snapshot.hasChild("budget")){
                        monthTotalSpentAmountRatio = Integer.parseInt(snapshot.child("budget").getValue().toString());
                    }else {
                        monthTotalSpentAmountRatio =0;
                    }


                    float monthPercent = (monthTotalSpentAmount/monthTotalSpentAmountRatio)*100;
                    if (monthPercent<50){
                        monthRatioSpending.setText(df.format(monthPercent)+" %" +" used of $"+df.format(monthTotalSpentAmountRatio) + ". \nStatus:");
                        monthRatioSpending_Image.setImageResource(R.drawable.green);
                    }else if (monthPercent >= 50 && monthPercent <100){
                        monthRatioSpending.setText(df.format(monthPercent)+" %" +" used of $"+df.format(monthTotalSpentAmountRatio) + ". \nStatus:");
                        monthRatioSpending_Image.setImageResource(R.drawable.brown);
                    }else {
                        monthRatioSpending.setText(df.format(monthPercent)+" %" +" used of $"+df.format(monthTotalSpentAmountRatio) + ". \nStatus:");
                        monthRatioSpending_Image.setImageResource(R.drawable.red);

                    }


                    //START TRANSPORT
                    if(traRatio == 0){
                        saveTrans.setText("-$"+String.valueOf("$0"));
                        progressBarTrans.setProgress(Integer.parseInt("0"));
                        status_Image_transport.setTextSize(12);
                        status_Image_transport.setText("You've never set this item to your budget");

                    }else{
                        float transportPercent = (traTotal/traRatio)*100;
                        status_Image_transport.setText(df.format(transportPercent)+"%");
                        s = df.format(transportPercent);
                        d = Double.parseDouble(s);
                        num = (int) d;
                        progressBarTrans.setProgress(num);

                        if (transportPercent<50){
                            progressBarTrans.getProgressDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.low), PorterDuff.Mode.SRC_IN );
                        }
                        else if (transportPercent >= 50 && transportPercent <100){
                            progressBarTrans.getProgressDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.mid), PorterDuff.Mode.SRC_IN );
                        }else {
                            progressBarTrans.getProgressDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.high), PorterDuff.Mode.SRC_IN );
                        }

                        float transportSave = traRatio - traTotal;
                        if(transportSave > traTotal){
                            saveTrans.setText("-$"+String.valueOf(transportSave));
                            saveTrans.setTextColor(getResources().getColor(R.color.low));
                        }else if(transportSave == traTotal){
                            saveTrans.setText("$"+String.valueOf(transportSave));
                        }else if(transportSave < traTotal) {
                            saveTrans.setText("!$" + String.valueOf(transportSave));
                            saveTrans.setTextColor(getResources().getColor(R.color.high));
                        }
                    }
                    // END TRANSPORT

                    // START FOOD
                    if(foodRatio == 0){
                        saveFood.setText("-$"+String.valueOf("$0"));
                        progressBarFood.setProgress(Integer.parseInt("0"));
                        status_Image_food.setTextSize(12);
                        status_Image_food.setText("You've never set this item to your budget");
                    }
                    else{
                        float foodPercent = (foodTotal/foodRatio)*100;
                        status_Image_food.setText(df.format(foodPercent)+"%");
                        Log.d("MonthlyAnalyticsActivity",""+Integer.parseInt(df.format(foodPercent)));
                        s = df.format(foodPercent);
                        d = Double.parseDouble(s);
                        num = (int) d;
                        progressBarFood.setProgress(num);

                        if (foodPercent<50){
                            progressBarFood.getProgressDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.low), PorterDuff.Mode.SRC_IN );
                        }
                        else if (foodPercent >= 50 && foodPercent <100){
                            progressBarFood.getProgressDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.mid), PorterDuff.Mode.SRC_IN );
                        }else {
                            progressBarFood.getProgressDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.high), PorterDuff.Mode.SRC_IN );
                        }

                        float foodSave = foodRatio - foodTotal;
                        if(foodSave > foodTotal){
                            saveFood.setText("-$"+String.valueOf(foodSave));
                            saveFood.setTextColor(getResources().getColor(R.color.low));
                        }else if(foodSave == foodTotal){
                            saveFood.setText("$"+String.valueOf(foodSave));
                        }else if(foodSave < foodTotal){
                            saveFood.setText("!$"+String.valueOf(foodSave));
                            saveFood.setTextColor(getResources().getColor(R.color.high));
                        }
                    }
                    // END FOOD


                    // START HOUSE
                    if(houseRatio == 0){
                        saveHouse.setText("-$"+String.valueOf("0"));
                        progressBarHouse.setProgress(Integer.parseInt("0"));
                        status_Image_house.setTextSize(12);
                        status_Image_house.setText("You've never set this item to your budget");

                    }else{
                        float housePercent = (houseTotal/houseRatio)*100;
                        status_Image_house.setText(df.format(housePercent)+"%");
                        Log.d("MonthlyAnalyticsActivity",""+Integer.parseInt(df.format(housePercent)));
                        s = df.format(housePercent);
                        d = Double.parseDouble(s);
                        num = (int) d;
                        progressBarHouse.setProgress(num);

                        if (housePercent<50){
                            progressBarHouse.getProgressDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.low), PorterDuff.Mode.SRC_IN );
                        }
                        else if (housePercent >= 50 && housePercent <100){
                            progressBarHouse.getProgressDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.mid), PorterDuff.Mode.SRC_IN );
                        }else {
                            progressBarHouse.getProgressDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.high), PorterDuff.Mode.SRC_IN );
                        }

                        float houseSave = houseRatio - houseTotal;
                        if(houseSave > houseTotal){
                            saveHouse.setText("-$"+String.valueOf(houseSave));
                            saveHouse.setTextColor(getResources().getColor(R.color.low));
                        }else if(houseSave == houseTotal){
                            saveHouse.setText("$"+String.valueOf(houseSave));
                        }else if(houseSave < houseTotal) {
                            saveHouse.setText("!$" + String.valueOf(houseSave));
                            saveHouse.setTextColor(getResources().getColor(R.color.high));
                        }
                    }

                    // END HOUSE

                    // START ENT
                    if(entRatio == 0){
                        saveEnter.setText("-$"+String.valueOf("$0"));
                        progressBarEnter.setProgress(Integer.parseInt("0"));
                        status_Image_ent.setTextSize(12);
                        status_Image_ent.setText("You've never set this item to your budget");
                    }else{
                        float entPercent = (entTotal/entRatio)*100;
                        status_Image_ent.setText(df.format(entPercent)+"%");
                        Log.d("MonthlyAnalyticsActivity",""+Integer.parseInt(df.format(entPercent)));
                        s = df.format(entPercent);
                        d = Double.parseDouble(s);
                        num = (int) d;
                        progressBarEnter.setProgress(num);

                        if (entPercent<50){
                            progressBarEnter.getProgressDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.low), PorterDuff.Mode.SRC_IN );
                        }
                        else if (entPercent >= 50 && entPercent <100){
                            progressBarEnter.getProgressDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.mid), PorterDuff.Mode.SRC_IN );
                        }else {
                            progressBarEnter.getProgressDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.high), PorterDuff.Mode.SRC_IN );
                        }

                        float entSave = entRatio - entTotal;
                        if(entSave > entTotal){
                            saveEnter.setText("-$"+String.valueOf(entSave));
                            saveEnter.setTextColor(getResources().getColor(R.color.low));
                        }else if(entSave == entTotal){
                            saveEnter.setText("$"+String.valueOf(entSave));
                        }else if(entSave < entTotal) {
                            saveEnter.setText("!$" + String.valueOf(entSave));
                            saveEnter.setTextColor(getResources().getColor(R.color.high));
                        }
                    }

                    // END ENT


                    // START EDU
                    if(eduRatio == 0){
                        saveEducation.setText("-$"+String.valueOf("$0"));
                        progressBarEducation.setProgress(Integer.parseInt("0"));
                        status_Image_edu.setTextSize(12);
                        status_Image_edu.setText("You've never set this item to your budget");
                    }else{
                        float eduPercent = (eduTotal/eduRatio)*100;
                        status_Image_edu.setText(df.format(eduPercent)+"%");
                        s = df.format(eduPercent);
                        d = Double.parseDouble(s);
                        num = (int) d;
                        progressBarEducation.setProgress(num);

                        if (eduPercent<50){
                            progressBarEducation.getProgressDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.low), PorterDuff.Mode.SRC_IN );
                        }
                        else if (eduPercent >= 50 && eduPercent <100){
                            progressBarEducation.getProgressDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.mid), PorterDuff.Mode.SRC_IN );
                        }else {
                            progressBarEducation.getProgressDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.high), PorterDuff.Mode.SRC_IN );
                        }
                        float eduSave = eduRatio - eduTotal;

                        if(eduSave > eduTotal){
                            saveEducation.setText("-$"+String.valueOf(eduSave));
                            saveEducation.setTextColor(getResources().getColor(R.color.low));
                        }else if(eduSave == eduTotal){
                            saveEducation.setText("$"+String.valueOf(eduSave));
                        }else if(eduSave < eduTotal) {
                            saveEducation.setText("!$" + String.valueOf(eduSave));
                            saveEducation.setTextColor(getResources().getColor(R.color.high));
                        }
                    }

                    // END EDU

                    // START CHA
                    if(chaRatio == 0){
                        saveCharity.setText("-$"+String.valueOf("$0"));
                        progressBarCharity.setProgress(Integer.parseInt("0"));
                        status_Image_cha.setTextSize(12);
                        status_Image_cha.setText("You've never set this item to your budget");

                    }else{
                        float chaPercent = (chaTotal/chaRatio)*100;
                        status_Image_cha.setText(df.format(chaPercent)+"%");
                        s = df.format(chaPercent);
                        d = Double.parseDouble(s);
                        num = (int) d;
                        progressBarCharity.setProgress(num);

                        if (chaPercent<50){
                            progressBarCharity.getProgressDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.low), PorterDuff.Mode.SRC_IN );
                        }
                        else if (chaPercent >= 50 && chaPercent <100){
                            progressBarCharity.getProgressDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.mid), PorterDuff.Mode.SRC_IN );
                        }else {
                            progressBarCharity.getProgressDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.high), PorterDuff.Mode.SRC_IN );
                        }
                        float chaSave = chaRatio - chaTotal;

                        if(chaSave > chaTotal){
                            saveCharity.setText("-$"+String.valueOf(chaSave));
                            saveCharity.setTextColor(getResources().getColor(R.color.low));
                        }else if(chaSave == chaTotal){
                            saveCharity.setText("$"+String.valueOf(chaSave));
                        }else if(chaSave < chaTotal) {
                            saveCharity.setText("!$" + String.valueOf(chaSave));
                            saveCharity.setTextColor(getResources().getColor(R.color.high));
                        }
                    }

                    // END CHA

                    // START APP
                    if(appRatio == 0){
                        saveApparel.setText("-$"+String.valueOf("$0"));
                        progressBarApparel.setProgress(Integer.parseInt("0"));
                        status_Image_app.setTextSize(12);
                        status_Image_app.setText("You've never set this item to your budget");

                    }else{
                        float appPercent = (appTotal/appRatio)*100;
                        status_Image_app.setText(df.format(appPercent)+"%");
                        Log.d("MonthlyAnalyticsActivity",""+Integer.parseInt(df.format(appPercent)));
                        s = df.format(appPercent);
                        d = Double.parseDouble(s);
                        num = (int) d;
                        progressBarApparel.setProgress(num);

                        if (appPercent<50){
                            progressBarApparel.getProgressDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.low), PorterDuff.Mode.SRC_IN );
                        }
                        else if (appPercent >= 50 && appPercent <100){
                            progressBarApparel.getProgressDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.mid), PorterDuff.Mode.SRC_IN );
                        }else {
                            progressBarApparel.getProgressDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.high), PorterDuff.Mode.SRC_IN );
                        }
                        float appSave = appRatio - appTotal;

                        if(appSave > appTotal){
                            saveApparel.setText("-$"+String.valueOf(appSave));
                            saveApparel.setTextColor(getResources().getColor(R.color.low));
                        }else if(appSave == appTotal){
                            saveApparel.setText("$"+String.valueOf(appSave));
                        }else if(appSave < appTotal) {
                            saveApparel.setText("!$" + String.valueOf(appSave));
                            saveApparel.setTextColor(getResources().getColor(R.color.high));
                        }
                    }
                    // END APP

                    // START HEA
                    if(heaRatio == 0){
                        saveHealth.setText("-$"+String.valueOf("$0"));
                        progressBarHealth.setProgress(Integer.parseInt("0"));
                        status_Image_hea.setTextSize(12);
                        status_Image_hea.setText("You've never set this item to your budget");
                    }else{
                        float heaPercent = (heaTotal/heaRatio)*100;
                        status_Image_hea.setText(df.format(heaPercent)+"%");
                        Log.d("MonthlyAnalyticsActivity",""+Integer.parseInt(df.format(heaPercent)));
                        s = df.format(heaPercent);
                        d = Double.parseDouble(s);
                        num = (int) d;
                        progressBarHealth.setProgress(num);

                        if (heaPercent<50){
                            progressBarHealth.getProgressDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.low), PorterDuff.Mode.SRC_IN );
                        }
                        else if (heaPercent >= 50 && heaPercent <100){
                            progressBarHealth.getProgressDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.mid), PorterDuff.Mode.SRC_IN );
                        }else {
                            progressBarHealth.getProgressDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.high), PorterDuff.Mode.SRC_IN );
                        }
                        float heaSave = heaRatio - heaTotal;

                        if(heaSave > heaTotal){
                            saveHealth.setText("-$"+String.valueOf(heaSave));
                            saveHealth.setTextColor(getResources().getColor(R.color.low));
                        }else if(heaSave == heaTotal){
                            saveHealth.setText("$"+String.valueOf(heaSave));
                        }else if(heaSave < heaTotal) {
                            saveHealth.setText("!$" + String.valueOf(heaSave));
                            saveHealth.setTextColor(getResources().getColor(R.color.high));
                        }
                    }
                    // END HEA

                    // START PER
                    if(perRatio == 0){
                        savePersonal.setText("-$"+String.valueOf("$0"));
                        progressBarPersonal.setProgress(Integer.parseInt("0"));
                        status_Image_per.setTextSize(12);
                        status_Image_per.setText("You've never set this item to your budget");
                    }else{
                        float perPercent = (perTotal/perRatio)*100;
                        status_Image_per.setText(df.format(perPercent)+"%");
                        Log.d("MonthlyAnalyticsActivity",""+Integer.parseInt(df.format(perPercent)));
                        s = df.format(perPercent);
                        d = Double.parseDouble(s);
                        num = (int) d;
                        progressBarPersonal.setProgress(num);

                        if (perPercent<50){
                            progressBarPersonal.getProgressDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.low), PorterDuff.Mode.SRC_IN );
                        }
                        else if (perPercent >= 50 && perPercent <100){
                            progressBarPersonal.getProgressDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.mid), PorterDuff.Mode.SRC_IN );
                        }else {
                            progressBarPersonal.getProgressDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.high), PorterDuff.Mode.SRC_IN );
                        }

                        float perSave = perRatio - perTotal;
                        if(perSave > perTotal){
                            savePersonal.setText("-$"+String.valueOf(perSave));
                            savePersonal.setTextColor(getResources().getColor(R.color.low));
                        }else if(perSave == perTotal){
                            savePersonal.setText("$"+String.valueOf(perSave));
                        }else if(perPercent < perTotal) {
                            savePersonal.setText("!$" + String.valueOf(perSave));
                            savePersonal.setTextColor(getResources().getColor(R.color.high));
                        }
                    }

                    // END PER

                    // START OTHER
                    if(othRatio == 0){
                        saveOther.setText("-$"+String.valueOf("$0"));
                        progressBarOther.setProgress(Integer.parseInt("0"));
                        status_Image_oth.setTextSize(12);
                        status_Image_oth.setText("You've never set this item to your budget");
                    }else{
                        float otherPercent = (othTotal/othRatio)*100;
                        status_Image_oth.setText(df.format(otherPercent)+"%");
                        Log.d("MonthlyAnalyticsActivity","Percent: "+Integer.parseInt(df.format(otherPercent)) + "Total: " + othTotal + " Ration: " + othRatio);
                        s = df.format(otherPercent);
                        d = Double.parseDouble(s);
                        num = (int) d;
                        progressBarOther.setProgress(num);

                        if (otherPercent<50){
                            progressBarOther.getProgressDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.low), PorterDuff.Mode.SRC_IN );
                        }
                        else if (otherPercent >= 50 && otherPercent <100){
                            progressBarOther.getProgressDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.mid), PorterDuff.Mode.SRC_IN );
                        }else {
                            progressBarOther.getProgressDrawable().setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.high), PorterDuff.Mode.SRC_IN );
                        }
                        float otherSave = othRatio - othTotal;

                        if(otherSave > othTotal){
                            saveOther.setText("-$"+String.valueOf(otherSave));
                            saveOther.setTextColor(getResources().getColor(R.color.low));
                        }else if(otherSave == othTotal){
                            saveOther.setText("$"+String.valueOf(otherSave));
                        }else if(otherSave < othTotal) {
                            saveOther.setText("!$" + String.valueOf(otherSave));
                            saveOther.setTextColor(getResources().getColor(R.color.high));
                        }
                    }

                    // END OTHER


                }
                else {
                    Toast.makeText(MonthlyAnalyticsActivity.this, "setStatusAndImageResource Errors", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            };
    });
}
}

