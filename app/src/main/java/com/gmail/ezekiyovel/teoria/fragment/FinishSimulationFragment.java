package com.gmail.ezekiyovel.teoria.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.ezekiyovel.teoria.R;

public class FinishSimulationFragment extends Fragment implements View.OnClickListener {

    private FragmentListener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finish_simulation, container, false);

        view.findViewById(R.id.btnFinishSim).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        listener.onSimulationFinished();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentListener) {
            listener = (FragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement FragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


    public interface FragmentListener{
        void onSimulationFinished();
    }
}
