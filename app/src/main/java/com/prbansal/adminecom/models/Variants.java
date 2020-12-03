package com.prbansal.adminecom.models;

public class Variants {

    public String name;
    public int price;
    private int VariantCount;

    public Variants(String name, int price) {
        this.name = name;
        this.price = price;
        this.VariantCount++;
    }

    public Variants() {
    }

    @Override
    public String toString() {
        return "Variant "+VariantCount+ "   \n";
    }
}
