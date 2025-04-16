package main.java.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents a customer order
 */
public class Order {
    private String orderId;
    private Date orderDate;
    private String customerName;
    private List<OrderItem> items;

    public Order(String customerName) {
        this.orderId = "ORD-" + System.currentTimeMillis();
        this.orderDate = new Date();
        this.customerName = customerName;
        this.items = new ArrayList<>();
    }

    public void addItem(Product product, int quantity) {
        items.add(new OrderItem(product, quantity));
    }

    public double getTotal() {
        return items.stream().mapToDouble(OrderItem::getTotalPrice).sum();
    }

    public List<OrderItem> getItems() { return items; }
    public String getOrderId() { return orderId; }
    public Date getOrderDate() { return orderDate; }
    public String getCustomerName() { return customerName; }
}
