package com.yongdd.o_der_re.server;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static androidx.appcompat.content.res.AppCompatResources.getColorStateList;

public class MenuListAdapter extends RecyclerView.Adapter<MenuListAdapter.MenuViewHolder> {
    ArrayList<String> menuLists = new ArrayList<>();
    Context context;
    private int mLastClickedPosition = Integer.MIN_VALUE;
    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.menu_list,parent,false);
        context = parent.getContext();


        return new MenuViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        String menu = menuLists.get(position);
        holder.setItem(menu,position);

    }

    @Override
    public int getItemCount() {
        return menuLists.size();
    }


    public void addItem(String menuName){
        menuLists.add(menuName);
    }

    public String getItem(int position){
        return menuLists.get(position);
    }



    public class MenuViewHolder extends RecyclerView.ViewHolder {
        Button menuListName;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            menuListName = itemView.findViewById(R.id.menuListButton);
            MenuFragment menuFragment = new MenuFragment();

            menuListName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {

                        toggleButton(position);
//                        menuFragment.menuChoice(position);
                    }

                }
            });

        }

        public void setItem(String menuName, int position) {
                menuListName.setText(menuName);
                menuListName.setTextColor(getColorStateList(context, R.color.menu_list_nofocus_button));
        }

        public void setItemFirst(String menuName){

            menuListName.setText(menuName);
            menuListName.setTextColor(getColorStateList(context, R.color.menu_list_focus_button));

        }

        @SuppressLint("ResourceAsColor")
        public void toggleButton(int position) {

            int lastClickedPosition = mLastClickedPosition;
            mLastClickedPosition = position;

            if (lastClickedPosition >= 0) {
                    menuListName.setTextColor(getColorStateList(context, R.color.menu_list_focus_button));
                    notifyItemChanged(lastClickedPosition);
            }

            if (mLastClickedPosition >= 0) {
                    menuListName.setTextColor(getColorStateList(context, R.color.menu_list_focus_button));

            }

        }
    }
}
