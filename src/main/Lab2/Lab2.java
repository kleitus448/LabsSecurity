package main.Lab2;

import main.utils.PrimeNumberGenerator;

import java.math.BigInteger;

public class Lab2 {
    public static void main(String[] args) {
        BigInteger p, g, a, b, A, B, K_A, K_B;

        p = PrimeNumberGenerator.getBigPrime();
        g = getG(p);
        a = PrimeNumberGenerator.getBigPrime();
        b = PrimeNumberGenerator.getBigPrime();

        A = g.modPow(a, p);
        B = g.modPow(b, p);

        K_A = B.modPow(a, p);
        K_B = A.modPow(b, p);

        System.out.println("------ DiffieHellman by Alisa ------:\n");
        System.out.println("p: " + p + ", g: " + g + ", A: " + A + ", K: " + K_A);
        System.out.println("------ DiffieHellman by Bob ------:\n");
        System.out.println("p: " + p + ", g: " + g + ", B: " + B + ", K: " + K_B);
        System.out.println("K_A == K_B: " + K_A.equals(K_B));
    }

    private static BigInteger getG(BigInteger p) {
        BigInteger g;
        do {
            g = PrimeNumberGenerator.getPrimitiveRoot(p);
        } while (p.compareTo(g) <= 0);
        return g;
    }
}