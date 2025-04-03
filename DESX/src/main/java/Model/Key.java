package Model;

import java.util.Arrays;

public class Key {
    private byte[] key64;
    private byte[] key56;
    private byte[] key48;
    private byte[][] Podklucz = new byte[16][48];

    //2: Permutacja klucza przez PC-1 (z 64 na 56 bitów)
    public Key(byte[] table) {
        this.key64 = table;
        this.key56 = Permutation.Permutation(Tables.PC1, key64, 56);
    }

    public byte[][] Subkey() {
        for (int i = 0; i < Tables.SoLS.length; i++) {
            byte count = Tables.SoLS[i];

            // 3: Podział klucza na pół
            byte[] leftHalf = Arrays.copyOfRange(this.key56, 0, 28);
            byte[] rightHalf = Arrays.copyOfRange(this.key56, 28, 56);

            // 4: Przesunięcia bitowe dla każdej połowy
            for (int shift = 0; shift < count; shift++) {
                // Przesunięcie lewej połowy
                byte tempL = leftHalf[0];
                for (int j = 0; j < 27; j++) {
                    leftHalf[j] = leftHalf[j + 1];
                }
                leftHalf[27] = tempL;

                // Przesunięcie prawej połowy
                byte tempR = rightHalf[0];
                for (int j = 0; j < 27; j++) {
                    rightHalf[j] = rightHalf[j + 1];
                }
                rightHalf[27] = tempR;
            }

            // 5: Konkatenacja lewej i prawej połowy
            System.arraycopy(leftHalf, 0, this.key56, 0, 28);
            System.arraycopy(rightHalf, 0, this.key56, 28, 28);

            // 6: Permutacja przez PC-2 (z 56 na 48 bitów)
            key48 = Permutation.Permutation(Tables.PC2, this.key56, 48);

            //Zapis podklucza dla tej rundy
            System.arraycopy(key48, 0, Podklucz[i], 0, 48);
        }
        return Podklucz;
    }
}
