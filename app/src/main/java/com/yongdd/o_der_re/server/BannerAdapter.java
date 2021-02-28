package com.yongdd.o_der_re.server;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
    ArrayList<BannerUri> banners = new ArrayList<>();
    Context context;

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.banner_item,parent,false);

        context = view.getContext();

        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        BannerUri banner = banners.get(position);
        holder.setItem(banner);
    }

    @Override
    public int getItemCount() {
        return banners.size();
    }


    public void addItem(BannerUri banner){
        banners.add(banner);
    }

    public BannerUri getItem(int position){
        return banners.get(position);
    }

    public void clearItem(){banners.clear();}

    public void updateItem(int position,BannerUri bannerUri){banners.set(position,bannerUri);}


    public class BannerViewHolder extends RecyclerView.ViewHolder{
        TextView menuTitle;
        TextView menuName;
        TextView menuDesc;
        ImageView menuImg;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            menuTitle = itemView.findViewById(R.id.banner_Title);
            menuName = itemView.findViewById(R.id.banner_MenuName);
            menuDesc = itemView.findViewById(R.id.banner_MenuDesc);
            menuImg = itemView.findViewById(R.id.banner_img);

        }
        public void setItem(BannerUri bannerUri){
            Banner banner = bannerUri.getBanner();
            Log.d("Banner","banner null?"+banner==null?null:"not null");

            String bannerTitleName = banner.getTitleName();
            String bannerMenuName = banner.getMenuName();
            String bannerMenuDesc = banner.getMenuDesc();
            int bannerMenuId = banner.getMenuId();
            String bannerMenuImgPath = banner.getMenuImgPath();

            menuTitle.setText(bannerTitleName);
            menuName.setText(bannerMenuName);
            menuDesc.setText(bannerMenuDesc);


            Uri uri = bannerUri.getUri();


            //차후 이미지 glide 등으로 대체
            if(uri!=null) {
                Glide.with(context).load(uri).into((ImageView) menuImg);
            }else{
                menuImg.setImageResource(R.drawable.standard_img);
            }


        }
    }
}
