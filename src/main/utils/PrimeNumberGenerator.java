package main.utils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PrimeNumberGenerator {

    // Размерность случайного числа
    public static int bitSize = 15;

    public static BigInteger getBigPrime(){
        return new BigInteger(bitSize, new SecureRandom())
                .nextProbablePrime();
    }

    public static BigInteger getSafePrime() {
        BigInteger prime = getBigPrime(), safePrime;
        do {
            safePrime = prime.multiply(new BigInteger("2")).add(BigInteger.ONE);

            // Встроенная проверка числа на простоту
            //if (safePrime.isProbablePrime(5)) return safePrime;

            // Проверка простоты через тест Рабина-Миллера
            if (testMillerRabin(safePrime)) return safePrime;
            else prime = prime.nextProbablePrime();
        } while (true);
    }

    /**
     * Нахождения первоообразного
     * корня по модулю "mod"
     *
     * @param mod - модуль (обязательно простое число)
     * @return - первообразный корень
     */
    public static BigInteger getPrimitiveRoot(BigInteger mod) {
        BigInteger root = new BigInteger("2");
        while (!isRootPrimitive(root, mod))
            root = root.add(BigInteger.ONE);
        return root;
    }

    /**
     * Проверка на то, является ли число "number"
     * первоообразным корнем по модулю "mod"
     *
     * @param number - возможный первообразный корень
     * @param mod - модуль (обязательно простое число)
     * @return - результат проверки
     *      true - является первообразным корнем
     *      false - не является первообрразным корнем
     */
    private static boolean isRootPrimitive(BigInteger number, BigInteger mod) {
        BigInteger euler_func = mod.subtract(BigInteger.ONE);
        for (BigInteger pow = BigInteger.ONE; pow.compareTo(euler_func) < 0; pow = pow.add(BigInteger.ONE)) {
            BigInteger numberPow = number.modPow(pow, mod);
            if (numberPow.compareTo(BigInteger.ONE.mod(mod)) == 0) return false;
        }
        return true;
    }

    /**
     * Тест Рабина-Миллера
     * Кол-во раундов ищется, как log2(n)
     *
     * @param n - число для проверки
     * @return - рузультат проверки
     *      "true" - возможно простое
     *      "false" - несоставное
     */
    private static boolean testMillerRabin(BigInteger n) {
        int s = 0; BigInteger t = n.subtract(BigInteger.ONE);
        while (t.mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            t = t.divide(BigInteger.TWO); s++;
        }
        int k = n.bitLength();
        A: for (int i = 0; i < k; i++) {
            BigInteger a = getBigIntegerInRange(BigInteger.TWO, n.subtract(BigInteger.TWO));
            BigInteger x = a.modPow(t, n);
            if (x.equals(BigInteger.ONE) || x.equals(n.subtract(BigInteger.ONE)))
                continue;
            for (int j = 0; j < s; j++) {
                x = x.modPow(BigInteger.TWO, n);
                if (x.equals(BigInteger.ONE))
                    return false;
                if (x.equals(n.subtract(BigInteger.ONE)))
                    break A;
            }
            return false;
        }
        return true;
    }

    /**
     * Генерация BigInteger в диапазоне
     * [minLimit, maxLimit]
     *
     * @param minLimit - минимальное число
     * @param maxLimit - максимальное число
     * @return - случайное число в диапазоне [minLimit, maxLimit]
     */
   private static BigInteger getBigIntegerInRange(BigInteger minLimit, BigInteger maxLimit) {
       BigInteger bigInteger = maxLimit.subtract(minLimit);
       Random randNum = new Random();
       int len = maxLimit.bitLength();
       BigInteger res = new BigInteger(len, randNum);
       if (res.compareTo(minLimit) < 0)
           res = res.add(minLimit);
       if (res.compareTo(bigInteger) >= 0)
           res = res.mod(bigInteger).add(minLimit);
       return res;
   }

    public static void main(String[] args) {
        System.out.println(testMillerRabin(BigInteger.valueOf(18)));
    }
}

