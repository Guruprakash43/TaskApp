package com.springlrtask.www.taskapp;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProductsFragment extends Fragment {

    private FloatingActionButton addPostBtn;
    private View view;

    private FirebaseAuth firebaseAuth;
    private String user_id;
    private DatabaseReference productLists;

    private Toolbar mToolbar;

    private ProgressDialog mProgressDialog;

    private ListView products;
    ArrayList<String> p_names = new ArrayList<String>();
    // String p_names[];


    public ProductsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_products, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        try{
            user_id = firebaseAuth.getCurrentUser().getUid();

            productLists = FirebaseDatabase.getInstance().getReference().child("Products").child(user_id);
            products = view.findViewById(R.id.products_list_names);

            mToolbar = view.findViewById(R.id.productsToolbar);
            mToolbar.setTitle("Products");

            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setTitle("Uploading Products");
            mProgressDialog.setMessage("Please wait while we uploading your profile");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();

            productLists.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        String n = snapshot.child("name").getValue().toString();
                        p_names.add(n);

                    }
                    ArrayAdapter adapter = new ArrayAdapter<String>(getContext(),
                            android.R.layout.simple_list_item_1, p_names);

                    products.setAdapter(adapter);
                    mProgressDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    mProgressDialog.dismiss();
                }
            });

            products.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent toView = new Intent(getContext(),ViewProduct.class);
                    toView.putExtra("name", p_names.get(position));
                    startActivity(toView);

                }
            });

            addPostBtn = view.findViewById(R.id.add_post_btn);
            addPostBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent newPostIntent = new Intent(getContext(), AddProduct.class);
                    startActivity(newPostIntent);

                }

            });

        }catch (Exception e)
        {
           // Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
        }

        return view;
    }
}
