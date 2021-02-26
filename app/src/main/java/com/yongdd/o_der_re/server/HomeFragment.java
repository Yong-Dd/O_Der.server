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

import java.util.ArrayList;

public class HomeFragment extends Fragment {
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
            reloadView();
        }
    }

    public void reloadView(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(HomeFragment.this).attach(HomeFragment.this).commitAllowingStateLoss();

    }


}
