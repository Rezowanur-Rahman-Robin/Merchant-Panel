package com.robin.onlinecourierservice;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MerchantHomeAdapter extends RecyclerView.Adapter<MerchantHomeAdapter.DataHolderHome> {
    private ArrayList<MerchantProduct> merchantProducts;
    private Context myContext;

    public MerchantHomeAdapter(ArrayList<MerchantProduct> merchantProducts, Context myContext) {
        this.merchantProducts = merchantProducts;
        this.myContext = myContext;
    }

    @NonNull
    @Override
    public DataHolderHome onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.most_recent_product_cardview,parent,false);

        return new DataHolderHome(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DataHolderHome holder, int position) {

        final MerchantProduct merchantProduct=merchantProducts.get(position);
        Picasso.with(myContext).load(merchantProduct.getImage_url()).into(holder.pImage);
        holder.pName.setText(merchantProduct.getProduct_name());

        holder.pPrize.setText(merchantProduct.getProduct_price()+ " ৳");


        String tempPDprice=merchantProduct.getProduct_discount_price();
        if(!tempPDprice.isEmpty()){
            holder.pDprice.setText("  "+tempPDprice+" ৳");
            holder.pDprice.setVisibility(View.VISIBLE);
            holder.pPrize.setText(merchantProduct.getProduct_price());
            holder.pPrize.setBackground(myContext.getResources().getDrawable(R.drawable.red_line));
        }

        final String pro_size=merchantProduct.getProduct_size();
        final String pro_weight=merchantProduct.getProduct_weight();





        holder.pImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(myContext,R.style.BottomSheetDialogueTheme);

                View bottomSheetView =LayoutInflater.from(myContext).inflate(R.layout.product_datails_popup_layout,null);

                TextView p_name,p_des,p_size,p_weight,p_price,p_main,P_d_price,p_district,p_thana,p_add_details,p_info;
                RoundedImageView img;

                //for bottomsheet

                p_name=bottomSheetView.findViewById(R.id.product_name_popupView);
                p_des=bottomSheetView.findViewById(R.id.product_description_popupView);
                p_size=bottomSheetView.findViewById(R.id.product_size_popup);
                p_weight=bottomSheetView.findViewById(R.id.product_weight_popup);
                p_price=bottomSheetView.findViewById(R.id.product_price_popupView);
                P_d_price=bottomSheetView.findViewById(R.id.product_discount_price_popup);
                p_district=bottomSheetView.findViewById(R.id.pickup_district_popup);
                p_thana=bottomSheetView.findViewById(R.id.pickup_thana_popup);
                p_add_details=bottomSheetView.findViewById(R.id.pickup_address_details_popup);
                p_info=bottomSheetView.findViewById(R.id.pickUp_instructions_popup);
                p_main=bottomSheetView.findViewById(R.id.product_main_price_popup);
                img=bottomSheetView.findViewById(R.id.product_image_popupView);


                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();




                p_name.setText(merchantProduct.getProduct_name());
                p_price.setText(merchantProduct.getProduct_price() +" ৳");
                P_d_price.setText(merchantProduct.getProduct_discount_price()+" ৳");
                p_des.setText(merchantProduct.getProduct_description());
                p_add_details.setText(merchantProduct.getAddress_details());
                p_info.setText(merchantProduct.getPickUp_instructions());
                p_district.setText(merchantProduct.getPickUp_district());
                p_thana.setText(merchantProduct.getPickUp_thana());
                p_main.setText(merchantProduct.getProduct_price() +" ৳");
                p_weight.setText(merchantProduct.getProduct_weight());

                Picasso.with(myContext).load(merchantProduct.getImage_url()).into(img);

                switch(pro_size){
                    case "s1":
                        p_size.setText("Under 1 sq Feet");
                        break;
                    case "s2":
                        p_size.setText("1-2 sq Feet");
                        break;
                    case "s3":
                        p_size.setText("More than 2 sq Feet");
                        break;
                }
                switch(pro_weight){
                    case "w1":
                        p_weight.setText("Under 500 gm");
                        break;
                    case "w2":
                        p_weight.setText("0.5-1 kg");
                        break;
                    case "w3":
                        p_weight.setText("1-3 kg");
                        break;
                    case "w4":
                        p_weight.setText("4-7 kg");
                        break;
                    case "w5":
                        p_weight.setText("More than 7 kg");
                        break;

                }

            }


        });
        holder.pName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(myContext,R.style.BottomSheetDialogueTheme);

                View bottomSheetView =LayoutInflater.from(myContext).inflate(R.layout.product_datails_popup_layout,null);
                TextView p_name,p_des,p_size,p_weight,p_price,p_main,P_d_price,p_district,p_thana,p_add_details,p_info;
                RoundedImageView img;

                //for bottomsheet

                p_name=bottomSheetView.findViewById(R.id.product_name_popupView);
                p_des=bottomSheetView.findViewById(R.id.product_description_popupView);
                p_size=bottomSheetView.findViewById(R.id.product_size_popup);
                p_weight=bottomSheetView.findViewById(R.id.product_weight_popup);
                p_price=bottomSheetView.findViewById(R.id.product_price_popupView);
                P_d_price=bottomSheetView.findViewById(R.id.product_discount_price_popup);
                p_district=bottomSheetView.findViewById(R.id.pickup_district_popup);
                p_thana=bottomSheetView.findViewById(R.id.pickup_thana_popup);
                p_add_details=bottomSheetView.findViewById(R.id.pickup_address_details_popup);
                p_info=bottomSheetView.findViewById(R.id.pickUp_instructions_popup);
                p_main=bottomSheetView.findViewById(R.id.product_main_price_popup);
                img=bottomSheetView.findViewById(R.id.product_image_popupView);


                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();




                p_name.setText(merchantProduct.getProduct_name());
                p_price.setText(merchantProduct.getProduct_price() +" ৳");
                P_d_price.setText(merchantProduct.getProduct_discount_price()+" ৳");
                p_des.setText(merchantProduct.getProduct_description());
                p_add_details.setText(merchantProduct.getAddress_details());
                p_info.setText(merchantProduct.getPickUp_instructions());
                p_district.setText(merchantProduct.getPickUp_district());
                p_thana.setText(merchantProduct.getPickUp_thana());
                p_main.setText(merchantProduct.getProduct_price() +" ৳");
                p_weight.setText(merchantProduct.getProduct_weight());

                Picasso.with(myContext).load(merchantProduct.getImage_url()).into(img);

                switch(pro_size){
                    case "s1":
                        p_size.setText("Under 1 sq Feet");
                        break;
                    case "s2":
                        p_size.setText("1-2 sq Feet");
                        break;
                    case "s3":
                        p_size.setText("More than 2 sq Feet");
                        break;
                }
                switch(pro_weight){
                    case "w1":
                        p_weight.setText("Under 500 gm");
                        break;
                    case "w2":
                        p_weight.setText("0.5-1 kg");
                        break;
                    case "w3":
                        p_weight.setText("1-3 kg");
                        break;
                    case "w4":
                        p_weight.setText("4-7 kg");
                        break;
                    case "w5":
                        p_weight.setText("More than 7 kg");
                        break;

                }

            }


        });

        holder.pPrize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomSheetDialog bottomSheetDialog=new BottomSheetDialog(myContext,R.style.BottomSheetDialogueTheme);

                View bottomSheetView =LayoutInflater.from(myContext).inflate(R.layout.product_datails_popup_layout,null);

                TextView p_name,p_des,p_size,p_weight,p_price,p_main,P_d_price,p_district,p_thana,p_add_details,p_info;
                RoundedImageView img;

                //for bottomsheet

                p_name=bottomSheetView.findViewById(R.id.product_name_popupView);
                p_des=bottomSheetView.findViewById(R.id.product_description_popupView);
                p_size=bottomSheetView.findViewById(R.id.product_size_popup);
                p_weight=bottomSheetView.findViewById(R.id.product_weight_popup);
                p_price=bottomSheetView.findViewById(R.id.product_price_popupView);
                P_d_price=bottomSheetView.findViewById(R.id.product_discount_price_popup);
                p_district=bottomSheetView.findViewById(R.id.pickup_district_popup);
                p_thana=bottomSheetView.findViewById(R.id.pickup_thana_popup);
                p_add_details=bottomSheetView.findViewById(R.id.pickup_address_details_popup);
                p_info=bottomSheetView.findViewById(R.id.pickUp_instructions_popup);
                p_main=bottomSheetView.findViewById(R.id.product_main_price_popup);
                img=bottomSheetView.findViewById(R.id.product_image_popupView);


                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();




                p_name.setText(merchantProduct.getProduct_name());
                p_price.setText(merchantProduct.getProduct_price() +" ৳");
                P_d_price.setText(merchantProduct.getProduct_discount_price()+" ৳");
                p_des.setText(merchantProduct.getProduct_description());
                p_add_details.setText(merchantProduct.getAddress_details());
                p_info.setText(merchantProduct.getPickUp_instructions());
                p_district.setText(merchantProduct.getPickUp_district());
                p_thana.setText(merchantProduct.getPickUp_thana());
                p_main.setText(merchantProduct.getProduct_price() +" ৳");
                p_weight.setText(merchantProduct.getProduct_weight());

                Picasso.with(myContext).load(merchantProduct.getImage_url()).into(img);

                switch(pro_size){
                    case "s1":
                        p_size.setText("Under 1 sq Feet");
                        break;
                    case "s2":
                        p_size.setText("1-2 sq Feet");
                        break;
                    case "s3":
                        p_size.setText("More than 2 sq Feet");
                        break;
                }
                switch(pro_weight){
                    case "w1":
                        p_weight.setText("Under 500 gm");
                        break;
                    case "w2":
                        p_weight.setText("0.5-1 kg");
                        break;
                    case "w3":
                        p_weight.setText("1-3 kg");
                        break;
                    case "w4":
                        p_weight.setText("4-7 kg");
                        break;
                    case "w5":
                        p_weight.setText("More than 7 kg");
                        break;

                }


            }


        });


    }




    @Override
    public int getItemCount() {
        return merchantProducts==null? 0:merchantProducts.size();
    }

    public class DataHolderHome extends RecyclerView.ViewHolder{

        ImageView pImage;
        TextView pName,pPrize,pDprice;
        LinearLayout bottomSheet;



        public DataHolderHome(@NonNull View itemView) {
            super(itemView);


            pImage=itemView.findViewById(R.id.cardView_image);
            pName=itemView.findViewById(R.id.product_name_cardView);
            pPrize=itemView.findViewById(R.id.product_price_cardView);
            bottomSheet=itemView.findViewById(R.id.bottomsheet_layout);
            pDprice=itemView.findViewById(R.id.product_discount_price_cardView);





        }
    }
}
