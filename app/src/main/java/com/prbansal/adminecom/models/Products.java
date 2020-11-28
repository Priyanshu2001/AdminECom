package com.prbansal.adminecom.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Products implements Serializable {
    public static final int WEIGHT_BASED=0, VARIANT_BASED=1;

    public String name;
    public int type;
    public String listOfNames;
    public String listOfPrice;
    public String serialNoList;
    // attributes of Weight based
    public int pricePerKg;
    public float minQty;

    // list of variants
    public List<Variants> variantsList;
    public int count =0;

    // Default Constructor
    public Products(){
    }


    //Constructor for Weight based products
    public Products(String name, int pricePerKg, float minQty) {
        this.type= WEIGHT_BASED;
        this.name = name;
        this.pricePerKg = pricePerKg;
        this.minQty = minQty;
    }

    public void WeightBasedProduct(String name, int pricePerKg, float minQty) {
        type = WEIGHT_BASED;
        this.name = name;
        this.pricePerKg = pricePerKg;
        this.minQty = minQty;
    }


    //Constructor for Variant based product

    public Products(String name, String listOfNames, String listOfPrice) {
        this.type= VARIANT_BASED;
       this.name =name;
       this.listOfNames= listOfNames;
       this.listOfPrice = listOfPrice;
    }

    public void VariantsBasedProduct(String name, String listOfNames, String listOfPrice) {
        type = VARIANT_BASED;
        this.name = name;
        this.listOfNames =listOfNames;
        this.listOfPrice = listOfPrice;
    }


    // Methods to get and set Attributes  of products

    public  String minQtyToString(){
        if(minQty<1){
            int gram = (int)(minQty*1000);
            return gram + "g";
        }

        return ((int) minQty)+"kg";
    }

 public void ExtractVariantsAndSet(String[] name, String[] price) {
        variantsList = new ArrayList<>();
     for (String s : name) {
         variantsList.add(new Variants(s,price[count],count+1));
         this.count++;
     }
 }

         public String listOfVariants(){
        String listOfVariants = variantsList.toString();
        return listOfVariants.replaceFirst("\\[","")
                .replaceFirst("]","")
                .replaceAll(",","");
    }

    @Override
    public String toString() {
        return "Products{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", listOfNames='" + listOfNames + '\'' +
                ", listOfPrice='" + listOfPrice + '\'' +
                ", pricePerKg=" + pricePerKg +
                ", minQty=" + minQty +
                ", variantsList=" + variantsList +
                '}';
    }
}
