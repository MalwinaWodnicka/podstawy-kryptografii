package Model;

public class DES {

    public byte[] encrypt(byte[] byteData, byte[] byteKey) {
        //1: Konwersja danych i klucza na tablicę 64-bitową
        byte[] binaryData = Converter.byteTo64Bit(byteData);
        byte[] binaryKey64 = Converter.byteTo64Bit(byteKey);

        //tymczasowe
        return encrypt(binaryData, binaryKey64);
    }

};
