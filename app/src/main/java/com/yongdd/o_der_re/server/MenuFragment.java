package com.yongdd.o_der_re.server;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MenuFragment extends Fragment {
    private static FrameLayout menuEditLayout, menuAddLayout;
    Button menuAddButton;

    static RecyclerView menuRecyclerView;
    static MenuAdapter menuAdapter;
    static Context context;

    static MenuListAdapter menuListAdapter;

    static ArrayList<MenuUri> menus = new ArrayList<>();

    static int lastClickedMenu;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_fragment,container,false);
        lastClickedMenu=-1;

        context = view.getContext();

        menuEditLayout = view.findViewById(R.id.M_menuEditLayout);
        menuAddLayout = view.findViewById(R.id.M_menuAddLayout);
        menuAddButton = view.findViewById(R.id.menuAddButton);
        menuAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuAddShow(true);
            }
        });


        //메인화면서 db메뉴 가져옴
        menus = MainActivity.menus;

        //menu List RecyclerView
        RecyclerView menuListRecyclerView = (RecyclerView) view.findViewById(R.id.menuListRecyclerView);
        menuListAdapter = new MenuListAdapter();
        menuListAdapter.addItem("COFFEE");
        menuListAdapter.addItem("NONCOFFEE");
        menuListAdapter.addItem("TEA");
        menuListAdapter.addItem("JUICE");
        menuListAdapter.addItem("DESSERT");

        menuListRecyclerView.setHasFixedSize(true);
        menuListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        menuListRecyclerView.setAdapter(menuListAdapter);

        //menu RecyclerView
        menuRecyclerView = (RecyclerView)view.findViewById(R.id.menuRecyclerView);
        menuAdapter = new MenuAdapter();
        menuRecyclerView.setHasFixedSize(true);
        menuRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        allMenuSetting();



        return view;
    }

    public void allMenuSetting(){
        for(int i=0; i<menus.size(); i++) {
            MenuUri menu = menus.get(i);
            if (menu != null) {
                menuAdapter.addItem(menu);
                menuRecyclerView.setAdapter(menuAdapter);
                menuAdapter.notifyDataSetChanged();
            } else {
                Log.d("menuDB", "menuChoice = menu null");
            }
        }
    }

    public static void menuEditShow(boolean show){
        int duration = 200;

        Animation pageUpAnim = AnimationUtils.loadAnimation(context, R.anim.page_slide_up);
        pageUpAnim.setDuration(duration);
        Animation pageDownAnim = AnimationUtils.loadAnimation(context, R.anim.page_slide_down);
        pageDownAnim.setDuration(duration);

        if(show) {
            menuEditLayout.setVisibility(View.VISIBLE);
            menuEditLayout.setAnimation(pageUpAnim);
        }else{
            menuEditLayout.setVisibility(View.GONE);
            menuEditLayout.setAnimation(pageDownAnim);
        }

    }

    public static void menuAddShow(boolean show){
        int duration = 200;

        Animation pageUpAnim = AnimationUtils.loadAnimation(context, R.anim.page_slide_up);
        pageUpAnim.setDuration(duration);
        Animation pageDownAnim = AnimationUtils.loadAnimation(context, R.anim.page_slide_down);
        pageDownAnim.setDuration(duration);

        if(show) {
            menuAddLayout.setVisibility(View.VISIBLE);
            menuAddLayout.setAnimation(pageUpAnim);
        }else{
            menuAddLayout.setVisibility(View.GONE);
            menuAddLayout.setAnimation(pageDownAnim);
        }

    }

    public void menuChoice(int position){
        Log.d("editMenu","menuChoice called");
        lastClickedMenu = position;
        menuAdapter.clearItem();
        Log.d("editMenu","menuAdapter clear");
        Log.d("editMenu","menuAdapter position " +position);
        for(int i=0; i<menus.size(); i++){
            MenuUri menu = menus.get(i);
            int delimiter = menus.get(i).getMenu().getMenuDelimiter();

            if((position+1) == delimiter){
                if(menu!=null){
                    Log.d("editMenu","menuAdapter adpater add ");
                    menuAdapter.addItem(menu);
                    menuRecyclerView.setAdapter(menuAdapter);
                    menuAdapter.notifyDataSetChanged();
                }else{
                    Log.d("menuDB","menuChoice = menu null");
                }

            }else{
                Log.d("menuDB","해당 선택 메뉴 없음");
            }
        }
    }


    public void updateItem(int position, MenuUri menuUri){
        Log.d("editMenu","updateItem called position "+position);
        menus.set(position,menuUri);
        int menuDelimiter = menuUri.getMenu().getMenuDelimiter();
        if(menuDelimiter == lastClickedMenu+1){
            int lastPosition = menuAdapter.updateItem(menuUri);
            Log.d("editMenu","menuAdapter updateItem last position " +lastPosition);
            menuAdapter.notifyItemChanged(lastPosition);
        }else{
            menuChoice(lastClickedMenu);
        }

    }

    public void reloadView(){
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(MenuFragment.this).attach(MenuFragment.this).commitAllowingStateLoss();

    }

}
