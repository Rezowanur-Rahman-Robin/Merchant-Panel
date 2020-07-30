package com.robin.onlinecourierservice;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    private TextView topMerchantName,merchantEmail;
    private TextView merchantName,merchantPhone,businessName,businessDetails,businessAdd,bkashno,rocketno;

    private ImageButton editButton;

    private View  profileView;

    private CircleImageView merchantProfileImage;

    private FirebaseAuth mAuth;
    private DatabaseReference rootRef;
    private String currentMerchantId;

    private ProgressDialog loadingBar,loadingbarforImage;
    private static  final int galleryPic = 1;
    private Uri imageUri;

    private StorageReference MerchantProfileImageRef;

    private AlertDialog.Builder dialogueBuilder;
    private AlertDialog dialogue;
    private EditText merchantName_E,merchantPhone_E,businessName_E,businessDetails_E,businessAdd_E,bkashno_E,rocketno_E;
    private Button Update_B,Cancel_B;





    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

         profileView=inflater.inflate(R.layout.fragment_profile, container, false);

         mAuth=FirebaseAuth.getInstance();
         rootRef= FirebaseDatabase.getInstance().getReference();
         MerchantProfileImageRef= FirebaseStorage.getInstance().getReference().child("Merchant-Profile-Image");
         currentMerchantId=mAuth.getCurrentUser().getUid();



        InitializeAllFields();


        RetriveUserInfo();

        merchantProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent =new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(Intent.createChooser(galleryIntent,"Select Profile Image"),galleryPic);
            }
        });


        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OpenEditNewDialogBox();
            }
        });




        return profileView;
    }




    private void InitializeAllFields() {

        topMerchantName=profileView.findViewById(R.id.merchant_name_profile_top);
        merchantEmail=profileView.findViewById(R.id.merchant_email_profile_account);
        merchantName=profileView.findViewById(R.id.merchant_name_profile_TextView);
        merchantPhone=profileView.findViewById(R.id.merchant_phone_profile);
        businessName=profileView.findViewById(R.id.business_name_profile_TextView);
        businessDetails=profileView.findViewById(R.id.business_details_profile);
        businessAdd=profileView.findViewById(R.id.business_address_profile);
        bkashno=profileView.findViewById(R.id.bkash_num_profile_TextView);
        rocketno=profileView.findViewById(R.id.merchant_rocket_num_profile);
        merchantProfileImage=profileView.findViewById(R.id.merchant_profile_image_profile);
        editButton=profileView.findViewById(R.id.edit_profile_button);


        loadingBar=new ProgressDialog(getContext());
        loadingbarforImage=new ProgressDialog(getContext());

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==galleryPic  && resultCode== Activity.RESULT_OK && data!=null){

            imageUri=data.getData();
            try {
                Bitmap bitmap;
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                merchantProfileImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

            loadingbarforImage.setTitle("Set Profile Image.");
            loadingbarforImage.setMessage("Please Wait,Your Profile Image is updating....");
            loadingbarforImage.setCanceledOnTouchOutside(false);
            loadingbarforImage.show();

            StorageReference filePath=MerchantProfileImageRef.child(currentMerchantId).child(currentMerchantId+".jpg");

            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    final Task<Uri> firebaseUri=taskSnapshot.getStorage().getDownloadUrl();
                    firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            final String downloadUrl=uri.toString();

                            DatabaseReference profileImageRef=rootRef.child("Merchants").child(currentMerchantId);

                            profileImageRef.child("profile-image-link")
                            .setValue(downloadUrl)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        loadingbarforImage.dismiss();
                                    }
                                    else {
                                        loadingbarforImage.dismiss();
                                        Toast.makeText(getContext(), "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });

                }
            });
        }
    }

    private void RetriveUserInfo() {

        loadingBar.setTitle("My Profile");
        loadingBar.setMessage("Please Wait Until Your Profile Loading Successfully Completed...");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        rootRef.child("Merchants").child(currentMerchantId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if((dataSnapshot.exists() && dataSnapshot.hasChild("profile-image-link")) ){



                            String retriveMerchantImage=dataSnapshot.child("profile-image-link").getValue().toString();

                            if(!retriveMerchantImage.isEmpty()){
                                Picasso.with(getContext()).load(retriveMerchantImage).into(merchantProfileImage);
                            }


                        }


                            String retriveMerchantName=dataSnapshot.child("name").getValue().toString();
                            String retriveMerchantPhone=dataSnapshot.child("phone").getValue().toString();
                            String retriveMerchantEmail=dataSnapshot.child("email").getValue().toString();
                            String retriveMerchantBusinessName=dataSnapshot.child("businessName").getValue().toString();
                            String retriveMerchantBusinessAddress=dataSnapshot.child("businessAddress").getValue().toString();
                            String retriveMerchantBusinessDetails=dataSnapshot.child("businessDetails").getValue().toString();
                            String retriveMerchantBkash=dataSnapshot.child("bkash").getValue().toString();
                            String retriveMerchantRocket=dataSnapshot.child("rocket").getValue().toString();



                            topMerchantName.setText(retriveMerchantName);
                            merchantName.setText(retriveMerchantName);
                            merchantPhone.setText(retriveMerchantPhone);
                            merchantEmail.setText(retriveMerchantEmail);
                            businessName.setText(retriveMerchantBusinessName);
                            businessDetails.setText(retriveMerchantBusinessDetails);
                            businessAdd.setText(retriveMerchantBusinessAddress);
                            bkashno.setText(retriveMerchantBkash);
                            rocketno.setText(retriveMerchantRocket);

                            loadingBar.dismiss();

                            loadingBar.dismiss();






                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }


    private void OpenEditNewDialogBox() {

             dialogueBuilder = new AlertDialog.Builder(getContext());
             final View EditProfileView=getLayoutInflater().inflate(R.layout.edit_profile_layout,null);


         merchantName_E=(EditText) EditProfileView.findViewById(R.id.merchant_name_profile_EditText);
        merchantPhone_E=(EditText) EditProfileView.findViewById(R.id.merchant_Phone_profile_EditText);
        businessName_E=(EditText) EditProfileView.findViewById(R.id.business_name_profile_EditText);
        businessDetails_E=(EditText) EditProfileView.findViewById(R.id.business_details_profile_EditText);
        businessAdd_E=(EditText) EditProfileView.findViewById(R.id.business_address_profile_EditText);
        bkashno_E=(EditText) EditProfileView.findViewById(R.id.bkash_profile_EditText);
        rocketno_E=(EditText) EditProfileView.findViewById(R.id.rocket_number_profile_EditText);

        Update_B=(Button) EditProfileView.findViewById(R.id.update_popup_merchant);
        Cancel_B=(Button) EditProfileView.findViewById(R.id.cancel_popup_merchant);

        dialogueBuilder.setView(EditProfileView);
        dialogue=dialogueBuilder.create();
        dialogue.show();

        Update_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateMerchantProfileInfo();
            }


        });

        Cancel_B.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogue.dismiss();
            }
        });






    }

    private void UpdateMerchantProfileInfo() {

        String setMerChantname=merchantName_E.getText().toString();
        String setMerChantPhone=merchantPhone_E.getText().toString();
        String setMerChantbkash=bkashno_E.getText().toString();
        String setMerChantrocket=rocketno_E.getText().toString();
        String setBusinessName=businessName_E.getText().toString();
        String setBusinessDetails=businessDetails_E.getText().toString();
        String setBusinessAddress=businessAdd_E.getText().toString();

        DatabaseReference merchantREf=rootRef.child("Merchants").child(currentMerchantId);


        if(!TextUtils.isEmpty(setMerChantname)){
            merchantREf.child("name").setValue(setMerChantname);
        }
        if(!TextUtils.isEmpty(setMerChantPhone)){
            merchantREf.child("phone").setValue(setMerChantPhone);
        }
        if(!TextUtils.isEmpty(setBusinessName)){
            merchantREf.child("businessName").setValue(setBusinessName);
        }
        if(!TextUtils.isEmpty(setBusinessDetails)){
            merchantREf.child("businessDetails").setValue(setBusinessDetails);
        }
        if(!TextUtils.isEmpty(setBusinessAddress)){
            merchantREf.child("businessAddress").setValue(setBusinessAddress);
        }
        if(!TextUtils.isEmpty(setMerChantbkash)){
            merchantREf.child("bkash").setValue(setMerChantbkash);
        }
        if(!TextUtils.isEmpty(setMerChantrocket)){
            merchantREf.child("rocket").setValue(setMerChantrocket);

        }

        dialogue.dismiss();

        Toast.makeText(getContext(), "Successfully Updated Your Info", Toast.LENGTH_SHORT).show();
        Fragment fragment = new ProfileFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

}
