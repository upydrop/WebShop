package org.example;

public class Item {
    private final String name;
    private final String cost;
    private final String description;
    private String count;

    public Item(String name, String cost, String description, String count) {
        this.name = name;
        this.description = description;
        this.cost = cost;
        this.count = count;
    }

    public String getName(){return name;}
    public String getCost(){return cost;}
    public String getDescription(){return description;}
    public String getCount(){return count;}
}
