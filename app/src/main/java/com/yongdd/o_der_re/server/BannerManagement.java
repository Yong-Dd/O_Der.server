package com.yongdd.o_der_re.server;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class BannerManagement extends AppCompatActivity implements View.OnClickListener {
    static ArrayList<BannerUri> banners = new ArrayList<>();
    static ArrayList<MenuUri> menus = new ArrayList<>();

    static Button backButton, editFloatButton, editCloseButton, editButton;
    static RecyclerView bannerRecyclerview;
    static BannerAdapter bannerAdapter;
    private static ConstraintLayout bannerEditLayout;
    ConstraintLayout loadingLayout;
    Spinner titleListSpinner, menuChoiceSpinner;
    EditText bannerMenuDescText;

    int titleChoice;
    int menuChoice;
    int editPosition;
    BannerUri editBannerUri;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.banner_management);

        bannerMenuDescText = findViewById(R.id.BM_bannerMenuDescText);
        loadingLayout = findViewById(R.id.BM_loadingLayout);
        bannerEditLayout = findViewById(R.id.BM_bannerEditLayout);
        backButton = findViewById(R.id.BM_backButton);
        editFloatButton = findViewById(R.id.BM_editFloatButton);
        editCloseButton = findViewById(R.id.BM_editCloseButton);
        editButton = findViewById(R.id.BM_editButton);
        backButton.setOnClickListener(this);
        editFloatButton.setOnClickListener(this);
        editButton.setOnClickListener(this);
        editCloseButton.setOnClickListener(this);


        bannerRecyclerview = findViewById(R.id.BM_bannerRecyclerview);
        bannerRecyclerview.setHasFixedSize(true);
        bannerRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        bannerAdapter = new BannerAdapter();


        titleListSpinner = findViewById(R.id.BM_titleListSpinner);
        menuChoiceSpinner = findViewById(R.id.BM_menuChoiceSpinner);
        titleListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                titleChoice = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                titleChoice = 0;
            }
        });

        menuChoiceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                menuChoice = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                menuChoice = 0;
            }
        });


        if(banners.size()==3){
            Log.d("banner","size>3");
            setBanner();
            setMenu();
        }else{
            getBannerDB();
            setMenu();
            Log.d("banner","size 0");
        }



    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void getBannerDB() {
        loadingLayout.setVisibility(View.VISIBLE);
        ProgressBar proBar = (ProgressBar) findViewById(R.id.progressBar);
        if (proBar != null) {
            proBar.setIndeterminate(true);
            proBar.setIndeterminateTintList(ColorStateList.valueOf(Color.rgb(43,144,217)));
        }
        Log.d("Banner", "getBannerDB called");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Log.d("Banner", "getBannerDB database");
        DatabaseReference ref = database.getReference("banners");
        Log.d("Banner", "getBannerDB reference");
        ref.orderByChild("titleName").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Banner banner = snapshot.getValue(Banner.class);
                getImageUri(banner);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Log.d("banner", "data change called");
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Banner", "getBannerDB error " + error);
            }
        });
    }

    public void getImageUri(Banner banner){
        Log.d("Banner","imageUri called");

        String imgPath = banner.getMenuImgPath();

        FirebaseStorage storage = FirebaseStorage.getInstance("gs://oder-e6555.appspot.com");
        StorageReference storageRef = storage.getReference();
        storageRef.child(imgPath).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                banners.add(new BannerUri(banner,uri));
                if(banners.size()==3){
                    setBanner();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("Banner","Uri 오류 "+exception);
            }
        });

    }

    public void setBanner(){
        for(BannerUri bannerUri:banners){
            bannerAdapter.addItem(bannerUri);
        }
        bannerRecyclerview.setAdapter(bannerAdapter);
        bannerAdapter.notifyDataSetChanged();
        loadingLayout.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if(v==backButton){
            Log.d("banner","backbutton clicked");
            onBackPressed();
        }else if(v== editFloatButton){
            bannerEditShow(true);
        }else if(v==editCloseButton){
            bannerEditShow(false);
        }else if(v==editButton){
            bannerEdit();
        }
    }
    public void bannerEditShow(boolean show){
        int duration = 200;

        Animation pageUpAnim = AnimationUtils.loadAnimation(this, R.anim.page_slide_up);
        pageUpAnim.setDuration(duration);
        Animation pageDownAnim = AnimationUtils.loadAnimation(this, R.anim.page_slide_down);
        pageDownAnim.setDuration(duration);

        if(show) {
            bannerEditLayout.setVisibility(View.VISIBLE);
            bannerEditLayout.setAnimation(pageUpAnim);
        }else{
            bannerEditLayout.setVisibility(View.GONE);
            bannerEditLayout.setAnimation(pageDownAnim);
            bannerMenuDescText.setText("");
            menuChoiceSpinner.setSelection(0);
            titleListSpinner.setSelection(0);
        }

    }

    public void setMenu(){
        menus = MainActivity.menus;
        ArrayList<String> menuName = new ArrayList<>();

        for(MenuUri menuUri : menus){
            menuName.add(menuUri.getMenu().getMenuName());
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, menuName);
        menuChoiceSpinner.setAdapter(arrayAdapter);

    }

    private void bannerEdit(){
        Log.d("banner","bannerEdit called");
        String bannerDesc = bannerMenuDescText.getText().toString();
        Menu menu = menus.get(menuChoice).getMenu();
        String menuName = menu.getMenuName();
        String menuImgPath = menu.getMenuImgPath();
        int menuId = menu.getMenuId();
        Uri uri = menus.get(menuChoice).getUri();


        String titleName="";
        if(titleChoice==0){
            titleName ="새로운 메뉴";
        }else if(titleChoice==1){
            titleName = "추천 메뉴";
        }else if(titleChoice==2){
            titleName = "인기 메뉴";
        }
        Log.d("banner","bannerEdit titleName "+titleName);

        Banner banner = new Banner(titleName,menuName,bannerDesc,menuId,menuImgPath);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("banners/"+titleChoice);

        String finalTitleName = titleName;
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("banner","bannerEdit onDataChange ");

                snapshot.getRef().child("menuDesc").setValue(bannerDesc);
                snapshot.getRef().child("menuId").setValue(menuId);
                snapshot.getRef().child("menuImgPath").setValue(menuImgPath);
                snapshot.getRef().child("menuName").setValue(menuName);


                for(int i=0; i<3; i++){
                    Log.d("banner","bannerEdit onDataChange in for "+i);
                    Log.d("banner","bannerEdit onDataChange finalTitleName "+finalTitleName);
                    String bannerName = banners.get(i).getBanner().getTitleName();
                    if(bannerName.equals(finalTitleName) || bannerName == finalTitleName){
                        Log.d("banner","bannerEdit onDataChange titlename 찾음");
                        bannerEditShow(false);

                        banners.set(i,new BannerUri(banner,uri));
                        bannerAdapter.updateItem(i,new BannerUri(banner,uri));
                        bannerAdapter.notifyItemChanged(i);

                        Log.d("banner","bannerEdit onDataChange change completed");


                    }

                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("editMenu","menu업로드 실패");
            }
        });


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.page_slide_in_left, R.anim.page_slide_out_right);
    }


}
