// Java UI Imports
import javax.swing.*;
import java.awt.*;

public class app {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Simple File Explorer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(2000, 1000);
        frame.setLayout(new FlowLayout());

        JLabel label = new JLabel("Welcome to Simple File Explorer.");
        
        frame.add(label);
        
        frame.setVisible(true);
    }
}