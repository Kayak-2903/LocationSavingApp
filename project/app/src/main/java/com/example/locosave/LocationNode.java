package com.example.locosave;

public class LocationNode extends CategoryNode{
    double latitude,longitude;
    String locationname;
    LocationNode(double latitude,double longitude,String name)
    {
        this.latitude=latitude;
        this.longitude=longitude;
        this.locationname=name;
    }
}
