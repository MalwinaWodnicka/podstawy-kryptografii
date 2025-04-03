package Model;

public class Converter {
    //Konwertowanie tablicy bajtów na tablicę 64 bitów
    public static byte[] byteTo64Bit(byte[] bytes) {
        int iteracje = 0;
        byte[] tab64 = new byte[64];

        for (int i = 0; i < 8; i++) {
            int number = bytes[i];
            byte[] bits8 = new byte[8];

            if (number >= 0) {
                for (int j = 7; j >= 0; j--) {
                    bits8[j] = (byte) (number % 2 == 1 ? 1 : 0);
                    number = number / 2;
                }
            } else {
                number = -number;
                for (int j = 7; j >= 0; j--) {
                    bits8[j] = (byte) (number % 2 == 1 ? 1 : 0);
                    number = number / 2;
                }
                for (int j = 0; j < 8; j++) {
                    bits8[j] ^= 1;
                }
                for (int j = 7; j >= 0; j--) {
                    if (bits8[j] == 0) {
                        bits8[j] = 1;
                        break;
                    }
                    bits8[j] = 0;
                }
            }

            for (int j = 0; j < 8; j++) {
                tab64[iteracje++] = bits8[j];
            }
        }
        return tab64;
    }
}
