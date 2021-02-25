package com.yongdd.o_der_re.server;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.OrderListViewHolder> {
    ArrayList<OrderClient> orderLists = new ArrayList<>();
    Context context;

    @NonNull
    @Override
    public OrderListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.order_list_item,parent,false);

        context = parent.getContext();

        return new OrderListViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull OrderListViewHolder holder, int position) {
        OrderClient orderList = orderLists.get(position);
        holder.setItem(orderList);
    }

    @Override
    public int getItemCount() {
        return orderLists.size();
    }


    public void addItem(OrderClient orderList){
        orderLists.add(orderList);
    }

    public OrderClient getItem(int position){
        return orderLists.get(position);
    }


    public class OrderListViewHolder extends RecyclerView.ViewHolder{
        TextView orderDate, orderReceived, orderAccepted, orderCompleted, totalCount, acceptedProgress
                ,completedProgress, customerName, customerPhoneNumber;

        ProgressBar orderProgressBar;

        RecyclerView orderListRecycler;
        InOrderListAdapter inOrderListAdapter;

        final DecimalFormat priceFormat = new DecimalFormat("###,###");


        public OrderListViewHolder(@NonNull View itemView) {
            super(itemView);
            orderDate = itemView.findViewById(R.id.O_date);
            orderReceived = itemView.findViewById(R.id.orderRecivedDate);
            orderAccepted = itemView.findViewById(R.id.orderAcceptedDate);
            orderCompleted = itemView.findViewById(R.id.orderCompletedDate);
            totalCount = itemView.findViewById(R.id.inO_totalPrice);
            orderListRecycler = itemView.findViewById(R.id.O_orderListRecyclerview);
            orderProgressBar = itemView.findViewById(R.id.orderProgressBar);
            acceptedProgress = itemView.findViewById(R.id.acceptedProgress);
            completedProgress = itemView.findViewById(R.id.completedProgress);
            customerName = itemView.findViewById(R.id.O_customerName);
            customerPhoneNumber = itemView.findViewById(R.id.O_customerPhoneNumber);
            orderProgressBar.setMax(10);


        }

        public void setItem(OrderClient orderClient){
            Order orderList = orderClient.getOrder();

            //주문 날짜 등 지정
            String orderAcceptedTime = orderList.getOrderAcceptedTime();
            String orderCompletedTime = orderList.getOrderCompletedTime();
            Log.d("orderListAdapter",orderAcceptedTime+", "+orderAcceptedTime);

            orderDate.setText(orderList.getOrderDate());
            orderReceived.setText(orderList.getOrderReceivedTime());
            orderAccepted.setText(orderAcceptedTime);
            orderCompleted.setText(orderCompletedTime);
            customerName.setText(orderClient.getCustomerName());
            customerPhoneNumber.setText(orderClient.getCustomerPhoneNumber());


            //프로그래스바 - 수정 필
            if(orderAcceptedTime==""||orderAcceptedTime.equals("")){
                orderProgressBar.setProgress(0);
                acceptedProgress.setBackgroundResource(R.drawable.progress_circle_light);
                completedProgress.setBackgroundResource(R.drawable.progress_circle_light);


            }else if(orderCompletedTime=="" || orderCompletedTime.equals("")){
                orderProgressBar.setProgress(5);
                acceptedProgress.setBackgroundResource(R.drawable.progress_circle_main);
                completedProgress.setBackgroundResource(R.drawable.progress_circle_light);
            }else{
                orderProgressBar.setProgress(10);
                acceptedProgress.setBackgroundResource(R.drawable.progress_circle_main);
                completedProgress.setBackgroundResource(R.drawable.progress_circle_main);
            }


            //결제금액
            String itemPriceFormat = priceFormat.format(orderList.getTotalPrice());
            totalCount.setText(itemPriceFormat+"원");

            //주문 메뉴 리싸이클러뷰
             inOrderListAdapter = new InOrderListAdapter();

            orderListRecycler.setHasFixedSize(true);
            orderListRecycler.setLayoutManager(new LinearLayoutManager(context));
//            orderListRecycler.setAdapter(adapter);

            orderMenuSetting(orderList.getOrderMenus());

        }
        public void orderMenuSetting(ArrayList<Payment> orderMenus){
            for(Payment payment : orderMenus){
                inOrderListAdapter.addItem(payment);
                orderListRecycler.setAdapter(inOrderListAdapter);
                inOrderListAdapter.notifyDataSetChanged();
            }
        }


    }
}
