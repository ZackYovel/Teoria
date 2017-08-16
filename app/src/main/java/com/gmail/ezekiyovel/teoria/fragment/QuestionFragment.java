package com.gmail.ezekiyovel.teoria.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.gmail.ezekiyovel.teoria.R;
import com.gmail.ezekiyovel.teoria.TeoriaApplication;
import com.gmail.ezekiyovel.teoria.entity.QuestionItem;
import com.gmail.ezekiyovel.teoria.util.Functions;

public class QuestionFragment extends Fragment {
    private static final String ARG_QUESTION_ITEM = "QuestionFragment.ARG_QUESTION_ITEM";
    private static final String ARG_MARK_RIGHT_WRONG = "QuestionFragment.ARG_MARK_RIGHT_WRONG";
    private static final String ARG_SAVED_STATE = "QuestionFragment.SAVED_STATE";
    private static final String ARG_IS_WRONG_ANSWER_DISPLAY =
            "QuestionFragment.IS_WRONG_ANSWER_DISPLAY";

    private QuestionItem questionItem;

    private QuestionFragmentListener mListener;

    private boolean markAsRightOrWrong;
    private View selectedView;
    private boolean[] clicked = new boolean[4];
    private int answerAttempts;
    private Option[] options;
    private boolean isWrongAnswerDisplay;

    public QuestionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * <p>
     * This is a short-hand for use when not displaying wrong answer.
     *
     * @param questionItem       the question to display.
     * @param markAsRightOrWrong
     * @param savedState
     * @return A new instance of fragment QuestionFragment.
     */
    public static QuestionFragment newInstance(QuestionItem questionItem,
                                               boolean markAsRightOrWrong,
                                               State savedState) {
        return newInstance(questionItem, markAsRightOrWrong, false, savedState);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * <p>
     * For most cases a short-hand of this method should be used.
     *
     * @param questionItem         the question to display.
     * @param markAsRightOrWrong   should an answer be marked as right or wrong.
     * @param isWrongAnswerDisplay is this a wrong answer display.
     * @param savedState           the saved state of this question display. If no state
     *                             exists send null.
     * @return A new instance of fragment QuestionFragment.
     */
    public static QuestionFragment newInstance(QuestionItem questionItem,
                                               boolean markAsRightOrWrong,
                                               boolean isWrongAnswerDisplay,
                                               @Nullable State savedState) {
        QuestionFragment fragment = new QuestionFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_QUESTION_ITEM, questionItem);
        args.putBoolean(ARG_MARK_RIGHT_WRONG, markAsRightOrWrong);
        args.putBoolean(ARG_IS_WRONG_ANSWER_DISPLAY, isWrongAnswerDisplay);
        args.putParcelable(ARG_SAVED_STATE, savedState);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            questionItem = arguments.getParcelable(ARG_QUESTION_ITEM);
            markAsRightOrWrong = arguments.getBoolean(ARG_MARK_RIGHT_WRONG);
            isWrongAnswerDisplay = arguments.getBoolean(ARG_IS_WRONG_ANSWER_DISPLAY);
            State savedState = arguments.getParcelable(ARG_SAVED_STATE);
            if (savedState != null) {
                clicked = savedState.clicked;
                options = savedState.options;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_question, container, false);
        initTitle(view);
        initImage(view);
        initOptions();
        initOptionViews(view);
//        ((TextView)view.findViewById(R.id.tvDetails)).setText("" + questionItem.getDisplayedCount());
        return view;
    }

    private void initTitle(View view) {
        TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvTitle.setText(questionItem.getTitle());
    }

    private void initImage(View view) {
        NetworkImageView networkImageView =
                (NetworkImageView) view.findViewById(R.id.networkImageView);

        if (questionItem.getImage() != null) {
            networkImageView.setVisibility(View.VISIBLE);
//            ImageLoader imageLoader = VolleySingleton
//                    .getInstance(getContext().getApplicationContext())
//                    .getImageLoader();
            ImageLoader imageLoader =
                    ((TeoriaApplication) getActivity().getApplication())
                            .getVolleySingleton().getImageLoader();
            networkImageView.setImageUrl(questionItem.getImage(), imageLoader);
        } else {
            networkImageView.setVisibility(View.GONE);
        }
    }

    private void initOptions() {
        if (options == null) {
            String[] rawOptions = questionItem.getOptions();
            options = new Option[rawOptions.length];
            for (int i = 0; i < rawOptions.length; i++) {
                options[i] = new Option(i, rawOptions[i]);
            }
            Functions.fisherYates_shuffleArray(options);
        }
    }

    private void initOptionViews(View view) {
        TextView tvOption1 = (TextView) view.findViewById(R.id.tvOption1);
        tvOption1.setText(options[0].text);

        TextView tvOption2 = (TextView) view.findViewById(R.id.tvOption2);
        tvOption2.setText(options[1].text);

        TextView tvOption3 = (TextView) view.findViewById(R.id.tvOption3);
        tvOption3.setText(options[2].text);

        TextView tvOption4 = (TextView) view.findViewById(R.id.tvOption4);
        tvOption4.setText(options[3].text);

        TextView[] tvOptions = {
                tvOption1, tvOption2, tvOption3, tvOption4
        };

        for (int i = 0; i < tvOptions.length; i++) {
            final int index = i;
            if (!isWrongAnswerDisplay) {
                tvOptions[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectOption(v, index);
                    }
                });
                if (clicked[i]) {
                    markAnswer(tvOptions[i], i);
                }
            } else {
                if (options[i].toString().equals(questionItem.getCorrectAnswer()) || clicked[i]) {
                    markAnswer(tvOptions[i], i);
                }
            }
        }
    }

    private void selectOption(View v, int i) {
        if (!clicked[i]) {
            if (!markAsRightOrWrong) {
                clicked = new boolean[4];
            }
            clicked[i] = true;
            answerAttempts++;
            mListener.onStateUpdated(questionItem.getId(), new State(clicked, options));
            if (isCorrectAnswer(i)) {
                mListener.onCorrectAnswer(
                        questionItem.getId(), answerAttempts, questionItem.getDisplayedCount() + 1
                );
            } else {
                mListener.onWrongAnswer(questionItem, i);
            }
            markAnswer(v, i);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof QuestionFragmentListener) {
            mListener = (QuestionFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement QuestionFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void markAnswer(View view, int position) {
        if (markAsRightOrWrong) {
            markAnswerCorrectness(position, view);
        } else {
            markAnswerAsSelected(view);
        }
    }

    private boolean isCorrectAnswer(int position) {
        return options[position].realIndex == questionItem.getCorrectAnswerIndex();
    }

    private void markAnswerAsSelected(View view) {
        if (selectedView != null) {
            selectedView.setBackgroundResource(0);
        }
        selectedView = view;
        int resId = R.drawable.border;
        selectedView.setBackgroundResource(resId);
    }

    private void markAnswerCorrectness(int position, View view) {
        selectedView = view;
        int background = isCorrectAnswer(position) ?
                R.drawable.background_green :
                R.drawable.background_red;
//        int icon = isCorrectAnswer(position) ?
//                R.drawable.ic_check :
//                R.drawable.ic_uncheck;
        selectedView.setBackgroundResource(background);
//        ((TextView)selectedView).setCompoundDrawablesWithIntrinsicBounds(
//                ContextCompat.getDrawable(getContext(), icon), null, null, null
//        );
    }

    private static class Option implements Parcelable {
        Option(int realIndex, String text) {
            this.realIndex = realIndex;
            this.text = text;
        }

        int realIndex;
        String text;

        @Override
        public String toString() {
            return text;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(realIndex);
            dest.writeString(text);
        }

        public static final Parcelable.Creator<Option> CREATOR
                = new Parcelable.Creator<Option>() {

            @Override
            public Option createFromParcel(Parcel source) {
                return new Option(source.readInt(), source.readString());
            }

            @Override
            public Option[] newArray(int size) {
                return new Option[size];
            }
        };
    }

    public interface QuestionFragmentListener {
        void onStateUpdated(long questionId, State state);

        void onCorrectAnswer(long questionId, int numberOfAttempts, int i);

        void onWrongAnswer(QuestionItem questionItem, int optionRealIndex);
    }

    public static class State implements Parcelable {
        boolean[] clicked = new boolean[4];
        Option[] options = new Option[4];

        State(boolean[] clicked, Option[] options) {
            this.clicked = clicked;
            this.options = options;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeBooleanArray(clicked);
            dest.writeTypedArray(options, Parcelable.PARCELABLE_WRITE_RETURN_VALUE);
        }

        public static final Parcelable.Creator<State> CREATOR
                = new Parcelable.Creator<State>() {

            @Override
            public State createFromParcel(Parcel source) {
                boolean[] clicked = new boolean[4];
                source.readBooleanArray(clicked);
                Option[] options = source.createTypedArray(Option.CREATOR);
                return new State(clicked, options);
            }

            @Override
            public State[] newArray(int size) {
                return new State[size];
            }
        };
    }
}