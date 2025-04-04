package Model;

public class Functions {
    public static byte[] Permutation(byte[] pattern, byte[] data, int length) {
        byte[] permutedData = new byte[length];
        for (int i = 0; i < length; i++) {
            permutedData[i] = data[pattern[i] - 1];
        }
        return permutedData;
    }

    public static byte[] XOR(byte[] a, byte[] b) {
        byte[] r = new byte[a.length];
        for (int i = 0; i < a.length; i++) r[i] = (byte)(a[i] ^ b[i]);
        return r;
    }

    public static byte[] sBoxTransformation(byte[] the48Block) {
        if (the48Block.length != 48) {
            throw new IllegalArgumentException("Input must be exactly 48 bits");
        }

        byte[] result = new byte[32];

        for (int i = 0; i < 8; i++) {
            byte[] sixBits = new byte[6];
            System.arraycopy(the48Block, i * 6, sixBits, 0, 6);

            int row = (sixBits[0] << 1) | sixBits[5];

            int col = (sixBits[1] << 3) | (sixBits[2] << 2) | (sixBits[3] << 1) | sixBits[4];

            byte sboxValue = Tables.SBOX[i][row][col];

            byte[] fourBits = {
                    (byte)((sboxValue >> 3) & 1),
                    (byte)((sboxValue >> 2) & 1),
                    (byte)((sboxValue >> 1) & 1),
                    (byte)(sboxValue & 1)
            };

            System.arraycopy(fourBits, 0, result, i * 4, 4);
        }

        return result;
    }

    public static byte[] padData(byte[] data) {
        int padLength = 8 - (data.length % 8);
        byte[] padded = new byte[data.length + padLength];
        System.arraycopy(data, 0, padded, 0, data.length);

        for (int i = data.length; i < padded.length; i++) {
            padded[i] = (byte) padLength;
        }

        return padded;
    }
}
