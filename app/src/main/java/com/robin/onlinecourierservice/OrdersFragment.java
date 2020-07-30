package com.robin.onlinecourierservice;


import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrdersFragment extends Fragment {

    private View orderView;
    private RecyclerView orderRecyclerView;
    private MerchantOrderAdapter merchantOrderAdapter;

    ArrayList<MerchantOrder> merchantOrderArrayList=new ArrayList<>();
    private DatabaseReference merchantOrdersRef;
    private FirebaseAuth mAuth;
    private String currentMerchantId="";

    private ProgressDialog loadingBar;





    public OrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        orderView= inflater.inflate(R.layout.fragment_orders, container, false);


        mAuth=FirebaseAuth.getInstance();

        FirebaseUser currentmerchant=mAuth.getCurrentUser();

        if(currentmerchant!=null){
            currentMerchantId=currentmerchant.getUid();
        }

        merchantOrdersRef= FirebaseDatabase.getInstance().getReference().child("Purchase-Order");

        orderRecyclerView=(RecyclerView) orderView.findViewById(R.id.my_orders_list);
        orderRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        merchantOrderAdapter=new MerchantOrderAdapter(merchantOrderArrayList,getContext());
        orderRecyclerView.setAdapter(merchantOrderAdapter);


        loadingBar=new ProgressDialog(getContext());



        return orderView;
    }

    @Override
    public void onStart() {
        super.onStart();

        loadingBar.setTitle("Orders");
        loadingBar.setMessage("Please Wait,until the loading is completed.....");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();

        merchantOrdersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                merchantOrderArrayList.clear();

                for(DataSnapshot keyNode:dataSnapshot.getChildren()){
                    final MerchantOrder merchantOrder=keyNode.getValue(MerchantOrder.class);
                    if (merchantOrder!=null){

                        String oderKey=keyNode.getKey();

                            DatabaseReference orderProductRef=merchantOrdersRef.child(oderKey).child("Products");
                            orderProductRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    for (DataSnapshot keynode2:dataSnapshot.getChildren()){
                                        MerchantOrderProduct merchantOrderProduct=keynode2.getValue(MerchantOrderProduct.class);

                                        if(merchantOrderProduct!=null){
                                            if(merchantOrderProduct.getMerchantID().equals(currentMerchantId)){
                                                merchantOrderArrayList.add(merchantOrder);
                                                merchantOrderAdapter.notifyDataSetChanged();
                                                break;
                                            }
                                        }
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

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
