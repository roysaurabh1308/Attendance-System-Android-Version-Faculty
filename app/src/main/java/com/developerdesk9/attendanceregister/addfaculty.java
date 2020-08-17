package com.developerdesk9.attendanceregister;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class addfaculty extends AppCompatActivity {
    Toolbar mToolbar;

    EditText Tname;
    EditText Tid;
    EditText tpassword;

    String tname,tid,tpass;
    String mEmail,mPassword;
    Button addBtn;
    DatabaseReference databaseFaculty;
    FirebaseAuth mAuth;
    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfaculty);


        mProgress=new ProgressDialog(this);


        mAuth=FirebaseAuth.getInstance();
        databaseFaculty= FirebaseDatabase.getInstance().getReference("Faculty");

        Tname = findViewById(R.id.facname_et);
        Tid = findViewById(R.id.facemail_et);
        tpassword = findViewById(R.id.facpass_et);
        addBtn= findViewById(R.id.facadd_btn);


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facstartadd();
            }
        });


    }

    public void facstartadd(){

        tname = Tname.getText().toString().trim();
        tid = Tid.getText().toString().toLowerCase().trim();
        tpass = tpassword.getText().toString().trim();

        if (tname.isEmpty())
        {
            Tname.setError("Enter name");
            return;
        }
        else if (tid.isEmpty()){
            Tid.setError("Enter email");
            return;
        }
        else if (tpass.isEmpty()){
            tpassword.setError("Enter Valid Password");
            return;
        }

        mProgress.setTitle("Registering..");
        mProgress.setMessage("Please wait...");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();

        mEmail=tid+"@iiitnr.edu.in";
        mPassword=tpass;



        mAuth.createUserWithEmailAndPassword(mEmail,mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Faculty faculty =new Faculty(tname,tid);
                    databaseFaculty.child(tid).setValue(faculty).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()){
                                mProgress.dismiss();
                                finish();
                                Toast.makeText(getApplicationContext(),"Faculty Added Successfully", Toast.LENGTH_LONG).show();

                            }
                            else {
                                mProgress.dismiss();
                                Toast.makeText(getApplicationContext(),"Failed...", Toast.LENGTH_LONG).show();

                            }

                        }
                    });
                    //sendEmailVerificaionLink();
                    mAuth.signOut();
                    finish();
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"Registration failed!!..",Toast.LENGTH_SHORT).show();
                    mProgress.dismiss();
                }
            }
        });




    }
}
