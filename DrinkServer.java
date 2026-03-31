package server;

import common.DrinkService;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;

public class DrinkServer {
    public static void main(String[] args) {
        try {
            System.out.println("========================================");
            System.out.println("   DRINK SALES SYSTEM - RMI SERVER");
            System.out.println("========================================\n");
            
            // Create RMI registry on port 1099
            LocateRegistry.createRegistry(1099);
            System.out.println("[OK] RMI Registry started on port 1099");
            
            // Create service implementation
            DrinkService service = new DrinkServiceImpl();
            
            // Bind service to registry
            Naming.rebind("rmi://localhost/DrinkService", service);
            System.out.println("[OK] DrinkService bound to rmi://localhost/DrinkService");
            
            System.out.println("\n[READY] Server is running and waiting for requests...");
            System.out.println("[INFO] Press Ctrl+C to stop the server\n");
            
        } catch (Exception e) {
            System.err.println("[ERROR] Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}