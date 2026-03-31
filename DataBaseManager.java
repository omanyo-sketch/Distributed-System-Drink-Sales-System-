package server;

import common.*;
import java.sql.*;
import java.util.*;

public class DatabaseManager {
    private static final String URL = "jdbc:postgresql://localhost:5432/drink_sales";
    private static final String USER = "postgres";
    private static final String PASSWORD = "password"; // Change to your PostgreSQL password
    
    public Connection connection;
    
    public DatabaseManager() {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("[DB] Database connected successfully");
            initializeDatabase();
        } catch (Exception e) {
            System.err.println("[DB] Database connection error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void initializeDatabase() {
        try {
            String createCustomers = """
                CREATE TABLE IF NOT EXISTS customers (
                    id SERIAL PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    phone VARCHAR(20),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """;
            
            String createBranches = """
                CREATE TABLE IF NOT EXISTS branches (
                    id SERIAL PRIMARY KEY,
                    name VARCHAR(50) UNIQUE NOT NULL,
                    password VARCHAR(100) NOT NULL,
                    location VARCHAR(100)
                )
            """;
            
            String createDrinks = """
                CREATE TABLE IF NOT EXISTS drinks (
                    id SERIAL PRIMARY KEY,
                    branch_name VARCHAR(50) REFERENCES branches(name),
                    brand VARCHAR(100) NOT NULL,
                    price DECIMAL(10,2) NOT NULL,
                    stock_quantity INT NOT NULL,
                    minimum_threshold INT DEFAULT 10,
                    last_restocked TIMESTAMP,
                    UNIQUE(branch_name, brand)
                )
            """;
            
            String createOrders = """
                CREATE TABLE IF NOT EXISTS orders (
                    id SERIAL PRIMARY KEY,
                    order_id VARCHAR(50) UNIQUE NOT NULL,
                    customer_name VARCHAR(100) NOT NULL,
                    branch_name VARCHAR(50) REFERENCES branches(name),
                    drink_brand VARCHAR(100) NOT NULL,
                    quantity INT NOT NULL,
                    price DECIMAL(10,2) NOT NULL,
                    total_amount DECIMAL(10,2) NOT NULL,
                    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    status VARCHAR(20) DEFAULT 'PENDING'
                )
            """;
            
            String createRestockHistory = """
                CREATE TABLE IF NOT EXISTS restock_history (
                    id SERIAL PRIMARY KEY,
                    branch_name VARCHAR(50) REFERENCES branches(name),
                    drink_brand VARCHAR(100) NOT NULL,
                    quantity INT NOT NULL,
                    restock_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    restocked_by VARCHAR(50)
                )
            """;
            
            String createStockAlerts = """
                CREATE TABLE IF NOT EXISTS stock_alerts (
                    id SERIAL PRIMARY KEY,
                    branch_name VARCHAR(50),
                    drink_brand VARCHAR(100),
                    alert_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    acknowledged BOOLEAN DEFAULT FALSE
                )
            """;
            
            Statement stmt = connection.createStatement();
            stmt.execute(createCustomers);
            stmt.execute(createBranches);
            stmt.execute(createDrinks);
            stmt.execute(createOrders);
            stmt.execute(createRestockHistory);
            stmt.execute(createStockAlerts);
            
            String insertBranches = """
                INSERT INTO branches (name, password, location) VALUES
                ('NAIROBI', 'nairobi123', 'Headquarters'),
                ('NAKURU', 'nakuru123', 'Nakuru CBD'),
                ('MOMBASA', 'mombasa123', 'Mombasa CBD'),
                ('KISUMU', 'kisumu123', 'Kisumu CBD')
                ON CONFLICT (name) DO NOTHING
            """;
            stmt.execute(insertBranches);
            
            String insertDrinks = """
                INSERT INTO drinks (branch_name, brand, price, stock_quantity, minimum_threshold) VALUES
                ('NAIROBI', 'Coca-Cola', 100.00, 100, 10),
                ('NAIROBI', 'Pepsi', 100.00, 100, 10),
                ('NAIROBI', 'Fanta', 100.00, 100, 10),
                ('NAKURU', 'Coca-Cola', 100.00, 100, 10),
                ('NAKURU', 'Pepsi', 100.00, 100, 10),
                ('NAKURU', 'Fanta', 100.00, 100, 10),
                ('MOMBASA', 'Coca-Cola', 100.00, 100, 10),
                ('MOMBASA', 'Pepsi', 100.00, 100, 10),
                ('MOMBASA', 'Fanta', 100.00, 100, 10),
                ('KISUMU', 'Coca-Cola', 100.00, 100, 10),
                ('KISUMU', 'Pepsi', 100.00, 100, 10),
                ('KISUMU', 'Fanta', 100.00, 100, 10)
                ON CONFLICT (branch_name, brand) DO NOTHING
            """;
            stmt.execute(insertDrinks);
            
            stmt.close();
            System.out.println("[DB] Database initialized successfully");
        } catch (SQLException e) {
            System.err.println("[DB] Database initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public boolean saveOrder(Order order) throws SQLException {
        String sql = "INSERT INTO orders (order_id, customer_name, branch_name, drink_brand, quantity, price, total_amount) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, order.getOrderId());
        pstmt.setString(2, order.getCustomerName());
        pstmt.setString(3, order.getBranchName());
        pstmt.setString(4, order.getDrinkBrand());
        pstmt.setInt(5, order.getQuantity());
        pstmt.setDouble(6, order.getPrice());
        pstmt.setDouble(7, order.getTotalAmount());
        
        int rows = pstmt.executeUpdate();
        pstmt.close();
        return rows > 0;
    }
    
    public boolean updateStock(String branchName, String drinkBrand, int quantity) throws SQLException {
        String sql = "UPDATE drinks SET stock_quantity = ?, last_restocked = CURRENT_TIMESTAMP WHERE branch_name = ? AND brand = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, quantity);
        pstmt.setString(2, branchName);
        pstmt.setString(3, drinkBrand);
        
        int rows = pstmt.executeUpdate();
        pstmt.close();
        return rows > 0;
    }
    
    public Drink getDrinkStock(String branchName, String drinkBrand) throws SQLException {
        String sql = "SELECT * FROM drinks WHERE branch_name = ? AND brand = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, branchName);
        pstmt.setString(2, drinkBrand);
        
        ResultSet rs = pstmt.executeQuery();
        Drink drink = null;
        if (rs.next()) {
            drink = new Drink();
            drink.setBrand(rs.getString("brand"));
            drink.setPrice(rs.getDouble("price"));
            drink.setStockQuantity(rs.getInt("stock_quantity"));
            drink.setMinimumThreshold(rs.getInt("minimum_threshold"));
            drink.setBranchName(rs.getString("branch_name"));
        }
        rs.close();
        pstmt.close();
        return drink;
    }
    
    public List<Order> getOrdersByBranch(String branchName) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE branch_name = ? ORDER BY order_date DESC LIMIT 20";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, branchName);
        
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            Order order = new Order();
            order.setOrderId(rs.getString("order_id"));
            order.setCustomerName(rs.getString("customer_name"));
            order.setBranchName(rs.getString("branch_name"));
            order.setDrinkBrand(rs.getString("drink_brand"));
            order.setQuantity(rs.getInt("quantity"));
            order.setPrice(rs.getDouble("price"));
            order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
            order.setStatus(rs.getString("status"));
            orders.add(order);
        }
        rs.close();
        pstmt.close();
        return orders;
    }
    
    public List<Order> getAllOrders() throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY order_date DESC";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        
        while (rs.next()) {
            Order order = new Order();
            order.setOrderId(rs.getString("order_id"));
            order.setCustomerName(rs.getString("customer_name"));
            order.setBranchName(rs.getString("branch_name"));
            order.setDrinkBrand(rs.getString("drink_brand"));
            order.setQuantity(rs.getInt("quantity"));
            order.setPrice(rs.getDouble("price"));
            order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
            order.setStatus(rs.getString("status"));
            orders.add(order);
        }
        rs.close();
        stmt.close();
        return orders;
    }
    
    public double getBranchSales(String branchName) throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE branch_name = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, branchName);
        ResultSet rs = pstmt.executeQuery();
        double total = rs.next() ? rs.getDouble(1) : 0;
        rs.close();
        pstmt.close();
        return total;
    }
    
    public double getTotalSales() throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM orders";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        double total = rs.next() ? rs.getDouble(1) : 0;
        rs.close();
        stmt.close();
        return total;
    }
    
    public List<BranchInfo> getAllBranchStats() throws SQLException {
        List<BranchInfo> stats = new ArrayList<>();
        String sql = """
            SELECT b.name, 
                   COALESCE(SUM(o.total_amount), 0) as total_sales,
                   COUNT(DISTINCT o.id) as total_orders,
                   COUNT(CASE WHEN d.stock_quantity <= d.minimum_threshold THEN 1 END) as low_stock_count
            FROM branches b
            LEFT JOIN orders o ON b.name = o.branch_name
            LEFT JOIN drinks d ON b.name = d.branch_name
            GROUP BY b.name
        """;
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        
        while (rs.next()) {
            BranchInfo info = new BranchInfo();
            info.setBranchName(rs.getString("name"));
            info.setTotalSales(rs.getDouble("total_sales"));
            info.setTotalOrders(rs.getInt("total_orders"));
            info.setLowStockCount(rs.getInt("low_stock_count"));
            stats.add(info);
        }
        rs.close();
        stmt.close();
        return stats;
    }
    
    public List<Drink> getLowStockItems() throws SQLException {
        List<Drink> lowStock = new ArrayList<>();
        String sql = "SELECT * FROM drinks WHERE stock_quantity <= minimum_threshold";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        
        while (rs.next()) {
            Drink drink = new Drink();
            drink.setBrand(rs.getString("brand"));
            drink.setPrice(rs.getDouble("price"));
            drink.setStockQuantity(rs.getInt("stock_quantity"));
            drink.setMinimumThreshold(rs.getInt("minimum_threshold"));
            drink.setBranchName(rs.getString("branch_name"));
            lowStock.add(drink);
        }
        rs.close();
        stmt.close();
        return lowStock;
    }
    
    public boolean authenticateBranch(String branchName, String password) throws SQLException {
        String sql = "SELECT * FROM branches WHERE name = ? AND password = ?";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, branchName);
        pstmt.setString(2, password);
        ResultSet rs = pstmt.executeQuery();
        boolean exists = rs.next();
        rs.close();
        pstmt.close();
        return exists;
    }
    
    public boolean addCustomer(String name, String phone) throws SQLException {
        String sql = "INSERT INTO customers (name, phone) VALUES (?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, name);
        pstmt.setString(2, phone);
        int rows = pstmt.executeUpdate();
        pstmt.close();
        return rows > 0;
    }
    
    public List<String> getAllCustomers() throws SQLException {
        List<String> customers = new ArrayList<>();
        String sql = "SELECT name FROM customers ORDER BY created_at DESC";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            customers.add(rs.getString("name"));
        }
        rs.close();
        stmt.close();
        return customers;
    }
    
    public boolean recordRestock(String branchName, String drinkBrand, int quantity) throws SQLException {
        String sql = "INSERT INTO restock_history (branch_name, drink_brand, quantity, restocked_by) VALUES (?, ?, ?, ?)";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, branchName);
        pstmt.setString(2, drinkBrand);
        pstmt.setInt(3, quantity);
        pstmt.setString(4, "SYSTEM");
        int rows = pstmt.executeUpdate();
        pstmt.close();
        
        // Update last_restocked timestamp
        String updateSql = "UPDATE drinks SET last_restocked = CURRENT_TIMESTAMP WHERE branch_name = ? AND brand = ?";
        PreparedStatement updateStmt = connection.prepareStatement(updateSql);
        updateStmt.setString(1, branchName);
        updateStmt.setString(2, drinkBrand);
        updateStmt.executeUpdate();
        updateStmt.close();
        
        return rows > 0;
    }
    
    public List<String> getRestockHistory(String branchName) throws SQLException {
        List<String> history = new ArrayList<>();
        String sql = "SELECT drink_brand, quantity, restock_date FROM restock_history WHERE branch_name = ? ORDER BY restock_date DESC LIMIT 10";
        PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setString(1, branchName);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            history.add(String.format("%s: +%d units on %s", 
                rs.getString("drink_brand"), 
                rs.getInt("quantity"),
                rs.getTimestamp("restock_date").toString().substring(0, 19)));
        }
        rs.close();
        pstmt.close();
        return history;
    }
}