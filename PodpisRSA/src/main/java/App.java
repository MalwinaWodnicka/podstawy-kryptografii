import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;

public class App extends JFrame {
    private int bitSize = 512;
    private RSA rsa = new RSA(bitSize);
    private File file = null;

    private JTextField nPrivateField;
    private JTextField nPublicField;
    private JTextField ePrivateField;
    private JTextField dPublicField;
    private JTextField signatureField;
    private JLabel signStatusLabel;
    private JLabel fileStatusLabel;
    private JTextArea messageArea;
    private JRadioButton fileSourceRadio;
    private JRadioButton textSourceRadio;

    public App() {
        setTitle("RSA Signature Application");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create menu bar
        JMenuBar menuBar = new JMenuBar();
        JMenu bitSizeMenu = new JMenu("Bit Size");

        JMenuItem bit128 = new JMenuItem("128 bits");
        bit128.addActionListener(e -> setBitSize(128));
        JMenuItem bit256 = new JMenuItem("256 bits");
        bit256.addActionListener(e -> setBitSize(256));
        JMenuItem bit512 = new JMenuItem("512 bits");
        bit512.addActionListener(e -> setBitSize(512));

        bitSizeMenu.add(bit128);
        bitSizeMenu.add(bit256);
        bitSizeMenu.add(bit512);
        menuBar.add(bitSizeMenu);
        setJMenuBar(menuBar);

        // Create main panel
        JPanel mainPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Key fields
        mainPanel.add(new JLabel("Private Key (n):"));
        nPrivateField = new JTextField();
        mainPanel.add(nPrivateField);

        mainPanel.add(new JLabel("Private Key (e):"));
        ePrivateField = new JTextField();
        mainPanel.add(ePrivateField);

        mainPanel.add(new JLabel("Public Key (n):"));
        nPublicField = new JTextField();
        mainPanel.add(nPublicField);

        mainPanel.add(new JLabel("Public Key (d):"));
        dPublicField = new JTextField();
        mainPanel.add(dPublicField);

        // Signature field
        mainPanel.add(new JLabel("Signature:"));
        signatureField = new JTextField();
        mainPanel.add(signatureField);

        // Status labels
        mainPanel.add(new JLabel("File Status:"));
        fileStatusLabel = new JLabel("No file loaded");
        mainPanel.add(fileStatusLabel);

        mainPanel.add(new JLabel("Signature Status:"));
        signStatusLabel = new JLabel("Not verified");
        mainPanel.add(signStatusLabel);

        //Source Panel

        JPanel sourcePanel = new JPanel(new FlowLayout());
        fileSourceRadio = new JRadioButton("From File", true);
        textSourceRadio = new JRadioButton("From Text");
        ButtonGroup sourceGroup = new ButtonGroup();
        sourceGroup.add(fileSourceRadio);
        sourceGroup.add(textSourceRadio);
        sourcePanel.add(new JLabel("Data source:"));
        sourcePanel.add(fileSourceRadio);
        sourcePanel.add(textSourceRadio);

        mainPanel.add(new JLabel("Message:"));
        messageArea = new JTextArea(5, 20);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        mainPanel.add(scrollPane);

        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 4, 5, 5));

        JButton generateKeysBtn = new JButton("Generate Keys");
        generateKeysBtn.addActionListener(e -> generateKeys());
        buttonPanel.add(generateKeysBtn);

        JButton loadFileBtn = new JButton("Load File");
        loadFileBtn.addActionListener(e -> loadFile());
        buttonPanel.add(loadFileBtn);

        JButton generateSignBtn = new JButton("Generate Signature");
        generateSignBtn.addActionListener(e -> {
            try {
                generateSignature();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error generating signature: " + ex.getMessage());
            }
        });
        buttonPanel.add(generateSignBtn);

        JButton verifySignBtn = new JButton("Verify Signature");
        verifySignBtn.addActionListener(e -> {
            try {
                verifySignature();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error verifying signature: " + ex.getMessage());
            }
        });
        buttonPanel.add(verifySignBtn);

        JButton saveSignBtn = new JButton("Save Signature");
        saveSignBtn.addActionListener(e -> saveSignature());
        buttonPanel.add(saveSignBtn);

        JButton loadSignBtn = new JButton("Load Signature");
        loadSignBtn.addActionListener(e -> {
            try {
                loadSignature();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error loading signature: " + ex.getMessage());
            }
        });
        buttonPanel.add(loadSignBtn);

        JButton saveKeysBtn = new JButton("Save Keys");
        saveKeysBtn.addActionListener(e -> saveKeys());
        buttonPanel.add(saveKeysBtn);

        JButton loadKeysBtn = new JButton("Load Keys");
        loadKeysBtn.addActionListener(e -> {
            try {
                loadKeys();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error loading keys: " + ex.getMessage());
            }
        });
        buttonPanel.add(loadKeysBtn);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        add(sourcePanel, BorderLayout.NORTH);
    }

    private void setBitSize(int size) {
        this.bitSize = size;
        this.rsa = new RSA(bitSize);
        JOptionPane.showMessageDialog(this, "Bit size set to " + size);
    }

    private void generateKeys() {
        rsa.generateKeys();
        nPrivateField.setText(rsa.get_n().toString());
        ePrivateField.setText(rsa.get_e().toString());
        nPublicField.setText(rsa.get_n().toString());
        dPublicField.setText(rsa.get_d().toString());
    }

    private void loadFile() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            file = fileChooser.getSelectedFile();
            fileStatusLabel.setText(file.getName());
        }
    }

    private void generateSignature() throws Exception {
        BigInteger fileBigInteger;

        if (fileSourceRadio.isSelected()) {
            if (file == null) {
                JOptionPane.showMessageDialog(this, "Please load a file first");
                return;
            }
            fileBigInteger = hashFile(file);
        } else {
            String text = messageArea.getText();
            if (text.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter some text");
                return;
            }
            fileBigInteger = hashText(text);
        }

        BigInteger e = new BigInteger(ePrivateField.getText());
        BigInteger n = new BigInteger(nPrivateField.getText());
        BigInteger cypher = rsa.CreateCipher(fileBigInteger, e, n);
        signatureField.setText(cypher.toString());
    }

    private void verifySignature() throws Exception {
        BigInteger fileBigInteger;

        if (fileSourceRadio.isSelected()) {
            if (file == null) {
                JOptionPane.showMessageDialog(this, "Please load a file first");
                return;
            }
            fileBigInteger = hashFile(file);
        } else {
            String text = messageArea.getText();
            if (text.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter some text");
                return;
            }
            fileBigInteger = hashText(text);
        }

        BigInteger d = new BigInteger(dPublicField.getText());
        BigInteger n = new BigInteger(nPublicField.getText());
        BigInteger cypher = new BigInteger(signatureField.getText());
        BigInteger decrypted = rsa.Decrypt(cypher, d, n);

        if (fileBigInteger.equals(decrypted)) {
            signStatusLabel.setText("Signature valid");
        } else {
            signStatusLabel.setText("Signature invalid");
        }
    }

    private BigInteger hashFile(File file) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] fileBytes = Files.readAllBytes(file.toPath());
        byte[] hash = md.digest(fileBytes);
        return new BigInteger(1, hash);
    }

    private BigInteger hashText(String text) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] textBytes = text.getBytes("UTF-8");
        byte[] hash = md.digest(textBytes);
        return new BigInteger(1, hash);
    }


    private void saveSignature() {
        if (signatureField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No signature to save");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Signature");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text files (*.txt)", "txt"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (PrintWriter out = new PrintWriter(fileToSave)) {
                out.println(signatureField.getText());
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage());
            }
        }
    }

    private void loadSignature() throws IOException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Signature");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Text files (*.txt)", "txt"));

        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String content = new String(Files.readAllBytes(selectedFile.toPath()));
            signatureField.setText(content);
        }
    }

    private void saveKeys() {
        if (nPrivateField.getText().isEmpty() || ePrivateField.getText().isEmpty() ||
                nPublicField.getText().isEmpty() || dPublicField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "No keys to save");
            return;
        }

        // Save private key
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Private Key");
        int returnValue = fileChooser.showSaveDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File privateKeyFile = fileChooser.getSelectedFile();
            try (PrintWriter out = new PrintWriter(privateKeyFile)) {
                out.println(nPrivateField.getText());
                out.println(ePrivateField.getText());
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(this, "Error saving private key: " + e.getMessage());
            }
        }

        // Save public key
        fileChooser.setDialogTitle("Save Public Key");
        returnValue = fileChooser.showSaveDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File publicKeyFile = fileChooser.getSelectedFile();
            try (PrintWriter out = new PrintWriter(publicKeyFile)) {
                out.println(nPublicField.getText());
                out.println(dPublicField.getText());
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(this, "Error saving public key: " + e.getMessage());
            }
        }
    }

    private void loadKeys() throws IOException {
        // Load private key
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Private Key");
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File privateKeyFile = fileChooser.getSelectedFile();
            String[] lines = new String(Files.readAllBytes(privateKeyFile.toPath())).split("\n");
            nPrivateField.setText(lines[0]);
            ePrivateField.setText(lines[1]);
        }

        // Load public key
        fileChooser.setDialogTitle("Load Public Key");
        returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File publicKeyFile = fileChooser.getSelectedFile();
            String[] lines = new String(Files.readAllBytes(publicKeyFile.toPath())).split("\n");
            nPublicField.setText(lines[0]);
            dPublicField.setText(lines[1]);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            App app = new App();
            app.setVisible(true);
        });
    }
}