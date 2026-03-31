package server;

import common.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class DrinkServiceImpl extends UnicastRemoteObject implements DrinkService {
    private static final long serialVersionUID = 1L;
    
    private DatabaseManager dbManager;
    private StockAlertService alertService;
    
    public DrinkServiceImpl() throws RemoteException {
        super();
        this.dbManager = new DatabaseManager();
        this.alertService = new StockAlertService(dbManager);
        System.out.println("[OK] DrinkServiceImpl initialized");
    }
    
    @Override
    public boolean placeOrder(Order order) throws RemoteException {
        try {
            Drink drink = dbManager.getDrinkStock(order.getBranchName(), order.getDrinkBrand());
            if (drink == null || drink.getStockQuantity() < order.getQuantity()) {
                System.err.println("[FAIL] Insufficient stock for " + order.getDrinkBrand() + " at " + order.getBranchName());
                return false;
            }
            
            boolean orderSuccess = dbManager.saveOrder(order);
            
            if (orderSuccess) {
                int newStock = drink.getStockQuantity() - order.getQuantity();
                dbManager.updateStock(order.getBranchName(), order.getDrinkBrand(), newStock);
                
                drink.setStockQuantity(newStock);
                if (drink.isLowStock()) {
                    alertService.triggerAlert(drink);
                }
                
                System.out.println("[SUCCESS] Order placed: " + order.getOrderId() + " | Branch: " + order.getBranchName());
                return true;
            }
            
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean updateStock(String branchName, String drinkBrand, int quantity) throws RemoteException {
        try {
            boolean success = dbManager.updateStock(branchName, drinkBrand, quantity);
            if (success) {
                System.out.println("[UPDATE] Stock updated - Branch: " + branchName + " | Drink: " + drinkBrand + " | New Qty: " + quantity);
                
                Drink drink = dbManager.getDrinkStock(branchName, drinkBrand);
                if (drink != null && drink.isLowStock()) {
                    alertService.triggerAlert(drink);
                }
            }
            return success;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public Drink getDrinkStock(String branchName, String drinkBrand) throws RemoteException {
        try {
            return dbManager.getDrinkStock(branchName, drinkBrand);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public List<Order> getOrdersByBranch(String branchName) throws RemoteException {
        try {
            return dbManager.getOrdersByBranch(branchName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public List<Order> getAllOrders() throws RemoteException {
        try {
            return dbManager.getAllOrders();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public double getBranchSales(String branchName) throws RemoteException {
        try {
            return dbManager.getBranchSales(branchName);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    @Override
    public double getTotalSales() throws RemoteException {
        try {
            return dbManager.getTotalSales();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    @Override
    public List<BranchInfo> getAllBranchStats() throws RemoteException {
        try {
            return dbManager.getAllBranchStats();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public List<Drink> getLowStockAlerts() throws RemoteException {
        try {
            return dbManager.getLowStockItems();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public boolean authenticateBranch(String branchName, String password) throws RemoteException {
        try {
            return dbManager.authenticateBranch(branchName, password);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean addCustomer(String name, String phone) throws RemoteException {
        try {
            return dbManager.addCustomer(name, phone);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public List<String> getAllCustomers() throws RemoteException {
        try {
            return dbManager.getAllCustomers();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    @Override
    public boolean recordRestock(String branchName, String drinkBrand, int quantity) throws RemoteException {
        try {
            return dbManager.recordRestock(branchName, drinkBrand, quantity);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public List<String> getRestockHistory(String branchName) throws RemoteException {
        try {
            return dbManager.getRestockHistory(branchName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}