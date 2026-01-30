package org.example;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import java.util.ArrayList;
import org.apache.commons.lang3.RandomStringUtils;

public class ClientDataController {
    private final MongoCollection<Document> clientsDB;
    private ArrayList<Client> clients;

    public ClientDataController(){
        String connectionString = "<connection link to MongoDB>";
        MongoClient mongoClient = MongoClients.create(connectionString);
        MongoDatabase database = mongoClient.getDatabase("ZaShop");
        this.clientsDB = database.getCollection("clients");
        this.clients = this.getClients();
    }

    public ArrayList<Client> getClients(){
        ArrayList<Client> clientList = new ArrayList<>();
        FindIterable<Document> documents = clientsDB.find();
        for (Document doc : documents) {
            clientList.add(new Client(doc.getString("name"), doc.getString("eMail"), doc.getString("auth_token")));
        }
        return clientList;
    }

    public String addClient(String name, String eMail){
        String token = RandomStringUtils.randomAlphanumeric(4);
        Document client = new Document("name", name).append("eMail", eMail).append("auth_token", token);
        clientsDB.insertOne(client);
        clients.add(new Client(name, eMail, token));
        return token;
    }

    public boolean checkAvailability(String eMail){
        return clients.stream()
                .anyMatch(client -> client.getEmail().equals(eMail));
    }

    public void deleteClient(Client client){
        clients.remove(client);
        clientsDB.deleteOne(Filters.eq("eMail", client.getEmail()));
    }

    public Client getClient(String auth_token) {
        return clients.stream()
                .filter(client -> client.getAuth_token().equals(auth_token))
                .findFirst()
                .orElse(null);
    }
}

