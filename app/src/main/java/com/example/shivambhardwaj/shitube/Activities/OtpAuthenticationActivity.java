package com.example.shivambhardwaj.shitube.Activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.shivambhardwaj.shitube.R;
import com.example.shivambhardwaj.shitube.Services.NetworkBroadcast;
import com.example.shivambhardwaj.shitube.databinding.ActivityOtpAuthenticationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class OtpAuthenticationActivity extends AppCompatActivity {
    ActivityOtpAuthenticationBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    ProgressDialog progressDialog;
    String phoneNumber, otp;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private String verificationCode;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityOtpAuthenticationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        broadcastReceiver = new NetworkBroadcast();
        registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering");

        StartFirebaseLogin();

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        binding.SingUpUsingEmil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change the background color temporarily
                int originalColor = Color.parseColor("#FFFFFFFF");
                int clickedColor = Color.parseColor("#E8E8E8"); // Replace with your desired color

                // Change background color when clicked
                binding.SingUpUsingEmil.setBackgroundColor(clickedColor);

                // Set a delayed runnable to revert the color after a short duration (e.g., 500 milliseconds)
                binding.SingUpUsingEmil.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.SingUpUsingEmil.setBackgroundColor(originalColor);
                    }
                }, 100);
                Intent intent = new Intent(OtpAuthenticationActivity.this, SignUpActivity.class);
                startActivity(intent);
                finish();
            }
        });
        binding.SendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.Phone.getText().toString().isEmpty()||binding.EtName.getText().toString().isEmpty()) {
                    Snackbar snackbar = Snackbar.make(v, "Fill all Details ", Snackbar.LENGTH_SHORT);
                    snackbar.setTextColor(Color.WHITE);
                    snackbar.setBackgroundTint(Color.RED);
                    snackbar.show();

                } else {
                    binding.Success.setVisibility(View.GONE);
                    binding.Failed.setVisibility(View.GONE);
                    binding.progressBar.setVisibility(View.VISIBLE);
                    phoneNumber = binding.Phone.getText().toString();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" + phoneNumber,                // Phone number to verify
                            60,                           // Timeout duration
                            TimeUnit.SECONDS,                // Unit of timeout
                            OtpAuthenticationActivity.this,        // Activity (for callback binding)
                            mCallback);                      // OnVerificationStateChangedCallbacks
                }
            }
        });

        binding.SubmitOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.otpsubmit.getText().toString().isEmpty()) {
                    Snackbar snackbar = Snackbar.make(v, "Enter Otp", Snackbar.LENGTH_SHORT);
                    snackbar.setTextColor(Color.WHITE);
                    snackbar.setBackgroundTint(Color.RED);
                    snackbar.show();
                } else {
                    progressDialog.show();
                    otp = binding.otpsubmit.getText().toString();
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, otp);
                    SigninWithPhone(credential);
                }
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void SigninWithPhone(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    binding.registeredsuccess.setVisibility(View.VISIBLE);
                    binding.layout.setVisibility(View.GONE);
                    binding.registeredsuccess.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(OtpAuthenticationActivity.this, "Welcome" + binding.EtName.getText().toString(), Toast.LENGTH_LONG).show();
                            database.getReference().child("Users").child(task.getResult().getUser().getUid()).child("number").setValue(phoneNumber);
                            database.getReference().child("Users").child(task.getResult().getUser().getUid()).child("userName").setValue(binding.EtName.getText().toString());
                            Intent intent = new Intent(OtpAuthenticationActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }, 4500);
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(OtpAuthenticationActivity.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void StartFirebaseLogin() {

        auth = FirebaseAuth.getInstance();
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(OtpAuthenticationActivity.this, "verification completed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(
                        OtpAuthenticationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(OtpAuthenticationActivity.this, "Code sent to " + phoneNumber, Toast.LENGTH_LONG).show();
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}