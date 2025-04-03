package Model;

public class Permutation {
    public static byte[] Permutation(byte[] pattern, byte[] data, int length) {
        byte[] permutedData = new byte[length];
        for (int i = 0; i < length; i++) {
            permutedData[i] = data[pattern[i] - 1];
        }
        return permutedData;
    }
}
