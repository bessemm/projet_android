package com.example.testing_gps_services;

import android.app.Activity;
import android.view.LayoutInflater;

import androidx.appcompat.app.AlertDialog;

import testing.gps_service.R;

public class LoadingDialog {
    private Activity activity;
    private AlertDialog alertDialog;

    LoadingDialog(Activity myActivity)
    {
        this.activity=myActivity;
    }
    void startLoadingDialog()
    {
        AlertDialog.Builder builder= new AlertDialog.Builder(activity);
        LayoutInflater infalter = activity.getLayoutInflater();
        builder.setView(infalter.inflate(R.layout.custom_dialog,null));
        builder.setCancelable(false);
        alertDialog=builder.create();
        alertDialog.show();
    }
    void dismissDialof()
    {
        alertDialog.dismiss();
    }
}
