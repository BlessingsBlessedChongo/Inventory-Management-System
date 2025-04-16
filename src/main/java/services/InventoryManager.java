package main.java.services;

import main.java.models.Product;
import main.java.models.Order;
import main.java.utils.FileHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Manages inventory operations including CRUD and reporting
 */

public class InventoryManager {
    private List<Product> products;
    private List<Order> orders;
    private static final String PRODUCTS_FILE = "data/products.dat";
    private static final String ORDERS_FILE = "data/orders.dat";

    public InventoryManager() {
        this.products = Optional.ofNullable(FileHandler.loadProducts(PRODUCTS_FILE))
                .orElseGet(ArrayList::new);
        this.orders = Optional.ofNullable(FileHandler.loadOrders(ORDERS_FILE))
                .orElseGet(ArrayList::new);
    }

    // Product CRUD Operations
    public void addProduct(Product product) {
        products.add(product);
        saveData();
    }

    public Product findProductById(String id) {
        return products.stream()
                .filter(p -> p.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(products); // Defensive copy
    }

    public void updateProduct(String id, Product updatedProduct) {
        products.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .ifPresent(existing -> {
                    existing.setName(updatedProduct.getName());
                    existing.setPrice(updatedProduct.getPrice());
                    existing.setQuantity(updatedProduct.getQuantity());
                    saveData();
                });
    }

    public void deleteProduct(String id) {
        products.removeIf(p -> p.getId().equals(id));
        saveData();
    }

    // Order Processing
    public Order createOrder(String customerName) {
        Order newOrder = new Order(customerName);
        orders.add(newOrder);
        return newOrder;
    }

    public void processOrder(Order order) {
        order.getItems().forEach(item -> {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() - item.getQuantity());
            product.recordSale(item.getQuantity());
        });
        saveData();
    }

    // Data Persistence
    private void saveData() {
        FileHandler.saveProducts(PRODUCTS_FILE, products);
        FileHandler.saveOrders(ORDERS_FILE, orders);
    }

    // Additional methods used by MainApp
    public List<Order> getAllOrders() {
        return new ArrayList<>(orders);
    }
}