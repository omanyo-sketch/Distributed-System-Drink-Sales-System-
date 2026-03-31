package common;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

public class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String orderId;
    private String customerName;
    private String branchName;
    private String drinkBrand;
    private int quantity;
    private double price;
    private double totalAmount;
    private LocalDateTime orderDate;
    private String status;
    
    public Order() {
        this.orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.orderDate = LocalDateTime.now();
        this.status = "PENDING";
    }
    
    public Order(String customerName, String branchName, String drinkBrand, 
                 int quantity, double price) {
        this();
        this.customerName = customerName;
        this.branchName = branchName;
        this.drinkBrand = drinkBrand;
        this.quantity = quantity;
        this.price = price;
        this.totalAmount = quantity * price;
    }
    
    // Getters and Setters
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    
    public String getBranchName() { return branchName; }
    public void setBranchName(String branchName) { this.branchName = branchName; }
    
    public String getDrinkBrand() { return drinkBrand; }
    public void setDrinkBrand(String drinkBrand) { this.drinkBrand = drinkBrand; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { 
        this.quantity = quantity; 
        this.totalAmount = this.price * quantity;
    }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { 
        this.price = price; 
        this.totalAmount = this.quantity * price;
    }
    
    public double getTotalAmount() { return totalAmount; }
    
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    @Override
    public String toString() {
        return String.format("Order[%s] %s - %s: %d x %.2f = %.2f", 
            orderId, customerName, drinkBrand, quantity, price, totalAmount);
    }
}