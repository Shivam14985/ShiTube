package com.example.shivambhardwaj.shitube.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DownloadManager;
import android.app.PictureInPictureParams;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Rational;
import android.view.GestureDetector;
import android.view.MotionEvent;
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
import androidx.media3.ui.PlayerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.shivambhardwaj.shitube.Adapters.BottomVideoSuggetionAdapter;
import com.example.shivambhardwaj.shitube.Adapters.ViewVideoAdapter;
import com.example.shivambhardwaj.shitube.Fragments.BottomSheetFragment;
import com.example.shivambhardwaj.shitube.Models.CommentsModel;
import com.example.shivambhardwaj.shitube.Models.CreatersModel;
import com.example.shivambhardwaj.shitube.Models.VideoModel;
import com.example.shivambhardwaj.shitube.R;
import com.example.shivambhardwaj.shitube.Services.NetworkBroadcast;
import com.example.shivambhardwaj.shitube.TrackSelectionDialog;
import com.example.shivambhardwaj.shitube.databinding.ActivityViewVideoBinding;
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

public class ViewVideoActivity extends AppCompatActivity {
    static boolean active = false;
    ActivityViewVideoBinding binding;
    VideoModel model = null;
    FirebaseDatabase database;
    ExoPlayer exoPlayer;
    ImageView forward_button, backward_button, play_pause_button, fullscreen_button, resize_button, settingbutton, backButton;
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
    private BroadcastReceiver broadcastReceiver;

    @UnstableApi
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        active = true;
        EdgeToEdge.enable(this);
        database = FirebaseDatabase.getInstance();
        Sprite doubleBounce = new FadingCircle();
        binding.spinKit.setIndeterminateDrawable(doubleBounce);
        broadcastReceiver = new NetworkBroadcast();
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        binding.KnowMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model.getAddedBy().equals(FirebaseAuth.getInstance().getUid())) {
                    Intent intent = new Intent(ViewVideoActivity.this, CommonActivity.class);
                    intent.putExtra("data", "Creater");
                    startActivity(intent);
                }
                 else {
                    Intent intent = new Intent(ViewVideoActivity.this, OthersProfileViewActivity.class);
                    String addedBy = model.getAddedBy().toString();
                    intent.putExtra("channelId", addedBy);
                    startActivity(intent);
                }
            }
        });
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });
//From Video
        final Object object = getIntent().getSerializableExtra("VideoContent");
        if (object instanceof VideoModel) {
            model = (VideoModel) object;
        }
        if (model != null) {
            binding.commentsdes.setText("Comments " + model.getCommentsCount());
            binding.VideoTitle.setText(model.getTitle());
            String time = TimeAgo.using(model.getAddedAt());
            binding.Views.setText(model.getViewsCount() + 1 + " views  " + time);
            if (model.getApproved().equals("true")) {
                binding.SaveVideo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ViewVideoActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                        database.getReference().child("Videos").child(model.getPostId()).child("watchLater").child(FirebaseAuth.getInstance().getUid()).setValue("true");
                    }
                });
                binding.mainLayout.setVisibility(View.VISIBLE);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //NAtiveAds Code
                        loadNAtiveAds();
                    }
                }, 10000);
                //interstitial ads
                loadInterstitialAds();
                //banner ads
                loadBannerAds();
            } if (model.getApproved().equals("Rejected")||model.getApproved().equals("Pending")){
                binding.ApprovalLayout.setVisibility(View.VISIBLE);
                binding.ApproveVideoTitle.setText(model.getTitle());
                binding.ApproveVideoDescription.setText("Description :" + model.getVideoDescription());
                binding.ApproveVideoCAtegory.setText("Category :" + model.getCategory());
                database.getReference().child("Creaters").child(model.getAddedBy()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        CreatersModel creatersModel = snapshot.getValue(CreatersModel.class);
                        Picasso.get().load(creatersModel.getProfileImage()).placeholder(R.drawable.profileuser).into(binding.channelImage);
                        binding.approvechannelName.setText(creatersModel.getName());
                        String subscribers = snapshot.child("subscribedBy").getChildrenCount() + "";
                        binding.approvesubscribers.setText(subscribers);
                        binding.Approve.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                database.getReference().child("Videos").child(model.getPostId()).child("approved").setValue("true");
                                finish();
                            }
                        });
                        binding.Reject.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                database.getReference().child("Videos").child(model.getPostId()).child("approved").setValue("Rejected");
                                finish();
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            if (model.getAddedBy().equals(FirebaseAuth.getInstance().getUid())) {
                binding.btnSubscribe.setVisibility(View.GONE);
            }

            // Prepare the media source
            exoPlayer = new ExoPlayer.Builder(ViewVideoActivity.this).build();
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
            gestureDetector = new GestureDetector(this, new GestureListener());
            // Set the onTouchListener for the gesture TextView
            binding.PlayerView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        if (controllerVisibility == true) {
                            binding.PlayerView.hideController();
                        } else {
                            binding.PlayerView.showController();
                        }
                    }
                    return gestureDetector.onTouchEvent(event);
                }
            });
            binding.PlayerView.setControllerVisibilityListener(new PlayerView.ControllerVisibilityListener() {
                @Override
                public void onVisibilityChanged(int visibility) {
                    if (visibility == View.VISIBLE) {
                        controllerVisibility = true;
                    } else {
                        controllerVisibility = false;
                    }
                }
            });
            binding.download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse(model.getVideo());
                    DownloadManager downloadManager = (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    request.setTitle(model.getTitle());
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "filename.jpg");
                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                    request.setAllowedOverRoaming(false);
                    downloadManager.enqueue(request);
                }
            });
            binding.share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uri = model.getVideo();
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT,"Watch this video:"+ model.getTitle() + " " + uri);
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
                    Toast.makeText(ViewVideoActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
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
                fullscreen_button.setImageDrawable(ContextCompat.getDrawable(ViewVideoActivity.this, R.drawable.fullscreenopen));
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.PlayerView.getLayoutParams();
                params.width = params.MATCH_PARENT;
                params.height = params.MATCH_PARENT;
                binding.PlayerView.setLayoutParams(params);
                isFullScreen = true;
            }
            backButton = binding.PlayerView.findViewById(R.id.backcontroller);

            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isFullScreen == true) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    } else {
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
            binding.SaveVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    database.getReference().child("Videos").child(model.getPostId()).child("watchLater").child(FirebaseAuth.getInstance().getUid()).setValue("true");
                    Toast.makeText(ViewVideoActivity.this, "Saved to Watch Later", Toast.LENGTH_SHORT).show();
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
            if (model.getCommentsCount() == 0) {
                binding.nocommentsLAyout.setVisibility(View.VISIBLE);
                database.getReference().child("Creaters").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
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
            database.getReference().child("Creaters").child(model.getAddedBy()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    CreatersModel creatersModel = snapshot.getValue(CreatersModel.class);
                    Picasso.get().load(creatersModel.getProfileImage()).placeholder(R.drawable.profileuser).into(binding.channelImage);
                    binding.channelName.setText(creatersModel.getName());
                    String subscribers = snapshot.child("subscribedBy").getChildrenCount() + "";
                    binding.subscribers.setText(subscribers);
                    if (snapshot.child("admin").getValue().toString().equals("true")) {
                        binding.verifiedMark.setVisibility(View.VISIBLE);
                    }

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
                                binding.likedAnimationBlast.playAnimation();
                                binding.likedAnimationBlast.setVisibility(View.VISIBLE);
                                binding.likedAnimationBlast.addAnimatorListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(@NonNull Animator animation, boolean isReverse) {
                                        super.onAnimationEnd(animation, isReverse);
                                        binding.likedAnimationBlast.setVisibility(View.GONE);
                                    }
                                });
                                database.getReference().child("Videos").child(model.getPostId()).child("likesCount").setValue(likes);
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
                            String postID2 = model.getPostId().toString();
                            if (postId.equals(postID2)) {
                            } else {
                                list.add(model1);
                            }
                        }else{

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


    private void loadNAtiveAds() {
        AdLoader.Builder adLoader = new AdLoader.Builder(ViewVideoActivity.this, "ca-app-pub-5928796239739806/7990923973");
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
                }, 30000);
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
                    interstitialAd.show(ViewVideoActivity.this);
                    exoPlayer.pause();
                    interstitialAd.setImmersiveMode(true);

                }
                interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        exoPlayer.play();
                    }
                });

            }
        });

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
           }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        exoPlayer.release();
        active = false;
        long lastseen = exoPlayer.getCurrentPosition();
        database.getReference().child("Videos").child(model.getPostId()).child("lastSeen").child(FirebaseAuth.getInstance().getUid()).setValue(lastseen);
    }

    @Override
    protected void onStop() {
        super.onStop();
        exoPlayer.pause();
        active = false;
        long lastseen = exoPlayer.getCurrentPosition();
        database.getReference().child("Videos").child(model.getPostId()).child("lastSeen").child(FirebaseAuth.getInstance().getUid()).setValue(lastseen);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            exo_title.setVisibility(View.INVISIBLE);
            fullscreen_button.setImageDrawable(ContextCompat.getDrawable(ViewVideoActivity.this, R.drawable.fullscreenclose));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.PlayerView.getLayoutParams();
            params.width = params.MATCH_PARENT;
            params.height = (int) (210 * getApplicationContext().getResources().getDisplayMetrics().density);
            binding.PlayerView.setLayoutParams(params);
            isFullScreen = false;
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            exo_title.setVisibility(View.VISIBLE);
            fullscreen_button.setImageDrawable(ContextCompat.getDrawable(ViewVideoActivity.this, R.drawable.fullscreenopen));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.PlayerView.getLayoutParams();
            params.width = params.MATCH_PARENT;
            params.height = params.MATCH_PARENT;
            binding.PlayerView.setLayoutParams(params);
            isFullScreen = true;
        }
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

//    private void loadbannerad() {
//        adView = new AdView(this);
//        //test ID ca-app-pub-3940256099942544/9214589741
//        //Ad Id ca-app-pub-5928796239739806/5049227563
//        adView.setAdUnitId("ca-app-pub-3940256099942544/9214589741");
////        adView.setAdSize(AdSize.BANNER);
//        AdSize adSize = getAdSize();
//        adView.setAdSize(adSize);
//        // Create an extra parameter that aligns the bottom of the expanded ad to
//        // the bottom of the bannerView.
//        Bundle extras = new Bundle();
//        extras.putString("collapsible", "top");
//
//        AdRequest adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, extras).build();
//        binding.adView.addView(adView);
//        adView.loadAd(adRequest);
//        adView.setAdListener(new AdListener() {
//            @Override
//            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                super.onAdFailedToLoad(loadAdError);
//                loadbannerad();
//            }
//        });
//    }

//    private AdSize getAdSize() {
//        // Determine the screen width (less decorations) to use for the ad width.
//        Display display = getWindowManager().getDefaultDisplay();
//        DisplayMetrics outMetrics = new DisplayMetrics();
//        display.getMetrics(outMetrics);
//
//        float density = outMetrics.density;
//
//        float adWidthPixels = binding.adView.getWidth();
//
//        // If the ad hasn't been laid out, default to the full screen width.
//        if (adWidthPixels == 0) {
//            adWidthPixels = outMetrics.widthPixels;
//        }
//
//        int adWidth = (int) (adWidthPixels / density);
//        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
//    }


    // This class has methods that check if two clicks were registered
    // within a span of DOUBLE_CLICK_TIME_DELTA i.e., in our case
    // equivalent to 300 ms
    public abstract static class DoubleClickListener implements View.OnClickListener {
        private static final long DOUBLE_CLICK_TIME_DELTA = 300; // milliseconds
        private long lastClickTime = 0;

        @Override
        public void onClick(View v) {
            long clickTime = System.currentTimeMillis();
            if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                onDoubleClick(v);
            }
            lastClickTime = clickTime;
        }

        public abstract void onDoubleClick(View v);
    }

    //Extra
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffY) > Math.abs(diffX)) {
                if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY < 0) {
                        onDragUp();
                    } else {
                        onDragDown();
                    }
                }
            }
            return true;
        }

        @OptIn(markerClass = UnstableApi.class)
        private void onDragUp() {
            if (isFullScreen == true) {
                binding.PlayerView.hideController();
                binding.recyclerViewvideoSuggestion.setVisibility(View.VISIBLE);
                ArrayList<VideoModel> list = new ArrayList<>();
                BottomVideoSuggetionAdapter adapter = new BottomVideoSuggetionAdapter(list, ViewVideoActivity.this);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ViewVideoActivity.this, LinearLayoutManager.HORIZONTAL, true);
                linearLayoutManager.setStackFromEnd(true);
                binding.recyclerViewvideoSuggestion.setLayoutManager(linearLayoutManager);
                binding.recyclerViewvideoSuggestion.setAdapter(adapter);
                if (model != null) {
                    FirebaseDatabase.getInstance().getReference().child("Videos").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            list.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                String approved = dataSnapshot.child("approved").getValue().toString();
                                if (approved.equals("true")) {
                                    VideoModel model1 = dataSnapshot.getValue(VideoModel.class);
                                    String postId = model1.getPostId().toString();
                                    String postID2 = model.getPostId().toString();
                                    if (postId.equals(postID2)) {
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
                }
                Intent intent = getIntent();
                String videoId = intent.getStringExtra("OnNotificationOpened");
                if (videoId != null) {
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
                }
                bottomSuggestion = true;
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                exo_title.setVisibility(View.VISIBLE);
                fullscreen_button.setImageDrawable(ContextCompat.getDrawable(ViewVideoActivity.this, R.drawable.fullscreenopen));
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.PlayerView.getLayoutParams();
                params.width = params.MATCH_PARENT;
                params.height = params.MATCH_PARENT;
                binding.PlayerView.setLayoutParams(params);
                isFullScreen = true;
            }
        }

        private void onDragDown() {
            if (bottomSuggestion == true && isFullScreen == true) {
                binding.recyclerViewvideoSuggestion.setVisibility(View.GONE);
                bottomSuggestion = false;
            } else if (bottomSuggestion == false && isFullScreen == true) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                exo_title.setVisibility(View.INVISIBLE);
                fullscreen_button.setImageDrawable(ContextCompat.getDrawable(ViewVideoActivity.this, R.drawable.fullscreenclose));
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) binding.PlayerView.getLayoutParams();
                params.width = params.MATCH_PARENT;
                params.height = (int) (210 * getApplicationContext().getResources().getDisplayMetrics().density);
                binding.PlayerView.setLayoutParams(params);
            }
            if (bottomSuggestion == false && isFullScreen == false) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    pictureInPictureParamsBuilder = new PictureInPictureParams.Builder();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    Rational aspectRatio = new Rational(16, 9);
                    pictureInPictureParamsBuilder.setAspectRatio(aspectRatio);
                    enterPictureInPictureMode(pictureInPictureParamsBuilder.build());

                } else {
                    Toast.makeText(ViewVideoActivity.this, "Your device doesn't support this feature", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}