package client;

import javax.swing.*;

public class MOMBASA extends BaseBranchGUI {
    
    public MOMBASA() {
        super("MOMBASA", "mombasa123");
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MOMBASA().setVisible(true);
        });
    }
}