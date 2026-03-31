package common;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Inventory implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String branchName;
    private Map<String, Drink> drinks;
    private double totalSales;
    private int totalOrders;
    private int lowStockCount;
    private Map<String, Integer> restockHistory;
    
    public Inventory(String branchName) {
        this.branchName = branchName;
        this.drinks = new HashMap<>();
        this.restockHistory = new HashMap<>();
        this.totalSales = 0;
        this.totalOrders = 0;
        this.lowStockCount = 0;
    }
    
    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }
    
    public Map<String, Drink> getDrinks() { return drinks; }
    public void setDrinks(Map<String, Drink> drinks) { this.drinks = drinks; }
    
    public double getTotalSales() { return totalSales; }
    public void setTotalSales(double totalSales) { this.totalSales = totalSales; }
    
    public int getTotalOrders() { return totalOrders; }
    public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }
    
    public int getLowStockCount() { return lowStockCount; }
    public void setLowStockCount(int lowStockCount) { this.lowStockCount = lowStockCount; }
    
    public Map<String, Integer> getRestockHistory() { return restockHistory; }
    
    public void addDrink(Drink drink) {
        drinks.put(drink.getBrand(), drink);
    }
    
    public void updateStock(String brand, int quantity) {
        Drink drink = drinks.get(brand);
        if (drink != null) {
            drink.setStockQuantity(quantity);
        }
    }
    
    public void recordRestock(String brand, int quantity) {
        restockHistory.merge(brand, quantity, Integer::sum);
        Drink drink = drinks.get(brand);
        if (drink != null) {
            drink.setStockQuantity(drink.getStockQuantity() + quantity);
        }
    }
    
    public void recordSale(String brand, int quantity, double amount) {
        totalSales += amount;
        totalOrders++;
        Drink drink = drinks.get(brand);
        if (drink != null) {
            drink.setStockQuantity(drink.getStockQuantity() - quantity);
        }
    }
    
    public String generateInventoryReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(60)).append("\n");
        sb.append(String.format("   %s BRANCH - INVENTORY REPORT\n", branchName));
        sb.append("=".repeat(60)).append("\n\n");
        
        sb.append("CURRENT STOCK LEVELS:\n");
        sb.append("-".repeat(40)).append("\n");
        for (Drink drink : drinks.values()) {
            String status = drink.isLowStock() ? "LOW STOCK" : "OK";
            sb.append(String.format("%-15s: %3d units (Min: %3d) - %s\n",
                drink.getBrand(),
                drink.getStockQuantity(),
                drink.getMinimumThreshold(),
                status));
        }
        
        sb.append("\nSALES SUMMARY:\n");
        sb.append("-".repeat(40)).append("\n");
        sb.append(String.format("Total Orders: %d\n", totalOrders));
        sb.append(String.format("Total Revenue: KSh %,.2f\n", totalSales));
        
        sb.append("\nRESTOCK HISTORY:\n");
        sb.append("-".repeat(40)).append("\n");
        if (restockHistory.isEmpty()) {
            sb.append("No restocks recorded\n");
        } else {
            for (Map.Entry<String, Integer> entry : restockHistory.entrySet()) {
                sb.append(String.format("%s: +%d units\n", entry.getKey(), entry.getValue()));
            }
        }
        
        sb.append("\nLOW STOCK ITEMS:\n");
        sb.append("-".repeat(40)).append("\n");
        boolean hasLowStock = false;
        for (Drink drink : drinks.values()) {
            if (drink.isLowStock()) {
                hasLowStock = true;
                sb.append(String.format("• %s: %d units remaining (below %d)\n",
                    drink.getBrand(),
                    drink.getStockQuantity(),
                    drink.getMinimumThreshold()));
            }
        }
        if (!hasLowStock) {
            sb.append("All stock levels are healthy\n");
        }
        
        sb.append("\n").append("=".repeat(60)).append("\n");
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return generateInventoryReport();
    }
}