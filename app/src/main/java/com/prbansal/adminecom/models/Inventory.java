package com.prbansal.adminecom.models;

import java.io.Serializable;
import java.util.ArrayList;


public class Inventory implements Serializable {
    public ArrayList<Products> myProductsList;

    public Inventory() {
    }

    public Inventory(ArrayList<Products> myProductsList) {
        this.myProductsList = myProductsList;
    }
}
