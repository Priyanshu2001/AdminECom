package com.prbansal.adminecom.models;

public class Variants {

    public String name;
    public String price;
    public int VariantCount;

    public Variants(String name, String price,int variantCount) {
        this.name = name;
        this.price = price;
        this.VariantCount= variantCount;
    }


    @Override
    public String toString() {
        return "Variant "+VariantCount+ "   \n";
    }
}
