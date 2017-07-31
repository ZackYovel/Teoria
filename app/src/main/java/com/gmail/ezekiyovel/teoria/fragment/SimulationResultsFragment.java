package com.gmail.ezekiyovel.teoria.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gmail.ezekiyovel.teoria.R;

public class SimulationResultsFragment extends Fragment{

    private static final String ARG_ITEM_COUNT = "SimulationResultsFragment.ARG_ITEM_COUNT";
    private static final String ARG_TIME_IS_UP = "SimulationResultsFragment.ARG_TIME_IS_UP";
    public static final int MAX_ERROR = 4;
    private int itemCount;
    private boolean timeIsUp;

    public static SimulationResultsFragment newInstance(int size, boolean timeIsUp) {
        SimulationResultsFragment fragment = new SimulationResultsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ITEM_COUNT, size);
        args.putBoolean(ARG_TIME_IS_UP, timeIsUp);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null){
            this.itemCount = args.getInt(ARG_ITEM_COUNT);
            this.timeIsUp = args.getBoolean(ARG_TIME_IS_UP);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_simulation_result, container, false);
        TextView tvMain = (TextView) view.findViewById(R.id.tvMain);
        if (itemCount <= MAX_ERROR && !timeIsUp){
            tvMain.setText(R.string.label_test_passed);
        } else {
            tvMain.setText(R.string.label_test_failed);
        }
        TextView tvErrorCount = (TextView) view.findViewById(R.id.tvErrorCount);
        tvErrorCount.setText(getString(R.string.label_error_count, itemCount));
        TextView tvTimeLimit = (TextView) view.findViewById(R.id.tvTimeLimit);
        tvTimeLimit.setText(timeIsUp ?
                getString(R.string.time_limit_passed) :
                getString(R.string.time_limit_not_passed));
        return view;
    }
}
