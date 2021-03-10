package com.yongdd.o_der_re.server;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.service.autofill.UserData;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class OrderListManagement extends AppCompatActivity implements View.OnClickListener {


    static Button todayButton, oneDayAgoButton, twoDayAgoButton, backButton;
    ConstraintLayout loadingLayout;
    LinearLayout noneOrderLayout;

    static RecyclerView orderListRecyclerview;
    static OrderListAdapter orderListAdapter;

    static ArrayList<OrderClient> orderLists = new ArrayList<>();
    int completedOrderCount;

    final String TAG = "orderListChecked";


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_list_management);

        completedOrderCount = 0;

        noneOrderLayout = findViewById(R.id.OM_noneOrderLayout);
        loadingLayout = findViewById(R.id.OM_loadingLayout);
        todayButton = findViewById(R.id.OM_todayButton);
        oneDayAgoButton = findViewById(R.id.OM_1dayAgoButton);
        twoDayAgoButton = findViewById(R.id.OM_2dayAgoButton);
        backButton = findViewById(R.id.OM_backButton);
        todayButton.setOnClickListener(this);
        oneDayAgoButton.setOnClickListener(this);
        twoDayAgoButton.setOnClickListener(this);
        backButton.setOnClickListener(this);

        orderListRecyclerview = findViewById(R.id.OM_orderListRecyclerview);
        orderListRecyclerview.setHasFixedSize(false);
        orderListRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        orderListAdapter = new OrderListAdapter();

        //날짜 세팅
        daySetting();


    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void daySetting(){
        Log.d(TAG,"daySetting called");
        loadingLayout.setVisibility(View.VISIBLE);
        ProgressBar proBar = (ProgressBar) findViewById(R.id.progressBar);
        if (proBar != null) {
            proBar.setIndeterminate(true);
            proBar.setIndeterminateTintList(ColorStateList.valueOf(Color.rgb(43,144,217)));
        }

        if(android.os.Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            LocalDateTime now = LocalDateTime.now();
            ZonedDateTime zonedDateTime = ZonedDateTime.of(now, ZoneId.of("Asia/Seoul"));

            LocalDate localDate = zonedDateTime.toLocalDate();
            String todayDate = String.valueOf(zonedDateTime.toLocalDate());
            String oneDayAgoDate = String.valueOf(ChronoUnit.DAYS.addTo(localDate, -1));
            String twoDayAgoDate = String.valueOf(ChronoUnit.DAYS.addTo(localDate, -2));

            todayButton.setText(todayDate);
            oneDayAgoButton.setText(oneDayAgoDate);
            twoDayAgoButton.setText(twoDayAgoDate);

            getMaxOrderCount(todayDate);


        }else{
            Date now = new Date();
            TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            dateFormat.setTimeZone(tz);
            String todayDate = dateFormat.format(now);

            Calendar cal = Calendar.getInstance();
            cal.setTime(now);
            cal.add(Calendar.DATE, -1);
            String oneDayAgoDate = dateFormat.format(cal.getTime());

            cal.add(Calendar.DATE, -1);
            String twoDayAgoDate = dateFormat.format(cal.getTime());

            todayButton.setText(todayDate);
            oneDayAgoButton.setText(oneDayAgoDate);
            twoDayAgoButton.setText(twoDayAgoDate);

            getMaxOrderCount(todayDate);

        }
    }


    public void getMaxOrderCount(String date){
        Log.d(TAG,"getMaxOrderCount called");
        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("orderList");
        database.orderByChild("orderDate").equalTo(date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d(TAG,"getMaxOrderCount data change called");
                long maxCount = snapshot.getChildrenCount();

                if(maxCount==0 ){
                    loadingLayout.setVisibility(View.GONE);
                    noneOrderLayout.setVisibility(View.VISIBLE);
                    orderListRecyclerview.setVisibility(View.GONE);
                }else{
                    noneOrderLayout.setVisibility(View.GONE);
                    orderListRecyclerview.setVisibility(View.VISIBLE);
                    getOrderList(date);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void getOrderList(String date){
        Log.d(TAG,"getOrderList called");
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("orderList");
        ref.orderByChild("orderDate").equalTo(date).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d(TAG,"getOrderList child add called");

                    String orderId = snapshot.getKey();
                    Order order = snapshot.getValue(Order.class);
                    String userId = order.getUserId();
                    String orderCompletedTime = order.getOrderCompletedTime();

                    if(orderCompletedTime.equals("") || orderCompletedTime ==""){
                        Log.d(TAG,"getOrderList child add none order");
                        loadingLayout.setVisibility(View.GONE);
                        noneOrderLayout.setVisibility(View.VISIBLE);
                        orderListRecyclerview.setVisibility(View.GONE);
                    }else{
                        Log.d(TAG,"getOrderList child add !none order");
                        noneOrderLayout.setVisibility(View.GONE);
                        orderListRecyclerview.setVisibility(View.VISIBLE);
                        matchUser(order,orderId,userId);
                    }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String orderId = snapshot.getKey();
                Order order = snapshot.getValue(Order.class);
                String userId = order.getUserId();
                String orderCompletedTime = order.getOrderCompletedTime();

                if(orderCompletedTime.equals("") || orderCompletedTime ==""){
                    return;
                }else{
                    matchUser(order,orderId,userId);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) { }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void matchUser(Order order,String orderId, String userIds){
        Log.d(TAG,"matchUser called");
        OrderClient orderClient =null;
        if(MainActivity.users.size()>0) {
            for (UserId userId : MainActivity.users){

                String userId_item = userId.getUserId();
                User user_item = userId.getUser();
                String userName = user_item.getUserName();
                String userPhoneNumber = user_item.getUserPhoneNumber();
                String userEmail = user_item.getUserEmail();

                if(userId_item.equals(userIds) || userId_item ==userIds){
                    orderClient = new OrderClient(userName,userPhoneNumber,userEmail,orderId,order);
                    Log.d(TAG,"matchUser  일치 user "+userIds);
                    orderLists.add(orderClient);
                }

            }
           setOrderList();
        }
    }

    private void setOrderList(){
        Log.d(TAG,"setOderList called ");

        orderListAdapter.clearItem();
        for(OrderClient orders:orderLists){
            orderListAdapter.addItem(orders);
            Log.d(TAG,"setOrderList addItem ");
        }
        orderListRecyclerview.setAdapter(orderListAdapter);
        orderListAdapter.notifyDataSetChanged();
        loadingLayout.setVisibility(View.GONE);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        if(v==todayButton){
            buttonClickedSetting(todayButton.getText().toString());

        }else if(v==oneDayAgoButton){
            buttonClickedSetting(oneDayAgoButton.getText().toString());
        }else if(v==twoDayAgoButton){
            buttonClickedSetting(twoDayAgoButton.getText().toString());
        }else if(v==backButton){

        }
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void buttonClickedSetting(String date){
        //로딩화면
        loadingLayout.setVisibility(View.VISIBLE);
        ProgressBar proBar = (ProgressBar) findViewById(R.id.progressBar);
        if (proBar != null) {
            proBar.setIndeterminate(true);
            proBar.setIndeterminateTintList(ColorStateList.valueOf(Color.rgb(43,144,217)));
        }

        //버튼 준비
        String today = todayButton.getText().toString();
        String oneDayAgo = oneDayAgoButton.getText().toString();
        String twoDayAgo = twoDayAgoButton.getText().toString();

        if(today.equals(date) || today == date){
            todayButton.setBackgroundResource(R.drawable.round_button_main);
            todayButton.setTextColor(getResources().getColorStateList(R.color.lightColor));
            oneDayAgoButton.setBackgroundResource(R.drawable.round_button_light);
            oneDayAgoButton.setTextColor(getResources().getColorStateList(R.color.mainColor));
            twoDayAgoButton.setBackgroundResource(R.drawable.round_button_light);
            twoDayAgoButton.setTextColor(getResources().getColorStateList(R.color.mainColor));
        }else if(oneDayAgo.equals(date) || oneDayAgo == date){
            todayButton.setBackgroundResource(R.drawable.round_button_light);
            todayButton.setTextColor(getResources().getColorStateList(R.color.mainColor));
            oneDayAgoButton.setBackgroundResource(R.drawable.round_button_main);
            oneDayAgoButton.setTextColor(getResources().getColorStateList(R.color.lightColor));
            twoDayAgoButton.setBackgroundResource(R.drawable.round_button_light);
            twoDayAgoButton.setTextColor(getResources().getColorStateList(R.color.mainColor));
        }else if(twoDayAgo.equals(date) || twoDayAgo == date){
            todayButton.setBackgroundResource(R.drawable.round_button_light);
            todayButton.setTextColor(getResources().getColorStateList(R.color.mainColor));
            oneDayAgoButton.setBackgroundResource(R.drawable.round_button_light);
            oneDayAgoButton.setTextColor(getResources().getColorStateList(R.color.mainColor));
            twoDayAgoButton.setBackgroundResource(R.drawable.round_button_main);
            twoDayAgoButton.setTextColor(getResources().getColorStateList(R.color.lightColor));
        }

        //db 준비
        orderListAdapter.clearItem();
        orderLists.clear();
        getMaxOrderCount(date);
       // getOrderList(0,todayButton.getText().toString());

    }


}
