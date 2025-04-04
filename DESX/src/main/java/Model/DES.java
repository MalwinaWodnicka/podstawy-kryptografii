package Model;

import java.util.Arrays;

public class DES {

    public byte[] encrypt(byte[] byteData, byte[] byteKey) {
        if (byteKey.length != 8) {
            throw new IllegalArgumentException("Key must be exactly 8 bytes (64 bits)");
        }

        byte[] paddedData = padData(byteData);

        //1: Konwersja danych i klucza na tablicę 64-bitową
        byte[] binaryData = Converter.byteTo64Bit(paddedData);
        byte[] binaryKey64 = Converter.byteTo64Bit(byteKey);

        Key key = new Key(binaryKey64);
        byte[][] subkeys = key.Subkey();

//        System.out.println("Binary Data: " + Arrays.toString(binaryData));
//        System.out.println("Key64: " + Arrays.toString(binaryKey64));
//        for (int i = 0; i < 16; i++) {
//        System.out.println("Subkey: " + Arrays.toString(subkeys[i]));}

        Data data = new Data(binaryData);
        //7a: Podział danych na dwie połowy
        byte[] left = data.getLeft();
        byte[] right = data.getRight();

        byte[] data48;
        byte[] data64 = new byte[64];

        for (int i = 0; i < 16; i++) {
            System.out.println("=== Runda " + (i + 1) + " ===");
            System.out.println("Wejściowa prawa strona: " + Arrays.toString(right));
            System.out.println("Podklucz: " + Arrays.toString(subkeys[i]));
            //7b: Expansion Permutation (rozszerzenie bloku 32 bitowego na 48 bitowy)
            data48 = Functions.Permutation(Tables.E,right,48);
            System.out.println("Po rozszerzeniu (E): " + Arrays.toString(data48));
            byte[] subkey = subkeys[i];
            //7c: Operacja XOR na podkluczach i rozszerzonej prawej części danych
            data48 = Functions.XOR(data48,subkey);
            System.out.println("Po XOR z podkluczem: " + Arrays.toString(data48));
            //7d: Przetworzenie 8 grup 6 bitowych przez S-Boxy
            data48 = Functions.sBoxTransformation(data48);
            System.out.println("Po S-Boxach: " + Arrays.toString(data48));
            //7e: Permutation Function, permutacja 32-bitowego otputu
            data48 = Functions.Permutation(Tables.P, data48,32);
            System.out.println("Po permutacji P: " + Arrays.toString(data48));
            //7f: XOR na lewej stronie i funkcji f
            data48 = Functions.XOR(left,data48);
            System.out.println("Po XOR z lewą stroną: " + Arrays.toString(data48));
            //7g: Zamiana stronami
            left = right;
            right = data48;
            System.out.println("Nowa lewa: " + Arrays.toString(left));
            System.out.println("Nowa prawa: " + Arrays.toString(right));
        }

        System.arraycopy(right, 0, data64, 0, 32);
        System.arraycopy(left, 0, data64, 32, 32);

        //8: Inverse Initial Permutation, odwrócenie permutacji
        data64 = Functions.Permutation(Tables.IP1,data64,64);

        //9: Konwersja z postaci binarną na szesnastkową
        return Converter.bitTobyte(data64);
    }

    private byte[] padData(byte[] data) {
        int padLength = 8 - (data.length % 8);
        byte[] padded = new byte[data.length + padLength];
        System.arraycopy(data, 0, padded, 0, data.length);

        for (int i = data.length; i < padded.length; i++) {
            padded[i] = (byte) padLength;
        }

        return padded;
    }

};
