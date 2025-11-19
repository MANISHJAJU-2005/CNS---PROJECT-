import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.Base64;

public class HackerCryptoUI extends JFrame {
    private JTextField filePathField, keyField;
    private JTextArea cipherOutputArea, keyOutputArea;
    private JButton[] cipherButtons;
    private JLabel statusLabel, clockLabel;
    private JButton encryptModeBtn, decryptModeBtn;
    private String currentCipher = "MORSE CODE";
    private String fileContent = "";
    private boolean isEncryptMode = true;
    
    // Elite hacker colors
    private static final Color PURE_BLACK = new Color(0, 0, 0);
    private static final Color DARK_BG = new Color(8, 8, 12);
    private static final Color PANEL_BG = new Color(12, 12, 18);
    private static final Color NEON_GREEN = new Color(0, 255, 65);
    private static final Color NEON_RED = new Color(255, 20, 60);
    private static final Color NEON_CYAN = new Color(0, 255, 255);
    private static final Color NEON_PURPLE = new Color(200, 50, 255);
    private static final Color TERMINAL_GREEN = new Color(0, 255, 100);
    private static final Color WARNING_ORANGE = new Color(255, 150, 0);
    private static final Color BUTTON_BG = new Color(15, 15, 22);
    private static final Color BUTTON_HOVER = new Color(25, 25, 35);
    private static final Color BUTTON_SELECTED = new Color(0, 50, 0);
    
    public HackerCryptoUI() {
        setTitle("▰▰▰ BLACKHAT CIPHER TERMINAL v2.0 ▰▰▰");
        setSize(1300, 850);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(PURE_BLACK);
        setUndecorated(false);
        
        // Main animated panel
        AnimatedBackgroundPanel mainPanel = new AnimatedBackgroundPanel();
        mainPanel.setLayout(new BorderLayout(0, 0));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Top bar with status
        JPanel topBar = createTopBar();
        
        // Control panel
        JPanel controlPanel = createControlPanel();
        controlPanel.setPreferredSize(new Dimension(350, 0));
        
        // Center display with split view
        JPanel centerPanel = createCenterPanel();
        
        // Bottom cipher grid with glow effect
        JPanel bottomPanel = createBottomPanel();
        
        mainPanel.add(topBar, BorderLayout.NORTH);
        mainPanel.add(controlPanel, BorderLayout.WEST);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        setVisible(true);
        
        startAnimations();
    }
    
    private JPanel createTopBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(createGlowBorder(NEON_RED, 2));
        bar.setPreferredSize(new Dimension(0, 50));
        
        clockLabel = new JLabel();
        clockLabel.setFont(new Font("Consolas", Font.BOLD, 14));
        clockLabel.setForeground(TERMINAL_GREEN);
        clockLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        
        statusLabel = new JLabel("[ SYSTEM READY ]");
        statusLabel.setFont(new Font("Consolas", Font.BOLD, 14));
        statusLabel.setForeground(NEON_GREEN);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        
        bar.add(statusLabel, BorderLayout.WEST);
        bar.add(clockLabel, BorderLayout.EAST);
        
        return bar;
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        
        // Mode selection - ENCRYPTION / DECRYPTION
        JPanel modePanel = createGlowPanel(NEON_RED);
        modePanel.setLayout(new GridBagLayout());
        modePanel.setPreferredSize(new Dimension(0, 100));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel modeLabel = createNeonLabel("▼ SELECT MODE", NEON_RED);
        modePanel.add(modeLabel, gbc);
        
        gbc.gridy = 1; gbc.gridwidth = 1; gbc.weightx = 0.5;
        encryptModeBtn = createModeButton("★ ENCRYPTION", NEON_GREEN, true);
        encryptModeBtn.addActionListener(e -> setMode(true));
        modePanel.add(encryptModeBtn, gbc);
        
        gbc.gridx = 1;
        decryptModeBtn = createModeButton("★ DECRYPTION", NEON_CYAN, false);
        decryptModeBtn.addActionListener(e -> setMode(false));
        modePanel.add(decryptModeBtn, gbc);
        
        // File selector
        JPanel filePanel = createGlowPanel(NEON_PURPLE);
        filePanel.setLayout(new GridBagLayout());
        filePanel.setPreferredSize(new Dimension(0, 80));
        
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel fileLabel = createNeonLabel("▼ INPUT FILE", NEON_PURPLE);
        filePanel.add(fileLabel, gbc);
        
        gbc.gridy = 1; gbc.gridwidth = 1; gbc.weightx = 1.0;
        filePathField = createTerminalField();
        filePathField.setEditable(false);
        filePathField.setText("No file selected...");
        filePanel.add(filePathField, gbc);
        
        gbc.gridx = 1; gbc.weightx = 0;
        JButton browseBtn = createEliteButton("◉ LOAD", NEON_PURPLE);
        browseBtn.addActionListener(e -> browseFile());
        filePanel.add(browseBtn, gbc);
        
        // Key input
        JPanel keyPanel = createGlowPanel(NEON_CYAN);
        keyPanel.setLayout(new GridBagLayout());
        keyPanel.setPreferredSize(new Dimension(0, 80));
        
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel keyLabel = createNeonLabel("▼ ENCRYPTION KEY", NEON_CYAN);
        keyPanel.add(keyLabel, gbc);
        
        gbc.gridy = 1; gbc.gridwidth = 1; gbc.weightx = 1.0;
        keyField = createTerminalField();
        keyPanel.add(keyField, gbc);
        
        gbc.gridx = 1; gbc.weightx = 0;
        JButton genBtn = createEliteButton("◉ GEN", NEON_CYAN);
        genBtn.addActionListener(e -> generateKey());
        keyPanel.add(genBtn, gbc);
        
        panel.add(modePanel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(filePanel);
        panel.add(Box.createVerticalStrut(15));
        panel.add(keyPanel);
        
        return panel;
    }
    
    private JButton createModeButton(String text, Color color, boolean selected) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getBackground() == BUTTON_SELECTED) {
                    g2d.setColor(BUTTON_SELECTED);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2d.setColor(color);
                    g2d.setStroke(new BasicStroke(3));
                    g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 8, 8);
                } else if (getModel().isRollover()) {
                    g2d.setColor(BUTTON_HOVER);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2d.setColor(color);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 8, 8);
                } else {
                    g2d.setColor(BUTTON_BG);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2d.setColor(color);
                    g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                }
                
                g2d.setColor(color);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2d.drawString(getText(), x, y);
            }
        };
        btn.setFont(new Font("Consolas", Font.BOLD, 13));
        btn.setForeground(color);
        btn.setBackground(selected ? BUTTON_SELECTED : BUTTON_BG);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(0, 40));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    private void setMode(boolean encrypt) {
        isEncryptMode = encrypt;
        encryptModeBtn.setBackground(encrypt ? BUTTON_SELECTED : BUTTON_BG);
        decryptModeBtn.setBackground(encrypt ? BUTTON_BG : BUTTON_SELECTED);
        encryptModeBtn.repaint();
        decryptModeBtn.repaint();
        statusLabel.setText(encrypt ? "[ MODE: ENCRYPTION ]" : "[ MODE: DECRYPTION ]");
        statusLabel.setForeground(encrypt ? NEON_GREEN : NEON_CYAN);
        if (!fileContent.isEmpty()) processEncryption();
    }
    
    private JPanel createCenterPanel() {
        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        
        // Single output panel
        gbc.gridx = 0; gbc.gridy = 0;
        JPanel outputPanel = createOutputPanel("⚡ OUTPUT", NEON_GREEN);
        cipherOutputArea = createTerminalArea();
        JScrollPane outputScroll = createTerminalScroll(cipherOutputArea);
        outputPanel.add(outputScroll, BorderLayout.CENTER);
        center.add(outputPanel, gbc);
        
        // Key display
        gbc.gridy = 1; gbc.weighty = 0.15;
        JPanel keyPanel = createOutputPanel("⚡ ACTIVE KEY", NEON_CYAN);
        keyOutputArea = createTerminalArea();
        keyOutputArea.setForeground(NEON_CYAN);
        JScrollPane keyScroll = createTerminalScroll(keyOutputArea);
        keyPanel.add(keyScroll, BorderLayout.CENTER);
        center.add(keyPanel, gbc);
        
        return center;
    }
    
    private JPanel createBottomPanel() {
        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new GridLayout(3, 3, 8, 8));
        bottom.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        bottom.setPreferredSize(new Dimension(0, 240));
        
        String[] ciphers = {
            "MORSE CODE", "ENIGMA MACHINE", "AFFINE CIPHER",
            "BACON CIPHER", "ROT 13", "A1Z26",
            "RC 3", "RC4", "RC5"
        };
        
        Color[] colors = {NEON_GREEN, NEON_PURPLE, NEON_CYAN, NEON_RED, WARNING_ORANGE, 
                         NEON_GREEN, NEON_PURPLE, NEON_CYAN, NEON_RED};
        
        cipherButtons = new JButton[9];
        for (int i = 0; i < ciphers.length; i++) {
            final String cipher = ciphers[i];
            final Color color = colors[i];
            cipherButtons[i] = createCipherButton(ciphers[i], color);
            cipherButtons[i].addActionListener(e -> {
                selectCipher(cipher);
                processEncryption();
            });
            bottom.add(cipherButtons[i]);
        }
        
        cipherButtons[0].setBackground(BUTTON_SELECTED);
        
        return bottom;
    }
    
    private JPanel createGlowPanel(Color glowColor) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Dark background
                g2d.setColor(PANEL_BG);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                
                // Inner glow
                g2d.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), 30));
                g2d.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, 8, 8);
                
                // Border glow
                g2d.setColor(glowColor);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 10, 10);
            }
        };
        panel.setOpaque(false);
        return panel;
    }
    
    private JPanel createOutputPanel(String title, Color color) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setOpaque(false);
        panel.setBorder(createGlowBorder(color, 2));
        
        JLabel label = createNeonLabel(title, color);
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        panel.add(label, BorderLayout.NORTH);
        
        return panel;
    }
    
    private Border createGlowBorder(Color color, int thickness) {
        return BorderFactory.createCompoundBorder(
            new LineBorder(color, thickness, true),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        );
    }
    
    private JLabel createNeonLabel(String text, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Consolas", Font.BOLD, 13));
        label.setForeground(color);
        return label;
    }
    
    private JTextField createTerminalField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Consolas", Font.PLAIN, 13));
        field.setBackground(PURE_BLACK);
        field.setForeground(TERMINAL_GREEN);
        field.setCaretColor(NEON_GREEN);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(NEON_GREEN, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }
    
    private JTextArea createTerminalArea() {
        JTextArea area = new JTextArea();
        area.setFont(new Font("Consolas", Font.PLAIN, 12));
        area.setBackground(PURE_BLACK);
        area.setForeground(TERMINAL_GREEN);
        area.setCaretColor(NEON_GREEN);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        return area;
    }
    
    private JScrollPane createTerminalScroll(JTextArea area) {
        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(new LineBorder(NEON_GREEN, 1));
        scroll.getViewport().setBackground(PURE_BLACK);
        return scroll;
    }
    
    private JButton createEliteButton(String text, Color color) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(color.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(BUTTON_HOVER);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2d.setColor(color);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 8, 8);
                } else {
                    g2d.setColor(BUTTON_BG);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2d.setColor(color);
                    g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 8, 8);
                }
                
                g2d.setColor(color);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                g2d.drawString(getText(), x, y);
            }
        };
        btn.setFont(new Font("Consolas", Font.BOLD, 12));
        btn.setForeground(color);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(80, 35));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    private JButton createCipherButton(String text, Color color) {
        JButton btn = new JButton(text) {
            private float glowIntensity = 0;
            
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background
                if (getBackground() == BUTTON_SELECTED) {
                    g2d.setColor(BUTTON_SELECTED);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    
                    // Pulsing glow
                    int alpha = (int)(50 + glowIntensity * 50);
                    g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
                    g2d.fillRoundRect(3, 3, getWidth()-6, getHeight()-6, 10, 10);
                } else if (getModel().isRollover()) {
                    g2d.setColor(BUTTON_HOVER);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                } else {
                    g2d.setColor(BUTTON_BG);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                }
                
                // Border
                g2d.setColor(color);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 12, 12);
                
                // Text with glow
                g2d.setColor(color);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent()) / 2 - 2;
                
                // Text shadow
                if (getBackground() == BUTTON_SELECTED) {
                    g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
                    g2d.drawString(getText(), x+1, y+1);
                }
                
                g2d.setColor(color);
                g2d.drawString(getText(), x, y);
            }
        };
        btn.setFont(new Font("Consolas", Font.BOLD, 14));
        btn.setForeground(color);
        btn.setBackground(BUTTON_BG);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
    
    private void startAnimations() {
        // Clock update
        javax.swing.Timer clockTimer = new javax.swing.Timer(1000, e -> {
            clockLabel.setText(String.format("[ %tH:%<tM:%<tS ]", System.currentTimeMillis()));
        });
        clockTimer.start();
        
        // Title animation
        javax.swing.Timer titleTimer = new javax.swing.Timer(500, new ActionListener() {
            private boolean toggle = false;
            public void actionPerformed(ActionEvent e) {
                String title = toggle ? 
                    "▰▰▰ BLACKHAT CIPHER TERMINAL v2.0 ▰▰▰" : 
                    "▱▱▱ BLACKHAT CIPHER TERMINAL v2.0 ▱▱▱";
                setTitle(title);
                toggle = !toggle;
            }
        });
        titleTimer.start();
        
        // Status blink for selected cipher
        javax.swing.Timer statusTimer = new javax.swing.Timer(300, e -> {
            for (JButton btn : cipherButtons) {
                if (btn.getBackground() == BUTTON_SELECTED) {
                    btn.repaint();
                }
            }
        });
        statusTimer.start();
    }
    
    // Animated background panel
    class AnimatedBackgroundPanel extends JPanel {
        private float offset = 0;
        
        public AnimatedBackgroundPanel() {
            setOpaque(true);
            javax.swing.Timer timer = new javax.swing.Timer(50, e -> {
                offset += 0.5f;
                if (offset > 20) offset = 0;
                repaint();
            });
            timer.start();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            
            // Pure black background
            g2d.setColor(DARK_BG);
            g2d.fillRect(0, 0, getWidth(), getHeight());
            
            // Animated grid lines
            g2d.setColor(new Color(0, 255, 65, 20));
            g2d.setStroke(new BasicStroke(1));
            
            // Vertical lines
            for (int x = 0; x < getWidth(); x += 40) {
                g2d.drawLine(x + (int)offset, 0, x + (int)offset, getHeight());
            }
            
            // Horizontal lines
            for (int y = 0; y < getHeight(); y += 40) {
                g2d.drawLine(0, y + (int)offset, getWidth(), y + (int)offset);
            }
            
            // Random glitch lines
            Random rand = new Random(System.currentTimeMillis() / 1000);
            g2d.setColor(new Color(0, 255, 255, 30));
            for (int i = 0; i < 5; i++) {
                int y = rand.nextInt(getHeight());
                g2d.drawLine(0, y, getWidth(), y);
            }
        }
    }
    
    private void selectCipher(String cipher) {
        currentCipher = cipher;
        for (JButton btn : cipherButtons) {
            btn.setBackground(BUTTON_BG);
        }
        for (JButton btn : cipherButtons) {
            if (btn.getText().equals(cipher)) {
                btn.setBackground(BUTTON_SELECTED);
                break;
            }
        }
        statusLabel.setText("[ CIPHER: " + cipher + " ]");
    }
    
    private void browseFile() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("▶ SELECT TARGET FILE");
        fc.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            filePathField.setText(file.getAbsolutePath());
            loadFile(file);
            statusLabel.setText("[ FILE LOADED: " + file.getName() + " ]");
            statusLabel.setForeground(NEON_GREEN);
            processEncryption();
        }
    }
    
    private void loadFile(File file) {
        try {
            BufferedReader r = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) sb.append(line).append("\n");
            r.close();
            fileContent = sb.toString().trim();
        } catch (IOException e) {
            statusLabel.setText("[ ERROR: FILE READ FAILED ]");
            statusLabel.setForeground(NEON_RED);
            fileContent = "";
        }
    }
    
    private void generateKey() {
        String key = "";
        if (currentCipher.equals("AFFINE CIPHER")) key = "5,8";
        else if (currentCipher.contains("RC")) key = genRandKey(16);
        else if (currentCipher.equals("ENIGMA MACHINE")) key = String.valueOf(new Random().nextInt(26)+1);
        else key = "KEY-" + System.currentTimeMillis()%10000;
        keyField.setText(key);
        keyOutputArea.setText(key);
        statusLabel.setText("[ KEY GENERATED ]");
        statusLabel.setForeground(NEON_CYAN);
        processEncryption();
    }
    
    private String genRandKey(int n) {
        String c = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder k = new StringBuilder();
        Random r = new Random();
        for (int i=0; i<n; i++) k.append(c.charAt(r.nextInt(c.length())));
        return k.toString();
    }
    
    private void processEncryption() {
        if (fileContent.isEmpty()) {
            cipherOutputArea.setText("[!] NO INPUT FILE LOADED\n[!] CLICK 'LOAD' TO SELECT A FILE");
            statusLabel.setText("[ AWAITING INPUT ]");
            statusLabel.setForeground(WARNING_ORANGE);
            return;
        }
        
        String key = keyField.getText();
        keyOutputArea.setText(key.isEmpty() ? "[NONE - USING DEFAULT]" : key);
        
        try {
            String output = "";
            
            if (isEncryptMode) {
                // ENCRYPTION MODE - Encrypt the file content
                statusLabel.setText("[ ENCRYPTING... ]");
                statusLabel.setForeground(WARNING_ORANGE);
                
                switch (currentCipher) {
                    case "MORSE CODE": output = toMorse(fileContent); break;
                    case "ROT 13": output = rot13(fileContent); break;
                    case "A1Z26": output = a1z26(fileContent); break;
                    case "AFFINE CIPHER": output = affine(fileContent, key, true); break;
                    case "BACON CIPHER": output = bacon(fileContent); break;
                    case "RC 3": output = rc3(fileContent, key); break;
                    case "RC4": output = rc4(fileContent, key); break;
                    case "RC5": output = rc5(fileContent, key); break;
                    case "ENIGMA MACHINE": output = enigma(fileContent, key, true); break;
                }
                
                cipherOutputArea.setText(output);
                cipherOutputArea.setForeground(NEON_GREEN);
                statusLabel.setText("[ ENCRYPTION COMPLETE ]");
                statusLabel.setForeground(NEON_GREEN);
                
            } else {
                // DECRYPTION MODE - Decrypt the file content
                statusLabel.setText("[ DECRYPTING... ]");
                statusLabel.setForeground(WARNING_ORANGE);
                
                switch (currentCipher) {
                    case "MORSE CODE": output = fromMorse(fileContent); break;
                    case "ROT 13": output = rot13(fileContent); break;
                    case "A1Z26": output = fromA1Z26(fileContent); break;
                    case "AFFINE CIPHER": output = affine(fileContent, key, false); break;
                    case "BACON CIPHER": output = fromBacon(fileContent); break;
                    case "RC 3": output = rc3Decrypt(fileContent, key); break;
                    case "RC4": output = rc4Decrypt(fileContent, key); break;
                    case "RC5": output = rc5Decrypt(fileContent, key); break;
                    case "ENIGMA MACHINE": output = enigma(fileContent, key, false); break;
                }
                
                cipherOutputArea.setText(output);
                cipherOutputArea.setForeground(WARNING_ORANGE);
                statusLabel.setText("[ DECRYPTION COMPLETE ]");
                statusLabel.setForeground(NEON_CYAN);
            }
            
        } catch (Exception e) {
            cipherOutputArea.setText("[!] OPERATION FAILED\n[!] ERROR: " + e.getMessage());
            statusLabel.setText("[ ERROR: OPERATION FAILED ]");
            statusLabel.setForeground(NEON_RED);
        }
    }
    
    // [All cipher implementations remain the same as previous version]
    private static final Map<Character,String> M = new HashMap<>();
    private static final Map<String,Character> MR = new HashMap<>();
    static {
        M.put('A',".-");M.put('B',"-...");M.put('C',"-.-.");M.put('D',"-..");M.put('E',".");M.put('F',"..-.");
        M.put('G',"--.");M.put('H',"....");M.put('I',"..");M.put('J',".---");M.put('K',"-.-");M.put('L',".-..");
        M.put('M',"--");M.put('N',"-.");M.put('O',"---");M.put('P',".--.");M.put('Q',"--.-");M.put('R',".-.");
        M.put('S',"...");M.put('T',"-");M.put('U',"..-");M.put('V',"...-");M.put('W',".--");M.put('X',"-..-");
        M.put('Y',"-.--");M.put('Z',"--..");M.put('0',"-----");M.put('1',".----");M.put('2',"..---");
        M.put('3',"...--");M.put('4',"....-");M.put('5',".....");M.put('6',"-....");M.put('7',"--...");
        M.put('8',"---..");M.put('9',"----.");M.put(' ',"/");
        M.forEach((k,v) -> MR.put(v,k));
    }
    private String toMorse(String t) {
        StringBuilder sb = new StringBuilder();
        for (char c : t.toUpperCase().toCharArray()) sb.append(M.getOrDefault(c,"")).append(" ");
        return sb.toString().trim();
    }
    private String fromMorse(String t) {
        StringBuilder sb = new StringBuilder();
        for (String code : t.split(" ")) sb.append(MR.getOrDefault(code,'?'));
        return sb.toString();
    }
    private String rot13(String t) {
        StringBuilder sb = new StringBuilder();
        for (char c : t.toCharArray()) {
            if (c>='a'&&c<='z') sb.append((char)('a'+(c-'a'+13)%26));
            else if (c>='A'&&c<='Z') sb.append((char)('A'+(c-'A'+13)%26));
            else sb.append(c);
        }
        return sb.toString();
    }
    private String a1z26(String t) {
        StringBuilder sb = new StringBuilder();
        for (char c : t.toUpperCase().toCharArray()) {
            if (c>='A'&&c<='Z') sb.append(c-'A'+1).append("-");
            else if (c==' ') sb.append(" ");
        }
        return sb.toString();
    }
    private String fromA1Z26(String t) {
        StringBuilder sb = new StringBuilder();
        for (String num : t.split("-")) {
            num = num.trim();
            if (!num.isEmpty() && !num.equals(" ")) {
                try {
                    int n = Integer.parseInt(num);
                    if (n>=1 && n<=26) sb.append((char)('A'+n-1));
                } catch (Exception e) { sb.append(" "); }
            }
        }
        return sb.toString();
    }
    private String affine(String t, String k, boolean enc) {
        int a=5, b=8;
        if (!k.isEmpty()) {
            try {
                String[] p = k.split(",");
                a = Integer.parseInt(p[0].trim());
                b = p.length>1 ? Integer.parseInt(p[1].trim()) : 8;
            } catch (Exception e) {}
        }
        int aInv = modInv(a, 26);
        StringBuilder sb = new StringBuilder();
        for (char c : t.toUpperCase().toCharArray()) {
            if (c>='A'&&c<='Z') {
                int x = c-'A';
                int y = enc ? (a*x+b)%26 : (aInv*(x-b+26))%26;
                sb.append((char)('A'+y));
            } else sb.append(c);
        }
        return sb.toString();
    }
    private int modInv(int a, int m) {
        for (int x=1; x<m; x++) if ((a*x)%m==1) return x;
        return 1;
    }
    private String bacon(String t) {
        String[] cd = {"AAAAA","AAAAB","AAABA","AAABB","AABAA","AABAB","AABBA","AABBB","ABAAA","ABAAB",
                      "ABABA","ABABB","ABBAA","ABBAB","ABBBA","ABBBB","BAAAA","BAAAB","BAABA","BAABB",
                      "BABAA","BABAB","BABBA","BABBB","BBAAA","BBAAB"};
        StringBuilder sb = new StringBuilder();
        for (char c : t.toUpperCase().toCharArray()) {
            if (c>='A'&&c<='Z') sb.append(cd[c-'A']).append(" ");
        }
        return sb.toString().trim();
    }
    private String fromBacon(String t) {
        String[] cd = {"AAAAA","AAAAB","AAABA","AAABB","AABAA","AABAB","AABBA","AABBB","ABAAA","ABAAB",
                      "ABABA","ABABB","ABBAA","ABBAB","ABBBA","ABBBB","BAAAA","BAAAB","BAABA","BAABB",
                      "BABAA","BABAB","BABBA","BABBB","BBAAA","BBAAB"};
        Map<String,Character> mp = new HashMap<>();
        for (int i=0; i<26; i++) mp.put(cd[i], (char)('A'+i));
        StringBuilder sb = new StringBuilder();
        for (String code : t.split(" ")) sb.append(mp.getOrDefault(code,'?'));
        return sb.toString();
    }
    private String rc3(String t, String k) {
        if (k.isEmpty()) k = "secret";
        return Base64.getEncoder().encodeToString(rc3Cipher(t.getBytes(), k.getBytes()));
    }
    private String rc3Decrypt(String t, String k) {
        if (k.isEmpty()) k = "secret";
        byte[] dec = Base64.getDecoder().decode(t);
        return new String(rc3Cipher(dec, k.getBytes()));
    }
    private byte[] rc3Cipher(byte[] d, byte[] k) {
        int[] S = new int[256];
        for (int i=0; i<256; i++) S[i]=i;
        for (int rnd=0; rnd<3; rnd++) {
            int j=0;
            for (int i=0; i<256; i++) {
                j = (j+S[i]+(k[(i+rnd)%k.length]&0xFF)+rnd)%256;
                int tmp=S[i]; S[i]=S[j]; S[j]=tmp;
            }
        }
        byte[] res = new byte[d.length];
        int i=0, j=0;
        for (int n=0; n<d.length; n++) {
            i=(i+1)%256;
            j=(j+S[i])%256;
            int tmp=S[i]; S[i]=S[j]; S[j]=tmp;
            res[n] = (byte)(d[n]^S[(S[i]+S[j])%256]);
        }
        return res;
    }
    private String rc4(String t, String k) {
        if (k.isEmpty()) k = "secret";
        return Base64.getEncoder().encodeToString(rc4Cipher(t.getBytes(), k.getBytes()));
    }
    private String rc4Decrypt(String t, String k) {
        if (k.isEmpty()) k = "secret";
        byte[] dec = Base64.getDecoder().decode(t);
        return new String(rc4Cipher(dec, k.getBytes()));
    }
    private byte[] rc4Cipher(byte[] d, byte[] k) {
        int[] S = new int[256];
        for (int i=0; i<256; i++) S[i]=i;
        int j=0;
        for (int i=0; i<256; i++) {
            j = (j+S[i]+(k[i%k.length]&0xFF))%256;
            int tmp=S[i]; S[i]=S[j]; S[j]=tmp;
        }
        byte[] res = new byte[d.length];
        int i=0, kt=0;
        for (int n=0; n<d.length; n++) {
            i=(i+1)%256;
            kt=(kt+S[i])%256;
            int tmp=S[i]; S[i]=S[kt]; S[kt]=tmp;
            res[n] = (byte)(d[n]^S[(S[i]+S[kt])%256]);
        }
        return res;
    }
    private String rc5(String t, String k) {
        if (k.isEmpty()) k = "secret";
        return Base64.getEncoder().encodeToString(rc5Cipher(t.getBytes(), k.getBytes(), true));
    }
    private String rc5Decrypt(String t, String k) {
        if (k.isEmpty()) k = "secret";
        byte[] dec = Base64.getDecoder().decode(t);
        return new String(rc5Cipher(dec, k.getBytes(), false)).replaceAll("\0", "");
    }
    private byte[] rc5Cipher(byte[] d, byte[] k, boolean enc) {
        int r=12;
        int[] S = rc5KeySched(k, r);
        int pLen = ((d.length+7)/8)*8;
        byte[] pad = new byte[pLen];
        System.arraycopy(d, 0, pad, 0, d.length);
        byte[] res = new byte[pLen];
        for (int i=0; i<pLen; i+=8) {
            int A = b2i(pad,i);
            int B = b2i(pad,i+4);
            if (enc) {
                A += S[0]; B += S[1];
                for (int j=1; j<=r; j++) {
                    A = rotL(A^B, B) + S[2*j];
                    B = rotL(B^A, A) + S[2*j+1];
                }
            } else {
                for (int j=r; j>=1; j--) {
                    B = rotR(B-S[2*j+1], A) ^ A;
                    A = rotR(A-S[2*j], B) ^ B;
                }
                B -= S[1]; A -= S[0];
            }
            i2b(A, res, i);
            i2b(B, res, i+4);
        }
        return res;
    }
    private int[] rc5KeySched(byte[] k, int r) {
        int c = Math.max((k.length+3)/4, 1);
        int[] L = new int[c];
        for (int i=k.length-1; i>=0; i--) L[i/4] = (L[i/4]<<8)+(k[i]&0xFF);
        int t = 2*(r+1);
        int[] S = new int[t];
        S[0] = 0xB7E15163;
        for (int i=1; i<t; i++) S[i] = S[i-1]+0x9E3779B9;
        int A=0, B=0, i=0, j=0;
        int v = 3*Math.max(t,c);
        for (int n=0; n<v; n++) {
            A = S[i] = rotL(S[i]+A+B, 3);
            B = L[j] = rotL(L[j]+A+B, A+B);
            i = (i+1)%t;
            j = (j+1)%c;
        }
        return S;
    }
    private int rotL(int v, int c) {
        c &= 0x1F;
        return (v<<c)|(v>>>(32-c));
    }
    private int rotR(int v, int c) {
        c &= 0x1F;
        return (v>>>c)|(v<<(32-c));
    }
    private int b2i(byte[] d, int o) {
        return (d[o]&0xFF)|((d[o+1]&0xFF)<<8)|((d[o+2]&0xFF)<<16)|((d[o+3]&0xFF)<<24);
    }
    private void i2b(int v, byte[] d, int o) {
        d[o]=(byte)(v&0xFF);
        d[o+1]=(byte)((v>>>8)&0xFF);
        d[o+2]=(byte)((v>>>16)&0xFF);
        d[o+3]=(byte)((v>>>24)&0xFF);
    }
    private String enigma(String t, String k, boolean enc) {
        int sh = 3;
        if (!k.isEmpty()) {
            try { sh = Math.abs(Integer.parseInt(k)%26); }
            catch (Exception e) { sh = Math.abs(k.hashCode()%26); }
        }
        if (!enc) sh = 26 - sh;
        StringBuilder sb = new StringBuilder();
        for (char c : t.toUpperCase().toCharArray()) {
            if (c>='A'&&c<='Z') sb.append((char)('A'+(c-'A'+sh)%26));
            else sb.append(c);
        }
        return sb.toString();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HackerCryptoUI());
    }
}
