package util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Util {
    public static List<String> getNumbersFromAsnString(String sameNumbers) {
        if (sameNumbers == null) return null;
        List<String> numbers = new ArrayList<>();
        StringBuilder number = new StringBuilder(); // accumulates subsequent digits to a number
        int rangeStart = 0;
        boolean range = false;
        for (char c : sameNumbers.toCharArray()) {
            if (Character.isDigit(c)) number.append(c);
            else if (c == ',') {
                String numberStr = number.toString();
                if (numberStr.isEmpty()) continue;
                if (range) {
                    addRange(numbers, numberStr, rangeStart);
                    range = false;
                } else {
                    numbers.add(numberStr);
                }
                number.delete(0, number.length());
            } else if (c == '-') {
                rangeStart = Integer.parseInt(number.toString());
                number.delete(0, number.length());
                range = true;
            }
        }
        if (range) {
            addRange(numbers, number.toString(), rangeStart);
        } else {
            numbers.add(number.toString());
        }

        return numbers;
    }

    private static void addRange(List<String> numbers, String number, int rangeStart) {
        int rangeEnd = Integer.parseInt(number);
        for (int i = rangeStart; i <= rangeEnd; i++) {
            numbers.add(String.valueOf(i));
        }
    }
}
