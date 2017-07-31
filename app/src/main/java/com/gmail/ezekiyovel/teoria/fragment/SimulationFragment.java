package com.gmail.ezekiyovel.teoria.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.gmail.ezekiyovel.teoria.QuestionActivity;
import com.gmail.ezekiyovel.teoria.R;
import com.gmail.ezekiyovel.teoria.database.DataAccess;

import java.util.List;

public class SimulationFragment extends Fragment {

    public static final String SIMULATION_PREFERENCES = "simulationPreferences";
    public static final String PREF_SIM_TYPE = "SimulationFragment.PREF_SIM_TYPE";
    private static final int SIM_TYPE_FULL = R.id.rbFullSim;
    private static final int SIM_TYPE_CUSTOM = R.id.rbCustomSim;
    private static final String PREF_SELECTED_CATEGORY =
            "SimulationFragment.PREF_SELECTED_CATEGORY";
    private static final int SELECTED_CATEGORY_DEFAULT = 0;
    private static final String PREF_IS_TIMER_ON = "SimulationFragment.PREF_IS_TIMER_ON";
    private static final boolean IS_TIMER_ON_DEFAULT = true;
    private static final String PREF_MARK_RIGHT_ANSWERS =
            "SimulationFragment.PREF_MARK_RIGHT_ANSWERS";
    private static final boolean MARK_RIGHT_ANSWERS_DEFAULT = false;

    private Listener listener;
    private SharedPreferences simulationPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_simulation, container, false);

        TextView tvCategoryLabel = (TextView) view.findViewById(R.id.tvCategoryLabel);
        final Spinner spCategory = (Spinner) view.findViewById(R.id.spCategory);
        final CheckBox cbTimerOn = (CheckBox) view.findViewById(R.id.cbTimerOn);
        TextView tvTimerOnLabel = (TextView) view.findViewById(R.id.tvTimerOnLabel);
        final CheckBox cbMarkRightAnswer = (CheckBox) view.findViewById(R.id.cbMarkRightAnswer);
        TextView tvMarkRightAnswerLabel =
                (TextView) view.findViewById(R.id.tvMarkRightAnswerLabel);

        final View[] customSimulation = {
                tvCategoryLabel, spCategory, cbTimerOn, tvTimerOnLabel, cbMarkRightAnswer,
                tvMarkRightAnswerLabel
        };

        final RadioGroup rgSimType = (RadioGroup) view.findViewById(R.id.rgSimType);
        rgSimType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                enableViews(checkedId == R.id.rbCustomSim, customSimulation);
            }
        });
        rgSimType.check(rgSimType.getCheckedRadioButtonId());

        final List<String> categories = new DataAccess(getContext()).readCategories();
        categories.add(0, getString(R.string.label_all));
        spCategory.setAdapter(new ArrayAdapter<>(getContext(),
                android.R.layout.simple_list_item_1, categories));
        spCategory.setEnabled(rgSimType.getCheckedRadioButtonId() == R.id.rbCustomSim);

        simulationPreferences = getContext()
                .getSharedPreferences(SIMULATION_PREFERENCES, Context.MODE_PRIVATE);

        view.findViewById(R.id.btnStartSim).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveConfiguration(rgSimType, spCategory, cbTimerOn, cbMarkRightAnswer);
                startSimulation(rgSimType, categories, spCategory, cbTimerOn, cbMarkRightAnswer);
            }
        });

        view.findViewById(R.id.btnBackToTraining).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onTrainingRequested();
            }
        });

        initPreferences(rgSimType, spCategory, cbTimerOn, cbMarkRightAnswer);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            ViewCompat.setLayoutDirection(view, ViewCompat.LAYOUT_DIRECTION_RTL);
        }

        return view;
    }

    private void saveConfiguration(RadioGroup rgSimType, Spinner spCategory, CheckBox cbTimerOn,
                                   CheckBox cbMarkRightAnswer) {
        SharedPreferences.Editor editor = simulationPreferences.edit();
        editor.putInt(PREF_SIM_TYPE, rgSimType.getCheckedRadioButtonId());
        editor.putInt(PREF_SELECTED_CATEGORY, spCategory.getSelectedItemPosition());
        editor.putBoolean(PREF_IS_TIMER_ON, cbTimerOn.isChecked());
        editor.putBoolean(PREF_MARK_RIGHT_ANSWERS, cbMarkRightAnswer.isChecked());
        editor.apply();
    }

    private void initPreferences(RadioGroup rgSimType, Spinner spCategory, CheckBox cbTimerOn,
                                 CheckBox cbMarkRightAnswers) {
        int simType = simulationPreferences.getInt(PREF_SIM_TYPE, SIM_TYPE_FULL);
        rgSimType.check(simType);

        int selectedCategory = simulationPreferences.getInt(PREF_SELECTED_CATEGORY,
                SELECTED_CATEGORY_DEFAULT);
        spCategory.setSelection(selectedCategory);

        boolean isTimerOn = simulationPreferences.getBoolean(PREF_IS_TIMER_ON,
                IS_TIMER_ON_DEFAULT);
        cbTimerOn.setChecked(isTimerOn);

        boolean markRightAnswers = simulationPreferences.getBoolean(PREF_MARK_RIGHT_ANSWERS,
                MARK_RIGHT_ANSWERS_DEFAULT);
        cbMarkRightAnswers.setChecked(markRightAnswers);
    }

    private void startSimulation(RadioGroup rgSimType, List<String> categories, Spinner spCategory,
                                 CheckBox cbTimerOn, CheckBox cbMarkRightAnswer) {
        Intent intent = new Intent(getContext(), QuestionActivity.class);
        intent.putExtra(QuestionActivity.EXTRA_ACTIVITY_MODE,
                QuestionActivity.ACTIVITY_MODE_SIMULATION);
        intent.putExtra(QuestionActivity.EXTRA_SIMULATION_MODE,
                rgSimType.getCheckedRadioButtonId() == R.id.rbCustomSim ?
                        QuestionActivity.SIMULATION_MODE_CUSTOM :
                        QuestionActivity.SIMULATION_MODE_FULL);
        String category = categories.get(spCategory.getSelectedItemPosition());
        intent.putExtra(QuestionActivity.EXTRA_SIMULATION_CATEGORY, category);
        intent.putExtra(QuestionActivity.EXTRA_SIMULATION_TIME_LIMIT,
                cbTimerOn.isChecked());
        intent.putExtra(QuestionActivity.EXTRA_SIMULATION_MARK_RIGHT_ANSWERS,
                cbMarkRightAnswer.isChecked());
        startActivity(intent);
    }

    private void enableViews(boolean enabled, View[] views) {
        for (View view : views) {
            view.setEnabled(enabled);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Listener) {
            listener = (Listener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement " + Listener.class.getName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface Listener {
        void onTrainingRequested();
    }
}
