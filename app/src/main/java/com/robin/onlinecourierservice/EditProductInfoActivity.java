package com.robin.onlinecourierservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class EditProductInfoActivity extends AppCompatActivity {

    private String pName,pDes,pDisPer,pDisP,pId,pImage,pPrice,pDistrict,pThana,pDetails,pType,pSize,pWeight,pPickUpInfo,product_type,product_size,product_weight,mId;
    private int p_D_id,p_T_id;

    private RadioGroup radioGroupPro_type,radioGroup_size,radioGroup_weight;
    private RadioButton radioButtontype,radioButtonSize,radioButtonWeight,radioBoxPackage,radioglasspackage,radios1,radios2,radios3,radiow1,radiow2,radiow3,radiow4,radiow5;
    private ImageButton uploadButton;
    private ImageView selectedImage;
    private static final int PICK_IMAGE=223;
    private Uri imageUri=null;
    private ProgressDialog loadingbar;

    private Spinner country_Spinner;
    private Spinner state_Spinner;



    private ArrayAdapter<EditProductInfoActivity.Country> countryArrayAdapter;
    private ArrayAdapter<EditProductInfoActivity.State> stateArrayAdapter;


    private ArrayList<EditProductInfoActivity.Country> countries;
    private ArrayList<EditProductInfoActivity.State> states;


    private Button editProductButton;

    private String selectedDistrict,selectedThana,lequidcheck;
    private int getSelectedDistrictcode,getSelectedThanacode;

    private EditText productnaame,product_descriptions,product_dis_percentage,product_address_details,product_pickUp_info,productprice;
    private TextView product_discount_price;


    private StorageReference mStorageRef;
    private DatabaseReference rootRef;

    private FirebaseAuth mAuth;
    private String currentMerchantId;

    private String mainUrl;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product_info);



        pId=getIntent().getExtras().get("edit_p_id").toString();
        pName=getIntent().getExtras().get("edit_p_name").toString();
        pDes=getIntent().getExtras().get("edit_p_des").toString();
        pDisPer=getIntent().getExtras().get("edit_p_dis_per").toString();
        pDisP=getIntent().getExtras().get("edit_p_dis_price").toString();
        pName=getIntent().getExtras().get("edit_p_name").toString();
        pName=getIntent().getExtras().get("edit_p_name").toString();
        pImage=getIntent().getExtras().get("edit_p_image").toString();
        pPrice=getIntent().getExtras().get("edit_p_price").toString();
        pDistrict=getIntent().getExtras().get("edit_p_district").toString();
        pThana=getIntent().getExtras().get("edit_p_thana").toString();
        pType=getIntent().getExtras().get("edit_p_type").toString();
        pSize=getIntent().getExtras().get("edit_p_volume").toString();
        pWeight=getIntent().getExtras().get("edit_p_weight").toString();
        pDetails=getIntent().getExtras().get("edit_p_address").toString();
        pPickUpInfo=getIntent().getExtras().get("edit_pickUp").toString();
        p_D_id=Integer.parseInt(getIntent().getExtras().get("edit_d_id").toString());
        p_T_id=Integer.parseInt(getIntent().getExtras().get("edit_t_id").toString());
        mId=getIntent().getExtras().get("edit_Id").toString();


        mStorageRef= FirebaseStorage.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        currentMerchantId=mAuth.getCurrentUser().getUid();
        rootRef= FirebaseDatabase.getInstance().getReference();


        initailizeall();




        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent gallery =new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(gallery,"Select Product Image"),PICK_IMAGE);
            }
        });


        SetAllinfo();




    }

    private void initailizeall() {

        radioGroupPro_type=findViewById(R.id.package_type_Group_edit);
        radioGroup_size=findViewById(R.id.sizeGroup_merchant_edit);
        radioGroup_weight=findViewById(R.id.weightGroup_merchant_edit);
        uploadButton=findViewById(R.id.product_image_upload_button_edit_product);
        selectedImage=findViewById(R.id.selected_product_image_edit_product);
        editProductButton=findViewById(R.id.edit_product_button_merchant);
        productnaame=findViewById(R.id.product_name_edit_product);
        product_descriptions=findViewById(R.id.product_des_edit_product);
        productprice=findViewById(R.id.product_price_edit_product);
        product_dis_percentage=findViewById(R.id.product_dis_percentage_edit_product);
        product_discount_price=findViewById(R.id.discount_price_text_edit_product);
        product_address_details=findViewById(R.id.product_details_address_edit_product);
        product_pickUp_info=findViewById(R.id.product_picUp_instructions_edit_product);




        radioBoxPackage=findViewById(R.id.p_type_1_edit);
        radioglasspackage=findViewById(R.id.p_type_2_edit);
        radios1=findViewById(R.id.size1_merchant_edit);
        radios2=findViewById(R.id.size2_merchant_edit);
        radios3=findViewById(R.id.size3_merchant_edit);
        radiow1=findViewById(R.id.w1_merchant_edit);
        radiow2=findViewById(R.id.w2_merchant_edit);
        radiow3=findViewById(R.id.w3_merchant_edit);
        radiow4=findViewById(R.id.w4_merchant_edit);
        radiow5=findViewById(R.id.w5_merchant_edit);







        loadingbar=new ProgressDialog(this);






        product_dis_percentage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String  priceS=productprice.getText().toString();
                String  discountS=product_dis_percentage.getText().toString();


                if ((!priceS.isEmpty())&&(!discountS.isEmpty())){
                    float price=Float.parseFloat(productprice.getText().toString());
                    float discount=Float.parseFloat(product_dis_percentage.getText().toString());

                    int discount_price=(int)(price-(discount/100)*price);

                    String dis_amount=Integer.toString(discount_price);
                    product_discount_price.setText(dis_amount);

                }
                else {
                    product_discount_price.setText("");

                }





            }
        });






        country_Spinner = (Spinner) findViewById(R.id.districts_edit_products);
        state_Spinner = (Spinner) findViewById(R.id.thana_edit_product);

        countries = new ArrayList<>();
        states = new ArrayList<>();


        createLists();

        countryArrayAdapter = new ArrayAdapter<EditProductInfoActivity.Country>(getApplicationContext(), R.layout.simple_spinner_dropdown_item, countries);
        countryArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        country_Spinner.setAdapter(countryArrayAdapter);

        stateArrayAdapter = new ArrayAdapter<EditProductInfoActivity.State>(getApplicationContext(), R.layout.simple_spinner_dropdown_item, states);
        stateArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        state_Spinner.setAdapter(stateArrayAdapter);



        country_Spinner.setOnItemSelectedListener(country_listener);
        state_Spinner.setOnItemSelectedListener(state_listener);


        editProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProductInfo();
            }
        });
    }




    private AdapterView.OnItemSelectedListener country_listener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position > 0) {
                final EditProductInfoActivity.Country country = (EditProductInfoActivity.Country) country_Spinner.getItemAtPosition(position);
                selectedDistrict=country.getCountryName();
                getSelectedDistrictcode=country.getCountryID();

                ArrayList<EditProductInfoActivity.State> tempStates = new ArrayList<>();

                tempStates.add(new EditProductInfoActivity.State(0, new EditProductInfoActivity.Country(0, "Choose a District"), "Choose a Thana"));

                for (EditProductInfoActivity.State singleState : states) {
                    if (singleState.getCountry().getCountryID() == country.getCountryID()) {
                        tempStates.add(singleState);
                    }
                }

                stateArrayAdapter = new ArrayAdapter<EditProductInfoActivity.State>(getApplicationContext(), R.layout.simple_spinner_dropdown_item, tempStates);
                stateArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                state_Spinner.setAdapter(stateArrayAdapter);
            }


        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private AdapterView.OnItemSelectedListener state_listener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position > 0) {
                final EditProductInfoActivity.State state = (EditProductInfoActivity.State) state_Spinner.getItemAtPosition(position);
                selectedThana=state.getStateName();
                getSelectedThanacode=state.getStateID();



            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };



    private void createLists() {
        EditProductInfoActivity.Country country0 = new EditProductInfoActivity.Country(0, "Choose a District");
        EditProductInfoActivity.Country country1 = new EditProductInfoActivity.Country(1, "Dhaka");
        EditProductInfoActivity.Country country2 = new EditProductInfoActivity.Country(2, "Chittagong");

        countries.add(new EditProductInfoActivity.Country(0, "Choose a District"));
        countries.add(new EditProductInfoActivity.Country(1, "Dhaka"));
        countries.add(new EditProductInfoActivity.Country(2, "Chittagong"));

        EditProductInfoActivity.State state0 = new EditProductInfoActivity.State(0, country0, "Choose a Thana");
        EditProductInfoActivity.State state1 = new EditProductInfoActivity.State(1, country1, "Mirpur");
        EditProductInfoActivity.State state2 = new EditProductInfoActivity.State(2, country1, "Gulshan");
        EditProductInfoActivity.State state3 = new EditProductInfoActivity.State(3, country2, "Agrabad");
        EditProductInfoActivity.State state4 = new EditProductInfoActivity.State(4, country2, "Pahartoli");

        states.add(state0);
        states.add(state1);
        states.add(state2);
        states.add(state3);
        states.add(state4);



    }

    private class Country implements Comparable<EditProductInfoActivity.Country> {

        private int countryID;
        private String countryName;


        public  Country(){

        }

        public Country(int countryID, String countryName) {
            this.countryID = countryID;
            this.countryName = countryName;
        }

        public int getCountryID() {
            return countryID;
        }

        public String getCountryName() {
            return countryName;
        }

        public void setCountryName(String countryName) {
            this.countryName = countryName;
        }

        @Override
        public String toString() {
            return countryName;
        }


        @Override
        public int compareTo(EditProductInfoActivity.Country another) {
            return this.getCountryID() - another.getCountryID();//ascending order
//            return another.getCountryID()-this.getCountryID();//descending  order
        }
    }

    private class State implements Comparable<EditProductInfoActivity.State> {

        private int stateID;
        private EditProductInfoActivity.Country country;
        private String stateName;

        public State(){

        }

        public State(int stateID, EditProductInfoActivity.Country country, String stateName) {
            this.stateID = stateID;
            this.country = country;
            this.stateName = stateName;
        }

        public int getStateID() {
            return stateID;
        }

        public EditProductInfoActivity.Country getCountry() {
            return country;
        }

        public String getStateName() {
            return stateName;
        }


        public void setStateName(String stateName) {
            this.stateName = stateName;
        }

        @Override
        public String toString() {
            return stateName;
        }

        @Override
        public int compareTo(EditProductInfoActivity.State another) {
            return this.getStateID() - another.getStateID();//ascending order
//            return another.getStateID()-this.getStateID();//descending order
        }
    }







    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK && requestCode==PICK_IMAGE && data!=null){

            imageUri = data.getData();
            try {
                Bitmap bitmap;
                bitmap = MediaStore.Images.Media.getBitmap(getApplication().getContentResolver(), imageUri);
                selectedImage.setVisibility(View.VISIBLE);
                selectedImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    private void SetAllinfo() {

        productnaame.setText(pName);

        productprice.setText(pPrice);
        product_address_details.setText(pDetails);
        product_pickUp_info.setText(pPickUpInfo);
        product_descriptions.setText(pDes);
        product_dis_percentage.setText(pDisPer);
        product_discount_price.setText(pDisP);

        Picasso.with(getApplicationContext()).load(pImage).into(selectedImage);

        if(pType.equals("Box Packaging")){
            radioBoxPackage.setChecked(true);
        }
        else
        {
            radioglasspackage.setChecked(true);
        }

        if(pSize.equals("s1")){
            radios1.setChecked(true);
        }
        else if(pSize.equals("s2"))
        {
            radios2.setChecked(true);
        }
        else {
            radios3.setChecked(true);
        }

        if(pWeight.equals("w1")){
            radiow1.setChecked(true);
        }
        else if(pWeight.equals("w2"))
        {
            radiow2.setChecked(true);
        }
        else if(pWeight.equals("w3")){
            radiow3.setChecked(true);
        }
        else if(pWeight.equals("w4")){
            radiow4.setChecked(true);
        }
        else {
            radiow5.setChecked(true);
        }




        EditProductInfoActivity.Country c=new EditProductInfoActivity.Country(p_D_id,pDistrict);
        EditProductInfoActivity.State s =new EditProductInfoActivity.State(p_T_id,c,pThana);

        //.country_Spinner.setSelection(countries.indexOf(c));
       // state_Spinner.setSelection(states.indexOf(s));


    }

    private void updateProductInfo() {

        int radioId=radioGroupPro_type.getCheckedRadioButtonId();
        radioButtontype=findViewById(radioId);
        if(radioButtontype==null){
            Toast.makeText(getApplicationContext(), "Please Check Product Type.", Toast.LENGTH_LONG).show();
            return;
        }
        product_type=radioButtontype.getText().toString();





        int radioId1=radioGroup_size.getCheckedRadioButtonId();
        radioButtonSize=findViewById(radioId1);
        if(radioButtonSize==null){
            Toast.makeText(getApplicationContext(), "Please Check Product Size.", Toast.LENGTH_LONG).show();
            return;
        }

        if(radioButtonSize.getText().toString().equals("Under 1 sq Feet")){
            product_size="s1";
        }
        else if(radioButtonSize.getText().toString().equals("1-2 sq Feet")){
            product_size="s2";
        }
        else
        {
            product_size="s3";
        }




        int radioId2=radioGroup_weight.getCheckedRadioButtonId();
        radioButtonWeight=findViewById(radioId2);
        if(radioButtonWeight==null){
            Toast.makeText(getApplicationContext(), "Please Check Product Weight.", Toast.LENGTH_LONG).show();
            return;
        }


        if(radioButtonWeight.getText().toString().equals("Under 500 gm")){
            product_weight="w1";
        }
        else if(radioButtonWeight.getText().toString().equals("0.5-1 kg")){
            product_weight="w2";
        }
        else if(radioButtonWeight.getText().toString().equals("1-3 kg")){
            product_weight="w3";
        }
        else if(radioButtonWeight.getText().toString().equals("4-7 kg")){
            product_weight="w4";
        }
        else {
            product_weight="w5";

        }



        final String p_name=productnaame.getText().toString();
        final String p_des=product_descriptions.getText().toString();
        final String p_price=productprice.getText().toString();
        final String addressDetails=product_address_details.getText().toString();
        final String pickUpInfo=product_pickUp_info.getText().toString();
        final String p_dis_per=product_dis_percentage.getText().toString();
        final String p_dis_price=product_discount_price.getText().toString();

        if(TextUtils.isEmpty(p_name)){
            productnaame.setError("Enter Product Name");
            productnaame.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(p_des)){
            product_descriptions.setError("Enter Product Description");
            product_descriptions.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(addressDetails)){
            product_address_details.setError("Enter Address Details");
            product_address_details.requestFocus();
            return;
        }
        if(TextUtils.isEmpty(p_price)){
            productprice.setError("Enter Product Price");
            productprice.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(selectedDistrict)){
            Toast.makeText(getApplicationContext(), "Please Choose District", Toast.LENGTH_SHORT).show();
            return;

        }
        if(TextUtils.isEmpty(selectedThana)){
            Toast.makeText(getApplicationContext(), "Please Choose Thana", Toast.LENGTH_SHORT).show();
            return;

        }

        loadingbar.setTitle("Update Product");
        loadingbar.setMessage("Please Wait Until the Product is Successfully Updated...");
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();

        final HashMap<String,Object> merchantProductInfo=new HashMap<>();


        if(imageUri!=null){

            final Random random=new Random();
            int ran= random.nextInt(100000000);

            StorageReference filePath=mStorageRef.child("Merchant-Product_Images").child(ran+"xyz.jpg");


            filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    final Task<Uri> firebaseUri=taskSnapshot.getStorage().getDownloadUrl();
                    firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            final String downloadUrl=uri.toString();

                             mainUrl= downloadUrl;





                            merchantProductInfo.put("id",currentMerchantId);
                            merchantProductInfo.put("p_id",pId);
                            merchantProductInfo.put("product_name",p_name);
                            merchantProductInfo.put("product_weight",product_weight);
                            merchantProductInfo.put("product_size",product_size);
                            merchantProductInfo.put("product_price",p_price);
                            merchantProductInfo.put("product_description",p_des);
                            merchantProductInfo.put("product_discount_percentage",p_dis_per);
                            merchantProductInfo.put("product_discount_price",p_dis_price);
                            merchantProductInfo.put("product_type",product_type);
                            merchantProductInfo.put("pickUp_district",selectedDistrict);
                            merchantProductInfo.put("pickUp_thana",selectedThana);
                            merchantProductInfo.put("address_details",addressDetails);
                            merchantProductInfo.put("pickUp_instructions",pickUpInfo);
                            merchantProductInfo.put("image_url",mainUrl);
                            merchantProductInfo.put("d_id",Integer.toString(getSelectedDistrictcode));
                            merchantProductInfo.put("t_id",Integer.toString(getSelectedThanacode));




                            rootRef.child("Merchant-Products").child(pId).updateChildren(merchantProductInfo)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task){

                                            if(task.isSuccessful()){
                                                loadingbar.dismiss();
                                                Toast.makeText(EditProductInfoActivity.this, "Product Info has been Successfully Updated.", Toast.LENGTH_SHORT).show();



                                            }
                                            else {
                                                loadingbar.dismiss();
                                                Toast.makeText(EditProductInfoActivity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();


                                            }
                                        }
                                    });


                        }
                    });



                }
            });
        }
        else {
           mainUrl =pImage;


            merchantProductInfo.put("id",currentMerchantId);
            merchantProductInfo.put("p_id",pId);
            merchantProductInfo.put("product_name",p_name);
            merchantProductInfo.put("product_weight",product_weight);
            merchantProductInfo.put("product_size",product_size);
            merchantProductInfo.put("product_price",p_price);
            merchantProductInfo.put("product_description",p_des);
            merchantProductInfo.put("product_discount_percentage",p_dis_per);
            merchantProductInfo.put("product_discount_price",p_dis_price);
            merchantProductInfo.put("product_type",product_type);
            merchantProductInfo.put("pickUp_district",selectedDistrict);
            merchantProductInfo.put("pickUp_thana",selectedThana);
            merchantProductInfo.put("address_details",addressDetails);
            merchantProductInfo.put("pickUp_instructions",pickUpInfo);
            merchantProductInfo.put("image_url",mainUrl);
            merchantProductInfo.put("d_id",Integer.toString(getSelectedDistrictcode));
            merchantProductInfo.put("t_id",Integer.toString(getSelectedThanacode));





            rootRef.child("Merchant-Products").child(pId).updateChildren(merchantProductInfo)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task){

                            if(task.isSuccessful()){
                                loadingbar.dismiss();
                                Toast.makeText(EditProductInfoActivity.this, "Product Info has been Successfully Updated.", Toast.LENGTH_SHORT).show();


                            }
                            else {
                                loadingbar.dismiss();
                                Toast.makeText(EditProductInfoActivity.this, "Error: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();


                            }
                        }
                    });
           }








    }
}


