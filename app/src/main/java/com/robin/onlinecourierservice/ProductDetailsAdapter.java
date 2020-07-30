package com.robin.onlinecourierservice;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductDetailsAdapter extends RecyclerView.Adapter<ProductDetailsAdapter.OrderProductHolder> {

    private ArrayList<MerchantOrderProduct> merchantOrderProducts;
    private Context context;
    private DatabaseReference productRef= FirebaseDatabase.getInstance().getReference().child("Merchant-Products");

    public ProductDetailsAdapter(ArrayList<MerchantOrderProduct> merchantOrderProducts, Context context) {
        this.merchantOrderProducts = merchantOrderProducts;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_details_recyclerview_popup_layout,parent,false);

        return new OrderProductHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderProductHolder holder, int position) {

        MerchantOrderProduct merchantOrderProduct=merchantOrderProducts.get(position);

        holder.name.setText(merchantOrderProduct.getProductName());
        holder.price.setText(merchantOrderProduct.getPrice()+ " ৳");
        holder.delivery_charge.setText(merchantOrderProduct.getDeliveryCharge()+ " ৳");
        holder.quantity.setText(merchantOrderProduct.getQuantity());

        String productId=merchantOrderProduct.getProductID();

        productRef.child(productId).child("image_url").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Picasso.with(context).load(dataSnapshot.getValue().toString()).into(holder.image);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return merchantOrderProducts==null? 0:merchantOrderProducts.size();
    }


    public class OrderProductHolder extends RecyclerView.ViewHolder{

        TextView name,price,delivery_charge,quantity;
        RoundedImageView image;


        public OrderProductHolder(@NonNull View itemView) {
            super(itemView);

            name=itemView.findViewById(R.id.product_name_popupView_order);
            price=itemView.findViewById(R.id.product_price_popupView_order);
            delivery_charge=itemView.findViewById(R.id.product_delivery_charge_popupView_order);
            quantity=itemView.findViewById(R.id.product_quantity_popupView_order);
            image=itemView.findViewById(R.id.product_image_popupView_order);



        }
    }



}
