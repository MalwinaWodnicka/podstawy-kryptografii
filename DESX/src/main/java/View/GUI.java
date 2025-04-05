package View;

import Model.DESX;
import Model.Functions;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Random;

public class GUI extends JFrame {

    private JTextArea plainTextArea;
    private JTextArea cipherTextArea;
    private JTextField keyInternalField;
    private JTextField keyDesField;
    private JTextField keyExternalField;

    public GUI() {
        setTitle("DESX Encrypt/Decrypt");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        initComponents();
    }

    private void initComponents() {
        JPanel topPanel = new JPanel(new GridLayout(3, 2, 5, 5));

        // Klucze DESX
        topPanel.add(new JLabel("Internal Key (8 bytes):"));
        keyInternalField = new JTextField(16);
        topPanel.add(keyInternalField);

        topPanel.add(new JLabel("DES Key (8 bytes):"));
        keyDesField = new JTextField(16);
        topPanel.add(keyDesField);

        topPanel.add(new JLabel("External Key (8 bytes):"));
        keyExternalField = new JTextField(16);
        topPanel.add(keyExternalField);

        JButton generateKeysButton = new JButton("Generate All Keys");
        JButton saveKeysButton = new JButton("Save Keys");
        JButton loadKeysButton = new JButton("Load Keys");

        generateKeysButton.setToolTipText("Generate new random keys for all fields");

        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        plainTextArea = new JTextArea(8, 40);
        plainTextArea.setBorder(BorderFactory.createTitledBorder("Plain Text"));
        cipherTextArea = new JTextArea(8, 40);
        cipherTextArea.setBorder(BorderFactory.createTitledBorder("Cipher Text (hex)"));
        centerPanel.add(new JScrollPane(plainTextArea));
        centerPanel.add(new JScrollPane(cipherTextArea));

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton encryptButton = new JButton("Encrypt");
        JButton decryptButton = new JButton("Decrypt");
        JButton encryptFileButton = new JButton("Encrypt File");
        JButton decryptFileButton = new JButton("Decrypt File");
        bottomPanel.add(generateKeysButton);
        bottomPanel.add(encryptButton);
        bottomPanel.add(decryptButton);
        bottomPanel.add(encryptFileButton);
        bottomPanel.add(decryptFileButton);

        generateKeysButton.addActionListener(e -> {
            keyInternalField.setText(generateRandomKey());
            keyDesField.setText(generateRandomKey());
            keyExternalField.setText(generateRandomKey());
        });

        encryptButton.addActionListener(e -> {
            try {
                String plainText = plainTextArea.getText();
                if (plainText.isEmpty()) {
                    showError("Plain text cannot be empty!");
                    return;
                }

                byte[] keyInternal = getKeyFromField(keyInternalField);
                byte[] keyDes = getKeyFromField(keyDesField);
                byte[] keyExternal = getKeyFromField(keyExternalField);

                // Dodaj dopełnienie danych przed szyfrowaniem
                byte[] paddedData = Functions.padData(plainText.getBytes());

                DESX desx = new DESX();
                byte[] encrypted = desx.encrypt(paddedData, keyInternal, keyDes, keyExternal);
                cipherTextArea.setText(byteArrayToHexString(encrypted));
            } catch (Exception ex) {
                showError("Error during encryption: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        decryptButton.addActionListener(e -> {
            try {
                String cipherTextHex = cipherTextArea.getText().trim();
                if (cipherTextHex.isEmpty()) {
                    showError("Cipher text cannot be empty!");
                    return;
                }

                byte[] keyInternal = getKeyFromField(keyInternalField);
                byte[] keyDes = getKeyFromField(keyDesField);
                byte[] keyExternal = getKeyFromField(keyExternalField);

                byte[] cipherBytes = hexStringToByteArray(cipherTextHex);
                DESX desx = new DESX();
                byte[] decrypted = desx.decrypt(cipherBytes, keyInternal, keyDes, keyExternal);

                // Usuń dopełnienie po odszyfrowaniu
                byte[] unpadded = Functions.removePadding(decrypted);
                plainTextArea.setText(new String(unpadded));
            } catch (Exception ex) {
                showError("Error during decryption: " + ex.getMessage());
                ex.printStackTrace();
            }
        });


        decryptButton.addActionListener(e -> {
            try {
                String cipherTextHex = cipherTextArea.getText().trim();
                if (cipherTextHex.isEmpty()) {
                    showError("Cipher text cannot be empty!");
                    return;
                }

                byte[] keyInternal = getKeyFromField(keyInternalField);
                byte[] keyDes = getKeyFromField(keyDesField);
                byte[] keyExternal = getKeyFromField(keyExternalField);

                byte[] cipherBytes = hexStringToByteArray(cipherTextHex);
                DESX desx = new DESX();
                byte[] decrypted = desx.decrypt(cipherBytes, keyInternal, keyDes, keyExternal);
                plainTextArea.setText(new String(decrypted));
            } catch (Exception ex) {
                showError("Error during decryption: " + ex.getMessage());
            }
        });

        encryptFileButton.addActionListener(e -> handleFileOperation(true));
        decryptFileButton.addActionListener(e -> handleFileOperation(false));

        setLayout(new BorderLayout(5, 5));
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private byte[] getKeyFromField(JTextField field) throws IllegalArgumentException {
        String text = field.getText().trim();
        if (text.length() != 16) {
            throw new IllegalArgumentException("Key must be 16 hex characters (8 bytes)");
        }
        try {
            return hexStringToByteArray(text);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid hex key format");
        }
    }

    private String generateRandomKey() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(16);
        for (int i = 0; i < 16; i++) {
            sb.append(Integer.toHexString(random.nextInt(16)));
        }
        return sb.toString().toUpperCase();
    }

    private void handleFileOperation(boolean encrypt) {
        try {
            byte[] keyInternal = getKeyFromField(keyInternalField);
            byte[] keyDes = getKeyFromField(keyDesField);
            byte[] keyExternal = getKeyFromField(keyExternalField);

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle(encrypt ? "Select File to Encrypt" : "Select File to Decrypt");
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File inputFile = fileChooser.getSelectedFile();
                byte[] fileData = Files.readAllBytes(inputFile.toPath());

                DESX desx = new DESX();
                byte[] resultData;
                if (encrypt) {
                    resultData = desx.encrypt(fileData, keyInternal, keyDes, keyExternal);
                } else {
                    resultData = desx.decrypt(fileData, keyInternal, keyDes, keyExternal);
                }

                JFileChooser saveChooser = new JFileChooser();
                saveChooser.setDialogTitle(encrypt ? "Save Encrypted File" : "Save Decrypted File");
                if (saveChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    File outputFile = saveChooser.getSelectedFile();
                    Files.write(outputFile.toPath(), resultData);
                    showInfo("File " + (encrypt ? "encrypted" : "decrypted") + " successfully");
                }
            }
        } catch (Exception ex) {
            showError("Error during file operation: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    private byte[] hexStringToByteArray(String s) {
        if (s.length() % 2 != 0) {
            throw new IllegalArgumentException("Hex string must have even length");
        }
        byte[] data = new byte[s.length() / 2];
        for (int i = 0; i < s.length(); i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GUI gui = new GUI();
            gui.setVisible(true);
        });
    }
}