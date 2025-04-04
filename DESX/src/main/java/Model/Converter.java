package Model;

public class Converter {
    //Konwertowanie tablicy bajtów na tablicę 64 bitów
    public static byte[] byteTo64Bit(byte[] bytes) {
        byte[] paddedBytes = new byte[8];
        System.arraycopy(bytes, 0, paddedBytes, 0, Math.min(bytes.length, 8));

        for (int i = bytes.length; i < 8; i++) {
            paddedBytes[i] = 0;
        }

        byte[] tab64 = new byte[64];
        int iteracje = 0;

        for (int i = 0; i < 8; i++) {
            int number = paddedBytes[i] & 0xFF;
            byte[] bits8 = new byte[8];

            for (int j = 7; j >= 0; j--) {
                bits8[j] = (byte) ((number >> (7 - j)) & 1);
            }

            for (int j = 0; j < 8; j++) {
                tab64[iteracje++] = bits8[j];
            }
        }
        return tab64;
    }

    public static byte[] bitTobyte(byte[] bits) {
        byte[] result = new byte[8];
        int bitIndex = 0;

        for (int bytePos = 0; bytePos < 8; bytePos++) {
            byte value = 0;
            int mask = 0x80;

            for (int bitInByte = 0; bitInByte < 8; bitInByte++) {
                if (bits[bitIndex] == 1) {
                    value |= (byte) mask;
                }
                bitIndex++;
                mask >>>= 1;
            }

            result[bytePos] = value;
        }

        return result;
    }

    public static byte[] countBytes(byte[] bytes, int index, int count) {
        byte[] temp = new byte[count];
        for (int i = 0; i < count; i++) {
            temp[i] = bytes[index];
            index++;
        }
        return temp;
    }
}
