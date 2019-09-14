package ru.gorbunov.MergeSort.Intefaces;

import java.io.BufferedWriter;
import java.io.IOException;

public interface ISortingDifferentTypes {
    Object sorting(Object first, Object second);

    Object catchWrongFormat(Object object);

    void catchUnsortedArrayByAscending(Object last, Object verifiable);

    void getWriteInFile(Object object, BufferedWriter bufferedWriter) throws IOException;
}
