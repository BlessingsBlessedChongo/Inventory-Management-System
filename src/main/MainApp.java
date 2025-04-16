package main;

import main.java.models.*;
import main.java.services.*;
import main.java.utils.Validation;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class MainApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final InventoryManager inventoryManager = new InventoryManager();
    private static final UserManager userManager = new UserManager();
    private static boolean running = true;

    public static void main(String[] args) {
        // Initialize data directories
        initDataDirectories();

        while (running) {
            User currentUser = userManager.getCurrentUser();
            if (currentUser == null) {
                showLoginScreen();
            } else {
                showMainMenu();
            }
        }
        scanner.close();
    }

    private static void initDataDirectories() {
        try {
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get("data"));
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get("receipts"));
        } catch (java.io.IOException e) {
            System.err.println("Failed to create data directories: " + e.getMessage());
        }
    }

    // ... rest of your MainApp methods remain the same ...


    private static void showWelcomeScreen() {
        System.out.println("====================================");
        System.out.println("    INVENTORY MANAGEMENT SYSTEM     ");
        System.out.println("====================================");
        System.out.println();
    }

    private static void showLoginScreen() {
        System.out.println("\nPlease login to continue");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Choose option: ");

        int choice = Validation.getValidInt(scanner, "", 1, 3);

        switch (choice) {
            case 1:
                handleLogin();
                break;
            case 2:
                handleRegistration();
                break;
            case 3:
                running = false;
                break;
        }
    }

    private static void handleLogin() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        if (userManager.login(username, password)) {
            System.out.println("\nLogin successful! Welcome, " + username);
        } else {
            System.out.println("\nInvalid username or password. Please try again.");
        }
    }

    private static void handleRegistration() {
        String username = Validation.getValidInput(scanner, "Enter username (3-15 chars): ", "^[a-zA-Z0-9]{3,15}$");
        String password = Validation.getValidInput(scanner, "Enter password (6+ chars): ", ".{6,}");

        if (userManager.register(username, password, "staff")) {
            System.out.println("Registration successful! You can now login.");
        } else {
            System.out.println("Username already exists. Please try another.");
        }
    }

    private static void showMainMenu() {
        System.out.println("\n=== MAIN MENU ===");
        System.out.println("1. Product Management");
        System.out.println("2. Sales Processing");
        System.out.println("3. Reports & Analytics");
        System.out.println("4. User Management");
        System.out.println("5. Logout");
        System.out.println("6. Exit System");
        System.out.print("Choose option: ");

        int choice = Validation.getValidInt(scanner, "", 1, 6);

        switch (choice) {
            case 1:
                showProductMenu();
                break;
            case 2:
                processSale();
                break;
            case 3:
                showReportsMenu();
                break;
            case 4:
                if (userManager.isAdminLoggedIn()) {
                    showUserManagementMenu();
                } else {
                    System.out.println("Access denied. Admin privileges required.");
                }
                break;
            case 5:
                userManager.logout();
                System.out.println("Logged out successfully.");
                break;
            case 6:
                running = false;
                break;
        }
    }
    private static void showProductMenu() {
        while (userManager.getCurrentUser() != null) {
            System.out.println("\n=== PRODUCT MANAGEMENT ===");
            System.out.println("1. List All Products");
            System.out.println("2. Add New Product");
            System.out.println("3. Update Product");
            System.out.println("4. Delete Product");
            System.out.println("5. Back to Main Menu");
            System.out.print("Choose option: ");

            int choice = Validation.getValidInt(scanner, "", 1, 5);

            switch (choice) {
                case 1:
                    listAllProducts();
                    break;
                case 2:
                    addNewProduct();
                    break;
                case 3:
                    updateProduct();
                    break;
                case 4:
                    deleteProduct();
                    break;
                case 5:
                    return;
            }
        }
    }

    private static void listAllProducts() {
        List<Product> products = inventoryManager.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("\nNo products found in inventory.");
            return;
        }

        System.out.println("\n=== PRODUCT LIST ===");
        System.out.printf("%-8s %-20s %-10s %-8s%n", "ID", "Name", "Price", "Qty");
        System.out.println("----------------------------------------");
        for (Product product : products) {
            System.out.printf("%-8s %-20s $%-9.2f %-8d%n",
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.getQuantity());
        }
    }

    private static void addNewProduct() {
        System.out.println("\n=== ADD NEW PRODUCT ===");
        String id = Validation.getValidInput(scanner, "Enter product ID: ", "^[A-Za-z0-9]{3,10}$");

        if (inventoryManager.findProductById(id) != null) {
            System.out.println("Product ID already exists!");
            return;
        }

        String name = Validation.getValidInput(scanner, "Enter product name: ", "^[A-Za-z0-9 ]{3,50}$");
        double price = Validation.getValidDouble(scanner, "Enter product price: ", 0.01);
        int quantity = Validation.getValidInt(scanner, "Enter initial quantity: ", 0, Integer.MAX_VALUE);

        Product newProduct = new Product(id, name, price, quantity);
        inventoryManager.addProduct(newProduct);
        System.out.println("Product added successfully!");
    }

    private static void updateProduct() {
        System.out.println("\n=== UPDATE PRODUCT ===");
        String id = Validation.getValidInput(scanner, "Enter product ID to update: ", "^[A-Za-z0-9]{3,10}$");
        Product product = inventoryManager.findProductById(id);

        if (product == null) {
            System.out.println("Product not found!");
            return;
        }

        System.out.println("Current details:");
        System.out.println("1. Name: " + product.getName());
        System.out.println("2. Price: " + product.getPrice());
        System.out.println("3. Quantity: " + product.getQuantity());
        System.out.print("Enter field number to update (1-3) or 0 to cancel: ");

        int field = Validation.getValidInt(scanner, "", 0, 3);
        if (field == 0) return;

        switch (field) {
            case 1:
                String newName = Validation.getValidInput(scanner, "Enter new name: ", "^[A-Za-z0-9 ]{3,50}$");
                product.setName(newName);
                break;
            case 2:
                double newPrice = Validation.getValidDouble(scanner, "Enter new price: ", 0.01);
                product.setPrice(newPrice);
                break;
            case 3:
                int newQty = Validation.getValidInt(scanner, "Enter new quantity: ", 0, Integer.MAX_VALUE);
                product.setQuantity(newQty);
                break;
        }

        inventoryManager.updateProduct(id, product);
        System.out.println("Product updated successfully!");
    }

    private static void deleteProduct() {
        System.out.println("\n=== DELETE PRODUCT ===");
        String id = Validation.getValidInput(scanner, "Enter product ID to delete: ", "^[A-Za-z0-9]{3,10}$");

        if (inventoryManager.findProductById(id) == null) {
            System.out.println("Product not found!");
            return;
        }

        System.out.print("Are you sure you want to delete this product? (y/n): ");
        String confirm = scanner.nextLine().toLowerCase();
        if (confirm.equals("y")) {
            inventoryManager.deleteProduct(id);
            System.out.println("Product deleted successfully!");
        }
    }
    private static void processSale() {
        System.out.println("\n=== PROCESS SALE ===");
        System.out.print("Enter customer name: ");
        String customerName = scanner.nextLine();

        Order order = inventoryManager.createOrder(customerName);
        boolean addingItems = true;

        while (addingItems) {
            System.out.println("\nCurrent Order:");
            if (order.getItems().isEmpty()) {
                System.out.println("No items added yet");
            } else {
                order.getItems().forEach(item ->
                        System.out.printf("%s x%d @ $%.2f = $%.2f%n",
                                item.getProduct().getName(),
                                item.getQuantity(),
                                item.getProduct().getPrice(),
                                item.getTotalPrice()));
                System.out.printf("TOTAL: $%.2f%n", order.getTotal());
            }

            System.out.println("\n1. Add Item");
            System.out.println("2. Remove Item");
            System.out.println("3. Complete Sale");
            System.out.println("4. Cancel Sale");
            System.out.print("Choose option: ");

            int choice = Validation.getValidInt(scanner, "", 1, 4);

            switch (choice) {
                case 1:
                    addItemToOrder(order);
                    break;
                case 2:
                    removeItemFromOrder(order);
                    break;
                case 3:
                    completeSale(order);
                    addingItems = false;
                    break;
                case 4:
                    System.out.println("Sale cancelled.");
                    addingItems = false;
                    break;
            }
        }
    }

    private static void addItemToOrder(Order order) {
        System.out.println("\n=== ADD ITEM ===");
        String id = Validation.getValidInput(scanner, "Enter product ID: ", "^[A-Za-z0-9]{3,10}$");
        Product product = inventoryManager.findProductById(id);

        if (product == null) {
            System.out.println("Product not found!");
            return;
        }

        System.out.println("Product: " + product.getName());
        System.out.println("Available: " + product.getQuantity());
        int qty = Validation.getValidInt(scanner, "Enter quantity: ", 1, product.getQuantity());

        order.addItem(product, qty);
        System.out.printf("Added %d x %s to order%n", qty, product.getName());
    }

    private static void removeItemFromOrder(Order order) {
        if (order.getItems().isEmpty()) {
            System.out.println("No items to remove!");
            return;
        }

        System.out.println("\n=== REMOVE ITEM ===");
        for (int i = 0; i < order.getItems().size(); i++) {
            OrderItem item = order.getItems().get(i);
            System.out.printf("%d. %s x%d%n", i+1, item.getProduct().getName(), item.getQuantity());
        }

        int itemNum = Validation.getValidInt(scanner, "Enter item number to remove: ", 1, order.getItems().size());
        OrderItem removed = order.getItems().remove(itemNum - 1);
        System.out.printf("Removed %s from order%n", removed.getProduct().getName());
    }

    private static void completeSale(Order order) {
        if (order.getItems().isEmpty()) {
            System.out.println("Cannot complete empty order!");
            return;
        }

        inventoryManager.processOrder(order);

        System.out.println("\n=== SALE COMPLETE ===");
        ReceiptPrinter.printReceiptToConsole(order);

        try {
            ReceiptPrinter.saveReceiptToFile(order);
            System.out.println("Receipt saved to file");
        } catch (IOException e) {
            System.out.println("Error saving receipt: " + e.getMessage());
        }

        System.out.printf("Total: $%.2f%n", order.getTotal());
        System.out.println("Thank you for your business!");
    }
    private static void showReportsMenu() {
        while (userManager.getCurrentUser() != null) {
            System.out.println("\n=== REPORTS & ANALYTICS ===");
            System.out.println("1. Inventory Status");
            System.out.println("2. Reorder Recommendations");
            System.out.println("3. Discount Suggestions");
            System.out.println("4. Sales Predictions");
            System.out.println("5. Back to Main Menu");
            System.out.print("Choose option: ");

            int choice = Validation.getValidInt(scanner, "", 1, 5);

            switch (choice) {
                case 1:
                    showInventoryStatus();
                    break;
                case 2:
                    showReorderRecommendations();
                    break;
                case 3:
                    showDiscountSuggestions();
                    break;
                case 4:
                    showSalesPredictions();
                    break;
                case 5:
                    return;
            }
        }
    }

    private static void showInventoryStatus() {
        List<Product> products = inventoryManager.getAllProducts();
        if (products.isEmpty()) {
            System.out.println("\nNo products in inventory");
            return;
        }

        System.out.println("\n=== INVENTORY STATUS ===");
        System.out.printf("%-8s %-20s %-8s %-12s %-10s%n",
                "ID", "Name", "Qty", "Wkly Sales", "Status");
        System.out.println("--------------------------------------------------");

        for (Product p : products) {
            String status;
            if (p.getQuantity() == 0) {
                status = "OUT OF STOCK";
            } else if (p.getQuantity() < p.getMinStockLevel()) {
                status = "LOW STOCK";
            } else {
                status = "OK";
            }

            System.out.printf("%-8s %-20s %-8d %-12.2f %-10s%n",
                    p.getId(), p.getName(), p.getQuantity(),
                    p.getWeeklySalesAverage(), status);
        }
    }

    private static void showReorderRecommendations() {
        List<Product> recommendations = InventoryAI.generateReorderSuggestions(
                inventoryManager.getAllProducts());

        if (recommendations.isEmpty()) {
            System.out.println("\nNo reorder recommendations at this time");
            return;
        }

        System.out.println("\n=== REORDER RECOMMENDATIONS ===");
        System.out.printf("%-8s %-20s %-8s %-12s %-15s%n",
                "ID", "Name", "Qty", "Wkly Sales", "Urgency");
        System.out.println("-------------------------------------------------------");

        for (Product p : recommendations) {
            double weeksLeft = p.getQuantity() / p.getWeeklySalesAverage();
            String urgency;
            if (weeksLeft < 1) urgency = "CRITICAL";
            else if (weeksLeft < 2) urgency = "HIGH";
            else urgency = "MEDIUM";

            System.out.printf("%-8s %-20s %-8d %-12.2f %-15s%n",
                    p.getId(), p.getName(), p.getQuantity(),
                    p.getWeeklySalesAverage(), urgency);
        }
    }

    private static void showDiscountSuggestions() {
        Map<Product, Double> suggestions = InventoryAI.generateDiscountRecommendations(
                inventoryManager.getAllProducts());

        if (suggestions.isEmpty()) {
            System.out.println("\nNo discount recommendations at this time");
            return;
        }

        System.out.println("\n=== DISCOUNT SUGGESTIONS ===");
        System.out.printf("%-8s %-20s %-8s %-12s %-10s%n",
                "ID", "Name", "Qty", "Wkly Sales", "Discount");
        System.out.println("--------------------------------------------------");

        suggestions.forEach((product, discount) -> {
            System.out.printf("%-8s %-20s %-8d %-12.2f %-10.0f%%%n",
                    product.getId(), product.getName(), product.getQuantity(),
                    product.getWeeklySalesAverage(), discount * 100);
        });
    }

    private static void showSalesPredictions() {
        Map<Product, Double> predictions = InventoryAI.predictSales(
                inventoryManager.getAllProducts());

        if (predictions.isEmpty()) {
            System.out.println("\nNo sales data available for predictions");
            return;
        }

        System.out.println("\n=== SALES PREDICTIONS (Next Week) ===");
        System.out.printf("%-8s %-20s %-12s %-12s%n",
                "ID", "Name", "Last Week", "Predicted");
        System.out.println("-----------------------------------------------");

        predictions.forEach((product, predicted) -> {
            double lastWeek = product.getWeeklySalesAverage();
            System.out.printf("%-8s %-20s %-12.2f %-12.2f%n",
                    product.getId(), product.getName(), lastWeek, predicted);
        });
    }
    private static void showUserManagementMenu() {
        while (userManager.isAdminLoggedIn()) {
            System.out.println("\n=== USER MANAGEMENT ===");
            System.out.println("1. List All Users");
            System.out.println("2. Create New User");
            System.out.println("3. Change User Role");
            System.out.println("4. Delete User");
            System.out.println("5. Back to Main Menu");
            System.out.print("Choose option: ");

            int choice = Validation.getValidInt(scanner, "", 1, 5);

            switch (choice) {
                case 1:
                    listAllUsers();
                    break;
                case 2:
                    createNewUser();
                    break;
                case 3:
                    changeUserRole();
                    break;
                case 4:
                    deleteUser();
                    break;
                case 5:
                    return;
            }
        }
    }

    private static void listAllUsers() {
        // Implementation would go here
        System.out.println("\nUser list functionality would be implemented here");
    }

    private static void createNewUser() {
        // Implementation would go here
        System.out.println("\nCreate user functionality would be implemented here");
    }

    private static void changeUserRole() {
        // Implementation would go here
        System.out.println("\nChange role functionality would be implemented here");
    }

    private static void deleteUser() {
        // Implementation would go here
        System.out.println("\nDelete user functionality would be implemented here");
    }
    // Additional menu methods would follow here...
    // (Product Management, Sales Processing, Reports, etc.)
    // Let me know if you want me to show these implementations
}