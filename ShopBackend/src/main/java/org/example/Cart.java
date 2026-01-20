package org.example;

import java.util.HashMap;

public class Cart {
    private HashMap<Item, Double> products;
    private double cartSum;

    public Cart(){
        this.products = new HashMap<>();
        this.cartSum = 0.0;
    }

    public HashMap<Item, Double> getProducts(){ return products; }
    public double getCartSum() { return cartSum; }

    public void setProducts(HashMap<Item, Double> products){
        this.products = products;
    }
    public void setCartSum(Double cartSum){
        this.cartSum = cartSum;
    }

    public void addCartSum(Double Sum){ this.cartSum += Sum; }
    public void removeCartSum(double Sum){ this.cartSum -= Sum; }
}
