package com.example.shivambhardwaj.shitube.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.shivambhardwaj.shitube.Fragments.HomeFragment;
import com.example.shivambhardwaj.shitube.Fragments.MyProfileFragment;
import com.example.shivambhardwaj.shitube.Fragments.PostFragment;
import com.example.shivambhardwaj.shitube.Fragments.ShortsFragment;
import com.example.shivambhardwaj.shitube.Fragments.SubscriptionFragment;
import com.example.shivambhardwaj.shitube.R;
import com.example.shivambhardwaj.shitube.databinding.ActivityMainBinding;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private final ActivityResultLauncher<String> resultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    });
    ActivityMainBinding binding;
    AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        loadbannerad();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });
        if (FirebaseAuth.getInstance().getCurrentUser()==null){
            Intent intent =new Intent(MainActivity.this,LoginActivity.class);
            startActivity(intent);
        }
        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.home) {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainerView, new HomeFragment());
                    fragmentTransaction.commit();
//                    loadbannerad();
//                    binding.adView.setVisibility(View.VISIBLE);
                }
                if (menuItem.getItemId() == R.id.shorts) {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainerView, new ShortsFragment());
                    fragmentTransaction.commit();
//                    binding.adView.setVisibility(View.GONE);
                }
                if (menuItem.getItemId() == R.id.Subscription) {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainerView, new SubscriptionFragment());
                    fragmentTransaction.commit();
//                    binding.adView.setVisibility(View.GONE);
                }
                if (menuItem.getItemId() == R.id.Post) {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainerView, new PostFragment());
                    fragmentTransaction.commit();
//                    binding.adView.setVisibility(View.GONE);
                }
                if (menuItem.getItemId() == R.id.Profile) {
                    FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainerView, new MyProfileFragment());
                    fragmentTransaction.commit();
//                    binding.adView.setVisibility(View.GONE);
                }
                return true;
            }
        });

        askNotificationPermission();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }

    private void askNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                resultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    //    private void loadbannerad() {
//        adView = new AdView(this);
//        //test ID ca-app-pub-3940256099942544/9214589741
//        //Ad Id ca-app-pub-5928796239739806/5049227563
//        adView.setAdUnitId("ca-app-pub-5928796239739806/5049227563");
////        adView.setAdSize(AdSize.BANNER);
//        AdSize adSize = getAdSize();
//        adView.setAdSize(adSize);
//        // Create an extra parameter that aligns the bottom of the expanded ad to
//        // the bottom of the bannerView.
//        Bundle extras = new Bundle();
//        extras.putString("collapsible", "bottom");
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
//
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
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        FragmentManager fragmentManager = getSupportFragmentManager();

        // Handle HomeFragment
        Fragment fragment = fragmentManager.findFragmentByTag(HomeFragment.class.getSimpleName());
        if (fragment instanceof HomeFragment) {
            ((HomeFragment) fragment).onConfigurationChanged(newConfig);
        }

        // Handle ShortsFragment
        fragment = fragmentManager.findFragmentByTag(ShortsFragment.class.getSimpleName());
        if (fragment instanceof ShortsFragment) {
            ((ShortsFragment) fragment).onConfigurationChanged(newConfig);
        }

        // Handle PostFragment
        fragment = fragmentManager.findFragmentByTag(PostFragment.class.getSimpleName());
        if (fragment instanceof PostFragment) {
            ((PostFragment) fragment).onConfigurationChanged(newConfig);
        }

        // Handle SubscriptionFragment
        fragment = fragmentManager.findFragmentByTag(SubscriptionFragment.class.getSimpleName());
        if (fragment instanceof SubscriptionFragment) {
            ((SubscriptionFragment) fragment).onConfigurationChanged(newConfig);
        }

        // Handle MyProfileFragment
        fragment = fragmentManager.findFragmentByTag(MyProfileFragment.class.getSimpleName());
        if (fragment instanceof MyProfileFragment) {
            ((MyProfileFragment) fragment).onConfigurationChanged(newConfig);
        }
    }

}