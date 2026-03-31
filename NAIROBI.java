package client;

import common.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class NAIROBI extends BaseBranchGUI {
    
    public NAIROBI() {
        super("NAIROBI", "nairobi123");
        setTitle("HEADQUARTERS - NAIROBI - Master Control Panel");
    }
    
    @Override
    protected void initUI() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        JPanel dashboardPanel = createDashboardPanel();
        JPanel orderPanel = createOrderPanel();
        JPanel inventoryPanel = createInventoryPanel();
        JPanel restockPanel = createRestockPanel();
        JPanel masterReportPanel = createMasterReportPanel();
        JPanel allBranchesPanel = createAllBranchesPanel();
        
        mainPanel.add(dashboardPanel, "dashboard");
        mainPanel.add(orderPanel, "order");
        mainPanel.add(inventoryPanel, "inventory");
        mainPanel.add(restockPanel, "restock");
        mainPanel.add(masterReportPanel, "masterreport");
        mainPanel.add(allBranchesPanel, "allbranches");
        
        add(mainPanel);
        cardLayout.show(mainPanel, "dashboard");
    }
    
    @Override
    protected JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(PRIMARY_COLOR);
        topBar.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("HEADQUARTERS - " + branchName + " - Master Control Panel");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(PRIMARY_COLOR);
        
        JButton dashboardBtn = createNavButton("Dashboard", PRIMARY_COLOR.darker());
        JButton orderBtn = createNavButton("Place Order", PRIMARY_COLOR.darker());
        JButton inventoryBtn = createNavButton("Inventory", PRIMARY_COLOR.darker());
        JButton restockBtn = createNavButton("Restock", PRIMARY_COLOR.darker());
        JButton masterReportBtn = createNavButton("Master Report", PRIMARY_COLOR.darker());
        JButton allBranchesBtn = createNavButton("All Branches", PRIMARY_COLOR.darker());
        JButton refreshBtn = createNavButton("Refresh", PRIMARY_COLOR.darker());
        
        dashboardBtn.addActionListener(e -> {
            cardLayout.show(mainPanel, "dashboard");
            refreshDashboard();
        });
        
        orderBtn.addActionListener(e -> {
            cardLayout.show(mainPanel, "order");
            refreshOrders();
        });
        
        inventoryBtn.addActionListener(e -> {
            cardLayout.show(mainPanel, "inventory");
            refreshInventory();
        });
        
        restockBtn.addActionListener(e -> {
            cardLayout.show(mainPanel, "restock");
            refreshRestockHistory();
        });
        
        masterReportBtn.addActionListener(e -> {
            cardLayout.show(mainPanel, "masterreport");
            refreshMasterReport();
        });
        
        allBranchesBtn.addActionListener(e -> {
            cardLayout.show(mainPanel, "allbranches");
            refreshAllBranches();
        });
        
        refreshBtn.addActionListener(e -> refreshAll());
        
        buttonPanel.add(dashboardBtn);
        buttonPanel.add(orderBtn);
        buttonPanel.add(inventoryBtn);
        buttonPanel.add(restockBtn);
        buttonPanel.add(masterReportBtn);
        buttonPanel.add(allBranchesBtn);
        buttonPanel.add(refreshBtn);
        
        topBar.add(titleLabel, BorderLayout.WEST);
        topBar.add(buttonPanel, BorderLayout.EAST);
        
        return topBar;
    }
    
    private JPanel createMasterReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel topBar = createTopBar();
        panel.add(topBar, BorderLayout.NORTH);
        
        JPanel reportCard = new JPanel(new BorderLayout());
        reportCard.setBackground(CARD_COLOR);
        reportCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel("MASTER BUSINESS REPORT");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        reportCard.add(titleLabel, BorderLayout.NORTH);
        
        JTextArea reportArea = new JTextArea();
        reportArea.setEditable(false);
        reportArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reportArea.setBackground(new Color(250, 250, 250));
        
        JScrollPane scrollPane = new JScrollPane(reportArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        reportCard.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(CARD_COLOR);
        JButton exportBtn = createStyledButton("Export Report", SECONDARY_COLOR);
        exportBtn.addActionListener(e -> exportReport(reportArea.getText()));
        buttonPanel.add(exportBtn);
        reportCard.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(reportCard, BorderLayout.CENTER);
        panel.putClientProperty("reportArea", reportArea);
        
        return panel;
    }
    
    private JPanel createAllBranchesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel topBar = createTopBar();
        panel.add(topBar, BorderLayout.NORTH);
        
        JPanel branchesCard = new JPanel(new BorderLayout());
        branchesCard.setBackground(CARD_COLOR);
        branchesCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel("ALL BRANCHES - INVENTORY OVERVIEW");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        branchesCard.add(titleLabel, BorderLayout.NORTH);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(NORMAL_FONT);
        
        String[] branches = {"NAIROBI", "NAKURU", "MOMBASA", "KISUMU"};
        for (String branch : branches) {
            JPanel branchPanel = createBranchInventoryPanel(branch);
            tabbedPane.addTab(branch, branchPanel);
        }
        
        branchesCard.add(tabbedPane, BorderLayout.CENTER);
        panel.add(branchesCard, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBranchInventoryPanel(String branch) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Drink Brand", "Price (KSh)", "Stock", "Min Threshold", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(tableModel);
        table.setFont(NORMAL_FONT);
        table.setRowHeight(30);
        
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, 
                    isSelected, hasFocus, row, column);
                if ("LOW STOCK".equals(value)) {
                    c.setForeground(DANGER_COLOR);
                    c.setFont(new Font("Segoe UI", Font.BOLD, 12));
                } else {
                    c.setForeground(PRIMARY_COLOR);
                }
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        try {
            String[] drinks = {"Coca-Cola", "Pepsi", "Fanta"};
            for (String drink : drinks) {
                Drink stock = service.getDrinkStock(branch, drink);
                if (stock != null) {
                    String status = stock.isLowStock() ? "LOW STOCK" : "OK";
                    tableModel.addRow(new Object[]{
                        stock.getBrand(),
                        String.format("%.2f", stock.getPrice()),
                        stock.getStockQuantity(),
                        stock.getMinimumThreshold(),
                        status
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        panel.putClientProperty("tableModel", tableModel);
        panel.putClientProperty("branch", branch);
        
        return panel;
    }
    
    private void refreshMasterReport() {
        try {
            JPanel masterReportPanel = (JPanel) mainPanel.getComponent(4);
            JPanel reportCard = (JPanel) ((BorderLayout) masterReportPanel.getLayout()).getCenter();
            JTextArea reportArea = (JTextArea) reportCard.getClientProperty("reportArea");
            
            if (reportArea != null) {
                Report report = new Report();
                
                List<BranchInfo> branchStats = service.getAllBranchStats();
                java.util.Map<String, Double> branchSales = new java.util.HashMap<>();
                for (BranchInfo info : branchStats) {
                    branchSales.put(info.getBranchName(), info.getTotalSales());
                }
                report.setBranchSales(branchSales);
                
                double totalSales = service.getTotalSales();
                report.setTotalSales(totalSales);
                
                List<String> customers = service.getAllCustomers();
                report.setCustomers(customers);
                
                java.util.Map<String, List<Order>> ordersByBranch = new java.util.HashMap<>();
                String[] branches = {"NAIROBI", "NAKURU", "MOMBASA", "KISUMU"};
                for (String branch : branches) {
                    ordersByBranch.put(branch, service.getOrdersByBranch(branch));
                }
                report.setOrdersByBranch(ordersByBranch);
                
                List<Drink> lowStockDrinks = service.getLowStockAlerts();
                java.util.Map<String, Integer> lowStockMap = new java.util.HashMap<>();
                for (Drink drink : lowStockDrinks) {
                    lowStockMap.merge(drink.getBranchName(), 1, Integer::sum);
                }
                report.setLowStockItems(lowStockMap);
                
                reportArea.setText(report.generateFormattedReport());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void refreshAllBranches() {
        try {
            JPanel allBranchesPanel = (JPanel) mainPanel.getComponent(5);
            JPanel branchesCard = (JPanel) ((BorderLayout) allBranchesPanel.getLayout()).getCenter();
            JTabbedPane tabbedPane = (JTabbedPane) branchesCard.getComponent(1);
            
            String[] branches = {"NAIROBI", "NAKURU", "MOMBASA", "KISUMU"};
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                JPanel branchPanel = (JPanel) tabbedPane.getComponentAt(i);
                DefaultTableModel tableModel = (DefaultTableModel) branchPanel.getClientProperty("tableModel");
                String branch = (String) branchPanel.getClientProperty("branch");
                
                if (tableModel != null) {
                    tableModel.setRowCount(0);
                    String[] drinks = {"Coca-Cola", "Pepsi", "Fanta"};
                    for (String drink : drinks) {
                        Drink stock = service.getDrinkStock(branch, drink);
                        if (stock != null) {
                            String status = stock.isLowStock() ? "LOW STOCK" : "OK";
                            tableModel.addRow(new Object[]{
                                stock.getBrand(),
                                String.format("%.2f", stock.getPrice()),
                                stock.getStockQuantity(),
                                stock.getMinimumThreshold(),
                                status
                            });
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void exportReport(String reportContent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new java.io.File("master_report_" + 
            java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt"));
        
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.FileWriter writer = new java.io.FileWriter(fileChooser.getSelectedFile());
                writer.write(reportContent);
                writer.close();
                JOptionPane.showMessageDialog(this, "Report exported successfully!", 
                    "Export Complete", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error exporting report: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    @Override
    protected void refreshAll() {
        super.refreshAll();
        refreshMasterReport();
        refreshAllBranches();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new NAIROBI().setVisible(true);
        });
    }
}