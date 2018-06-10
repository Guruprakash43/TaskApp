package com.springlrtask.www.taskapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class EditProduct extends AppCompatActivity {

    EditText editId;
    EditText editName;
    EditText editDesc;
    EditText editQuantity;
    EditText editMRP;
    EditText editPrice;

    TextView editLink;

    ImageButton editCamera;

    Button editSave;

    private ProgressDialog mProgressDialog;

    private StorageReference mImageStore;

    private FirebaseAuth firebaseAuth;
    private String user_id;
    private DatabaseReference productLists;

    private String download_url="";

    private DatabaseReference mEditProduct;
    private static final int GALLERY_PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        final String s = getIntent().getStringExtra("name");
        //Toast.makeText(this, ""+s, Toast.LENGTH_SHORT).show();

        editId = (EditText)findViewById(R.id.edit_id);
        editName = (EditText)findViewById(R.id.edit_name);
        editDesc = (EditText)findViewById(R.id.edit_desc);
        editQuantity = (EditText)findViewById(R.id.edit_quantity);
        editMRP = (EditText)findViewById(R.id.edit_mrp);
        editPrice = (EditText)findViewById(R.id.edit_price);
        editSave = (Button)findViewById(R.id.edit_save);

        editLink = (TextView)findViewById(R.id.edit_link);

        editCamera = (ImageButton)findViewById(R.id.edit_camera);

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        productLists = FirebaseDatabase.getInstance().getReference().child("Products").child(user_id);
        mImageStore = FirebaseStorage.getInstance().getReference();

        editCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent,"Select Image"),GALLERY_PICK);

            }
        });


        editSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgressDialog = new ProgressDialog(EditProduct.this);
                mProgressDialog.setTitle("Updating..");
                mProgressDialog.setMessage("Please wait while we uploading your profile");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                final String id_product = editId.getText().toString();
                final String name_product = editName.getText().toString();
                final String desc_product = editDesc.getText().toString();
                final String quantity_product = editQuantity.getText().toString();
                final String mrp_product = editMRP.getText().toString();
                final String price_product = editPrice.getText().toString();

                if(id_product.isEmpty())
                {
                    Toast.makeText(EditProduct.this, "Enter id", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                    return;
                }
                if(name_product.isEmpty())
                {
                    Toast.makeText(EditProduct.this, "Enter Name", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                    return;
                }
                if(desc_product.isEmpty())
                {
                    Toast.makeText(EditProduct.this, "Enter Description", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                    return;
                }
                if(quantity_product.isEmpty())
                {
                    Toast.makeText(EditProduct.this, "Enter Quantity", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                    return;
                }
                if(mrp_product.isEmpty())
                {
                    Toast.makeText(EditProduct.this, "Enter MRP", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                    return;
                }
                if(price_product.isEmpty())
                {
                    Toast.makeText(EditProduct.this, "Enter Price", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                    return;
                }

                if (download_url.isEmpty())
                {
                    Toast.makeText(EditProduct.this, "Please select Image", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                    return;
                }

                Map<String, String> productMap = new HashMap<>();
                productMap.put("id", id_product);
                productMap.put("name", name_product);
                productMap.put("desc", desc_product);
                productMap.put("quantity", quantity_product);
                productMap.put("mrp", mrp_product);
                productMap.put("price", price_product);
                productMap.put("image",download_url);

                mEditProduct = FirebaseDatabase.getInstance().getReference().child("Products").child(user_id).child(name_product);
                mEditProduct.setValue(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mProgressDialog.dismiss();
                        Intent toMain = new Intent(EditProduct.this,MainActivity.class);
                        startActivity(toMain);
                        finish();
                    }
                });
            }
        });
        productLists.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mProgressDialog = new ProgressDialog(EditProduct.this);
                mProgressDialog.setTitle("Loading");
                mProgressDialog.setMessage("Please wait while we retriving data");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String n = snapshot.child("name").getValue().toString();
                    if (n.equals(s))
                    {
                        editId.setText(snapshot.child("id").getValue().toString());
                        editName.setText(snapshot.child("name").getValue().toString());
                        editDesc.setText(snapshot.child("desc").getValue().toString());
                        editPrice.setText(snapshot.child("price").getValue().toString());
                        editMRP.setText(snapshot.child("mrp").getValue().toString());
                        editQuantity.setText(snapshot.child("quantity").getValue().toString());

//                        mImageStore = FirebaseStorage.getInstance().getReference();
//                        RequestOptions placeholderRequest = new RequestOptions();
//                        // placeholderRequest.placeholder(R.drawable.default_image);
//
//                        Glide.with(getApplicationContext()).setDefaultRequestOptions(placeholderRequest).load(snapshot.child("image").getValue().toString()).into(itemImage);
                    }
                }
                mProgressDialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){

        mProgressDialog = new ProgressDialog(EditProduct.this);
        mProgressDialog.setTitle("Adding Image");
        mProgressDialog.setMessage("Please wait while we adding image");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        super.onActivityResult(requestCode,resultCode,data);

        String name="";
        name = editName.getText().toString();
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
                final StorageReference filepath = mImageStore.child("productImages").child( name+".jpg");
                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){

                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    download_url = uri.toString();
                                    editLink.setText(download_url);
                                }
                            });
                            mProgressDialog.dismiss();
                        }
                        else {
                            mProgressDialog.dismiss();
                            Toast.makeText(EditProduct.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }
}
