package com.example.shivambhardwaj.shitube.Activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.shivambhardwaj.shitube.R;
import com.github.appintro.AppIntroFragment;
import com.github.appintro.AppIntroPageTransformerType;

public class AppIntro extends com.github.appintro.AppIntro {
    // we are calling on create method
    // to generate the view for our java file.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        // below line is use for creating first slide
        addSlide(AppIntroFragment.newInstance("C++", "C++ Self Paced Course",
                R.drawable.youtube, ContextCompat.getColor(getApplicationContext(), R.color.textColorGray)));

        // below line is for creating second slide.
        addSlide(AppIntroFragment.newInstance("DSA", "Data Structures and Algorithms", R.drawable.youtube,
                ContextCompat.getColor(getApplicationContext(), R.color.textColorGray)));

        // below line is for creating second slide.
        addSlide(AppIntroFragment.newInstance("DSA", "Data Structures and Algorithms", R.drawable.youtube,
                ContextCompat.getColor(getApplicationContext(), R.color.textColorGray)));
    }

    @Override
    protected void onSkipPressed(@Nullable Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
    }

    @Override
    protected void onDonePressed(@Nullable Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
    }
}
