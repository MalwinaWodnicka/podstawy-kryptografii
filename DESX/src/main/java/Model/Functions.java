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
        byte[] result = new byte[32];

        for (int i = 0; i < 8; i++) {
            byte[] sixBits = new byte[6];
            System.arraycopy(the48Block, i * 6, sixBits, 0, 6);

            int row = sixBits[1] * 8 + sixBits[2] * 4 + sixBits[3] * 2 + sixBits[4];

            int col = sixBits[0] * 2 + sixBits[5];

            byte number = Tables.SBOX[i][col][row];

            byte[] binaryTab = Converter.byteTo4Bit(number);
            System.arraycopy(binaryTab, 0, result, i * 4, 4);
        }

        return result;
    }
}
