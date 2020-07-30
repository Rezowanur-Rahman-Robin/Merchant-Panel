package com.robin.onlinecourierservice;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private View HomeView;
    private MerchantHomeAdapter merchantHomeAdapter;


    private FirebaseUser currentUser;


    ArrayList<MerchantProduct> merchantProductArrayList=new ArrayList<>();

    private DatabaseReference ProductRef;
    private FirebaseAuth mAuth;
    private ProgressDialog loadingBar;
    private String currentMerchantId;

    private String ProductsCount;
    private TextView totalProductsTextView,totalOrdersTextview,pendingOrdersTextview,completeOrdersTextView;



    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        HomeView= inflater.inflate(R.layout.fragment_home, container, false);


        mAuth=FirebaseAuth.getInstance();
        ProductRef= FirebaseDatabase.getInstance().getReference().child("Merchant-Products");

        currentUser = mAuth.getCurrentUser();

        if(currentUser!=null){
            currentMerchantId=currentUser.getUid();
        }


        recyclerView=(RecyclerView) HomeView.findViewById(R.id.dashBoard_recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));

        merchantHomeAdapter=new MerchantHomeAdapter(merchantProductArrayList,getContext());
        recyclerView.setAdapter(merchantHomeAdapter);

        loadingBar=new ProgressDialog(getContext());

        totalProductsTextView=HomeView.findViewById(R.id.total_products_dashboard);
        totalOrdersTextview=HomeView.findViewById(R.id.total_products_dashboard);
        pendingOrdersTextview=HomeView.findViewById(R.id.total_products_dashboard);
        completeOrdersTextView=HomeView.findViewById(R.id.total_products_dashboard);


        return HomeView;
    }

    @Override
    public void onStart() {
        super.onStart();


        if (currentUser==null){

            SendMerchantToLogin();
        }
        else {
            loadingBar.setTitle("Merchant Home");
            loadingBar.setMessage("Please Wait,until the loading is completed.....");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            ProductRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    merchantProductArrayList.clear();


                    for(DataSnapshot keyNode: dataSnapshot.getChildren()){
                        MerchantProduct merchantProduct=keyNode.getValue(MerchantProduct.class);
                        if(merchantProduct!=null){
                            if(merchantProduct.getId().equals(currentMerchantId)){


                                merchantProductArrayList.add(merchantProduct);
                                merchantHomeAdapter.notifyDataSetChanged();
                            }
                            ProductsCount= Integer.toString(merchantProductArrayList.size());
                            totalProductsTextView.setText(ProductsCount);

                            loadingBar.dismiss();


                        }
                    }






                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }






    }

    private void SendMerchantToLogin() {
        Intent loginIntent = new Intent(getContext(),MerchantLoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }


}
