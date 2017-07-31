package com.apps.ezekiel.teoria.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.apps.ezekiel.teoria.QuestionActivity;
import com.apps.ezekiel.teoria.R;
import com.apps.ezekiel.teoria.database.DataAccess;

import java.util.List;

/**
 * Displays a training menu.
 *
 * Training means covering all available question in a chosen category, or in all categories.
 * The menu allows choosing a category or all categories.
 */
public class TrainingFragment extends Fragment {

    private static final String PREF_IS_NOT_FIRST_USE =
            "TrainingFragment.PREF_IS_NOT_FIRST_USE";

    private Listener listener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_training, container, false);
        GridView gridView = (GridView) view.findViewById(R.id.gridView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 &&
                Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT){
            gridView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

        final List<String> categories = new DataAccess(getContext()).readCategories();
        categories.add(getString(R.string.label_all));
        categories.add(getString(R.string.label_goto_simulation));
        TrainingAdapter adapter =
                new TrainingAdapter(getContext(), R.layout.training_grid_item,
                        android.R.id.text1, categories);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == categories.size() - 1) {
                    listener.onSimulationRequested();
                } else {
                    startTraining(view);
                }
            }
        });

        return view;
    }

    private void startTraining(View view) {
        Intent intent = new Intent(getContext(), QuestionActivity.class);
        intent.putExtra(QuestionActivity.EXTRA_ACTIVITY_MODE,
                QuestionActivity.ACTIVITY_MODE_TRAINING);
        String category = ((TextView) view.findViewById(android.R.id.text1))
                .getText().toString();
        intent.putExtra(QuestionActivity.EXTRA_TRAINING_CATEGORY, category);
        startActivity(intent);
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
        void onSimulationRequested();
    }

    private class TrainingAdapter extends ArrayAdapter<String> {
        private final int count;
        private final int textViewResId;

        public TrainingAdapter(Context context, int layout_resid,
                               int textview_resid, List<String> items) {
            super(context, layout_resid, textview_resid, items);
            this.count = items.size();
            this.textViewResId = textview_resid;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;

            boolean isSimulationItem = (position == count - 1);

            if (isSimulationItem) {
                view = null;
            }

            ViewHolder holder = (view == null ?
                    null :
                    (ViewHolder) view.getTag());

            if (view == null) {
                int layoutResId = isSimulationItem ?
                        R.layout.grid_item_goto_sim :
                        R.layout.training_grid_item;

                view = LayoutInflater.from(getContext()).inflate(layoutResId, parent, false);
            }

            if (holder == null) {
                holder = new ViewHolder((TextView) view.findViewById(textViewResId));
                view.setTag(holder);
            }

            holder.textView.setText(getItem(position));

            return view;
        }

        private class ViewHolder {
            public final TextView textView;

            public ViewHolder(TextView textView) {
                this.textView = textView;
            }
        }
    }
}
