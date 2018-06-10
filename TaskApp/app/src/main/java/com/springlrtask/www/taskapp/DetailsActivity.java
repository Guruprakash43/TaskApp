package com.springlrtask.www.taskapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class DetailsActivity extends AppCompatActivity {

    private EditText detailsName;
    private EditText detailsDesc;
    private EditText detailsAdd;
    private RatingBar detailsRating;
    private Spinner detailsDrop;
    private ImageButton detailsCam;
    private Button detailsSave;
    private TextView detailsLink;

    private ProgressDialog mProgressDialog;
    private String download_url="";

    private static final int GALLERY_PICK = 1;

    private DatabaseReference mBusinessDetails;
    private StorageReference mImageStore;

    String drop;
    String user_id;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        detailsName = (EditText)findViewById(R.id.details_businame);
        detailsDesc = (EditText)findViewById(R.id.details_busidesc);
        detailsAdd = (EditText)findViewById(R.id.details_busiadd);
        detailsRating = (RatingBar)findViewById(R.id.ratingBar);
        detailsCam = (ImageButton)findViewById(R.id.details_busicam);
        detailsDrop = (Spinner)findViewById(R.id.spinner1);
        detailsSave = (Button)findViewById(R.id.details_save);
        detailsLink = (TextView)findViewById(R.id.details_link);

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        mImageStore = FirebaseStorage.getInstance().getReference();

        final String[] items = new String[]{"Hyderabad", "Mumbai", "Bangalore", "Chennai", "kolkata", "Pune", "Ahmedabad", "New Delhi", "Kochi", "Mangalore"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        detailsDrop.setAdapter(adapter);

        detailsDrop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //Toast.makeText(DetailsActivity.this, ""+items[position], Toast.LENGTH_SHORT).show();
                drop = items[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        detailsCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent,"Select Image"),GALLERY_PICK);

            }
        });

        detailsSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgressDialog = new ProgressDialog(DetailsActivity.this);
                mProgressDialog.setTitle("Uploading Your details");
                mProgressDialog.setMessage("Please wait while we uploading your profile");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                String dd = String.valueOf(detailsRating.getRating());
                String name_b = detailsName.getText().toString();
                String desc_b = detailsDesc.getText().toString();
                String add_b = detailsAdd.getText().toString();
                if (name_b.isEmpty())
                {
                    Toast.makeText(DetailsActivity.this, "Enter Bussiness name", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                    return;
                }
                if (desc_b.isEmpty())
                {
                    Toast.makeText(DetailsActivity.this, "Enter Description", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                    return;
                }
                if (add_b.isEmpty())
                {
                    Toast.makeText(DetailsActivity.this, "Enter address", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                    return;
                }
                if(download_url.isEmpty())
                {
                    Toast.makeText(DetailsActivity.this, "Please select image", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                    return;
                }

                Map<String, String> productMap = new HashMap<>();
                productMap.put("name", name_b);
                productMap.put("desc", desc_b);
                productMap.put("address", add_b);
                productMap.put("rating", dd);
                productMap.put("cities", drop);
                productMap.put("user_id",user_id);
                productMap.put("image",download_url);

                mBusinessDetails = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
                mBusinessDetails.setValue(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mProgressDialog.dismiss();
                        Toast.makeText(DetailsActivity.this, "Details Saved", Toast.LENGTH_SHORT).show();
                        Intent toMain = new Intent(DetailsActivity.this,MainActivity.class);
                        startActivity(toMain);
                        finish();
                    }
                });

            }
        });

    }


    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){

        mProgressDialog = new ProgressDialog(DetailsActivity.this);
        mProgressDialog.setTitle("Adding Image");
        mProgressDialog.setMessage("Please wait while we uploading your profile");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        super.onActivityResult(requestCode,resultCode,data);

        String name="";
        name = detailsName.getText().toString();
        if (name.equals(""))
        {
            Toast.makeText(this, "Please add name to product", Toast.LENGTH_SHORT).show();
            mProgressDialog.dismiss();
            return;
        }
        else
        {

            if (resultCode == RESULT_OK) {
                Uri resultUri = data.getData();
                final String encodeImages = resultUri.toString();
                File thumb_filepath = new File(resultUri.getPath());
                final StorageReference filepath = mImageStore.child("BusinesImages").child( name+".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){

                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    download_url = uri.toString();
                                    detailsLink.setText(download_url);
                                }
                            });
                            mProgressDialog.dismiss();
                        }
                        else {
                            mProgressDialog.dismiss();
                            Toast.makeText(DetailsActivity.this, "Sorry Try after some try", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

        }

    }

}
