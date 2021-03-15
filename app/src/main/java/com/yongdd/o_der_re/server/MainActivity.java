package com.yongdd.o_der_re.server;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.loader.content.CursorLoader;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity  {
    ConstraintLayout loadingLayout;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    HomeFragment homeFragment;
    ManagementFragment managementFragment;
    MenuFragment menuFragment;


    static ArrayList<OrderClient> orderLists = new ArrayList<>();
    static ArrayList<UserId> users = new ArrayList<>();
    public static ArrayList<MenuUri> menus = new ArrayList<>();

    static String name;
    int orderListCount;
    int UserCount;
    static boolean menuAddClicked;
    static boolean menuEditClicked;
    static boolean firstZero;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);;

        menuAddClicked = false;
        menuEditClicked = false;
        firstZero = false;
        UserCount = 0;
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
            getUserInfo();
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
            getUserInfo();
            getDate();


        }

        //메뉴 가져오기
        getMenuDB();

        //토큰 가져오기
        getToken();
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
    private void getUserInfo(){
        DatabaseReference ref = database.getReference("users");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                snapshot.getChildrenCount();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String userId = dataSnapshot.getKey();
                    User user = dataSnapshot.getValue(User.class);
                    users.add(new UserId(userId,user));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void getDate(){

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
            Log.d("MainOrderList DB", "today "+today);

            getOrderListCount(today);

        }
    }

    private void getOrderListCount(String today){
        Log.d("MainOrderList DB", "getOrderListCount called "+today);
        DatabaseReference ref = database.getReference("orderList");
        ref.orderByChild("orderDate").equalTo(today).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                long size = snapshot.getChildrenCount();
                Log.d("MainOrderList DB", "datachage 1 called");
                Log.d("MainOrderList DB", "size "+size);

                if(size>0) {
                    firstZero = false;
                    getOrderList(today, size);
                }else{
                    firstZero = true;
                    loadingLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void getOrderList(String today,long size){
        if(size>=0) {
            Log.d("MainOrderList DB", "getOrderList called size "+size);
            DatabaseReference ref = database.getReference("orderList");
            ref.orderByChild("orderDate").equalTo(today).addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Log.d("MainOrderList DB", "childadd called1");

                    firstZero = false;

                    String orderId = snapshot.getKey();
                    Order order = snapshot.getValue(Order.class);
                    String ordercompletedTime = order.getOrderCompletedTime();
                    String userId = order.getUserId();

                    if (ordercompletedTime.equals("") || ordercompletedTime == "") {
                        Log.d("MainOrderList DB", "matchUser start");
                        matchUser(order, orderId, userId);
                    } else {
                        loadingLayout.setVisibility(View.GONE);
                        getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, homeFragment).commit();
                        Log.d("MainOrderList DB", "전부 완료된 주문");
                    }

                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Log.d("MainOrderList DB", "changed3 called");


                        String orderId = snapshot.getKey();
                        Order order = snapshot.getValue(Order.class);
                        String orderAcceptedTime = order.getOrderAcceptedTime();
                        String ordercompletedTime = order.getOrderCompletedTime();



                            if (ordercompletedTime.equals("") || ordercompletedTime == ""){
                                int position = HomeFragment.selectPosition;
                                Log.d("position22","position "+position);

                                if(position>-1){
                                    try {
                                        OrderClient orderClient = homeFragment.getItems();
                                        String customerName = orderClient.getCustomerName();
                                        String customerPhoneNumber = orderClient.getCustomerPhoneNumber();
                                        String customerEmail = orderClient.getCustomerEmail();
                                        if (!orderAcceptedTime.equals("") || orderAcceptedTime != "") {
                                            Log.d("MainOrderList DB", "matchUser start");
                                            homeFragment.acceptedOrderChange(new OrderClient(customerName,customerPhoneNumber,customerEmail,orderId,order));
                                        } else {
                                            getSupportFragmentManager().beginTransaction().replace(R.id.mainContainer, homeFragment).commit();
                                        }
                                    }catch (IndexOutOfBoundsException e){
                                        Log.d("position22","오류남 힝힝 "+e);
                                    }
                                }else {
                                    Log.d("MainOrderList DB", "전부 완료된 주문");
                                }

                            }else{
                                int position = HomeFragment.selectPosition;
                                homeFragment.deleteOrder(position);
                            }

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) { }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

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

    private void matchUser(Order order,String orderId, String userIds){
        Log.d("MainOrderList DB", "matchUser called");
        OrderClient orderClient =null;
        if(users.size()>0) {
            for (UserId userId : users){

                String userId_item = userId.getUserId();
                User user_item = userId.getUser();
                String userName = user_item.getUserName();
                String userPhoneNumber = user_item.getUserPhoneNumber();
                String userEmail = user_item.getUserEmail();

                if(userId_item.equals(userIds) || userId_item ==userIds){
                    orderClient = new OrderClient(userName,userPhoneNumber,userEmail,orderId,order);
                    orderLists.add(orderClient);
                    Log.d("MainOrderList DB", "matchUser orderLsits add "+userName);
                }
            }

            homeFragment.setList(orderLists);
            loadingLayout.setVisibility(View.GONE);

        }
    }

    public void getMenuDB(){
        DatabaseReference ref = database.getReference("menus");
        ref.orderByChild("menuDelimiter").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Menu menu = snapshot.getValue(Menu.class);
                getMenuImgUri(menu,Integer.parseInt(snapshot.getKey()));

                Log.d("menuDB","size  "+menus.size());
                Log.d("menuDB","id  "+snapshot.getKey());

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Menu menu = snapshot.getValue(Menu.class);
                getMenuImgUriChange(menu,Integer.parseInt(snapshot.getKey()));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                deleteMenu(Integer.parseInt(snapshot.getKey()));
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public void getMenuImgUri(Menu menu,int menuId){

        String imgPath = menu.getMenuImgPath();
        if(imgPath.equals("") || imgPath == ""){
            menus.add(new MenuUri(new Menu(menuId,menu.getMenuDelimiter(),menu.getMenuHotIce(),menu.getMenuImgPath(),
                    menu.getMenuName(),menu.getMenuPrice()),null));
        }else{
            FirebaseStorage storage = FirebaseStorage.getInstance("gs://oder-e6555.appspot.com");
            StorageReference storageRef = storage.getReference();
            storageRef.child(imgPath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    menus.add(new MenuUri(new Menu(menuId,menu.getMenuDelimiter(),menu.getMenuHotIce(),menu.getMenuImgPath(),
                            menu.getMenuName(),menu.getMenuPrice()),uri));
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    menus.add(new MenuUri(new Menu(menuId,menu.getMenuDelimiter(),menu.getMenuHotIce(),menu.getMenuImgPath(),
                            menu.getMenuName(),menu.getMenuPrice()),null));
                }
            });
        }

    }

    public void getMenuImgUriChange(Menu menu, int menuId){

        String imgPath = menu.getMenuImgPath();

        FirebaseStorage storage = FirebaseStorage.getInstance("gs://oder-e6555.appspot.com");
        StorageReference storageRef = storage.getReference();
        storageRef.child(imgPath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                for(int i=0; i<menus.size(); i++) {
                    int menu_id = menus.get(i).getMenu().getMenuId();
                    if(menu_id==menuId) {
                        menus.set(i,new MenuUri(menu,uri));
                        menuFragment.updateItem(i,new MenuUri(menu,uri));
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(getApplicationContext(),"수정된 메뉴를 새로 불러오는데 실패했습니다.",Toast.LENGTH_SHORT).show();
                for(int i=0; i<menus.size(); i++) {
                    int menu_id = menus.get(i).getMenu().getMenuId();
                    if(menu_id==menu_id) {
                        menus.set(i,new MenuUri(menu,null));
                    }
                }
            }
        });
    }

    public void deleteMenu(int menuId){
        Log.d("deleteMenu","Mainactivity menu remove size start"+ menus.size());
        for(int i=0; i<menus.size(); i++) {
            int menu_id = menus.get(i).getMenu().getMenuId();
            if(menu_id==menuId) {
                Log.d("deleteMenu","Mainactivity menu remove "+i);

                menus.remove(i);
                menuFragment.deleteMenu();
                Log.d("deleteMenu","Mainactivity menu remove size end"+ menus.size());
            }
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

                if(resultCode==RESULT_OK){
                    if(menuEditClicked) {
                        menuEditClicked = false;
                        Uri uri = data.getData();
                        if (uri != null) {
                            MenuEditFragment menuEditFragment = new MenuEditFragment();
                            menuEditFragment.onActivityResult(uri, getRealPathFromUri(uri), getImageNameToUri(uri));
                        }
                    }else if(menuAddClicked){
                        menuAddClicked = false;
                        Uri uri = data.getData();
                        if (uri != null) {
                            Log.d("menuChoice", "image uri null");
                            MenuAddFragment menuAddFragment = new MenuAddFragment();
                            menuAddFragment.onActivityResult(uri, getRealPathFromUri(uri), getImageNameToUri(uri));
                        }

                    }
                }else{
                    menuEditClicked = false;
                    menuAddClicked = false;
                }
    }

    protected String getRealPathFromUri(Uri uri){
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this,uri,proj,null,null,null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();

        return result;
    }

    public String getImageNameToUri(Uri data){
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);

        return imgName;
    }

    private void getToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        Log.d("token",token);

                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference ref = database.getReference("seller");
                        ref.orderByChild("token").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                               String dbToken =  snapshot.child("token").getValue(String.class);
                                Log.d("token","dbToken : "+dbToken);
//                               if(!token.equals(dbToken)){
                                   snapshot.getRef().setValue(token);
//                               }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) { }
                        });
                    }
                });

    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("앱을 종료하시겠습니까?")
                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        moveTaskToBack(true); // 태스크를 백그라운드로 이동
                        finishAndRemoveTask(); // 액티비티 종료 + 태스크 리스트에서 지우기
                        android.os.Process.killProcess(android.os.Process.myPid()); // 앱 프로세스 종료
                    }
                })
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
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

}