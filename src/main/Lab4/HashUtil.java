package main.Lab4;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class HashUtil {
    static BigInteger generateHash(byte[]... byteArrays) {
        try {
            MessageDigest sha = MessageDigest.getInstance("MD5");
            for (byte[] bytes : byteArrays) sha.update(bytes);
            byte[] hash = sha.digest();
            return new BigInteger(hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return BigInteger.ZERO;
        }
    }
}
