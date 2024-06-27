package com.example.shivambhardwaj.shitube.Activities;

import android.app.PictureInPictureParams;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Rational;
import android.view.GestureDetector;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.PlaybackException;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.ui.AspectRatioFrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.shivambhardwaj.shitube.Adapters.ViewVideoAdapter;
import com.example.shivambhardwaj.shitube.Fragments.BottomSheetFragment;
import com.example.shivambhardwaj.shitube.Models.CommentsModel;
import com.example.shivambhardwaj.shitube.Models.CreatersModel;
import com.example.shivambhardwaj.shitube.Models.NotificationModel;
import com.example.shivambhardwaj.shitube.Models.VideoModel;
import com.example.shivambhardwaj.shitube.R;
import com.example.shivambhardwaj.shitube.TrackSelectionDialog;
import com.example.shivambhardwaj.shitube.databinding.ActivityNotificationVideoViewBinding;
import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.FadingCircle;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

public class NotificationVideoViewActivity extends AppCompatActivity {
    static boolean active = false;
    ActivityNotificationVideoViewBinding binding;
    FirebaseDatabase database;
    ExoPlayer exoPlayer;
    ImageView forward_button, backward_button, play_pause_button, fullscreen_button, resize_button, settingbutton, PictureInPictureMode;
    TextView exo_title;
    boolean isFullScreen = false;
    int RESIZE_MODE = 0;
    PictureInPictureParams.Builder pictureInPictureParamsBuilder;
    private NativeAd mnativeAd;
    private boolean isShowingTrackSelectionDialog;
    private boolean isCrossChecked;

    private GestureDetector gestureDetector;
    private boolean controllerVisibility = false;
    private boolean bottomSuggestion = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityNotificationVideoViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        active = true;
        EdgeToEdge.enable(this);
        database = FirebaseDatabase.getInstance();
        Sprite doubleBounce = new FadingCircle();
        binding.spinKit.setIndeterminateDrawable(doubleBounce);
        loadBannerAds();
        loadInterstitialAds();
        loadNAtiveAds();
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });

        Intent intent = getIntent();
        String videoId = intent.getStringExtra("OnNotificationOpened");
        database.getReference().child("Videos").child(videoId).addValueEventListener(new ValueEventListener() {
            @UnstableApi
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                VideoModel model = snapshot.getValue(VideoModel.class);
                binding.VideoTitle.setText(model.getTitle());
                binding.commentsdes.setText("Comments "+model.getCommentsCount() );
                String time = TimeAgo.using(model.getAddedAt());
                binding.Views.setText(model.getViewsCount() + 1 + " views  " + time);
                if (model.getAddedBy().equals(FirebaseAuth.getInstance().getUid())) {
                    binding.btnSubscribe.setVisibility(View.GONE);
                }
                exoPlayer = new ExoPlayer.Builder(NotificationVideoViewActivity.this).build();
                binding.PlayerView.setPlayer(exoPlayer);
                Uri uri = Uri.parse(model.getVideo());
                MediaItem mediaItem = MediaItem.fromUri(uri);
                exoPlayer.setMediaItem(mediaItem);
                exoPlayer.addListener(new Player.Listener() {
                    @Override
                    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                        if (playWhenReady && playbackState == Player.STATE_READY) {
                            // Active playback.
                        } else if (playbackState == Player.STATE_ENDED) {
                            //The player finished playing all media
                            binding.PlayerView.hideController();
                            //Add your code here

                        } else if (playWhenReady) {
                            // Not playing because playback ended, the player is buffering, stopped or
                            // failed. Check playbackState and player.getPlaybackError for details.
                        } else {
                            // Paused by app.
                        }

                    }
                });

                binding.download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse(model.getVideo());
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(String.valueOf(uri)));
                        startActivity(i);
//                            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//                            DownloadManager.Request request = new DownloadManager.Request(uri);
//                            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "filename.jpg");
//                            downloadManager.enqueue(request);
//                            Toast.makeText(context, "Downloading", Toast.LENGTH_SHORT).show();
                    }
                });
                binding.share.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String uri = model.getVideo();
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_TEXT, model.getTitle() + "\n" + uri);
                        intent.setType("text/plain");
                        startActivity(Intent.createChooser(intent, "Share Via"));

                    }
                });
                database.getReference().child("Videos").child(model.getPostId()).child("lastSeen").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            long lastSeen = Long.parseLong(snapshot.getValue() + "");
                            exoPlayer.seekTo(lastSeen);
                            exoPlayer.prepare();
                            exoPlayer.setPlayWhenReady(true);
                        } else {
                            exoPlayer.prepare();
                            exoPlayer.setPlayWhenReady(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                exoPlayer.addListener(new Player.Listener() {
                    @Override
                    public void onPlayerError(PlaybackException error) {
                        Player.Listener.super.onPlayerError(error);
                        Toast.makeText(NotificationVideoViewActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                });
                exoPlayer.addListener(new Player.Listener() {
                });
                exoPlayer.addListener(new Player.Listener() {
                    @Override
                    public void onPlaybackStateChanged(int state) {
                        if (state == Player.STATE_READY) {
                            binding.spinKit.setVisibility(View.GONE);
                            play_pause_button.setVisibility(View.VISIBLE);
                        } else if (state == Player.STATE_BUFFERING) {
                            binding.spinKit.setVisibility(View.VISIBLE);
                            play_pause_button.setVisibility(View.GONE);
                        } else {
                            binding.spinKit.setVisibility(View.GONE);
                            play_pause_button.setVisibility(View.VISIBLE);
                        }

                    }
                });
                forward_button = binding.PlayerView.findViewById(R.id.exo_forward);
                backward_button = binding.PlayerView.findViewById(R.id.exo_backward);
                play_pause_button = binding.PlayerView.findViewById(R.id.exo_play_pause);
                fullscreen_button = binding.PlayerView.findViewById(R.id.exo_full);
                settingbutton = binding.PlayerView.findViewById(R.id.settings);
                resize_button = binding.PlayerView.findViewById(R.id.size_ration);

                exo_title = binding.PlayerView.findViewById(R.id.exo_title);
                int orientation = getResources().getConfiguration().orientation;

                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    exo_title.setVisibility(View.VISIBLE);
                    fullscreen_button.setImageDrawable(ContextCompat.getDrawable(NotificationVideoViewActivity.this, R.drawable.fullscreenopen));
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                    ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.PlayerView.getLayoutParams();
                    params.width = params.MATCH_PARENT;
                    params.height = params.MATCH_PARENT;
                    binding.PlayerView.setLayoutParams(params);
                    isFullScreen = true;
                }
                PictureInPictureMode = binding.PlayerView.findViewById(R.id.backcontroller);
                PictureInPictureMode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isFullScreen==true) {
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        } else{
                            finish();
                        }
                    }
                });
                exo_title.setText(model.getTitle());
                play_pause_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        exoPlayer.setPlayWhenReady(!exoPlayer.getPlayWhenReady());
                        play_pause_button.setImageResource(Boolean.TRUE.equals(exoPlayer.getPlayWhenReady()) ? R.drawable.pause : R.drawable.play);
                    }
                });
                forward_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        exoPlayer.seekTo(exoPlayer.getCurrentPosition() + 10000);
                    }
                });
                backward_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        long num = exoPlayer.getCurrentPosition() - 10000;
                        if (num < 0) {
                            exoPlayer.seekTo(0);
                        } else {
                            exoPlayer.seekTo(num);
                        }
                    }
                });
//            back_button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (isFullScreen) {
//                        exo_title.setVisibility(View.INVISIBLE);
//                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
//                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.PlayerView.getLayoutParams();
//                        params.width = params.MATCH_PARENT;
//                        params.height = (int) (210 * getApplicationContext().getResources().getDisplayMetrics().density);
//                        binding.PlayerView.setLayoutParams(params);
//                        isFullScreen = false;
//                        fullscreen_button.setImageDrawable(ContextCompat.getDrawable(ViewVideoActivity.this, R.drawable.fullscreenclose));
//
//                    } else {
//                        finish();
//                        exoPlayer.release();
//                    }
//                }
//            });
                fullscreen_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isFullScreen) {
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                        } else {
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        }
                    }
                });
                settingbutton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isShowingTrackSelectionDialog && TrackSelectionDialog.willHaveContent(exoPlayer)) {
                            isShowingTrackSelectionDialog = true;
                            TrackSelectionDialog trackSelectionDialog = TrackSelectionDialog.createForPlayer(exoPlayer,
                                    /* onDismissListener= */ dismissedDialog -> isShowingTrackSelectionDialog = false);
                            trackSelectionDialog.show(getSupportFragmentManager(), /* tag= */ null);

                        }
                    }
                });
                resize_button.setOnClickListener(new View.OnClickListener() {
                    @OptIn(markerClass = UnstableApi.class)
                    @Override
                    public void onClick(View v) {
                        if (RESIZE_MODE == 0) {
                            binding.PlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
                            RESIZE_MODE = 1;
                        } else {
                            binding.PlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
                            RESIZE_MODE = 0;
                        }
                    }
                });
                database.getReference().child("Creaters").child(model.getAddedBy()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        CreatersModel creatersModel = snapshot.getValue(CreatersModel.class);
                        Picasso.get().load(creatersModel.getProfileImage()).placeholder(R.drawable.profileuser).into(binding.channelImage);
                        binding.channelName.setText(creatersModel.getName());
                        String subscribers = snapshot.child("subscribedBy").getChildrenCount() + "";
                        binding.subscribers.setText(subscribers);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                database.getReference().child("Videos").child(model.getPostId()).child("likesCount").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String likes = snapshot.getValue().toString();
                        binding.likenumber.setText(likes);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                database.getReference().child("Videos").child(model.getPostId()).child("likedBy").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            binding.likevideo.setImageResource(R.drawable.liked);
                            binding.likevideo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int dislekes = model.getLikesCount() - 1;
                                    model.setLikesCount(dislekes);
                                    database.getReference().child("Videos").child(model.getPostId()).child("likesCount").setValue(dislekes);
                                    database.getReference().child("Videos").child(model.getPostId()).child("likedBy").child(FirebaseAuth.getInstance().getUid()).removeValue();
                                    binding.likevideo.setImageResource(R.drawable.like);

                                    database.getReference().child("Videos").child(model.getPostId()).child("likesCount").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String likes = snapshot.getValue().toString();
                                            binding.likenumber.setText(likes);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            });
                        } else {
                            binding.likevideo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    int likes = model.getLikesCount() + 1;
                                    model.setLikesCount(likes);
                                    database.getReference().child("Videos").child(model.getPostId()).child("likesCount").setValue(likes);
                                    NotificationModel notificationModel = new NotificationModel();
                                    notificationModel.setVideoId(model.getPostId());
                                    notificationModel.setNotificationOpened(false);
                                    notificationModel.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                    notificationModel.setNotificationAt(new Date().getTime());
                                    notificationModel.setNotificationText("liked your video.");
                                    notificationModel.setNotificationType("Likes");
                                    database.getReference().child("Users").child(model.getAddedBy()).child("Notifications").push().setValue(notificationModel);
                                    database.getReference().child("Videos").child(model.getPostId()).child("likedBy").child(FirebaseAuth.getInstance().getUid()).setValue(true);
                                    binding.likevideo.setImageResource(R.drawable.liked);
                                    database.getReference().child("Videos").child(model.getPostId()).child("likesCount").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            String likes = snapshot.getValue().toString();
                                            binding.likenumber.setText(likes);
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
                if (model.getCommentsCount() == 0) {
                    binding.nocommentsLAyout.setVisibility(View.VISIBLE);
                    database.getReference().child("Creaters").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                CreatersModel creatersModel = snapshot.getValue(CreatersModel.class);
                                Picasso.get().load(creatersModel.getProfileImage()).placeholder(R.drawable.profileuser).into(binding.MyProfile);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                } else {
                    binding.commentInputLayout.setVisibility(View.VISIBLE);
                    database.getReference().child("Videos").child(model.getPostId()).child("comments").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                CommentsModel commentsModel = dataSnapshot.getValue(CommentsModel.class);
                                binding.comment.setText(commentsModel.getComment());
                                FirebaseDatabase.getInstance().getReference().child("Creaters").child(commentsModel.getCommentedBy()).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            CreatersModel model = snapshot.getValue(CreatersModel.class);
                                            Picasso.get().load(model.getProfileImage()).placeholder(R.drawable.profileuser).into(binding.commentdProfile);

                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
                binding.Comments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle mBundle = new Bundle();
                        mBundle.putString("mText", model.getPostId());
                        BottomSheetDialogFragment bottomSheetDialogFragment = new BottomSheetFragment();
                        bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
                        bottomSheetDialogFragment.setArguments(mBundle);
                    }
                });
                binding.SaveVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        database.getReference().child("Videos").child(model.getPostId()).child("watchLater").child(FirebaseAuth.getInstance().getUid()).setValue("true");
                    }
                });
                database.getReference().child("Creaters").child(model.getAddedBy()).child("subscribedBy").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            binding.btnSubscribe.setText("Subscibed");
                            binding.btnSubscribe.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    database.getReference().child("Creaters").child(model.getAddedBy()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            CreatersModel creatersModel = snapshot.getValue(CreatersModel.class);
                                            int subscribers = creatersModel.getSubscribersCount() - 1;
                                            creatersModel.setSubscribersCount(subscribers);
                                            database.getReference().child("Creaters").child(model.getAddedBy()).child("subscribersCount").setValue(subscribers);
                                            database.getReference().child("Creaters").child(model.getAddedBy()).child("subscribedBy").child(FirebaseAuth.getInstance().getUid()).removeValue();
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
                                    database.getReference().child("Creaters").child(model.getAddedBy()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            CreatersModel creatersModel = snapshot.getValue(CreatersModel.class);
                                            int subscribers = creatersModel.getSubscribersCount() + 1;
                                            creatersModel.setSubscribersCount(subscribers);
                                            database.getReference().child("Creaters").child(model.getAddedBy()).child("subscribersCount").setValue(subscribers);
                                            database.getReference().child("Creaters").child(model.getAddedBy()).child("subscribedBy").child(FirebaseAuth.getInstance().getUid()).setValue("true");
                                            binding.btnSubscribe.setText("Subscribed");

                                            NotificationModel notificationModel = new NotificationModel();
                                            notificationModel.setNotificationType("Subscribers");
                                            notificationModel.setNotificationText("subscribed your channel.");
                                            notificationModel.setNotificationBy(FirebaseAuth.getInstance().getUid());
                                            notificationModel.setNotificationOpened(false);
                                            notificationModel.setVideoId(model.getPostId());
                                            notificationModel.setNotificationAt(new Date().getTime());
                                            database.getReference().child("Users").child(model.getAddedBy()).child("Notifications").push().setValue(notificationModel);
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


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ArrayList list = new ArrayList();
        ViewVideoAdapter adapter = new ViewVideoAdapter(list, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true);
        linearLayoutManager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(linearLayoutManager);
        binding.recyclerView.setAdapter(adapter);
        FirebaseDatabase.getInstance().getReference().child("Videos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String approved = dataSnapshot.child("approved").getValue().toString();
                    if (approved.equals("true")) {
                        VideoModel model1 = dataSnapshot.getValue(VideoModel.class);
                        String postId = model1.getPostId().toString();
                        if (postId.equals(videoId)) {
                        } else {
                            list.add(model1);
                        }
                    }

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadNAtiveAds() {
        AdLoader.Builder adLoader = new AdLoader.Builder(NotificationVideoViewActivity.this, "ca-app-pub-5928796239739806/7990923973");
        adLoader.forNativeAd(new NativeAd.OnNativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                if (isDestroyed() || isFinishing() || isChangingConfigurations()) {
                    nativeAd.destroy();
                    return;
                }
                if (mnativeAd != null) {
                    mnativeAd.destroy();
                }
                mnativeAd = nativeAd;
                FrameLayout frameLayout = findViewById(R.id.nativeads);
                NativeAdView nativeAdView = (NativeAdView) getLayoutInflater().inflate(R.layout.native_advance_ads, null);
                populateNativeAdView(nativeAd, nativeAdView);
                frameLayout.removeAllViews();
                frameLayout.addView(nativeAdView);
            }
        });
        AdLoader adLoader1 = adLoader.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                loadNAtiveAds();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNAtiveAds();
                    }
                }, 25000);
            }
        }).withNativeAdOptions(new NativeAdOptions.Builder().setVideoOptions(new VideoOptions.Builder().setStartMuted(true).build()).build()).build();
//if error the remove this code withNativeAdOptions(new NativeAdOptions.Builder()
//                        .setVideoOptions(new VideoOptions.Builder().setStartMuted(true).build())
//                        .build())
        adLoader1.loadAd(new AdRequest.Builder().build());
    }

    private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        // Set the media view.
        adView.setMediaView(adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline and mediaContent are guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.GONE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.GONE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView()).setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);
    }

    public void loadBannerAds() {//Banner Ads Code
        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);
        binding.adView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                loadBannerAds();
            }

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                binding.adView.setVisibility(View.VISIBLE);
            }
        });
    }

    public void loadInterstitialAds() {

        Intent intent = getIntent();
        String videoId = intent.getStringExtra("OnNotificationOpened");
        database.getReference().child("Videos").child(videoId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                VideoModel model = snapshot.getValue(VideoModel.class);
                AdRequest InterstitialAds = new AdRequest.Builder().build();
                InterstitialAd.load(getApplicationContext(), "ca-app-pub-5928796239739806/2272936706", InterstitialAds, new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        super.onAdFailedToLoad(loadAdError);
                    }

                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        super.onAdLoaded(interstitialAd);
                        if (active == true) {
                            interstitialAd.show(NotificationVideoViewActivity.this);
                            exoPlayer.pause();

                        }
                        interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();
                                if (model.getDuration() < 300000) {
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            loadInterstitialAds();
                                        }
                                    }, 100000);
                                } else if (model.getDuration() > 300000 && model.getDuration() < 900000) {
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            loadInterstitialAds();
                                        }
                                    }, 225000);
                                } else if (model.getDuration() > 900000 && model.getDuration() < 1800000) {
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            loadInterstitialAds();
                                        }
                                    }, 300000);
                                } else if (model.getDuration() > 1800000 && model.getDuration() < 3600000) {
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            loadInterstitialAds();
                                        }
                                    }, 400000);
                                } else if (model.getDuration() > 3600000) {
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            loadInterstitialAds();
                                        }
                                    }, 600000);
                                }
                            }
                        });

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            exo_title.setVisibility(View.INVISIBLE);
            fullscreen_button.setImageDrawable(ContextCompat.getDrawable(NotificationVideoViewActivity.this, R.drawable.fullscreenclose));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.PlayerView.getLayoutParams();
            params.width = params.MATCH_PARENT;
            params.height = (int) (210 * getApplicationContext().getResources().getDisplayMetrics().density);
            binding.PlayerView.setLayoutParams(params);
            isFullScreen = false;
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            exo_title.setVisibility(View.VISIBLE);
            fullscreen_button.setImageDrawable(ContextCompat.getDrawable(NotificationVideoViewActivity.this, R.drawable.fullscreenopen));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.PlayerView.getLayoutParams();
            params.width = params.MATCH_PARENT;
            params.height = params.MATCH_PARENT;
            binding.PlayerView.setLayoutParams(params);
            isFullScreen = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        active = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isInPictureInPictureMode()) {
            exoPlayer.setPlayWhenReady(true);
            active = false;
        } else {
            exoPlayer.setPlayWhenReady(false);
            exoPlayer.getPlaybackState();
            active = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        exoPlayer.release();
        active = false;
        long lastseen = exoPlayer.getCurrentPosition();
        Intent intent = getIntent();
        String videoId = intent.getStringExtra("OnNotificationOpened");
        database.getReference().child("Videos").child(videoId).child("lastSeen").child(FirebaseAuth.getInstance().getUid()).setValue(lastseen);
    }

    @Override
    protected void onStop() {
        super.onStop();
        exoPlayer.pause();
        active = false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        exoPlayer.release();
    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
    }

    @UnstableApi
    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, @NonNull Configuration newConfig) {
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        isCrossChecked = isInPictureInPictureMode;
        if (isInPictureInPictureMode) {
            binding.PlayerView.hideController();
            active = false;
        } else {
            binding.PlayerView.showController();
            active = true;
        }
    }

}