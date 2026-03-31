package client;

import javax.swing.*;

public class NAKURU extends BaseBranchGUI {
    
    public NAKURU() {
        super("NAKURU", "nakuru123");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new NAKURU().setVisible(true);
        });
    }
}