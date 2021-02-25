package com.yongdd.o_der_re.server;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ConstraintLayout loadingLayout;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    HomeFragment homeFragment;
    ManagementFragment managementFragment;
    MenuFragment menuFragment;

    static String name;
    static ArrayList<OrderClient> orderLists = new ArrayList<>();
    int orderListCount;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        orderListCount=0;

        loadingLayout = findViewById(R.id.loadingLayout);

        homeFragment = new HomeFragment();
        managementFragment = new ManagementFragment();
        menuFragment = new MenuFragment();


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                tabSelect(item);

                if(tabSelect(item)){
                    return true;
                }else{
                    return false;
                }
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.homeTab);

        Intent intent = getIntent();
        if(intent!=null){
            //이름 설정
            name = intent.getStringExtra("name");

            //로딩화면 설정
            loadingLayout.setVisibility(View.VISIBLE);
            ProgressBar proBar = (ProgressBar) findViewById(R.id.progressBar);
            if (proBar != null) {
                proBar.setIndeterminate(true);
                proBar.setIndeterminateTintList(ColorStateList.valueOf(Color.rgb(43,144,217)));
            }

            //db 연결
            getDate();



        }else{
            //이름 설정
            name = "이름 없음";

            //로딩화면 설정
            loadingLayout.setVisibility(View.VISIBLE);
            ProgressBar proBar = (ProgressBar) findViewById(R.id.progressBar);
            if (proBar != null) {
                proBar.setIndeterminate(true);
                proBar.setIndeterminateTintList(ColorStateList.valueOf(Color.rgb(175,18,18)));
            }

            //db연결
            getDate();


        }



    }

    @Override
    public void onClick(View v) {

    }

    public boolean tabSelect(MenuItem item){
        switch (item.getItemId()){
            case R.id.homeTab:
                getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer,homeFragment).commit();
                return true;
            case R.id.menuTab:
                getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer,menuFragment).commit();
                return true;
            case R.id.managementTab:
                getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer,managementFragment).commit();
                return true;
            default:return false;
        }
    }

    private void getDate(){
        Log.d("MainOrderList DB", "getDate called");

        if(android.os.Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            LocalDateTime now = LocalDateTime.now();
            ZonedDateTime zonedDateTime = ZonedDateTime.of(now, ZoneId.of("Asia/Seoul"));

            String today = String.valueOf(zonedDateTime.toLocalDate());

            getOrderListCount(today);

        }else{
            Date now = new Date();
            TimeZone tz = TimeZone.getTimeZone("Asia/Seoul");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
            dateFormat.setTimeZone(tz);

            String today = dateFormat.format(now);

            getOrderListCount(today);

        }
    }

    private void getOrderListCount(String today){
        Log.d("MainOrderList DB", "getOrderListCount called");
        DatabaseReference ref = database.getReference("orderList");
        ref.orderByChild("orderDate").equalTo(today).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long size = snapshot.getChildrenCount();
                Log.d("MainOrderList DB", "size "+size);
                getOrderList(today,size);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getOrderList(String today,long size){
        if(size>=0) {
            Log.d("MainOrderList DB", "getOrderList called");

            DatabaseReference ref = database.getReference("orderList");
            ref.orderByChild("orderDate").equalTo(today).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    Order order = snapshot.getValue(Order.class);
                    String ordercompletedTime = order.getOrderCompletedTime();
                    String userId = order.getUserId();

                    if (!ordercompletedTime.equals("") || ordercompletedTime != "") {
                        Log.d("MainOrderList DB", "getUserInfo start");
                        getUserInfo(order, userId, size);
                    } else {
                        loadingLayout.setVisibility(View.GONE);
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, homeFragment).commit();
                        Log.d("MainOrderList DB", "전부 완료된 주문");
                    }

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    loadingLayout.setVisibility(View.GONE);
                    getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, homeFragment).commit();
                    Log.d("MainOrderList DB", "에러 " + error);
                }
            });
        }else{
            loadingLayout.setVisibility(View.GONE);
        }
    }

    private void getUserInfo(Order order, String userId, long size){
        Log.d("MainOrderList DB", "getUserInfo called");


        DatabaseReference ref = database.getReference("users");
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String UserId = snapshot.getKey();
                Log.d("MainOrderList DB", "userid  읽어옴"+UserId+", "+userId);

                if(UserId.equals(userId)){
                    Log.d("MainOrderList DB", "userid  일치");
                    User user = snapshot.getValue(User.class);
                    String customerName = user.getUserName();
                    String customerPhoneNumber = user.getUserPhoneNumber();
                    orderLists.add(new OrderClient(customerName,customerPhoneNumber,order));

                    orderListCount+=1;
                }else{
                    Log.d("MainOrderList DB", "userid  불일치");
                }

                if(orderListCount==size){
                    Log.d("MainOrderList DB", "count==size");
                    homeFragment.setOrderList(orderLists);
                    orderListCount=0;
//                    orderLists.clear();
                    loadingLayout.setVisibility(View.GONE);
                }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) { }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("MainOrderList DB", "getuserInfo 에러 "+error);
            }
        });
    }

}