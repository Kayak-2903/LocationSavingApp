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
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    static ArrayList<String> list;
    //    static ArrayList<Double>latitudes,longitudes;
    static ListView listView;
    static SharedPreferences sharedPreferences;
    static ArrayAdapter<String> arrayAdapter;
    static ArrayList<CategoryNode> categoryList;
    public void go_to_map(View view){
        Intent intent=new Intent(getApplicationContext(),MapsActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences=getSharedPreferences("com.example.hitchhicker",MODE_PRIVATE);
        setContentView(R.layout.activity_main);
        listView=findViewById(R.id.listView);
        if(categoryList==null)
        {
            categoryList = new ArrayList<>();
            categoryList.add(new CategoryNode("All Places"));
            categoryList.add(new CategoryNode("Others"));
            list = new ArrayList<>();
            list.add(categoryList.get(0).name);
            list.add(categoryList.get(1).name);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent(getApplicationContext(),ListofPlaces.class);
                intent.putExtra("category",i);
                System.out.println(categoryList.get(i)+" main");
                startActivity(intent);
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0||i==categoryList.size()-1)
                    return false;
                PopupMenu p=new PopupMenu(MainActivity.this,view);
                p.getMenuInflater().inflate(R.menu.menu,p.getMenu());
                p.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Toast.makeText(MainActivity.this,menuItem.getTitle(),Toast.LENGTH_LONG).show();
                        if(menuItem.getTitle().equals("Delete"))
                        {
                            categoryList.remove(i);
                            list.remove(i);
                            arrayAdapter.notifyDataSetChanged();
                            storeChanges();
                        }
                        if(menuItem.getTitle().equals("Rename"))
                        {
                            AlertDialog.Builder builder1=new AlertDialog.Builder(MainActivity.this);
                            View view1=getLayoutInflater().inflate(R.layout.activity_new_category,null);
                            ImageButton btn_ok=view1.findViewById(R.id.btn_ok);
                            EditText editText_newCategory=view1.findViewById(R.id.editText_newCategory);
                            TextView heading=view1.findViewById(R.id.heading);
                            heading.setText("Rename the category");
                            builder1.setView(view1);
                            AlertDialog alertDialog=builder1.create();
                            String defaultName=categoryList.get(i).name;
                            editText_newCategory.setText(defaultName);
                            btn_ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(editText_newCategory.getText()==null||editText_newCategory.getText().toString()==null||editText_newCategory.getText().toString().trim().equals(""))
                                        editText_newCategory.setText(defaultName);
                                    categoryList.get(i).name=editText_newCategory.getText().toString();
                                    list.set(i,editText_newCategory.getText().toString());
                                    arrayAdapter.notifyDataSetChanged();
                                    storeChanges();
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
        if(categoryList.size()>1)
            for(LocationNode i:categoryList.get(1).locationlist)
                System.out.println("1st place "+i);
        try
        {
            Gson gson=new Gson();
            String json=sharedPreferences.getString("CategoryNode",null);
            Type type=new TypeToken<ArrayList<LocationNode>>(){}.getType();
            categoryList=gson.fromJson(json,type);
            if(categoryList==null)
            {
                categoryList = new ArrayList<>();
                categoryList.add(new CategoryNode("All Places"));
                categoryList.add(new CategoryNode("Others"));
                list = new ArrayList<>();
                list.add(categoryList.get(0).name);
                list.add(categoryList.get(1).name);
            }
            else
            {
                list=new ArrayList<>();
                for(int i=0;i<categoryList.size();i++)
                {
                    list.add((categoryList.get(i)).name);
                }
            }
            arrayAdapter=new ArrayAdapter<>(this,R.layout.text_view,list);
            listView.setAdapter(arrayAdapter);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public static void addItems(String address, double latitude, double longitude,int i)
    {
        LocationNode locationNode=new LocationNode(latitude,longitude,address);
        CategoryNode categoryNode=categoryList.get(i);
        ArrayList<LocationNode> locationList=categoryNode.locationlist;
        locationList.add(locationNode);
        CategoryNode begin=categoryList.get(0);
        ArrayList<LocationNode> beginlist=begin.locationlist;
        beginlist.add(locationNode);
        categoryList.set(0,begin);
        categoryList.set(i,categoryNode);
        System.out.println("adddddddddddddddddddinggggggggggg"+locationNode+" "+categoryList.get(i).locationlist.get(0)+" "+" "+categoryList.get(0).locationlist.get(0));
        arrayAdapter.notifyDataSetChanged();
        storeChanges();
    }
    public static void storeChanges()
    {
        try {
            SharedPreferences.Editor editor=sharedPreferences.edit();
            Gson gson=new Gson();
            String json=gson.toJson(categoryList);
            editor.putString("CategoryNode",json);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

