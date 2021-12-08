package com.example.personalbudgetingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AccountActivity extends AppCompatActivity {

    private Toolbar settingsToolbar;
    private TextView userEmail;
    private Button logoutBtn;
    private TextView tvAddress;
    String sCurrentLocation;
    String sTVAddress;

    private DatabaseReference profilRef;
    private FirebaseAuth mAuth;

    private TextView latitudeField, longitudeField;
    private static final int PERMISSIONS_FINE_LOCATION = 99;

    Location currentLocation;

    //    Google's API for location services. The majority of the app functions using this class
    FusedLocationProviderClient fusedLocationProviderClient;

    //    Location request is a config file for all settings related to FusedLocationProviderClient
    LocationRequest locationRequest;

    LocationCallback locationCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        userEmail = findViewById(R.id.userEmail);
        userEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());


        tvAddress = findViewById(R.id.tvAddress);
        Toolbar settingToolbar = findViewById(R.id.my_Feed_Toolbar);
        setSupportActionBar(settingToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Profile");

        // set all properties of locationRequest
        locationRequest = new LocationRequest();

        // how often does the default location check occur?
        locationRequest.setInterval(1000 * 3);

//        how often does the location check occur when set to the most frequent update?
        locationRequest.setFastestInterval(1000 * 5);

        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // event that is triggered whenever the update interval is met.
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);

                // save the location
                Location location = locationResult.getLastLocation();
                updateUIValues(location);
            }

        };

        mAuth = FirebaseAuth.getInstance();
        profilRef = FirebaseDatabase.getInstance().getReference().child("profil").child(mAuth.getCurrentUser().getUid());

        showAddress();

        ImageView addImg = findViewById(R.id.addImg);

        addImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
                LayoutInflater layoutInflater = LayoutInflater.from(AccountActivity.this);
                final View dialogView = layoutInflater.inflate(R.layout.input_address, null);

                builder
                        .setView(dialogView)
                        .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                
                AlertDialog alertDialog = builder.create();
                
                ImageView imgGPS = dialogView.findViewById(R.id.imgGPS);
                TextView tvCurrentLocation = dialogView.findViewById(R.id.tvCurrentLocation);
                Button btnSetCustomAdrs = dialogView.findViewById(R.id.btnCustomAddress);
                Button btnSetCurrentAdrs = dialogView.findViewById(R.id.btnCurrentLocation);
                EditText etAddress = dialogView.findViewById(R.id.etAddress);

                etAddress.setText(tvAddress.getText().toString());

                btnSetCustomAdrs.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (etAddress.getText().toString().equals("")){
                            Toast.makeText(getApplicationContext(), "fill required field", Toast.LENGTH_SHORT).show();
                        }else{
                            profilRef.child("address").setValue(etAddress.getText().toString());
                            Toast.makeText(getApplicationContext(), "Address added", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        }
                    }
                });
                btnSetCurrentAdrs.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(tvCurrentLocation.getText().toString().equals("Double Click GPS Button")){
                            Toast.makeText(getApplicationContext(), "Press GPS Button", Toast.LENGTH_SHORT).show();
                        }else {
                            profilRef.child("address").setValue(tvCurrentLocation.getText().toString());
                            Toast.makeText(getApplicationContext(), "Address added", Toast.LENGTH_SHORT).show();
                            alertDialog.dismiss();
                        }

                    }
                });

                imgGPS.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(AccountActivity.this);
                        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
                        updateGPS();
                        tvCurrentLocation.setText(sCurrentLocation);
                    }
                });
                alertDialog.show();

            }
        });


    }

    private void showAddress() {
        profilRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("snapshotexists",String.valueOf(snapshot.exists()));
                Log.d("children",snapshot.toString());
                if (snapshot.exists() && snapshot.getChildrenCount()>0){
                    for (DataSnapshot snap:snapshot.getChildren()){
                        Log.d("snap",snap.toString());
                        tvAddress.setText(snap.getValue().toString());
                    }

                }else {
                    tvAddress.setText("Set Your Address");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case PERMISSIONS_FINE_LOCATION:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateGPS();
                }
                else {
                    Toast.makeText(this, "This app requires permissions to be granted in order to work properly", Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private void updateGPS() {
        // get permissions from the user to track
        // get the current location from the fused client
        // update the UI - i.e. set all properties in their associated text view items

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(AccountActivity.this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // user provided the permission
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // we got permissions. Put the values of location. XXX into the UI components.
                    updateUIValues(location);
                }
            });
        } else{
            // user not granted yet.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
            }
        }
    }

    private void updateUIValues(Location location) {
        LayoutInflater layoutInflater = LayoutInflater.from(AccountActivity.this);
        final View dialogView = layoutInflater.inflate(R.layout.input_address, null);
        TextView tvCurrentLocation = dialogView.findViewById(R.id.tvCurrentLocation);
        Geocoder geocoder = new Geocoder(AccountActivity.this);
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            tvCurrentLocation.setText(addresses.get(0).getAddressLine(0));
            sCurrentLocation = addresses.get(0).getAddressLine(0);
        } catch (Exception e) {
            sCurrentLocation = "Unable to get street address";
            tvCurrentLocation.setText("Unable to get street address");
        }
    }
}