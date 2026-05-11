package com.project1;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.Date;
import java.util.HashMap;

// iText Professional PDF Libraries
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.LineSeparator;

// Email Engine
import javax.mail.*;
import javax.mail.internet.*;

// Twilio Imports
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class InsurancePolicy extends JFrame implements ActionListener {
    JTextField t_no, t_name, t_prem, t_dur, t_email, t_search, t_phone, t_aadhar, t_address;
    JComboBox<String> comboType, comboVehicle;
    JButton btnAdd, btnUpdate, btnDelete, btnClear, btnExport, btnReceipt, btnLogout, btnAlert, btnAIPredict, btnChat, btnTheme, btnClaim;
    JLabel lblTotalPolicies, lblTotalPremium; 
    JTable table;
    DefaultTableModel model;
    
    boolean isDarkMode = false;
    JPanel mainPanel, leftPanel, header, dashBoard;
    JLabel lblSearch;

    // Twilio Credentials
    private static final String SMS_SID = "AC6a707365b379912ef017e62318d3588a"; 
    private static final String SMS_TOKEN = "4368343298dc0cdcf0f895b25ab0ec88";  
    private static final String SMS_FROM = "+18166407618"; 

    // Class level variable initialize
    StringBuilder chatHistory = new StringBuilder(); 

    public InsurancePolicy() {
        setTitle("PolicyTrack Elite Management v9.0 - Dark Mode Integrated");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1200, 700));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(240, 244, 248));
        setLayout(new BorderLayout(0, 0));

        // --- HEADER with gradient look --- (pehle initialize karo)
        header = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 18));
        header.setBackground(new Color(15, 32, 65));
        header.setOpaque(true);
        header.setPreferredSize(new Dimension(1350, 75));
        JLabel lblShield = new JLabel("\uD83D\uDEE1\uFE0F");
        lblShield.setFont(new java.awt.Font("Segoe UI Emoji", java.awt.Font.PLAIN, 30));
        JLabel lblTitle = new JLabel("  POLICYTRACK ELITE INSURANCE SYSTEM");
        lblTitle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);
        header.add(lblShield);
        header.add(lblTitle);

        // --- DASHBOARD STRIP with stat cards --- (pehle initialize karo)
        dashBoard = new JPanel(new FlowLayout(FlowLayout.CENTER, 60, 8));
        dashBoard.setBackground(new Color(22, 45, 85));
        dashBoard.setOpaque(true);
        dashBoard.setPreferredSize(new Dimension(1350, 55));

        JPanel cardPolicies = createStatCard("\uD83D\uDCCB  TOTAL POLICIES", "0", new Color(52, 152, 219));
        JPanel cardPremium  = createStatCard("\uD83D\uDCB0  TOTAL VALUE", "\u20B90.00", new Color(46, 204, 113));
        lblTotalPolicies = (JLabel) cardPolicies.getComponent(1);
        lblTotalPremium  = (JLabel) cardPremium.getComponent(1);
        dashBoard.add(cardPolicies);
        dashBoard.add(cardPremium);

        // Ab topContainer mein add karo
        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.add(header, BorderLayout.NORTH);
        topContainer.add(dashBoard, BorderLayout.SOUTH);
        add(topContainer, BorderLayout.NORTH);

        mainPanel = new JPanel(null);
        mainPanel.setBackground(new Color(240, 244, 248));

        leftPanel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth()-1, getHeight()-1, 16, 16);
            }
        };
        leftPanel.setOpaque(false);
        leftPanel.setBounds(15, 10, 420, 800);
        TitledBorder tb = new TitledBorder(BorderFactory.createEmptyBorder(), "  \uD83D\uDCDD  Policy Registration  ");
        tb.setTitleFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        tb.setTitleColor(new Color(41, 128, 185));
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(52, 152, 219), 2, true), tb
        ));
        
        int lx = 25, fx = 170, sy = 30, gy = 33, w = 215, h = 26;
        String[] labels = {"Policy Number:", "Holder Name:", "Holder Email:", "Phone Number:", "Aadhar Card:", "Address:", "Policy Type:", "Vehicle Type:", "Premium (₹):", "Duration (Yrs):"};
        for(int i=0; i<labels.length; i++) {
            JLabel lbl = new JLabel(labels[i]);
            lbl.setBounds(lx, sy + (i*gy), 140, h);
            lbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
            lbl.setForeground(new Color(52, 73, 94));
            leftPanel.add(lbl);
        }

        t_no = createStyledField();
        t_no.setBounds(fx, sy, w, h);
        t_no.setToolTipText("Khali chodein - Auto generate hoga (e.g. POL1002)");
        // Placeholder text
        t_no.setForeground(Color.GRAY);
        t_no.setText("Auto (e.g. POL1002)");
        t_no.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                if (t_no.getText().startsWith("Auto")) { t_no.setText(""); t_no.setForeground(new Color(52,73,94)); }
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                if (t_no.getText().isEmpty()) { t_no.setForeground(Color.GRAY); t_no.setText("Auto (e.g. POL1002)"); }
            }
        });
        leftPanel.add(t_no);
        t_name = createStyledField(); t_name.setBounds(fx, sy+gy, w, h); leftPanel.add(t_name);
        t_email = createStyledField(); t_email.setBounds(fx, sy+gy*2, w, h); leftPanel.add(t_email);
        t_phone = createStyledField(); t_phone.setBounds(fx, sy+gy*3, w, h); leftPanel.add(t_phone);
        t_aadhar = createStyledField(); t_aadhar.setBounds(fx, sy+gy*4, w, h); leftPanel.add(t_aadhar);
        t_address = createStyledField(); t_address.setBounds(fx, sy+gy*5, w, h); leftPanel.add(t_address);
        
        comboType = new JComboBox<>(new String[]{"Health Insurance", "Life Insurance", "Vehicle Insurance", "Property", "Auto Insurance"});
        comboType.setBounds(fx, sy+gy*6, w, h); comboType.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12)); leftPanel.add(comboType);
        comboVehicle = new JComboBox<>(new String[]{"N/A", "Two-Wheeler", "Four-Wheeler", "Commercial"});
        comboVehicle.setBounds(fx, sy+gy*7, w, h); comboVehicle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12)); leftPanel.add(comboVehicle);
        t_prem = createStyledField(); t_prem.setBounds(fx, sy+gy*8, w, h); leftPanel.add(t_prem);
        t_dur = createStyledField(); t_dur.setBounds(fx, sy+gy*9, w, h); leftPanel.add(t_dur);

        btnAIPredict = new JButton("\uD83E\uDD16 AI SUGGEST") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover()?new Color(52,152,219):new Color(41,128,185));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10); super.paintComponent(g);
            }
        };
        btnAIPredict.setBounds(25, 375, 175, 35);
        btnAIPredict.setBackground(new Color(41, 128, 185));
        btnAIPredict.setForeground(Color.WHITE);
        btnAIPredict.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        btnAIPredict.setFocusPainted(false); btnAIPredict.setBorderPainted(false); btnAIPredict.setContentAreaFilled(false);
        btnAIPredict.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAIPredict.addActionListener(this);
        leftPanel.add(btnAIPredict);

        btnClaim = new JButton("\uD83D\uDCDD CLAIM POLICY") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover()?new Color(243,156,18):new Color(230,126,34));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10); super.paintComponent(g);
            }
        };
        btnClaim.setBounds(220, 375, 175, 35);
        btnClaim.setBackground(new Color(230, 126, 34));
        btnClaim.setForeground(Color.WHITE);
        btnClaim.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        btnClaim.setFocusPainted(false); btnClaim.setBorderPainted(false); btnClaim.setContentAreaFilled(false);
        btnClaim.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClaim.addActionListener(this);
        leftPanel.add(btnClaim);

        btnAdd = createBtn("ADD & MAIL", 25, 418, new Color(39, 174, 96));
        btnUpdate = createBtn("UPDATE", 220, 418, new Color(41, 128, 185));
        btnDelete = createBtn("DELETE", 25, 462, new Color(192, 57, 43));
        btnClear = createBtn("CLEAR", 220, 462, new Color(149, 165, 166));
        btnExport = createBtn("EXPORT REPORT", 25, 506, new Color(142, 68, 173));
        btnReceipt = createBtn("GENERATE BILL", 220, 506, new Color(243, 156, 18));
        btnAlert = createBtn("DUE ALERTS", 25, 550, new Color(211, 84, 0));
        btnLogout = createBtn("LOGOUT", 220, 550, new Color(44, 62, 80));
        
        btnChat = new JButton("\uD83D\uDCAC AI CHATBOT") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover()?new Color(175,122,197):new Color(155,89,182));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10); super.paintComponent(g);
            }
        };
        btnChat.setBounds(25, 594, 175, 35);
        btnChat.setBackground(new Color(155, 89, 182));
        btnChat.setForeground(Color.WHITE);
        btnChat.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        btnChat.setFocusPainted(false); btnChat.setBorderPainted(false); btnChat.setContentAreaFilled(false);
        btnChat.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnChat.addActionListener(this);
        leftPanel.add(btnChat);

        btnTheme = new JButton("\uD83C\uDF13 SWITCH MODE") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover()?new Color(72,84,96):new Color(52,73,94));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10); super.paintComponent(g);
            }
        };
        btnTheme.setBounds(220, 594, 175, 35);
        btnTheme.setBackground(new Color(52, 73, 94));
        btnTheme.setForeground(Color.WHITE);
        btnTheme.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        btnTheme.setFocusPainted(false); btnTheme.setBorderPainted(false); btnTheme.setContentAreaFilled(false);
        btnTheme.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTheme.addActionListener(this);
        leftPanel.add(btnTheme);

        JButton[] actionBtns = {btnAdd, btnUpdate, btnDelete, btnClear, btnExport, btnReceipt, btnAlert, btnLogout};
        for(JButton b : actionBtns) { b.addActionListener(this); leftPanel.add(b); }
        mainPanel.add(leftPanel);

        lblSearch = new JLabel("\uD83D\uDD0D  Search Policy No:");
        lblSearch.setBounds(450, 20, 165, 30);
        lblSearch.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        lblSearch.setForeground(new Color(44, 62, 80));
        mainPanel.add(lblSearch);

        t_search = new JTextField();
        t_search.setBounds(618, 18, 260, 32);
        t_search.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        t_search.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(52, 152, 219), 2, true),
            BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));
        mainPanel.add(t_search);
        
        t_search.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String query = t_search.getText().toLowerCase();
                TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
                table.setRowSorter(sorter);
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + query, 0));
            }
        });

        model = new DefaultTableModel(new String[]{"Policy No", "Name", "Type", "Vehicle", "Premium", "Yrs", "Expiry"}, 0);
        table = new JTable(model);
        table.setRowHeight(38);
        table.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
        table.setSelectionBackground(new Color(174, 214, 241));
        table.setSelectionForeground(new Color(30, 60, 100));
        table.setGridColor(new Color(220, 230, 240));
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setShowGrid(true);

        JTableHeader headerTable = table.getTableHeader();
        headerTable.setBackground(new Color(22, 65, 118));
        headerTable.setForeground(Color.WHITE);
        headerTable.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        headerTable.setPreferredSize(new Dimension(100, 42));
        headerTable.setOpaque(true);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                try {
                    Date exp = new SimpleDateFormat("yyyy-MM-dd").parse(table.getValueAt(row, 6).toString());
                    long diff = (exp.getTime() - new Date().getTime()) / (1000*60*60*24);
                    if (isSelected) {
                        c.setBackground(new Color(174, 214, 241));
                    } else {
                        if (exp.before(new Date())) c.setBackground(new Color(255, 204, 204));
                        else if (diff <= 30) c.setBackground(new Color(255, 249, 196));
                        else {
                            if (isDarkMode) c.setBackground(new Color(45, 52, 54));
                            else if (row % 2 == 0) c.setBackground(Color.WHITE);
                            else c.setBackground(new Color(248, 249, 249));
                        }
                    }
                    if(isDarkMode && !isSelected) c.setForeground(Color.WHITE);
                    else if(!isSelected) c.setForeground(Color.BLACK);
                } catch (Exception ex) { c.setBackground(isDarkMode? new Color(45, 52, 54) : Color.WHITE); }
                return c;
            }
        });

     // Table create karne ke turant baad ye listener lagayein
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int r = table.getSelectedRow();
                if (r != -1) {
                    try {
                        // Table se basic info (jo dashboard par dikh rahi hai)
                        t_no.setText(model.getValueAt(r, 0).toString());
                        t_name.setText(model.getValueAt(r, 1).toString());
                        
                        // Policy type aur Vehicle combo boxes set karein (agar dashboard table mein hain)
                        comboType.setSelectedItem(model.getValueAt(r, 2).toString());
                        comboVehicle.setSelectedItem(model.getValueAt(r, 3).toString());
                        t_prem.setText(model.getValueAt(r, 4).toString());
                        t_dur.setText(model.getValueAt(r, 5).toString());

                        // Baaki details (Email, Phone, Aadhar) DB se uthane ke liye
                        fetchExtraDetails(model.getValueAt(r, 0).toString());
                        
                    } catch (Exception ex) {
                        System.out.println("Selection Error: " + ex.getMessage());
                    }
                }
            }
        });
        JScrollPane jsp = new JScrollPane(table);
        jsp.setBounds(450, 65, 870, 710);
        jsp.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(52, 152, 219), 2, true),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        jsp.getViewport().setBackground(Color.WHITE);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jsp.getVerticalScrollBar().setPreferredSize(new Dimension(12, 0));
        jsp.getVerticalScrollBar().setBackground(new Color(230, 240, 255));
        jsp.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(jsp);
        add(mainPanel, BorderLayout.CENTER);

        // Window resize hone par table aur leftPanel bhi resize ho
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent ev) {
                int w = getContentPane().getWidth();
                int h = getContentPane().getHeight();
                leftPanel.setBounds(15, 10, 420, h - 20);
                jsp.setBounds(450, 65, w - 470, h - 80);
                mainPanel.revalidate();
            }
        });

        loadData();
        setVisible(true);
    }

    private void sendSMS(String toNumber, String name, String details) {
        try {
            Twilio.init(SMS_SID, SMS_TOKEN);
            String formattedNumber = toNumber.startsWith("+91") ? toNumber : "+91" + toNumber;
            
            Message message = Message.creator(
                new PhoneNumber(formattedNumber), 
                new PhoneNumber(SMS_FROM),      
                "PolicyPro AI: Hello " + name + ", " + details
            ).create();

            System.out.println("SMS Sent Successfully! SID: " + message.getSid());
        } catch (Exception e) {
            System.out.println("SMS Error: " + e.getMessage());
        }
    }

    private void openClaimForm() {
        int r = table.getSelectedRow();
        String selectedAmount = "";
        if(r != -1) {
            selectedAmount = model.getValueAt(r, 4).toString();
        }

        JFrame f = new JFrame("Submit Insurance Claim");
        f.setSize(400, 450); f.setLayout(null); f.setLocationRelativeTo(null);
        
        JLabel l1 = new JLabel("Policy No:"); l1.setBounds(30, 30, 100, 30);
        JTextField tf1 = new JTextField(t_no.getText()); tf1.setBounds(150, 30, 180, 30);

        JLabel l2 = new JLabel("Reason:"); l2.setBounds(30, 80, 100, 30);
        JTextArea ta1 = new JTextArea(); ta1.setBounds(150, 80, 180, 100);
        ta1.setBorder(new LineBorder(Color.GRAY));

        JLabel l3 = new JLabel("Amount:"); l3.setBounds(30, 200, 100, 30);
        JTextField tf3 = new JTextField(selectedAmount); tf3.setBounds(150, 200, 180, 30);

        JButton submit = new JButton("SUBMIT CLAIM");
        submit.setBounds(100, 280, 180, 40);
        submit.setBackground(new Color(231, 76, 60));
        submit.setForeground(Color.WHITE);

        submit.addActionListener(ae -> {
            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc_swing", "root", "root")) {
                PreparedStatement ps = con.prepareStatement("INSERT INTO claims (policy_no, reason, amount, status) VALUES(?,?,?,'APPROVED')");
                ps.setString(1, tf1.getText());
                ps.setString(2, ta1.getText());
                ps.setString(3, tf3.getText());
                ps.executeUpdate();
                
                String phone = t_phone.getText().trim();
                String name = t_name.getText().trim();
                if(!phone.isEmpty()) {
                    new Thread(() -> {
                        sendSMS(phone, name, "your claim for Policy " + tf1.getText() + " of amount Rs." + tf3.getText() + " has been APPROVED successfully.");
                    }).start();
                }
                
                JOptionPane.showMessageDialog(f, "Claim Approved & SMS Sent!");
                f.dispose();
            } catch (Exception ex) { JOptionPane.showMessageDialog(f, "Error: " + ex.getMessage()); }
        });

        f.add(l1); f.add(tf1); f.add(l2); f.add(ta1); f.add(l3); f.add(tf3); f.add(submit);
        f.setVisible(true);
    }

    private void showVisualAnalytics(int mode) {
        JFrame fr = new JFrame(mode == 1 ? "Policy Distribution" : (mode == 2 ? "Monthly Business Trend" : "System Workflow"));
        fr.setSize(500, 550); 
        fr.setLocationRelativeTo(this);
        
        JPanel container = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (mode == 1) { 
                    HashMap<String, Integer> data = new HashMap<>();
                    for(int i=0; i<model.getRowCount(); i++) {
                        String s = model.getValueAt(i, 2).toString();
                        data.put(s, data.getOrDefault(s, 0) + 1);
                    }
                    int x=125, y=80, w=250, h=250, start=0, i=0;
                    Color[] colors = {new Color(52, 152, 219), new Color(46, 204, 113), new Color(241, 196, 15), new Color(231, 76, 60), new Color(155, 89, 182)};
                    g2.setFont(new java.awt.Font("Segoe UI Bold", java.awt.Font.BOLD, 18));
                    g2.drawString("POLICY DISTRIBUTION", 150, 45);
                    for(String key : data.keySet()) {
                        int angle = (int)(data.get(key) * 360.0 / (model.getRowCount()==0?1:model.getRowCount()));
                        g2.setColor(colors[i % 5]); 
                        g2.fillArc(x, y, w, h, start, angle);
                        g2.fillRect(100, 360+(i*25), 15, 15);
                        g2.setColor(Color.DARK_GRAY);
                        g2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 13));
                        g2.drawString(key + " (" + data.get(key) + ")", 125, 372+(i*25));
                        start += angle; i++;
                    }
                } 
                else if (mode == 2) { 
                    g2.setFont(new java.awt.Font("Segoe UI Bold", java.awt.Font.BOLD, 18));
                    g2.drawString("MONTHLY SALES TREND", 140, 45);
                    int padding = 60, graphWidth = getWidth() - 2 * padding, graphHeight = getHeight() - 2 * padding - 100;
                    g2.setStroke(new BasicStroke(2f));
                    g2.drawLine(padding, padding + graphHeight, padding + graphWidth, padding + graphHeight);
                    g2.drawLine(padding, padding, padding, padding + graphHeight);
                    int[] values = {150, 280, 190, 350, 420, 380, 480};
                    String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul"};
                    int xStep = graphWidth / (values.length - 1);
                    g2.setStroke(new BasicStroke(3f));
                    g2.setColor(new Color(52, 152, 219));
                    for (int i = 0; i < values.length - 1; i++) {
                        int x1 = padding + i * xStep, y1 = (padding + graphHeight) - values[i];
                        int x2 = padding + (i + 1) * xStep, y2 = (padding + graphHeight) - values[i+1];
                        g2.drawLine(x1, y1, x2, y2);
                        g2.fillOval(x1-4, y1-4, 8, 8);
                    }
                    g2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 10));
                    for(int i=0; i<months.length; i++) g2.drawString(months[i], padding + i * xStep - 10, padding + graphHeight + 20);
                }
                else if (mode == 3) { 
                    g2.setFont(new java.awt.Font("Segoe UI Bold", java.awt.Font.BOLD, 18));
                    g2.drawString("SYSTEM DATA FLOW", 150, 45);
                    String[] stages = {"USER INPUT", "AI ANALYSIS", "DB STORAGE", "PDF ENGINE", "EMAIL SMTP"};
                    Color[] boxColors = {new Color(52, 73, 94), new Color(41, 128, 185), new Color(39, 174, 96), new Color(243, 156, 18), new Color(192, 57, 43)};
                    int boxW = 180, boxH = 50, startY = 80;
                    for(int i=0; i<stages.length; i++) {
                        g2.setColor(boxColors[i]);
                        g2.fillRoundRect(150, startY + (i*85), boxW, boxH, 15, 15);
                        g2.setColor(Color.WHITE);
                        g2.drawString(stages[i], 190, startY + (i*85) + 30);
                        if(i < stages.length - 1) {
                            g2.setColor(Color.GRAY);
                            g2.drawLine(240, startY + (i*85) + 50, 240, startY + (i*85) + 85);
                        }
                    }
                }
            }
        };
        container.setBackground(Color.WHITE);
        fr.add(container);
        fr.setVisible(true);
    }

    private void toggleTheme() {
        isDarkMode = !isDarkMode;
        Color bg        = isDarkMode ? new Color(25, 32, 42)  : new Color(240, 244, 248);
        Color panelBg   = isDarkMode ? new Color(35, 44, 55)  : Color.WHITE;
        Color fieldBg   = isDarkMode ? new Color(50, 62, 75)  : Color.WHITE;
        Color txt       = isDarkMode ? Color.WHITE             : new Color(52, 73, 94);
        Color labelClr  = isDarkMode ? new Color(180, 200, 230): new Color(52, 73, 94);
        Color borderClr = isDarkMode ? new Color(70, 100, 140) : new Color(180, 210, 230);

        // Main background
        mainPanel.setBackground(bg);
        getContentPane().setBackground(bg);

        // Left panel background
        leftPanel.setBackground(panelBg);
        leftPanel.repaint();

        // Search bar
        lblSearch.setForeground(txt);
        t_search.setBackground(fieldBg);
        t_search.setForeground(txt);
        t_search.setCaretColor(txt);
        t_search.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(borderClr, 2, true),
            BorderFactory.createEmptyBorder(2, 8, 2, 8)
        ));

        // All components inside leftPanel
        for (Component c : leftPanel.getComponents()) {
            if (c instanceof JLabel) {
                c.setForeground(labelClr);
            } else if (c instanceof JTextField) {
                c.setBackground(fieldBg);
                c.setForeground(txt);
                ((JTextField) c).setCaretColor(txt);
                ((JTextField) c).setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(borderClr, 1, true),
                    BorderFactory.createEmptyBorder(2, 6, 2, 6)
                ));
            } else if (c instanceof JComboBox) {
                c.setBackground(fieldBg);
                c.setForeground(txt);
            }
        }

        // Table
        table.setBackground(panelBg);
        table.setForeground(txt);
        table.getTableHeader().setBackground(isDarkMode ? new Color(20, 50, 90) : new Color(22, 65, 118));
        table.getTableHeader().setForeground(Color.WHITE);
        table.repaint();

        leftPanel.revalidate();
        leftPanel.repaint();
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void fetchExtraDetails(String pNo) {
        String url = "jdbc:mysql://localhost:3306/jdbc_swing";
        String user = "root";
        String pass = "root";

        try (Connection con = DriverManager.getConnection(url, user, pass)) {
            // Query mein 'email' column ko bhi add kiya gaya hai
            String sql = "SELECT phone, aadhar, address, email FROM insurance_db WHERE policy_no = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, pNo);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                // Index (1,2,3) ki jagah direct name use karne se data sahi field mein jayega
                t_phone.setText(rs.getString("phone") != null ? rs.getString("phone") : "");
                
                // Aadhar details set karna (Privacy maintained)
                t_aadhar.setText(rs.getString("aadhar") != null ? rs.getString("aadhar") : "");
                
                t_address.setText(rs.getString("address") != null ? rs.getString("address") : "");
                
                // Email ab properly fetch hoga kyunki humne SELECT query mein ise mang liya hai
                t_email.setText(rs.getString("email") != null ? rs.getString("email") : "");
                
                System.out.println("Extra details loaded for policy: " + pNo);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            // Agar error aaye toh message dikhayega
            JOptionPane.showMessageDialog(this, "Details fetch karne mein error: " + ex.getMessage());
        }
    }

    private JButton createBtn(String t, int x, int y, Color bg) {
        JButton b = new JButton(t) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(bg.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(bg.brighter());
                } else {
                    g2.setColor(bg);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                super.paintComponent(g);
            }
        };
        b.setBounds(x, y, 175, 38);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    /** Creates a styled stat card for the dashboard strip */
    private JPanel createStatCard(String title, String value, Color accent) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(4, 18, 4, 18));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 11));
        lblTitle.setForeground(new Color(180, 210, 255));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblValue = new JLabel(value);
        lblValue.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
        lblValue.setForeground(accent);
        lblValue.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(lblTitle);
        card.add(lblValue);
        return card;
    }

    /** Creates a styled text field with rounded border */
    private JTextField createStyledField() {
        JTextField tf = new JTextField();
        tf.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
        tf.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(180, 210, 230), 1, true),
            BorderFactory.createEmptyBorder(2, 6, 2, 6)
        ));
        return tf;
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnTheme) toggleTheme();
        else if (e.getSource() == btnAdd) saveData();
        else if (e.getSource() == btnUpdate) updateData();
        else if (e.getSource() == btnDelete) deleteData();
        else if (e.getSource() == btnClear) clearFields();
        else if (e.getSource() == btnReceipt) generateColorfulReceipt();
        else if (e.getSource() == btnExport) exportProfessionalReport();
        else if (e.getSource() == btnAlert) checkAlerts();
        else if (e.getSource() == btnAIPredict) runAIPrediction();
        else if (e.getSource() == btnChat) openChatbot();
        else if (e.getSource() == btnLogout) System.exit(0);
        else if (e.getSource() == btnClaim) openClaimForm(); 
    }

    private void exportProfessionalReport() {
        JFileChooser c = new JFileChooser();
        c.setSelectedFile(new File("Master_Database_Report.pdf"));
        if(c.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = c.getSelectedFile().getAbsolutePath();
            try {
                Document doc = new Document(PageSize.A4.rotate());
                PdfWriter.getInstance(doc, new FileOutputStream(path));
                doc.open();

                BaseColor themeColor = new BaseColor(44, 62, 80);
                Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22, themeColor);
                Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
                Font summaryFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, themeColor);

                Paragraph p1 = new Paragraph("PolicyTrack Elite - Master Inventory Report", titleFont);
                p1.setAlignment(Element.ALIGN_CENTER); 
                doc.add(p1);
                
                Paragraph p2 = new Paragraph("Report Generated on: " + new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").format(new Date()) + "\n\n", dateFont);
                p2.setAlignment(Element.ALIGN_CENTER);
                doc.add(p2);

                PdfPTable summaryTab = new PdfPTable(2);
                summaryTab.setWidthPercentage(40);
                summaryTab.setHorizontalAlignment(Element.ALIGN_RIGHT);
                
                PdfPCell s1 = new PdfPCell(new Phrase(lblTotalPolicies.getText(), summaryFont));
                s1.setBackgroundColor(new BaseColor(245, 245, 245)); s1.setPadding(5);
                summaryTab.addCell(s1);
                
                PdfPCell s2 = new PdfPCell(new Phrase(lblTotalPremium.getText(), summaryFont));
                s2.setBackgroundColor(new BaseColor(245, 245, 245)); s2.setPadding(5);
                summaryTab.addCell(s2);
                
                doc.add(summaryTab);
                doc.add(new Paragraph("\n"));

                PdfPTable pdfTable = new PdfPTable(7);
                pdfTable.setWidthPercentage(100);
                pdfTable.setSpacingBefore(10f);
                float[] columnWidths = {2f, 3f, 3f, 2f, 2f, 1.5f, 2f};
                pdfTable.setWidths(columnWidths);

                String[] headers = {"Policy No", "Holder Name", "Type", "Vehicle", "Premium", "Yrs", "Expiry"};
                for (String h : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(h, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, BaseColor.WHITE)));
                    cell.setBackgroundColor(themeColor);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setPadding(8);
                    pdfTable.addCell(cell);
                }

                for(int i=0; i<model.getRowCount(); i++) {
                    for(int j=0; j<7; j++) {
                        PdfPCell dataCell = new PdfPCell(new Phrase(model.getValueAt(i, j).toString(), FontFactory.getFont(FontFactory.HELVETICA, 10)));
                        dataCell.setPadding(5);
                        if(i % 2 == 1) dataCell.setBackgroundColor(new BaseColor(240, 243, 244));
                        pdfTable.addCell(dataCell);
                    }
                }
                
                doc.add(pdfTable);
                doc.add(new Paragraph("\n\n"));
                LineSeparator ls = new LineSeparator();
                ls.setLineColor(BaseColor.LIGHT_GRAY);
                doc.add(ls);
                Paragraph footer = new Paragraph("PolicyTrack Management System Internal Document | Confidental", dateFont);
                footer.setAlignment(Element.ALIGN_CENTER);
                doc.add(footer);

                doc.close();
                JOptionPane.showMessageDialog(this, "Professional Report Exported Successfully!");
            } catch (Exception ex) { ex.printStackTrace(); JOptionPane.showMessageDialog(this, "Export Error!"); }
        }
    }

    private void generateColorfulReceipt() {
        int r = table.getSelectedRow();
        if(r == -1) { JOptionPane.showMessageDialog(this, "Select a record first!"); return; }
        
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("Invoice_" + model.getValueAt(r, 0).toString() + ".pdf"));
        if(chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            try {
                Document doc = new Document(PageSize.A4);
                PdfWriter.getInstance(doc, new FileOutputStream(path));
                doc.open();

                BaseColor eliteBlue = new BaseColor(44, 62, 80);
                Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, eliteBlue);
                Font subHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.GRAY);
                Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 11);
                Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);

                PdfPTable headerTab = new PdfPTable(2);
                headerTab.setWidthPercentage(100);
                headerTab.setWidths(new int[]{3, 2});

                PdfPCell logoCell = new PdfPCell(new Phrase("PolicyTrack Elite", headerFont));
                logoCell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
                headerTab.addCell(logoCell);

                PdfPCell invInfo = new PdfPCell(new Phrase("INVOICE\n#" + model.getValueAt(r, 0).toString() + "\nDate: " + new SimpleDateFormat("dd-MMM-yyyy").format(new Date()), subHeaderFont));
                invInfo.setHorizontalAlignment(Element.ALIGN_RIGHT);
                invInfo.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
                headerTab.addCell(invInfo);
                doc.add(headerTab);

                doc.add(new Paragraph("\n"));
                doc.add(new LineSeparator());
                doc.add(new Paragraph("\n"));

                PdfPTable billingTab = new PdfPTable(2);
                billingTab.setWidthPercentage(100);
                
                PdfPCell billedTo = new PdfPCell(new Phrase("BILLED TO:\n" + model.getValueAt(r, 1).toString() + "\nPhone: " + t_phone.getText(), boldFont));
                billedTo.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
                billingTab.addCell(billedTo);

                PdfPCell companyFrom = new PdfPCell(new Phrase("FROM:\nPolicyTrack Elite Ltd.\nBhopal, India", boldFont));
                companyFrom.setHorizontalAlignment(Element.ALIGN_RIGHT);
                companyFrom.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
                billingTab.addCell(companyFrom);
                doc.add(billingTab);

                doc.add(new Paragraph("\n\n"));

                PdfPTable mainTable = new PdfPTable(4);
                mainTable.setWidthPercentage(100);
                mainTable.setWidths(new int[]{4, 2, 2, 3});

                String[] headers = {"Description", "Duration", "Expiry", "Total (INR)"};
                for(String head : headers) {
                    PdfPCell c1 = new PdfPCell(new Phrase(head, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE)));
                    c1.setBackgroundColor(eliteBlue);
                    c1.setPadding(8);
                    c1.setHorizontalAlignment(Element.ALIGN_CENTER);
                    mainTable.addCell(c1);
                }

                mainTable.addCell(new PdfPCell(new Phrase(model.getValueAt(r, 2).toString() + " (" + model.getValueAt(r, 3).toString() + ")", normalFont)));
                mainTable.addCell(new PdfPCell(new Phrase(model.getValueAt(r, 5).toString() + " Years", normalFont)));
                mainTable.addCell(new PdfPCell(new Phrase(model.getValueAt(r, 6).toString(), normalFont)));
                
                PdfPCell priceCell = new PdfPCell(new Phrase("Rs. " + model.getValueAt(r, 4).toString(), boldFont));
                priceCell.setBackgroundColor(new BaseColor(245, 245, 245));
                mainTable.addCell(priceCell);
                
                doc.add(mainTable);

                doc.add(new Paragraph("\n\n\n\n"));
                Paragraph footer = new Paragraph("Thank you for choosing PolicyTrack Elite. This is a computer-generated invoice.", FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.LIGHT_GRAY));
                footer.setAlignment(Element.ALIGN_CENTER);
                doc.add(footer);

                doc.close();
                JOptionPane.showMessageDialog(this, "Attractive Invoice Generated Successfully!");
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    public void loadData() {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc_swing", "root", "root")) {
            model.setRowCount(0); // Table clear karein
            
            // Humesha column names ke sath fetch karein taaki sequence change hone par error na aaye
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM insurance_db");
            
            double tot = 0; 
            int cnt = 0;

            while (rs.next()) {
                // Column names wahi likhein jo aapke MySQL table mein hain
                model.addRow(new Object[]{
                    rs.getString("policy_no"),      // Index 1
                    rs.getString("holder_name"),    // Index 2
                    rs.getString("policy_type"),    // Index 3
                    rs.getString("vehicle_type"),   // Index 4
                    rs.getDouble("premium_amount"), // Index 5
                    rs.getInt("duration_years"),    // Index 6
                    rs.getString("expiry_date")     // Index 7
                });

                // Analytics calculate karein
                tot += rs.getDouble("premium_amount"); 
                cnt++;
            }

            // Dashboard labels update karein
            lblTotalPolicies.setText(String.valueOf(cnt));
            lblTotalPremium.setText("₹" + String.format("%.2f", tot));

        } catch (Exception e) {
            e.printStackTrace(); // Error console mein dikhega agar connection fail hua toh
            JOptionPane.showMessageDialog(this, "Data load karne mein error: " + e.getMessage());
        }
    }

    private void saveData() {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc_swing", "root", "root")) {

            // ✅ Auto Policy Number - agar empty ya placeholder hai toh automatically generate karo
            String policyNo = t_no.getText().trim();
            if (policyNo.isEmpty() || policyNo.startsWith("Auto")) {
                policyNo = generatePolicyNumber(con);
                t_no.setText(policyNo);
                t_no.setForeground(new Color(52, 73, 94));
            }

            // ✅ Duplicate check
            PreparedStatement checkPs = con.prepareStatement("SELECT COUNT(*) FROM insurance_db WHERE policy_no = ?");
            checkPs.setString(1, policyNo);
            ResultSet checkRs = checkPs.executeQuery();
            checkRs.next();
            if (checkRs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this,
                    "❌ Policy Number \"" + policyNo + "\" already exists!\nPlease enter a different Policy Number.",
                    "Duplicate Policy Number",
                    JOptionPane.ERROR_MESSAGE);
                t_no.requestFocus();
                t_no.selectAll();
                return;
            }

            int yrs = Integer.parseInt(t_dur.getText().trim());
            Calendar cal = Calendar.getInstance(); cal.add(Calendar.YEAR, yrs);
            String exp = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO insurance_db (policy_no, holder_name, policy_type, vehicle_type, premium_amount, duration_years, expiry_date, phone, aadhar, address, email) VALUES(?,?,?,?,?,?,?,?,?,?,?)");
            ps.setString(1, policyNo);
            ps.setString(2, t_name.getText());
            ps.setString(3, comboType.getSelectedItem().toString());
            ps.setString(4, comboVehicle.getSelectedItem().toString());
            ps.setDouble(5, Double.parseDouble(t_prem.getText()));
            ps.setInt(6, yrs);
            ps.setString(7, exp);
            ps.setString(8, t_phone.getText());
            ps.setString(9, t_aadhar.getText());
            ps.setString(10, t_address.getText());
            ps.setString(11, t_email.getText());

            int result = ps.executeUpdate();
            loadData();

            if (result > 0) {
                String customerEmail = t_email.getText().trim();
                String customerPhone = t_phone.getText().trim();
                String customerName  = t_name.getText().trim();
                final String finalPolicyNo = policyNo;

                new Thread(() -> {
                    if (!customerEmail.isEmpty()) {
                        sendMail(customerEmail, customerName, "Policy Active: " + finalPolicyNo,
                            "Your policy " + finalPolicyNo + " is successfully active.");
                    }
                    if (!customerPhone.isEmpty()) {
                        sendSMS(customerPhone, customerName,
                            "Your policy " + finalPolicyNo + " is now successfully active and verified by PolicyPro AI.");
                    }
                }).start();

                JOptionPane.showMessageDialog(this,
                    "✅ Registration Successful!\nPolicy No: " + finalPolicyNo + "\nEmail & SMS Sent.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Error Saving! " + ex.getMessage());
        }
    }

    /** Auto Policy Number generate karta hai - POL + timestamp based unique number */
    private String generatePolicyNumber(Connection con) throws Exception {
        // Last policy number DB se lo aur usse +1 karo
        PreparedStatement ps = con.prepareStatement(
            "SELECT policy_no FROM insurance_db ORDER BY id DESC LIMIT 1");
        ResultSet rs = ps.executeQuery();
        int nextNum = 1001;
        if (rs.next()) {
            String last = rs.getString("policy_no");
            try {
                // POL1001 -> 1001 -> 1002
                int lastNum = Integer.parseInt(last.replaceAll("[^0-9]", ""));
                nextNum = lastNum + 1;
            } catch (NumberFormatException e) {
                nextNum = (int)(System.currentTimeMillis() % 100000);
            }
        }
        // Duplicate na ho toh ensure karo
        String candidate = "POL" + nextNum;
        PreparedStatement check = con.prepareStatement(
            "SELECT COUNT(*) FROM insurance_db WHERE policy_no = ?");
        check.setString(1, candidate);
        ResultSet cr = check.executeQuery(); cr.next();
        while (cr.getInt(1) > 0) {
            nextNum++;
            candidate = "POL" + nextNum;
            check.setString(1, candidate);
            cr = check.executeQuery(); cr.next();
        }
        return candidate;
    }

    private void sendMail(String to, String name, String subject, String bodyContent) { 
        final String user = "ankushkr.hjp11@gmail.com"; 
        final String pass = "dtly vfms zcgt evrx"; 

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        javax.mail.Session session = javax.mail.Session.getInstance(props, new javax.mail.Authenticator() {
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new javax.mail.PasswordAuthentication(user, pass);
            }
        });

        try {
            // Yahan 'javax.mail.Message' use kiya hai taaki Twilio ke Message se conflict na ho
            javax.mail.Message message = new javax.mail.internet.MimeMessage(session);
            message.setFrom(new javax.mail.internet.InternetAddress(user));
            message.setRecipients(javax.mail.Message.RecipientType.TO, javax.mail.internet.InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText("Dear " + name + ",\n\n" + bodyContent + "\n\nRegards,\nPolicyTrack AI Team");
            
            // Transport ko bhi pura path diya hai
            javax.mail.Transport.send(message);
            
            System.out.println("Mail Sent Successfully to: " + to);
        } catch (javax.mail.MessagingException e) { 
            e.printStackTrace(); 
        }
    }

    private void updateData() {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc_swing", "root", "root")) {
            int yrs = Integer.parseInt(t_dur.getText().trim());
            Calendar cal = Calendar.getInstance(); cal.add(Calendar.YEAR, yrs);
            String exp = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
            PreparedStatement ps = con.prepareStatement("UPDATE insurance_db SET holder_name=?, policy_type=?, vehicle_type=?, premium_amount=?, duration_years=?, expiry_date=?, phone=?, aadhar=?, address=? WHERE policy_no=?");
            ps.setString(1, t_name.getText()); ps.setString(2, comboType.getSelectedItem().toString());
            ps.setString(3, comboVehicle.getSelectedItem().toString()); ps.setDouble(4, Double.parseDouble(t_prem.getText()));
            ps.setInt(5, yrs); ps.setString(6, exp); ps.setString(7, t_phone.getText()); ps.setString(8, t_aadhar.getText()); ps.setString(9, t_address.getText()); ps.setString(10, t_no.getText());
            ps.executeUpdate(); loadData();
            JOptionPane.showMessageDialog(this, "Updated Successfully!");
        } catch (Exception ex) {}
    }

    private void deleteData() {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc_swing", "root", "root")) {
            PreparedStatement ps = con.prepareStatement("DELETE FROM insurance_db WHERE policy_no=?");
            ps.setString(1, t_no.getText()); ps.executeUpdate(); loadData(); clearFields();
            JOptionPane.showMessageDialog(this, "Deleted!");
        } catch (Exception ex) {}
    }

    private void checkAlerts() {
        int expCount = 0, dueCount = 0;
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc_swing", "root", "root")) {
            ResultSet rs = con.createStatement().executeQuery("SELECT holder_name, phone, expiry_date, policy_no, email FROM insurance_db");
            while(rs.next()) {
                String name = rs.getString(1);
                String phone = rs.getString(2);
                String pNo = rs.getString(4);
                String email = rs.getString(5);
                Date d = new SimpleDateFormat("yyyy-MM-dd").parse(rs.getString(3));
                long diff = (d.getTime() - new Date().getTime()) / (1000*60*60*24);

                if(d.before(new Date())) {
                    expCount++;
                    new Thread(() -> {
                        if(phone != null && !phone.isEmpty()) sendSMS(phone, name, "Urgent: Your Policy " + pNo + " has EXPIRED. Renew now!");
                        if(email != null && !email.isEmpty()) sendMail(email, name, "URGENT: Policy Expired", "Your policy " + pNo + " has expired. Please renew immediately.");
                    }).start();
                } else if(diff <= 30) {
                    dueCount++;
                    new Thread(() -> {
                        if(phone != null && !phone.isEmpty()) sendSMS(phone, name, "Reminder: Your Policy " + pNo + " expires in " + diff + " days.");
                        if(email != null && !email.isEmpty()) sendMail(email, name, "Reminder: Policy Renewal", "Your policy " + pNo + " will expire in " + diff + " days.");
                    }).start();
                }
            }
            JOptionPane.showMessageDialog(this, "Alerts Processed:\nExpired: " + expCount + "\nDue (30 days): " + dueCount + "\n\nAuto-Renewal SMS and Emails sent to all affected customers!");
        } catch(Exception ex) { JOptionPane.showMessageDialog(this, "Alert Error: " + ex.getMessage()); }
    }

    private void runAIPrediction() {
        try {
            int yrs = Integer.parseInt(t_dur.getText().trim());
            double predicted = 5000 + (yrs * 1250.75);
            t_prem.setText(String.format("%.2f", predicted));
        } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Enter Duration!"); }
    }

    private String analyzeSentiment(String text) {
        text = text.toLowerCase();
        if (text.contains("good") || text.contains("thanks")) return "POSITIVE 😊";
        if (text.contains("bad") || text.contains("problem")) return "NEGATIVE 😡";
        return "NEUTRAL 😐";
    }

    private void openChatbot() {
        JFrame f = new JFrame("PolicyPro AI: Smart Guardian Assistant");
        f.setSize(520, 800); f.setLocationRelativeTo(this); f.setLayout(new BorderLayout());

        JTextPane area = new JTextPane();
        area.setEditable(false); area.setContentType("text/html"); area.setBackground(new Color(245, 246, 250));
        area.addHyperlinkListener(e -> { if (e.getEventType() == javax.swing.event.HyperlinkEvent.EventType.ACTIVATED) triggerManualDownload(e.getDescription()); });

        chatHistory.setLength(0);
        chatHistory.append("<html><body style='font-family:Segoe UI; margin:10px;'>")
                   .append("<div style='text-align:center; background:#0984e3; color:white; padding:15px; border-radius:10px;'>")
                   .append("<h2 style='margin:0;'>🛡️ PolicyPro AI</h2><p style='margin:5px 0 0 0;'>Aapka Smart Assistant</p></div>")
                   .append("<div style='margin-top:15px; color:#636e72;'>👋 Namaste! Policy Number enter karein...</div>");
        area.setText(chatHistory.toString());

        // Button Panel (Insights, Trends, Process) - Bina kisi change ke
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnPanel.setBackground(Color.WHITE);
        String[] labels = {"📊 Insights", "📈 Trends", "🗺️ Process"};
        Color[] colors = {new Color(108, 92, 231), new Color(0, 184, 148), new Color(225, 112, 85)};
        
        for(int i=0; i<labels.length; i++) {
            final int index = i;
            JButton b = new JButton(labels[i]);
            b.setBackground(colors[i]); b.setForeground(Color.WHITE); b.setFocusPainted(false);
            b.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 12));
            b.addActionListener(ev -> {
                if(index == 0) {
                    // INSIGHTS: Pie Chart Analytics (Progress Bars)
                    HashMap<String, Integer> stats = new HashMap<>();
                    for(int j=0; j<model.getRowCount(); j++) {
                        String type = model.getValueAt(j, 2).toString();
                        stats.put(type, stats.getOrDefault(type, 0) + 1);
                    }
                    chatHistory.append("<div style='background:white; border-left:5px solid #6c5ce7; padding:15px; border-radius:8px; margin-top:10px;'>")
                               .append("<b style='color:#6c5ce7;'>📊 POLICY DISTRIBUTION</b><hr>");
                    for(String key : stats.keySet()) {
                        int count = stats.get(key);
                        int percentage = (count * 100) / (model.getRowCount() == 0 ? 1 : model.getRowCount());
                        chatHistory.append("<div style='margin-bottom:8px;'>")
                                   .append("<span style='font-size:11px;'>").append(key).append("</span>")
                                   .append("<div style='background:#f1f2f6; width:100%; height:8px; border-radius:5px;'>")
                                   .append("<div style='background:#6c5ce7; height:8px; border-radius:5px; width:").append(percentage).append("%;'></div>")
                                   .append("</div></div>");
                    }
                    chatHistory.append("</div>");
                    showVisualAnalytics(1); // Pie Chart Popup
                } else if(index == 1) {
                    chatHistory.append("<div style='background:white; border-left:5px solid #00b894; padding:15px; border-radius:8px; margin-top:10px;'><b>📈 TRENDS:</b> Bhopal leads in Life Insurance business.</div>");
                } else {
                    chatHistory.append("<div style='background:white; border-left:5px solid #e17055; padding:15px; border-radius:8px; margin-top:10px;'><b>🗺️ PROCESS:</b> Fill -> AI Verify -> Active.</div>");
                }
                area.setText(chatHistory.toString() + "</body></html>");
            });
            btnPanel.add(b);
        }

        JTextField input = new JTextField();
        input.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 15));
        input.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(200, 200, 200), 1), BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        input.addActionListener(e -> {
            String m = input.getText().trim(); if(m.isEmpty()) return;
            chatHistory.append("<div style='text-align:right; margin-top:10px;'><span style='background:#dfe6e9; padding:8px 12px; border-radius:15px; display:inline-block;'><b>You:</b> ").append(m).append("</span></div>");
            
            try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc_swing", "root", "root")) {
                PreparedStatement ps = con.prepareStatement("SELECT * FROM insurance_db WHERE policy_no = ?");
                ps.setString(1, m); ResultSet rs = ps.executeQuery();
                
                if(rs.next()) {
                    // AB YAHAN SAARI DETAILS DIKHEGI
                    chatHistory.append("<div style='background:white; border:1px solid #dcdde1; padding:15px; border-radius:10px; margin-top:10px;'>")
                               .append("<b style='color:#0984e3;'>✅ RECORD FOUND</b><hr>")
                               .append("<table style='width:100%; font-size:12px;'>")
                               .append("<tr><td><b>Holder:</b></td><td>").append(rs.getString("holder_name")).append("</td></tr>")
                               .append("<tr><td><b>Email:</b></td><td>").append(rs.getString("email")).append("</td></tr>")
                               .append("<tr><td><b>Phone:</b></td><td>").append(rs.getString("phone")).append("</td></tr>")
                               .append("<tr><td><b>Aadhar:</b></td><td>").append(rs.getString("aadhar")).append("</td></tr>")
                               .append("<tr><td><b>Address:</b></td><td>").append(rs.getString("address")).append("</td></tr>")
                               .append("<tr><td><b>Type:</b></td><td>").append(rs.getString("policy_type")).append("</td></tr>")
                               .append("<tr><td><b>Premium:</b></td><td style='color:#27ae60;'>₹").append(rs.getString("premium_amount")).append("</td></tr>")
                               .append("<tr><td><b>Expiry:</b></td><td>").append(rs.getString("expiry_date")).append("</td></tr>")
                               .append("</table>")
                               .append("<br><center><a href='").append(m).append("' style='color:#0984e3; text-decoration:none; font-weight:bold;'>📥 DOWNLOAD PDF</a></center></div>");
                } else { 
                    chatHistory.append("<div style='color:red; margin-top:10px;'>❌ Record Not Found!</div>"); 
                }
                area.setText(chatHistory.toString() + "</body></html>"); 
                input.setText("");
                area.setCaretPosition(area.getDocument().getLength());
            } catch (Exception ex) { ex.printStackTrace(); }
        });

        f.add(btnPanel, BorderLayout.NORTH); f.add(new JScrollPane(area), BorderLayout.CENTER); f.add(input, BorderLayout.SOUTH);
        f.addWindowListener(new WindowAdapter() { public void windowOpened(WindowEvent e) { input.requestFocus(); } });
        f.setVisible(true);
    }


    private void triggerManualDownload(String pNo) {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc_swing", "root", "root")) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM insurance_db WHERE policy_no = ?");
            ps.setString(1, pNo); 
            ResultSet rs = ps.executeQuery();
            if(rs.next()) {
                downloadSinglePolicyPDF(
                    rs.getString(1), rs.getString(2), rs.getString(3), 
                    rs.getString(5), rs.getString(7), rs.getString(8)
                );
            }
        } catch (Exception ex) {}
    }

    private void downloadSinglePolicyPDF(String no, String name, String type, String prem, String exp, String phone) {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("Policy_Details_" + no + ".pdf"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                Document doc = new Document(PageSize.A4);
                PdfWriter.getInstance(doc, new FileOutputStream(chooser.getSelectedFile().getAbsolutePath()));
                doc.open();
                com.itextpdf.text.Font f1 = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD);
                doc.add(new Paragraph("PolicyPro AI - Insurance Certificate", f1));
                doc.add(new Paragraph("---------------------------------------------------------"));
                doc.add(new Paragraph("\nPolicy No: " + no + "\nName: " + name + "\nType: " + type + "\nPremium: Rs. " + prem + "\nExpiry: " + exp + "\nPhone: " + phone));
                doc.add(new Paragraph("\nStatus: VERIFIED & ACTIVE"));
                doc.close();
                JOptionPane.showMessageDialog(this, "PDF Downloaded Successfully!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "PDF Error: " + ex.getMessage());
            }
        }
    }

    private void clearFields() { 
        t_no.setText(""); t_name.setText(""); t_prem.setText(""); t_dur.setText(""); 
        t_email.setText(""); t_phone.setText(""); t_aadhar.setText(""); t_address.setText(""); 
    }
    public static void main(String[] args) { new Login(); }
}