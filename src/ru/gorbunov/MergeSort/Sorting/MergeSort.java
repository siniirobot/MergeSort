package ru.gorbunov.MergeSort.Sorting;

import ru.gorbunov.MergeSort.Exeptions.EmptyFile;
import ru.gorbunov.MergeSort.Intefaces.ISortingDifferentTypes;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MergeSort {
    private String[] filePaths;
    private int i = 0;
    private String outputFile;
    private String temporaryName;
    private ISortingDifferentTypes iSorting;

    /**
     * Конструктор для создания входных и выходных данных сортировки.
     * Так же на этом этапе отсеиваются пустые файлы.
     *
     * @param keys       Ключи сортировки типа данных, -a - сортировка на возрастание, -d сортировка на убывание,
     *                   -i сортировка целых чисел, -s сортировка строк
     * @param outputFile по данному пути сохраняется результат сортировки.
     * @param filePaths  по данному пути берутся файлы для сортровки.
     */
    public MergeSort(String[] keys, String outputFile, String[] filePaths) {
        if (keys[1].equals("-i")) {
            this.iSorting = new SortInt(keys[0]);
        } else {
            this.iSorting = new SortString(keys[0]);
        }
        this.outputFile = outputFile;
        this.filePaths = new String[filePaths.length];
        int k = 0;

        for (String filePath : filePaths) {
            try {
                catchEmptyFile(filePath);
                this.filePaths[k] = filePath;
                k++;
            } catch (EmptyFile | IOException emptyFile) {
                System.out.println(emptyFile.getMessage());
            }
        }

        this.filePaths = Arrays.copyOf(this.filePaths, k);
    }

    /**
     * Процесс сортировки:
     * 1-Если входной файл всего один, то сразу же происходит его проверка на корректность и запись в выходной файл.
     * 2-Если входных файлов больше, то берется один файл копируется во временный файл, после чего происходит процесс
     * слияния и запись входных данных, затем во временный файл записываются данные что уже прошли слияние и
     * происходит процесс слияния нового файла с временным данный процесс повторяется до тех пор пока входные файлы
     * не кончатся.
     * 3-Если при сортировке целых чисел попадаются строчные, то слияния из этого файла прекращается и данные берутся
     * только из второго сливаемого файла до тех пор пока данные будут являтся целыми числами или файл не закончится.
     * 4-Если при сортировке как целых чисел так и строк попадутся данные что идут не по порядку то слияние из этого
     * файла так же прекратиться, данные будут браться из второго файла до тех пор пока данные будут
     * корректными или файлы не закончаться.
     */
    public void Merge() {
        for (; this.i < this.filePaths.length; this.i++) {
            if (this.filePaths.length == 1) {
                try (BufferedInputStream mergingFile = new BufferedInputStream(new FileInputStream(this.filePaths[this.i]));
                     BufferedWriter outputFile = new BufferedWriter(new FileWriter(this.outputFile))
                ) {
                    Object[] mergingArray = getArray(mergingFile);

                    try {
                        Object lastElement = this.iSorting.catchWrongFormat(mergingArray[0]);
                        iSorting.getWriteInFile(lastElement, outputFile);
                        outputFile.flush();
                        getCollectEndOfFile(lastElement, mergingArray, 1, outputFile, mergingFile, this.filePaths[this.i]);
                    } catch (IllegalArgumentException e) {
                        System.out.println(e.getMessage());
                    }
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            copyDataToTemporaryFile();

            try (BufferedInputStream mergingFile = new BufferedInputStream(new FileInputStream(this.filePaths[this.i]));
                 BufferedInputStream temporaryFile = new BufferedInputStream(new FileInputStream("temporaryFile.txt"));
                 BufferedWriter outputFile = new BufferedWriter(new FileWriter(this.outputFile, true))
            ) {
                Object[] mergingArray;
                Object[] temporaryArray;

                mergingArray = getArray(mergingFile);
                temporaryArray = getArray(temporaryFile);

                Object[] result = new Object[mergingArray.length + temporaryArray.length];

                int l = 0;
                int j = 0;
                int k = 1;
                Object lastElement;
                try {
                    if (mergingArray.length == 0 && temporaryArray.length == 0) {
                        throw new EmptyFile(this.temporaryName + " " + this.filePaths[this.i] + "(Являются пустыми)");
                    } else if (mergingArray.length == 0) {
                        throw new EmptyFile(this.filePaths[this.i] + "(Является пустым)");
                    } else if (temporaryArray.length == 0) {
                        throw new EmptyFile(this.temporaryName + "(Является пустым)");
                    }

                    if (this.iSorting.sorting(mergingArray[l], temporaryArray[j]).equals(temporaryArray[j])) {
                        result[0] = temporaryArray[j];
                        j++;
                    } else {
                        result[0] = mergingArray[l];
                        l++;
                    }

                    lastElement = result[0];
                    try {
                        for (; k < result.length; k++) {
                            if (j != temporaryArray.length && l != mergingArray.length) {
                                result[k] = this.iSorting.sorting(mergingArray[l], temporaryArray[j]);
                                catchWrongData(lastElement, result[k]);
                                lastElement = result[k];

                                if (result[k].equals(temporaryArray[j])) {
                                    j++;
                                } else {
                                    l++;
                                }
                            } else if (j < temporaryArray.length) {
                                for (int n = 0; n < k; n++) {
                                    this.iSorting.getWriteInFile(result[n], outputFile);
                                }
                                outputFile.flush();

                                if (mergingArray.length != 0) {
                                    mergingArray = getArray(mergingFile);
                                    result = new Object[result.length - k + mergingArray.length];
                                    l = 0;
                                    k = -1;
                                } else {
                                    k = 0;
                                    result = new Object[0];
                                    getCollectEndOfFile(lastElement, temporaryArray, j, outputFile, temporaryFile, this.temporaryName);
                                    break;
                                }
                            } else {
                                if (temporaryArray.length != 0) {
                                    temporaryArray = getArray(temporaryFile);
                                    result = Arrays.copyOf(result, result.length + temporaryArray.length);
                                    j = 0;
                                    k--;
                                } else {
                                    for (int n = 0; n < k; n++) {
                                        this.iSorting.getWriteInFile(result[n], outputFile);
                                    }
                                    outputFile.flush();
                                    k = 0;
                                    result = new Object[0];
                                    getCollectEndOfFile(lastElement, mergingArray, l, outputFile, mergingFile, this.filePaths[this.i]);
                                    break;
                                }
                            }
                        }
                    } catch (IllegalArgumentException e) {
                        for (int n = 0; n < k; n++) {
                            this.iSorting.getWriteInFile(result[n], outputFile);
                        }
                        outputFile.flush();

                        try {
                            catchWrongData(lastElement, temporaryArray[j]);
                            getCollectEndOfFile(lastElement, temporaryArray, j, outputFile, temporaryFile, this.temporaryName);
                            System.out.println(this.filePaths[this.i] + e.getMessage());
                        } catch (IllegalArgumentException e1) {
                            try {
                                catchWrongData(lastElement, mergingArray[l]);
                                getCollectEndOfFile(lastElement, mergingArray, l, outputFile, mergingFile, this.filePaths[this.i]);
                                System.out.println(this.temporaryName + e.getMessage());
                            } catch (IllegalArgumentException e2) {
                                System.out.println(this.temporaryName + e.getMessage());
                                System.out.println(this.filePaths[this.i] + e2.getMessage());
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    try {
                        this.iSorting.catchWrongFormat(temporaryArray[j]);
                        lastElement = temporaryArray[j];
                        this.iSorting.getWriteInFile(temporaryArray[j], outputFile);
                        j++;
                        getCollectEndOfFile(lastElement, temporaryArray, j, outputFile, temporaryFile, this.temporaryName);
                        System.out.println(this.filePaths[this.i] + e.getMessage());
                    } catch (NumberFormatException e1) {
                        try {
                            this.iSorting.catchWrongFormat(mergingArray[l]);
                            lastElement = mergingArray[l];
                            this.iSorting.getWriteInFile(mergingArray[l], outputFile);
                            l++;
                            getCollectEndOfFile(lastElement, mergingArray, l, outputFile, mergingFile, this.filePaths[i]);
                            System.out.println(this.temporaryName + e1.getMessage());
                        } catch (NumberFormatException e2) {
                            System.out.println(this.temporaryName + e1.getMessage());
                            System.out.println(this.filePaths[i] + e2.getMessage());
                        }
                    }
                } catch (EmptyFile emptyFile) {
                    System.out.println(emptyFile.getMessage());
                    if (mergingArray.length != 0 || temporaryArray.length != 0) {
                        if (mergingArray.length == 0) {
                            getCollectEndOfFile(temporaryArray[0], temporaryArray, 1, outputFile, temporaryFile, this.temporaryName);
                        } else {
                            getCollectEndOfFile(mergingArray[0], mergingArray, 1, outputFile, mergingFile, this.filePaths[this.i]);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        File temporaryFile = new File("temporaryFile.txt");
        temporaryFile.deleteOnExit();
    }

    /**
     * Проверяются данные на корректность.
     *
     * @param lastElement последний корректный элемент.
     * @param verifiable  проверяемый элемент.
     * @throws IllegalArgumentException исключение некорректных данных.
     */
    private void catchWrongData(Object lastElement, Object verifiable) throws IllegalArgumentException {
        this.iSorting.catchWrongFormat(verifiable);
        this.iSorting.catchUnsortedArrayByAscending(lastElement, verifiable);
    }

    /**
     * Происходит проверка и запись данных из одного файла в случае если второй файл оказался поврежденным или закончился
     *
     * @param lastElement         последний корректный элемент, для проверки правильной сортировки.
     * @param array               массив проверяемых данных
     * @param i                   место в массиве данных на котором прекратилось слияние данных
     * @param outputFile          куда записываются данные
     * @param bufferedInputStream откуда беруться следующие данные
     * @param fileName            наименование текущего файла, чтобы указать неправильный файл.
     * @throws IOException ошибки связанные с записью.
     */
    private void getCollectEndOfFile(Object lastElement, Object[] array, int i, BufferedWriter outputFile,
                                     BufferedInputStream bufferedInputStream, String fileName) throws IOException {
        while (array.length != 0) {
            for (; i < array.length; i++) {
                try {
                    catchWrongData(lastElement, array[i]);
                } catch (IllegalArgumentException e) {
                    System.out.println(fileName + e.getMessage());
                    outputFile.flush();
                    return;
                }
                this.iSorting.catchUnsortedArrayByAscending(lastElement, array[i]);
                lastElement = array[i];
                this.iSorting.getWriteInFile(array[i], outputFile);
            }
            outputFile.flush();
            array = getArray(bufferedInputStream);
            i = 0;
        }
    }

    /**
     * Процесс записи данных во временный файл:
     * 1-Если процесс слияния происходит впервые, то данные копируюся из первого файла по порядку, затем данные берутся
     * из выходного файла во временный файл.
     * 2-Если по каким то причинам выходной файл оказался пуст то данные берутся из следующего по порядку файла,затем
     * данные берутся из выходного файла во временный файл.
     */
    private void copyDataToTemporaryFile() {
        String path;
        if (i == 0) {
            path = this.filePaths[this.i];
            this.temporaryName = this.filePaths[this.i];
            i++;
        } else {
            path = this.outputFile;
            this.temporaryName = this.outputFile;
        }
        try (BufferedOutputStream temporaryFile = new BufferedOutputStream(new FileOutputStream("temporaryFile.txt"));
             BufferedInputStream outputFile = new BufferedInputStream(new FileInputStream(path))
        ) {
            catchEmptyFile(path);
            int read;
            byte[] res = new byte[1000000];
            while ((read = outputFile.read(res)) != -1) {
                temporaryFile.write(res, 0, read);
            }
            temporaryFile.flush();
            new BufferedWriter(new FileWriter(this.outputFile));
        } catch (FileNotFoundException | EmptyFile e) {
            System.out.println(e.getMessage());
            try (BufferedInputStream mergingFile = new BufferedInputStream(new FileInputStream(this.filePaths[this.i]));
                 BufferedOutputStream temporaryFile = new BufferedOutputStream(new FileOutputStream("temporaryFile.txt"))
            ) {
                new BufferedWriter(new FileWriter(this.outputFile));
                i++;
                int read;
                byte[] res = new byte[1000000];
                while ((read = mergingFile.read(res)) != -1) {
                    temporaryFile.write(res, 0, read);
                }
                temporaryFile.flush();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Поиск пустых файлов.
     *
     * @param path сам файл.
     * @throws IOException ошибки связанные с открытием файла.
     * @throws EmptyFile   ошибка пустого файла.
     */
    private void catchEmptyFile(String path) throws IOException, EmptyFile {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(path));
        if (bufferedInputStream.available() == 0) {
            throw new EmptyFile(path + "(Является пустым)");
        }
    }

    /**
     * Процесс получения фрагмента данных из файла.
     * Заполняем буффер данными из файла, полученные данные преобразуем в строку, которую разбиваем на массив строк по
     * символу переносу строки.
     * Если данные из буфера прервались на середине файла, то данные будут добираться до тех пор пока непоявится перенос
     * строки или данные не закончатся.
     *
     * @param bufferedInputStream файл
     * @return данные для слияния.
     */
    private Object[] getArray(BufferedInputStream bufferedInputStream) {
        Object[] array = new Object[0];
        try {
            if (bufferedInputStream.available() == 0) {
                return new Object[0];
            }

            byte[] res = new byte[1000000];
            int read = bufferedInputStream.read(res);
            res = Arrays.copyOf(res, read);
            StringBuilder line = new StringBuilder(new String(res, StandardCharsets.UTF_8));

            if (bufferedInputStream.available() != 0) {
                String lineSeparator = System.lineSeparator();
                String catchLineSeparator = line.substring(line.length() - lineSeparator.length(), line.length());

                while (!catchLineSeparator.equals(lineSeparator) && read > 0) {
                    res = new byte[1];
                    read = bufferedInputStream.read(res);
                    line.append(new String(res, StandardCharsets.UTF_8));
                    catchLineSeparator = line.substring(line.length() - lineSeparator.length(), line.length());
                }
            }

            array = line.toString().split(System.lineSeparator());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return array;
    }
}