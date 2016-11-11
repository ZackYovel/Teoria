package com.apps.ezekiel.teoria.util;


import android.content.Context;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class PersistTimes implements Iterable<String> {
    public static final String FILE_NAME = "PersistTimes";

    private ArrayList<String> persistTimes;
    private Context context;

    public PersistTimes(Context context) {
        this.context = context;
    }

    /**
     * Get the last recorded persistence time (as timestamp).
     * <p/>
     * If no persist times found, returns null.
     *
     * @return the last time persistence was recorded, if a record exists, and null if not.
     */
    @Nullable
    public Long getLastPersist() {
        if (persistTimes == null) {
            readPersistTimes();
        }
        int size = persistTimes.size();
        if (persistTimes != null && size > 0) {
            return Long.parseLong(persistTimes.get(size - 1));
        } else {
            return null;
        }
    }

    public void recordPersistTime() {
        if (persistTimes == null) {
            readPersistTimes();
        }
        persistTimes.add(String.valueOf(System.currentTimeMillis()));
        writePersistTimes();
    }

    private void readPersistTimes() {
        File storage = new File(context.getFilesDir(), FILE_NAME);

        StringBuilder builder = new StringBuilder();

        // Try to read from file. If file not found, just work on an empty StringBuilder, and
        // as a result create an empty persistTimes list (this is okay because it's probably
        // the first time the app is running and the file didn't have a chance to be created yet)
        try {
            Scanner scanner = new Scanner(storage);
            while (scanner.hasNext()) {
                builder.append(scanner.next());
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(builder.toString());
        } catch (JSONException e) {
            // This means the file is corrupted.
            e.printStackTrace();
            jsonArray = new JSONArray();
        }
        int length = jsonArray.length();
        persistTimes = new ArrayList<>(length + 1);
        for (int i = 0; i < length; i++) {
            try {
                persistTimes.add(jsonArray.getString(i));
            } catch (JSONException e) {
                // This means jsonArray does not contain a String at index i (corruption again)
                e.printStackTrace();
            }
        }
    }

    private void writePersistTimes() {
        try {
            File storage = new File(context.getFilesDir(), FILE_NAME);
            PrintWriter writer = new PrintWriter(storage);

            JSONArray jsonArray = new JSONArray(persistTimes);

            writer.write(jsonArray.toString());
            writer.flush();
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void recordPersistence(Context context) {
        PersistTimes instance = new PersistTimes(context);
        instance.recordPersistTime();
    }

    @Override
    public Iterator<String> iterator() {
        readPersistTimes();
        return new Iterator<String>() {
            private int index;

            @Override
            public boolean hasNext() {
                return index < persistTimes.size();
            }

            @Override
            public String next() {
                return persistTimes.get(index++);
            }
        };
    }
}
