package Model;

import java.util.Arrays;

public class DES {
    private Data data;
    private Key key;

    public byte[] encrypt(byte[] byteData, byte[] byteKey) {
        if (byteKey.length != 8) {
            throw new IllegalArgumentException("Key must be exactly 8 bytes (64 bits)");
        }

        byte[] paddedData = Functions.padData(byteData);

        //1: Konwersja danych i klucza na tablicę 64-bitową
        byte[] binaryData = Converter.byteTo64Bit(paddedData);
        byte[] binaryKey64 = Converter.byteTo64Bit(byteKey);

        key = new Key(binaryKey64);
        byte[][] subkeys = key.Subkey();

//        System.out.println("Binary Data: " + Arrays.toString(binaryData));
//        System.out.println("Key64: " + Arrays.toString(binaryKey64));
//        for (int i = 0; i < 16; i++) {
//        System.out.println("Subkey: " + Arrays.toString(subkeys[i]));}

        data = new Data(binaryData);
        //7a: Podział danych na dwie połowy
        byte[] left = data.getLeft();
        byte[] right = data.getRight();

        byte[] data48;
        byte[] data64 = new byte[64];

        for (int i = 0; i < 16; i++) {
            //System.out.println("Runda " + (i + 1));
            //System.out.println("Wejściowa prawa strona: " + Arrays.toString(right));
            //System.out.println("Podklucz: " + Arrays.toString(subkeys[i]));
            //7b: Expansion Permutation (rozszerzenie bloku 32 bitowego na 48 bitowy)
            data48 = Functions.Permutation(Tables.E,right,48);
            //System.out.println("Po rozszerzeniu (E): " + Arrays.toString(data48));
            byte[] subkey = subkeys[i];
            //7c: Operacja XOR na podkluczach i rozszerzonej prawej części danych
            data48 = Functions.XOR(data48,subkey);
            //System.out.println("Po XOR z podkluczem: " + Arrays.toString(data48));
            //7d: Przetworzenie 8 grup 6 bitowych przez S-Boxy
            data48 = Functions.sBoxTransformation(data48);
            //System.out.println("Po S-Boxach: " + Arrays.toString(data48));
            //7e: Permutation Function, permutacja 32-bitowego otputu
            data48 = Functions.Permutation(Tables.P, data48,32);
            //System.out.println("Po permutacji P: " + Arrays.toString(data48));
            //7f: XOR na lewej stronie i funkcji f
            data48 = Functions.XOR(left,data48);
            //System.out.println("Po XOR z lewą stroną: " + Arrays.toString(data48));
            //7g: Zamiana stronami
            left = right;
            right = data48;
            //System.out.println("Nowa lewa: " + Arrays.toString(left));
            //System.out.println("Nowa prawa: " + Arrays.toString(right));
        }

        System.arraycopy(right, 0, data64, 0, 32);
        System.arraycopy(left, 0, data64, 32, 32);

        //8: Inverse Initial Permutation, odwrócenie permutacji
        data64 = Functions.Permutation(Tables.IP1,data64,64);

        //9: Konwersja z postaci binarną na szesnastkową
        return Converter.bitTobyte(data64);
    }

    public byte[] decrypt(byte[] byteText, byte[] byteKey) {
        byte[] binaryData = Converter.byteTo64Bit(byteText);
        byte[] binaryKey = Converter.byteTo64Bit(byteKey);

        key = new Key(binaryKey);
        byte[][] keys = key.Subkey();
        data = new Data(binaryData);

        byte[] left = data.getLeft();
        byte[] right = data.getRight();

        byte[] data48 = new byte[48];
        byte[] data64 = new byte[64];

        for (int i = 15; i >= 0; i--) {
            data48 = Functions.Permutation(Tables.E,right,48);
            byte[] key = keys[i];

            data48 = Functions.XOR(data48,key);
            data48 = Functions.sBoxTransformation(data48);
            data48 = Functions.Permutation(Tables.P, data48,32);
            data48 = Functions.XOR(left,data48);

            left = right;
            right = data48;
        }

        System.arraycopy(right, 0, data64, 0, 32);
        System.arraycopy(left, 0, data64, 32, 32);

        data64 = Functions.Permutation(Tables.IP1,data64,64);

        return Converter.bitTobyte(data64);
    }

};
