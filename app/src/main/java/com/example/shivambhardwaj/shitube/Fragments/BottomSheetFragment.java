package com.example.shivambhardwaj.shitube.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.shivambhardwaj.shitube.Adapters.CommentsAdapter;
import com.example.shivambhardwaj.shitube.Models.CommentsModel;
import com.example.shivambhardwaj.shitube.Models.CreatersModel;
import com.example.shivambhardwaj.shitube.Models.VideoModel;
import com.example.shivambhardwaj.shitube.R;
import com.example.shivambhardwaj.shitube.databinding.FragmentBottomSheetBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class BottomSheetFragment extends BottomSheetDialogFragment {
    FragmentBottomSheetBinding binding;
    FirebaseDatabase database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBottomSheetBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();
        database = FirebaseDatabase.getInstance();

        // Gets the data from View Video Activity
        Bundle bundle = getArguments();
        String message = bundle.getString("mText");
        String shorts = bundle.getString("shortComments");

        if (message != null) {
            ArrayList<CommentsModel> list = new ArrayList<>();
            CommentsAdapter adapter = new CommentsAdapter(list, getContext());
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
            linearLayoutManager.setStackFromEnd(true);
            binding.recyclerView.setLayoutManager(linearLayoutManager);
            binding.recyclerView.setAdapter(adapter);

            database.getReference().child("Videos").child(message).child("comments").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            CommentsModel commentsModel = dataSnapshot.getValue(CommentsModel.class);
                            list.add(commentsModel);
                        }
                        adapter.notifyDataSetChanged();
                    } else {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            binding.commentbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String comment = binding.commentEdittext.getText().toString();
                    if (comment.isEmpty()) {
                        Snackbar snackbar = Snackbar.make(view, "Please enter comment", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        snackbar.setActionTextColor(getResources().getColor(R.color.white));
                        snackbar.setBackgroundTint(getResources().getColor(R.color.red));
                        snackbar.setTextColor(getResources().getColor(R.color.white));
                        snackbar.setAction("Dismiss", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                            }
                        });
                    } else {
                        database.getReference().child("Videos").child(message).child("comments").push().addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                CommentsModel commentsModel = new CommentsModel();
                                commentsModel.setComment(comment);
                                commentsModel.setCommentedBy(FirebaseAuth.getInstance().getUid());
                                commentsModel.setCommentedAt(new Date().getTime());
                                database.getReference().child("Videos").child(message).child("comments").push().setValue(commentsModel);
                                database.getReference().child("Videos").child(message).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        VideoModel videoModel = snapshot.getValue(VideoModel.class);
                                        int comments = videoModel.getCommentsCount() + 1;
                                        database.getReference().child("Videos").child(message).child("commentsCount").setValue(comments);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        binding.commentEdittext.setText("");
                    }
                }
            });

            database.getReference().child("Creaters").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        CreatersModel creatersModel = snapshot.getValue(CreatersModel.class);
                        Picasso.get().load(creatersModel.getProfileImage()).placeholder(R.drawable.profileuser).into(binding.profileImage);
                    }else {
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        if (shorts != null) {
            ArrayList<CommentsModel> list = new ArrayList<>();
            CommentsAdapter adapter = new CommentsAdapter(list, getContext());
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, true);
            linearLayoutManager.setStackFromEnd(true);
            binding.recyclerView.setLayoutManager(linearLayoutManager);
            binding.recyclerView.setAdapter(adapter);

            database.getReference().child("Shorts").child(shorts).child("comments").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        list.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            CommentsModel commentsModel = dataSnapshot.getValue(CommentsModel.class);
                            list.add(commentsModel);
                        }
                        adapter.notifyDataSetChanged();
                    } else {

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            binding.commentbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String comment = binding.commentEdittext.getText().toString();
                    if (comment.isEmpty()) {
                        Snackbar snackbar = Snackbar.make(view, "Enter comment", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                        snackbar.setActionTextColor(getResources().getColor(R.color.white));
                        snackbar.setBackgroundTint(getResources().getColor(R.color.red));
                        snackbar.setTextColor(getResources().getColor(R.color.white));
                        snackbar.setAction("Dismiss", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackbar.dismiss();
                            }
                        });
                    } else {
                        database.getReference().child("Shorts").child(shorts).child("comments").push().addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                CommentsModel commentsModel = new CommentsModel();
                                commentsModel.setComment(comment);
                                commentsModel.setCommentedBy(FirebaseAuth.getInstance().getUid());
                                commentsModel.setCommentedAt(new Date().getTime());
                                database.getReference().child("Shorts").child(shorts).child("comments").push().setValue(commentsModel);
                                database.getReference().child("Shorts").child(shorts).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        VideoModel videoModel = snapshot.getValue(VideoModel.class);
                                        int comments = videoModel.getCommentsCount() + 1;
                                        database.getReference().child("Shorts").child(shorts).child("commentsCount").setValue(comments);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                        binding.commentEdittext.setText("");
                    }
                }
            });

            database.getReference().child("Creaters").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){CreatersModel creatersModel = snapshot.getValue(CreatersModel.class);
                    Picasso.get().load(creatersModel.getProfileImage()).placeholder(R.drawable.profileuser).into(binding.profileImage);
                }}

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        binding.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }
}