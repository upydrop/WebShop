package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static spark.Spark.*;

public class Shop {
    public static void main(String[] args) {
        ShopController shopController = new ShopController();
        DataController dataController = new DataController();
        CartController cartController = new CartController();
        ClientDataController clientDataController = new ClientDataController();
        Shop shop = new Shop();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // Allow CORS
        options("/*", (request, response) -> {
            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }

            return "OK";
        });

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Request-Method", "*");
            response.header("Access-Control-Allow-Headers", "*");
        });

        post("/register", (req, res) ->{
            String name = req.headers("name");
            String eMail = req.headers("eMail");
            if (clientDataController.checkAvailability(eMail)){
                return gson.toJson("EMail already in use!");
            }
            return gson.toJson(clientDataController.addClient(name, eMail));
        });

        get("/products", (req, res) -> {
            StandardResponse products = new StandardResponse(StatusResponse.SUCCESS, gson.toJsonTree(shopController.printProductList(shop)));
            return gson.toJson(products);
        });

        /*put("/products/:id/buy/:quantity", (req, res) -> {
            int productID = Integer.parseInt(req.params(":id")) - 1;
            double quantity = Double.parseDouble(req.params(":quantity"));
            String auth_token = req.headers("auth_token");
            Client client = clientDataController.getClient(auth_token);
            if (client == null){
                return "You are not logged in!";
            }
            Item item = shopController.getItem(shop, productID);

            if (item != null && quantity <= Double.parseDouble(item.getCount())) {
                return client.addItemToCart(item, quantity, shop.getProductList(), dataController);
            } else {
                return "Not enough product in stock. Try again!";
            }
        });

        delete("/cart/remove/:row/:quantity", (req, res) ->{
            String auth_token = req.headers("auth_token");
            Client client = clientDataController.getClient(auth_token);
            if (client == null){
                return "You are not logged in!";
            }
            if (client.getCart().getProducts().isEmpty()){
                return "You have no items in the cart!";
            }
            client.removeItemFromCart(Integer.parseInt(req.params(":row")), Double.parseDouble(req.params(":quantity")), shop.getProductList(), dataController);
            return "Success!";
        });*/

        put("/checkout", (req, res) -> {
            String auth_token = req.headers("auth_token");
            String Cart = req.headers("cart");
            Client client = clientDataController.getClient(auth_token);
            if (client == null){
                return gson.toJson("You are not registered!");
            }
            Cart cart = cartController.createCart(Cart);
            client.setCart(cart);
            return gson.toJson(shopController.checkOut(shop, client));
        });

        /*// Admin functions
        post("/admin/add/:adminPass/:name/:cost/:desc/:count", (req, res) -> {
            String adminPass = req.params(":adminPass");
            if (adminPass.equals(shopController.getAdminPass(shop))) {
                String name = req.params(":name");
                String cost = req.params(":cost");
                String desc = req.params(":desc");
                String count = req.params(":count");

                dataController.addItem(new Item(name, cost, desc, count, "0.0"));
                shopController.updateProductList(shop);
                return "Product added successfully!";
            } else {
                return "Incorrect admin password!";
            }
        });

        delete("/admin/delete/:adminPass/:name", (req, res) -> {
            String adminPass = req.params(":adminPass");
            if (adminPass.equals(shopController.getAdminPass(shop))) {
                String name = String.valueOf(req.params(":name"));

                dataController.deleteItem(name);
                shopController.updateProductList(shop);
                return "Product deleted successfully!";
            } else {
                return "Incorrect admin password!";
            }
        });

        put("/admin/update/:adminPass/:productId/:quantity", (req, res) -> {
            String adminPass = req.params(":adminPass");
            if (adminPass.equals(shopController.getAdminPass(shop))) {
                int productId = Integer.parseInt(req.params(":productId"));
                double quantity = Double.parseDouble(req.params(":quantity"));

                Item item = shopController.getItem(shop, productId-1);
                shop.changeItemCount(item, quantity);
                dataController.updateProducts(shop.getProductList());

                return "Product stock updated successfully!";
            } else {
                return "Incorrect admin password!";
            }
        });*/
    }

}
