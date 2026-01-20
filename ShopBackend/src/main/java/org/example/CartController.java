package org.example;

import com.google.gson.*;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class CartController {
    public String addItem(Cart cart, Item item, Double productCount, ArrayList<Item> productList, DataController dataController){
        if (cart.getProducts().keySet().stream().anyMatch(Item -> Item.equals(item))){
            cart.getProducts().put(item, cart.getProducts().get(item)+productCount);
        } else{
            cart.getProducts().put(item, productCount);
        }
        dataController.updateProducts(productList);
        Double Sum = Double.parseDouble(item.getCost()) * productCount;
        cart.addCartSum(Sum);
        return "Success! Added to your cart";
    }

    public void removeItem(Cart cart, Integer productNum, double productCount, ArrayList<Item> productList, DataController dataController){
        HashMap<Item, Double> cartProducts= cart.getProducts();
        Set<Item> keyset = cartProducts.keySet();
        Item[] keyarray = keyset.toArray(new Item[0]);
        Item key = keyarray[productNum-1];
        if (cartProducts.get(key) - productCount > 0) {
            cartProducts.replace(key, cartProducts.get(key) - productCount);
        } else{
            cartProducts.remove(key);
        }
        dataController.updateProducts(productList);
        try {
            double Sum = (DecimalFormat.getNumberInstance().parse(key.getCost()).doubleValue())*productCount;
            cart.removeCartSum(Sum);
        } catch (ParseException e) {
            System.out.println("Technical difficulties...");
        }
    }

    public HashMap<String, Double> printCart(Cart cart){
        HashMap<Item, Double> products = cart.getProducts();
        HashMap<String, Double> cartProducts = new HashMap<>();
        Set<Item> keyset = products.keySet();
        Item[] keyarray = keyset.toArray(new Item[0]);
        for ( int i = 0; i < products.size(); i++){
            Item key = keyarray[i];
            cartProducts.put(key.getName() + " количество: " + products.get(key), Double.parseDouble(key.getCost())*products.get(key));
        }
        cartProducts.put("Сумма корзины: ", cart.getCartSum());
        return cartProducts;
    }

    public Cart createCart(String cartJson) {
        HashMap<Item, Double> products = new HashMap<>();

        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(cartJson).getAsJsonObject();
        JsonObject params = json.getAsJsonObject("products");

        for (Map.Entry<String, JsonElement> entry : params.entrySet()) {
            String key = entry.getKey();
            JsonArray array = entry.getValue().getAsJsonArray();

            double amount = array.size() > 0 ? array.get(0).getAsDouble() : 0.0;

            Item item = new Item(key, "0", "0", "0");

            products.put(item, amount);
        }
        double cartSum = json.get("cartSum").getAsDouble();

        Cart cart = new Cart();
        cart.setProducts(products);
        cart.setCartSum(cartSum);

        return cart;
    }
}
