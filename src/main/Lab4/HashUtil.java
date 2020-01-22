package main.Lab4;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

class HashUtil {
    /**
     * Функция для генерация хэша из массивов байт
     * @param byteArrays - массивы байт
     * @return - хэш типа BigInteger
     */
    static BigInteger generateHash(byte[]... byteArrays) {
        try {
            MessageDigest sha = MessageDigest.getInstance("MD5");
            for (byte[] bytes : byteArrays) sha.update(bytes);
            byte[] hash = sha.digest();
            System.out.println(Arrays.toString(hash));
            System.out.println(new BigInteger(hash));
            return new BigInteger(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return BigInteger.ZERO;
        }
    }
}
