package com.yongdd.o_der_re.server;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class HomeFragment extends Fragment {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();

    static TextView logInNameText;
    FrameLayout noneOrderLayout;
    RecyclerView orderListRecyclerView;
    OrderListAdapter orderListAdapter;

    static ArrayList<OrderClient> orderClients = new ArrayList<>();
    static int selectPosition;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment,container,false);

        selectPosition = -1;

        logInNameText = view.findViewById(R.id.loginNameText);
        noneOrderLayout = view.findViewById(R.id.noneOrderLayout);

        orderListRecyclerView = view.findViewById(R.id.orderListRecyclerView);
        orderListAdapter = new OrderListAdapter();

        orderListRecyclerView.setHasFixedSize(false);
        orderListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        setLogInName(MainActivity.name);
        setList(MainActivity.orderLists);

        return view;
    }

    public void setLogInName(String name){
        logInNameText.setText(name);
    }

    public void setList(ArrayList<OrderClient> orderLists){

        if(orderLists.size()>0){
            setNoneOrder(false);
            orderClients = orderLists;
            setOrderList();
        }else{
            setNoneOrder(true);
        }
    }

    public void setOrderList(){

            orderListAdapter.clearItem();
            for(OrderClient orderClient : orderClients){
                orderListAdapter.addItem(orderClient);
                orderListRecyclerView.setAdapter(orderListAdapter);
                orderListAdapter.notifyDataSetChanged();
            }
    }
    public void getPosition(int position){
        selectPosition = position;
    }



    public void setNoneOrder(boolean none){
        if(none){
            noneOrderLayout.setVisibility(View.VISIBLE);
        }else{
            noneOrderLayout.setVisibility(View.GONE);
        }
    }

    public OrderClient getItems(){
        OrderClient orderClient = orderListAdapter.getItem(selectPosition);
        return orderClient;
    }

    public void acceptedOrderChange(OrderClient orderClient){
        orderClients.set(selectPosition,orderClient);
        orderListAdapter.updateItem(selectPosition,orderClient);
        orderListRecyclerView.setAdapter(orderListAdapter);
        orderListAdapter.notifyItemChanged(selectPosition);
    }

    public void deleteOrder(int position){

        orderListAdapter.deleteItem(position);
        orderListRecyclerView.setAdapter(orderListAdapter);
        orderListAdapter.notifyItemChanged(position);

        orderClients.remove(position);
        Log.d("delete Order","사이즈 "+orderClients.size());
        if(orderClients.size()==0){
            Log.d("delete Order","사이즈 0임");
            setNoneOrder(false);
        }
    }

    public void sendNotification(String email, String message){
        final String FCM_MESSAGE_URL = "https://fcm.googleapis.com/fcm/send";
        final String SERVER_KEY = "AAAA9EetdA4:APA91bFoaEw-hzCziWA36z1UCer2KmJC06W0gE5s2Vn6YIba1HIMVkqjN0TaLZsz1YA-BDeF-4ZrNU2ENQRM0aFGPtAFTdnbizrhvgBV5o36ED-Tli-PcVyecP9RGKZOIT-K5phMHgYz";

        DatabaseReference ref = database.getReference("users");
        ref.orderByChild("userEmail").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = null;
                for (DataSnapshot child: snapshot.getChildren()) {
                    user = child.getValue(User.class);
                    Log.d("token",user.getUserName());
                    Log.d("token",user.getUserEmail());
                    Log.d("token",user.getUserToken());
                }


                User finalUser = user;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // FMC 메시지 생성 start
                            JSONObject root = new JSONObject();
                            JSONObject notification = new JSONObject();
                            notification.put("title", "O:Der");
                            notification.put("body", message);
                            root.put("notification", notification);
                            root.put("to", finalUser.getUserToken());

                            // FMC 메시지 생성 end
                            URL Url = new URL(FCM_MESSAGE_URL);
                            HttpURLConnection conn = (HttpURLConnection) Url.openConnection();
                            conn.setRequestMethod("POST");
                            conn.setDoOutput(true);
                            conn.setDoInput(true);
                            conn.addRequestProperty("Authorization", "key=" + SERVER_KEY);
                            conn.setRequestProperty("Accept", "application/json");
                            conn.setRequestProperty("Content-type", "application/json");
                            OutputStream os = conn.getOutputStream();
                            os.write(root.toString().getBytes("utf-8"));
                            os.flush();
                            conn.getResponseCode();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

    }

    public void reloadView(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(HomeFragment.this).attach(HomeFragment.this).commitAllowingStateLoss();

    }




}
