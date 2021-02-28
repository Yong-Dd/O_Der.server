package com.yongdd.o_der_re.server;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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

    public void clearItem(){orderLists.clear();}

    public void deleteItem(int position){orderLists.remove(position);}

    public void updateItem(int position, OrderClient orderClient){orderLists.set(position,orderClient);}

    public class OrderListViewHolder extends RecyclerView.ViewHolder{
        TextView orderDate, orderReceived, orderAccepted, orderCompleted, totalCount, acceptedProgress
                ,completedProgress, customerName, customerPhoneNumber, orderMemoText;
        Button orderAcceptedButton, orderCompletedButton;

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
            orderAcceptedButton = itemView.findViewById(R.id.O_orderAcceptedButton);
            orderCompletedButton = itemView.findViewById(R.id.O_orderCompletedButton);
            orderMemoText = itemView.findViewById(R.id.O_orderMemo);
            orderProgressBar.setMax(10);

            orderAcceptedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    acceptedOrder(getAdapterPosition());
                }
            });


            orderCompletedButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    completedOrder(getAdapterPosition());
                }
            });


        }

        public void setItem(OrderClient orderClient){
            Order orderList = orderClient.getOrder();

            //주문 날짜 등 지정
            String orderAcceptedTime = orderList.getOrderAcceptedTime();
            String orderCompletedTime = orderList.getOrderCompletedTime();
            String orderMemo = orderList.getOrderMemo();
            Log.d("orderListAdapter",orderAcceptedTime+", "+orderAcceptedTime);

            orderDate.setText(orderList.getOrderDate());
            orderReceived.setText(orderList.getOrderReceivedTime());
            orderAccepted.setText(orderAcceptedTime);
            orderCompleted.setText(orderCompletedTime);
            customerName.setText(orderClient.getCustomerName());
            customerPhoneNumber.setText(orderClient.getCustomerPhoneNumber());

            Log.d("orderListChecked","OrderListAdapter orderDate "+orderDate.getText().toString());
            //프로그래스바 & 버튼 세팅
            if(orderAcceptedTime==""||orderAcceptedTime.equals("")){
                orderAcceptedButton.setVisibility(View.VISIBLE);
                orderCompletedButton.setVisibility(View.GONE);

                orderProgressBar.setProgress(0);
                acceptedProgress.setBackgroundResource(R.drawable.progress_circle_light);
                completedProgress.setBackgroundResource(R.drawable.progress_circle_light);


            }else if(orderCompletedTime=="" || orderCompletedTime.equals("")){
                orderAcceptedButton.setVisibility(View.GONE);
                orderCompletedButton.setVisibility(View.VISIBLE);

                orderProgressBar.setProgress(5);
                acceptedProgress.setBackgroundResource(R.drawable.progress_circle_main);
                completedProgress.setBackgroundResource(R.drawable.progress_circle_light);
            }else{
                orderAcceptedButton.setVisibility(View.GONE);
                orderCompletedButton.setVisibility(View.GONE);

                orderProgressBar.setProgress(10);
                acceptedProgress.setBackgroundResource(R.drawable.progress_circle_main);
                completedProgress.setBackgroundResource(R.drawable.progress_circle_main);
            }

            //메모 설정
            if(orderMemo.equals("") || orderMemo == ""){
                orderMemoText.setVisibility(View.GONE);
            }else{
                orderMemoText.setVisibility(View.VISIBLE);
                orderMemoText.setText(orderMemo);
            }


            //결제금액
            String itemPriceFormat = priceFormat.format(orderList.getTotalPrice());
            totalCount.setText(itemPriceFormat+"원");

            //주문 메뉴 리싸이클러뷰
             inOrderListAdapter = new InOrderListAdapter();

            orderListRecycler.setHasFixedSize(true);
            orderListRecycler.setLayoutManager(new LinearLayoutManager(context));

            orderMenuSetting(orderList.getOrderMenus());

        }
        public void orderMenuSetting(ArrayList<Payment> orderMenus){
            for(Payment payment : orderMenus){
                inOrderListAdapter.addItem(payment);
                orderListRecycler.setAdapter(inOrderListAdapter);
                inOrderListAdapter.notifyDataSetChanged();
            }
        }
        public void acceptedOrder(int position){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("주문을 승인하시겠습니까?")
                    .setPositiveButton("승인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            OrderClient orderClient = getItem(position);
                            String orderId = orderClient.getOrderId();


                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference ref = database.getReference("orderList/"+orderId+"/orderAcceptedTime");
                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    snapshot.getRef().setValue(getTimeNow());
                                    HomeFragment homeFragment =new HomeFragment();
                                    homeFragment.getPosition(getAdapterPosition());

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    })
                    .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
            final AlertDialog dialog = builder.create();

            dialog.setOnShowListener( new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg0) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.rgb(43,144,217));
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                }
            });
            dialog.show();
        }

        public void completedOrder(int position){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("주문을 완료하시겠습니까?")
                    .setPositiveButton("완료", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            OrderClient orderClient = getItem(position);
                            String orderId = orderClient.getOrderId();


                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference ref = database.getReference("orderList/"+orderId+"/orderCompletedTime");
                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    snapshot.getRef().setValue(getTimeNow());
                                    HomeFragment homeFragment =new HomeFragment();
                                    homeFragment.getPosition(getAdapterPosition());

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    })
                    .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
            final AlertDialog dialog = builder.create();

            dialog.setOnShowListener( new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface arg0) {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.rgb(43,144,217));
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
                }
            });
            dialog.show();

        }

        public String getTimeNow(){
            String time = "";
            if(android.os.Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
                DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm");

                LocalDateTime now = LocalDateTime.now();
                ZonedDateTime zonedDateTime = ZonedDateTime.of(now, ZoneId.of("Asia/Seoul"));

               time = zonedDateTime.format(timeFormat);

               return time;

            }else{
                Date now = new Date();
                TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");

                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.KOREA);
                timeFormat.setTimeZone(tz);
                time = timeFormat.format(now);

                return time;
            }

        }

    }
}
