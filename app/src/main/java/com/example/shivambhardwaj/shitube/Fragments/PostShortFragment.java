package com.example.shivambhardwaj.shitube.Fragments;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.shivambhardwaj.shitube.Activities.CommonActivity;
import com.example.shivambhardwaj.shitube.Models.VideoModel;
import com.example.shivambhardwaj.shitube.databinding.FragmentPostShortBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

public class PostShortFragment extends Fragment {
    FragmentPostShortBinding binding;
    Uri videoUri;
    Uri ThumnailUri;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    VideoModel videoModel = new VideoModel();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPostShortBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("Shorts");

        binding.videoView.start();
        binding.browse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("video/*");
                startActivityForResult(intent, 101);
            }
        });
        binding.upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.videoView.setVisibility(View.GONE);
                binding.upload.setVisibility(View.GONE);
                binding.browse.setVisibility(View.GONE);
                binding.scrollView.setVisibility(View.VISIBLE);
            }
        });
        binding.UplaodToFirebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.editTextText.getText().toString().isEmpty() || binding.Description.getText().toString().isEmpty()) {
                    Snackbar snackbar = Snackbar.make(view, "Enter Details", Snackbar.LENGTH_LONG);
                    snackbar.setBackgroundTint(Color.RED);
                    snackbar.setTextColor(Color.WHITE);
                    snackbar.setDuration(5000);
                    snackbar.show();
                } else {
                    FirebaseDatabase.getInstance().getReference().child("Creaters").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                final ProgressDialog dialog = new ProgressDialog(getContext());
                                dialog.setTitle("Uploading....");
                                dialog.show();

                                final StorageReference storageReference1 = FirebaseStorage.getInstance().getReference().child("Thumbnails").child(new Date().getTime() + "");
                                storageReference1.putFile(ThumnailUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                videoModel.setThumbnail(uri.toString());
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                                StorageReference upload = storageReference.child("Shorts/" + System.currentTimeMillis() + "." + getExtension());
                                upload.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        upload.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("userName").addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        String Name = snapshot.getValue().toString();
                                                        videoModel.setChannelName(Name);
                                                        videoModel.setVideo(uri.toString());
                                                        videoModel.setTitle(binding.editTextText.getText().toString());
                                                        videoModel.setPostType("Shorts");
                                                        videoModel.setViewsCount(0);
                                                        videoModel.setCommentsCount(0);
                                                        videoModel.setLikesCount(0);
                                                        videoModel.setVideoDescription(binding.Description.getText().toString());
                                                        videoModel.setAddedAt(new Date().getTime());
                                                        videoModel.setAddedBy(FirebaseAuth.getInstance().getUid());
                                                        videoModel.setSearchVideo(Name + " " + binding.editTextText.getText().toString() + " " + binding.Description.getText().toString());
                                                        videoModel.setAddedBy(FirebaseAuth.getInstance().getUid());
                                                        databaseReference.push().setValue(videoModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                Toast.makeText(getContext(), "Video Uploaded", Toast.LENGTH_SHORT).show();
                                                                dialog.dismiss();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                dialog.dismiss();
                                                                Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });

                                            }
                                        });
                                    }
                                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                                        float percentage = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                                        dialog.setMessage("Uploaded:" + (int) percentage + "%");
                                    }
                                });
                            } else {
                                Snackbar snackbar = Snackbar.make(view, "You can't Upload videos", Snackbar.LENGTH_LONG).setAction("Become Creater",
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent = new Intent(getContext(), CommonActivity.class);
                                                intent.putExtra("data", "Creater");
                                                startActivity(intent);
                                            }
                                        });
                                snackbar.setActionTextColor(Color.rgb(228, 8, 10));
                                snackbar.setBackgroundTint(Color.rgb(233, 233, 233));
                                snackbar.setTextColor(Color.rgb(0, 0, 0));
                                snackbar.setDuration(5000);
                                snackbar.show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
        binding.SelectThumnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 100);
            }
        });
        FirebaseDatabase.getInstance().getReference().child("Creaters").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                } else {
                    Snackbar snackbar = Snackbar.make(view, "You can't Upload videos", Snackbar.LENGTH_LONG).setAction("Become Creater",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(getContext(), CommonActivity.class);
                                    intent.putExtra("data", "Creater");
                                    startActivity(intent);
                                }
                            });
                    snackbar.setActionTextColor(Color.rgb(228, 8, 10));
                    snackbar.setBackgroundTint(Color.rgb(233, 233, 233));
                    snackbar.setTextColor(Color.rgb(0, 0, 0));
                    snackbar.setDuration(5000);
                    snackbar.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }

    public String getExtension() {
        ContentResolver contentResolver = getContext().getContentResolver();
        String mimeType = contentResolver.getType(videoUri);
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(mimeType);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK) {
            videoUri = data.getData();
            binding.videoView.setVideoURI(videoUri);
            binding.UplaodToFirebase.setEnabled(true);
            binding.videoView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    long duration = binding.videoView.getDuration();
                    if (duration < 60000) {
                        binding.upload.setVisibility(View.VISIBLE);
                        videoModel.setDuration(duration);
                    } else {
                        Snackbar snackbar = Snackbar.make(getView(), "Video Length is too long", Toast.LENGTH_LONG);
                        snackbar.setBackgroundTint(Color.rgb(255, 0, 0));
                        snackbar.setTextColor(Color.rgb(255, 255, 255));
                        snackbar.setDuration(5000);
                        snackbar.show();
                        binding.upload.setVisibility(View.GONE);
                    }
                }
            }, 500);
        }
        if (requestCode == 100 && resultCode == RESULT_OK) {
            if (data.getData() != null) {
                ThumnailUri = data.getData();
                binding.ThumnailImage.setVisibility(View.VISIBLE);
                binding.ThumnailImage.setImageURI(ThumnailUri);
            }
        }
    }
}