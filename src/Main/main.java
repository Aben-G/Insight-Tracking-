package Main;

import javax.swing.SwingUtilities;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;

public class main {
    public static void main(String[] args) {
       
        try {
            FlatMacDarkLaf.setup();
        } catch (Exception e) {
            System.err.println("Failed to load FlatLaf theme. Falling back to default.");
            e.printStackTrace();
        }

       
        SwingUtilities.invokeLater(App::new); 
    }
}
