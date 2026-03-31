package common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface DrinkService extends Remote {
    // Order processing
    boolean placeOrder(Order order) throws RemoteException;
    
    // Stock management
    boolean updateStock(String branchName, String drinkBrand, int quantity) throws RemoteException;
    Drink getDrinkStock(String branchName, String drinkBrand) throws RemoteException;
    
    // Reports
    List<Order> getOrdersByBranch(String branchName) throws RemoteException;
    List<Order> getAllOrders() throws RemoteException;
    double getBranchSales(String branchName) throws RemoteException;
    double getTotalSales() throws RemoteException;
    List<BranchInfo> getAllBranchStats() throws RemoteException;
    
    // Stock alerts
    List<Drink> getLowStockAlerts() throws RemoteException;
    
    // Authentication
    boolean authenticateBranch(String branchName, String password) throws RemoteException;
    
    // Customer management
    boolean addCustomer(String name, String phone) throws RemoteException;
    List<String> getAllCustomers() throws RemoteException;
    
    // Restock tracking
    boolean recordRestock(String branchName, String drinkBrand, int quantity) throws RemoteException;
    List<String> getRestockHistory(String branchName) throws RemoteException;
}