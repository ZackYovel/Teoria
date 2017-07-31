package com.gmail.ezekiyovel.teoria.entity;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class QuestionItem implements Parcelable {
    public static final String CLASS_STR_A = "A";
    public static final String CLASS_STR_A1 = "A1";
    public static final String CLASS_STR_A2 = "A2";
    public static final String CLASS_STR_B = "B";
    public static final String CLASS_STR_C = "C";
    public static final String CLASS_STR_C1 = "C1";
    public static final String CLASS_STR_CPlusE = "C+E";
    public static final String CLASS_STR_D = "D";
    public static final String CLASS_STR_D1 = "D1";
    public static final String CLASS_STR_D3 = "D3";
    public static final String CLASS_STR_1 = "1";

    // Some time later, our hero realizes that there is no B class in Israeli driver's licenses,
    // and that we all live in a great lie, because the class is actually the Cyrillic letter VE.
    public static final String CLASS_STR_CYRILLIC_VE = "\u0412";

    public static final long CLASS_BIN_A = 0b1;
    public static final long CLASS_BIN_A1 = 0b10;
    public static final long CLASS_BIN_A2 = 0b100;
    public static final long CLASS_BIN_B = 0b1000;
    public static final long CLASS_BIN_C = 0b10000;
    public static final long CLASS_BIN_C1 = 0b100000;
    public static final long CLASS_BIN_CPlusE = 0b1000000;
    public static final long CLASS_BIN_D = 0b10000000;
    public static final long CLASS_BIN_D1 = 0b100000000;
    public static final long CLASS_BIN_D3 = 0b1000000000;
    public static final long CLASS_BIN_1 = 0b10000000000;

    private static final String TAG = "QuestionItem";

    private long id;
    private String title;
    private String[] options = new String[4];
    private int correctAnswerIndex;
    private String category;
    private String pubDate;
    private String image;
    private int answerAttempts;
    private int displayedCount;
    private long classes;

    public QuestionItem() {
    }

    public QuestionItem(long id, String title, String[] options, int correctAnswerIndex,
                        String category, String pubDate, String image, int answerAttempts,
                        int displayedCount, long classes) {
        this.id = id;
        this.title = title;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
        this.category = category;
        this.pubDate = pubDate;
        this.image = image;
        this.answerAttempts = answerAttempts;
        this.displayedCount = displayedCount;
        this.classes = classes;
    }

    public void addClass(String cls) {
        classes += binClassForStrClass(cls);
    }

    public static long binClassForStrClass(String strClass) {
        switch (strClass) {
            case CLASS_STR_A:
                return CLASS_BIN_A;
            case CLASS_STR_A1:
                return CLASS_BIN_A1;
            case CLASS_STR_A2:
                return CLASS_BIN_A2;
            case CLASS_STR_B:
            case CLASS_STR_CYRILLIC_VE:
                return CLASS_BIN_B;
            case CLASS_STR_C:
                return CLASS_BIN_C;
            case CLASS_STR_C1:
                return CLASS_BIN_C1;
            case CLASS_STR_CPlusE:
                return CLASS_BIN_CPlusE;
            case CLASS_STR_D:
                return CLASS_BIN_D;
            case CLASS_STR_D1:
                return CLASS_BIN_D1;
            case CLASS_STR_D3:
                return CLASS_BIN_D3;
            case CLASS_STR_1:
                return CLASS_BIN_1;
            default:
                Log.e(TAG, "Unmatched class string: " + strClass
                        + ":" + ((int) strClass.charAt(0)));
                return 0;
        }
    }

    public boolean isRelevantToClass(long classBin) {
        return (classes & classBin) != 0b0;
    }

    public long getClasses() {
        return classes;
    }

    public void setClasses(long classes) {
        this.classes = classes;
    }

    public int getAnswerAttempts() {
        return answerAttempts;
    }

    public void setAnswerAttempts(int answerAttempts) {
        this.answerAttempts = answerAttempts;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPubDate() {
        return pubDate;
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }

    public String getCorrectAnswer() {
        return options[correctAnswerIndex];
    }

    public void setCorrectAnswerIndex(int correctAnswerIndex) {
        this.correctAnswerIndex = correctAnswerIndex;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public int getDisplayedCount() {
        return displayedCount;
    }

    public void setDisplayedCount(int displayedCount) {
        this.displayedCount = displayedCount;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(title);
        dest.writeString(options[0]);
        dest.writeString(options[1]);
        dest.writeString(options[2]);
        dest.writeString(options[3]);
        dest.writeInt(correctAnswerIndex);
        dest.writeString(category);
        dest.writeString(pubDate);
        dest.writeString(image);
        dest.writeInt(answerAttempts);
        dest.writeInt(displayedCount);
        dest.writeLong(classes);
    }

    public static final Parcelable.Creator<QuestionItem> CREATOR
            = new Parcelable.Creator<QuestionItem>() {

        @Override
        public QuestionItem createFromParcel(Parcel source) {
            return new QuestionItem(
                    source.readLong(),
                    source.readString(),
                    new String[]{
                            source.readString(),
                            source.readString(),
                            source.readString(),
                            source.readString()
                    },
                    source.readInt(),
                    source.readString(),
                    source.readString(),
                    source.readString(),
                    source.readInt(),
                    source.readInt(),
                    source.readLong()
            );
        }

        @Override
        public QuestionItem[] newArray(int size) {
            return new QuestionItem[size];
        }
    };
}