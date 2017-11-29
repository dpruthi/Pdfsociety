package com.navjot.deepak.manpreet.pdfsociety.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.navjot.deepak.manpreet.pdfsociety.Models.Pdf;
import com.navjot.deepak.manpreet.pdfsociety.R;
import com.navjot.deepak.manpreet.pdfsociety.Services.MyDownloadService;

public class PdfDetailActivity extends AppCompatActivity implements View.OnClickListener {

    TextView userName;
    TextView pdfName;
    TextView pdfDescription;
    TextView pdfsize;
    TextView uploaddate;
    Button btndownload;

    private DatabaseReference mPdfReference;
    private ValueEventListener mPdfListener;
    private static String pdfkey;
    private static String uid;
    Pdf pdf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_detail);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        pdfkey = getIntent().getStringExtra("pdfkey");
        init();
        pdfListener();
    }

    public void init() {
        findViewById(R.id.btnDownload).setOnClickListener(this);
        userName = (TextView) findViewById(R.id.userName);
        pdfName = (TextView) findViewById(R.id.pdfName);
        pdfsize = (TextView) findViewById(R.id.textViewSize);
        pdfDescription = (TextView) findViewById(R.id.pdfDescription);
        uploaddate = (TextView) findViewById(R.id.uploaddate) ;
        mPdfReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.DB_Pdfs)).child(pdfkey);
        btndownload = (Button) findViewById(R.id.btnDownload);
        btndownload.setOnClickListener(this);
    }

    public void pdfListener() {
        mPdfListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pdf = dataSnapshot.getValue(Pdf.class);
                Log.d(getString(R.string.tag), "onDataChange: " + pdf);
                pdfName.setText(pdf.getPdfname());
                pdfDescription.setText(pdf.getDescription());
                userName.setText(pdf.getUsername());
                pdfsize.setText(String.format( "%.2f MB",pdf.getPdfsize()));
                uploaddate.setText(pdf.getUploaddate());
                uid = pdf.getUid();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(getString(R.string.tag), "loadPdf:onCancelled", databaseError.toException());
                Toast.makeText(PdfDetailActivity.this, "Failed to load pdf.",
                        Toast.LENGTH_SHORT).show();
            }
        };
        mPdfReference.addValueEventListener(mPdfListener);
    }

    private void beginDownload() {
        // Get path
        String pdfNAME = pdfName.getText().toString();

        startService(
                new Intent(this, MyDownloadService.class)
                        .putExtra("pdfname", pdfNAME)
                        .putExtra("pdfkey", pdfkey)
                        .putExtra("uid", uid)
                        .setAction(MyDownloadService.ACTION_DOWNLOAD)
        );

        Toast.makeText(PdfDetailActivity.this, getString(R.string.progress_downloading), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mPdfListener != null) {
            Log.d(getString(R.string.tag), "pdfListener stopped");
            mPdfReference.removeEventListener(mPdfListener);
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btnDownload:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:" + getPackageName()));
                    startActivity(intent);
                    Toast.makeText(this, "Please give storage permissions", Toast.LENGTH_LONG).show();
                } else {
                    beginDownload();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sendpdflink, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_sendurl:
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, pdf.getDownload_url());
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, "Send to"));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(PdfDetailActivity.this, HomeActivity.class));
        finish();
    }

    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}