package com.robin.onlinecourierservice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;

public class OrderDetails extends AppCompatActivity {

    TextView orderId,totalAmount,b_name,b_phone,b_order_time,payment_type,district,thana,detailsAddress;
    String o_id,t_amt,b_n,b_p,b_ot,p_t,dis,tha,det_add,u_id;
    Button requestForPickUp;

    private RecyclerView orderProductRecyclerView;
    private ProductDetailsAdapter productDetailsAdapter;
    private DatabaseReference merchantOrderProductRef;
    private String b_phn,currentMerchantId;
    private FirebaseAuth mAuth;

    private ProgressDialog loadingBar;
    private FirebaseUser currentUser;

    private DatabaseReference rootRef;

    TextView sumPriceForMerchant;

    private DatabaseReference userRef= FirebaseDatabase.getInstance().getReference().child("Users");

    private ArrayList<MerchantOrderProduct> merchantOrderProductArrayList=new ArrayList<>();




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        o_id=getIntent().getExtras().get("order_no").toString();
        t_amt=getIntent().getExtras().get("total_amount").toString();
        b_n=getIntent().getExtras().get("b_name").toString();
        u_id=getIntent().getExtras().get("user_id").toString();
        b_ot=getIntent().getExtras().get("b_order_time").toString();
        p_t=getIntent().getExtras().get("b_payment_type").toString();
        dis=getIntent().getExtras().get("district").toString();
        tha=getIntent().getExtras().get("thana").toString();
        det_add=getIntent().getExtras().get("address").toString();

        mAuth=FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();

        if(currentUser!=null){
            currentMerchantId=currentUser.getUid();
        }

        rootRef=FirebaseDatabase.getInstance().getReference();

        merchantOrderProductRef= FirebaseDatabase.getInstance().getReference().child("Purchase-Order").child(o_id).child("Products");

        sumPriceForMerchant=findViewById(R.id.total_amount_popup);



        loadingBar=new ProgressDialog(this);






        orderId=findViewById(R.id.order_no_popup);
        totalAmount=findViewById(R.id.total_amount_popup);
        b_name=findViewById(R.id.buyer_name_order_popup);
        b_phone=findViewById(R.id.buyer_phone_order_popup);
        b_order_time=findViewById(R.id.buyer_order_time_order_popup);
        payment_type=findViewById(R.id.buyer_payment_type_order_popup);
        district=findViewById(R.id.buyer_district_order_popup);
        thana=findViewById(R.id.buyer_thana_order_popup);
        detailsAddress=findViewById(R.id.buyer_add_details_order_popup);


        requestForPickUp=findViewById(R.id.request_pickup_button_popup);





        orderProductRecyclerView=(RecyclerView) findViewById(R.id.order_product_list);
        orderProductRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        productDetailsAdapter=new ProductDetailsAdapter(merchantOrderProductArrayList,getApplicationContext());
        orderProductRecyclerView.setAdapter(productDetailsAdapter);



        orderId.setText(o_id);
        totalAmount.setText(t_amt);
        b_name.setText(b_n);
        b_order_time.setText(b_ot);
        payment_type.setText(p_t);
        district.setText(dis);
        thana.setText(tha);
        detailsAddress.setText(det_add);


        requestForPickUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAllInfo();
            }
        });

    }



    @Override
    protected void onStart() {
        super.onStart();


        loadingBar.setTitle("Order Details");
        loadingBar.setMessage("Please Wait,until the loading is completed.....");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        userRef.child(u_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    b_phone.setText(dataSnapshot.getValue().toString());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        merchantOrderProductRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                merchantOrderProductArrayList.clear();
                final int[] sumPrice = {0};

                for(DataSnapshot d:dataSnapshot.getChildren()){
                     MerchantOrderProduct merchantOrderProduct=d.getValue(MerchantOrderProduct.class);

                    if(merchantOrderProduct!=null && (merchantOrderProduct.getMerchantID().equals(currentMerchantId))){

                        merchantOrderProductArrayList.add(merchantOrderProduct);
                        productDetailsAdapter.notifyDataSetChanged();
                        sumPrice[0] +=Integer.parseInt(merchantOrderProduct.getPrice())*Integer.parseInt(merchantOrderProduct.getQuantity())+Integer.parseInt(merchantOrderProduct.getDeliveryCharge());

                    }

                }
                loadingBar.dismiss();
                sumPriceForMerchant.setText(Integer.toString(sumPrice[0]));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                loadingBar.dismiss();
            }
        });





    }


    private void saveAllInfo() {

        loadingBar.setTitle("PickUp Request");
        loadingBar.setMessage("Please Wait,until the loading is completed.....");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();




        DatabaseReference merchantRef=FirebaseDatabase.getInstance().getReference().child("Merchants").child(currentMerchantId);

        merchantRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    String senderName=dataSnapshot.child("name").getValue().toString();
                    String senderPhone=dataSnapshot.child("phone").getValue().toString();

                    final DatabaseReference keyRef=rootRef.child("Merchant-Parcel").push();

                    final String key=keyRef.getKey();

                    HashMap<String,Object> merchantPercel =new HashMap<>();

                    merchantPercel.put("percel_id",key);
                    merchantPercel.put("merchant_id",currentMerchantId);
                    merchantPercel.put("order_no",o_id);
                    merchantPercel.put("buyer_id",u_id);
                    merchantPercel.put("sender_name",senderName);
                    merchantPercel.put("sender_phone",senderPhone);
                    merchantPercel.put("receiver_name",b_n);
                    merchantPercel.put("receiver_phone",b_phone.getText().toString());
                    merchantPercel.put("receiver_district",dis);
                    merchantPercel.put("receiver_thana",tha);
                    merchantPercel.put("receiver_address_details",det_add);
                    merchantPercel.put("order_time",b_ot);
                    merchantPercel.put("payment_type",p_t);
                    merchantPercel.put("total_amount_of_order",t_amt);
                    merchantPercel.put("total_amount_of_order_for_merchant",totalAmount.getText().toString());

                    rootRef.child("Merchant-parcel").child(key).setValue(merchantPercel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                rootRef.child("Merchant-Parcel-Status").child(key).setValue("pending");


                                merchantOrderProductRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                        for(DataSnapshot d:dataSnapshot.getChildren()){
                                            MerchantOrderProduct merchantOrderProduct=d.getValue(MerchantOrderProduct.class);

                                            if(merchantOrderProduct!=null && (merchantOrderProduct.getMerchantID().equals(currentMerchantId))){

                                                final String productId=merchantOrderProduct.getProductID();
                                                final String productPrice=merchantOrderProduct.getPrice();

                                                DatabaseReference productRef=FirebaseDatabase.getInstance().getReference().child("Merchant-Products").child(productId);
                                                productRef.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if(dataSnapshot.exists()){

                                                            final String senderDistrict=dataSnapshot.child("pickUp_district").getValue().toString();
                                                            final String senderThana=dataSnapshot.child("pickUp_thana").getValue().toString();
                                                            final String senderAddress=dataSnapshot.child("address_details").getValue().toString();


                                                            DatabaseReference PkeyRef=rootRef.child("Merchant-Parcel").child(key).child("Products").push();

                                                            String Pkey=PkeyRef.getKey();


                                                            HashMap<String,Object> merchantPercelP =new HashMap<>();


                                                            merchantPercelP.put("sender_district",senderDistrict);
                                                            merchantPercelP.put("sender_thana",senderThana);
                                                            merchantPercelP.put("sender_address_details",senderAddress);
                                                            merchantPercelP.put("product_id",productId);
                                                            merchantPercelP.put("product_price",productPrice);


                                                            rootRef.child("Merchant-parcel").child(key).child("Products").child(Pkey).setValue(merchantPercelP).addOnCompleteListener(
                                                                    new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()){

                                                                                loadingBar.dismiss();
                                                                                Toast.makeText(OrderDetails.this, "You have Successfully Requested for PickUp", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                            else {
                                                                                loadingBar.dismiss();
                                                                                Toast.makeText(OrderDetails.this, "Rrror: "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }
                                                                    }
                                                            );




                                                        }
                                                        else{
                                                            loadingBar.dismiss();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });

                                            }

                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {


                                    }
                                });

                            }
                            else {
                                loadingBar.dismiss();
                                Toast.makeText(OrderDetails.this, "Error:"+task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        }
                    });




                }
                else {
                    loadingBar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });








    }
}
