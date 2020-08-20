package com.developerdesk9.attendanceregister;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class addstudent extends AppCompatActivity {

    String item_batchname;
    EditText Sname;
    EditText Sid,spassword;
    String sname,sid,spass;
    Button addstdbtn;
    DatabaseReference databaseStudent;
    DatabaseReference batchdetails;
    DatabaseReference dbbatchname;
    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addstudent);

        Sname = findViewById(R.id.stdname_et);
        Sid =  findViewById(R.id.enrollnemtno_et);
        spassword =findViewById(R.id.stdpassword_et);
        addstdbtn=findViewById(R.id.addstudentmainpage_btn);

        mDialog=new ProgressDialog(this);

        databaseStudent = FirebaseDatabase.getInstance().getReference("Student");
        batchdetails=FirebaseDatabase.getInstance().getReference("Batchdetails");
        dbbatchname=FirebaseDatabase.getInstance().getReference("BatchName");

        Spinner spinner = (Spinner) findViewById(R.id.spinner1);


        //Spinner for batchname
        final List<String> lstbacthn=new ArrayList<String>();
        lstbacthn.add("Select Batch");

        ArrayAdapter<String> facultyarrayadapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,lstbacthn);
        facultyarrayadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(facultyarrayadapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                item_batchname=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dbbatchname.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dsp :dataSnapshot.getChildren()){
                    String name;
                    name=dsp.getKey();
                    lstbacthn.add(name);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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

        sname = Sname.getText().toString().trim();
        sid = Sid.getText().toString().trim();
        spass = spassword.getText().toString().trim();
        String batch=item_batchname.toUpperCase().trim();

        if(sname.isEmpty()){
            Sname.setError("Enter Name");
            return;
        }
        else if(sid.isEmpty()){
            Sid.setError("Enter Enrollment Number");
            return;
        }

        else if(spass.isEmpty()){
            spassword.setError("Enter Password");
            return;
        }
        else if(item_batchname=="Select Batch")
        {
            Toast.makeText(getApplicationContext(),"Please Select Batch",Toast.LENGTH_SHORT).show();
            return;
        }

        mDialog.setTitle("Registering");
        mDialog.setMessage("Please wait");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        Student student=new Student(sname,sid,spass,batch);
        batchdetails batchs=new batchdetails(sid,sname);
        batchdetails.child(item_batchname).child(sid).setValue(batchs);
        databaseStudent.child(sid).setValue(student).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    mDialog.dismiss();
                    startActivity(getIntent());
                    Toast.makeText(getApplicationContext(),"Student Added Successfully", Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(),"Add another Student", Toast.LENGTH_LONG).show();

                }
                else {
                    mDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Failed...", Toast.LENGTH_LONG).show();

                }
            }
        });




    }



}
