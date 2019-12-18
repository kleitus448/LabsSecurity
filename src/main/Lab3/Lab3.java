package main.Lab3;

import main.utils.PrimeNumberGenerator;

import java.math.BigInteger;

public class Lab3 {
    public static void main(String[] args) {
        String message = "Клавиатура? ☑\n Нет, мышь)";

        //Генерация ключа
        BigInteger p = PrimeNumberGenerator.getBigPrime();
        BigInteger q = PrimeNumberGenerator.getBigPrime();
        RSA.Key key = RSA.genKey(p, q);

        //Шифрование через RSA
        String encrypted = RSA.encrypt(message, key);

        System.out.println("Зашифрованный фрагмент:\n" + encrypted);
        System.out.println("Расшифрованный фрагмент:\n" + RSA.decrypt(encrypted, key));
    }
}
