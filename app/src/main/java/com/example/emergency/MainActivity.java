package com.example.emergency;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.emergency.common.Common;
import com.example.emergency.common.MyLocation;
import com.example.emergency.common.Report;
import com.example.emergency.common.ReportAdapter;
import com.example.emergency.common.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.Types.BoomType;
import com.nightonke.boommenu.Types.ButtonType;
import com.nightonke.boommenu.Types.PlaceType;
import com.nightonke.boommenu.Util;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private boolean init = false;
    private BoomMenuButton mBoomMenuButton;
    private Location mLocation;
    private FirebaseUser mUser;
    private LocationManager mLocationManager;

    private TextView mUserNameTextView;
    private TextView mUserPhoneTextView;
    private TextView mUserOccupationTextView;
    private TextView mUserAddress;
    private RecyclerView mReportRecyclerview;
    ArrayList<Report> mEmergencyReports;
    ReportAdapter mReportsAdapter;


    DatabaseReference reportsdbRef = FirebaseDatabase.getInstance().getReference("Reports");
    DatabaseReference usersdbRef = FirebaseDatabase.getInstance().getReference("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeViews();
        userIsLoggedIn();

        requestLocationPermission();
        initializeUserLocation();
        initializeUser();

        initializeRecyclerView();
        getReportData();

    }

    private void getReportData() {
        reportsdbRef.child(mUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                        String emergencyStatus= "";
                        String emergencyType ="";
                        String emergencyDate = "";

                        if (snapshot.child("emergencyType").getValue() != null){
                            emergencyType = snapshot.child("emergencyType").getValue().toString();
                        }

                        if (snapshot.child("timeStamp").getValue() != null){
                            emergencyDate = snapshot.child("timeStamp").getValue().toString();
                        }

                        if (snapshot.child("status").getValue() != null){
                            emergencyStatus = snapshot.child("status").getValue().toString();
                        }

                        Report report = new Report(emergencyType, emergencyStatus, emergencyDate);

                        boolean reportExists = false;

                        for(Report reportsIterator : mEmergencyReports){
                            if( reportsIterator.getEmergencyTimeStamp().equals(report.getEmergencyTimeStamp())){
                                reportExists = true;
                            }
                        }

                        if (reportExists){
                            continue;
                        }

                        mEmergencyReports.add(report);
                        mReportsAdapter.notifyDataSetChanged();

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initializeRecyclerView() {
        mEmergencyReports = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mReportsAdapter = new ReportAdapter(mEmergencyReports, getApplicationContext());
        mReportRecyclerview.setLayoutManager(layoutManager);
        mReportRecyclerview.setHasFixedSize(true);
        mReportRecyclerview.setAdapter(mReportsAdapter);

    }

    private void initializeViews() {
        mBoomMenuButton = findViewById(R.id.boom);

        mUserNameTextView = findViewById(R.id.username_TextView);
        mUserPhoneTextView = findViewById(R.id.user_phone_number_TextView);
        mUserOccupationTextView = findViewById(R.id.user_occupation_TextView);
        mUserAddress = findViewById(R.id.user_address_TextView);
        mReportRecyclerview = findViewById(R.id.reports_RecyclerView);
    }

    private void userIsLoggedIn() {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser == null) {
            startActivity(
                    new Intent(MainActivity.this, LoginActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            );
            finish();
        }
    }

    private void initializeUser() {

        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference("Users").child(mUser.getUid());
        userDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    User newUser = new User();

                    if (dataSnapshot.child("occupation").getValue() != null) {
                        String occupation = dataSnapshot.child("occupation").getValue().toString();
                        newUser.setOccupation(occupation);
                        mUserOccupationTextView.setText(occupation);
                    }

                    if (dataSnapshot.child("age").getValue() != null) {
                        newUser.setAge(dataSnapshot.child("age").getValue().toString());
                    }

                    if (dataSnapshot.child("name").getValue() != null) {
                        String name = dataSnapshot.child("name").getValue().toString();
                        newUser.setName(name);
                        mUserNameTextView.setText(name);
                    }

                    if (dataSnapshot.child("phoneNumber").getValue() != null) {
                        String phoneNumber = dataSnapshot.child("phoneNumber").getValue().toString();
                        newUser.setPhoneNumber(phoneNumber);
                        mUserPhoneTextView.setText(phoneNumber);
                    }

                    Common.currentUser = newUser;

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initializeUserLocation(){
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                useLocation(location);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }
    private void useLocation(Location location) {
        mLocation = location;
    }

    private void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

            }

        }

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (init) return;
        init = true;

        Drawable[] subButtonDrawables = new Drawable[3];

        int[] drawableResource = new int[]{
                R.drawable.fire,
                R.drawable.medical,
                R.drawable.police
        };

        for (int i = 0; i < 3; i++) {
            subButtonDrawables[i] = ContextCompat.getDrawable(this, drawableResource[i]);
        }

        String[] subButtonsText = new String[]{"BoomMenuButton", "View Source Code", "Follow me"};

        int[][] subButtonColors = new int[3][2];

        for (int i = 0; i < 3; i++) {
            subButtonColors[i][1] = ContextCompat.getColor(this, R.color.almost_black);
            subButtonColors[i][0] = Util.getInstance().getPressedColor(subButtonColors[i][1]);
        }

        new BoomMenuButton.Builder()
                .addSubButton(ContextCompat.getDrawable(this, R.drawable.fire), subButtonColors[0], "Report Fire Emergency")
                .addSubButton(ContextCompat.getDrawable(this, R.drawable.medical), subButtonColors[0], "Report Medical Emergency")
                .addSubButton(ContextCompat.getDrawable(this, R.drawable.police), subButtonColors[0], "Report Vices")
                .button(ButtonType.HAM)
                .boom(BoomType.PARABOLA)
                .place(PlaceType.HAM_3_1)
                .subButtonTextColor(ContextCompat.getColor(this, R.color.material_white))
                .subButtonsShadow(Util.getInstance().dp2px(2), Util.getInstance().dp2px(2))
                .onSubButtonClick(new BoomMenuButton.OnSubButtonClickListener() {
                    @Override
                    public void onClick(int buttonIndex) {
                        if (buttonIndex == 0) {
                            processEmergency("Fire");
                        } else if (buttonIndex == 1) {
                            processEmergency("Medical");
                        } else {
                            processEmergency("Police");
                        }
                    }
                })
                .init(mBoomMenuButton);
    }

    private void processEmergency(final String emergencyType) {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
            return;
        }
        HashMap<String, Object> newEmergencyMap = new HashMap<>();

        if (mLocation != null) {
            mLocation = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            MyLocation newLocation = new MyLocation(mLocation.getLatitude(), mLocation.getLongitude());
            newEmergencyMap.put("location", newLocation);
        } else {
            Toast.makeText(MainActivity.this, "Make sure your device location is switched on", Toast.LENGTH_LONG).show();
            return;
        }

        long timestamp = new Timestamp(new Date().getTime()).getTime();
        newEmergencyMap.put("emergencyType", emergencyType);
        newEmergencyMap.put("timeStamp", timestamp);
        newEmergencyMap.put("status", "pending");


        reportsdbRef.child(mUser.getUid()).push().setValue(newEmergencyMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(MainActivity.this, "New " + emergencyType + " emergency reported, immediate response on the way", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemID = item.getItemId();

        switch (itemID) {
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this,
                        LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
}
