package com.example.shivambhardwaj.shitube.Activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.shivambhardwaj.shitube.Adapters.HistoryAdapter;
import com.example.shivambhardwaj.shitube.Adapters.HomeAdapter;
import com.example.shivambhardwaj.shitube.Models.CreatersModel;
import com.example.shivambhardwaj.shitube.Models.UsersModel;
import com.example.shivambhardwaj.shitube.Models.VideoModel;
import com.example.shivambhardwaj.shitube.R;
import com.example.shivambhardwaj.shitube.Services.NetworkBroadcast;
import com.example.shivambhardwaj.shitube.databinding.ActivityCommonBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class CommonActivity extends AppCompatActivity {
    ActivityCommonBinding binding;
    FirebaseDatabase database;
    FirebaseStorage storage;
    Uri uri;
    Uri backImageUri;
    private BroadcastReceiver broadcastReceiver;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCommonBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();
        Intent intent = getIntent();
        String value = intent.getStringExtra("data");
        broadcastReceiver = new NetworkBroadcast();
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        if (value.equals("Creater")) {
            binding.CreaterLayout.setVisibility(View.VISIBLE);
            binding.SelectProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, 100);
                }
            });
            binding.SelectBAclgrounImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, 102);
                }
            });
            binding.EnterWebsiteLink.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (Patterns.WEB_URL.matcher(binding.EnterWebsiteLink.getText().toString()).matches()) {
                    } else {
                        // otherwise show error of invalid url
                        binding.EnterWebsiteLink.setError("Invalid Url");
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            binding.EditFacebookLink.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (Patterns.WEB_URL.matcher(binding.EditFacebookLink.getText().toString()).matches()) {
                    } else {
                        // otherwise show error of invalid url
                        binding.EditFacebookLink.setError("Invalid Url");
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            binding.EnterTwitterLink.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (Patterns.WEB_URL.matcher(binding.EnterTwitterLink.getText().toString()).matches()) {
                    } else {
                        // otherwise show error of invalid url
                        binding.EnterTwitterLink.setError("Invalid Url");
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            binding.EnterInstagramLink.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (Patterns.WEB_URL.matcher(binding.EnterInstagramLink.getText().toString()).matches()) {
                    } else {
                        // otherwise show error of invalid url
                        binding.EnterInstagramLink.setError("Invalid Url");
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            binding.btnBecomeCreater.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProgressDialog progressDialog = new ProgressDialog(CommonActivity.this);
                    progressDialog.setMessage("Please Wait");
                    progressDialog.show();
                    CreatersModel model = new CreatersModel();
                    String Name = binding.EtName.getText().toString();
                    String UserName = binding.EtUserNAme.getText().toString();
                    String Description = binding.EnterChannelDescription.getText().toString();
                    String Instagram = binding.EnterInstagramLink.getText().toString();
                    String facebook = binding.EnterFacebookLink.getText().toString();
                    String Twitter = binding.EnterTwitterLink.getText().toString();
                    String Website = binding.EnterWebsiteLink.getText().toString();
                    if (Name.isEmpty()) {
                        Toast.makeText(CommonActivity.this, "Fill Name", Toast.LENGTH_SHORT).show();
                    }
                    if (UserName.isEmpty()) {
                        Toast.makeText(CommonActivity.this, "Fill User Name", Toast.LENGTH_SHORT).show();
                    }
                    if (uri == null) {
                        Toast.makeText(CommonActivity.this, "Select Image", Toast.LENGTH_SHORT).show();

                    }
                    if (!UserName.isEmpty() && !Name.isEmpty() && uri != null) {
                        StorageReference reference = FirebaseStorage.getInstance().getReference().child("Profile").child(FirebaseAuth.getInstance().getUid());
                        reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        if (backImageUri != null) {
                                            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("backgroundImage");
                                            storageReference.putFile(backImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                        @Override
                                                        public void onSuccess(Uri backgrounduri) {
                                                            model.setName(Name);
                                                            model.setUserName(UserName);
                                                            model.setProfileImage(uri.toString());
                                                            model.setAdmin("false");
                                                            model.setBackgroundImage(backgrounduri.toString());
                                                            model.setJoinedOn(new Date().getTime());
                                                            model.setSubscribersCount(0);
                                                            model.setChannelDescription(Description);
                                                            model.setInstagramLink(Instagram);
                                                            model.setTwitterLink(Twitter);
                                                            model.setWebsiteLink(Website);
                                                            model.setFacebookLink(facebook);
                                                            database.getReference().child("Creaters").child(FirebaseAuth.getInstance().getUid()).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    Toast.makeText(CommonActivity.this, "You are Creater now", Toast.LENGTH_SHORT).show();
                                                                    binding.AlreadyCreatersLAyout.setVisibility(View.VISIBLE);
                                                                    binding.BecomeCreaterLayout.setVisibility(View.GONE);
                                                                    progressDialog.dismiss();
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(CommonActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                                    progressDialog.dismiss();
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            });
                                        } else {
                                            model.setName(Name);
                                            model.setUserName(UserName);
                                            model.setProfileImage(uri.toString());
                                            model.setAdmin("false");
                                            model.setJoinedOn(new Date().getTime());
                                            model.setSubscribersCount(0);
                                            model.setChannelDescription(Description);
                                            model.setInstagramLink(Instagram);
                                            model.setTwitterLink(Twitter);
                                            model.setWebsiteLink(Website);
                                            model.setFacebookLink(facebook);
                                            database.getReference().child("Creaters").child(FirebaseAuth.getInstance().getUid()).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(CommonActivity.this, "You are Creater now", Toast.LENGTH_SHORT).show();
                                                    binding.AlreadyCreatersLAyout.setVisibility(View.VISIBLE);
                                                    binding.BecomeCreaterLayout.setVisibility(View.GONE);
                                                    progressDialog.dismiss();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(CommonActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                    progressDialog.dismiss();
                                                }
                                            });
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(CommonActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CommonActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
            binding.EtUserNAme.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String UserNAme = binding.EtUserNAme.getText().toString();
                    database.getReference().child("Creaters").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                CreatersModel creatersModel = snapshot1.getValue(CreatersModel.class);
                                String alreadyUserName = creatersModel.getUserName().toString();
                                if (UserNAme.equals(alreadyUserName)) {
                                    binding.EtUserNAme.setError("Username Already Exist");
                                    binding.btnBecomeCreater.setEnabled(false);
                                } else {
                                    binding.btnBecomeCreater.setEnabled(true);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    UsersModel model = snapshot.getValue(UsersModel.class);
                    binding.EtName.setText(model.getUserName());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            database.getReference().child("Creaters").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    CreatersModel model = snapshot.getValue(CreatersModel.class);
                    if (snapshot.exists()) {
                        binding.name.setText(model.getName());
                        binding.UserName.setText(model.getUserName());
                        Picasso.get().load(model.getProfileImage()).placeholder(R.drawable.profileuser).into(binding.profileuser);
                        Picasso.get().load(model.getBackgroundImage()).into(binding.creatersBackgroundImage);
                        binding.AlreadyCreatersLAyout.setVisibility(View.VISIBLE);
                        String subscribers = snapshot.child("subscribedBy").getChildrenCount() + "";
                        binding.totalSubscribers.setText(subscribers + " Subscribers");
                        if (model.getBackgroundImage() != null) {
                            binding.creatersBackgroundImage.setVisibility(View.VISIBLE);
                        } else {
                            binding.creatersBackgroundImage.setVisibility(View.GONE);
                        }
                    } else {
                        binding.BecomeCreaterLayout.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            ArrayList list = new ArrayList();
            HomeAdapter adapter = new HomeAdapter(list, this);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
            linearLayoutManager.setStackFromEnd(true);
            binding.myVideosrecyclerview.setAdapter(adapter);
            binding.myVideosrecyclerview.setLayoutManager(linearLayoutManager);
            database.getReference().child("Videos").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        VideoModel model = dataSnapshot.getValue(VideoModel.class);
                        String addedBy = model.getAddedBy();
                        String myId = FirebaseAuth.getInstance().getUid();
                        if (addedBy.equals(myId)) {
                            list.add(model);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            binding.AllVideos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.AllVideos.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
                    binding.AllVideos.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#000000")));
                    binding.ApprovedVideos.setTextColor(getApplicationContext().getResources().getColor(R.color.black));
                    binding.ApprovedVideos.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.RejectedVideos.setTextColor(getApplicationContext().getResources().getColor(R.color.black));
                    binding.RejectedVideos.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.PendingVideos.setTextColor(getApplicationContext().getResources().getColor(R.color.black));
                    binding.PendingVideos.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    database.getReference().child("Videos").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            list.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                VideoModel model = dataSnapshot.getValue(VideoModel.class);
                                String addedBy = model.getAddedBy();
                                String myId = FirebaseAuth.getInstance().getUid();
                                if (addedBy.equals(myId)) {
                                    list.add(model);
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
            binding.ApprovedVideos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.AllVideos.setTextColor(getApplicationContext().getResources().getColor(R.color.black));
                    binding.AllVideos.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.RejectedVideos.setTextColor(getApplicationContext().getResources().getColor(R.color.black));
                    binding.RejectedVideos.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.PendingVideos.setTextColor(getApplicationContext().getResources().getColor(R.color.black));
                    binding.PendingVideos.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.ApprovedVideos.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
                    binding.ApprovedVideos.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#000000")));
                    database.getReference().child("Videos").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            list.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                VideoModel model = dataSnapshot.getValue(VideoModel.class);
                                String addedBy = model.getAddedBy();
                                String myId = FirebaseAuth.getInstance().getUid();
                                if (addedBy.equals(myId)) {
                                    if (model.getApproved().equals("true")) {
                                        list.add(model);
                                    } else {
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
            binding.RejectedVideos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.AllVideos.setTextColor(getApplicationContext().getResources().getColor(R.color.black));
                    binding.AllVideos.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.ApprovedVideos.setTextColor(getApplicationContext().getResources().getColor(R.color.black));
                    binding.ApprovedVideos.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.PendingVideos.setTextColor(getApplicationContext().getResources().getColor(R.color.black));
                    binding.PendingVideos.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.RejectedVideos.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
                    binding.RejectedVideos.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#000000")));
                    database.getReference().child("Videos").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            list.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                VideoModel model = dataSnapshot.getValue(VideoModel.class);
                                String addedBy = model.getAddedBy();
                                String myId = FirebaseAuth.getInstance().getUid();
                                if (addedBy.equals(myId)) {
                                    if (model.getApproved().equals("Rejected")) {
                                        list.add(model);
                                    } else {
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
            binding.PendingVideos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.AllVideos.setTextColor(getApplicationContext().getResources().getColor(R.color.black));
                    binding.AllVideos.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.ApprovedVideos.setTextColor(getApplicationContext().getResources().getColor(R.color.black));
                    binding.ApprovedVideos.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.RejectedVideos.setTextColor(getApplicationContext().getResources().getColor(R.color.black));
                    binding.RejectedVideos.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECEC")));
                    binding.PendingVideos.setTextColor(getApplicationContext().getResources().getColor(R.color.white));
                    binding.PendingVideos.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#000000")));
                    database.getReference().child("Videos").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            list.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                VideoModel model = dataSnapshot.getValue(VideoModel.class);
                                String addedBy = model.getAddedBy();
                                String myId = FirebaseAuth.getInstance().getUid();
                                if (addedBy.equals(myId)) {
                                    if (model.getApproved().equals("Pending")) {
                                        list.add(model);
                                    } else {
                                    }
                                }
                            }
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            });
            binding.EditPRofile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(CommonActivity.this, CommonActivity.class);
                    intent1.putExtra("data", "Edit Profile");
                    startActivity(intent1);
                }
            });
        }
        if (value.equals("Edit Profile")) {
            binding.EDITEtUserNAme.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String UserNAme = binding.EtUserNAme.getText().toString();
                    database.getReference().child("Creaters").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                CreatersModel creatersModel = snapshot1.getValue(CreatersModel.class);
                                String alreadyUserName = creatersModel.getUserName().toString();
                                if (UserNAme.equals(alreadyUserName)) {
                                    binding.EtUserNAme.setError("Username Already Exist");
                                    binding.btnBecomeCreater.setEnabled(false);
                                } else {
                                    binding.btnBecomeCreater.setEnabled(true);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            binding.EditWebsiteLink.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (Patterns.WEB_URL.matcher(binding.EditWebsiteLink.getText().toString()).matches()) {
                    } else {
                        // otherwise show error of invalid url
                        binding.EditWebsiteLink.setError("Invalid Url");
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            binding.EditTwitterLink.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (Patterns.WEB_URL.matcher(binding.EditTwitterLink.getText().toString()).matches()) {
                    } else {
                        // otherwise show error of invalid url
                        binding.EditTwitterLink.setError("Invalid Url");
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            binding.EditInstagramLink.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (Patterns.WEB_URL.matcher(binding.EditInstagramLink.getText().toString()).matches()) {
                    } else {
                        // otherwise show error of invalid url
                        binding.EditInstagramLink.setError("Invalid Url");
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            binding.EditFacebookLink.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (Patterns.WEB_URL.matcher(binding.EditFacebookLink.getText().toString()).matches()) {
                    } else {
                        // otherwise show error of invalid url
                        binding.EditFacebookLink.setError("Invalid Url");
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            database.getReference().child("Creaters").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    CreatersModel creatersModel = snapshot.getValue(CreatersModel.class);
                    Picasso.get().load(creatersModel.getProfileImage()).placeholder(R.drawable.profileuser).into(binding.Changeprofile);
                    Picasso.get().load(creatersModel.getBackgroundImage()).into(binding.EDITBackgroundImage);
                    binding.EDITEtName.setText(creatersModel.getName());
                    binding.EDITEtUserNAme.setText(creatersModel.getUserName());
                    binding.EditChannelDescription.setText(creatersModel.getChannelDescription());
                    binding.EditInstagramLink.setText(creatersModel.getInstagramLink());
                    binding.EditWebsiteLink.setText(creatersModel.getWebsiteLink());
                    binding.EditTwitterLink.setText(creatersModel.getTwitterLink());
                    binding.EditFacebookLink.setText(creatersModel.getFacebookLink());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            binding.EditProfile.setVisibility(View.VISIBLE);
            binding.EDITSelectProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, 104);
                }
            });
            binding.EDITSelectBAclgrounImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, 105);
                }
            });
            binding.EDITbtnBecomeCreater.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProgressDialog progressDialog = new ProgressDialog(CommonActivity.this);
                    progressDialog.setMessage("Please Wait");
                    progressDialog.show();
                    if (uri != null) {
                        StorageReference reference = FirebaseStorage.getInstance().getReference().child("Profile").child(FirebaseAuth.getInstance().getUid());
                        reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        database.getReference().child("Creaters").child(FirebaseAuth.getInstance().getUid()).child("profileImage").setValue(uri.toString());
                                        }
                                });
                            }
                        });
                    }
                    if (backImageUri != null) {
                        StorageReference reference = FirebaseStorage.getInstance().getReference().child("backgroundImage").child(FirebaseAuth.getInstance().getUid());
                        reference.putFile(backImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        database.getReference().child("Creaters").child(FirebaseAuth.getInstance().getUid()).child("backgroundImage").setValue(uri.toString());
                                        }
                                });
                            }
                        });
                    }
                    database.getReference().child("Creaters").child(FirebaseAuth.getInstance().getUid()).child("name").setValue(binding.EDITEtName.getText().toString());
                    database.getReference().child("Creaters").child(FirebaseAuth.getInstance().getUid()).child("channelDescription").setValue(binding.EditChannelDescription.getText().toString());
                    database.getReference().child("Creaters").child(FirebaseAuth.getInstance().getUid()).child("instagramLink").setValue(binding.EditInstagramLink.getText().toString());
                    database.getReference().child("Creaters").child(FirebaseAuth.getInstance().getUid()).child("websiteLink").setValue(binding.EditWebsiteLink.getText().toString());
                    database.getReference().child("Creaters").child(FirebaseAuth.getInstance().getUid()).child("twitterLink").setValue(binding.EditTwitterLink.getText().toString());
                    database.getReference().child("Creaters").child(FirebaseAuth.getInstance().getUid()).child("facebookLink").setValue(binding.EditFacebookLink.getText().toString());
                    database.getReference().child("Creaters").child(FirebaseAuth.getInstance().getUid()).child("userName").setValue(binding.EDITEtUserNAme.getText().toString());
                    Toast.makeText(CommonActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }
        if (value.equals("moreHistory")) {
            binding.historylayout.setVisibility(View.VISIBLE);
            ArrayList list = new ArrayList();
            HistoryAdapter adapter = new HistoryAdapter(list, this);
            LinearLayoutManager linearLayoutManager1 = new GridLayoutManager(this, 2);
            binding.historyrecyclerview.setAdapter(adapter);
            binding.historyrecyclerview.setLayoutManager(linearLayoutManager1);
            database.getReference().child("Videos").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.child("viewedBy").child(FirebaseAuth.getInstance().getUid()).exists()) {
                            VideoModel model = dataSnapshot.getValue(VideoModel.class);
                            list.add(model);
                        } else {
                        }
                    }

                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            binding.backhistorybtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        if (value.equals("likedVideos")) {
            binding.LikedVideos.setVisibility(View.VISIBLE);
            ArrayList list = new ArrayList();
            HistoryAdapter adapter = new HistoryAdapter(list, this);
            LinearLayoutManager linearLayoutManager1 = new GridLayoutManager(this, 2);
            binding.LikedVideosrecyclerview.setAdapter(adapter);
            binding.LikedVideosrecyclerview.setLayoutManager(linearLayoutManager1);
            database.getReference().child("Videos").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.child("likedBy").child(FirebaseAuth.getInstance().getUid()).exists()) {
                            VideoModel model = dataSnapshot.getValue(VideoModel.class);
                            list.add(model);
                        } else {
                        }
                    }

                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            binding.backlikedbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        if (value.equals("watchLaterVideos")) {
            binding.watchLAterlayout.setVisibility(View.VISIBLE);
            ArrayList list = new ArrayList();
            HistoryAdapter adapter = new HistoryAdapter(list, this);
            LinearLayoutManager linearLayoutManager1 = new GridLayoutManager(this, 2);
            binding.watchLaterrecyclerview.setAdapter(adapter);
            binding.watchLaterrecyclerview.setLayoutManager(linearLayoutManager1);
            database.getReference().child("Videos").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.child("watchLater").child(FirebaseAuth.getInstance().getUid()).exists()) {
                            VideoModel model = dataSnapshot.getValue(VideoModel.class);
                            list.add(model);
                        } else {
                        }
                    }

                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            binding.backwatchlaterbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        if (value.equals("Review")) {
            binding.ReviewVideoLayout.setVisibility(View.VISIBLE);
            binding.backreviewbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            ArrayList<VideoModel> list = new ArrayList();
            HomeAdapter adapter = new HomeAdapter(list, this);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
            linearLayoutManager.setStackFromEnd(true);
            binding.ReviewVideosrecyclerview.setAdapter(adapter);
            binding.ReviewVideosrecyclerview.setLayoutManager(linearLayoutManager);
            database.getReference().child("Videos").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    list.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        VideoModel model = dataSnapshot.getValue(VideoModel.class);
                        String approved = dataSnapshot.child("approved").getValue().toString();
                        if (approved.equals("Pending")) {
                            list.add(model);
                            String id = dataSnapshot.getKey().toString();
                            database.getReference().child("Videos").child(id).child("postId").setValue(id);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            if (data.getData() != null) {
                uri = data.getData();
                binding.profile.setImageURI(uri);
                binding.btnBecomeCreater.setEnabled(true);
            }

        }
        if (requestCode == 102 && resultCode == RESULT_OK) {
            if (data.getData() != null) {
                backImageUri = data.getData();
                binding.BackgroundImage.setImageURI(backImageUri);
            }

        }
        if (requestCode == 104 && resultCode == RESULT_OK) {
            if (data.getData() != null) {
                uri = data.getData();
                binding.Changeprofile.setImageURI(uri);
            }

        }
        if (requestCode == 105 && resultCode == RESULT_OK) {
            if (data.getData() != null) {
                backImageUri = data.getData();
                binding.EDITBackgroundImage.setImageURI(backImageUri);
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}