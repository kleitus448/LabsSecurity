package main.utils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class PrimeNumberGenerator {

    public static BigInteger getBigPrime(){
        return new BigInteger(15, new SecureRandom())
                .nextProbablePrime();
    }

    public static BigInteger getSafePrime() {
        BigInteger prime = getBigPrime(), safePrime;
        do {
            safePrime = prime.multiply(new BigInteger("2")).add(BigInteger.ONE);
            if (safePrime.isProbablePrime(5)) return safePrime;
            else prime = prime.nextProbablePrime();
        } while (true);
    }

    public static BigInteger getPrimitiveRoot(BigInteger mod) {
        BigInteger root = new BigInteger("2");
        while (!isRootPrimitive(root, mod)) root = root.add(BigInteger.ONE);
        return root;
    }

    private static boolean isRootPrimitive(BigInteger number, BigInteger modulo) {
        List<BigInteger> list = new ArrayList<>();
        for (BigInteger pow = BigInteger.ONE; pow.compareTo(modulo) < 0; pow = pow.add(BigInteger.ONE)) {
            BigInteger rem = number.modPow(pow, modulo);
            if (list.contains(rem)) return false;
            list.add(rem);
        }
        return true;
    }
}
