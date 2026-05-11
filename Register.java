package com.project1;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.net.URL;

public class Register extends JFrame {
    // Original Fields (No change)
    JTextField t_user, t_full, t_email, t_phone, t_address;
    JRadioButton rb_male, rb_female; 
    ButtonGroup genderGroup;
    JPasswordField t_pass, t_confirm;
    JButton btnRegister, btnBack;
    JLabel lblTimer; 
    
    // Switch karne ke liye panels
    JPanel rightPanel, regBox;

    public Register() {
        setTitle("PolicyTrack Elite | Stap Enrollment");
        setSize(1150, 850); 
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- LEFT PANEL: Original Premium Design ---
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(15, 32, 39), 0, getHeight(), new Color(44, 83, 100));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(new Color(255, 255, 255, 5));
                for(int i=0; i<getHeight(); i+=25) g2d.drawLine(0, i, getWidth(), i);
                for(int i=0; i<getWidth(); i+=25) g2d.drawLine(i, 0, i, getHeight());
                g2d.setColor(new Color(22, 160, 133, 20));
                g2d.fillOval(-100, -100, 400, 400);
            }
        };
        leftPanel.setPreferredSize(new Dimension(550, 750));
        leftPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;

        try {
            URL url = new URL("https://cdn-icons-png.flaticon.com/128/1162/1162456.png");
            ImageIcon icon = new ImageIcon(url);
            Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            JLabel lblIcon = new JLabel(new ImageIcon(img));
            leftPanel.add(lblIcon, gbc);
        } catch (Exception e) {
            JLabel lblFallback = new JLabel("🛡️");
            lblFallback.setFont(new Font("Segoe UI", Font.PLAIN, 80));
            leftPanel.add(lblFallback, gbc);
        }

        gbc.gridy = 1; gbc.insets = new Insets(20, 0, 5, 0);
        JLabel lblLogo = new JLabel("POLICYTRACK");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 42));
        lblLogo.setForeground(Color.WHITE);
        leftPanel.add(lblLogo, gbc);

        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 20, 0);
        JLabel lblSlogan = new JLabel("ELITE SECURITY INFRASTRUCTURE");
        lblSlogan.setFont(new Font("Segoe UI Semilight", Font.PLAIN, 14));
        lblSlogan.setForeground(new Color(26, 188, 156));
        leftPanel.add(lblSlogan, gbc);

        lblTimer = new JLabel();
        lblTimer.setFont(new Font("Monospaced", Font.BOLD, 16));
        lblTimer.setForeground(new Color(241, 196, 15)); 
        startClock();
        gbc.gridy = 3;
        leftPanel.add(lblTimer, gbc);

        gbc.gridy = 4; gbc.insets = new Insets(40, 0, 0, 0);
        JPanel dashPanel = new JPanel();
        dashPanel.setLayout(new BoxLayout(dashPanel, BoxLayout.Y_AXIS));
        dashPanel.setOpaque(false);
        JProgressBar securityBar = new JProgressBar();
        securityBar.setIndeterminate(true);
        securityBar.setPreferredSize(new Dimension(250, 4));
        securityBar.setForeground(new Color(26, 188, 156));
        dashPanel.add(securityBar);
        leftPanel.add(dashPanel, gbc);

        // --- RIGHT PANEL ---
        rightPanel = new JPanel(new CardLayout());
        
        regBox = new JPanel();
        regBox.setLayout(new BoxLayout(regBox, BoxLayout.Y_AXIS));
        regBox.setBackground(Color.WHITE);
        regBox.setBorder(new EmptyBorder(30, 70, 30, 70));

        JLabel lblTitle = new JLabel("Create Stap Identity");
        lblTitle.setFont(new Font("Segoe UI Semilight", Font.BOLD, 28));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        t_full = createStyledField("Full Name");
        t_user = createStyledField("Official Username");
        t_email = createStyledField("Email Address");
        t_phone = createStyledField("Phone Number");
        
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        genderPanel.setBackground(Color.WHITE);
        genderPanel.setBorder(BorderFactory.createTitledBorder("Gender"));
        rb_male = new JRadioButton("Male"); rb_male.setBackground(Color.WHITE);
        rb_female = new JRadioButton("Female"); rb_female.setBackground(Color.WHITE);
        genderGroup = new ButtonGroup();
        genderGroup.add(rb_male); genderGroup.add(rb_female);
        genderPanel.add(rb_male); genderPanel.add(rb_female);
        genderPanel.setMaximumSize(new Dimension(360, 50));

        t_address = createStyledField("Current Address");
        t_pass = createStyledPassField("Secret Password");
        t_confirm = createStyledPassField("Confirm Secret Password");

        btnRegister = new JButton("INITIALIZE ACCOUNT") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(22, 160, 133));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        styleButton(btnRegister);

        btnBack = new JButton("Switch to Authentication Portal");
        styleBackButton(btnBack);

        regBox.add(lblTitle);
        regBox.add(Box.createRigidArea(new Dimension(0, 20)));
        regBox.add(t_full); regBox.add(Box.createRigidArea(new Dimension(0, 10)));
        regBox.add(t_user); regBox.add(Box.createRigidArea(new Dimension(0, 10)));
        regBox.add(t_email); regBox.add(Box.createRigidArea(new Dimension(0, 10)));
        regBox.add(t_phone); regBox.add(Box.createRigidArea(new Dimension(0, 10)));
        regBox.add(genderPanel); regBox.add(Box.createRigidArea(new Dimension(0, 10)));
        regBox.add(t_address); regBox.add(Box.createRigidArea(new Dimension(0, 10)));
        regBox.add(t_pass); regBox.add(Box.createRigidArea(new Dimension(0, 10)));
        regBox.add(t_confirm); regBox.add(Box.createRigidArea(new Dimension(0, 25)));
        regBox.add(btnRegister);
        regBox.add(Box.createRigidArea(new Dimension(0, 10)));
        regBox.add(btnBack);

        JScrollPane scrollPane = new JScrollPane(regBox);
        scrollPane.setBorder(null);
        rightPanel.add(scrollPane, "FORM");

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        btnBack.addActionListener(e -> { new Login(); dispose(); }); 
        btnRegister.addActionListener(e -> handleRegistration());

        setVisible(true);
    }

    private void showSuccessUI() {
        JPanel successContainer = new JPanel(new BorderLayout());
        successContainer.setBackground(Color.WHITE);
        JPanel card = new JPanel(new GridBagLayout());
        card.setOpaque(false);
        JPanel innerContent = new JPanel();
        innerContent.setLayout(new BoxLayout(innerContent, BoxLayout.Y_AXIS));
        innerContent.setOpaque(false);

        JLabel lblLogoImage;
        try {
            URL url = new URL("https://cdn-icons-png.flaticon.com/128/1067/1067357.png");
            ImageIcon icon = new ImageIcon(url);
            Image img = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            lblLogoImage = new JLabel(new ImageIcon(img));
        } catch (Exception e) {
            lblLogoImage = new JLabel("✅");
            lblLogoImage.setFont(new Font("Segoe UI", Font.BOLD, 80));
            lblLogoImage.setForeground(new Color(46, 204, 113));
        }
        lblLogoImage.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblMsg = new JLabel("Access Granted!");
        lblMsg.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblMsg.setForeground(new Color(44, 62, 80));
        lblMsg.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Elite Administrator Identity Created Successfully");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSub.setForeground(Color.GRAY);
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnProceed = new JButton("PROCEED TO AUTHENTICATION") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(22, 160, 133), getWidth(), 0, new Color(26, 188, 156));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        styleButton(btnProceed);
        btnProceed.setPreferredSize(new Dimension(320, 55));
        btnProceed.setMaximumSize(new Dimension(320, 55));
        btnProceed.addActionListener(e -> { new Login(); dispose(); });

        innerContent.add(lblLogoImage);
        innerContent.add(Box.createRigidArea(new Dimension(0, 20)));
        innerContent.add(lblMsg);
        innerContent.add(Box.createRigidArea(new Dimension(0, 5)));
        innerContent.add(lblSub);
        innerContent.add(Box.createRigidArea(new Dimension(0, 40)));
        innerContent.add(btnProceed);

        card.add(innerContent);
        successContainer.add(card, BorderLayout.CENTER);
        rightPanel.add(successContainer, "SUCCESS");
        CardLayout cl = (CardLayout) rightPanel.getLayout();
        cl.show(rightPanel, "SUCCESS");
    }

    private void handleRegistration() {
        String full = t_full.getText().trim();
        String user = t_user.getText().trim();
        String email = t_email.getText().trim();
        String phone = t_phone.getText().trim();
        String address = t_address.getText().trim();
        String pass = new String(t_pass.getPassword());
        String confirm = new String(t_confirm.getPassword());
        
        if(user.isEmpty() || pass.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠️ Registration Error: Mandatory fields cannot be empty.");
            return;
        }
        if(!pass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "⚠️ Error: Passwords do not match.");
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc_swing", "root", "root");
            
            // Aapki table mein 8 columns hain (id, username, password, full_name, email, phone_number, gender, current_address)
            // Hum VALUES mein NULL bhej rahe hain taaki 'id' auto-increment ho jaye.
            String query = "INSERT INTO users VALUES (NULL, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, user);
            ps.setString(2, pass);
            ps.setString(3, full);
            ps.setString(4, email);
            ps.setString(5, phone);
            ps.setString(6, rb_male.isSelected() ? "Male" : "Female");
            ps.setString(7, address);
            
            ps.executeUpdate();
            con.close();
            showSuccessUI(); 
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database connection failed: " + ex.getMessage());
        }
    }

    private void startClock() { Timer t = new Timer(1000, e -> lblTimer.setText(new SimpleDateFormat("EEE, d MMM yyyy | HH:mm:ss").format(new Date()))); t.start(); }
    private JTextField createStyledField(String hint) { JTextField f = new JTextField(); f.setMaximumSize(new Dimension(360, 50)); f.setBorder(BorderFactory.createTitledBorder(hint)); return f; }
    private JPasswordField createStyledPassField(String hint) { JPasswordField f = new JPasswordField(); f.setMaximumSize(new Dimension(360, 50)); f.setBorder(BorderFactory.createTitledBorder(hint)); return f; }
    private void styleButton(JButton b) { b.setFont(new Font("Segoe UI", Font.BOLD, 14)); b.setForeground(Color.WHITE); b.setCursor(new Cursor(Cursor.HAND_CURSOR)); b.setAlignmentX(Component.CENTER_ALIGNMENT); b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false); }
    private void styleBackButton(JButton b) { b.setFont(new Font("Segoe UI", Font.BOLD, 12)); b.setForeground(new Color(22, 160, 133)); b.setContentAreaFilled(false); b.setBorderPainted(false); b.setAlignmentX(Component.CENTER_ALIGNMENT); }

    public static void main(String[] args) { 
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch(Exception e){}
        new Login(); 
    }
}