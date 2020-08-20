package com.developerdesk9.attendanceregister;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class collective_batch_creation_mid1 extends AppCompatActivity {

    String item;
    Spinner spinnerbatch;
    EditText firstenno;
    EditText lstenno,spassword;
    String firstenno_vr,lstenno_vr,spass;
    Button addstdbtn;
    DatabaseReference databaseStudent;
    DatabaseReference batchdetails;
    ProgressDialog mDialog;
    int i=0,j=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collective_batch_creation_mid1);

        firstenno = findViewById(R.id.initial_enno_et);
        lstenno =  findViewById(R.id.elast_enno_et);
        spassword =findViewById(R.id.std_default_pass_et);
        addstdbtn=findViewById(R.id.start_create_batch_btn);
        spinnerbatch=findViewById(R.id.spinner_batch);

        mDialog=new ProgressDialog(this);

        databaseStudent = FirebaseDatabase.getInstance().getReference("Student");
        batchdetails=FirebaseDatabase.getInstance().getReference("Batchdetails");

        List<String> lstbatch=new ArrayList<String>();
        lstbatch.add("Select Batch");
        for(int i=2016;i<=2030;i++)
        {
            String a=String.valueOf(i);
            lstbatch.add("CSE"+a);
            lstbatch.add("ECE"+a);
        }

        ArrayAdapter<String> batcharrayadapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,lstbatch);
        batcharrayadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerbatch.setAdapter(batcharrayadapter);

        spinnerbatch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                item=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        addstdbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stdstartadd();
            }
        });



    }

    public void stdstartadd(){

        firstenno_vr = firstenno.getText().toString().trim();
        lstenno_vr= lstenno.getText().toString().trim();
        spass = spassword.getText().toString().trim();
        String batch=item.toUpperCase().trim();

        if(firstenno_vr.isEmpty()){
            firstenno.setError("Enter starting Enrollment no");
            return;
        }
        else if(lstenno_vr.isEmpty()){
            lstenno.setError("Enter last Enrollment no");
            return;
        }

        else if(spass.isEmpty()){
            spassword.setError("Enter default Password");
            return;
        }
        else if(spass.length()<6){
            spassword.setError("Password should be of minimum six digit");
            return;
        }
        else if(item=="Select Batch")
        {
            Toast.makeText(getApplicationContext(),"Please Select Batch",Toast.LENGTH_SHORT).show();
            return;
        }

        mDialog.setTitle("Registering");
        mDialog.setMessage("Please wait");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

          i=Integer.valueOf(firstenno_vr);
          j=Integer.valueOf(lstenno_vr);

        for ( ;i<=j ;i++){

            Student student=new Student("Name",String.valueOf(i),spass,item);
            batchdetails batchs=new batchdetails(String.valueOf(i),"Name");
            batchdetails.child(item).child(String.valueOf(i)).setValue(batchs);
            databaseStudent.child(String.valueOf(i)).setValue(student).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        mDialog.setMessage("Enrollment Number= "+String.valueOf(i));
                    }
                    else {
                        mDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Failed...", Toast.LENGTH_LONG).show();

                    }
                }
            });

        }

        mDialog.dismiss();
        Toast.makeText(getApplicationContext(),"Batch Added Successfully", Toast.LENGTH_LONG).show();
        finish();

    }




}
