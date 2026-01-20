package org.example;

import java.util.Collection;

public class ShopController {

    public Collection<Item> printProductList(Shop shop){ return shop.printProductList(); }

    public String checkOut(Shop shop, Client client) { return shop.checkOut(client); }

    public Item getItem(Shop shop, Integer itemNum) { return shop.getItem(itemNum); }

    public String getAdminPass(Shop shop) { return shop.getAdminPass(); }

    public void updateProductList(Shop shop) { shop.updateProductList(); }
}
