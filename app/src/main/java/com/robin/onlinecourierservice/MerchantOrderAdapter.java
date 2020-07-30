package com.robin.onlinecourierservice;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MerchantOrderAdapter extends RecyclerView.Adapter<MerchantOrderAdapter.OrderHolder> {

    private ArrayList<MerchantOrder> merchantOrders;
    private Context myContext;
    private DatabaseReference userRef= FirebaseDatabase.getInstance().getReference().child("Users");
    private DatabaseReference statusRef= FirebaseDatabase.getInstance().getReference().child("Order-Status");






    public MerchantOrderAdapter(ArrayList<MerchantOrder> merchantOrders, Context myContext) {
        this.merchantOrders = merchantOrders;
        this.myContext = myContext;
    }

    @NonNull
    @Override
    public OrderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from((parent.getContext())).inflate(R.layout.merchant_order_layout,parent,false);

        return new OrderHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OrderHolder holder, int position) {

        final MerchantOrder merchantOrder=merchantOrders.get(position);
        holder.orderID.setText(merchantOrder.getOrderNo());
        holder.orderCustomerName.setText(merchantOrder.getBuyerName());
        String DateString=merchantOrder.getDate();
        String date=DateString.substring(5,7);
        String month=DateString.substring(8,11);
        String year=DateString.substring(12,16);

        holder.orderYear.setText(year);
        holder.orderMonth.setText(month);
        holder.orderDate.setText(date);






        userRef.child(merchantOrder.getUserID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){

                    holder.orderCustomerPhone.setText(dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        statusRef.child(merchantOrder.getOrderNo()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    holder.orderStatus.setText(dataSnapshot.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        holder.orderLayout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        Intent details_orderIntent=new Intent(myContext,OrderDetails.class);

                        details_orderIntent.putExtra("order_no",merchantOrder.getOrderNo());
                        details_orderIntent.putExtra("total_amount",merchantOrder.getTotal_taka());
                        details_orderIntent.putExtra("b_name",merchantOrder.getBuyerName());
                        details_orderIntent.putExtra("user_id",merchantOrder.getUserID());
                        details_orderIntent.putExtra("b_order_time",merchantOrder.getDate());
                        details_orderIntent.putExtra("b_payment_type",merchantOrder.getPaymentType());
                        details_orderIntent.putExtra("district",merchantOrder.getShippingCity());
                        details_orderIntent.putExtra("thana",merchantOrder.getShippingThana());
                        details_orderIntent.putExtra("address",merchantOrder.getShippingAddress());

                        myContext.startActivity(details_orderIntent);



                    }
                }
        );



    }

    @Override
    public int getItemCount() {
        return merchantOrders==null? 0: merchantOrders.size();
    }

    public class OrderHolder extends RecyclerView.ViewHolder{

        TextView orderYear,orderMonth,orderDate,orderCustomerName,orderCustomerPhone,orderID,orderStatus;
        LinearLayout orderLayout;


        public OrderHolder(@NonNull View itemView) {
            super(itemView);

            orderYear=itemView.findViewById(R.id.order_date_year_merchant);
            orderMonth=itemView.findViewById(R.id.order_date_month_merchant);
            orderDate=itemView.findViewById(R.id.order_date_date_merchant);
            orderCustomerName=itemView.findViewById(R.id.customer_name_order_merchant);
            orderCustomerPhone=itemView.findViewById(R.id.order_customer_number);
            orderID=itemView.findViewById(R.id.order_id_merchant);
            orderStatus=itemView.findViewById(R.id.delivery_status_order_merchant);
            orderLayout=(LinearLayout) itemView.findViewById(R.id.order_layout_id);


        }
    }


}
