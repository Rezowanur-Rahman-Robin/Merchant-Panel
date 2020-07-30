package com.robin.onlinecourierservice;

import android.content.Intent;
import android.os.Bundle;



import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;

import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private String currentUserId;
    private DatabaseReference MerchantRef;
    private CircleImageView navImage;
    private TextView navName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth=FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if(currentUser!=null){
            currentUserId=currentUser.getUid();
        }



        MerchantRef= FirebaseDatabase.getInstance().getReference().child("Merchants");



        drawer=findViewById(R.id.drawer_layout);


        navigationView=findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView=navigationView.getHeaderView(0);


        navImage=(CircleImageView)headerView.findViewById(R.id.nav_header_image);
        navName=(TextView)headerView.findViewById(R.id.nav_name);




        ActionBarDrawerToggle toggle =new ActionBarDrawerToggle(this,drawer,toolbar,
                R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);


        }





    }


    @Override
    public void onStart() {
        super.onStart();

        if (currentUser==null){

            SendMerchantToLogin();
        }
        else{
            getNavInfo();

        }

    }



    private void getNavInfo() {
        MerchantRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if((dataSnapshot.exists() && dataSnapshot.hasChild("profile-image-link")) ){


                    String retriveMerchantName=dataSnapshot.child("name").getValue().toString();
                    navName.setText(retriveMerchantName);



                    String retriveMerchantImageNAv=dataSnapshot.child("profile-image-link").getValue().toString();

                    if(!retriveMerchantImageNAv.isEmpty()){
                        Picasso.with(getApplicationContext()).load(retriveMerchantImageNAv).into(navImage);
                    }



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SendMerchantToLogin() {
        Intent loginIntent = new Intent(getApplicationContext(),MerchantLoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch(menuItem.getItemId()){

            case R.id.nav_home:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
                break;

            case R.id.nav_profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
                break;

            case R.id.nav_add_product:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AddProductFragment()).commit();
                break;

            case R.id.nav_my_products:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new MyProductsFragment()).commit();
                break;

            case R.id.nav_orders:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new OrdersFragment()).commit();
                break;

            case R.id.nav_wallet:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new WalletFragment()).commit();
                break;

            case R.id.logout_merchant:
                getMerchantLogout();
                break;


        }

        drawer.closeDrawer(GravityCompat.START);


        return true;
    }

    private void getMerchantLogout() {

        mAuth.signOut();
        SendMerchantToLogin();
    }


    @Override
    public void onBackPressed() {

        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }


    }




}
