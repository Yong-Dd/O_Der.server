package com.yongdd.o_der_re.server;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class InOrderListAdapter extends RecyclerView.Adapter<InOrderListAdapter.PaymentViewHolder> {
    ArrayList<Payment> paymentLists = new ArrayList<>();
    Context context;

    final DecimalFormat priceFormat = new DecimalFormat("###,###");

    @NonNull
    @Override
    public PaymentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.in_orderlist_menu_list,parent,false);
        context = view.getContext();
        return new PaymentViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull PaymentViewHolder holder, int position) {
        Payment payment = paymentLists.get(position);
        holder.setItem(payment);
    }

    @Override
    public int getItemCount() {
        return paymentLists.size();
    }


    public void addItem(Payment payment){
        paymentLists.add(payment);
    }

    public Payment getItem(int position){
        return paymentLists.get(position);
    }



    public class PaymentViewHolder extends RecyclerView.ViewHolder{
        TextView menuName,menuCount, menuPrice;


        public PaymentViewHolder(@NonNull View itemView) {
            super(itemView);
            menuName = itemView.findViewById(R.id.IO_menuName);
            menuCount = itemView.findViewById(R.id.IO_menuCount);
            menuPrice = itemView.findViewById(R.id.IO_menuPrice);

        }
        public void setItem(Payment payment){
            String hotIce = payment.getMenuHotIce();
            if(hotIce=="" || hotIce.equals("")){
                menuName.setText(payment.getMenuName());
            }else{
                menuName.setText(payment.getMenuName()+"("+hotIce+")");
            }

            menuCount.setText(payment.getMenuTotalCount()+"개");

            String itemPriceFormat = priceFormat.format(payment.getMenuTotalPrice());
            menuPrice.setText(itemPriceFormat+"원");
        }

    }
}
