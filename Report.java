package common;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class Report implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private LocalDateTime reportDate;
    private Map<String, Double> branchSales;
    private double totalSales;
    private List<String> customers;
    private Map<String, List<Order>> ordersByBranch;
    private Map<String, Integer> lowStockItems;
    
    public Report() {
        this.reportDate = LocalDateTime.now();
    }
    
    public Report(Map<String, Double> branchSales, double totalSales, 
                  List<String> customers, Map<String, List<Order>> ordersByBranch,
                  Map<String, Integer> lowStockItems) {
        this();
        this.branchSales = branchSales;
        this.totalSales = totalSales;
        this.customers = customers;
        this.ordersByBranch = ordersByBranch;
        this.lowStockItems = lowStockItems;
    }
    
    // Getters and Setters
    public LocalDateTime getReportDate() { return reportDate; }
    public void setReportDate(LocalDateTime reportDate) { this.reportDate = reportDate; }
    
    public Map<String, Double> getBranchSales() { return branchSales; }
    public void setBranchSales(Map<String, Double> branchSales) { this.branchSales = branchSales; }
    
    public double getTotalSales() { return totalSales; }
    public void setTotalSales(double totalSales) { this.totalSales = totalSales; }
    
    public List<String> getCustomers() { return customers; }
    public void setCustomers(List<String> customers) { this.customers = customers; }
    
    public Map<String, List<Order>> getOrdersByBranch() { return ordersByBranch; }
    public void setOrdersByBranch(Map<String, List<Order>> ordersByBranch) { this.ordersByBranch = ordersByBranch; }
    
    public Map<String, Integer> getLowStockItems() { return lowStockItems; }
    public void setLowStockItems(Map<String, Integer> lowStockItems) { this.lowStockItems = lowStockItems; }
    
    public String generateFormattedReport() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("=".repeat(80)).append("\n");
        sb.append("                    DRINK SALES SYSTEM - MASTER REPORT\n");
        sb.append("=".repeat(80)).append("\n");
        sb.append(String.format("Report Generated: %s\n\n", reportDate));
        
        sb.append("1. CUSTOMERS THAT MADE ORDERS\n");
        sb.append("-".repeat(40)).append("\n");
        if (customers != null && !customers.isEmpty()) {
            for (int i = 0; i < customers.size(); i++) {
                sb.append(String.format("   %d. %s\n", i + 1, customers.get(i)));
            }
        } else {
            sb.append("   No customers found\n");
        }
        sb.append("\n");
        
        sb.append("2. ORDERS BY BRANCH\n");
        sb.append("-".repeat(40)).append("\n");
        if (ordersByBranch != null) {
            for (Map.Entry<String, List<Order>> entry : ordersByBranch.entrySet()) {
                sb.append(String.format("\n   %s BRANCH:\n", entry.getKey()));
                List<Order> orders = entry.getValue();
                if (orders != null && !orders.isEmpty()) {
                    for (Order order : orders) {
                        sb.append(String.format("      - %s: %s ordered %d %s (KSh %.2f)\n",
                            order.getOrderDate().toLocalDate(),
                            order.getCustomerName(),
                            order.getQuantity(),
                            order.getDrinkBrand(),
                            order.getTotalAmount()));
                    }
                } else {
                    sb.append("      No orders placed\n");
                }
            }
        }
        sb.append("\n");
        
        sb.append("3. SALES PER BRANCH\n");
        sb.append("-".repeat(40)).append("\n");
        if (branchSales != null) {
            for (Map.Entry<String, Double> entry : branchSales.entrySet()) {
                sb.append(String.format("   %-12s: KSh %,.2f\n", 
                    entry.getKey(), entry.getValue()));
            }
        }
        sb.append("\n");
        
        sb.append("4. TOTAL BUSINESS REVENUE\n");
        sb.append("-".repeat(40)).append("\n");
        sb.append(String.format("   GRAND TOTAL: KSh %,.2f\n", totalSales));
        sb.append("\n");
        
        sb.append("5. LOW STOCK ALERTS\n");
        sb.append("-".repeat(40)).append("\n");
        if (lowStockItems != null && !lowStockItems.isEmpty()) {
            for (Map.Entry<String, Integer> entry : lowStockItems.entrySet()) {
                sb.append(String.format("   ⚠️ %s BRANCH: %d item(s) below threshold\n",
                    entry.getKey(), entry.getValue()));
            }
        } else {
            sb.append("   ✓ All stock levels are healthy\n");
        }
        sb.append("\n");
        sb.append("=".repeat(80)).append("\n");
        sb.append("                     END OF REPORT\n");
        sb.append("=".repeat(80));
        
        return sb.toString();
    }
    
    @Override
    public String toString() {
        return generateFormattedReport();
    }
}