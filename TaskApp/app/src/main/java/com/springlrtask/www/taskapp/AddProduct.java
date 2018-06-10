package com.springlrtask.www.taskapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddProduct extends AppCompatActivity {

    private EditText productId;
    private EditText productName;
    private EditText productDesc;
    private EditText productQuantity;
    private EditText productMrp;
    private EditText productPrice;
    private ImageButton productImage;
    private Button addProduct;

    private TextView productLink;

    private ProgressDialog mProgressDialog;
    private Uri mainImageURI = null;
    private String download_url="";

    private DatabaseReference mAddingProduct;
    private StorageReference mImageStore;

    private FirebaseAuth firebaseAuth;
    private String user_id;

    private static final int GALLERY_PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();
        mImageStore = FirebaseStorage.getInstance().getReference();

        productId = (EditText)findViewById(R.id.product_id);
        productName = (EditText)findViewById(R.id.product_name);
        productDesc = (EditText)findViewById(R.id.product_desc);
        productQuantity = (EditText)findViewById(R.id.product_quantity);
        productMrp = (EditText)findViewById(R.id.product_mrp);
        productPrice = (EditText)findViewById(R.id.product_sp);
        productImage = (ImageButton)findViewById(R.id.product_image);
        productLink = (TextView)findViewById(R.id.product_link);
        addProduct = (Button)findViewById(R.id.add_product);

        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent,"Select Image"),GALLERY_PICK);

            }
        });

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProgressDialog = new ProgressDialog(AddProduct.this);
                mProgressDialog.setTitle("Uploading Product");
                mProgressDialog.setMessage("Please wait while we uploading your profile");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                final String id_product = productId.getText().toString();
                final String name_product = productName.getText().toString();
                final String desc_product = productDesc.getText().toString();
                final String quantity_product = productQuantity.getText().toString();
                final String mrp_product = productMrp.getText().toString();
                final String price_product = productPrice.getText().toString();

                if(id_product.isEmpty())
                {
                    Toast.makeText(AddProduct.this, "Enter id", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                    return;
                }
                if(name_product.isEmpty())
                {
                    Toast.makeText(AddProduct.this, "Enter Name", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                    return;
                }
                if(desc_product.isEmpty())
                {
                    Toast.makeText(AddProduct.this, "Enter Description", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                    return;
                }
                if(quantity_product.isEmpty())
                {
                    Toast.makeText(AddProduct.this, "Enter Quantity", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                    return;
                }
                if(mrp_product.isEmpty())
                {
                    Toast.makeText(AddProduct.this, "Enter MRP", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                    return;
                }
                if(price_product.isEmpty())
                {
                    Toast.makeText(AddProduct.this, "Enter Price", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                    return;
                }

                if (download_url.isEmpty())
                {
                    Toast.makeText(AddProduct.this, "Please select Image", Toast.LENGTH_SHORT).show();
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

                mAddingProduct = FirebaseDatabase.getInstance().getReference().child("Products").child(user_id).child(name_product);
                mAddingProduct.setValue(productMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(AddProduct.this, "Product Added", Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                        Intent toMain = new Intent(AddProduct.this,MainActivity.class);
                        startActivity(toMain);
                        finish();
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){

        super.onActivityResult(requestCode,resultCode,data);

        mProgressDialog = new ProgressDialog(AddProduct.this);
        mProgressDialog.setTitle("Adding Image");
        mProgressDialog.setMessage("Please wait while we uploading");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        String name;
        name = productName.getText().toString();
        if (name.isEmpty())
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
                                    productLink.setText(download_url);
                                }
                            });
                            mProgressDialog.dismiss();
                        }
                        else {
                            mProgressDialog.dismiss();
                            Toast.makeText(AddProduct.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

        }

    }

}
