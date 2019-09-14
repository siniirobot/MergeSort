package ru.gorbunov.MergeSort.Sorting;

import ru.gorbunov.MergeSort.Intefaces.ISortingDifferentTypes;

import java.io.BufferedWriter;
import java.io.IOException;

public class SortString implements ISortingDifferentTypes {
    private String key;

    /**
     * Сортировка строк.
     * Конструктор согласно которому выбирается тип сортировки.
     *
     * @param key -a сортировка по возрастанию -d сортировка по убыванию
     */
    SortString(String key) {
        this.key = key;
    }

    /**
     * Возвращает строку что короче или длинее согласно ключу.
     *
     * @param first  первый сравниваемый элемент
     * @param second второй сравниваемый элемент
     * @return сравниваемый элемент.
     */
    @Override
    public Object sorting(Object first, Object second) {
        if (key.equals("-a")) {
            return ((String) first).length() < ((String) second).length() ? first : second;
        } else {
            return ((String) first).length() > ((String) second).length() ? first : second;
        }
    }

    @Override
    public String catchWrongFormat(Object object) {
        return null;
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
            if (((String) last).length() > ((String) verifiable).length()) {
                throw new IllegalArgumentException("(Данный элемент - " + verifiable + " не отсортирован.)");
            }
        } else {
            if (((String) last).length() < ((String) verifiable).length()) {
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
        bufferedWriter.write((String) object);
        bufferedWriter.newLine();
    }
}
