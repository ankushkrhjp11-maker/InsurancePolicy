package com.project1;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.net.URL;
import java.util.Random;
import java.io.*;
import java.net.HttpURLConnection;

// Nayi Libraries (Chatbot ke liye)
import org.json.JSONArray;
import org.json.JSONObject;

public class Login extends JFrame implements ActionListener {
    JTextField t_user;
    JPasswordField t_pass;
    JButton btnLogin, btnReg, btnAI; // AI Button added
    JLabel lblTimer, lblGreeting;
    JCheckBox chkShowPass; 

    // Aapka Chatbot API Logic
    static String API_KEY = "AIzaSyCVrk_IYST9qCqvKNuSKVJ0FlOBZS1pQt0";

    public Login() {
        setTitle("PolicyTrack Elite | Enterprise Security");
        setSize(1150, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // LayeredPane taaki AI icon design ke upar float kare
        JLayeredPane layeredPane = new JLayeredPane();
        setContentPane(layeredPane);
        layeredPane.setLayout(null);

        // --- MAIN CONTENT PANEL ---
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setBounds(0, 0, 1150, 720);

        // --- LEFT PANEL (FULL ORIGINAL DESIGN & ANIMATION) ---
        JPanel leftPanel = new JPanel() {
            private final int PARTICLE_COUNT = 15;
            private final Point[] particles = new Point[PARTICLE_COUNT];
            private final Random rand = new Random();

            {
                for (int i = 0; i < PARTICLE_COUNT; i++) {
                    particles[i] = new Point(rand.nextInt(550), rand.nextInt(750));
                }
                new Timer(50, e -> {
                    for (Point p : particles) {
                        p.y -= 1; 
                        if (p.y < 0) p.y = getHeight();
                    }
                    repaint();
                }).start();
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gp = new GradientPaint(0, 0, new Color(10, 24, 30), 0, getHeight(), new Color(28, 40, 51));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                g2d.setPaint(new RadialGradientPaint(100, 150, 300, new float[]{0f, 1f}, new Color[]{new Color(52, 152, 219, 30), new Color(0,0,0,0)}));
                g2d.fillOval(-100, -100, 500, 500);
                g2d.setPaint(new RadialGradientPaint(getWidth()-100, getHeight()-100, 400, new float[]{0f, 1f}, new Color[]{new Color(46, 204, 113, 20), new Color(0,0,0,0)}));
                g2d.fillOval(getWidth()-300, getHeight()-300, 600, 600);

                g2d.setColor(new Color(52, 152, 219, 60));
                for (Point p : particles) { g2d.fillOval(p.x, p.y, 3, 3); }

                g2d.setColor(new Color(255, 255, 255, 5));
                for(int i=0; i<getHeight(); i+=30) g2d.drawLine(0, i, getWidth(), i);
                for(int i=0; i<getWidth(); i+=30) g2d.drawLine(i, 0, i, getHeight());

                g2d.setPaint(new GradientPaint(0, 0, new Color(255,255,255,15), 15, 0, new Color(255,255,255,0)));
                g2d.fillRect(0, 0, 15, getHeight());

                g2d.setColor(new Color(52, 152, 219, 15));
                g2d.fillOval(getWidth()/2 - 120, 100, 240, 240);
            }
        };
        leftPanel.setPreferredSize(new Dimension(550, 750));
        leftPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;

        try {
            URL url = new URL("https://cdn-icons-png.flaticon.com/128/1162/1162456.png");
            ImageIcon icon = new ImageIcon(url);
            Image img = icon.getImage().getScaledInstance(110, 110, Image.SCALE_SMOOTH);
            leftPanel.add(new JLabel(new ImageIcon(img)), gbc);
        } catch (Exception e) {
            JLabel lblFallback = new JLabel("🛡");
            lblFallback.setFont(new Font("Segoe UI", Font.PLAIN, 80));
            lblFallback.setForeground(new Color(52, 152, 219));
            leftPanel.add(lblFallback, gbc);
        }

        gbc.gridy = 1; gbc.insets = new Insets(25, 0, 5, 0);
        JLabel lblLogo = new JLabel("POLICYTRACK");
        lblLogo.setFont(new Font("Segoe UI Semibold", Font.BOLD, 52));
        lblLogo.setForeground(Color.WHITE);
        leftPanel.add(lblLogo, gbc);

        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 10, 0);
        JPanel line = new JPanel();
        line.setPreferredSize(new Dimension(200, 2));
        line.setBackground(new Color(52, 152, 219, 180));
        leftPanel.add(line, gbc);

        gbc.gridy = 3; gbc.insets = new Insets(5, 0, 25, 0);
        JLabel lblSlogan = new JLabel("ELITE MANAGEMENT SYSTEM");
        lblSlogan.setFont(new Font("Segoe UI Semilight", Font.PLAIN, 15));
        lblSlogan.setForeground(new Color(180, 180, 180));
        leftPanel.add(lblSlogan, gbc);

        lblTimer = new JLabel();
        lblTimer.setFont(new Font("Monospaced", Font.BOLD, 15));
        lblTimer.setForeground(new Color(46, 204, 113)); 
        startClock();
        gbc.gridy = 4; leftPanel.add(lblTimer, gbc);

        gbc.gridy = 5; gbc.insets = new Insets(30, 0, 0, 0);
        JPanel monitorPanel = new JPanel();
        monitorPanel.setLayout(new BoxLayout(monitorPanel, BoxLayout.Y_AXIS));
        monitorPanel.setOpaque(false);

        String[] statuses = {"● Encryption: AES-256 Active", "● Cloud Sync: Synchronized", "● DB Status: Connected"};
        for (String status : statuses) {
            JLabel s = new JLabel(status);
            s.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            s.setForeground(new Color(150, 150, 150));
            s.setAlignmentX(Component.CENTER_ALIGNMENT);
            monitorPanel.add(s);
            monitorPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }
        leftPanel.add(monitorPanel, gbc);

        // --- RIGHT PANEL (FIXED FOR CENTERING) ---
        JPanel rightPanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(248, 250, 253));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setStroke(new BasicStroke(1f));
                g2d.setColor(new Color(52, 152, 219, 30)); 
                for (int i = 0; i < getWidth(); i += 40) g2d.drawLine(i, 0, i, getHeight());
                for (int i = 0; i < getHeight(); i += 40) g2d.drawLine(0, i, getWidth(), i);
            }
        };
        
        JPanel loginBox = new JPanel();
        loginBox.setLayout(new BoxLayout(loginBox, BoxLayout.Y_AXIS));
        loginBox.setBackground(Color.WHITE);
        loginBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0,0,0,15), 1), 
            new EmptyBorder(50, 75, 50, 75)
        ));

        lblGreeting = new JLabel("<html><body style='letter-spacing:3px;'><b>OFFICIAL ACCESS PORTAL</b></body></html>");
        lblGreeting.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        lblGreeting.setForeground(new Color(52, 152, 219));
        lblGreeting.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitle = new JLabel("Staff Authentication");
        lblTitle.setFont(new Font("Segoe UI Semibold", Font.BOLD, 32));
        lblTitle.setForeground(new Color(33, 47, 61));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        t_user = createStyledField("Enter Username");
        t_pass = createStyledPassField("Enter Password");

        chkShowPass = new JCheckBox("Show Password Credentials");
        chkShowPass.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkShowPass.setBackground(Color.WHITE);
        chkShowPass.setForeground(new Color(120, 120, 120));
        chkShowPass.setFocusPainted(false);
        chkShowPass.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chkShowPass.setAlignmentX(Component.CENTER_ALIGNMENT);
        chkShowPass.addActionListener(e -> {
            if (chkShowPass.isSelected()) t_pass.setEchoChar((char) 0);
            else t_pass.setEchoChar('•');
        });

        btnLogin = new JButton("AUTHORIZE ACCESS") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) g2.setColor(new Color(21, 67, 96));
                else if (getModel().isRollover()) g2.setColor(new Color(41, 128, 185));
                else g2.setColor(new Color(31, 97, 141));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        styleButton(btnLogin);

        JLabel lblNewUser = new JLabel("New Staff Member?");
        lblNewUser.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblNewUser.setForeground(Color.GRAY);
        lblNewUser.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnReg = new JButton("Initialize Administrator Enrollment");
        btnReg.setFont(new Font("Segoe UI Semibold", Font.BOLD, 13));
        btnReg.setForeground(new Color(31, 97, 141));
        btnReg.setContentAreaFilled(false);
        btnReg.setBorderPainted(false);
        btnReg.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReg.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginBox.add(lblGreeting);
        loginBox.add(Box.createRigidArea(new Dimension(0, 10)));
        loginBox.add(lblTitle);
        loginBox.add(Box.createRigidArea(new Dimension(0, 45)));
        loginBox.add(t_user);
        loginBox.add(Box.createRigidArea(new Dimension(0, 20)));
        loginBox.add(t_pass);
        loginBox.add(Box.createRigidArea(new Dimension(0, 8))); 
        loginBox.add(chkShowPass); 
        loginBox.add(Box.createRigidArea(new Dimension(0, 40)));
        loginBox.add(btnLogin);
        loginBox.add(Box.createRigidArea(new Dimension(0, 20)));
        loginBox.add(lblNewUser); 
        loginBox.add(btnReg);

        // FIX: Adding loginBox with constraints to center it
        rightPanel.add(loginBox, new GridBagConstraints());

        // --- FOOTER SECTION ---
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(240, 242, 245));
        footerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        footerPanel.setBounds(0, 680, 1150, 40);

        JLabel lblCopyright = new JLabel("© 2026 PolicyTrack Elite Systems | Version 4.0.1 LTS");
        lblCopyright.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblCopyright.setForeground(Color.GRAY);

        JLabel lblStatus = new JLabel("● System Secure | 256-bit Encryption Active");
        lblStatus.setFont(new Font("Segoe UI Semibold", Font.BOLD, 11));
        lblStatus.setForeground(new Color(46, 204, 113));

        footerPanel.add(lblCopyright, BorderLayout.WEST);
        footerPanel.add(lblStatus, BorderLayout.EAST);

        mainContent.add(leftPanel, BorderLayout.WEST);
        mainContent.add(rightPanel, BorderLayout.CENTER);
        
        layeredPane.add(mainContent, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(footerPanel, JLayeredPane.DEFAULT_LAYER);

        // --- FLOATING CHATBOT ICON ---
        btnAI = new JButton();
        try {
            URL aiIconUrl = new URL("https://cdn-icons-png.flaticon.com/128/2040/2040946.png");
            ImageIcon aiIcon = new ImageIcon(new ImageIcon(aiIconUrl).getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH));
            btnAI.setIcon(aiIcon);
        } catch (Exception e) { btnAI.setText("🤖"); }

        btnAI.setBounds(1050, 590, 75, 75); 
        btnAI.setBorderPainted(false);
        btnAI.setContentAreaFilled(false);
        btnAI.setFocusPainted(false);
        btnAI.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnAI.addActionListener(e -> openInsuranceChatbot());
        layeredPane.add(btnAI, JLayeredPane.PALETTE_LAYER); 

        btnLogin.addActionListener(this);
        btnReg.addActionListener(e -> { new Register(); dispose(); });

        setVisible(true);
    }

    // --- INTEGRATED GEMINI CHATBOT LOGIC ---
    private void openInsuranceChatbot() {
        JFrame chatFrame = new JFrame("Insurance Chatbot (Hinglish)");
        chatFrame.setSize(500, 600);
        chatFrame.setLocationRelativeTo(this);
        chatFrame.setLayout(new BorderLayout());

        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setFont(new Font("Nirmala UI", Font.PLAIN, 16));
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setBackground(new Color(245, 245, 245));

        JScrollPane scrollPane = new JScrollPane(chatArea);
        chatFrame.add(scrollPane, BorderLayout.CENTER);

        JTextField inputField = new JTextField();
        JButton sendBtn = new JButton("Send");
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(inputField, BorderLayout.CENTER);
        panel.add(sendBtn, BorderLayout.EAST);
        chatFrame.add(panel, BorderLayout.SOUTH);

        chatArea.append("🤖 Bot: Hello! Main aapka Insurance Assistant hoon. Main simple Hinglish me help kar sakta hoon.\n\n");

        ActionListener sendAction = e -> {
            String userText = inputField.getText().trim();
            if (userText.isEmpty()) return;

            chatArea.append("You: " + userText + "\n\n");
            inputField.setText("");
            chatArea.append("🤖 Bot is typing...\n\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());

            new Thread(() -> {
                try {
                    String response = callGeminiAPI(userText);
                    SwingUtilities.invokeLater(() -> {
                        String currentText = chatArea.getText().replace("🤖 Bot is typing...\n\n", "");
                        chatArea.setText(currentText);
                        chatArea.append("🤖 Bot:\n" + formatResponse(response) + "\n\n");
                        chatArea.setCaretPosition(chatArea.getDocument().getLength());
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> chatArea.append("Error: " + ex.getMessage() + "\n\n"));
                }
            }).start();
        };

        sendBtn.addActionListener(sendAction);
        inputField.addActionListener(sendAction);
        chatFrame.setVisible(true);
    }

    private String callGeminiAPI(String userInput) throws Exception {
        String endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + API_KEY;
        URL url = new URL(endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String safeInput = userInput.replace("\"", "\\\"");
        String prompt = "You are a friendly insurance assistant. Always reply in simple Hinglish (Hindi + English mix). Explain clearly about insurance policy, claim, premium, benefits. User: " + safeInput;

        String jsonInput = "{ \"contents\": [{ \"parts\": [{ \"text\": \"" + prompt + "\" }] }] }";

        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonInput.getBytes("utf-8"));
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) response.append(line);
        br.close();

        JSONObject obj = new JSONObject(response.toString());
        return obj.getJSONArray("candidates").getJSONObject(0)
                  .getJSONObject("content").getJSONArray("parts")
                  .getJSONObject(0).getString("text");
    }

    private String formatResponse(String text) {
        text = text.replace("**", "").replace("* ", "• ").replace("*", "");
        text = text.replace("\n\n", "\n").replace("\n", "\n\n");
        return text.trim();
    }

    private void startClock() {
        new Timer(1000, e -> {
            lblTimer.setText(new SimpleDateFormat("EEE, d MMM yyyy | HH:mm:ss").format(new Date()));
        }).start();
    }

    private JTextField createStyledField(String hint) {
        JTextField f = new JTextField();
        f.setMaximumSize(new Dimension(380, 60));
        f.setPreferredSize(new Dimension(380, 60));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        f.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(215, 215, 215)), hint, TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12), Color.GRAY));
        f.setBackground(Color.WHITE);
        f.setOpaque(true);
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { f.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(52, 152, 219), 2), hint, 0, 0, new Font("Segoe UI", Font.BOLD, 12), new Color(52, 152, 219))); }
            public void focusLost(FocusEvent e) { f.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(215, 215, 215)), hint, 0, 0, new Font("Segoe UI", Font.BOLD, 12), Color.GRAY)); }
        });
        return f;
    }

    private JPasswordField createStyledPassField(String hint) {
        JPasswordField f = new JPasswordField();
        f.setMaximumSize(new Dimension(380, 60));
        f.setPreferredSize(new Dimension(380, 60));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        f.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(215, 215, 215)), hint, TitledBorder.LEFT, TitledBorder.TOP, new Font("Segoe UI", Font.BOLD, 12), Color.GRAY));
        f.setBackground(Color.WHITE);
        f.setOpaque(true);
        f.setEchoChar('•');
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) { f.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(52, 152, 219), 2), hint, 0, 0, new Font("Segoe UI", Font.BOLD, 12), new Color(52, 152, 219))); }
            public void focusLost(FocusEvent e) { f.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(215, 215, 215)), hint, 0, 0, new Font("Segoe UI", Font.BOLD, 12), Color.GRAY)); }
        });
        return f;
    }

    private void styleButton(JButton btn) {
        btn.setMaximumSize(new Dimension(380, 55));
        btn.setPreferredSize(new Dimension(380, 55));
        btn.setFont(new Font("Segoe UI Semibold", Font.BOLD, 17));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    public void actionPerformed(ActionEvent e) {
        String user = t_user.getText().trim();
        String pass = new String(t_pass.getPassword());
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc_swing", "root", "root")) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
            ps.setString(1, user); ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                btnLogin.setText("VERIFYING SYSTEM ACCESS...");
                btnLogin.setEnabled(false);
                Timer timer = new Timer(800, evt -> {
                    JPanel welcomePanel = new JPanel(new BorderLayout(20, 20));
                    welcomePanel.setBackground(Color.WHITE);
                    welcomePanel.setBorder(new EmptyBorder(30, 30, 30, 30));
                    JLabel lblText = new JLabel("<html><div style='text-align: center;'>" +
                            "<font size='7' color='#2ECC71'><b>✔ ACCESS GRANTED</b></font><br><br>" +
                            "<font size='5' color='#34495E'>Welcome back, Official Admin</font><br>" +
                            "<font size='3' color='#95A5A6'>Enterprise Management Session Initialized Successfully.</font></div></html>");
                    lblText.setHorizontalAlignment(SwingConstants.CENTER);
                    welcomePanel.add(lblText, BorderLayout.CENTER);
                    JOptionPane.showMessageDialog(null, welcomePanel, "Security Clearance Successful", JOptionPane.PLAIN_MESSAGE);
                    new InsurancePolicy();
                    dispose();
                });
                timer.setRepeats(false);
                timer.start();
            } else { 
                JOptionPane.showMessageDialog(this, "<html><font color='red'><b>⚠️ Access Denied:</b></font> Invalid credentials detected.</html>"); 
                
                new Thread(() -> {
                    try (Connection con2 = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc_swing", "root", "root")) {
                        PreparedStatement psEmail = con2.prepareStatement("SELECT email FROM users WHERE username=?");
                        psEmail.setString(1, user);
                        ResultSet rsEmail = psEmail.executeQuery();
                        if (rsEmail.next()) {
                            String registeredEmail = rsEmail.getString("email");
                            sendSecurityAlertMail(user, registeredEmail);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }).start();
            }
        } catch (Exception ex) { 
            JOptionPane.showMessageDialog(this, "Database Integrity Error: " + ex.getMessage()); 
        }
    }

    private void sendSecurityAlertMail(String userAttempt, String recipientEmail) {
        final String sender = "ankushkr.hjp11@gmail.com";
        final String appPass = "dtly vfms zcgt evrx";

        java.util.Properties props = new java.util.Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        javax.mail.Session session = javax.mail.Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new javax.mail.PasswordAuthentication(sender, appPass);
            }
        });

        try {
            javax.mail.Message message = new javax.mail.internet.MimeMessage(session);
            message.setFrom(new javax.mail.internet.InternetAddress(sender));
            message.setRecipients(javax.mail.Message.RecipientType.TO, javax.mail.internet.InternetAddress.parse(recipientEmail));
            message.setSubject("⚠️ PolicyTrack Security Alert");
            
            String currentFullTime = new java.text.SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new java.util.Date());
            message.setText("Security Alert!\n\nInvalid login attempt detected on your account.\nUsername: " + userAttempt + "\nTime: " + currentFullTime + "\n\nIf this wasn't you, please secure your account.");

            javax.mail.Transport.send(message);
            System.out.println("Alert Mail Sent to: " + recipientEmail);

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        new Login();
    }
}