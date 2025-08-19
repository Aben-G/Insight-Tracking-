package GUI;

import javax.swing.*;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import java.awt.*;

public class MyApp {
    public static void main(String[] args) {
        FlatMacDarkLaf.setup();
        
        JFrame frame = new JFrame();
//        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/pics/bgrlogo.png"));
//        frame.setIconImage(logoIcon.getImage());

        SwingUtilities.invokeLater(() -> new WelcomePage());
    }
}
