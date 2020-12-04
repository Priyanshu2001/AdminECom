package com.prbansal.adminecom.models;

public class CartItem {
    public String name;
    public int price;
    public float qty;
    public int pricePerKg;

    public CartItem() {
    }

    public CartItem(String name, int price, float qty) {
        this.name = name;
        this.price = price;
        this.qty = qty;
    }

    public CartItem(String name, int price) {
        this.name = name;
        this.price = price;
        this.qty=1;
    }

    public CartItem(String name, int price, float qty, int pricePerKg) {
        this.name = name;
        this.price = price;
        this.qty = qty;
        this.pricePerKg = pricePerKg;
    }
    @Override
    public String toString() {
        return ": Qty= "+qty + "  Rs. "+ price+"\n";
    }
}
