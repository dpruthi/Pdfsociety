package com.navjot.deepak.manpreet.pdfsociety.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.navjot.deepak.manpreet.pdfsociety.Models.Feedback;
import com.navjot.deepak.manpreet.pdfsociety.R;

public class FeedbackActivity extends AppCompatActivity {

    RatingBar RatingBar;
    EditText txtComment;
    Button buttonSubmit;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        setContentView(R.layout.activity_feedback);
        RatingBar = (RatingBar) findViewById(R.id.RatingBar);
        txtComment = (EditText) findViewById(R.id.editTextComment);
        buttonSubmit = (Button) findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Submit();
            }
        });
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

    }

    private boolean ValidateFeedback(){
            boolean result = true;

        if(RatingBar.getRating() == 0.0){
            Toast.makeText(getApplicationContext(),"Rating bar cannot be empty",Toast.LENGTH_SHORT).show();
            result = false;
        }
        if(TextUtils.isEmpty(txtComment.getText().toString().trim())){
            Toast.makeText(getApplicationContext(),"Comment section cannot be empty",Toast.LENGTH_SHORT).show();
            result = false;
        }
        else {
            Toast.makeText(getApplicationContext(),"Submit Successful",Toast.LENGTH_SHORT).show();

        }
        return result;

    }

        private void Submit() {

                if(ValidateFeedback()){
                    Feedback fb = new Feedback(

                            RatingBar.getRating(),
                            txtComment.getText().toString().trim()

                    );

                    mDatabase.child(getString(R.string.DB_Feedbacks))
                            .child(mAuth.getCurrentUser().getUid())
                            .setValue(fb);

                    startActivity(new Intent(FeedbackActivity.this, HomeActivity.class));
                }

        }


    public boolean onSupportNavigateUp(){
        onBackPressed();
        return true;
    }
}








