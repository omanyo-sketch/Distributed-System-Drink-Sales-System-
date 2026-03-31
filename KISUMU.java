package client;

import javax.swing.*;

public class KISUMU extends BaseBranchGUI {
    
    public KISUMU() {
        super("KISUMU", "kisumu123");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new KISUMU().setVisible(true);
        });
    }
}