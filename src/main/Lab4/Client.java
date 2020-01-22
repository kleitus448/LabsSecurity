package main.Lab4;

import java.math.BigInteger;
import java.security.SecureRandom;

class Client {
    private static final String alphabet = ("АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ"+
                                            "абвгдеёжзийклмнопрстуфхцчшщъыьэюя");

    private BigInteger N;   // "безопасное простое" (2*q+1)
    private BigInteger g;   // первообразный корень по модулю N
    private BigInteger k;   // параметр-множитель
    private BigInteger x;   // хэш от S и p
    private BigInteger v;   // верификатор пароля
    private BigInteger a;   // закрытый ключ клиента
    private BigInteger A;   // открытый ключ клиента
    private BigInteger B;   // открытый ключ сервера
    private BigInteger u;   // скрамблер
    private BigInteger K;   // хэш-значение ключа сессии
    private BigInteger M_C; // подтверждение ключа сессии
    private String I;       // логин клиента
    private String p;       // пароль клиента
    private String s;       // соль

    Client(BigInteger N, BigInteger g, BigInteger k, String I, String p) {
        this.N = N; this.g = g; this.k = k; this.I = I; this.p = p;
    }

    /**
     * Метод вычисляет значения
     * s, x, p для клента
     */
    void signUp() {
        s = generateSalt(8); // генерация соли
        x = HashUtil.generateHash(s.getBytes(), p.getBytes()); // x = H(s, p)
        v = g.modPow(x, N); // v = g^x mod N
    }

    /**
     * Метод генерирует соль длины "size"
     *
     * @param size - длина соли
     * @return - соль
     */
    private String generateSalt(int size) {
        final SecureRandom RANDOM = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; ++i)
            sb.append(alphabet.charAt(RANDOM.nextInt(alphabet.length())));
        return sb.toString();
    }

    /**
     * Метод генерирует число A
     * @return - число A типа BigInteger
     */
    BigInteger generateA() {
        a = new BigInteger(1024, new SecureRandom());
        A = g.modPow(a, N); // A = g^a mod N
        return A;
    }

    /**
     * Метод генерирует число U
     * (скремблер), если u == 0,
     * соединение прерывается
     */
    void generateU() {
        u = HashUtil.generateHash(A.toByteArray(), B.toByteArray()); // u = H(A, B)
        assert u.compareTo(BigInteger.ZERO) != 0; //u != 0
    }

    /**
     * Метод генерирует ключ сессии
     */
    void genSessionKey() {
        x = HashUtil.generateHash(s.getBytes(), p.getBytes()); // x = H(s, p)

        // S = (B - K*(g^x mod N))^(a+u*x)) mod N
        BigInteger S = (B.subtract(k.multiply(g.modPow(x, N))))
                               .modPow(a.add(u.multiply(x)), N);

        K = HashUtil.generateHash(S.toByteArray()); // K = H(S)
        System.err.println("Ключ (Клиент): " + K);
    }

    /**
     * Метод генерирует M
     * @return - значение M
     */
    BigInteger generateM() {
        // M = H(H(N) xor H(g), H(I), s, A, B, K)
        BigInteger hashN = HashUtil.generateHash(N.toByteArray());
        BigInteger hashG = HashUtil.generateHash(g.toByteArray());
        M_C = HashUtil.generateHash(
                hashN.xor(hashG).toByteArray(),
                HashUtil.generateHash(I.getBytes()).toByteArray(),
                s.getBytes(), A.toByteArray(), B.toByteArray(), K.toByteArray()
        );
        return M_C;
    }

    /**
     * Вычисление значения R, его сравнение
     * со значением R от сервера
     * @return - значение M
     */
    boolean compare_R(BigInteger R_S) {
        // R = H(A, M, K)
        BigInteger R_C = HashUtil.generateHash(A.toByteArray(), M_C.toByteArray(), K.toByteArray());
        return R_C.equals(R_S);
    }
    
    // Метод возвращает S
    String getS() {return s;}
    
    // Метод возвращает V
    BigInteger getV() {return v;}
    
    //Метод задаёт значения для S и B
    void set_S_and_B(String s, BigInteger B) {this.s = s; this.B = B;}
}
