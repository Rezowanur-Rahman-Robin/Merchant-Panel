package com.robin.onlinecourierservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MerchantLoginActivity extends AppCompatActivity {

    private EditText merchantEmail,merchantPassword;
    private Button merchantLoginButton,applyMerchantButton;
    private Toolbar toolbar;

    private ProgressDialog loadingbar;

    private FirebaseAuth mAuth;

    private DatabaseReference merchantRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_login);

        mAuth=FirebaseAuth.getInstance();
        merchantRef= FirebaseDatabase.getInstance().getReference().child("Merchants");

        merchantEmail=findViewById(R.id.merchantLoginEmail);
        merchantPassword=findViewById(R.id.merchantLoginPassword);
        merchantLoginButton=findViewById(R.id.merchant_signInButton);
        applyMerchantButton=findViewById(R.id.merchant_apply_button);

        loadingbar=new ProgressDialog(this);

        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        merchantLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLoginMerchant();
            }
        });



       applyMerchantButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               Intent applyIntent = new Intent(getApplicationContext(),MarchantRegisterActivity.class);
               startActivity(applyIntent);

           }
       });

    }

    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            SendUserToMainActivity();
        }

    }

    private void checkLoginMerchant() {


        String email=merchantEmail.getText().toString();
        String password =merchantPassword.getText().toString();

        if(email.isEmpty())
        {
            merchantEmail.setError("Enter an email address");
            merchantEmail.requestFocus();
            return;
        }

        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            merchantEmail.setError("Enter a valid email address");
            merchantEmail.requestFocus();
            return;
        }

        //checking the validity of the password
        if(password.isEmpty())
        {
            merchantPassword.setError("Enter a password");
            merchantPassword.requestFocus();
            return;
        }

        //check password length
        if(password.length()<6){
            Toast.makeText(this, "Password Length should be 6", Toast.LENGTH_LONG).show();
            merchantPassword.requestFocus();
            return;
        }

        loadingbar.setTitle("Sign In");
        loadingbar.setMessage("Please Wait Until the Login is Successfully Completed...");
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();

        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){




                                                SendUserToMainActivity();
                                                Toast.makeText(MerchantLoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                                                loadingbar.dismiss();



                        }
                        else {
                            loadingbar.dismiss();


                            Toast.makeText(MerchantLoginActivity.this, "Error: "+task.getException().toString(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void SendUserToMainActivity() {
        Intent Intent = new Intent(getApplicationContext(),MainActivity.class);
        Intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(Intent);
        finish();
    }


}
