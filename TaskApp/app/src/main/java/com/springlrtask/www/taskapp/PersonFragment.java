package com.springlrtask.www.taskapp;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

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

import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * A simple {@link Fragment} subclass.
 */
public class PersonFragment extends Fragment {

    TextView personName;
    TextView personDesc;
    TextView personAdd;
    TextView personState;
    RatingBar personRating;
    ImageView personImage;

    private ProgressDialog mProgressDialog;

    private View view;

    private FirebaseAuth firebaseAuth;
    private String user_id;
    private DatabaseReference productLists;

    private StorageReference mImageStore;

    public PersonFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_person, container, false);

        personName = view.findViewById(R.id.person_name);
        personDesc = view.findViewById(R.id.person_Desc);
        personAdd = view.findViewById(R.id.person_Add);
        personState = view.findViewById(R.id.person_State);
        personRating = view.findViewById(R.id.person_rat);
        personImage = view.findViewById(R.id.person_image);

        firebaseAuth = FirebaseAuth.getInstance();
        try{
            user_id = firebaseAuth.getCurrentUser().getUid();

            productLists = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

            productLists.keepSynced(true);
            productLists.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    mProgressDialog = new ProgressDialog(getContext());
                    mProgressDialog.setTitle("Loading..");
                    mProgressDialog.setMessage("Please wait while we loading data");
                    mProgressDialog.setCanceledOnTouchOutside(false);
                    mProgressDialog.show();

                    String name = dataSnapshot.child("name").getValue().toString();
                    String desc = dataSnapshot.child("desc").getValue().toString();
                    String add = dataSnapshot.child("address").getValue().toString();
                    String cities = dataSnapshot.child("cities").getValue().toString();
                    String imageUrl = dataSnapshot.child("image").getValue().toString();
                    String ratt = dataSnapshot.child("rating").getValue().toString();
                    float r = Float.valueOf(ratt);
                    personName.setText(name);
                    personDesc.setText(desc);
                    personAdd.setText(add);
                    personState.setText(cities);
                    personRating.setRating(r);
                    personRating.setFocusable(false);

                    RequestOptions placeholderRequest = new RequestOptions();
                    Glide.with(getApplicationContext()).setDefaultRequestOptions(placeholderRequest).load(imageUrl).into(personImage);

                    mProgressDialog.dismiss();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }catch (Exception e){
           //Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
        }


        return view;
    }

}
