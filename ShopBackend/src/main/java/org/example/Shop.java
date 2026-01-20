package org.example;

import java.util.*;

public class Shop {
    private ArrayList<Item> productList;

    public Shop(){
        DataController dataController = new DataController();
        this.productList = dataController.getItems();
    }

    public String getAdminPass() {
        return "ShopPass";}

    public Item getItem(Integer ItemNum){ return this.productList.get(ItemNum); }
    public ArrayList<Item> getProductList() { return productList; }

    public void updateProductList(){
        DataController dataController = new DataController();
        this.productList = dataController.getItems();
    }

    public Collection<Item> printProductList() {
        return productList;
    }

    public void removeItemCount(Item toChange, Double productCount){
        for (int i = 0; i < productList.size(); i++){
            Item item = productList.get(i);
            if (Objects.equals(item.getName(), toChange.getName())){
                productList.set(i, new Item(item.getName(), item.getCost(), item.getDescription(), Double.toString(Double.parseDouble(item.getCount()) - productCount)));
            }
        }
    }


    public void changeItemCount(Item toChange, Double productCount){
        DataController dataController = new DataController();
        for (int i = 0; i < productList.size(); i++){
            if (productList.get(i) == toChange){
                productList.set(i, new Item(toChange.getName(), toChange.getCost(), toChange.getDescription(), Double.toString(Double.parseDouble(toChange.getCount()) + productCount)));
            }
        }
        dataController.updateProducts(this.getProductList());
    }

    public String checkOut(Client client){
        Cart cart = client.getCart();
        if (cart.getProducts().isEmpty()){
            return "You have no products in the cart!";
        }
        DataController dataController = new DataController();
        HashMap<Item, Double> cartProducts = cart.getProducts();
        Set<Item> keyset = cartProducts.keySet();
        Item[] keyarray = keyset.toArray(new Item[0]);
        for (int i = 0; i < cartProducts.size(); i++){
            Item toChange = keyarray[i];
            Item item = productList.stream()
                    .filter(itemm -> itemm.getName().equals(toChange.getName()))
                    .findFirst()
                    .orElse(null);
            if (Double.parseDouble(item.getCount()) == 0.0){
                return ":( " +  item.getName() + "'s is out of stock";
            }
            if (Double.parseDouble(item.getCount()) < cartProducts.get(toChange)) {
                return ":( " + item.getName() + "'s stocks are not enough for your order";
            }
        }
        for (int i = 0; i < cartProducts.size(); i++){
            Item toChange = keyarray[i];
            Item item = productList.stream()
                    .filter(itemm -> itemm.getName().equals(toChange.getName()))
                    .findFirst()
                    .orElse(null);
            removeItemCount(item, cartProducts.get(toChange));
        }
        cart.getProducts().clear();
        cart.removeCartSum(cart.getCartSum());
        dataController.updateProducts(productList);
        return "Success!";
    }
}
