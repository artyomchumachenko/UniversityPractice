package secondterm.exam;

import java.util.Scanner;

public class Main {
    public static final String REGEX_FOR_CONCORD = "^[qwrtpsdfghjklzxcvbnm]$";
    public static final String REGEX_FOR_VOWEL = "^[eyuioa]$";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Введите текст на английском языке:");
        String text = scanner.nextLine();
        String[] offers = text.split("\\.");

        for (int i = 0; i < offers.length; i++) {
            int numOfConcord = 0;
            int numOfVowel = 0;
            for (int j = 0; j < offers[i].length(); j++) {
                String letter = Character.toString(offers[i].charAt(j));
                if (letter.toLowerCase().matches(REGEX_FOR_CONCORD)) {
                    numOfConcord++;
                } else if (letter.toLowerCase().matches(REGEX_FOR_VOWEL)) {
                    numOfVowel++;
                }
            }
            String result;
            if (numOfConcord > numOfVowel) {
                result = "больше согласных";
            } else if (numOfVowel > numOfConcord) {
                result = "больше гласных";
            } else result = "согласных и гласных одинаковое количество";
            System.out.println("В " + (i + 1) + " предложении " + result);
        }
    }
}
