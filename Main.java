import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InterfaceGrafica gui = new InterfaceGrafica();
            gui.setVisible(true);
        });
    }
}