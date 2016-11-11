package com.apps.ezekiel.teoria.util;

import java.util.Random;

public class Functions {

    public static void fisherYates_shuffleArray(Object[] ar, int start, int end) {
        Random rnd = new Random();
        for (int i = end - 1; i > start; i--) {
            int index = rnd.nextInt(i + 1);
            Object a = ar[index];
            ar[index] = ar[i];
            ar[i] = a;
        }
    }

    public static void fisherYates_shuffleArray(Object[] ar) {
        fisherYates_shuffleArray(ar, 0, ar.length);
    }
}
