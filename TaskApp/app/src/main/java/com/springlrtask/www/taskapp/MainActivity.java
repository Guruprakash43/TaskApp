package com.springlrtask.www.taskapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private String current_user_id;
    private ProductsFragment productsFragment;
    private PersonFragment personFragment;

    private BottomNavigationView mainbottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        mainbottomNav = findViewById(R.id.mainBottomNav);


        productsFragment = new ProductsFragment();
        personFragment = new PersonFragment();

        initializeFragment();


        mainbottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.main_container);

                switch (item.getItemId()) {

                    case R.id.bottom_action_products:

                        replaceFragment(productsFragment, currentFragment);
                        return true;

                    case R.id.bottom_action_person:

                        replaceFragment(personFragment, currentFragment);
                        return true;

                    default:
                        return false;


                }


            }


        });

    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        try{
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if(currentUser == null)
            {
                sendToLogin();
            }
            else {
                //Toast.makeText(this, "MainActivity", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            //Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
        }
    }

    private void sendToLogin() {

        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();

    }

    public void logout(View view) {
        mAuth.signOut();
        sendToLogin();
    }


    private void initializeFragment(){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        fragmentTransaction.add(R.id.main_container, productsFragment);
        fragmentTransaction.add(R.id.main_container, personFragment);

        fragmentTransaction.hide(personFragment);

        fragmentTransaction.commit();

    }


    private void replaceFragment(Fragment fragment, Fragment currentFragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if(fragment == productsFragment){

            fragmentTransaction.hide(personFragment);


        }

        if(fragment == personFragment){

            fragmentTransaction.hide(productsFragment);

        }
        fragmentTransaction.show(fragment);

        //fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();

    }


    public void logoutMe(View view) {

        FirebaseAuth.getInstance().signOut();
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();

    }
}
