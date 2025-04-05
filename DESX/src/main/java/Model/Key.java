package Model;

import java.util.Arrays;

public class Key {
    private byte[] key64; //oryginalny klucz
    private byte[] key56; //klucz po PC-1
    private byte[] key48; //klucz po PC-2
    private byte[][] subkeys = new byte[16][48]; //tablica 16 podkluczy

    //2: Permutacja klucza przez PC-1 (z 64 na 56 bitów)
    public Key(byte[] key) {
        this.key64 = key;
        this.key56 = Functions.Permutation(Tables.PC1, key64, 56);
    }

    public byte[][] Subkey() {
        for (int i = 0; i < Tables.SoLS.length; i++) {
            byte count = Tables.SoLS[i];

            // 3a: Podział klucza 56-bitowego na dwie połowy po 28 bitów
            byte[] leftHalf = Arrays.copyOfRange(this.key56, 0, 28);
            byte[] rightHalf = Arrays.copyOfRange(this.key56, 28, 56);

            // 3b: Przesunięcia bitowe dla każdej połowy
            for (int shift = 0; shift < count; shift++) {
                byte tempL = leftHalf[0];
                for (int j = 0; j < 27; j++) {
                    leftHalf[j] = leftHalf[j + 1];
                }
                leftHalf[27] = tempL;

                byte tempR = rightHalf[0];
                for (int j = 0; j < 27; j++) {
                    rightHalf[j] = rightHalf[j + 1];
                }
                rightHalf[27] = tempR;
            }

            // 4: Konkatenacja lewej i prawej połowy
            System.arraycopy(leftHalf, 0, this.key56, 0, 28);
            System.arraycopy(rightHalf, 0, this.key56, 28, 28);

            // 5: Permutacja przez PC-2 (z 56 na 48 bitów)
            key48 = Functions.Permutation(Tables.PC2, this.key56, 48);

            //Zapis podklucza dla tej rundy
            System.arraycopy(key48, 0, subkeys[i], 0, 48);
        }
        return subkeys;
    }
}
