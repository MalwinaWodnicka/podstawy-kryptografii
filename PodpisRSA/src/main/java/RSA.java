import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Random;

public class RSA {
    private BigInteger _p;
    private BigInteger _q;
    private BigInteger _n;
    private BigInteger _k;
    private BigInteger _Euler;
    private BigInteger _e = new BigInteger("65537");
    private BigInteger _d;
    private int bitLength;

    public BigInteger get_n() {
        return _n;
    }
    public BigInteger get_e() {
        return _e;
    }
    public BigInteger get_d() {
        return _d;
    }
    public BigInteger get_Euler() {
        return _Euler;
    }

    public RSA (int bitLength){
        this.bitLength = bitLength;
    }

    public void generateKeys(){
        Random random = new Random();
        _q = BigInteger.probablePrime(bitLength, random);
        _p = BigInteger.probablePrime(bitLength, random);
        _n = _p.multiply(_q);
        _k = generateCoprimeNumber(_n);
        _Euler = (_p.subtract(BigInteger.ONE)).multiply(_q.subtract(BigInteger.ONE));
        _e = new BigInteger("65537");
        _d = Euklides.EuclideanAlgorithmExtended(_e, _Euler);
    }

    public BigInteger CreateCipher(BigInteger message, BigInteger e, BigInteger n){
        return message.modPow(e,n);
    }
    public BigInteger Decrypt(BigInteger cypher, BigInteger d, BigInteger n){
        return cypher.modPow(d,n);
    }

    public BigInteger CreateT(BigInteger message, BigInteger e, BigInteger n){
        return message.multiply(_k.modPow(e,n));
    }


    public String CheckSignature(BigInteger message, BigInteger s, BigInteger k, BigInteger n){
        if(message.equals(s.multiply(k.modInverse(n))))
        {
            return "Podpis poprawny";
        }
        return "Podpis niepoprawny";
    }

    public BigInteger get_k() {
        return _k;
    }

    public BigInteger generateCoprimeNumber(BigInteger max){
        BigInteger tmp = max.divide(BigInteger.valueOf(3));
        while(!Euklides.isPrime(max,tmp)){
            tmp = tmp.add(BigInteger.ONE);
        }
        return tmp;
    }
}
