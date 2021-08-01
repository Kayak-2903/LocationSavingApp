package com.example.locosave;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    String address;
    double longitude,latitude;
    Marker currentlocation;
    LatLng myLocation;
    Button btn_myLocation;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,100,10,locationListener);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Intent instances=getIntent();
        address=instances.getStringExtra("address");
        latitude=instances.getDoubleExtra("latitude",-400);
        longitude=instances.getDoubleExtra("longitude",-400);
        mapFragment.getMapAsync(this);
        myLocation=null;
        btn_myLocation=findViewById(R.id.btn_myLocation);
        btn_myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(myLocation!=null)
                {
                    String time=new SimpleDateFormat("dd-MMM-yyyy,HH:mm", Locale.getDefault()).format(new Date());
                    String address="";
                    Geocoder geocoder=new Geocoder(getApplicationContext(),Locale.getDefault());
                    List<Address> addressList;
                    try {
                        addressList = geocoder.getFromLocation(myLocation.latitude,myLocation.longitude,1);
                        if(addressList.get(0).getAddressLine(0)!=null)
                            address+=addressList.get(0).getAddressLine(0)+", ";
                        if(address.equals(""))
                            address=time;
                        addNewAlertBox(myLocation,address,null);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                // Add a marker in Sydney and move the camera
                //mMap.clear();
                 myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                if(currentlocation!=null)
                    currentlocation.remove();

                currentlocation=mMap.addMarker(new MarkerOptions().position(myLocation).title("You").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100,10, locationListener);
            Location lastknown = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
//            if()
            if(lastknown!=null)
            {
                myLocation = new LatLng(lastknown.getLatitude(), lastknown.getLongitude());
                if (currentlocation != null)
                    currentlocation.remove();

                currentlocation = mMap.addMarker(new MarkerOptions().position(myLocation).title("You").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation,15));
            }
        }
        if(latitude!=-400) {
            LatLng sydney = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(sydney).title(address));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        System.out.println("Long pressed");
        String time=new SimpleDateFormat("dd-MMM-yyyy,HH:mm", Locale.getDefault()).format(new Date());
        String address="";
        Geocoder geocoder=new Geocoder(getApplicationContext(),Locale.getDefault());
        List<Address> addressList;
        try {
            addressList = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            if(addressList.get(0).getAddressLine(0)!=null)
                address+=addressList.get(0).getAddressLine(0)+", ";
            if(address.equals(""))
                address=time;
            System.out.println("ADDRESS IS "+address);

            Marker marker=mMap.addMarker(new MarkerOptions().position(latLng).title(address));
            addNewAlertBox(latLng,address,marker);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void addNewAlertBox(LatLng latLng, String address, Marker marker)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(MapsActivity.this);
        View view=getLayoutInflater().inflate(R.layout.activity_new_alert_box,null);
        ImageButton btn_plus=view.findViewById(R.id.btn_plus);
        ListView listView_categories=view.findViewById(R.id.listview_categories);
        builder.setView(view);
        ArrayList<CategoryNode> categoryNodeArrayList=new ArrayList<>(MainActivity.categoryList);
        categoryNodeArrayList.remove(0);
        ArrayList<String> lists=new ArrayList<>();
        for(CategoryNode i:categoryNodeArrayList)
            lists.add(i.name);
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(this,R.layout.text_view,lists);
        listView_categories.setAdapter(arrayAdapter);
        AlertDialog alert=builder.create();
        alert.show();
        AlertDialog.Builder builder1=new AlertDialog.Builder(MapsActivity.this);
        View view1=getLayoutInflater().inflate(R.layout.activity_new_category,null);
        ImageButton btn_ok=view1.findViewById(R.id.btn_ok);
        EditText editText_newCategory=view1.findViewById(R.id.editText_newCategory);
        TextView heading=view1.findViewById(R.id.heading);
        heading.setText("Name new category");
        builder1.setView(view1);
        AlertDialog alertDialog=builder1.create();
        int index=lists.size()-1;
        String defaultName=editText_newCategory.getText()+"("+new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())+")";
        editText_newCategory.setText(defaultName);
        listView_categories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                addNewName(address,latLng,i+1,marker);
//                MainActivity.addItems(newname,latLng.latitude,latLng.longitude,i+1);

                alert.dismiss();
            }
        });
        btn_plus.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                alert.dismiss();
                Toast.makeText(MapsActivity.this,"Showing",Toast.LENGTH_LONG).show();
                alertDialog.show();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText_newCategory.getText()==null||editText_newCategory.getText().toString()==null||editText_newCategory.getText().toString().trim().equals(""))
                    editText_newCategory.setText(defaultName);
                MainActivity.categoryList.add(new CategoryNode(editText_newCategory.getText().toString().trim()));
                CategoryNode temp=MainActivity.categoryList.get(MainActivity.categoryList.size()-1);
                MainActivity.categoryList.set(MainActivity.categoryList.size()-1,MainActivity.categoryList.get(MainActivity.categoryList.size()-2));
                MainActivity.categoryList.set(MainActivity.categoryList.size()-2,temp);
                addNewName(address,latLng,MainActivity.categoryList.size()-2,marker);
//                MainActivity.addItems(newname,latLng.latitude,latLng.longitude,MainActivity.categoryList.size()-2);
                alertDialog.dismiss();
            }
        });
        alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if(marker!=null)marker.remove();
            }
        });
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if(marker!=null)marker.remove();
            }
        });
    }
    String tempname="";
    public void addNewName(String defaultName,LatLng latLng,int index,Marker marker){
        tempname=defaultName;
        AlertDialog.Builder builder1=new AlertDialog.Builder(MapsActivity.this);
        View view1=getLayoutInflater().inflate(R.layout.activity_new_category,null);
        ImageButton btn_ok=view1.findViewById(R.id.btn_ok);
        EditText editText_newCategory=view1.findViewById(R.id.editText_newCategory);
        TextView heading=view1.findViewById(R.id.heading);
        heading.setText("Name the place");
        builder1.setView(view1);
        AlertDialog alertDialog=builder1.create();
        editText_newCategory.setText(defaultName);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText_newCategory.getText()==null||editText_newCategory.getText().toString()==null||editText_newCategory.getText().toString().trim().equals(""))
                    editText_newCategory.setText(defaultName);
                tempname=editText_newCategory.getText().toString();
                MainActivity.addItems(tempname,latLng.latitude,latLng.longitude,index);

                alertDialog.dismiss();
            }
        });
        alertDialog.show();

        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                if(marker!=null)marker.remove();
            }
        });
    }
}