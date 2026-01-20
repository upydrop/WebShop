package org.example;

import java.util.ArrayList;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;

public class DataController {
    private final MongoCollection<Document> productsDB;

    public DataController(){
        String connectionString = "mongodb+srv://valerijss:Valerarul1@cluster0.2tk53v8.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0";
        MongoClient mongoClient = MongoClients.create(connectionString);
        MongoDatabase database = mongoClient.getDatabase("ZaShop");
        this.productsDB = database.getCollection("products");
    }

    public ArrayList<Item> getItems() {
        ArrayList<Item> productList = new ArrayList<>();
        FindIterable<Document> documents = productsDB.find();
        for (Document doc : documents) {
            productList.add(new Item(doc.getString("Name"), doc.getString("Cost"), doc.getString("Description"), doc.getString("Count")));
        }
        return productList;
    }

    public void deleteItem(String name){
        productsDB.deleteOne(Filters.eq("Name", name));
    }

    public void updateProducts(ArrayList<Item> products) {
        for (int i = 0; i < products.size(); i++){
            Item item = products.get(i);
            Document filter = new Document("Name", item.getName());
            Document update = new Document("$set", new Document("Name", item.getName())
                    .append("Cost", item.getCost())
                    .append("Description", item.getDescription())
                    .append("Count", item.getCount()));
            productsDB.updateOne(filter, update);
        }
    }
}