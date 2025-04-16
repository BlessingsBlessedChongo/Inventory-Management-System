package main.java.services;

import main.java.models.Product;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contains AI-like algorithms for inventory optimization
 */
public class InventoryAI {
    /**
     * Identifies products that need reordering based on sales velocity
     */
    public static List<Product> generateReorderSuggestions(List<Product> inventory) {
        return inventory.stream()
                .filter(p -> {
                    double weeklySales = p.getWeeklySalesAverage();
                    if (weeklySales == 0) return false;

                    int daysOfStockLeft = (int)((p.getQuantity() / weeklySales) * 7);
                    int leadTimeBuffer = (int)(p.getLeadTimeDays() * 1.2); // 20% buffer
                    return daysOfStockLeft < leadTimeBuffer;
                })
                .sorted(Comparator.comparingInt(Product::getQuantity))
                .collect(Collectors.toList());
    }

    /**
     * Recommends discounts for slow-moving products
     */
    public static Map<Product, Double> generateDiscountRecommendations(List<Product> inventory) {
        return inventory.stream()
                .filter(p -> {
                    double weeksOfStock = p.getQuantity() / Math.max(p.getWeeklySalesAverage(), 1);
                    return weeksOfStock > 8; // More than 8 weeks of inventory
                })
                .collect(Collectors.toMap(
                        p -> p,
                        p -> {
                            double weeksOfStock = p.getQuantity() / Math.max(p.getWeeklySalesAverage(), 1);
                            if (weeksOfStock > 12) return 0.25; // 25% discount
                            if (weeksOfStock > 8) return 0.15;   // 15% discount
                            return 0.10;                         // 10% discount
                        }
                ));
    }

    /**
     * Predicts sales for next period using weighted moving average
     */
    public static Map<Product, Double> predictSales(List<Product> inventory) {
        return inventory.stream()
                .collect(Collectors.toMap(
                        p -> p,
                        p -> {
                            List<Double> sales = p.getWeeklySales();
                            if (sales.size() < 3) return p.getWeeklySalesAverage();

                            // Weighted average (recent weeks more important)
                            double sum = 0;
                            double weight = 1;
                            double totalWeight = 0;

                            for (int i = sales.size() - 1; i >= 0; i--) {
                                sum += sales.get(i) * weight;
                                totalWeight += weight;
                                weight *= 0.9; // Reduce weight for older data
                            }

                            return sum / totalWeight;
                        }
                ));
    }
}