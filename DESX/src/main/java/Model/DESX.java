package Model;

public class DESX {

    public byte[] encrypt(byte[] plainText, byte[] keyInternal, byte[] keyDes, byte[] keyExternal) {
        DES des = new DES();
        byte[] finalText = new byte[plainText.length];
        for(int i = 0; i < plainText.length/8; i++){
            byte[] tmp = Functions.XOR(Converter.countBytes(plainText, i * 8, 8), keyInternal);
            tmp  = des.encrypt(tmp, keyDes);
            tmp = Functions.XOR(keyExternal,tmp);
            System.arraycopy(tmp, 0, finalText, i * 8, 8);
        }
        return finalText;
    }

    public byte[] decrypt(byte[] plainText, byte[] keyInternal, byte[] keyDes, byte[] keyExternal) {
        DES des = new DES();
        byte[] finalText = new byte[plainText.length];
        for(int i = 0; i < plainText.length/8; i++){
            byte[] tmp = Functions.XOR(Converter.countBytes(plainText, i * 8, 8), keyExternal);
            tmp = des.decrypt(tmp, keyDes);
            tmp = Functions.XOR(keyInternal,tmp);
            System.arraycopy(tmp, 0, finalText, i * 8, 8);
        }
        return finalText;
    }
}
