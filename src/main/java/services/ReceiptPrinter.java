package main.java.services;

import main.java.models.Order;
import main.java.models.OrderItem;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Handles receipt generation and printing
 * **/
public class ReceiptPrinter {
    private static final int LINE_LENGTH = 40;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void printReceiptToConsole(Order order){
        String receipt = generateReceipt(order);
        System.out.println(receipt);
    }

    public static void saveReceiptToFile(Order order)throws IOException{
        String receipt = generateReceipt(order);
        String filename = "receipts/" + order.getOrderId() + "txt";

        try(BufferedWriter writer = new BufferedWriter(new FileWriter(filename))){
            writer.write(receipt);
        }
    }
    private static String generateReceipt(Order order){
        StringBuilder receipt = new StringBuilder();

        //Header
// Header
        receipt.append("=".repeat(LINE_LENGTH)).append("\n");
        receipt.append(centerText("INVENTORY SYSTEM")).append("\n");
        receipt.append(centerText("Official Receipt")).append("\n");
        receipt.append("-".repeat(LINE_LENGTH)).append("\n");

        // Order Info
        receipt.append(String.format("%-15s: %s%n", "Order ID", order.getOrderId()));
        receipt.append(String.format("%-15s: %s%n", "Date", DATE_FORMAT.format(order.getOrderDate())));
        receipt.append(String.format("%-15s: %s%n", "Customer", order.getCustomerName()));
        receipt.append("-".repeat(LINE_LENGTH)).append("\n");

        // Items Header
        receipt.append(String.format("%-20s %8s %10s%n", "ITEM", "QTY", "PRICE"));
        receipt.append("-".repeat(LINE_LENGTH)).append("\n");

        // Items
        for (OrderItem item : order.getItems()) {
            receipt.append(String.format("%-20s %8d %10.2f%n",
                    item.getProduct().getName(),
                    item.getQuantity(),
                    item.getTotalPrice()));
        }

        // Footer
        receipt.append("-".repeat(LINE_LENGTH)).append("\n");
        receipt.append(String.format("%-20s %18.2f%n", "TOTAL:", order.getTotal()));
        receipt.append("=".repeat(LINE_LENGTH)).append("\n");
        receipt.append(centerText("Thank you for your purchase!")).append("\n");
        receipt.append(centerText("Scan QR code to return")).append("\n");
        receipt.append(generateSimpleQRCode()).append("\n"); // ASCII art QR placeholder
        receipt.append("=".repeat(LINE_LENGTH)).append("\n");
        return receipt.toString();
    }

    private static String centerText(String text) {
        int padding = (LINE_LENGTH - text.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + text;
    }

    private static String generateSimpleQRCode() {
        // This is a placeholder for actual QR generation
        // In a real system, you'd use a library like ZXing
        return """
               ██████████████
               █∙∙∙∙∙∙∙∙∙∙∙█
               █∙████∙∙████∙█
               █∙████∙∙████∙█
               █∙████∙∙████∙█
               █∙∙∙∙∙∙∙∙∙∙∙█
               █████∙██∙█████
               █∙∙∙∙∙∙∙∙∙∙∙█
               ██████████████
               """;

    }
}
