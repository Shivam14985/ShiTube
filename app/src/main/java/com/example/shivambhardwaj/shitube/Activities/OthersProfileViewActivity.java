package com.example.shivambhardwaj.shitube.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.shivambhardwaj.shitube.Adapters.HomeAdapter;
import com.example.shivambhardwaj.shitube.Models.CreatersModel;
import com.example.shivambhardwaj.shitube.Models.NotificationModel;
import com.example.shivambhardwaj.shitube.Models.VideoModel;
import com.example.shivambhardwaj.shitube.R;
import com.example.shivambhardwaj.shitube.databinding.ActivityOthersProfileViewBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class OthersProfileViewActivity extends AppCompatActivity {
    ActivityOthersProfileViewBinding binding;
    FirebaseDatabase database;
    String dateTime;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOthersProfileViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        binding.LoadingEffect.startShimmer();
        Intent intent = getIntent();
        String value = intent.getStringExtra("channelId");
        if (value != null) {
            database.getReference().child("Creaters").child(value).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    CreatersModel model = snapshot.getValue(CreatersModel.class);
                    if (snapshot.exists()) {
                        binding.name.setText(model.getName());
                        binding.channelname.setText(model.getName());
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
                        if (snapshot.child("admin").getValue().toString().equals("true")){
                            binding.verifiedMark.setVisibility(View.VISIBLE);
                        }
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
                        if (addedBy.equals(value)) {
                            list.add(model);
                            binding.LoadingEffect.stopShimmer();
                            binding.LoadingEffect.setVisibility(View.GONE);
                        }
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            if (value.equals(FirebaseAuth.getInstance().getUid())) {
                binding.btnSubscribe.setVisibility(View.GONE);
            }
            //subscribe Button
            database.getReference().child("Creaters").child(value).child("subscribedBy").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        binding.btnSubscribe.setText("Subscibed");
                        binding.btnSubscribe.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                database.getReference().child("Creaters").child(value).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        CreatersModel creatersModel = snapshot.getValue(CreatersModel.class);
                                        int subscribers = creatersModel.getSubscribersCount() - 1;
                                        creatersModel.setSubscribersCount(subscribers);
                                        database.getReference().child("Creaters").child(value).child("subscribersCount").setValue(subscribers);
                                        database.getReference().child("Creaters").child(value).child("subscribedBy").child(FirebaseAuth.getInstance().getUid()).removeValue();
                                        binding.btnSubscribe.setText("Subscribe");
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                    } else {
                        binding.btnSubscribe.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                database.getReference().child("Creaters").child(value).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        CreatersModel creatersModel = snapshot.getValue(CreatersModel.class);
                                        int subscribers = creatersModel.getSubscribersCount() + 1;
                                        creatersModel.setSubscribersCount(subscribers);
                                        database.getReference().child("Creaters").child(value).child("subscribersCount").setValue(subscribers);
                                        database.getReference().child("Creaters").child(value).child("subscribedBy").child(FirebaseAuth.getInstance().getUid()).setValue("true");
                                        binding.btnSubscribe.setText("Subscribed");

                                        NotificationModel notificationModel = new NotificationModel();
                                        notificationModel.setNotificationType("Subscribers");
                                        notificationModel.setNotificationText("subscribed your channel.");
                                        notificationModel.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                        notificationModel.setNotificationOpened(false);
                                        notificationModel.setVideoId(value);
                                        notificationModel.setNotificationAt(new Date().getTime());
                                        database.getReference().child("Users").child(value).child("Notifications").push().setValue(notificationModel);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            binding.moreaboutchannellayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.channellayoutbackbutton.setVisibility(View.GONE);
                    binding.channellayout.setVisibility(View.GONE);
                    binding.morebacklayout.setVisibility(View.VISIBLE);
                    binding.moreaboutchanneldetails.setVisibility(View.VISIBLE);

                    Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/shitube-173fe.appspot.com/o/Icons%20For%20App%2Finternet.png?alt=media&token=fb524c41-b569-46ea-ba8a-b163e8ca8b6a").into(binding.WebsiteImage);
                    Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/shitube-173fe.appspot.com/o/Icons%20For%20App%2Finformation.png?alt=media&token=8b98a1fc-0210-4bb6-8069-170ff51d68a4").into(binding.JoinedOn);
//                    Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/shitube-173fe.appspot.com/o/Icons%20For%20App%2Fgrow-up.png?alt=media&token=8afd4e6c-9fde-4fcd-a618-13920979e6d8").into(binding.growth);
                    Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/shitube-173fe.appspot.com/o/Icons%20For%20App%2Finstagram.png?alt=media&token=6848c167-4e12-4840-8ad8-51e4b237118e").into(binding.InstagramImage);
                    Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/shitube-173fe.appspot.com/o/Icons%20For%20App%2Ftwitter.png?alt=media&token=c42789e9-f329-4ce7-8e12-6f47c9734ab0").into(binding.TwitterImage);
                    Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/shitube-173fe.appspot.com/o/Icons%20For%20App%2Ffacebook.png?alt=media&token=c3dba9b8-7a24-4f4e-b3fe-d77b00a31a3b").into(binding.facebookImage);
                    database.getReference().child("Creaters").child(value).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            CreatersModel model = snapshot.getValue(CreatersModel.class);
                            binding.Description.setText(model.getChannelDescription());
                            binding.WebsiteLink.setText(model.getWebsiteLink());
                            binding.morechannelname.setText(model.getName());
                            binding.facebookLink.setText(model.getFacebookLink());
                            binding.InstagramLink.setText(model.getInstagramLink());
                            binding.TwitterLink.setText(model.getTwitterLink());


                            calendar = Calendar.getInstance();
                            calendar.setTimeInMillis(model.getJoinedOn());
                            simpleDateFormat = new SimpleDateFormat("dd MMM yyyy");
                            dateTime = simpleDateFormat.format(calendar.getTime());
                            binding.JoinedOnText.setText(dateTime);

                            binding.WebsiteLinkLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Uri uri = Uri.parse(model.getWebsiteLink());
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(String.valueOf(uri)));
                                    startActivity(i);
                                }
                            });
                            binding.FacebookLinkLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Uri uri = Uri.parse(model.getFacebookLink());
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(String.valueOf(uri)));
                                    startActivity(i);
                                }
                            });
                            binding.InstagramLinkLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Uri uri = Uri.parse(model.getInstagramLink());
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(String.valueOf(uri)));
                                    startActivity(i);
                                }
                            });
                            binding.TwitterLinkLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Uri uri = Uri.parse(model.getTwitterLink());
                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                    i.setData(Uri.parse(String.valueOf(uri)));
                                    startActivity(i);
                                }
                            });
                            if (!model.getWebsiteLink().equals("")) {
                                binding.WebsiteLinkLayout.setVisibility(View.VISIBLE);
                            }
                            if (!model.getFacebookLink().equals("")) {
                                binding.FacebookLinkLayout.setVisibility(View.VISIBLE);
                            }
                            if (!model.getInstagramLink().equals("")) {
                                binding.InstagramLinkLayout.setVisibility(View.VISIBLE);
                            }
                            if (!model.getTwitterLink().equals("")) {
                                binding.TwitterLinkLayout.setVisibility(View.VISIBLE);
                            }
                            if (model.getTwitterLink().equals("") && model.getFacebookLink().equals("") && model.getInstagramLink().equals("") && model.getWebsiteLink().equals("")) {
                                binding.linkHeading.setVisibility(View.GONE);
                            }
                            if (model.getChannelDescription().equals("")){
                                binding.DescriptionHeading.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
            });
            binding.morebackButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.channellayoutbackbutton.setVisibility(View.VISIBLE);
                    binding.channellayout.setVisibility(View.VISIBLE);
                    binding.morebacklayout.setVisibility(View.GONE);
                    binding.moreaboutchanneldetails.setVisibility(View.GONE);
                }
            });
        }

        //backButton
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}