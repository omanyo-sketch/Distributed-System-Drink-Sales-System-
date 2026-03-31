package common;

import java.io.Serializable;

public class Drink implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String brand;
    private double price;
    private int stockQuantity;
    private int minimumThreshold;
    private String branchName;
    
    public Drink() {}
    
    public Drink(String brand, double price, int stockQuantity, int minimumThreshold, String branchName) {
        this.brand = brand;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.minimumThreshold = minimumThreshold;
        this.branchName = branchName;
    }
    
    // Getters and Setters
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public int getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(int stockQuantity) { this.stockQuantity = stockQuantity; }
    
    public int getMinimumThreshold() { return minimumThreshold; }
    public void setMinimumThreshold(int minimumThreshold) { this.minimumThreshold = minimumThreshold; }
    
    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }
    
    public boolean isLowStock() {
        return stockQuantity <= minimumThreshold;
    }
}