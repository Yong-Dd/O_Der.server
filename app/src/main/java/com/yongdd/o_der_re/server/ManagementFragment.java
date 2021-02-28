package com.yongdd.o_der_re.server;

import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

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
            Intent intent = new Intent(getActivity(),BannerManagement.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Bundle bundle = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.page_slide_in_right, R.anim.page_slide_out_left).toBundle();
            getActivity().startActivity(intent,bundle);
        }else if(v==orderListManagementButton){
            Intent intent = new Intent(getActivity(),OrderListManagement.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Bundle bundle = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.page_slide_in_right, R.anim.page_slide_out_left).toBundle();
            getActivity().startActivity(intent,bundle);
        }else if(v==salesButton){
            Intent intent = new Intent(getActivity(),SalesManagement.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Bundle bundle = ActivityOptions.makeCustomAnimation(getActivity(), R.anim.page_slide_in_right, R.anim.page_slide_out_left).toBundle();
            getActivity().startActivity(intent,bundle);
        }
    }


}
