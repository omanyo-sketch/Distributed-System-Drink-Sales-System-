package client;

import common.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.Naming;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public abstract class BaseBranchGUI extends JFrame {
    protected DrinkService service;
    protected String branchName;
    protected Inventory inventory;
    
    protected final Color PRIMARY_COLOR = new Color(46, 204, 113);
    protected final Color SECONDARY_COLOR = new Color(52, 73, 94);
    protected final Color DANGER_COLOR = new Color(231, 76, 60);
    protected final Color WARNING_COLOR = new Color(241, 196, 15);
    protected final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    protected final Color CARD_COLOR = Color.WHITE;
    
    protected final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    protected final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 18);
    protected final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    protected final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    
    protected JPanel mainPanel;
    protected CardLayout cardLayout;
    protected Timer refreshTimer;
    
    public BaseBranchGUI(String branchName, String branchPassword) {
        this.branchName = branchName;
        setTitle(branchName + " Branch - Drink Sales System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 800);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1200, 700));
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        connectToServer();
        
        if (!authenticate(branchPassword)) {
            JOptionPane.showMessageDialog(null, 
                "Authentication failed for " + branchName + " branch!",
                "Authentication Error", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        loadInventory();
        initUI();
        startAutoRefresh();
    }
    
    protected void connectToServer() {
        try {
            service = (DrinkService) Naming.lookup("rmi://localhost/DrinkService");
            System.out.println(branchName + " branch connected to server");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Could not connect to server. Make sure the server is running.\n" + e.getMessage(),
                "Connection Error", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    protected boolean authenticate(String password) {
        try {
            return service.authenticateBranch(branchName, password);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    protected void loadInventory() {
        try {
            inventory = new Inventory(branchName);
            String[] drinks = {"Coca-Cola", "Pepsi", "Fanta"};
            for (String drink : drinks) {
                Drink stock = service.getDrinkStock(branchName, drink);
                if (stock != null) {
                    inventory.addDrink(stock);
                }
            }
            
            double sales = service.getBranchSales(branchName);
            inventory.setTotalSales(sales);
            
            List<Order> orders = service.getOrdersByBranch(branchName);
            inventory.setTotalOrders(orders.size());
            
            int lowStock = 0;
            for (Drink drink : inventory.getDrinks().values()) {
                if (drink.isLowStock()) {
                    lowStock++;
                }
            }
            inventory.setLowStockCount(lowStock);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    protected void initUI() {
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        JPanel dashboardPanel = createDashboardPanel();
        JPanel orderPanel = createOrderPanel();
        JPanel inventoryPanel = createInventoryPanel();
        JPanel restockPanel = createRestockPanel();
        
        mainPanel.add(dashboardPanel, "dashboard");
        mainPanel.add(orderPanel, "order");
        mainPanel.add(inventoryPanel, "inventory");
        mainPanel.add(restockPanel, "restock");
        
        add(mainPanel);
        cardLayout.show(mainPanel, "dashboard");
    }
    
    protected JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel topBar = createTopBar();
        panel.add(topBar, BorderLayout.NORTH);
        
        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanel.setBackground(BACKGROUND_COLOR);
        statsPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        statsPanel.add(createStatCard("TOTAL REVENUE", "KSh 0.00", PRIMARY_COLOR));
        statsPanel.add(createStatCard("TOTAL ORDERS", "0", SECONDARY_COLOR));
        statsPanel.add(createStatCard("LOW STOCK ITEMS", "0", DANGER_COLOR));
        statsPanel.add(createStatCard("INVENTORY ITEMS", "3", WARNING_COLOR));
        
        panel.add(statsPanel, BorderLayout.CENTER);
        
        panel.putClientProperty("revenueLabel", findLabelInPanel(statsPanel, 0));
        panel.putClientProperty("ordersLabel", findLabelInPanel(statsPanel, 1));
        panel.putClientProperty("lowstockLabel", findLabelInPanel(statsPanel, 2));
        
        return panel;
    }
    
    private JLabel findLabelInPanel(JPanel panel, int index) {
        Component comp = panel.getComponent(index);
        if (comp instanceof JPanel) {
            JPanel card = (JPanel) comp;
            Component centerComp = ((BorderLayout) card.getLayout()).getLayoutComponent(BorderLayout.CENTER);
            if (centerComp instanceof JPanel) {
                JPanel valuePanel = (JPanel) centerComp;
                if (valuePanel.getComponentCount() > 0 && valuePanel.getComponent(0) instanceof JLabel) {
                    return (JLabel) valuePanel.getComponent(0);
                }
            }
        }
        return null;
    }
    
    protected JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(SECONDARY_COLOR);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        valuePanel.setBackground(CARD_COLOR);
        valuePanel.add(valueLabel);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valuePanel, BorderLayout.CENTER);
        
        return card;
    }
    
    protected JPanel createOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel topBar = createTopBar();
        panel.add(topBar, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setBackground(BACKGROUND_COLOR);
        
        JPanel formCard = createOrderFormCard();
        contentPanel.add(formCard);
        
        JPanel ordersCard = createRecentOrdersCard();
        contentPanel.add(ordersCard);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    protected JPanel createOrderFormCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel titleLabel = new JLabel("PLACE NEW ORDER");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        card.add(titleLabel, gbc);
        
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        card.add(new JLabel("Customer Name:"), gbc);
        JTextField nameField = createStyledTextField();
        gbc.gridx = 1;
        card.add(nameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        card.add(new JLabel("Drink Brand:"), gbc);
        String[] drinks = {"Coca-Cola", "Pepsi", "Fanta"};
        JComboBox<String> drinkCombo = new JComboBox<>(drinks);
        drinkCombo.setFont(NORMAL_FONT);
        drinkCombo.setPreferredSize(new Dimension(200, 35));
        gbc.gridx = 1;
        card.add(drinkCombo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        card.add(new JLabel("Quantity:"), gbc);
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        quantitySpinner.setFont(NORMAL_FONT);
        quantitySpinner.setPreferredSize(new Dimension(200, 35));
        gbc.gridx = 1;
        card.add(quantitySpinner, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        card.add(new JLabel("Available Stock:"), gbc);
        JLabel stockLabel = new JLabel("Loading...");
        stockLabel.setFont(NORMAL_FONT);
        stockLabel.setForeground(PRIMARY_COLOR);
        gbc.gridx = 1;
        card.add(stockLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        card.add(new JLabel("Price per unit:"), gbc);
        JLabel priceLabel = new JLabel("KSh 100.00");
        priceLabel.setFont(NORMAL_FONT);
        priceLabel.setForeground(PRIMARY_COLOR);
        gbc.gridx = 1;
        card.add(priceLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 6;
        card.add(new JLabel("Total Amount:"), gbc);
        JLabel totalLabel = new JLabel("KSh 0.00");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalLabel.setForeground(DANGER_COLOR);
        gbc.gridx = 1;
        card.add(totalLabel, gbc);
        
        drinkCombo.addActionListener(e -> updateStockDisplay(drinkCombo, stockLabel));
        quantitySpinner.addChangeListener(e -> {
            int qty = (Integer) quantitySpinner.getValue();
            double total = qty * 100.00;
            totalLabel.setText(String.format("KSh %.2f", total));
        });
        
        JButton orderBtn = createStyledButton("PLACE ORDER", PRIMARY_COLOR);
        orderBtn.setPreferredSize(new Dimension(200, 45));
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 8, 8, 8);
        card.add(orderBtn, gbc);
        
        orderBtn.addActionListener(e -> {
            String customer = nameField.getText().trim();
            if (customer.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter customer name", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String drink = (String) drinkCombo.getSelectedItem();
            int quantity = (Integer) quantitySpinner.getValue();
            
            try {
                Drink stock = service.getDrinkStock(branchName, drink);
                if (stock != null && stock.getStockQuantity() >= quantity) {
                    Order order = new Order(customer, branchName, drink, quantity, 100.00);
                    if (service.placeOrder(order)) {
                        service.addCustomer(customer, "");
                        JOptionPane.showMessageDialog(this, "Order placed successfully!\nOrder ID: " + order.getOrderId(), 
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                        nameField.setText("");
                        quantitySpinner.setValue(1);
                        updateStockDisplay(drinkCombo, stockLabel);
                        refreshAll();
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to place order!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Insufficient stock!\nAvailable: " + 
                        (stock != null ? stock.getStockQuantity() : 0), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error placing order: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        updateStockDisplay(drinkCombo, stockLabel);
        
        return card;
    }
    
    protected void updateStockDisplay(JComboBox<String> drinkCombo, JLabel stockLabel) {
        try {
            String drink = (String) drinkCombo.getSelectedItem();
            Drink stock = service.getDrinkStock(branchName, drink);
            if (stock != null) {
                stockLabel.setText(String.valueOf(stock.getStockQuantity()));
                stockLabel.setForeground(stock.isLowStock() ? DANGER_COLOR : PRIMARY_COLOR);
            }
        } catch (Exception e) {
            stockLabel.setText("Error");
        }
    }
    
    protected JPanel createRecentOrdersCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel("RECENT ORDERS");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(SECONDARY_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        card.add(titleLabel, BorderLayout.NORTH);
        
        DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Order ID", "Customer", "Drink", "Qty", "Total", "Date"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable ordersTable = new JTable(tableModel);
        ordersTable.setFont(NORMAL_FONT);
        ordersTable.setRowHeight(30);
        
        JScrollPane scrollPane = new JScrollPane(ordersTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        card.add(scrollPane, BorderLayout.CENTER);
        
        card.putClientProperty("tableModel", tableModel);
        
        return card;
    }
    
    protected JPanel createInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel topBar = createTopBar();
        panel.add(topBar, BorderLayout.NORTH);
        
        JPanel inventoryCard = new JPanel(new BorderLayout());
        inventoryCard.setBackground(CARD_COLOR);
        inventoryCard.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel("CURRENT INVENTORY");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        inventoryCard.add(titleLabel, BorderLayout.NORTH);
        
        DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"Brand", "Price (KSh)", "Stock", "Min Threshold", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable inventoryTable = new JTable(tableModel);
        inventoryTable.setFont(NORMAL_FONT);
        inventoryTable.setRowHeight(35);
        
        inventoryTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
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
        
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        inventoryCard.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(inventoryCard, BorderLayout.CENTER);
        panel.putClientProperty("inventoryTableModel", tableModel);
        
        return panel;
    }
    
    protected JPanel createRestockPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JPanel topBar = createTopBar();
        panel.add(topBar, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setBackground(BACKGROUND_COLOR);
        
        JPanel restockCard = createRestockFormCard();
        contentPanel.add(restockCard);
        
        JPanel historyCard = createRestockHistoryCard();
        contentPanel.add(historyCard);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    protected JPanel createRestockFormCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel titleLabel = new JLabel("RESTOCK INVENTORY");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        card.add(titleLabel, gbc);
        
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        card.add(new JLabel("Drink Brand:"), gbc);
        String[] drinks = {"Coca-Cola", "Pepsi", "Fanta"};
        JComboBox<String> drinkCombo = new JComboBox<>(drinks);
        drinkCombo.setFont(NORMAL_FONT);
        drinkCombo.setPreferredSize(new Dimension(200, 35));
        gbc.gridx = 1;
        card.add(drinkCombo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        card.add(new JLabel("Current Stock:"), gbc);
        JLabel currentStockLabel = new JLabel("Loading...");
        currentStockLabel.setFont(NORMAL_FONT);
        gbc.gridx = 1;
        card.add(currentStockLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        card.add(new JLabel("Quantity to Add:"), gbc);
        JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(10, 1, 500, 10));
        quantitySpinner.setFont(NORMAL_FONT);
        quantitySpinner.setPreferredSize(new Dimension(200, 35));
        gbc.gridx = 1;
        card.add(quantitySpinner, gbc);
        
        drinkCombo.addActionListener(e -> {
            try {
                String drink = (String) drinkCombo.getSelectedItem();
                Drink stock = service.getDrinkStock(branchName, drink);
                if (stock != null) {
                    currentStockLabel.setText(String.valueOf(stock.getStockQuantity()));
                }
            } catch (Exception ex) {
                currentStockLabel.setText("Error");
            }
        });
        
        JButton restockBtn = createStyledButton("RESTOCK NOW", PRIMARY_COLOR);
        restockBtn.setPreferredSize(new Dimension(200, 45));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 8, 8, 8);
        card.add(restockBtn, gbc);
        
        restockBtn.addActionListener(e -> {
            String drink = (String) drinkCombo.getSelectedItem();
            int quantity = (Integer) quantitySpinner.getValue();
            
            try {
                Drink currentStock = service.getDrinkStock(branchName, drink);
                if (currentStock != null) {
                    int newStock = currentStock.getStockQuantity() + quantity;
                    if (service.updateStock(branchName, drink, newStock)) {
                        service.recordRestock(branchName, drink, quantity);
                        JOptionPane.showMessageDialog(this, 
                            String.format("Restocked %d units of %s!\nNew stock: %d units", 
                                quantity, drink, newStock),
                            "Restock Successful", 
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        currentStockLabel.setText(String.valueOf(newStock));
                        if (inventory != null) {
                            inventory.recordRestock(drink, quantity);
                        }
                        refreshAll();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error restocking: " + ex.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        try {
            String drink = (String) drinkCombo.getSelectedItem();
            Drink stock = service.getDrinkStock(branchName, drink);
            if (stock != null) {
                currentStockLabel.setText(String.valueOf(stock.getStockQuantity()));
            }
        } catch (Exception ex) {
            currentStockLabel.setText("Error");
        }
        
        return card;
    }
    
    protected JPanel createRestockHistoryCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_COLOR);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel("RESTOCK HISTORY");
        titleLabel.setFont(HEADER_FONT);
        titleLabel.setForeground(SECONDARY_COLOR);
        titleLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        card.add(titleLabel, BorderLayout.NORTH);
        
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> historyList = new JList<>(listModel);
        historyList.setFont(new Font("Monospaced", Font.PLAIN, 12));
        historyList.setBackground(new Color(250, 250, 250));
        
        JScrollPane scrollPane = new JScrollPane(historyList);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        card.add(scrollPane, BorderLayout.CENTER);
        
        card.putClientProperty("historyList", listModel);
        
        return card;
    }
    
    protected JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(PRIMARY_COLOR);
        topBar.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel(branchName + " Branch - Drink Sales System");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(PRIMARY_COLOR);
        
        JButton dashboardBtn = createNavButton("Dashboard", PRIMARY_COLOR.darker());
        JButton orderBtn = createNavButton("Place Order", PRIMARY_COLOR.darker());
        JButton inventoryBtn = createNavButton("Inventory", PRIMARY_COLOR.darker());
        JButton restockBtn = createNavButton("Restock", PRIMARY_COLOR.darker());
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
        
        refreshBtn.addActionListener(e -> refreshAll());
        
        buttonPanel.add(dashboardBtn);
        buttonPanel.add(orderBtn);
        buttonPanel.add(inventoryBtn);
        buttonPanel.add(restockBtn);
        buttonPanel.add(refreshBtn);
        
        topBar.add(titleLabel, BorderLayout.WEST);
        topBar.add(buttonPanel, BorderLayout.EAST);
        
        return topBar;
    }
    
    protected JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    protected JButton createNavButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    protected JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(NORMAL_FONT);
        field.setPreferredSize(new Dimension(200, 35));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }
    
    protected void refreshAll() {
        refreshDashboard();
        refreshOrders();
        refreshInventory();
        refreshRestockHistory();
        loadInventory();
    }
    
    protected void refreshDashboard() {
        try {
            JPanel dashboardPanel = (JPanel) mainPanel.getComponent(0);
            JLabel revenueLabel = (JLabel) dashboardPanel.getClientProperty("revenueLabel");
            JLabel ordersLabel = (JLabel) dashboardPanel.getClientProperty("ordersLabel");
            JLabel lowstockLabel = (JLabel) dashboardPanel.getClientProperty("lowstockLabel");
            
            double sales = service.getBranchSales(branchName);
            List<Order> orders = service.getOrdersByBranch(branchName);
            List<Drink> lowStock = service.getLowStockAlerts();
            
            int branchLowStock = 0;
            for (Drink drink : lowStock) {
                if (drink.getBranchName().equals(branchName)) {
                    branchLowStock++;
                }
            }
            
            if (revenueLabel != null) revenueLabel.setText(String.format("KSh %,.2f", sales));
            if (ordersLabel != null) ordersLabel.setText(String.valueOf(orders.size()));
            if (lowstockLabel != null) lowstockLabel.setText(String.valueOf(branchLowStock));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    protected void refreshOrders() {
        try {
            JPanel orderPanel = (JPanel) mainPanel.getComponent(1);
            JPanel contentPanel = (JPanel) ((BorderLayout) orderPanel.getLayout()).getCenter();
            JPanel ordersCard = (JPanel) contentPanel.getComponent(1);
            DefaultTableModel tableModel = (DefaultTableModel) ordersCard.getClientProperty("tableModel");
            
            if (tableModel != null) {
                tableModel.setRowCount(0);
                List<Order> orders = service.getOrdersByBranch(branchName);
                for (Order order : orders) {
                    tableModel.addRow(new Object[]{
                        order.getOrderId(),
                        order.getCustomerName(),
                        order.getDrinkBrand(),
                        order.getQuantity(),
                        String.format("KSh %.2f", order.getTotalAmount()),
                        order.getOrderDate().toString().substring(0, 19)
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    protected void refreshInventory() {
        try {
            JPanel inventoryPanel = (JPanel) mainPanel.getComponent(2);
            JPanel inventoryCard = (JPanel) ((BorderLayout) inventoryPanel.getLayout()).getCenter();
            DefaultTableModel tableModel = (DefaultTableModel) inventoryCard.getClientProperty("inventoryTableModel");
            
            if (tableModel != null) {
                tableModel.setRowCount(0);
                String[] drinks = {"Coca-Cola", "Pepsi", "Fanta"};
                for (String drink : drinks) {
                    Drink stock = service.getDrinkStock(branchName, drink);
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    protected void refreshRestockHistory() {
        try {
            JPanel restockPanel = (JPanel) mainPanel.getComponent(3);
            JPanel contentPanel = (JPanel) ((BorderLayout) restockPanel.getLayout()).getCenter();
            JPanel historyCard = (JPanel) contentPanel.getComponent(1);
            DefaultListModel<String> listModel = (DefaultListModel<String>) historyCard.getClientProperty("historyList");
            
            if (listModel != null) {
                listModel.clear();
                List<String> history = service.getRestockHistory(branchName);
                if (history.isEmpty()) {
                    listModel.addElement("No restocks recorded yet.");
                } else {
                    for (String record : history) {
                        listModel.addElement(record);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    protected void startAutoRefresh() {
        refreshTimer = new Timer();
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> refreshAll());
            }
        }, 30000, 30000);
    }
    
    @Override
    public void dispose() {
        if (refreshTimer != null) {
            refreshTimer.cancel();
        }
        super.dispose();
    }
}