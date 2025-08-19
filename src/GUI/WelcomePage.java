
package GUI;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.themes.FlatMacDarkLaf;

import raven.toast.Notifications;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.net.Authenticator;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class WelcomePage {

    private JFrame frame;
    private Timer animationTimer;
    private float gradientOffset = 0f;
    private int mouseX, mouseY;
    private boolean isMouseOver = false;
    private final List<Particle> particles = new ArrayList<>();
    private final List<Ripple> ripples = new ArrayList<>();
    private final Color  Bulish = new Color(0, 25, 51);
    private final Color orangeYellow = new Color(200, 150, 50,200);
    private final int WIDTH = 750;
    private final int HEIGHT = 450;
    public static String currentUser = null;

    private static class Ripple {
        float x, y, radius, alpha;
        Ripple(float x, float y) {
            this.x = x;
            this.y = y;
            this.radius = 0;
            this.alpha = 0.8f;
        }
    }

    private static class Particle {
        float x, y, dx, dy, opacity;
        Color color;
        boolean isSparkle;
        final float originalX, originalY; 
        final float speedVariation; 
        
        Particle(int x, int y, float speed, boolean isSparkle) {
            this.originalX = x;
            this.originalY = y;
            this.x = x;
            this.y = y;
            this.speedVariation = (float) (Math.random() * 0.1 + 0.95); 
            this.dx = (float) (Math.random() * speed - speed / 2);
            this.dy = (float) (Math.random() * speed - speed / 2);
            this.opacity = (float) Math.random() * 0.5f + 0.3f;
            this.color = Color.WHITE;
            this.isSparkle = isSparkle;
        }
    }

    public WelcomePage() {
       
        initializeParticles();
        initializeFrame();
        showSignInPage(frame);
    }

    private void initializeFrame() {
        frame = new JFrame("Welcome");
        frame.setSize(WIDTH, HEIGHT);
        frame.setUndecorated(true);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null);
        startAnimations();
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/pics/bgrlogo.png"));
        frame.setIconImage(logoIcon.getImage());

        frame.add(createAnimatedBackground());
        frame.setVisible(true);
    }

    private void initializeParticles() {
        for (int i = 0; i < 200; i++) {
            boolean isSparkle = i % 4 == 0;
            particles.add(new Particle(
                (int) (Math.random() * WIDTH),
                (int) (Math.random() * HEIGHT),
                (float) (Math.random() * 0.4 + 0.2),
                isSparkle
            ));
        }
    }
    

    private void startAnimations() {
        animationTimer = new Timer(50, e -> {
            gradientOffset = (float) (0.4 + Math.sin(System.currentTimeMillis() * 0.001) * 0.1);

          
            float startX = WIDTH * gradientOffset;
            float startY = HEIGHT * gradientOffset;
            float endX = WIDTH * (1 - gradientOffset);
            float endY = HEIGHT * (1 - gradientOffset);
            float dirX = endX - startX;
            float dirY = endY - startY;
            float lengthSq = dirX * dirX + dirY * dirY;

            for (Particle p : particles) {
               
                float distance = (float) Math.hypot(p.x - mouseX, p.y - mouseY);
                if (distance < 50) {
                    float angle = (float) Math.atan2(p.y - mouseY, p.x - mouseX);
                    p.dx += Math.cos(angle) * 1.2f;
                    p.dy += Math.sin(angle) * 1.2f;
                }

              
                float restoreStrength = 0.02f * p.speedVariation; 
                float deltaX = p.originalX - p.x;
                float deltaY = p.originalY - p.y;
                p.dx += deltaX * restoreStrength;
                p.dy += deltaY * restoreStrength;

             
                p.dx *= 0.92f;
                p.dy *= 0.92f;

                
                p.x += p.dx;
                p.y += p.dy;

              
                if (p.x < 0 || p.x > WIDTH) p.dx *= -0.3f;
                if (p.y < 0 || p.y > HEIGHT) p.dy *= -0.3f;

               
                if (p.isSparkle) {
                  
                    float dx = p.x - startX;
                    float dy = p.y - startY;
                    float dotProduct = dx * dirX + dy * dirY;
                    float t = lengthSq != 0 ? Math.max(0, Math.min(1, dotProduct / lengthSq)) : 0;
                    
                    int rgbValue = (int) (255 * (1 - t));
                    p.opacity = (float) (0.3 + 0.3 * Math.abs(Math.sin(System.currentTimeMillis() * 0.003)));
                    p.color = new Color(rgbValue, rgbValue, rgbValue, (int) (p.opacity * 255));

                   
                    if (rgbValue < 128) { 
                        float shine = (float) (0.5 + 0.5 * Math.abs(Math.sin(System.currentTimeMillis() * 0.005)));
                        p.opacity = shine; 
                        p.color = new Color(rgbValue, rgbValue, rgbValue, (int) (shine * 255));
                    }
                } else {
                    float yellowValue = Math.abs(gradientOffset - 0.5f) * 2;
                    float colorValue = (1 - yellowValue) * 255;
                    p.color = yellowValue > 0.5 ? 
                        new Color((int) colorValue, (int) colorValue, (int) colorValue, (int) (p.opacity * 255)) :
                        new Color(255, 255, 255, (int) (p.opacity * 255));
                }
            }

        
            Iterator<Ripple> rippleIter = ripples.iterator();
            while (rippleIter.hasNext()) {
                Ripple ripple = rippleIter.next();
                ripple.radius += 2;
                ripple.alpha -= 0.03;
                if (ripple.alpha <= 0) rippleIter.remove();
            }

            frame.repaint();
        });
        animationTimer.start();
    }
    private JPanel createAnimatedBackground() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

               
                Point2D start = new Point2D.Float(getWidth() * gradientOffset, getHeight() * gradientOffset);
                Point2D end = new Point2D.Float(getWidth() * (1 - gradientOffset), getHeight() * (1 - gradientOffset));
                
                Color midColor = new Color(
                    (Bulish.getRed() + orangeYellow.getRed())/2,
                    (Bulish.getGreen() + orangeYellow.getGreen())/2,
                    (Bulish.getBlue() + orangeYellow.getBlue())/2
                );
                
                LinearGradientPaint gradient = new LinearGradientPaint(
                    start, end,
                    new float[] {0f, 0.5f, 1f},
                    new Color[] {Bulish, midColor, orangeYellow}
                );
                
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

               
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
                for (Particle p : particles) {
                    g2d.setColor(p.color);
                    int size = p.isSparkle ? 3 : 2;
                    g2d.fillOval((int) p.x, (int) p.y, size, size);
                }

             
                for (Ripple ripple : ripples) {
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ripple.alpha));
                    g2d.setColor(new Color(200, 255, 200));
                    g2d.drawOval((int) (ripple.x - ripple.radius), (int) (ripple.y - ripple.radius),
                            (int) (ripple.radius * 2), (int) (ripple.radius * 2));
                }

                
                if (isMouseOver) {
                    float radius = 25f;
                    RadialGradientPaint glowPaint = new RadialGradientPaint(
                        new Point2D.Float(mouseX, mouseY), radius,
                        new float[]{0f, 1f},
                        new Color[]{new Color(0, 0, 0, 150), new Color(0, 0, 0, 0)}
                    );
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
                    g2d.setPaint(glowPaint);
                    g2d.fill(new RoundRectangle2D.Float(
                        mouseX - radius, mouseY - radius,
                        radius*2, radius*2,
                        radius, radius
                    ));
                }
                g2d.setComposite(AlphaComposite.SrcOver);
            }
        };

        panel.setLayout(null);
        panel.setBounds(0, 0, WIDTH, HEIGHT);

        panel.addMouseMotionListener(new MouseAdapter() {
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
                panel.repaint();
            }
        });

        panel.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { isMouseOver = true; }
            public void mouseExited(MouseEvent e) { isMouseOver = false; }
            public void mousePressed(MouseEvent e) { ripples.add(new Ripple(e.getX(), e.getY())); }
        });
        
        return panel;
    }

  
    public void showSignInPage(JFrame frame) {
        frame.getContentPane().removeAll();
        new FlatMacDarkLaf();
        JPanel backgroundPanel = createAnimatedBackground();
        
     
        JButton closeButton = new JButton("X");
        closeButton.setBounds(700, 10, 40, 30);
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setBackground(Color.BLACK);
        closeButton.setForeground(Color.WHITE);
        closeButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        closeButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                null, 
                "Are you sure you want to exit?", 
                "Exit Confirmation", 
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        backgroundPanel.add(closeButton);


      

        JLabel  lbl1 = new JLabel("BGR");
        lbl1.setBounds(95, 50, 250, 120);
        lbl1.setForeground(orangeYellow);
        lbl1.setFont(new Font("Ariel", Font.BOLD, 84));
        backgroundPanel.add(lbl1);
        
        JLabel  lbl2 = new JLabel("Print & Advert");
        lbl2.setBounds(115, 100, 250, 120);
        lbl2.setForeground(Color.white);
        lbl2.setFont(new Font("Ariel", Font.PLAIN, 24));
        backgroundPanel.add(lbl2);
        

        JButton guestbtn = new JButton("Login as Guest");
        guestbtn.setBounds(90, 200, 200, 40);
        guestbtn.setFocusPainted(false);
        guestbtn.setBorderPainted(false);
        guestbtn.setBackground(new Color(0, 0, 0, 150));
        guestbtn.setForeground(Color.WHITE);
        guestbtn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        guestbtn.addActionListener(e -> {
            frame.dispose(); // Close the current frame
            SwingUtilities.invokeLater(() -> {
                
                JFrame guestFrame = new JFrame("Guest Page");
                guestFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                guestFrame.setSize(750, 450);
                guestFrame.setLocationRelativeTo(null);
                FlatMacDarkLaf.setup();
                guestFrame.setResizable(false);
                ImageIcon logoIcon = new ImageIcon(getClass().getResource("/pics/bgrlogo.png"));
                guestFrame.setIconImage(logoIcon.getImage());

                GuestPanel guestPanel = new GuestPanel();
                guestFrame.add(guestPanel);

                guestFrame.setVisible(true); // Show the new frame
            });
        });
        backgroundPanel.add(guestbtn);

        JLabel orLabel = new JLabel("or");
        orLabel.setForeground(Color.WHITE);
        orLabel.setBounds(185, 250, 30, 20);
        backgroundPanel.add(orLabel);
        
        JLabel trademark = new JLabel("");
        trademark.setForeground(Color.LIGHT_GRAY);
        trademark.setFont(new Font("SansSerif", Font.PLAIN, 10));
        trademark.setBounds(40, 400, 300, 20);

        backgroundPanel.add(trademark);
        frame.add(backgroundPanel);
        frame.setVisible(true);

        // Full text to display
        String fullText = "Powered by KeyPrime Technologies™";

        // Typewriter effect timer
        Timer[] typewriterTimer = new Timer[1];

        typewriterTimer[0] = new Timer(100, null);
        typewriterTimer[0].addActionListener(e -> {
            int length = trademark.getText().length();
            if (length < fullText.length()) {
                trademark.setText(fullText.substring(0, length + 1));
            } else {
                ((Timer) e.getSource()).stop();
                
                // Delay before restart
                Timer restartTimer = new Timer(4000, ev -> {
                    trademark.setText(""); // Clear text
                    typewriterTimer[0].start(); // Restart effect
                });
                restartTimer.setRepeats(false); // Only trigger once
                restartTimer.start();
            }
        });

        typewriterTimer[0].start();
    

        JLabel registerLabel = new JLabel("Don't have an account?");
        registerLabel.setForeground(Color.WHITE);
        registerLabel.setBounds(100, 280, 150, 20);
        backgroundPanel.add(registerLabel);

        JLabel registerLink = new JLabel("Register");
        registerLink.setForeground(Color.GREEN);
        registerLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerLink.setBounds(230, 280, 60, 20);
        registerLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                showRegisterPage(frame);
            }
        });
        backgroundPanel.add(registerLink);

     
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setBounds(375, 50, 2, 350);
        separator.setForeground(Color.WHITE);
        backgroundPanel.add(separator);

        JLabel signInTitle = new JLabel("Sign In");
        signInTitle.setFont(new Font("Lucida Console", Font.BOLD, 36));
        signInTitle.setBackground(Color.black);
        signInTitle.setForeground(Color.black);
        signInTitle.setBounds(470, 90, 200, 40);
        backgroundPanel.add(signInTitle);

        JTextField emailField = new JTextField();
        emailField.setBounds(400, 150, 300, 40);
        setupPlaceholder(emailField, "Enter Your email or username");
        emailField.putClientProperty(FlatClientProperties.STYLE, "roundRect:true;");
        backgroundPanel.add(emailField);

        JPasswordField passwordField = new JPasswordField("Enter your Password");
        passwordField.setBounds(400, 195, 300, 40);
        setupPlaceholder(passwordField, "Enter your Password");
        passwordField.putClientProperty(FlatClientProperties.STYLE, 
            "roundRect:true;showRevealButton:true;showCapsLock:true");
        backgroundPanel.add(passwordField);

        JButton signInButton = new JButton("Sign In");
        signInButton.setBounds(490, 250, 120, 40);
        signInButton.setFocusPainted(false);
        signInButton.setBorderPainted(false);
        signInButton.setBackground(new Color(0, 0, 0, 150));
        signInButton.setForeground(Color.WHITE);
        signInButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));


        signInButton.addActionListener(e -> {
            String emailOrUsername = emailField.getText(); // Get text from email/username field
            String passwordText = new String(passwordField.getPassword()); // Get password text from password field

            // Check if any of the fields are empty
            if (emailOrUsername.isEmpty() || passwordText.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                
                return;
            }

            // Use the Authenticator method to validate the user
            if (authenticateUser(emailOrUsername, passwordText)) {
                //JOptionPane.showMessageDialog(frame, "Sign in successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
             
                frame.dispose(); // Close the current window
                SwingUtilities.invokeLater(() -> new Dashboard()); // Open the dashboard
            } else {
                JOptionPane.showMessageDialog(frame, "Incorrect email/username or password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    

        
        
        
        emailField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                	passwordField.requestFocus();  
                }
            }
        });

        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                	signInButton.requestFocus();  
                }
            }
        });
        
              
        backgroundPanel.add(signInButton);

        frame.add(backgroundPanel);
        frame.revalidate();
        frame.repaint();
    }
    public static boolean authenticateUser(String emailOrUsername, String password) {
        try (Connection conn = Database.getConnection()) { // Assuming Database.getConnection() is your method for DB connection
            String sql = "SELECT * FROM users WHERE (email = ? OR user_name = ?) AND password = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, emailOrUsername); // Set email or username
                pstmt.setString(2, emailOrUsername); // Set email or username again for OR condition
                pstmt.setString(3, password); // Set password

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        // If user is authenticated, set the static currentUser to the signed-in username
                        currentUser = rs.getString("user_name");
                        return true; // Authentication successful
                    }
                    return false; // Authentication failed
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false; // Return false in case of SQL errors
        }
    }

    public void showRegisterPage(JFrame frame) {
        frame.getContentPane().removeAll();
        new FlatMacDarkLaf();
        JPanel backgroundPanel = createAnimatedBackground();
        
        JButton closeButton = new JButton("X");
        closeButton.setBounds(700, 10, 40, 30);
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setBackground(Color.BLACK);
        closeButton.setForeground(Color.WHITE);
        closeButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        closeButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                null, 
                "Are you sure you want to exit?", 
                "Exit Confirmation", 
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        backgroundPanel.add(closeButton);



        JLabel  lbl1 = new JLabel("BGR");
        lbl1.setBounds(95, 50, 250, 120);
        lbl1.setForeground(orangeYellow);
        lbl1.setFont(new Font("Ariel", Font.BOLD, 84));
        backgroundPanel.add(lbl1);
        
        JLabel  lbl2 = new JLabel("Print & Advert");
        lbl2.setBounds(115, 100, 250, 120);
        lbl2.setForeground(Color.white);
        lbl2.setFont(new Font("Ariel", Font.PLAIN, 24));
        backgroundPanel.add(lbl2);
        
        JLabel logo = new JLabel();
        logo.setBounds(65, 50, 250, 120);
        backgroundPanel.add(logo);


        JButton guestbtn = new JButton("Login as Guest");
        guestbtn.setBounds(90, 200, 200, 40);
        guestbtn.setFocusPainted(false);
        guestbtn.setBorderPainted(false);
        guestbtn.setBackground(new Color(0, 0, 0, 150));
        guestbtn.setForeground(Color.WHITE);
        guestbtn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        guestbtn.addActionListener(e -> {
            frame.dispose(); // Close the current frame
            SwingUtilities.invokeLater(() -> {
                
                JFrame guestFrame = new JFrame("Guest Page");
                guestFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                guestFrame.setSize(750, 450);
                guestFrame.setLocationRelativeTo(null);
                FlatMacDarkLaf.setup();
                guestFrame.setResizable(false);
                ImageIcon logoIcon = new ImageIcon(getClass().getResource("/pics/bgrlogo.png"));
                guestFrame.setIconImage(logoIcon.getImage());

               
                GuestPanel guestPanel = new GuestPanel();
                guestFrame.add(guestPanel);

                guestFrame.setVisible(true); // Show the new frame
            });
        });

       
      

      
        guestbtn.putClientProperty("JButton.iconTextGap", 10);

        backgroundPanel.add(guestbtn);

        JLabel orLabel = new JLabel("or");
        orLabel.setForeground(Color.WHITE);
        orLabel.setBounds(185, 250, 30, 20);
        backgroundPanel.add(orLabel);
        
        JLabel signInLabel = new JLabel("Already have an account?");
        signInLabel.setForeground(Color.WHITE);
        signInLabel.setBounds(100, 280, 150, 20);
        backgroundPanel.add(signInLabel);

        JLabel signInLink = new JLabel("Sign In");
        signInLink.setForeground(Color.GREEN);
        signInLink.setBounds(230, 280, 60, 20);
        signInLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signInLink.setBounds(240, 280, 60, 20);
        signInLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                showSignInPage(frame);
            }
        });
        backgroundPanel.add(signInLink);

        
        JLabel trademark = new JLabel("");
        trademark.setForeground(Color.LIGHT_GRAY);
        trademark.setFont(new Font("SansSerif", Font.PLAIN, 10));
        trademark.setBounds(40, 400, 300, 20);

        backgroundPanel.add(trademark);
        frame.add(backgroundPanel);
        frame.setVisible(true);

        // Full text to display
        String fullText = "Powered by KeyPrime Technologies™";

        // Typewriter effect timer
        Timer[] typewriterTimer = new Timer[1];

        typewriterTimer[0] = new Timer(100, null);
        typewriterTimer[0].addActionListener(e -> {
            int length = trademark.getText().length();
            if (length < fullText.length()) {
                trademark.setText(fullText.substring(0, length + 1));
            } else {
                ((Timer) e.getSource()).stop();
                
                // Delay before restart
                Timer restartTimer = new Timer(4000, ev -> {
                    trademark.setText(""); // Clear text
                    typewriterTimer[0].start(); // Restart effect
                });
                restartTimer.setRepeats(false); // Only trigger once
                restartTimer.start();
            }
        });

        typewriterTimer[0].start();
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setBounds(375, 50, 2, 350);
        separator.setForeground(Color.WHITE);
        backgroundPanel.add(separator);

        JLabel registerTitle = new JLabel("Register");
        registerTitle.setFont(new Font("Lucida Console", Font.BOLD, 36));
        registerTitle.setBackground(Color.black);
        registerTitle.setForeground(Color.black);
        
        registerTitle.setBounds(465, 50, 200, 40);
        backgroundPanel.add(registerTitle);

        JTextField FullName = new JTextField("Full Name");
        FullName.setBounds(400, 100, 300, 35);
        setupPlaceholder(FullName, "Full Name");
        FullName.putClientProperty(FlatClientProperties.STYLE, "roundRect:true;");
        backgroundPanel.add(FullName);

        JTextField Username = new JTextField("Username");
        Username.setBounds(400, 140, 300, 35);
        setupPlaceholder(Username, "Username");
        Username.putClientProperty(FlatClientProperties.STYLE, "roundRect:true;");
        backgroundPanel.add(Username);
        
        JLabel emaillabel = new JLabel("Email:");
        emaillabel.setForeground(Color.WHITE);
        emaillabel.setBounds(410, 180, 50, 35);
        backgroundPanel.add(emaillabel);
        emaillabel.setForeground(Color.white);
        emaillabel.setFont(new Font("Ariel", Font.BOLD, 12));

        JTextField email = new JTextField();
        email.setBounds(455, 180, 245, 35);
       
        email.putClientProperty(FlatClientProperties.STYLE, "roundRect:true;");
        backgroundPanel.add(email);

        JComboBox<String> gender = new JComboBox<>(new String[]{"Male", "Female"});
        gender.setBounds(400, 220, 300, 35);
        backgroundPanel.add(gender);

        JPasswordField password = new JPasswordField("Password");
        password.setBounds(400, 260, 300, 35);
        setupPlaceholder(password, "Password");
        password.putClientProperty(FlatClientProperties.STYLE, 
            "roundRect:true;showRevealButton:true;showCapsLock:true");
        backgroundPanel.add(password);

        JPasswordField confirmPassword = new JPasswordField("Confirm Password");
        confirmPassword.setBounds(400, 300, 300, 35);
        setupPlaceholder(confirmPassword, "Confirm Password");
        confirmPassword.putClientProperty(FlatClientProperties.STYLE, 
            "roundRect:true;showRevealButton:true;showCapsLock:true");
        backgroundPanel.add(confirmPassword);
        
        JTextField Phone = new JTextField("Phone Number");
        Phone.setBounds(400, 340, 300, 35);
        setupPlaceholder(Phone,"Phone Number");
        Phone.putClientProperty(FlatClientProperties.STYLE, 
            "roundRect:true;");
        backgroundPanel.add(Phone);

        JButton registerButton = new JButton("Register");
        registerButton.setBounds(480, 390, 120, 40);
        registerButton.setFocusPainted(false);
        registerButton.setBorderPainted(false);
        registerButton.setBackground(new Color(0, 0, 0, 150));
        registerButton.setForeground(Color.WHITE);
        registerButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        backgroundPanel.add(registerButton);

        registerButton.addActionListener(e -> {
            String FullNameText = FullName.getText();
            String UserNameText = Username.getText();
            String emailText = email.getText().trim(); // Trim to handle empty strings
            String genderText = (String) gender.getSelectedItem();
            String passwordText = new String(password.getPassword());
            String confirmPasswordText = new String(confirmPassword.getPassword());
            String phoneText = Phone.getText();

            // Check if passwords match
            if (!passwordText.equals(confirmPasswordText)) {
                JOptionPane.showMessageDialog(frame, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // If email is empty, set it to "none"
            if (emailText.isEmpty()) {
                emailText = "none";
            }

            // Check if username or email already exists
            try (Connection conn = Database.getConnection()) {
                // Check if username exists
                String checkUsernameSql = "SELECT * FROM users WHERE user_name = ?";
                try (PreparedStatement checkUsernameStmt = conn.prepareStatement(checkUsernameSql)) {
                    checkUsernameStmt.setString(1, UserNameText);
                    try (ResultSet rs = checkUsernameStmt.executeQuery()) {
                        if (rs.next()) {
                            JOptionPane.showMessageDialog(frame, "Username already taken!", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                }

             // Check if email exists (except for "none")
                if (emailText != null && !emailText.equals("none")) {
                    String checkEmailSql = "SELECT * FROM users WHERE email = ? AND email <> 'none'";
                    try (PreparedStatement checkEmailStmt = conn.prepareStatement(checkEmailSql)) {
                        checkEmailStmt.setString(1, emailText);
                        try (ResultSet rs = checkEmailStmt.executeQuery()) {
                            if (rs.next()) {
                                JOptionPane.showMessageDialog(frame, "Email already taken!", "Error", JOptionPane.ERROR_MESSAGE);
                                return;
                            }
                        }
                    }
                }


                // Insert new user if username and email are unique
                String insertSql = "INSERT INTO users (full_name, user_name, email, gender, password, phone_number) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                    insertStmt.setString(1, FullNameText);
                    insertStmt.setString(2, UserNameText);
                    insertStmt.setString(3, emailText);
                    insertStmt.setString(4, genderText);
                    insertStmt.setString(5, passwordText);
                    insertStmt.setString(6, phoneText);

                    int rowsInserted = insertStmt.executeUpdate();
                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(frame, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        showSignInPage(frame); // Navigate to the sign-in page
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error during registration: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        
        
        
        FullName.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                	Username.requestFocus();  
                }
            }
        });
        Username.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                	email.requestFocus();  
                }
            }
        });
        email.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                	gender.requestFocus();  
                }
            }
        });
        gender.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                	password.requestFocus();  
                }
            }
        });
        password.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                	confirmPassword.requestFocus();  
                }
            }
        });

        confirmPassword.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                	Phone.requestFocus();  
                }
            }
        });
        Phone.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                	registerButton.requestFocus();  
                }
            }
        });
                
        
        
        frame.add(backgroundPanel);
        frame.revalidate();
        frame.repaint();
    }
    
    private static void setupPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.white);

        field.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.white);
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.white);
                    field.setText(placeholder);
                }
            }
        });
    }
  
}