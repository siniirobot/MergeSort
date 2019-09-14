package ru.gorbunov.MergeSort.Sorting;

import ru.gorbunov.MergeSort.Intefaces.ISortingDifferentTypes;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SortInt implements ISortingDifferentTypes {
    private String key;

    /**
     * Сортировка целочисленных чисел.
     * Конструктор согласно которому выбирается тип сортировки.
     *
     * @param key -a сортировка по возрастанию -d сортировка по убыванию
     */
    SortInt(String key) {
        this.key = key;
    }

    /**
     * Возвращает большее или меньшее число согласно ключу.
     *
     * @param first  первый сравниваемый элемент
     * @param second второй сравниваемый элемент
     * @return сравниваемый элемент.
     */
    @Override
    public Object sorting(Object first, Object second) {
        if (key.equals("-a")) {
            return Integer.parseInt((String) catchWrongFormat(first)) < Integer.parseInt((String) catchWrongFormat(second)) ? first : second;
        } else {
            return Integer.parseInt((String) catchWrongFormat(first)) > Integer.parseInt((String) catchWrongFormat(second)) ? first : second;
        }
    }

    /**
     * Проверяет является ли объект целочисленным числом.
     *
     * @param object проверяемый объект.
     * @return возвращает обьект если правильно, выдает ошибку NumberFormatException если не правильно.
     */
    @Override
    public Object catchWrongFormat(Object object) {
        Matcher matcher = Pattern.compile("-?\\d+").matcher((String) object);
        if (!matcher.matches()) {
            throw new NumberFormatException("(Данный элемент -" + object + "- является не корректным.)");
        }
        return object;
    }

    /**
     * Проверяет идет ли данный объект за тем что уже проверен согласно ключу.
     *
     * @param last       корректный элемент
     * @param verifiable проверяемый элемент
     *                   Выдает IllegalArgumentException если объект идет не по порядку.
     */
    @Override
    public void catchUnsortedArrayByAscending(Object last, Object verifiable) {
        if (key.equals("-a")) {
            if (Integer.parseInt((String) last) > Integer.parseInt((String) verifiable)) {
                throw new IllegalArgumentException("(Данный элемент - " + verifiable + " не отсортирован.)");
            }
        } else {
            if (Integer.parseInt((String) last) < Integer.parseInt((String) verifiable)) {
                throw new IllegalArgumentException("(Данный элемент - " + verifiable + " не отсортирован.)");
            }
        }
    }

    /**
     * Запись файла в выходной файл.
     *
     * @param object         записываемый объект.
     * @param bufferedWriter файл куда записывать
     * @throws IOException ошибки возникающие при записи.
     */
    @Override
    public void getWriteInFile(Object object, BufferedWriter bufferedWriter) throws IOException {
        bufferedWriter.write(String.valueOf(object));
        bufferedWriter.newLine();
    }
}
