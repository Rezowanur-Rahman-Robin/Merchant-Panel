package com.robin.onlinecourierservice;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddProductFragment extends Fragment {

    private RadioGroup radioGroupPro_type,radioGroup_size,radioGroup_weight;
    private RadioButton radioButtontype,radioButtonSize,radioButtonWeight;
    private View addProductView;
    private ImageButton uploadButton;
    private ImageView selectedImage;
    private static final int PICK_IMAGE=233;
    private Uri imageUri;

    private ProgressDialog loadingbar;

    private Spinner country_Spinner;
    private Spinner state_Spinner;


    private ArrayAdapter<AddProductFragment.Country> countryArrayAdapter;
    private ArrayAdapter<AddProductFragment.State> stateArrayAdapter;


    private ArrayList<AddProductFragment.Country> countries;
    private ArrayList<AddProductFragment.State> states;


    private Button addProductButton;

    private String selectedDistrict,selectedThana,product_type,product_size,product_weight;
    private int getSelectedDistrictcode,getSelectedThanacode;

    private EditText productName,product_descriptions,product_dis_percentage,product_address_details,product_pickUp_info,productPrice;
    private TextView product_discount_price;

    private StorageReference mStorageRef;
    private DatabaseReference rootRef;

    private FirebaseAuth mAuth;
    private String currentMerchantId;


    public AddProductFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        addProductView = inflater.inflate(R.layout.fragment_add_product, container, false);


        mAuth=FirebaseAuth.getInstance();
        currentMerchantId=mAuth.getCurrentUser().getUid();
        rootRef= FirebaseDatabase.getInstance().getReference();
        mStorageRef= FirebaseStorage.getInstance().getReference();





        initializeUI();



        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent gallery =new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(gallery,"Select Product Image"),PICK_IMAGE);
            }
        });

        addProductButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                uploaddatasinFirebasae();
            }
        });



        return addProductView;
    }





    private void initializeUI() {

        radioGroupPro_type=addProductView.findViewById(R.id.package_type_Group);
        radioGroup_size=addProductView.findViewById(R.id.sizeGroup_merchant);
        radioGroup_weight=addProductView.findViewById(R.id.weightGroup_merchant);
        uploadButton=addProductView.findViewById(R.id.product_image_upload_button_add_product);
        selectedImage=addProductView.findViewById(R.id.selected_product_image_add_product);
        addProductButton=addProductView.findViewById(R.id.add_product_button_merchant);
        productName=addProductView.findViewById(R.id.product_name_add_product);
        product_descriptions=addProductView.findViewById(R.id.product_des_add_product);
        productPrice=addProductView.findViewById(R.id.product_price_add_product);
        product_dis_percentage=addProductView.findViewById(R.id.product_dis_percentage_add_product);
        product_discount_price=addProductView.findViewById(R.id.discount_price_text_add_product);
        product_address_details=addProductView.findViewById(R.id.address_Details_add_product_merchant);
        product_pickUp_info=addProductView.findViewById(R.id.pickUp_instruction_merchant);
        loadingbar=new ProgressDialog(getContext());



        product_dis_percentage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String  priceS=productPrice.getText().toString();
                String  discountS=product_dis_percentage.getText().toString();


                if ((!priceS.isEmpty())&&(!discountS.isEmpty())){
                    float price=Float.parseFloat(productPrice.getText().toString());
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




        country_Spinner = (Spinner) addProductView.findViewById(R.id.districts_add_products);
        state_Spinner = (Spinner) addProductView.findViewById(R.id.thana_add_product);

        countries = new ArrayList<>();
        states = new ArrayList<>();


        createLists();

        countryArrayAdapter = new ArrayAdapter<AddProductFragment.Country>(getContext(), R.layout.simple_spinner_dropdown_item, countries);
        countryArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        country_Spinner.setAdapter(countryArrayAdapter);

        stateArrayAdapter = new ArrayAdapter<AddProductFragment.State>(getContext(), R.layout.simple_spinner_dropdown_item, states);
        stateArrayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        state_Spinner.setAdapter(stateArrayAdapter);



        country_Spinner.setOnItemSelectedListener(country_listener);
        state_Spinner.setOnItemSelectedListener(state_listener);


    }

    private AdapterView.OnItemSelectedListener country_listener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position > 0) {
                final AddProductFragment.Country country = (AddProductFragment.Country) country_Spinner.getItemAtPosition(position);
                selectedDistrict=country.getCountryName();
                getSelectedDistrictcode=country.getCountryID();

                ArrayList<AddProductFragment.State> tempStates = new ArrayList<>();

                tempStates.add(new State(0, new Country(0, "Choose a District"), "Choose a Thana"));

                for (AddProductFragment.State singleState : states) {
                    if (singleState.getCountry().getCountryID() == country.getCountryID()) {
                        tempStates.add(singleState);
                    }
                }

                stateArrayAdapter = new ArrayAdapter<AddProductFragment.State>(getContext(), R.layout.simple_spinner_dropdown_item, tempStates);
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
                final AddProductFragment.State state = (AddProductFragment.State) state_Spinner.getItemAtPosition(position);
                selectedThana=state.getStateName();
                getSelectedThanacode=state.getStateID();




            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };



    private void createLists() {
        AddProductFragment.Country country0 = new Country(0, "Choose a District");
        AddProductFragment.Country country1 = new Country(1, "Dhaka");
        AddProductFragment.Country country2 = new Country(2, "Chittagong");

        countries.add(new Country(0, "Choose a District"));
        countries.add(new Country(1, "Dhaka"));
        countries.add(new Country(2, "Chittagong"));

        AddProductFragment.State state0 = new State(0, country0, "Choose a Thana");
        AddProductFragment.State state1 = new State(1, country1, "Mirpur");
        AddProductFragment.State state2 = new State(2, country1, "Gulshan");
        AddProductFragment.State state3 = new State(3, country2, "Agrabad");
        AddProductFragment.State state4 = new State(4, country2, "Pahartoli");

        states.add(state0);
        states.add(state1);
        states.add(state2);
        states.add(state3);
        states.add(state4);



    }

    private class Country implements Comparable<AddProductFragment.Country> {

        private int countryID;
        private String countryName;


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

        @Override
        public String toString() {
            return countryName;
        }


        @Override
        public int compareTo(AddProductFragment.Country another) {
            return this.getCountryID() - another.getCountryID();//ascending order
//            return another.getCountryID()-this.getCountryID();//descending  order
        }
    }

    private class State implements Comparable<AddProductFragment.State> {

        private int stateID;
        private AddProductFragment.Country country;
        private String stateName;

        public State(int stateID, AddProductFragment.Country country, String stateName) {
            this.stateID = stateID;
            this.country = country;
            this.stateName = stateName;
        }

        public int getStateID() {
            return stateID;
        }

        public AddProductFragment.Country getCountry() {
            return country;
        }

        public String getStateName() {
            return stateName;
        }

        @Override
        public String toString() {
            return stateName;
        }

        @Override
        public int compareTo(AddProductFragment.State another) {
            return this.getStateID() - another.getStateID();//ascending order
//            return another.getStateID()-this.getStateID();//descending order
        }
    }

    private class City implements Comparable<AddProductFragment.City> {

        private int cityID;
        private AddProductFragment.Country country;
        private AddProductFragment.State state;
        private String cityName;

        public City(int cityID, AddProductFragment.Country country, AddProductFragment.State state, String cityName) {
            this.cityID = cityID;
            this.country = country;
            this.state = state;
            this.cityName = cityName;
        }

        public int getCityID() {
            return cityID;
        }

        public AddProductFragment.Country getCountry() {
            return country;
        }

        public AddProductFragment.State getState() {
            return state;
        }

        public String getCityName() {
            return cityName;
        }

        @Override
        public String toString() {
            return cityName;
        }

        @Override
        public int compareTo(AddProductFragment.City another) {
            return this.cityID - another.getCityID();//ascending order
//            return another.getCityID() - this.cityID;//descending order
        }
    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode== Activity.RESULT_OK && requestCode==PICK_IMAGE && data!=null){

            imageUri = data.getData();
            try {
                Bitmap bitmap;
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                selectedImage.setVisibility(View.VISIBLE);
                selectedImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void uploaddatasinFirebasae() {



        int radioId=radioGroupPro_type.getCheckedRadioButtonId();
        radioButtontype=addProductView.findViewById(radioId);
        if(radioButtontype==null){
            Toast.makeText(getContext(), "Please Check Product Type.", Toast.LENGTH_LONG).show();
            return;
        }
            product_type=radioButtontype.getText().toString();





        int radioId1=radioGroup_size.getCheckedRadioButtonId();
        radioButtonSize=addProductView.findViewById(radioId1);
        if(radioButtonSize==null){
            Toast.makeText(getContext(), "Please Check Product Size.", Toast.LENGTH_LONG).show();
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
        radioButtonWeight=addProductView.findViewById(radioId2);
        if(radioButtonWeight==null){
            Toast.makeText(getContext(), "Please Check Product Weight.", Toast.LENGTH_LONG).show();
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




        final String p_name=productName.getText().toString();
        final String p_des=product_descriptions.getText().toString();
        final String addressDetails=product_address_details.getText().toString();
        final String pickUpInfo=product_pickUp_info.getText().toString();
        final String p_price=productPrice.getText().toString();
        final String p_dis_per=product_dis_percentage.getText().toString();
        final String p_dis_price=product_discount_price.getText().toString();


        if(TextUtils.isEmpty(p_name)){
            productName.setError("Enter Product Name");
            productName.requestFocus();
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
            productPrice.setError("Enter Product Price");
            productPrice.requestFocus();
            return;
        }

        if(imageUri==null){
            Toast.makeText(getContext(), "Please Enter Product Image", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(selectedDistrict)){
            Toast.makeText(getContext(), "Please Choose District", Toast.LENGTH_SHORT).show();
            return;

        }
        if(TextUtils.isEmpty(selectedThana)){
            Toast.makeText(getContext(), "Please Choose Thana", Toast.LENGTH_SHORT).show();
            return;

        }

        loadingbar.setTitle("Add Product");
        loadingbar.setMessage("Please Wait Until the Product is Successfully Added...");
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();



        final Random random=new Random();
        int ran= random.nextInt(100000000);

        StorageReference filePath=mStorageRef.child("Merchant-Product_Images").child(ran+"abc.jpg");


        filePath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                final Task<Uri> firebaseUri=taskSnapshot.getStorage().getDownloadUrl();
                firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        final String downloadUrl=uri.toString();

                        DatabaseReference KeyRef=rootRef.child("Merchant-Products")
                                .push();

                        String messagePushKey =KeyRef.getKey();

                        HashMap<String,Object> merchantProductInfo=new HashMap<>();

                        merchantProductInfo.put("id",currentMerchantId);
                        merchantProductInfo.put("p_id",messagePushKey);
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
                        merchantProductInfo.put("image_url",downloadUrl);
                        merchantProductInfo.put("d_id",Integer.toString(getSelectedDistrictcode));
                        merchantProductInfo.put("t_id",Integer.toString(getSelectedThanacode));




                        rootRef.child("Merchant-Products").child(messagePushKey).setValue(merchantProductInfo).addOnCompleteListener(
                                new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getContext(), "Product added Successfully", Toast.LENGTH_SHORT).show();
                                            loadingbar.dismiss();

                                            radioGroup_size.clearCheck();
                                            radioGroup_weight.clearCheck();
                                            radioGroupPro_type.clearCheck();
                                            productName.setText("");
                                            product_descriptions.setText("");
                                            productPrice.setText("");
                                            product_dis_percentage.setText("");
                                            product_discount_price.setText("");
                                            product_address_details.setText("");
                                            product_pickUp_info.setText("");

                                        }
                                        else {
                                            Toast.makeText(getContext(), "Error:"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            loadingbar.dismiss();

                                            radioGroup_size.clearCheck();
                                            radioGroup_weight.clearCheck();
                                            radioGroupPro_type.clearCheck();
                                            productName.setText("");
                                            product_descriptions.setText("");
                                            productPrice.setText("");
                                            product_dis_percentage.setText("");
                                            product_discount_price.setText("");
                                            product_address_details.setText("");
                                            product_pickUp_info.setText("");
                                        }
                                    }
                                }
                        );



                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        loadingbar.dismiss();
                    }
                });
            }
        });
















    }
}
