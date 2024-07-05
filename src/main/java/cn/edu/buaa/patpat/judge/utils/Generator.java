package cn.edu.buaa.patpat.judge.utils;

public class Generator {
    private Generator() {}

    public static int randomInt(int min, int max) {
        return (int) (Math.random() * (max - min) + min);
    }

    public static String randomWord(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = (int) (Math.random() * str.length());
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static String randomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = (int) (Math.random() * str.length());
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }
}
