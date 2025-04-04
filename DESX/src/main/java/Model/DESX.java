package Model;

import java.util.Arrays;

public class DESX {
    private final DES des = new DES();

    public byte[] encrypt(byte[] plainText, byte[] keyInternal, byte[] keyDes, byte[] keyExternal) {
        if (keyInternal.length != 8 || keyDes.length != 8 || keyExternal.length != 8) {
            throw new IllegalArgumentException("All keys must be 8 bytes long");
        }

        byte[] paddedData = padData(plainText);
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

        return removePadding(finalText);
    }

    private byte[] getBlock(byte[] data, int offset) {
        byte[] block = new byte[8];
        System.arraycopy(data, offset, block, 0, 8);
        return block;
    }

    private byte[] padData(byte[] data) {
        int padLength = 8 - (data.length % 8);
        byte[] padded = new byte[data.length + padLength];
        System.arraycopy(data, 0, padded, 0, data.length);
        Arrays.fill(padded, data.length, padded.length, (byte)padLength);
        return padded;
    }

    private byte[] removePadding(byte[] data) {
        if (data.length == 0) return data;
        int padLength = data[data.length - 1] & 0xFF;
        if (padLength > 8) return data;
        return Arrays.copyOf(data, data.length - padLength);
    }
}