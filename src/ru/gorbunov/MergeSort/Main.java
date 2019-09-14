package ru.gorbunov.MergeSort;

import ru.gorbunov.MergeSort.Sorting.MergeSort;

public class Main {
    /**
     * При запуске проверяется правильность входных аргументов.
     *
     * @param args входные данные
     *             1-ключ -а сортровка по возрастанию -d сортировка по убыванию -необязательный параметр без него будет
     *             выбрана сортировка по возрастанию
     *             2-ключ -i сортируем целые числа -s сортируем строки
     *             3- выходной файл в котором будет результат
     *             4- входные данные необходимо указать файлы которые необходимо обьеденить вводить не меньше одного файла
     */
    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                throw new IllegalArgumentException("Вы не ввели аргументы.");
            }
            int i = 0;
            String[] keys = new String[2];

            if (args[i].equals("-a") || args[i].equals("-d")) {
                keys[i] = args[i];
                i++;
            } else {
                keys[i] = "-a";
            }

            if (!args[i].equals("-i") && !args[i].equals("-s")) {
                throw new IllegalArgumentException("Аргументы необходимо вводить согласно правилам указаным к заданию");
            } else {
                keys[1] = args[i];
                i++;
            }
            String outputFile;

            if (args[i].equals("") || args[i + 1].equals("")) {
                throw new IllegalArgumentException("Аргументы необходимо вводить согласно правилам указаным к заданию");
            } else {
                outputFile = args[i];
                i++;
            }

            String[] files = new String[args.length - i];
            System.arraycopy(args, i, files, 0, files.length);
            MergeSort mergeSort = new MergeSort(keys, outputFile, files);
            mergeSort.Merge();
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
