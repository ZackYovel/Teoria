package com.gmail.ezekiyovel.teoria;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.LongSparseArray;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.gmail.ezekiyovel.teoria.database.DataAccess;
import com.gmail.ezekiyovel.teoria.entity.QuestionItem;
import com.gmail.ezekiyovel.teoria.fragment.FinishSimulationFragment;
import com.gmail.ezekiyovel.teoria.fragment.QuestionFragment;
import com.gmail.ezekiyovel.teoria.fragment.SimulationResultsFragment;

import org.joda.time.Duration;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QuestionActivity extends AppCompatActivity
        implements QuestionFragment.QuestionFragmentListener,
        FinishSimulationFragment.FragmentListener {

    /**
     * expects int values, enumerated
     */
    public static final String EXTRA_ACTIVITY_MODE = "QuestionActivity.EXTRA_ACTIVITY_MODE";
    public static final int ACTIVITY_MODE_TRAINING = 0;
    public static final int ACTIVITY_MODE_SIMULATION = 1;
    public static final int ACTIVITY_MODE_DEFAULT = ACTIVITY_MODE_TRAINING;

    /**
     * expects string values, dynamic
     */
    public static final String EXTRA_TRAINING_CATEGORY =
            "QuestionActivity.EXTRA_TRAINING_CATEGORY";
    public static final String CATEGORY_ALL = "הכל";

    /**
     * expects int values, enumerated
     */
    public static final String EXTRA_SIMULATION_MODE = "QuestionActivity.EXTRA_SIMULATION_MODE";
    public static final int SIMULATION_MODE_FULL = 0;
    public static final int SIMULATION_MODE_CUSTOM = 1;
    public static final int SIMULATION_MODE_DEFAULT = SIMULATION_MODE_FULL;

    /**
     * expects string values, dynamic
     */
    public static final String EXTRA_SIMULATION_CATEGORY =
            "QuestionActivity.EXTRA_SIMULATION_CATEGORY";

    /**
     * expects boolean values
     */
    public static final String EXTRA_SIMULATION_TIME_LIMIT =
            "QuestionActivity.EXTRA_SIMULATION_TIME_LIMIT";
    public static final boolean SIMULATION_TIME_LIMIT_DEFAULT = true;

    /**
     * expects boolean values
     */
    public static final String EXTRA_SIMULATION_MARK_RIGHT_ANSWERS =
            "QuestionActivity.EXTRA_SIMULATION_MARK_RIGHT_ANSWERS";
    public static final boolean SIMULATION_MARK_RIGHT_ANSWERS_DEFAULT = false;

    private static final long TEST_DURATION_MS = 2400000;  // test duration is 40 minutes
    private static final long COUNT_DOWN_INTERVAL = 1000;  // update each 1 second
    public static final int TEST_QUESTIONS = 30;
    private static final String TAG = "QuestionActivity";

    private int activityMode;
    private String trainingCategory;
    private int simulationMode;
    private boolean useTimeLimit;
    private boolean useMarkRightAnswer;
    private long licenceClass;

    private ViewPager viewPager;
    private TextView tvCountDownTimer;
    private DataAccess dataAccess;
    private LongSparseArray<QuestionFragment.State> fragmentState;
    private String simulationCategory;
    private HashMap<Long, QuestionItem> wrongAnsweredQuestions;
    private boolean timeIsUp;
    private boolean isDisplayWrongAnswerMode;


    @Override
    public void onSimulationFinished() {
        isDisplayWrongAnswerMode = true;
        tvCountDownTimer.setVisibility(View.GONE);
        viewPager.setAdapter(new QuestionPagerAdapter(
                new ArrayList<>(wrongAnsweredQuestions.values())));
        viewPager.getAdapter().notifyDataSetChanged();
    }

    @SuppressLint("UseSparseArrays")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        Bundle extras = getIntent().getExtras();
        this.activityMode = extras.getInt(EXTRA_ACTIVITY_MODE, ACTIVITY_MODE_DEFAULT);
        this.trainingCategory = extras.getString(EXTRA_TRAINING_CATEGORY);
        this.simulationMode = extras.getInt(EXTRA_SIMULATION_MODE, SIMULATION_MODE_DEFAULT);
        this.simulationCategory = extras.getString(EXTRA_SIMULATION_CATEGORY);
        this.useTimeLimit = extras.getBoolean(EXTRA_SIMULATION_TIME_LIMIT);
        this.useMarkRightAnswer = extras.getBoolean(EXTRA_SIMULATION_MARK_RIGHT_ANSWERS);

        SharedPreferences defaultPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.licenceClass = QuestionItem.binClassForStrClass(defaultPreferences
                .getString(SettingsActivity.LICENCE_CLASS,
                        SettingsActivity.DEFAULT_LICENCE_CLASS));

        this.fragmentState = new LongSparseArray<>();
        this.wrongAnsweredQuestions = new HashMap<>();

        this.viewPager = (ViewPager) findViewById(R.id.viewPager);
        initPager();
        this.tvCountDownTimer = (TextView) findViewById(R.id.tvCountDownTimer);
        initCountDownTimer();
    }

    private void initCountDownTimer() {
        if (activityMode == ACTIVITY_MODE_TRAINING) {
            tvCountDownTimer.setVisibility(View.GONE);
        } else if (simulationMode == SIMULATION_MODE_FULL || useTimeLimit) {
            tvCountDownTimer.setVisibility(View.VISIBLE);
            final PeriodFormatter formatter = new PeriodFormatterBuilder()
                    .appendMinutes()
                    .appendSuffix(":")
                    .appendSeconds()
                    .toFormatter();
            new CountDownTimer(TEST_DURATION_MS, COUNT_DOWN_INTERVAL) {

                @Override
                public void onTick(long millisUntilFinished) {
                    tvCountDownTimer.setText(
                            formatter.print(new Duration(millisUntilFinished).toPeriod())
                    );
                }

                @Override
                public void onFinish() {
                    timeIsUp = true;
                    tvCountDownTimer.setTextColor(ContextCompat.getColor(
                            QuestionActivity.this, R.color.colorWrong
                    ));
                }
            }.start();
        }
    }

    private void initPager() {
        dataAccess = new DataAccess(this);

        boolean allCategories = isAllCategories();

        List<QuestionItem> items = null;
        int allQuestions = 0;


        if (allCategories && activityMode == ACTIVITY_MODE_TRAINING) {
            items = dataAccess.getNewQuestionsForClass(licenceClass);
            allQuestions = dataAccess.getNumQuestionsForClass(licenceClass);
        } else if (allCategories && activityMode == ACTIVITY_MODE_SIMULATION) {
            items = dataAccess.getAllQuestionsForClassUpTo(licenceClass, TEST_QUESTIONS);
        } else if (!allCategories && activityMode == ACTIVITY_MODE_TRAINING) {
            items = dataAccess.getNewQuestionsFromCategoryForClass(licenceClass, trainingCategory);
            allQuestions = dataAccess.getNumQuestionsFromCategoryForClass(
                    licenceClass, trainingCategory
            );
        } else if (!allCategories && activityMode == ACTIVITY_MODE_SIMULATION) {
            items = dataAccess.getQuestionsFromCategoryForClassUpTo(
                    licenceClass, simulationCategory, TEST_QUESTIONS
            );
        } else {
            Log.wtf(TAG, "no questions found in db!!!");
            return;
        }

        final int numAllQuestions = allQuestions;
        final int numItemsViewed = allQuestions - items.size();

        viewPager.setAdapter(new QuestionPagerAdapter(items));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(
                    int position, float positionOffset, int positionOffsetPixels
            ) {
                int size = numAllQuestions - (activityMode == ACTIVITY_MODE_SIMULATION ? 1 : 0);
                if ((position + 1) <= size) {
                    setTitle("שאלה " + (numItemsViewed + position + 1) + "/" + size);
                } else {
                    setTitle(R.string.label_end);
                }
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * Analise mode booleans and determine if all categories are chosen on not.
     *
     * @return true if questions should be of all categories, false if not
     */
    private boolean isAllCategories() {
        return (activityMode == ACTIVITY_MODE_SIMULATION &&
                (simulationMode == SIMULATION_MODE_FULL ||
                        CATEGORY_ALL.equals(simulationCategory))) ||

                (activityMode == ACTIVITY_MODE_TRAINING &&
                        CATEGORY_ALL.equals(trainingCategory));
    }

    @Override
    public void onStateUpdated(long questionId, QuestionFragment.State state) {
        fragmentState.put(questionId, state);
    }

    @Override
    public void onCorrectAnswer(long questionId, int numberOfAttempts, int displayCount) {
        if (wrongAnsweredQuestions.containsKey(questionId)) {
            wrongAnsweredQuestions.remove(questionId);
        }
        dataAccess.setCountersForQuestion(questionId, numberOfAttempts, displayCount);
    }

    @Override
    public void onWrongAnswer(QuestionItem questionItem, int optionRealIndex) {
        wrongAnsweredQuestions.put(questionItem.getId(), questionItem);
    }

    private class QuestionPagerAdapter extends FragmentStatePagerAdapter {

        private final List<QuestionItem> items;

        public QuestionPagerAdapter(List<QuestionItem> items) {
            super(getSupportFragmentManager());
            this.items = items;
        }

        @Override
        public Fragment getItem(int position) {
            boolean indexInBound = position < items.size();
            if (indexInBound && isDisplayWrongAnswerMode) {
                QuestionItem questionItem = items.get(position);
                return QuestionFragment.newInstance(questionItem, true, true,
                        fragmentState.get(questionItem.getId()));
            } else if (indexInBound) {
                QuestionItem questionItem = items.get(position);
                return QuestionFragment.newInstance(questionItem,
                        activityMode == ACTIVITY_MODE_TRAINING ||
                                simulationMode == SIMULATION_MODE_CUSTOM && useMarkRightAnswer,
                        fragmentState.get(questionItem.getId()));
            } else if (!isDisplayWrongAnswerMode) {
                return new FinishSimulationFragment();
            } else {
                return SimulationResultsFragment.newInstance(items.size(), timeIsUp);
            }
        }

        @Override
        public int getCount() {
            return items.size() +
                    (activityMode == ACTIVITY_MODE_SIMULATION ? 1 : 0);
        }
    }
}