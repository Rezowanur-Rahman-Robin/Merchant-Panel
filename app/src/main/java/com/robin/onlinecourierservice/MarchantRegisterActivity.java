package com.robin.onlinecourierservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class MarchantRegisterActivity extends AppCompatActivity {

    EditText merchantName,merchantphn,merchantEmail,businessName,businessDetails,businessAddress,bkashNumber,rocketNumber;
    Button applyMerchantButton;
    Toolbar toolbar;

    ProgressDialog loadingBar;

    private FirebaseAuth mAuth;

    private DatabaseReference rootRef;

   private String name,phone,email,businessname,businessdetails,businessaddress,bkashnumber,rocketnumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marchant_register);

        mAuth=FirebaseAuth.getInstance();

        rootRef= FirebaseDatabase.getInstance().getReference();

        loadingBar=new ProgressDialog(this);

        InitializeAllFields();

        applyMerchantButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMerchantApplyRequest();
            }
        });
    }



    private void InitializeAllFields() {

        merchantName=findViewById(R.id.merchant_name_registration);
        merchantphn=findViewById(R.id.merchant_phone_registration);
        merchantEmail=findViewById(R.id.merchant_email_registration);
        businessName=findViewById(R.id.merchant_business_name_registration);
        businessDetails=findViewById(R.id.merchant_business_details_registration);
        businessAddress=findViewById(R.id.merchant_business_Address_registration);
        bkashNumber=findViewById(R.id.merchant_bkash_num_registration);
        rocketNumber=findViewById(R.id.merchant_rocket_num_registration);
        applyMerchantButton=findViewById(R.id.apply_merchant);

        toolbar=findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Become A Merchant");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void sendMerchantApplyRequest() {
        loadingBar.setTitle("Apply For Merchant");
        loadingBar.setMessage("Please Wait Until the Process is Successfully Completed...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();


         name=merchantName.getText().toString();
         phone =merchantphn.getText().toString();
         email =merchantEmail.getText().toString();
         businessname =businessName.getText().toString();
         businessdetails =businessDetails.getText().toString();
         businessaddress =businessAddress.getText().toString();
         bkashnumber =bkashNumber.getText().toString();
         rocketnumber =rocketNumber.getText().toString();

        if(name.isEmpty()){
            merchantName.setError("Enter Your Name");
            merchantName.requestFocus();
            return;
        }
        if(phone.isEmpty()){
            merchantphn.setError("Enter Your Number");
            merchantphn.requestFocus();
            return;
        }
        if(email.isEmpty()){
            merchantEmail.setError("Enter Your Email");
            merchantEmail.requestFocus();
            return;
        }
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            Toast.makeText(this, "Enter a valid email address", Toast.LENGTH_SHORT).show();
            merchantEmail.requestFocus();
            return;
        }
        if(businessname.isEmpty()){
            businessName.setError("Enter Business Name");
            businessName.requestFocus();
            return;
        }
        if(businessdetails.isEmpty()){
            businessDetails.setError("Enter Business Details");
            businessDetails.requestFocus();
            return;
        }
        if(businessaddress.isEmpty()){
            businessAddress.setError("Enter Business Address");
            businessAddress.requestFocus();
            return;
        }if(bkashnumber.isEmpty()){
            bkashNumber.setError("Enter Bkash Number");
            bkashNumber.requestFocus();
            return;
        }


        DatabaseReference merchantRef=rootRef.child("Merchant Requests");
        String uniqueKey = merchantRef.push().getKey();
        MerchantRequest getmerchantinfo=new MerchantRequest(name,phone,email,businessname,businessdetails,businessaddress,bkashnumber,rocketnumber,uniqueKey);


        merchantRef.child(uniqueKey).setValue(getmerchantinfo);


        getRegisteredMerchantTemp();










        Toast.makeText(this, "You Have Successfully Applied For Merchant.", Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "We Will Reply You On Your Mail.", Toast.LENGTH_LONG).show();





    }

    private void getRegisteredMerchantTemp() {

        String password="123456";

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            String currentUserId=mAuth.getCurrentUser().getUid();

                            HashMap<String,Object> merchantRegisterMap=new HashMap<>();
                            merchantRegisterMap.put("id",currentUserId);
                            merchantRegisterMap.put("name",name);
                            merchantRegisterMap.put("phone",phone);
                            merchantRegisterMap.put("email",email);
                            merchantRegisterMap.put("businessName",businessname);
                            merchantRegisterMap.put("businessDetails",businessdetails);
                            merchantRegisterMap.put("businessAddress",businessaddress);
                            merchantRegisterMap.put("bkash",bkashnumber);
                            merchantRegisterMap.put("rocket",rocketnumber);
                            merchantRegisterMap.put("profile-image-link","");


                            rootRef.child("Merchants").child(currentUserId).setValue(merchantRegisterMap);

                            Toast.makeText(MarchantRegisterActivity.this, "MerchantRegistered Successfully.", Toast.LENGTH_SHORT).show();

                            loadingBar.dismiss();
                            gotoMainActivity();


                        }
                        else {
                            Toast.makeText(MarchantRegisterActivity.this, "Error:"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }




                    }
                });

    }

    private void gotoMainActivity() {

    }
}
