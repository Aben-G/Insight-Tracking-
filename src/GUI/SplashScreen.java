package GUI;

import javax.swing.*;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class SplashScreen extends JWindow {

    private JProgressBar progressBar;
    private JLabel titleLabel;
 
    private final Color orangeYellow = new Color(200, 150, 50, 200);
    private final Color bluish = new Color(0, 25, 51);
    private float opacity = 0f; // For fade-in effect

    public SplashScreen() {
       
        setSize(300, 100);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        
        JPanel titlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
               
                GradientPaint gradient = new GradientPaint(0, 0, new Color(30, 30, 30), getWidth(), getHeight(), new Color(50, 50, 50));
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        titlePanel.setLayout(new GridBagLayout()); 
        titleLabel = new JLabel("BGR: Customer Handling v1.0.0", SwingConstants.CENTER);
        titleLabel.setForeground(orangeYellow); 
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16)); 
        titlePanel.add(titleLabel);

     
        progressBar = new JProgressBar(0, 100) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                int width = getWidth();
                int height = getHeight();
                GradientPaint gradient = new GradientPaint(0, 0, orangeYellow, width, 0, bluish);
                g2.setPaint(gradient);
                g2.fillRect(0, 0, (int) (width * (getPercentComplete())), height);
                super.paintComponent(g);
            }
        };
        progressBar.setPreferredSize(new Dimension(250, 10)); 
        progressBar.setBackground(Color.DARK_GRAY);
        progressBar.setStringPainted(true);

       
     
      
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(30, 30, 30));
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(5, 0, 5, 0); 
    
        gbc.gridy = 1;
        mainPanel.add(progressBar, gbc);

       
        add(titlePanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.SOUTH);
    }

    public void showSplash() {
        
        new Thread(() -> {
            while (opacity < 1f) {
                opacity += 0.05f;
                setOpacity(Math.min(opacity, 1f));
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        setVisible(true);

       
        new Thread(() -> {
            for (int i = 0; i <= 100; i += 5) {
                try {
                    Thread.sleep(100); 
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                progressBar.setValue(i);
              //  loadingLabel.setText("Loading... " + i + "%");
            }

          
            while (opacity > 0f) {
                opacity -= 0.05f;
                setOpacity(Math.max(opacity, 0f));
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            dispose(); 
        }).start();
    }
//    public static void main(String[] args) {
//        FlatMacDarkLaf.setup();
//        SwingUtilities.invokeLater(() -> {
//            SplashScreen splash = new SplashScreen();
//            splash.showSplash();
//        });
//    }

    
}