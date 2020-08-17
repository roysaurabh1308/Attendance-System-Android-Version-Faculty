package com.developerdesk9.attendanceregister;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText username,password;
    public Button loginbtn;
    String item;
    String userid,pass;
    DatabaseReference mreference;
    ProgressDialog mDialog;


    private static long back_pressed;
    private FirebaseAuth defaultAuth;
    private TextView forpass_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        defaultAuth=FirebaseAuth.getInstance();

        mDialog=new ProgressDialog(this);

        username = findViewById(R.id.username_et);
        password = findViewById(R.id.password_et);
        loginbtn= findViewById(R.id.loginButton);
        forpass_btn=findViewById(R.id.forgetpass_tv);


        forpass_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forPassCustomDialog();
            }
        });

        Spinner spinner = (Spinner) findViewById(R.id.logi_spinner);
        spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        List<String> categories = new ArrayList<String>();
        categories.add("Faculty");
        categories.add("Admin");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);


        loginbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                userid = username.getText().toString().trim();
                pass = password.getText().toString().trim();

                if(userid.isEmpty()){
                    username.setError("Please Enter Username");
                    return;
                }
                else if (pass.isEmpty()){
                    password.setError("Enter Password");
                    return;
                }

                mDialog.setTitle("Authenticating...");
                mDialog.setMessage(userid);
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.show();

                checklogin();

            }
        });

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        item =parent.getItemAtPosition(position).toString().trim();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void checklogin(){

        String mEmail,mPassword;
        mEmail=userid+"@iiitnr.edu.in";
        mPassword=pass;

        defaultAuth.signInWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                   // checkEmailVerification();
                    verifyusercredential();
                } else {
                    mDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Login Failed!!..", Toast.LENGTH_LONG).show();
                    defaultAuth.signOut();

                }
            }

        });








    }


    public void verifyusercredential(){

        mreference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference dbuser = mreference.child(item).child(userid);

        dbuser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    String  dbpassword;
                    if (item == "Admin") {
                        dbpassword = dataSnapshot.child("aid").getValue(String.class);
                        verify(dbpassword);

                    }

                    else if (item == "Faculty") {
                        dbpassword = dataSnapshot.child("tid").getValue(String.class);
                        verify(dbpassword);
                    }
                }
                catch (Exception e)
                {
                    Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    defaultAuth.signOut();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }


    public void verify(String dbpassword){

        if (item == "Faculty" && userid.equals(dbpassword)) {

            mDialog.dismiss();
            //startActivity(new Intent(this,facultydashboard.class));
            Intent intent =new Intent(this,facultydashboard.class);
            intent.putExtra("tid",userid);
            startActivity(intent);
            finish();

        }
        else if (item == "Admin" && userid.equals(dbpassword)) {
            mDialog.dismiss();
            Intent intent1 =new Intent(this,admin_mainpage.class);
            intent1.putExtra("tid",userid);
            startActivity(intent1);
            finish();
        }
        else if(! userid.equals(dbpassword)){
            defaultAuth.signOut();
            Toast.makeText(getApplicationContext(),"UserId/Password is Incorrect or you aren't "+item, Toast.LENGTH_LONG).show();
            mDialog.dismiss();

        }


        /*

        private void checkEmailVerification(){

            FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
            boolean emailflag=firebaseUser.isEmailVerified();

            if(emailflag){

                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                finish();

            }
            else
            {
                Toast.makeText(getApplicationContext(), "Please verify your Email", Toast.LENGTH_LONG).show();
                defaultAuth.signOut();
            }
        }

        */

        //end of emailverification

    }

    private void forPassCustomDialog()
    {
        final AlertDialog.Builder myDialog =new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater =LayoutInflater.from(MainActivity.this);

        final View myview=inflater.inflate(R.layout.forgot_pass,null);

        final AlertDialog dialog=myDialog.create();
        dialog.setView(myview);

        final EditText forpassEmail=myview.findViewById(R.id.forpassemail_et);
        Button resetpass_btn=myview.findViewById(R.id.forpass_bt);

        resetpass_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail=forpassEmail.getText().toString().trim();
                if(TextUtils.isEmpty(mEmail))
                {
                    forpassEmail.setError("Please Enter Valid Email..!");
                    return;
                }

                mDialog.setMessage("Sending Reset Link...");
                mDialog.show();
                defaultAuth.sendPasswordResetEmail(mEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            dialog.dismiss();
                            mDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Reset link sent to your Email..!", Toast.LENGTH_LONG).show();

                        }
                        else
                        {
                            mDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Error...in sending Password Reset Link", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        dialog.show();
    }

}
