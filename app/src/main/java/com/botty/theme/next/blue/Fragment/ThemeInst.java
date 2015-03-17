package com.botty.theme.next.blue.Fragment;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.botty.theme.next.blue.R;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.ProgressCallback;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by BottyIvan on 19/01/15.
 */
public class ThemeInst extends Fragment {

    ImageButton mInstaTheme;
    ProgressDialog progressDialog;
    TextView info;
    String theme_name;

    Future<File> downloading;
    boolean downloaded = false;

    public ThemeInst() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.theme_inst, container,
                false);

        info = (TextView)view.findViewById(R.id.info);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            theme_name = "blue_next_lollipop";
            info.setText(Html.fromHtml("<body><br><b>NOTE: <font color='red'>THIS IS A BETA !!</font><br><br>Try this very very cool theme !! It's work with CM/PA/MAHDI and more !!</b></body>"));
        }else {
            theme_name = "blue_next_kitkat";
            info.setText(Html.fromHtml("<body><br><b>NOTE: <font color='blue'>THIS IS THE CURRENT RELEASE !!</font><br><br>Try this very very cool theme !! It's work with CM/PA/MAHDI and more !!</b></body>"));
        }

        mInstaTheme = (ImageButton)view.findViewById(R.id.download_theme);
        mInstaTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    AlertDialog.Builder miaAlert = new AlertDialog.Builder(getActivity());
                    miaAlert.setTitle("Options for Blue Next on Lollipop");
                    miaAlert.setMessage("Do you want try the dark version, with the setting app themed in black ?");
                    miaAlert.setPositiveButton("OK",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            theme_name = "blue_next_dark_lollipop";
                            DownloadTheme();
                        }
                    });
                    AlertDialog alert = miaAlert.create();
                    alert.show();
                } else {
                    DownloadTheme();
                }
            }
        });
        return view;
    }

    public void DownloadTheme(){
        if (downloading != null && !downloading.isCancelled()) {
            resetDownload();
            return;
        }
        downloading = Ion.with(getActivity())
                .load("http://gnexushd.altervista.org/dl_app/" + theme_name + ".apk")
                        // have a ProgressBar get updated automatically with the percent
                        // and a ProgressDialog
                .progressDialog(Progress())
                .progress(new ProgressCallback() {
                    @Override
                    public void onProgress(long downloaded, long total) {
                        System.out.println("" + downloaded + " / " + total);
                    }
                })
                .write(new File("/sdcard/" + theme_name + ".apk"))
                .setCallback(new FutureCallback<File>() {
                    @Override
                    public void onCompleted(Exception e, File file) {
                        // download done...
                        // do stuff with the File or error
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Downloaded !!", Toast.LENGTH_SHORT).show();
                        downloaded = true;
                        if (downloaded == true) {
                            File apkFile = new File("/sdcard/" + theme_name + ".apk");
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
                            startActivity(intent);
                        } else {
                            Toast.makeText(getActivity(), "error on install !!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public ProgressDialog Progress(){
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMessage("Downloading ...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;
    }

    void resetDownload() {
        // cancel any pending download
        downloading.cancel();
        downloading = null;
    }
}