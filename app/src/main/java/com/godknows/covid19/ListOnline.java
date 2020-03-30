package com.godknows.covid19;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.google.android.gms.location.LocationListener;

public class ListOnline extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final long UPDATE_TIME = 5000;
    private static final long FASTEST_INTERVAL = 3000;
    private static final long DISTANCE = 10;
    DatabaseReference onlineRef,currentUserRef, counterRef,locations;
    FirebaseRecyclerAdapter<User,ListOnlineViewHolder> adapter;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    private static final int MY_PERMISSION=7171;
    private static final int PLAY_SERVICES=7172;
    private LocationRequest MY_LOCATION;
    private GoogleApiClient googleApiClient;
    private Location mlocation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_online);

        recyclerView  = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        Toolbar toolbar=(Toolbar) findViewById(R.id.toolBar);
        toolbar.setTitle("Tracker App COCOCO");
        setSupportActionBar(toolbar);

        // Firebase
        locations=FirebaseDatabase.getInstance().getReference("Locations");
        onlineRef= FirebaseDatabase.getInstance().getReference().child(".info/connected");
        counterRef = FirebaseDatabase.getInstance().getReference("lastOnline");
        currentUserRef = FirebaseDatabase.getInstance().getReference("lastOnline").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        setupSystem();

        updateList();

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            },MY_PERMISSION);
        } else{
            if(checkPlayServices()){
                buildGoogleApiCLient();
                createLocationRequest();
                displayLocation();
            }
        }

    }

    private void displayLocation() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            return;
        }
        mlocation=LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if(mlocation!=null){
            locations.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(new Tracking(FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                            FirebaseAuth.getInstance().getCurrentUser().getUid(),
                            String.valueOf(mlocation.getLatitude()),
                                    String.valueOf(mlocation.getLongitude())));
        } else{
            Toast.makeText(this, "Could not get Location", Toast.LENGTH_SHORT).show();

        }
    }

    private void createLocationRequest() {
        MY_LOCATION=new LocationRequest();
        MY_LOCATION.setInterval(UPDATE_TIME);
        MY_LOCATION.setFastestInterval(FASTEST_INTERVAL);
        MY_LOCATION.setSmallestDisplacement(DISTANCE);
        MY_LOCATION.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void buildGoogleApiCLient() {
        googleApiClient=new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        googleApiClient.connect();
    }

    private boolean checkPlayServices() {
        int resulyCode= GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(resulyCode!= ConnectionResult.SUCCESS){
            if(GooglePlayServicesUtil.isUserRecoverableError(resulyCode)){
                GooglePlayServicesUtil.getErrorDialog(resulyCode,this,PLAY_SERVICES).show();
            } else{
                Toast.makeText(this, "This device is not Supported Bhag yahan se", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }
        return true;
    }

    private void updateList() {

        //
        FirebaseRecyclerOptions<User> options =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("lastOnline"), User.class)
                        .build();
        //

        adapter= new FirebaseRecyclerAdapter<User, ListOnlineViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ListOnlineViewHolder holder, int position, @NonNull final User model) {
                holder.txtEmail.setText(model.getEmail());
                Toast.makeText(ListOnline.this, "Click444ed", Toast.LENGTH_SHORT).show();
                holder.itemClickListener1=new itemClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Toast.makeText(ListOnline.this, "Clicked", Toast.LENGTH_SHORT).show();
                        if(!model.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                            Intent map = new Intent(ListOnline.this,Tracking_Activity.class);
                            map.putExtra("email",model.getEmail());
                            map.putExtra("lat",mlocation.getLatitude());
                            map.putExtra("lng",mlocation.getLongitude());
                            startActivity(map);

                        }
                    }
                };
                //holder.setItemClickListener(holder.itemClickListener1);
            }

            @NonNull
            @Override
            public ListOnlineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(getBaseContext())
                        .inflate(R.layout.user_layout, parent, false);

                return new ListOnlineViewHolder(view);
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
    //
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        if(googleApiClient!=null){
            googleApiClient.connect();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case MY_PERMISSION:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    if(checkPlayServices()){
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
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
//        if(googleApiClient!=null){
//            googleApiClient.disconnect();
//        }
    }
    //
    private void setupSystem()  {
        onlineRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(Boolean.class)){
                    currentUserRef.onDisconnect().removeValue();
                    counterRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(new User(FirebaseAuth.getInstance().getCurrentUser().getEmail(),"Online"));
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
                for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                    User user=postSnapshot.getValue(User.class);
                    Log.d("User"," "+user.getEmail() + " is " + user.getStatus());
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
        inflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_join:
                counterRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(new User(FirebaseAuth.getInstance().getCurrentUser().getEmail(),"Online"));
                break;
            case R.id.action_logout:
                currentUserRef.removeValue();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onLocationChanged(Location location) {
        mlocation=location;
        displayLocation();
    }

//    @Override
//    public void onStatusChanged(String provider, int status, Bundle extras) {
//
//    }

//    @Override
//    public void onProviderEnabled(String provider) {
//
//    }

//    @Override
//    public void onProviderDisabled(String provider) {
//
//    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        displayLocation();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,MY_LOCATION, (com.google.android.gms.location.LocationListener) ListOnline.this);

    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
