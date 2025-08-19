package Main;

import java.awt.Color;

import javax.swing.*;
import Pages.MainPage;


public class App {
    public App() {
        JFrame frame = new JFrame("EAG: Bible Study Management v1.0.0");
        frame.setSize(1100, 650);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        ImageIcon logo = new ImageIcon("src/logo/AssemblyLogo.png");
        frame.setIconImage(logo.getImage());
        frame.add(new MainPage());
        frame.setBackground(new Color(186, 168,118));
        frame.setVisible(true);
    }

    
}
