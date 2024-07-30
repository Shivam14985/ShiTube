package com.example.shivambhardwaj.shitube.Services;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.example.shivambhardwaj.shitube.databinding.NoInternetConnectionBinding;


public class NetworkBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!isNetworkConnected(context)) {
            NoInternetConnectionBinding binding = NoInternetConnectionBinding.inflate(LayoutInflater.from(context));
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(binding.getRoot());
            Dialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
            binding.TRy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isNetworkConnected(context)) {
                        dialog.dismiss();
                    } else {
                        Toast.makeText(context, "No Internet Connection ", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
        }
    }

    public boolean isNetworkConnected(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
