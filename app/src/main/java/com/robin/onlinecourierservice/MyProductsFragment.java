package com.robin.onlinecourierservice;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyProductsFragment extends Fragment {

    private View myProductsView;
    private RecyclerView myProductRecyclerView;
    private ProductViewAdapter productViewAdapter;

    ArrayList<MerchantProduct> merchantProductArrayList=new ArrayList<>();

    DatabaseReference merchantProductRef;
    private FirebaseAuth mAuth;
    private String currentMerchantId="";

    private ProgressDialog loadingBar;






    public MyProductsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myProductsView= inflater.inflate(R.layout.fragment_my_products, container, false);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentmerchant=mAuth.getCurrentUser();

        if(currentmerchant!=null){
            currentMerchantId=currentmerchant.getUid();
        }
        merchantProductRef= FirebaseDatabase.getInstance().getReference().child("Merchant-Products");

        myProductRecyclerView =(RecyclerView) myProductsView.findViewById(R.id.my_products_list);
        myProductRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        productViewAdapter=new ProductViewAdapter(merchantProductArrayList,getContext());
        myProductRecyclerView.setAdapter(productViewAdapter);

        loadingBar=new ProgressDialog(getContext());

        return myProductsView;
    }

    @Override
    public void onStart() {
        super.onStart();

        loadingBar.setTitle("My Products");
        loadingBar.setMessage("Please Wait,until the loading is completed.....");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        merchantProductRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                merchantProductArrayList.clear();

                for(DataSnapshot keyNode: dataSnapshot.getChildren()){
                    MerchantProduct merchantProduct=keyNode.getValue(MerchantProduct.class);
                    if(merchantProduct!=null){
                        if(merchantProduct.getId().equals(currentMerchantId)){

                            merchantProductArrayList.add(merchantProduct);
                            productViewAdapter.notifyDataSetChanged();
                        }
                    }
                }


                loadingBar.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });





    }


}
