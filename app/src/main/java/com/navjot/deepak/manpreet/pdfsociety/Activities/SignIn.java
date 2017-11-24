package com.navjot.deepak.manpreet.pdfsociety.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.navjot.deepak.manpreet.pdfsociety.R;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignIn extends Progressdialog {

    EditText txtEmail,txtPassword;
    Button btnSignIn;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();
        txtEmail = (EditText) findViewById(R.id.EditTextEmail);
        txtPassword = (EditText) findViewById(R.id.EditTextPassword);
        btnSignIn = (Button) findViewById(R.id.ButtonSignIn);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInClicked();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        Log.i(getString(R.string.tag),user+"");
        if(user != null){
            if (user.isEmailVerified()){
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            }
        }
    }

    private void SignInClicked(){

        if(TextUtils.isEmpty(txtEmail.getText().toString().trim())){
            txtEmail.setError("Fields can't be empty");

        }
        else if(TextUtils.isEmpty(txtPassword.getText().toString().trim())){
            txtPassword.setError("Fields can't be empty");
        }
            else if (!emailValidator(txtEmail.getText().toString())) {
            txtEmail.setError("Please enter a valid Email address ");
            }
            else{
                authSignIn(txtEmail.getText().toString().trim(), txtPassword.getText().toString().trim());
        }
    }

    public void authSignIn(String email, String password){
        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            checkIfEmailVerified();
                        } else {
                            try {
                                throw task.getException();
                            }
                            catch (FirebaseNetworkException e) {
                                Toast.makeText(SignIn.this, "Check your internet connection",Toast.LENGTH_SHORT).show();
                            }
                            catch (FirebaseAuthInvalidUserException e){
                                Toast.makeText(SignIn.this, "User doesn't exist", Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e) {
                                Log.d(getString(R.string.tag), "task.getException(): "+task.getException());
                                Toast.makeText(SignIn.this, ""+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }

                        hideProgressDialog();
                    }
                });
    }

    private void checkIfEmailVerified(){
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            Log.d(getString(R.string.tag), "checkIfEmailVerified: user: "+user);
            if (user.isEmailVerified()){
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            } else {
                Toast.makeText(getApplicationContext(), "Email verification is not complete",Toast.LENGTH_LONG).show();
            }
        }

    }

    public boolean emailValidator(String email)
    {
        Pattern pattern;
        Matcher matcher;
        final String  email_pattern = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+";
        pattern = Pattern.compile(email_pattern);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public void onclick(View view) {
        startActivity(new Intent(SignIn.this, SignUp.class));
    }

}
