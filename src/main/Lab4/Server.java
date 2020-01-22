package main.Lab4;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

class Server {
    private BigInteger N;   // большое простое число (модуль)
    private BigInteger g;   // первообразный корень по модулю N
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

    /**
     * Регистрация пользователя
     * на стороне сервера
     *
     * @param I - имя пользователя
     * @param s - соль
     * @param v - верификатор пароля
     * @throws IllegalAccessException - "Пользователь уже существует"
     */
    void signUp(String I, String s, BigInteger v) throws IllegalAccessException {
        if (!database.containsKey(I)) database.put(I, new Pair<>(s, v));
        else throw new IllegalAccessException("Пользователь уже существует");
    }

    /**
     * Задать значение A (открытого ключа сервера)
     *
     * @param A - открытый ключ
     * @throws IllegalAccessException - проверка, что A != 0
     */
    void setA(BigInteger A) throws IllegalAccessException {
        if (A.equals(BigInteger.ZERO)) // A != 0
            throw new IllegalAccessException();
        else this.A = A;
    }

    /**
     * Метод генерирует число B
     * (закрытый ключ сервера)
     * @return - число B
     */
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

    /**
     * Метод возвращает соль пользователя,
     * созданную при регистрации
     *
     * @param I - имя пользователя
     * @return - соль
     * @throws IllegalAccessException - "Не могу найти пользователя"
     */
    String getClientS(String I) throws IllegalAccessException {
        if (database.containsKey(I)) {
            this.I = I;
            s = database.get(this.I).first;
            v = database.get(this.I).second;
            return s;
        } else
            throw new IllegalAccessException("Не могу найти пользователя.");
    }

    /**
     * Метод вычисляет ключ сессии
     */
    void genSessionKey() {
        // S = (A*(v^u mod N))^b mod N
        BigInteger S = A.multiply(v.modPow(u, N)).modPow(b, N);
        K = HashUtil.generateHash(S.toByteArray()); // K = H(S)
        System.err.println("Ключ (Сервер): " + K);
    }

    /**
     * Вычисление значения "М" на стороне сервера
     * и сравнение его со значением "M",
     * которое прислал клиент
     *
     * @param M_C - значение "M" клиента
     * @return - если клиент опознан, возвращается
     * значение R для дальнейшего подтверждения клиентом,
     * если клиент не опознан, возвращется 0 и соединение
     * разрывается, поскольку 0 не равен R, вычисленном
     * на стороне клиента
     */
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

    /**
     * Класс для хранения пар значений
     *
     * @param <A> - первое значение
     * @param <B> - второе значение
     */
    private static class Pair<A, B> {
        A first; B second;

        Pair(A first, B second) {
            this.first = first; this.second = second;
        }
    }
}

