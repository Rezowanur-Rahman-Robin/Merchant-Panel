package com.robin.onlinecourierservice;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductViewAdapter extends RecyclerView.Adapter<ProductViewAdapter.ProductsDataHolder> {

    private ArrayList<MerchantProduct> merchantProducts;
    private Context myContext;

    public ProductViewAdapter(ArrayList<MerchantProduct> merchantProducts, Context myContext) {
        this.merchantProducts = merchantProducts;
        this.myContext = myContext;
    }

    @NonNull
    @Override
    public ProductsDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view =LayoutInflater.from(parent.getContext()).inflate(R.layout.product_show_layout_merchant,parent,false);


        return new ProductsDataHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ProductsDataHolder holder, final int position) {

        final MerchantProduct merchantProduct=merchantProducts.get(position);
       Picasso.with(myContext).load(merchantProduct.getImage_url()).into(holder.productImage);
       holder.productName.setText(merchantProduct.getProduct_name());
       holder.productPrice.setText(merchantProduct.getProduct_discount_price()+ " ৳");

        holder.editInfo.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent editIntent=new Intent(myContext,EditProductInfoActivity.class);
                        editIntent.putExtra("edit_p_id",merchantProduct.getP_id());
                        editIntent.putExtra("edit_p_name",merchantProduct.getProduct_name());
                        editIntent.putExtra("edit_p_des",merchantProduct.getProduct_description());
                        editIntent.putExtra("edit_p_price",merchantProduct.getProduct_price());
                        editIntent.putExtra("edit_p_dis_per",merchantProduct.getProduct_discount_percentage());
                        editIntent.putExtra("edit_p_dis_price",merchantProduct.getProduct_discount_price());
                        editIntent.putExtra("edit_p_image",merchantProduct.getImage_url());
                        editIntent.putExtra("edit_p_type",merchantProduct.getProduct_type());
                        editIntent.putExtra("edit_p_district",merchantProduct.getPickUp_district());
                        editIntent.putExtra("edit_p_thana",merchantProduct.getPickUp_thana());
                        editIntent.putExtra("edit_p_volume",merchantProduct.getProduct_size());
                        editIntent.putExtra("edit_p_weight",merchantProduct.getProduct_weight());
                        editIntent.putExtra("edit_d_id",merchantProduct.getD_id());
                        editIntent.putExtra("edit_t_id",merchantProduct.getT_id());
                        editIntent.putExtra("edit_p_address",merchantProduct.getAddress_details());
                        editIntent.putExtra("edit_pickUp",merchantProduct.getPickUp_instructions());
                        editIntent.putExtra("edit_Id",merchantProduct.getId());
                        myContext.startActivity(editIntent);

                    }
                }
        );




        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 AlertDialog.Builder dialogBuilder;
                 final AlertDialog dialogue;
                 Button delete,cancel;



                dialogBuilder =new AlertDialog.Builder(myContext);
                LayoutInflater li = (LayoutInflater) myContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                final View confirmDeleteView= li.inflate(R.layout.delete_confirm_layout,null);

                delete=(Button) confirmDeleteView.findViewById(R.id.delete_popup_confirm);
                cancel=(Button) confirmDeleteView.findViewById(R.id.cancel_popup_confirm);


                dialogBuilder.setView(confirmDeleteView);
                dialogue=dialogBuilder.create();
                dialogue.show();

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        DatabaseReference productRef=FirebaseDatabase.getInstance().getReference().child("Merchant-Products").child(merchantProduct.getP_id());

                        productRef.removeValue();
                        dialogue.dismiss();


                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogue.dismiss();
                    }
                });


            }
        });

    }

    @Override
    public int getItemCount() {
        return merchantProducts==null ? 0:merchantProducts.size();
    }

    public class ProductsDataHolder extends RecyclerView.ViewHolder{

        ImageView productImage;
        TextView productName,productPrice;
        Button editInfo,deleteBtn;


        public ProductsDataHolder(@NonNull View itemView) {
            super(itemView);

            productImage= itemView.findViewById(R.id.product_image_my_products);
            productName= itemView.findViewById(R.id.product_name_my_products);
            productPrice= itemView.findViewById(R.id.product_price_my_products);
            editInfo=itemView.findViewById(R.id.edit_product_info_button);
            deleteBtn=itemView.findViewById(R.id.delete_product_info_button);

        }


    }
}
