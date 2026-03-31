package common;

import java.io.Serializable;

public class BranchInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String branchName;
    private double totalSales;
    private int totalOrders;
    private int lowStockCount;
    
    public BranchInfo() {}
    
    public BranchInfo(String branchName, double totalSales, int totalOrders, int lowStockCount) {
        this.branchName = branchName;
        this.totalSales = totalSales;
        this.totalOrders = totalOrders;
        this.lowStockCount = lowStockCount;
    }
    
    // Getters and Setters
    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }
    
    public double getTotalSales() { return totalSales; }
    public void setTotalSales(double totalSales) { this.totalSales = totalSales; }
    
    public int getTotalOrders() { return totalOrders; }
    public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }
    
    public int getLowStockCount() { return lowStockCount; }
    public void setLowStockCount(int lowStockCount) { this.lowStockCount = lowStockCount; }
}