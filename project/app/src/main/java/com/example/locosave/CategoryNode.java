package com.example.locosave;

import java.io.Serializable;
import java.util.ArrayList;

public class CategoryNode implements Serializable {
    ArrayList<LocationNode> locationlist;
    String name;
    CategoryNode(String name)
    {
        this.name=name;
        locationlist=new ArrayList<>();
    }
    CategoryNode(){}
}
