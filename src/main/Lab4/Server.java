package main.Lab4;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

class Server {
    private BigInteger N;   // большое простое число (модуль)
    private BigInteger g;   // генератор
    private BigInteger k;   // параметр-множитель
    private BigInteger v;   // верификатор пароля
    private BigInteger A;   // открытый ключ сервера
    private BigInteger b;   // закрытое значение сервера
    private BigInteger B;   // закрытый ключ сервера
    private BigInteger u;   // скрамблер
    private BigInteger K;   // хэш ключа сессии
    private String I;       // логин
    private String s;       // соль
    private Map<String, Pair<String, BigInteger>> database = new HashMap<>();

    Server(BigInteger N, BigInteger g, BigInteger k) {
        this.N = N; this.g = g; this.k = k;
    }

    void signUp(String I, String s, BigInteger v) throws IllegalAccessException {
        if (!database.containsKey(I)) database.put(I, new Pair<>(s, v));
        else throw new IllegalAccessException("Пользователь уже существует");
    }

    void setA(BigInteger A) throws IllegalAccessException {
        if (A.equals(BigInteger.ZERO)) // A != 0
            throw new IllegalAccessException();
        else this.A = A;
    }

    BigInteger generateB() {
        b = new BigInteger(1024, new SecureRandom());
        // B = (k*v + g^b mod N) mod N
        B = (k.multiply(v).add(g.modPow(b, N))).mod(N);
        return B;
    }

    void generateU() throws IllegalAccessException {
        u = HashUtil.generateHash(A.toByteArray(), B.toByteArray()); // u = H(A, B)
        if (u.equals(BigInteger.ZERO)) // u != 0
            throw new IllegalAccessException("u == 0");
    }

    String getClientS(String I) throws IllegalAccessException {
        if (database.containsKey(I)) {
            this.I = I;
            s = database.get(this.I).first;
            v = database.get(this.I).second;
            return s;
        } else
            throw new IllegalAccessException("Не могу найти пользователя.");
    }

    void genSessionKey() {
        // S = (A*(v^u mod N))^b mod N
        BigInteger S = A.multiply(v.modPow(u, N)).modPow(b, N);
        K = HashUtil.generateHash(S.toByteArray()); // K = H(S)
        System.err.println("Ключ (Сервер): " + K);
    }

    BigInteger testM(BigInteger M_C) {
        // M = H(H(N) xor H(g), H(I), s, A, B, K)
        BigInteger hashN = HashUtil.generateHash(N.toByteArray());
        BigInteger hashG = HashUtil.generateHash(g.toByteArray());
        BigInteger M_S = HashUtil.generateHash(hashN.xor(hashG).toByteArray(),
                HashUtil.generateHash(I.getBytes()).toByteArray(),
                s.getBytes(), A.toByteArray(), B.toByteArray(), K.toByteArray()
        );
        // R = H(A, M, K)
        if (M_S.equals(M_C))
            return HashUtil.generateHash(A.toByteArray(), M_S.toByteArray(), K.toByteArray());
        else
            return BigInteger.ZERO;
    }

    private static class Pair<A, B> {
        A first; B second;

        Pair(A first, B second) {
            this.first = first; this.second = second;
        }
    }
}

