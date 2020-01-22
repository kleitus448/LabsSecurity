package main.Lab4;

import main.utils.PrimeNumberGenerator;

import java.math.BigInteger;

public class TestSRP {
    /**
     * "Поле безопасности"
     * N - безопасное простое 2*q+1, где q - тоже простое число
     * g - первообразный корень по модулю N
     * k - параметр-множитель
     */
    private BigInteger N = PrimeNumberGenerator.getSafePrime();
    private BigInteger g = PrimeNumberGenerator.getPrimitiveRoot(N);
    private BigInteger k = BigInteger.valueOf(3);

    private Server server = new Server(N, g, k);

    public static void main(String[] args) {
        String user = "just_login", password = "123456";
        TestSRP test = new TestSRP();
        test.signUp(user, password); //регистрация
        test.signIn(user, password); //авторизация
    }

    /**
     * Регистрация нового пользователя
     * @param login - логин
     * @param password - пароль
     */
    private void signUp(String login, String password) {
        System.out.println("Регистрация");
        Client client = new Client(N, g, k, login, password);

        // Регистрация клиента, вычисление "s", "x", "p"
        client.signUp();

        // Передача серверу "s", "v" и "login"
        String s = client.getS();
        BigInteger v = client.getV();
        try {
            server.signUp(login, s, v);
        } catch (IllegalAccessException e) {
            System.err.println("Это имя пользователя недоступно");
        }
    }
    /**
     * Авторизация пользователя
     * @param login - логин
     * @param password - пароль
     */
    private void signIn(String login, String password) {
        Client client = new Client(N, g, k, login, password);
        BigInteger A = client.generateA();
        try {
            server.setA(A);
        } catch (IllegalAccessException e) {
            System.err.println("Ошибка! A = 0");
            return;
        }
        try {
            String s = server.getClientS(login);
            BigInteger B = server.generateB();
            client.set_S_and_B(s, B);
        } catch (IllegalAccessException e) {
            System.err.println("Этого пользователя не существует");
            return;
        }

        // Вычисления скрамблеров на обеих сторонах
        try {
            client.generateU();
            server.generateU();
        } catch (IllegalAccessException e) {
            System.err.println(e.getMessage());
            return;
        }

        // Вычисления общего ключа сессии
        client.genSessionKey(); server.genSessionKey();

        //Генерация подтверждения и проверка
        BigInteger server_R = server.testM(client.generateM());
        if (client.compare_R(server_R))
            System.out.println("Соединение установлено");
        else
            System.out.println("Неправильный пароль");
    }
}