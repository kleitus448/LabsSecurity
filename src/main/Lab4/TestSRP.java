package main.Lab4;

import main.utils.PrimeNumberGenerator;

import java.math.BigInteger;

public class TestSRP {
    private BigInteger N = PrimeNumberGenerator.getSafePrime();
    private BigInteger g = PrimeNumberGenerator.getPrimitiveRoot(N);
    private BigInteger k = BigInteger.valueOf(3);
    private Server server = new Server(N, g, k);

    public static void main(String[] args) {
        String user = "just_login", password = "123456";
        TestSRP test = new TestSRP();
        test.signUp(user, password);
        test.signIn(user, password);
    }

    private void signUp(String login, String password) {
        System.out.println("Регистрация");
        Client client = new Client(N, g, k, login, password);
        client.signUp(); String s = client.getS();
        BigInteger v = client.getV();
        try {
            server.signUp(login, s, v);
        } catch (IllegalAccessException e) {
            System.err.println("Это имя пользователя недоступно");
        }
    }

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
        try {
            client.generateU();
            server.generateU();
        } catch (IllegalAccessException e) {
            System.err.println(e.getMessage());
            return;
        }
        client.genSessionKey();
        server.genSessionKey();
        BigInteger server_R = server.testM(client.generateM());
        if (client.compare_R(server_R))
            System.out.println("Соединение установлено");
        else
            System.out.println("Неправильный пароль");
    }
}