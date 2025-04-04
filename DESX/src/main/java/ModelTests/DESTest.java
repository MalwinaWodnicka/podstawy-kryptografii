package ModelTests;

import Model.DES;
import org.junit.Test;
import static org.junit.Assert.*;

public class DESTest {

    private byte[] hexToBytes(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
        }
        return bytes;
    }

    @Test
    public void testEncrypt() {
        byte[] plaintext = hexToBytes("0123456789ABCDEF");
        byte[] key = hexToBytes("0123456789ABCDEF");
        byte[] expectedCiphertext = hexToBytes("56CC09E7CFDC4CEF");

        DES des = new DES();
        byte[] ciphertext = des.encrypt(plaintext, key);

        assertArrayEquals("Ciphertext powinien zgadzać się z wektorem testowym",
                expectedCiphertext, ciphertext);
    }

    @Test
    public void testDecrypt() {
        // Given
        byte[] ciphertext = hexToBytes("56CC09E7CFDC4CEF");
        byte[] key = hexToBytes("0123456789ABCDEF");
        byte[] expectedPlaintext = hexToBytes("0123456789ABCDEF");

        DES des = new DES();
        byte[] decrypted = des.decrypt(ciphertext, key);

        assertArrayEquals("Odszyfrowany tekst powinien zgadzać się z wektorem testowym",
                expectedPlaintext, decrypted);
    }

    @Test
    public void testPaddingHandling() {
        byte[] shortPlaintext = "Short".getBytes();
        byte[] key = new byte[8];

        DES des = new DES();
        byte[] ciphertext = des.encrypt(shortPlaintext, key);

        assertEquals(8, ciphertext.length);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidKeySize() {
        byte[] plaintext = hexToBytes("0123456789ABCDEF");
        byte[] invalidKey = hexToBytes("133457799BBCDF");

        DES des = new DES();
        des.encrypt(plaintext, invalidKey);
    }

    @Test
    public void testConsistentEncryption() {
        byte[] plaintext = "Test data".getBytes();
        byte[] key = "Password".getBytes();

        DES des = new DES();
        byte[] ciphertext1 = des.encrypt(plaintext, key);
        byte[] ciphertext2 = des.encrypt(plaintext, key);

        assertArrayEquals("Ten sam klucz i dane powinny dać ten sam wynik",
                ciphertext1, ciphertext2);
    }
}
