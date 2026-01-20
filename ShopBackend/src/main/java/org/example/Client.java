package org.example;


import java.util.ArrayList;

public class Client {
    private Cart cart;
    private final String eMail;
    private final String name;
    private final String auth_token;

    public Client(String name, String eMail, String auth_token){
        this.cart = new Cart();
        this.eMail = eMail;
        this.name = name;
        this.auth_token = auth_token;
    }

    public Cart getCart() { return cart; }
    public String getName() { return name; }
    public String getEmail() { return eMail; }
    public String getAuth_token() { return auth_token; }

    public void setCart(Cart cart) { this.cart = cart; }

    public String addItemToCart(Item item, Double productCount, ArrayList<Item> productList, DataController dataController){
        CartController cartController = new CartController();
        return cartController.addItem(this.cart, item, productCount, productList, dataController);
    }

    public void removeItemFromCart(Integer productNum, Double productCount, ArrayList<Item> productList, DataController dataController){
        CartController cartController = new CartController();
        cartController.removeItem(this.cart, productNum, productCount, productList, dataController);
    }

}