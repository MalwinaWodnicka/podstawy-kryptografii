package View;

import Model.DESX;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.math.BigInteger;
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

        // Akcja dla przycisku generowania kluczy
        generateKeysButton.addActionListener(e -> {
            keyInternalField.setText(generateRandomHexKey());
            keyDesField.setText(generateRandomHexKey());
            keyExternalField.setText(generateRandomHexKey());
            showInfo("New random keys generated successfully");
        });

        saveKeysButton.addActionListener(e -> saveKeysToFile());
        loadKeysButton.addActionListener(e -> loadKeysFromFile());

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
                byte[] paddedData = padData(plainText.getBytes());

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
                byte[] unpadded = removePadding(decrypted);
                plainTextArea.setText(new String(unpadded));
            } catch (Exception ex) {
                showError("Error during decryption: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        encryptFileButton.addActionListener(e -> handleFileOperation(true));
        decryptFileButton.addActionListener(e -> handleFileOperation(false));

        setLayout(new BorderLayout(5, 5));
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private String generateRandomHexKey() {
        // Generujemy 64-bitową liczbę pierwszą
        BigInteger prime = generate64BitPrime();
        // Konwertujemy do hex (16 znaków, bo 64 bity = 8 bajtów = 16 znaków hex)
        String hex = prime.toString(16).toUpperCase();

        // Upewniamy się, że ma dokładnie 16 znaków (może mieć mniej jeśli pierwsze cyfry są zerami)
        while (hex.length() < 16) {
            hex = "0" + hex;
        }

        return hex.substring(0, 16); // Na wypadek gdyby była dłuższa
    }

    private BigInteger generate64BitPrime() {
        Random rand = new Random();
        BigInteger prime;

        // Generujemy liczby aż znajdziemy pierwszą
        do {
            // Generujemy 64-bitową liczbę (długość w bitach, certainty=10 dla testu Millera-Rabina)
            prime = new BigInteger(64, 10, rand);
        } while (!isPrime(prime)); // Dodatkowe sprawdzenie (choć BigInteger z certainty=10 już powinno być pierwsze)

        return prime;
    }

    private boolean isPrime(BigInteger n) {
        // Implementacja testu Millera-Rabina
        if (n.compareTo(BigInteger.ONE) <= 0) {
            return false;
        }
        if (n.compareTo(BigInteger.valueOf(3)) <= 0) {
            return true;
        }
        if (n.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            return false;
        }

        // Zapisz n-1 jako d*2^s
        BigInteger d = n.subtract(BigInteger.ONE);
        int s = 0;
        while (d.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            d = d.divide(BigInteger.TWO);
            s++;
        }

        // Testujemy dla kilku podstaw (a) - tutaj 5 iteracji dla pewności
        for (int i = 0; i < 5; i++) {
            BigInteger a = randomBigInteger(BigInteger.TWO, n.subtract(BigInteger.TWO));
            BigInteger x = a.modPow(d, n);

            if (x.equals(BigInteger.ONE) || x.equals(n.subtract(BigInteger.ONE))) {
                continue;
            }

            boolean composite = true;
            for (int j = 0; j < s - 1; j++) {
                x = x.modPow(BigInteger.TWO, n);
                if (x.equals(n.subtract(BigInteger.ONE))) {
                    composite = false;
                    break;
                }
            }

            if (composite) {
                return false;
            }
        }

        return true;
    }

    private BigInteger randomBigInteger(BigInteger min, BigInteger max) {
        Random rand = new Random();
        BigInteger range = max.subtract(min);
        int length = max.bitLength();
        BigInteger result;

        do {
            result = new BigInteger(length, rand);
        } while (result.compareTo(range) >= 0);

        return result.add(min);
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

    private void saveKeysToFile() {
        String keys = String.join("|",
                keyInternalField.getText(),
                keyDesField.getText(),
                keyExternalField.getText()
        );

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Keys File");
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(keys);
                showInfo("Keys saved successfully to: " + file.getAbsolutePath());
            } catch (IOException e) {
                showError("Error saving keys: " + e.getMessage());
            }
        }
    }

    private byte[] padData(byte[] data) {
        int padLength = 8 - (data.length % 8);
        byte[] padded = new byte[data.length + padLength];
        System.arraycopy(data, 0, padded, 0, data.length);
        Arrays.fill(padded, data.length, padded.length, (byte) padLength);
        return padded;
    }

    private byte[] removePadding(byte[] data) {
        if (data.length == 0) return data;
        int padLength = data[data.length - 1] & 0xFF;
        if (padLength > 8) return data; // Nieprawidłowe dopełnienie
        return Arrays.copyOf(data, data.length - padLength);
    }

    private void loadKeysFromFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Keys File");
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String[] keys = reader.readLine().split("\\|");
                if (keys.length == 3) {
                    keyInternalField.setText(keys[0]);
                    keyDesField.setText(keys[1]);
                    keyExternalField.setText(keys[2]);
                    showInfo("Keys loaded successfully");
                } else {
                    showError("Invalid keys file format");
                }
            } catch (IOException e) {
                showError("Error loading keys: " + e.getMessage());
            }
        }
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