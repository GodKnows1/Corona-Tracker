package com.godknows.covid19;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.google.android.gms.location.LocationListener;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ListOnline extends AppCompatActivity implements  GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, LocationListener {

    Map<String,Double> map=new HashMap<String, Double>();

    ListOnlineViewHolder holder1;

    private static final long UPDATE_TIME = 5000;
    private static final long FASTEST_INTERVAL = 3000;
    private static final long DISTANCE = 10;

    DatabaseReference onlineRef, currentUserRef, counterRef, locations;
    FirebaseRecyclerAdapter<User, ListOnlineViewHolder> adapter;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ImageView detailsImageView;

    private BottomSheetBehavior bottomSheetBehavior;
    private Button showBottomSheetDialogButton;

    private static final int MY_PERMISSION = 7171;
    private static final int PLAY_SERVICES = 7172;
    private LocationRequest MY_LOCATION;
    private GoogleApiClient googleApiClient;
    private Location mlocation;

    Double di=-1.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_online);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitle("The Finder App");
        toolbar.setNavigationIcon(R.drawable.ic_action_user);
        setSupportActionBar(toolbar);

        detailsImageView=(ImageView) findViewById(R.id.dist);
        detailsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getApplicationContext(),ChangeData.class);
                startActivity(i);
            }
        });

        // Firebase
        locations = FirebaseDatabase.getInstance().getReference("Locations");
        onlineRef = FirebaseDatabase.getInstance().getReference().child(".info/connected");
        counterRef = FirebaseDatabase.getInstance().getReference("lastOnline");
        currentUserRef = FirebaseDatabase.getInstance().getReference("lastOnline").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        //
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, MY_PERMISSION);
        } else {
            if (checkPlayServices()) {
                buildGoogleApiCLient();
                createLocationRequest();
                displayLocation();
            }
        }
        //
        map.put("high",2.02);
        setupSystem();
        updateList();
        loadLocationForThisUser();
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mlocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (mlocation != null) {
            locations.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(new Tracking(FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                            FirebaseAuth.getInstance().getCurrentUser().getUid(),
                            String.valueOf(mlocation.getLatitude()),
                            String.valueOf(mlocation.getLongitude())));
        } else {
            //Toast.makeText(this, "Could not get Location", Toast.LENGTH_SHORT).show();
        }
    }

    private void createLocationRequest() {
        MY_LOCATION = new LocationRequest();
        MY_LOCATION.setInterval(UPDATE_TIME);
        MY_LOCATION.setFastestInterval(FASTEST_INTERVAL);
        MY_LOCATION.setSmallestDisplacement(DISTANCE);
        MY_LOCATION.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void buildGoogleApiCLient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        googleApiClient.connect();
    }

    private boolean checkPlayServices() {
        int resulyCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resulyCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resulyCode)) {
                GooglePlayServicesUtil.getErrorDialog(resulyCode, this, PLAY_SERVICES).show();
            } else {
                Toast.makeText(this, "This device is not Supported Bhag yahan se", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }


    private void updateList() {
        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("lastOnline"), User.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<User, ListOnlineViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ListOnlineViewHolder holder, int position, @NonNull final User model) {
                try {
                    if (model.getEmail() == FirebaseAuth.getInstance().getCurrentUser().getEmail()) {
                        holder.txtEmail.setText(model.getEmail() + " (me) \n" + "0 " + " km");
                    } else {
                        holder.txtEmail.setText("Person having Id -"+model.getUid() + "\nis at " + map.get(model.getEmail()) + " km");
                    }

                } catch (Exception e){}
            }

            @NonNull
            @Override
            public ListOnlineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(getBaseContext())
                        .inflate(R.layout.user_layout, parent, false);

                return new ListOnlineViewHolder(view);
            }
        };
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    //
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices()) {
                        buildGoogleApiCLient();
                        createLocationRequest();
                        displayLocation();
                    }
                }
            }
            break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    @Override
    protected void onStop() {
        super.onStop();

        adapter.stopListening();
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
    }

    //
    private void setupSystem() {
        onlineRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Boolean.class)) {
                    currentUserRef.onDisconnect().removeValue();
                    counterRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(new User(FirebaseAuth.getInstance().getCurrentUser().getEmail(), "Quarantine",FirebaseAuth.getInstance().getCurrentUser().getUid()));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        counterRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    User user = postSnapshot.getValue(User.class);
                    Log.d("User", " " + user.getEmail() + " is " + user.getStatus());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_join:
                counterRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(new User(FirebaseAuth.getInstance().getCurrentUser().getEmail(), "Online",FirebaseAuth.getInstance().getCurrentUser().getUid()));
                break;
            case R.id.action_logout:
                currentUserRef.removeValue();
                adapter.stopListening();
                if (googleApiClient != null) {
                    googleApiClient.disconnect();
                }
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {
        mlocation = location;
        displayLocation();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, MY_LOCATION, (com.google.android.gms.location.LocationListener) ListOnline.this);

    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void loadLocationForThisUser() {
        DatabaseReference db=FirebaseDatabase.getInstance().getReference("Locations");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()){
                    Object o1=childDataSnapshot.child("lng").getValue();
                    Object o2=childDataSnapshot.child("lat").getValue();
                    String s1=o1.toString();
                    String s2=o2.toString();
                    Log.d("distance", distance(mlocation, s1, s2) + " ");
                    //if(distance(mlocation,s1,s2)<=5) { //5km
                        map.put(childDataSnapshot.child("email").getValue().toString(), distance(mlocation, s1, s2));
                        adapter.notifyDataSetChanged();
                    //}
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //updateList();
    }

    private double distance(Location currentUser, String s1,String s2) {
        double dist=0.0;
        try {
            Log.v("locations", currentUser.getLatitude() + " " + s2);
            double rlat1 = Math.PI * (currentUser.getLatitude()) / 180;
            double rlat2 = Math.PI * (Double.valueOf(s2)) / 180;
            double theta = currentUser.getLongitude() - Double.valueOf(s1);
            double rtheta = Math.PI * theta / 180;
            dist = Math.sin(rlat1) * Math.sin(rlat2) + Math.cos(rlat1) * Math.cos(rlat2) * Math.cos(rtheta);
            dist = Math.acos(dist);
            dist = dist * 180 / Math.PI;
            dist = dist * 60 * 1.1515;
        } catch (Exception re){}
       // Toast.makeText(this, dist*1.609344+" ", Toast.LENGTH_SHORT).show();
        return dist * 1.609344;
    }
}
