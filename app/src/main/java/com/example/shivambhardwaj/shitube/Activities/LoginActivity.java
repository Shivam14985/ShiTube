package com.example.shivambhardwaj.shitube.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.shivambhardwaj.shitube.Models.UsersModel;
import com.example.shivambhardwaj.shitube.R;
import com.example.shivambhardwaj.shitube.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    // Evaluating ChannelID and Description for the Custom Notification
    private final String description = "Some Description";
    private final String channelID = "123";
    private final ActivityResultLauncher<String> resultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (isGranted) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    });
    ActivityLoginBinding binding;
    FirebaseAuth auth;
    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            if (o.getResultCode() == RESULT_OK) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(o.getData());
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                    auth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String NAme = auth.getCurrentUser().getDisplayName();
                                String Email = auth.getCurrentUser().getEmail();
                                UsersModel usersModel = new UsersModel(NAme, Email);
                                FirebaseDatabase.getInstance().getReference().child("Users").child(task.getResult().getUser().getUid()).setValue(usersModel);
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } catch (ApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    });
    // Assigning variables to Notification Manager, Channel and Builder
    NotificationManager notifManager;
    NotificationChannel notifChannel;
    Notification.Builder notifBuilder;
    GoogleSignInClient mGoogleSignInClient;
    ProgressDialog progressDialog;
    String phoneNumber, otp;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private String verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
        FirebaseApp.initializeApp(LoginActivity.this);

        // Notification Service for the Manager
        notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        StartFirebaseLogin();
        //SignIn button
        binding.signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
                progressDialog.setMessage("Please Wait...");
                String Email = binding.EtEmail.getText().toString();
                String Password = binding.EtPassword.getText().toString();

                if (Email.isEmpty() || Password.isEmpty()) {
                    Snackbar snackbar= Snackbar.make(v, "Fill above fields", Snackbar.LENGTH_SHORT);
                    snackbar.setTextColor(Color.WHITE);
                    snackbar.setBackgroundTint(Color.RED);
                    snackbar.show();
                } else {
                    progressDialog.show();
                    auth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @SuppressLint("NewApi")
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Welcome Back", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                // This is how an Image to be displayed in our Notification
                                // is decoded and stored in a variable. I've added a picture
                                // named "download.jpeg" in the "Drawables".
                                Bitmap myBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.welcome);
                                Bitmap myBitmapS = BitmapFactory.decodeResource(getResources(), R.drawable.youtube);
                                Intent notificationIntent = new Intent(LoginActivity.this, MainActivity.class);
                                PendingIntent playcontentIntent = PendingIntent.getActivity(LoginActivity.this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

                                // If Min. API level of the phone is 26, then notification could be
                                // made aesthetic
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    notifChannel = new NotificationChannel(channelID, description, NotificationManager.IMPORTANCE_HIGH);
                                    notifChannel.enableLights(true);
                                    notifChannel.setLightColor(Color.RED);
                                    notifChannel.enableVibration(true);

                                    notifManager.createNotificationChannel(notifChannel);

                                    notifBuilder = new Notification.Builder(LoginActivity.this, channelID).setContentTitle("Welcome Back").setContentText("We thanks you to return on our platform. I will keep try to provide best user Interface, Services and Features.")
                                            .setSmallIcon(R.drawable.youtube)
                                            .setPriority(Notification.PRIORITY_HIGH)
                                            .setStyle(new Notification.BigPictureStyle()
                                                    .bigPicture(myBitmap)
                                                    .setContentDescription("We thanks you to return on our platform. I will keep try to provide best user Interface, Services and Features.")).setLargeIcon(myBitmapS).setContentIntent(playcontentIntent);

                                }
                                // Else the Android device would give out default UI attributes
                                else {
                                    notifBuilder = new Notification.Builder(LoginActivity.this).setContentTitle("Welcome Back").setContentText("We thanks you to return on our platform. I will keep try to provide best user Interface, Services and Features.");
                                }

                                // Everything is done now and the Manager is to be notified about
                                // the Builder which built a Notification for the application
                                notifManager.notify(1234, notifBuilder.build());
                            } else {
                                Snackbar snackbar= Snackbar.make(v, task.getException().getLocalizedMessage(), Snackbar.LENGTH_SHORT);
                                snackbar.setTextColor(Color.WHITE);
                                snackbar.setBackgroundTint(Color.GREEN);
                                snackbar.show();

                            }
                        }
                    });
                }
            }
        });
        //SigUp activity
        binding.SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
        binding.ForGotTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.SignInLAyout.setVisibility(View.GONE);
                binding.ForgotPAssWordLAyout.setVisibility(View.VISIBLE);
            }
        });
        binding.GOBAck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.SignInLAyout.setVisibility(View.VISIBLE);
                binding.ForgotPAssWordLAyout.setVisibility(View.GONE);
            }
        });

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Signing In");

        binding.ResetPAssword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Email = binding.EtEmailForgot.getText().toString();
                if (Email.isEmpty()) {
                    Snackbar snackbar= Snackbar.make(v, "Enter Email First", Snackbar.LENGTH_SHORT);
                    snackbar.setTextColor(Color.WHITE);
                    snackbar.setBackgroundTint(Color.RED);
                    snackbar.show();

                } else {
                    auth.sendPasswordResetEmail(Email).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Snackbar snackbar= Snackbar.make(v, "Reset password link has send to your Email ", Snackbar.LENGTH_SHORT);
                            snackbar.setTextColor(Color.WHITE);
                            snackbar.setBackgroundTint(Color.GREEN);
                            snackbar.show();
                            binding.SignInLAyout.setVisibility(View.VISIBLE);
                            binding.ForgotPAssWordLAyout.setVisibility(View.GONE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar snackbar= Snackbar.make(v, e.getLocalizedMessage(), Snackbar.LENGTH_SHORT);
                            snackbar.setTextColor(Color.WHITE);
                            snackbar.setBackgroundTint(Color.RED);
                            snackbar.show();

                        }
                    });
                }
            }
        });
        binding.GoToOTpSignIN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.SignInLAyout.setVisibility(View.GONE);
                binding.SignInWithOtpLayout.setVisibility(View.VISIBLE);
            }
        });
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.SignInWithOtpLayout.setVisibility(View.GONE);
                binding.SignInLAyout.setVisibility(View.VISIBLE);
            }
        });

        binding.SendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.Phone.getText().toString().isEmpty()) {
                    Snackbar snackbar = Snackbar.make(v, "Enter Number", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.RED);
                    snackbar.setTextColor(Color.WHITE);
                    snackbar.show();
                }
                if (binding.Phone.getMinEms() < 10){
                    Snackbar snackbar = Snackbar.make(v, "Enter Valid Number", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.RED);
                    snackbar.setTextColor(Color.WHITE);
                    snackbar.show();
                }else{
                binding.Success.setVisibility(View.GONE);
                binding.Failed.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.VISIBLE);
                phoneNumber = binding.Phone.getText().toString();
                PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" + phoneNumber,                // Phone number to verify
                        60,                           // Timeout duration
                        TimeUnit.SECONDS,                // Unit of timeout
                        LoginActivity.this,        // Activity (for callback binding)
                        mCallback);                      // OnVerificationStateChangedCallbacks
            }}
        });

        binding.SubmitOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.otpsubmit.getText().toString().isEmpty()){
                    Snackbar snackbar = Snackbar.make(v, "Enter Otp First", Snackbar.LENGTH_SHORT);
                    snackbar.setBackgroundTint(Color.RED);
                    snackbar.setTextColor(Color.WHITE);
                    snackbar.show();
                }
                else {
                    progressDialog.show();
                    otp = binding.otpsubmit.getText().toString();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, otp);
                    SigninWithPhone(credential);
                }
            }
        });


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.clientId))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        binding.AuthGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                activityResultLauncher.launch(signInIntent);
            }
        });

        askNotificationPermission();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private void askNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                resultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void SigninWithPhone(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);

                } else {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void StartFirebaseLogin() {

        auth = FirebaseAuth.getInstance();
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(LoginActivity.this, "verification completed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                binding.progressBar.setVisibility(View.GONE);
                binding.Failed.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationCode = s;
                binding.progressBar.setVisibility(View.GONE);
                binding.Success.setVisibility(View.VISIBLE);
                binding.SendOtp.setVisibility(View.GONE);
                binding.otpsubmitt.setVisibility(View.VISIBLE);
                binding.SubmitOtp.setVisibility(View.VISIBLE);
                Toast.makeText(LoginActivity.this, "Code sent to " + phoneNumber, Toast.LENGTH_LONG).show();
            }
        };
    }
}