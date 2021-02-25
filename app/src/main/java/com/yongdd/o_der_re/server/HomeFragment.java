package com.yongdd.o_der_re.server;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    static TextView logInNameText;
    FrameLayout noneOrderLayout;
    RecyclerView orderListRecyclerView;
    OrderListAdapter orderListAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.home_fragment,container,false);

        logInNameText = view.findViewById(R.id.loginNameText);
        noneOrderLayout = view.findViewById(R.id.noneOrderLayout);

        orderListRecyclerView = view.findViewById(R.id.orderListRecyclerView);
        orderListAdapter = new OrderListAdapter();

        orderListRecyclerView.setHasFixedSize(false);
        orderListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        setLogInName(MainActivity.name);
        setOrderList(MainActivity.orderLists);

        return view;
    }

    public void setLogInName(String name){
        logInNameText.setText(name);
    }

    public void setOrderList(ArrayList<OrderClient> orderLists){
        if(orderLists.size()>0){
            setNoneOrder(false);

            for(OrderClient orderClient : orderLists){
                orderListAdapter.addItem(orderClient);
                orderListRecyclerView.setAdapter(orderListAdapter);
                orderListAdapter.notifyDataSetChanged();
            }
        }else{
            setNoneOrder(true);
        }

    }

    public void setNoneOrder(boolean none){
        if(none){
            noneOrderLayout.setVisibility(View.VISIBLE);
        }else{
            noneOrderLayout.setVisibility(View.GONE);
        }
    }

}
