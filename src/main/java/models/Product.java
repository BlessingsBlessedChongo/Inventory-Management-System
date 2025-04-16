package main.java.models;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


/**
 * Represents a product in the inventory system
 */

public class Product {
    private String id;
    private String name;
    private double price;
    private int quantity;
    private List<Double> weeklySales;
    private int minStockLevel = 10;
    private int leadTimeDays = 7;
    private LocalDate lastRestockDate;

    public Product(String id, String name, double price, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.weeklySales = new ArrayList<>();
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public int getMinStockLevel() { return minStockLevel; }
    public int getLeadTimeDays() { return leadTimeDays; }
    public List<Double> getWeeklySales() { return weeklySales; }

    // Business Methods
    public void recordSale(int quantitySold) {
        weeklySales.add((double) quantitySold);
        if (weeklySales.size() > 12) {
            weeklySales.remove(0);
        }
    }

    public double getWeeklySalesAverage() {
        if (weeklySales.isEmpty()) return 0;
        return weeklySales.stream().mapToDouble(d -> d).average().orElse(0);
    }

    @Override
    public String toString() {
        return String.format("%s - %s (Qty: %d, Price: $%.2f)", id, name, quantity, price);
    }
}