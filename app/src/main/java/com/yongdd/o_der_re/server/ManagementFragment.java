package com.yongdd.o_der_re.server;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class ManagementFragment extends Fragment implements View.OnClickListener {
    Button bannerButton, orderListManagementButton, salesButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.management_fragment,container,false);

        bannerButton = view.findViewById(R.id.M_bannerButton);
        orderListManagementButton = view.findViewById(R.id.M_orderListMagementButton);
        salesButton = view.findViewById(R.id.M_SalesButton);

        bannerButton.setOnClickListener(this);
        orderListManagementButton.setOnClickListener(this);
        salesButton.setOnClickListener(this);




        return view;
    }

    @Override
    public void onClick(View v) {
        if(v==bannerButton){
//            Intent intent = new Intent(getActivity(),OrderList.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            Bundle bundle = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.page_slide_in_right, R.anim.page_slide_out_left).toBundle();
//            getActivity().startActivity(intent,bundle);
        }else if(v==orderListManagementButton){

        }else if(v==salesButton){

        }
    }
}
