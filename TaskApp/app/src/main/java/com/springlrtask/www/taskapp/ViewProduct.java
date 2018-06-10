package com.springlrtask.www.taskapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import javax.security.auth.callback.Callback;

public class ViewProduct extends AppCompatActivity {

    TextView itemId;
    TextView itemName;
    TextView itemDesc;
    TextView itemMRP;
    TextView itemQuantity;
    TextView itemPrice;

    ImageView itemImage;
    ImageButton itemEdit;
    private ProgressDialog mProgressDialog;

    private StorageReference mImageStore;

    private FirebaseAuth firebaseAuth;
    private String user_id;
    private DatabaseReference productLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_product);

        final String s = getIntent().getStringExtra("name");
        //Toast.makeText(this, ""+s, Toast.LENGTH_SHORT).show();

        itemId = (TextView)findViewById(R.id.item_id);
        itemName = (TextView)findViewById(R.id.item_name);
        itemDesc = (TextView)findViewById(R.id.item_desc);
        itemQuantity = (TextView)findViewById(R.id.item_quantity);
        itemMRP = (TextView)findViewById(R.id.item_mrp);
        itemPrice = (TextView)findViewById(R.id.item_price);

        itemImage = (ImageView) findViewById(R.id.item_image);
        itemEdit = (ImageButton) findViewById(R.id.edit_p);

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        productLists = FirebaseDatabase.getInstance().getReference().child("Products").child(user_id);

        productLists.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String n = snapshot.child("name").getValue().toString();
                    if (n.equals(s))
                    {
                        itemId.setText(snapshot.child("id").getValue().toString());
                        itemName.setText(snapshot.child("name").getValue().toString());
                        itemDesc.setText(snapshot.child("desc").getValue().toString());
                        itemPrice.setText("Price: "+snapshot.child("price").getValue().toString());
                        itemMRP.setText("MRP: "+snapshot.child("mrp").getValue().toString());
                        itemQuantity.setText("Quantity: "+snapshot.child("quantity").getValue().toString());

                        mImageStore = FirebaseStorage.getInstance().getReference();
                        RequestOptions placeholderRequest = new RequestOptions();
                       // placeholderRequest.placeholder(R.drawable.default_image);
                        Glide.with(getApplicationContext()).setDefaultRequestOptions(placeholderRequest).load(snapshot.child("image").getValue().toString()).into(itemImage);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        itemEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toEdit = new Intent(ViewProduct.this,EditProduct.class);
                toEdit.putExtra("name", itemName.getText().toString());
                startActivity(toEdit);
            }
        });

    }

}
