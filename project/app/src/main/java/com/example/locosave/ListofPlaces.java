package com.example.locosave;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ListofPlaces extends AppCompatActivity {
    static ArrayList<String> list;
    //    static ArrayList<Double>latitudes,longitudes;
    static ArrayList<LocationNode> locationlist;
    static ListView listView;
    static SharedPreferences sharedPreferences;
    static ArrayAdapter<String> arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences=getSharedPreferences("com.example.hitchhicker",MODE_PRIVATE);
        setContentView(R.layout.activity_listof_places);
        listView=findViewById(R.id.listViewPlaces);
        list=new ArrayList<>();
        locationlist=new ArrayList<>();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("address",locationlist.get(i).name);
                intent.putExtra("latitude",locationlist.get(i).latitude);
                intent.putExtra("longitude",locationlist.get(i).longitude);
                startActivity(intent);
            }
        });
        CategoryNode category;
        try
        {
            Intent intent=getIntent();
            category=MainActivity.categoryList.get(intent.getIntExtra("category",-1));
            locationlist=category.locationlist;
            System.out.println(category+" listof places");
            for(LocationNode i:locationlist)
                list.add(i.locationname);
            arrayAdapter=new ArrayAdapter<>(this,R.layout.text_view,list);
            listView.setAdapter(arrayAdapter);
            TextView header=findViewById(R.id.headerplaces);
            header.setText(category.name);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                PopupMenu p=new PopupMenu(ListofPlaces.this,view);
                p.getMenuInflater().inflate(R.menu.menu,p.getMenu());
                p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Toast.makeText(ListofPlaces.this,menuItem.getTitle(),Toast.LENGTH_LONG).show();
                        if(menuItem.getTitle().equals("Delete"))
                        {
                            locationlist.remove(i);
                            list.remove(i);
                            arrayAdapter.notifyDataSetChanged();
                            MainActivity.storeChanges();
                        }
                        if(menuItem.getTitle().equals("Rename"))
                        {
                            AlertDialog.Builder builder1=new AlertDialog.Builder(ListofPlaces.this);
                            View view1=getLayoutInflater().inflate(R.layout.activity_new_category,null);
                            ImageButton btn_ok=view1.findViewById(R.id.btn_ok);
                            EditText editText_newCategory=view1.findViewById(R.id.editText_newCategory);
                            TextView heading=view1.findViewById(R.id.heading);
                            heading.setText("Rename the place");
                            builder1.setView(view1);
                            AlertDialog alertDialog=builder1.create();
                            String defaultName=locationlist.get(i).locationname;
                            editText_newCategory.setText(defaultName);
                            btn_ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(editText_newCategory.getText()==null||editText_newCategory.getText().toString()==null||editText_newCategory.getText().toString().trim().equals(""))
                                        editText_newCategory.setText(defaultName);
                                    locationlist.get(i).locationname=editText_newCategory.getText().toString();
                                    list.set(i,editText_newCategory.getText().toString());
                                    arrayAdapter.notifyDataSetChanged();
                                    MainActivity.storeChanges();
                                    alertDialog.dismiss();
                                }
                            });
                            alertDialog.show();
                        }
                        return true;
                    }
                });
                p.show();
                return true;
            }
        });
    }
}

