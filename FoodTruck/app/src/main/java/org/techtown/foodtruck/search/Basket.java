package org.techtown.foodtruck.search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.techtown.foodtruck.DO.Image;
import org.techtown.foodtruck.DO.Location;
import org.techtown.foodtruck.DO.Order;
import org.techtown.foodtruck.DO.Order_history;
import org.techtown.foodtruck.DO.Truck;
import org.techtown.foodtruck.DO.UserAccount;
import org.techtown.foodtruck.MainActivity;
import org.techtown.foodtruck.R;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Basket extends AppCompatActivity {

    //???????????? textview
    TextView textView1;
    //???????????? ??????
    TextView textView2;
    //??????????????? textview
    TextView textView3;
    //??? ??? ?????? textview
    TextView textView4;
    //?????? ?????? ??????
    ImageView imageView;
    //???????????? ??????
    Button button1;
    //????????????
    Button button2;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    //order ?????? ?????? ?????????
    ArrayList<Order> order_list;
    //truck ??????
    Truck truck;
    //??? ?????? ?????? ??????
    int total_cost;
    //?????? ?????? ??????
    String address;
    //BasketAdapter ?????? ??????
    BasketAdapter adapter;
    Gson gson;
    Type listType;
    //?????????????????? ??????
    private FirebaseAuth mAuth;
    //?????????????????? ????????? ??????????????????
    private DatabaseReference databaseReference;
    //????????? ??????
    UserAccount userAccount;
    FirebaseUser user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);
        textView1 = findViewById(R.id.basket_textview);
        textView2 = findViewById(R.id.basket_textview2);
        textView3 = findViewById(R.id.basket_textview3);
        textView4 = findViewById(R.id.basket_textview4);
        imageView = findViewById(R.id.basket_imageview);
        button1 = findViewById(R.id.basket_button);
        button2 = findViewById(R.id.basket_button2);
        //order ?????? ???????????????????????? ????????????
        gson = new GsonBuilder().create();
        sharedPreferences = getSharedPreferences("order",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        String data = sharedPreferences.getString("order_list","");
        listType = new TypeToken<ArrayList<Order>>() {}.getType();
        order_list = gson.fromJson(data,listType);
        truck = (Truck) getIntent().getSerializableExtra("Truck");
        ActionBar ac = getSupportActionBar();
        ac.setTitle("????????????");
        total_cost = 0;
        ArrayList<Order> temporary_list = new ArrayList<>();
        //order_list ?????????(list?????? ????????? food??? ???????????? ?????? ????????? ??????????????? ?????????)  ex)   list : 1. food1 2???  2.  food2 2???  3. food1 1??? ->  1. food1 3??? 2. food2 2???
        for(Order order:order_list){
            if(temporary_list.size() > 0){
                boolean a = false;
                int i = 0;
                //?????? ????????? ????????? ??????
                for(Order temporary_order : temporary_list){
                    //?????? ????????????????????? order??? ?????? ????????? ????????? ????????????(food name??? ?????? ??????) temporary_list??? add?????? ?????? food_number??? ??????????????????.
                    if(order.getFood_name().equals(temporary_order.getFood_name())){
                        a = true;
                        temporary_list.get(i).setFood_number(temporary_list.get(i).getFood_number() + order.getFood_number());
                        break;
                    }
                    i++;
                }
                //????????? ??????????????? ????????? ???????????? ?????? ????????? ????????? ???????????? ????????? ????????? temporary_list??? add?????????.
                if(a == false){
                    temporary_list.add(order);
                }
            }
            //???????????? ?????? ?????? ???????????? order?????? ?????? ??????
            else{
                temporary_list.add(order);
            }
        }
        order_list = temporary_list;
        for(Order order: order_list ){
            total_cost = total_cost + (order.getFood_number() * Integer.parseInt(order.getFood_cost()));
        }
        //?????? ??????
        convertLocationToAddress();
        //??????????????? ??????
        textView3.setText(truck.getName());
        //??? ?????? ??????
        textView4.setText(Integer.toString(total_cost)+"???");
        //recyclerview ??????
        setRecycleView();
        //?????? ?????? listener ??????
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", address);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(v.getContext(),"?????? ?????????",Toast.LENGTH_SHORT).show();
            }
        });
        //?????? ?????? listener ??????
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json = gson.toJson(order_list,listType);
                editor.putString("order_list",json);
                editor.commit();
                finish();
            }
        });
        //?????? ?????? listener ??????
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("FoodTruck");
        databaseReference.child("UserAccount").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userAccount = snapshot.getValue(UserAccount.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //?????? ?????? ????????? ?????? ?????? and ?????? ??????
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //?????????????????? ????????????
                HashMap hashMap = new HashMap();
                int add = Integer.parseInt(truck.getOrder_count()) + 1;
                hashMap.put("order_count",Integer.toString(add));
                databaseReference.child("Truck").child(truck.getId()).updateChildren(hashMap);
                editor.clear();
                editor.commit();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                Order_history order_history = new Order_history(truck.getName(),order_list,dateFormat.format(date),truck.getImage(),truck.getId());
                databaseReference.child("Order_history").child(user.getUid()).push().setValue(order_history);
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("startFragment",3);
                //????????? ?????? ??????
                SharedPreferences orderData = getSharedPreferences(user.getUid(),MODE_PRIVATE);
                SharedPreferences.Editor orderDataEditor = orderData.edit();
                Gson gson = new GsonBuilder().create();
                String data = gson.toJson(order_history);
                String data2 = gson.toJson(truck);
                orderDataEditor.putString("Order_history",data);
                orderDataEditor.putString("Truck",data2);
                orderDataEditor.commit();
                startActivity(intent);
                Toast.makeText(getApplicationContext(),"????????????",Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(order_list.size() == 0){
            editor.clear();
            editor.commit();
        }
        else{
            String json = gson.toJson(order_list,listType);
            editor.putString("order_list",json);
            editor.commit();
        }
        super.onBackPressed();
    }

    private void convertLocationToAddress(){
        DecimalFormat f = new DecimalFormat("#.##");
        Location location = truck.getLocation();
        List<Address> list= null;
        Geocoder g = new Geocoder(this);
        try{
            list = g.getFromLocation(Double.parseDouble(location.getLatitude()),Double.parseDouble(location.getLongitude()),10);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,"??????",Toast.LENGTH_SHORT).show();
        }
        address = list.get(0).getAddressLine(0).substring(5);
        String distance = f.format(truck.getDistance()/1000);
        textView1.setText(address+" (????????????????????? "+distance+"km)");
    }

    private void setRecycleView(){
        RecyclerView recyclerView = findViewById(R.id.basket_recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new BasketAdapter(this);
        adapter.setItems(order_list);
        recyclerView.setAdapter(adapter);
    }
}