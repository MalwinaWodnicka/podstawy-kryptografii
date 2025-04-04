package ModelTests;

import Model.DESX;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class DESXTest {

    @Test
    public void testEncryptDecrypt() {
            byte[] plainText = new byte[]{1, 35, 69, 103, -119, -85, -51, -17};
            byte[] keyInternal = new byte[]{12, 34, 56, 78, 90, -12, -34, -56};
            byte[] keyDes = new byte[]{1, 35, 69, 103, -119, -85, -51, -17};
            byte[] keyExternal = new byte[]{23, 45, 67, 89, -101, -67, -33, -5};

            DESX desx = new DESX();

            byte[] encrypted = desx.encrypt(plainText, keyInternal, keyDes, keyExternal);
            byte[] decrypted = desx.decrypt(encrypted, keyInternal, keyDes, keyExternal);

            System.out.println("Oryginalny tekst: " + Arrays.toString(plainText));
            System.out.println("Zaszyfrowany tekst: " + Arrays.toString(encrypted));
            System.out.println("Odszyfrowany tekst: " + Arrays.toString(decrypted));
            System.out.println("Odszyfrowanie poprawne? " + Arrays.equals(plainText, decrypted));
    }
}

