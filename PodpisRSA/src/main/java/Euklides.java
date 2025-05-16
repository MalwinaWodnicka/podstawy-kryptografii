import java.math.BigInteger;

public class Euklides {

    public static BigInteger EuclideanAlgorithmExtended(BigInteger a, BigInteger b){
        BigInteger u, w, x, z, q;
        BigInteger zero = BigInteger.ZERO;
        BigInteger one =  BigInteger.ONE;

        q = zero;
        u = one;
        x = zero;
        w = a;
        z = b;

        while(w.compareTo(zero) != 0) {
            if(w.compareTo(z) == -1) {
                q = u;
                u = x;
                x = q;
                q = w;
                w = z;
                z = q;
            }
            q = w.divide(z);
            u = u.subtract(q.multiply(x));
            w = w.subtract(q.multiply(z));
        }

        if(z.compareTo(one) == 0) {
            if (x.compareTo(zero) == -1) {
                x = x.add(b);
            }
            return x;
        }
        return x;
    }
    public static BigInteger EuclideanAlgorithm(BigInteger a, BigInteger b){
        BigInteger tmp;

        while(!b.equals(BigInteger.ZERO)){
            tmp = a.mod(b);
            a = b;
            b = tmp;
        }
        return a;
    }
    public static int EuclideanAlgorithm(int a, int b){
        int tmp;

        while(b != 0){
            tmp = a % b;
            a = b;
            b = tmp;
        }
        return a;
    }

    public static boolean isPrime (BigInteger a, BigInteger b){
        return EuclideanAlgorithm(a,b).equals(BigInteger.ONE);
    }
    public static boolean isPrime (int a, int b)
    {
        return EuclideanAlgorithm(a,b) == 1;
    }
}
