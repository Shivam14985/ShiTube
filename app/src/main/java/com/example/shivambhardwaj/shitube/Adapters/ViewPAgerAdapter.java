package com.example.shivambhardwaj.shitube.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.shivambhardwaj.shitube.Fragments.PostShortFragment;
import com.example.shivambhardwaj.shitube.Fragments.PostVideoFragment;

public class ViewPAgerAdapter extends FragmentStateAdapter {
    public ViewPAgerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PostVideoFragment();
            case 1:
                return new PostShortFragment();
            default:
                return new PostVideoFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
