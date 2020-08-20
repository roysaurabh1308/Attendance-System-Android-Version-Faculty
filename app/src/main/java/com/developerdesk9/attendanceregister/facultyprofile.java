package com.developerdesk9.attendanceregister;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class facultyprofile extends AppCompatActivity {

    String tid;
    CircleImageView profileimgview;
    TextView fac_name_profile,designation;
    Button pic_upload_btn;

    DatabaseReference dbfaculty;
    FirebaseStorage firebaseStorage;
    private static int PICK_IMAGE=123;
    Uri imagepath;
    ProgressDialog mdialog;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==PICK_IMAGE && resultCode == RESULT_OK && data.getData() != null){
            imagepath=data.getData();
            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),imagepath);
                profileimgview.setImageBitmap(bitmap);
            }catch (IOException e){
                e.printStackTrace();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facultyprofile);
        mdialog=new ProgressDialog(this);

        dbfaculty= FirebaseDatabase.getInstance().getReference("Faculty");
        firebaseStorage=FirebaseStorage.getInstance();

        final StorageReference storageReference=firebaseStorage.getReference().child("Faculty");

        final Intent intent=getIntent();
        tid=intent.getStringExtra("tid");

        profileimgview=findViewById(R.id.profile_imageview);
        fac_name_profile=findViewById(R.id.fac_profile_name_et);
        designation=findViewById(R.id.fac_profile_desig_et);
        pic_upload_btn=findViewById(R.id.upload_pic_btn);


        dbfaculty.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fac_name_profile.setText(dataSnapshot.child(tid).child("tname").getValue(String.class).toUpperCase());
                designation.setText("PRIVILEGE: "+dataSnapshot.child(tid).child("type").getValue(String.class).toUpperCase());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        profileimgview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent1=new Intent();
                intent1.setType("image/*");
                intent1.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent1,"Select Image"),PICK_IMAGE);
                return false;
            }
        });

        pic_upload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdialog.setMessage("Uploading..");
                mdialog.setCanceledOnTouchOutside(false);
                mdialog.show();
                if (imagepath !=null){
                    StorageReference storageReference1=storageReference.child(tid);
                    UploadTask uploadTask=storageReference1.putFile(imagepath);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mdialog.dismiss();
                            Toast.makeText(getApplicationContext(),"Photo Uploaded",Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else {
                    mdialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_LONG).show();
                }
            }
        });

        StorageReference storageReference1=firebaseStorage.getReference("Faculty");
                storageReference1.child(tid).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).fit().centerCrop().into(profileimgview);
            }
        });



    }
}
