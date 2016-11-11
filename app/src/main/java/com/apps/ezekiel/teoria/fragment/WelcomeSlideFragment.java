package com.apps.ezekiel.teoria.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.apps.ezekiel.teoria.R;


public class WelcomeSlideFragment extends Fragment {

    private static final String SCREEN_ID = "WelcomeSlideFragment.SCREEN_ID";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ImageView imageView =
                (ImageView) inflater.inflate(R.layout.fragment_welcome_slide, container, false);
        int screenId = getArguments().getInt(SCREEN_ID);
        imageView.setImageResource(screenId);
        return imageView;
    }

    public static Fragment newInstance(int screen) {
        WelcomeSlideFragment result = new WelcomeSlideFragment();
        Bundle args = new Bundle(1);
        args.putInt(SCREEN_ID, screen);
        result.setArguments(args);
        return result;
    }
}
