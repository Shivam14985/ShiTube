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
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

import com.example.shivambhardwaj.shitube.Activities.CommonActivity;
import com.example.shivambhardwaj.shitube.Models.CreatersModel;
import com.example.shivambhardwaj.shitube.Models.VideoModel;
import com.example.shivambhardwaj.shitube.R;
import com.example.shivambhardwaj.shitube.databinding.FragmentPostVideoBinding;
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

public class PostVideoFragment extends Fragment {
    FragmentPostVideoBinding binding;
    Uri videoUri;
    Uri ThumnailUri;
    ExoPlayer mediaController;
    StorageReference storageReference;
    DatabaseReference databaseReference;
    ArrayAdapter<String> adapter;
    VideoModel videoModel = new VideoModel();
    String[] items = {"Autos and Vehicles", "Comedy", "Education", "Entertainment", "Films and Animation", "Gaming", "Howto & Style", "News & Politics", "Nonprofits & Activism", "Music", "People & Blogs", "Pets & Animals", "Science & Technology", "Sports", "Travel & Events"};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPostVideoBinding.inflate(inflater, container, false);
        View view = binding.getRoot();


        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("Videos");

        adapter = new ArrayAdapter<String>(getContext(), R.layout.professions, items);
        binding.EtCategory.setAdapter(adapter);

        mediaController = new ExoPlayer.Builder(getContext()).build();
        binding.videoView.setPlayer(mediaController);

        FirebaseDatabase.getInstance().getReference().child("Creaters").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    CreatersModel creatersModel = snapshot.getValue(CreatersModel.class);
                    if (creatersModel.getAdmin().equals("true")){
                        binding.approval.setText("true");
                    }if (creatersModel.getAdmin().equals("false")){
                        binding.approval.setText("Pending");
                    }
                    else {}
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                mediaController.stop();
                binding.videoView.setVisibility(View.GONE);
                binding.upload.setVisibility(View.GONE);
                binding.browse.setVisibility(View.GONE);
                binding.scrollView.setVisibility(View.VISIBLE);

            }
        });
        binding.UplaodToFirebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Title = binding.editTextText.getText().toString();
                String description = binding.Description.getText().toString();
                String category = binding.EtCategory.getText().toString();
                if (category.isEmpty() || Title.isEmpty() || description.isEmpty()) {
                    Snackbar snackbar = Snackbar.make(view, "fill all the fields", Snackbar.LENGTH_LONG);
                    snackbar.setBackgroundTint(Color.rgb(233, 233, 233));
                    snackbar.setTextColor(Color.rgb(0, 0, 0));
                    snackbar.show();
                }
//                if (Title.isEmpty()) {
//                    Snackbar snackbar = Snackbar.make(view, "Fill Title", Snackbar.LENGTH_LONG);
//                    snackbar.setBackgroundTint(Color.rgb(233, 233, 233));
//                    snackbar.setTextColor(Color.rgb(0, 0, 0));
//                    snackbar.show();
//                    binding.UplaodToFirebase.setEnabled(false);
//                }
//                if (description.isEmpty()) {
//                    Snackbar snackbar = Snackbar.make(view, "Fill Description", Snackbar.LENGTH_LONG);
//                    snackbar.setBackgroundTint(Color.rgb(233, 233, 233));
//                    snackbar.setTextColor(Color.rgb(0, 0, 0));
//                    snackbar.show();
//                    binding.UplaodToFirebase.setEnabled(false);
//                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("Creaters").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                CreatersModel creatersModel = snapshot.getValue(CreatersModel.class);
                                String ChannelName = creatersModel.getName();
//                                final ProgressDialog dialog = new ProgressDialog(getContext());
//                                dialog.setTitle("Uploading....");
//                                dialog.show();
                                binding.ProgressLayout.setVisibility(View.VISIBLE);
                                binding.UplaodToFirebase.setEnabled(false);

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
                                StorageReference upload = storageReference.child("Videos/" + Title + "." + getExtension());
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
                                                        videoModel.setTitle(Title);
                                                        videoModel.setPostType("Video");
                                                        videoModel.setVideoDescription(description);
                                                        videoModel.setAddedAt(new Date().getTime());
                                                        videoModel.setAddedBy(FirebaseAuth.getInstance().getUid());
                                                        videoModel.setCategory(category);
                                                        videoModel.setSearchVideo(Name + " " + Title + " " + description + " " + category + " " + ChannelName);
                                                        videoModel.setAddedBy(FirebaseAuth.getInstance().getUid());
                                                        videoModel.setApproved(binding.approval.getText().toString());

                                                        databaseReference.push().setValue(videoModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                FirebaseDatabase.getInstance().getReference().child("Creaters").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                        if (snapshot.exists()){
                                                                            CreatersModel creatersModel = snapshot.getValue(CreatersModel.class);
                                                                            if (creatersModel.getAdmin().equals("true")){
                                                                                Snackbar snackbar= Snackbar.make(view, "Video Uploaded", Snackbar.LENGTH_LONG);
                                                                                snackbar.setBackgroundTint(Color.rgb(5, 146, 18));
                                                                                snackbar.setTextColor(Color.rgb(252, 252, 252));
                                                                                snackbar.show();
                                                                            }if (creatersModel.getAdmin().equals("false")){
                                                                                Snackbar snackbar= Snackbar.make(view, "Review Pending", Snackbar.LENGTH_INDEFINITE);
                                                                                snackbar.setBackgroundTint(Color.rgb(200, 0, 54));
                                                                                snackbar.setTextColor(Color.rgb(255, 255, 255));
                                                                                snackbar.setActionTextColor(Color.rgb(255, 255, 255));
                                                                                snackbar.setAction("Know", new View.OnClickListener() {
                                                                                    @Override
                                                                                    public void onClick(View v) {
                                                                                        Snackbar snackbar= Snackbar.make(view, "your video will be reviewed by admin", Snackbar.LENGTH_LONG);
                                                                                        snackbar.setBackgroundTint(Color.rgb(117, 134, 148));
                                                                                        snackbar.setTextColor(Color.rgb(252, 252, 252));
                                                                                        snackbar.show();
                                                                                    }
                                                                                });
                                                                                snackbar.show();
                                                                            }
                                                                            else {}
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                                    }
                                                                });
//                                                                dialog.dismiss();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
//                                                                dialog.dismiss();
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
                                        int percentage = (int) ((100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount());
//                                        dialog.setMessage("Uploaded:" + (int) percentage + "%");
                                        binding.progresspercent.setText(percentage +"%");
                                        binding.progressbar.setProgress(percentage);
                                    }
                                });
                            } else {
                                Snackbar snackbar = Snackbar.make(view, "You can't Upload videos", Snackbar.LENGTH_LONG).setAction("Become Creater", new View.OnClickListener() {
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
                    Snackbar snackbar = Snackbar.make(view, "You can't Upload videos", Snackbar.LENGTH_LONG).setAction("Become Creater", new View.OnClickListener() {
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
    public void onPause() {
        super.onPause();
        mediaController.pause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mediaController.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mediaController.play();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaController.release();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK) {
            if (data.getData() != null) {
                videoUri = data.getData();
                MediaItem mediaItem = MediaItem.fromUri(videoUri);
                mediaController.setMediaItem(mediaItem);
                mediaController.prepare();
                mediaController.play();
                mediaController.addListener(new Player.Listener() {
                    @Override
                    public void onPlaybackStateChanged(int playbackState) {
                        if (playbackState == Player.STATE_READY) {
                            long duration = mediaController.getDuration();
                            videoModel.setDuration(duration);
                        }
                    }
                });
                binding.upload.setVisibility(View.VISIBLE);
            } else {
                binding.upload.setVisibility(View.GONE);
            }
        }
        if (requestCode == 100 && resultCode == RESULT_OK) {
            if (data.getData() != null) {
                ThumnailUri = data.getData();
                binding.ThumnailImage.setVisibility(View.VISIBLE);
                binding.ThumnailImage.setImageURI(ThumnailUri);
                binding.UplaodToFirebase.setEnabled(true);
            }
        }
    }
}