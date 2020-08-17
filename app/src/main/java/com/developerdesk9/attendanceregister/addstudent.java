package com.developerdesk9.attendanceregister;

import android.app.ProgressDialog;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class addstudent extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    String item;
    EditText Sname;
    EditText Sid,spassword;
    String sname,sid,spass;
    Button addstdbtn;
    DatabaseReference databaseStudent;
    DatabaseReference batchdetails;
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

        Spinner spinner = (Spinner) findViewById(R.id.spinner1);
        spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        List<String> categories = new ArrayList<String>();
        categories.add("Select Batch");
        for(int i=2016;i<=2030;i++)
        {
            String a=String.valueOf(i);
            categories.add("CSE"+a);
            categories.add("ECE"+a);
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        addstdbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stdstartadd();
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

    public void stdstartadd(){

        sname = Sname.getText().toString().trim();
        sid = Sid.getText().toString().trim();
        spass = spassword.getText().toString().trim();
        String batch=item.toUpperCase().trim();

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
        else if(item=="Select Batch")
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
        batchdetails.child(item).child(sid).setValue(batchs);
        databaseStudent.child(sid).setValue(student).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    mDialog.dismiss();
                    finish();
                    Toast.makeText(getApplicationContext(),"Student Added Successfully", Toast.LENGTH_LONG).show();

                }
                else {
                    mDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Failed...", Toast.LENGTH_LONG).show();

                }
            }
        });




    }



}
