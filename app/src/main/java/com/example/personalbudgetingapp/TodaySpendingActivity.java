package com.example.personalbudgetingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class TodaySpendingActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    private Toolbar toolbar;
    private TextView totalAmountSpentOn;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private FloatingActionButton fab;
    private ProgressDialog loader;

    private FirebaseAuth mAuth;
    private String onlineUserId = "";
    private DatabaseReference expensesRef;

    private TodayItemAdapter todayItemAdapter;
    private List<Data> myDataList;

    private String sMonth, sDate, sDay, sYear;
    private DateTime specificDateSelected;

    private Button btnChooseDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_spending_acitvity);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Spending");

        totalAmountSpentOn = findViewById(R.id.totalAmountSpentOn);
        progressBar = findViewById(R.id.progressBar);

        fab = findViewById(R.id.fab);
        loader = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        onlineUserId = mAuth.getCurrentUser().getUid();
        expensesRef = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        btnChooseDate = findViewById(R.id.btn_chooseDate);

        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        myDataList = new ArrayList<>();
        todayItemAdapter = new TodayItemAdapter(TodaySpendingActivity.this,myDataList);
        recyclerView.setAdapter(todayItemAdapter);

        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                addItemSpentOn();
            }
        });

        btnChooseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog();
            }
        });

    }// end onCreate

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        sYear = String.valueOf(year);
        sMonth =  String.valueOf(month + 1);
        sDay = String.valueOf(dayOfMonth);
        sDate = sDay + "/" + sMonth + "/" + sYear;

        final TextView sPeriode = findViewById(R.id.tvExpensePeriode);
        sPeriode.setText("Periode: " + sDate);
        sPeriode.setVisibility(View.VISIBLE);
        specificDateSelected = new DateTime(sYear+"-"+sMonth+"-"+sDay);
        Toast.makeText(getApplicationContext(),specificDateSelected.toString() , Toast.LENGTH_SHORT).show();

        readItems();


    }

    private void readItems(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("expenses").child(onlineUserId);
        Query query = reference.orderByChild("date").equalTo(sDay+"-"+sMonth+"-"+sYear);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myDataList.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Data data = dataSnapshot.getValue(Data.class);
                    myDataList.add(data);
                }

                todayItemAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                int totalAmount = 0;
                for(DataSnapshot ds: snapshot.getChildren()){
                    Map<String, Object> map = (Map<String, Object>)ds.getValue();
                    Object total = map.get("amount");
                    int pTotal = Integer.parseInt(String.valueOf(total));
                    totalAmount += pTotal;

                    totalAmountSpentOn.setText("Total Day's Spending: $" + totalAmount);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addItemSpentOn(){
        AlertDialog.Builder myDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View myView = inflater.inflate(R.layout.input_layout,null);
        myDialog.setView(myView);

        final AlertDialog dialog = myDialog.create();
        dialog.setCancelable(false);

//        Adding item to the realtime firedatabase

        final Spinner itemSpinner = myView.findViewById(R.id.itemSpinner);
        final EditText amount = myView.findViewById(R.id.amount);
        final EditText note = myView.findViewById(R.id.note);
        final Button cancel = myView.findViewById(R.id.cancel);
        final Button save = myView.findViewById(R.id.save);
        final TextView tvSelectedDate= myView.findViewById(R.id.selectedDate);

        tvSelectedDate.setText("Periode: " + sDate);
        tvSelectedDate.setVisibility(View.VISIBLE);

        note.setVisibility(View.VISIBLE);

        save.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                String Amount = amount.getText().toString();
                String Item = itemSpinner.getSelectedItem().toString();
                String notes = note.getText().toString();

                if(TextUtils.isEmpty(Amount)){
                    amount.setError("Amount is required");
                    return;
                }
                if (Item.equals("Select item")) {
                    Toast.makeText(TodaySpendingActivity.this, "Select a valid item", Toast.LENGTH_SHORT).show();
                }
                if (TextUtils.isEmpty(notes)){
                    note.setError("Note is required");
                    return;
                }
                else {
                    loader.setMessage("adding a budget item");
                    loader.setCanceledOnTouchOutside(false);
                    loader.show();

                    String id = expensesRef.push().getKey();
                    String date = sDay+"-"+sMonth+"-"+sYear;

                    MutableDateTime epoch = new MutableDateTime();
                    epoch.setDate(0);

                    Months months = Months.monthsBetween(epoch,specificDateSelected);
                    Weeks weeks = Weeks.weeksBetween(epoch,specificDateSelected);

                    String itemDay ="";
                    String itemWeek ="";
                    String itemMonth = "";

                    Integer getMonth;
                    Integer getWeek;

                    Log.d("hari",String.valueOf(sDay.equals("1")));
                    if(sDay.equals("1")){
                        Log.d("hari",String.valueOf(sDay.equals("1")));
                        Log.d("hari",String.valueOf(months.getMonths()+1));
                        itemDay = Item+date;
                        itemWeek = Item+(weeks.getWeeks()+1);
                        itemMonth = Item+(months.getMonths()+1);
                        getMonth = months.getMonths()+1;
                        getWeek = weeks.getWeeks()+1;
                    } else {
                        itemDay = Item+date;
                        itemWeek = Item+weeks.getWeeks();
                        itemMonth = Item+months.getMonths();
                        getMonth = months.getMonths();
                        getWeek = weeks.getWeeks();
                    }

                    Log.d("itemMonth",itemMonth);

                    Data data = new Data(Item, date, id, itemDay, itemWeek, itemMonth,  Integer.parseInt(Amount),getMonth,getWeek,notes);
                    expensesRef.child(id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(TodaySpendingActivity.this, "Budget item added successfuly", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(TodaySpendingActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                            loader.dismiss();
                        }
                    });
                }
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


}