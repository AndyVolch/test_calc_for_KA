import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Принимаем строку с примером на ввод
        Scanner s = new Scanner(System.in);
        System.out.println("Введите пример одной строкой");
        String input = s.nextLine();
        System.out.println(Main.calc(input));
    }
    public static String calc(String input){
        // Разбиваем строку на элементы цифры и ариф.знак
        String[] parts = input.split(" ");

        // Проверка на формат операции
        try {
            Check.check_form(parts);
        }catch (IOException e){
            System.out.println(e);
            System.exit(1);
        }
        // Проверка на соответствие типов чисел + на соответствие символов
        boolean roman = false; // Переменная-флаг, запоминающая тип чисел
        try {
            roman = Check.check_number_type(parts);
        }catch (IOException e){
            System.out.println(e);
            System.exit(1);
        }
        // Проверка на вхождение арабских чисел в диапазон от 1 до 10
        try {
            Check.arabic_confirm(parts);
        }catch (IOException e){
            System.out.println(e);
            System.exit(1);
        }

        // РАСЧЁТ
        // Создаём объекты класса Transform для трансформации в тип int
        Transform num1 = new Transform();
        num1.roman = parts[0];
        Transform num2 = new Transform();
        num2.roman = parts[2];

        // Создаём объект класса Count для расчёта ответа
        Count answer = new Count();

        // Подготовим переменную для вывода ответа
        String out = "";
        // В зависимости от типа чисел получаем ответ
        if(roman){//РИМСКИЕ ЧИСЛА: Трансформация римских цифр в арабские (int)
            answer.value1 = num1.roman_to_arabic();
            answer.value2 = num2.roman_to_arabic();
            answer.sign = parts[1];
            // Расчёт
            int ans = 0;
            try {
                ans = answer.count();
            }catch (IOException e){
                System.out.println(e);
                System.exit(1);
            }
            // Обратная трансформация
            Transform roman_answer = new Transform();
            roman_answer.arabic = ans;
            try {
                out = roman_answer.arabic_to_roman();
            }catch (IOException e){
                System.out.println(e);
            }
        }else {// АРБСКИЕ ЧИСЛА: Трансформация в int
            answer.value1 = num1.arabic_to_count();
            answer.value2 = num2.arabic_to_count();
            answer.sign = parts[1];
            // Расчёт
            try {
            out = answer.count()+"";
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    return out;
    }
}

class Check{// КЛАСС ПРОВЕРОК
    static void check_form(String[] input) throws IOException{// ПРОВЕРКА НА СООТВЕТСТВИЕ ФОРМЫ ВХОДНОГО ПРИМЕРА
        if(input.length != 3) {
            throw new IOException("Формат операции не удовлетворяет условию");
        }
    }
    static boolean check_number_type(String[] input) throws IOException{// ПРОВЕРКА НА СООТВЕТСВИЕ ЧИСЕЛ РИМСКОЙ/АРАБСКОЙ СИСТЕМЕ
        // По умолчанию числа арабские
        boolean roman_flag1 = false;
        boolean roman_flag2 = false;

        //Проверяем первое число
        // если индекс символа в уникоде 73, 86, 88, то цифра Римская
        if (input[0].codePointAt(0) == 73 || input[0].codePointAt(0) == 86 || input[0].codePointAt(0) == 88){
            roman_flag1 = true;
        // если индекс дополнительно не соответствует арабским числам, бросаем исключение
        } else if (input[0].codePointAt(0) < 49 || input[0].codePointAt(0) > 57) {
            throw new IOException("В строке есть неидопустимые символы. Используйте арабские или римские цифры от 1 до 10");
        }

        //Проверяем второе число
        // если индекс символа в уникоде 73, 86, 88, то цифра Римская
        if (input[2].codePointAt(0) == 73 || input[2].codePointAt(0) == 86 || input[2].codePointAt(0) == 88){
            roman_flag2 = true;
        } else if (input[2].codePointAt(0) < 49 || input[2].codePointAt(0) > 57) {
            throw new IOException("В строке есть неидопустимые символы. Используйте арабские или римские цифры от 1 до 10");
        }

        // Проверка на соответствие двух чисел одному типу
        if (roman_flag1 != roman_flag2) {
            throw new IOException("Типы чисел не соответствуют друг другу");
        }
    return roman_flag1;
    }
    static void arabic_confirm(String [] input) throws IOException{
        // Если используем арабские числа и они или > 10 — бросаем исключение
        // Условие, что они не могут быть <1 соблюдается в методе check_number_type()
        if (Check.check_number_type(input) == false && (Integer.parseInt(input[0]) > 10 || Integer.parseInt(input[2]) > 10 )){
            throw new IOException("Арабские числа в примере должны быть от 1 до 10 включительно");
        }
    }
}

class Transform{ // КЛАСС ПРЕОБРАЗОВАНИЙ РАЗНЫХ ТИПОВ ЧИСЕЛ
    String roman;
    int arabic;
    String [] roman_list_sing = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
    String [] roman_list_decimal = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC", "C"};

    int roman_to_arabic(){ // переводит римские цифры в арабские
        int number = 0;
        for(int i = 0; i<roman_list_sing.length; i++){
            if(roman.compareTo(roman_list_sing[i])==0){
                number = i;
                }
        }
        return number;
    }
    String arabic_to_roman() throws IOException { // переводит арабские в римские
        if(arabic > 0) {
            int decimal = arabic / 10;
            int sing = arabic % 10;
            return roman_list_decimal[decimal] + roman_list_sing[sing];
        }else{
            throw new IOException("Римское число может быть только положительным");
        }
    }
    int arabic_to_count(){ // переводит арабские в класс int для вывода в count
        return Integer.parseInt(roman);
    }
}

class Count { // КЛАСС АРИФМЕТИЧЕСКИХ ОПЕРАЦИЙ
int value1;
int value2;
String sign;
    int count() throws IOException{
        int result = 0;
        switch(sign){
            case "+":
                result = value1 + value2;
                break;
            case "-":
                result = value1 - value2;
                break;
            case "*":
                result = value1 * value2;
                break;
            case "/":
                result = value1 / value2;
                break;
            default:
                throw new IOException("Некорректный арифметический знак");
        }
        return result;
    }
}