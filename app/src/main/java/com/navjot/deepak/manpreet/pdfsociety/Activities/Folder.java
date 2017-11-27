package com.navjot.deepak.manpreet.pdfsociety.Activities;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.navjot.deepak.manpreet.pdfsociety.R;

import java.lang.reflect.InvocationTargetException;

public class Folder extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder);

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case 101:
                if (requestCode == RESULT_OK) {

                    String path = data.getData().getPath();
                    Toast.makeText(Folder.this, path, Toast.LENGTH_SHORT).show();
                    break;
                }
        }
    }
}
