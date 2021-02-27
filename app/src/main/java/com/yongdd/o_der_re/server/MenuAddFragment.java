package com.yongdd.o_der_re.server;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.TreeSet;

public class MenuAddFragment extends Fragment implements View.OnClickListener {
    static ImageView menuImage;
    static EditText menuNameText, menuPriceText;
    static Spinner menuDelimiterSpinner, menuHotIceSpinner;
    Button menuAddButton, xButton;

    MenuFragment menuFragment;

    static boolean imageChoice;
    static String imageName;
    static Uri imageUri;
    static int menuDelimiter;
    static int menuHotIce;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_add,container,false);

        imageChoice = false;
        imageName = "";
        imageUri = null;

        menuFragment = new MenuFragment();

        menuImage = view.findViewById(R.id.MA_menuImg);
        menuNameText = view.findViewById(R.id.MA_menuNameText);
        menuPriceText = view.findViewById(R.id.MA_menuPriceText);
        menuAddButton = view.findViewById(R.id.MA_menuAddButton);
        xButton = view. findViewById(R.id.MA_xButton);
        menuDelimiterSpinner = view.findViewById(R.id.MA_menuDelimiterSpinner);
        menuHotIceSpinner = view.findViewById(R.id.MA_hotIceSpinner);

        menuImage.setOnClickListener(this);
        xButton.setOnClickListener(this);
        menuAddButton.setOnClickListener(this);

        menuDelimiterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                menuDelimiter = position+1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                menuDelimiter = 1;
            }
        });
        menuHotIceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                menuHotIce = position+1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                menuHotIce = 1;
            }
        });

        return view;
    }

    @Override
    public void onClick(View v) {
        if(v==xButton){
            menuFragment.menuAddShow(false);
        }else if(v==menuAddButton){
            if(imageUri!=null){
                ImageUpload(imageUri,imageName);
            }else{
                getMaxCount("");
            }

        }else if(v==menuImage){
            MainActivity.menuAddClicked = true;
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 10);
        }
    }

    public void onActivityResult(Uri uri, String ImgPath, String ImgName){
        Log.d("menuChoice","image edit fragment activity called & imgPath "+ImgPath);
        imageChoice =true;

        menuImage.setImageURI(uri);

        imageName = ImgName;
        imageUri = uri;

    }

    public void ImageUpload(Uri uri, String ImgName){
        Uri file = uri;
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference riversRef = storageRef.child(file.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(file);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

                Log.d("imageUpload","이미지 업로드 실패 "+exception);
                return;

            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d("imageUpload","이미지 업로드 성공");

                getMaxCount(ImgName);
            }
        });
    }

/*    private void addMenu(String imgPath){
        String menuName =menuNameText.getText().toString();
        int menuPrice = Integer.valueOf(menuPriceText.getText().toString());


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("menus/"+menu_id);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                snapshot.getRef().child("menuDelimiter").setValue(menuDelimiter);
                snapshot.getRef().child("menuHotIce").setValue(menuHotIce);
                snapshot.getRef().child("menuImgPath").setValue(imgPath);
                snapshot.getRef().child("menuName").setValue(menuName);
                snapshot.getRef().child("menuPrice").setValue(menuPrice);

                menuFragment.menuEditShow(false);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("editMenu","menu업로드 실패");
            }
        });

    }*/

    private void getMaxCount(String ImgName){

        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("menus");
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long maxCount = snapshot.getChildrenCount();
                getMaxId(maxCount, ImgName);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getMaxId(long maxCount, String ImgName){

        TreeSet<Integer> orderId = new TreeSet<>();

        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("menus");
        database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                orderId.add(Integer.parseInt(snapshot.getKey()));

                if(orderId.size()==maxCount){
                    maxId(orderId,ImgName);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) { }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) { }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }



    private void maxId(TreeSet<Integer> orderId, String ImgName){

        int maxId = orderId.last();

        if(maxId>=0){
            addDB(maxId,ImgName);
        }
    }

    private void addDB(int maxId,String ImgName){
        String menuName = menuNameText.getText().toString();
        int menuPrice = Integer.valueOf(menuPriceText.getText().toString());

        DbMenu menu = new DbMenu(menuDelimiter,menuName,menuPrice,menuHotIce,ImgName);


        DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("menus");
        database.child(String.valueOf(maxId + 1)).setValue(menu).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    menuFragment.menuAddShow(false);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("menuAddFragement"," DB등록 실패");
            }
        });

    }
}
