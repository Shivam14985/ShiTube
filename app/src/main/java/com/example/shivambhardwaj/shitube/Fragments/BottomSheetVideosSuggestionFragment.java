package com.example.shivambhardwaj.shitube.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.shivambhardwaj.shitube.Adapters.HistoryAdapter;
import com.example.shivambhardwaj.shitube.Models.VideoModel;
import com.example.shivambhardwaj.shitube.databinding.FragmentBottomSheetVideosSuggestionBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BottomSheetVideosSuggestionFragment extends BottomSheetFragment {
    FragmentBottomSheetVideosSuggestionBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBottomSheetVideosSuggestionBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();

        // Gets the data from View Video Activity
        Bundle bundle = getArguments();
        String postId = bundle.getString("mText");


        return view;
    }
}