package com.developerdesk9.attendanceregister;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class batchnamecreate extends AppCompatActivity {


    String batchnamestr;
    private EditText batchname;
    private Button adbatchnamebtn;
    private DatabaseReference dbbatchname;
    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batchnamecreate);

        mDialog=new ProgressDialog(this);

        batchname=findViewById(R.id.batchname_et);
        adbatchnamebtn=findViewById(R.id.adbatchname_btn);


        dbbatchname= FirebaseDatabase.getInstance().getReference("BatchName");

        adbatchnamebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                batchnamestr=batchname.getText().toString().toUpperCase().trim();
                if (batchnamestr.isEmpty()){
                    batchname.setError("Enter Valid Name");
                    return;
                }
                else if (batchnamestr.length()>10){
                    batchname.setError("Name should not greater than 10 char");
                    return;
                }
                else{
                    mDialog.setTitle("Adding..\n"+batchnamestr);
                    mDialog.setMessage("Please Wait..");
                    mDialog.show();
                    mDialog.setCanceledOnTouchOutside(false);
                    dbbatchname.child(batchnamestr).child("batch").setValue(batchnamestr).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                mDialog.dismiss();
                                finish();
                                Toast.makeText(getApplicationContext(),"Added Succesfully",Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(getApplicationContext(),"Failed..",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
}
