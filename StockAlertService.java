package server;

import common.Drink;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.List;

public class StockAlertService {
    private DatabaseManager dbManager;
    private JFrame alertFrame;
    private static boolean alertShowing = false;
    
    public StockAlertService(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }
    
    public void triggerAlert(Drink drink) {
        if (alertShowing) {
            return;
        }
        
        String message = String.format(
            "STOCK ALERT\n\n" +
            "Branch: %s\n" +
            "Drink: %s\n" +
            "Current Stock: %d\n" +
            "Minimum Threshold: %d\n\n" +
            "Please restock immediately!",
            drink.getBranchName(),
            drink.getBrand(),
            drink.getStockQuantity(),
            drink.getMinimumThreshold()
        );
        
        logAlertToDatabase(drink);
        showAlertPopup(message);
        
        System.err.println("\n" + message + "\n");
    }
    
    private void logAlertToDatabase(Drink drink) {
        try {
            String sql = "INSERT INTO stock_alerts (branch_name, drink_brand) VALUES (?, ?)";
            PreparedStatement pstmt = dbManager.connection.prepareStatement(sql);
            pstmt.setString(1, drink.getBranchName());
            pstmt.setString(2, drink.getBrand());
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void showAlertPopup(String message) {
        if (alertFrame != null && alertFrame.isShowing()) {
            alertFrame.dispose();
        }
        
        alertShowing = true;
        alertFrame = new JFrame("STOCK ALERT");
        alertFrame.setAlwaysOnTop(true);
        alertFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(255, 235, 235));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel iconLabel = new JLabel("");
        iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 48));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JTextArea messageArea = new JTextArea(message);
        messageArea.setEditable(false);
        messageArea.setBackground(new Color(255, 235, 235));
        messageArea.setFont(new Font("Segoe UI", Font.BOLD, 14));
        messageArea.setMargin(new Insets(10, 10, 10, 10));
        
        JButton okButton = new JButton("OK");
        okButton.setBackground(new Color(220, 60, 50));
        okButton.setForeground(Color.WHITE);
        okButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        okButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        okButton.addActionListener(e -> {
            alertFrame.dispose();
            alertShowing = false;
        });
        
        panel.add(iconLabel, BorderLayout.NORTH);
        panel.add(messageArea, BorderLayout.CENTER);
        panel.add(okButton, BorderLayout.SOUTH);
        
        alertFrame.add(panel);
        alertFrame.setSize(400, 300);
        alertFrame.setLocationRelativeTo(null);
        alertFrame.setVisible(true);
        
        Timer timer = new Timer(10000, e -> {
            if (alertFrame != null && alertFrame.isShowing()) {
                alertFrame.dispose();
                alertShowing = false;
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    public void checkAllStockLevels() {
        try {
            List<Drink> lowStock = dbManager.getLowStockItems();
            for (Drink drink : lowStock) {
                triggerAlert(drink);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}