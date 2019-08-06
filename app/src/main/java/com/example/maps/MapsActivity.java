package com.example.maps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
//import android.location.LocationListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import com.google.android.gms.location.LocationListener;

import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.example.maps.directionhelpers.FetchURL;
import com.example.maps.directionhelpers.DataParser;
import com.example.maps.directionhelpers.PointsParser;
import com.example.maps.directionhelpers.*;
import com.example.maps.directionhelpers.TaskLoadedCallback;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener ,TaskLoadedCallback {

    private GoogleMap mMap;

private MarkerOptions place1,place2;
private Polyline currentPolyline;
   // private Context mContext;

    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location lastLocation;
    private Marker currentuserlocation;
    private static final int Request_user_location_code = 99;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkuserlocationPermission();
        }
        // Obtain
        // the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

       // place1 = currentuserlocation;
        //place2 = new MarkerOptions().position(new LatLng(27.667491,85.32085)).title("Location 2");

//                String url = getUrl(place1.getPosition(),place2.getPosition(),"driving");
//
//                new FetchURL( MapsActivity.this).execute(url,"driving");

        }

    private void checkuserlocationPermission() {
    }

    ;





    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d("mylog", "Added Markers");
        try {
            geoLocate();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMap.addMarker(place1);
        mMap.addMarker(place2);
        //new FetchURL(MapsActivity.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){

            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);

        }
    }
    @Override//handle the permission request response
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode){
            case Request_user_location_code:
                if(grantResults.length  > 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                        if(googleApiClient == null){
                            buildGoogleApiClient();//if google api client is null create a new method down
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                }
                else {
                    Toast.makeText(this,"permission denied...",Toast.LENGTH_SHORT).show();
                }
                return;
        }

    }
    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();

    }
    private String getUrl(LatLng origin,LatLng dest,String directionMode){
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key);
        return url;
    }




    @Override
    public void onLocationChanged(Location location) {
        lastLocation= location;//removes default location
        if(currentuserlocation!=null){
            currentuserlocation.remove();//remove current location of user
        }
        LatLng latlan = new LatLng(location.getLatitude(),location.getLongitude());//latitude longitude
        MarkerOptions markerOptions = new MarkerOptions();//marker option
        markerOptions.title("user current location");//current location of user
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));//change color to orange of marker

//currentuserlocation =(Marker)mMap.addMarker((MarkerOptions)markerOptions);//marker options
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlan));//move camera to current location
        mMap.animateCamera((CameraUpdateFactory.zoomBy(12)));//zoom
        //stop location update so use if
        if(googleApiClient!=null){
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, (com.google.android.gms.location.LocationListener) this);
        }

    }




    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1100);//how frequent location is refreshed
        locationRequest.setFastestInterval(1100);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);//priority
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest, (com.google.android.gms.location.LocationListener) this);//error because of user permission
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    public void geoLocate() throws IOException {
        //hideSoftKey();
        Intent intent = getIntent();
        String loc = intent.getStringExtra(MainActivity.EXTRA_LOC);
        String dest = intent.getStringExtra(MainActivity.EXTRA_DES);
        Geocoder gc = new Geocoder(this);
        List<Address> list = gc.getFromLocationName(loc,1);
        Address address = list.get(0);
        //String locality = getLocality();
        Toast.makeText(this,loc,Toast.LENGTH_LONG).show();
       final double lat = address.getLatitude();
        final double lon = address.getLongitude();
        LatLng Mumbai = new LatLng(lat,lon);
        //MarkerOptions markerOptions = new MarkerOptions();

        mMap.addMarker(new MarkerOptions().position(Mumbai).title("Marker in "+loc));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(Mumbai));
        //  mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Mumbai,0));
        Geocoder g  = new Geocoder(this);
        List<Address> list1 = g.getFromLocationName(dest,1);
        Address add = list1.get(0);
       final double lat1 = add.getLatitude();
       final double lon1 = add.getLongitude();
        LatLng delhi = new LatLng(lat1,lon1);
        mMap.addMarker(new MarkerOptions().position(delhi).title("Marker in "+dest));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(delhi));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(delhi,0));
        place1 = new MarkerOptions().position(new LatLng(lat,lon)).title("Location 1");
        place2 = new MarkerOptions().position(new LatLng(lat1,lon1)).title("Location 2");
        Button b = findViewById(R.id.maps2);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW,     Uri.parse("https://www.google.com/maps/dir/"+lat+","+lon+"/"+lat1+","+lon1));
                startActivity(i);
            }
        });
    }
    @Override
    public void onTaskDone(Object... values){
        if (currentuserlocation != null)
            currentuserlocation.remove();

        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);

    }

}


