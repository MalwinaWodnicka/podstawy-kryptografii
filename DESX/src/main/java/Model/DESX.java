package Model;

import java.util.Arrays;

public class DESX {
    private final DES des = new DES();

    public byte[] encrypt(byte[] plainText, byte[] keyInternal, byte[] keyDes, byte[] keyExternal) {
        if (keyInternal.length != 8 || keyDes.length != 8 || keyExternal.length != 8) {
            throw new IllegalArgumentException("All keys must be 8 bytes long");
        }

        byte[] paddedData = Functions.padData(plainText);
        byte[] finalText = new byte[paddedData.length];

        for (int i = 0; i < paddedData.length; i += 8) {
            byte[] block = getBlock(paddedData, i);
            byte[] tmp = Functions.XOR(block, keyInternal);
            tmp = des.encrypt(tmp, keyDes);
            tmp = Functions.XOR(tmp, keyExternal);
            System.arraycopy(tmp, 0, finalText, i, 8);
        }
        return finalText;
    }

    public byte[] decrypt(byte[] cipherText, byte[] keyInternal, byte[] keyDes, byte[] keyExternal) {
        if (keyInternal.length != 8 || keyDes.length != 8 || keyExternal.length != 8) {
            throw new IllegalArgumentException("All keys must be 8 bytes long");
        }

        byte[] finalText = new byte[cipherText.length];

        for (int i = 0; i < cipherText.length; i += 8) {
            byte[] block = getBlock(cipherText, i);
            byte[] tmp = Functions.XOR(block, keyExternal);
            tmp = des.decrypt(tmp, keyDes);
            tmp = Functions.XOR(tmp, keyInternal);
            System.arraycopy(tmp, 0, finalText, i, 8);
        }

        return Functions.removePadding(finalText);
    }

    private byte[] getBlock(byte[] data, int offset) {
        byte[] block = new byte[8];
        System.arraycopy(data, offset, block, 0, 8);
        return block;
    }
}