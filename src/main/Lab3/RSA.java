package main.Lab3;

import java.math.BigInteger;
import java.util.Base64;

class RSA {

    //Генерация ключа RSA
    static Key genKey(BigInteger p, BigInteger q){
        BigInteger n = p.multiply(q);
        BigInteger fi = p.subtract(BigInteger.ONE)
                    .multiply(q.subtract(BigInteger.ONE));

        //В качестве e берётся одно из чисел Ферма
        BigInteger e = BigInteger.valueOf(65537);
        BigInteger d = e.modInverse(fi);
        return new Key(e, n, d);
    }

    //Шифрование сообщения
    static String encrypt(String message, Key key){
        Base64.Encoder encoder = Base64.getMimeEncoder();
        StringBuilder encrypted = new StringBuilder();
        for (char c : message.toCharArray()){
            BigInteger cNum = BigInteger.valueOf(c);
            encrypted.append(encoder.encodeToString(
                    cNum.modPow(key.e, key.n).toByteArray()));
            encrypted.append("\n");
        }
        return encrypted.toString();
    }

    //Расшифровка сообщения
    static String decrypt(String encryptedMessage, Key key){
        Base64.Decoder decoder = Base64.getMimeDecoder();
        StringBuilder decrypted = new StringBuilder();
        String[] messages = encryptedMessage.split("\n");
        for (String message : messages) {
            decrypted.append((char)
                new BigInteger(decoder.decode(message)).modPow(key.d, key.n).intValue());
        }
        return decrypted.toString();
    }

    //Класс ключа RSA
    static class Key {
        BigInteger e, n, d;
        Key(BigInteger e, BigInteger n, BigInteger d) {
            this.e = e; this.n = n; this.d = d;
        }
    }
}
