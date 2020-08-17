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

public class backlogstdregisteration_tosubject extends AppCompatActivity {
    String tid;
    Spinner spinnerfacsublist;
    String itemfacsublist;

    EditText Sname;
    EditText Sid;
    String sname,sid;
    Button bckaddstdbtn;

    DatabaseReference facsubjectdetails;
    DatabaseReference studentenrolledsub;
    DatabaseReference student;

    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backlogstdregisteration_tosubject);

        mDialog=new ProgressDialog(this);

        Intent intent=getIntent();
        tid=intent.getStringExtra("ttid");

        spinnerfacsublist=findViewById(R.id.back_spinner1);
        Sname=findViewById(R.id.back_stdname_et);
        Sid=findViewById(R.id.back_enrollnemtno_et);
        bckaddstdbtn=findViewById(R.id.backstd_mainpage_btn);

        facsubjectdetails= FirebaseDatabase.getInstance().getReference("FacultySubjectDetails");
        studentenrolledsub=FirebaseDatabase.getInstance().getReference("StuEnSubDetails");
        student=FirebaseDatabase.getInstance().getReference("Student");

        final List<String> lstfacsub=new ArrayList<String>();
        lstfacsub.add("Select Subject");

        ArrayAdapter<String> facultysubarrayadapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,lstfacsub);
        facultysubarrayadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerfacsublist.setAdapter(facultysubarrayadapter);

        spinnerfacsublist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                itemfacsublist=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        facsubjectdetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dsp :dataSnapshot.child(tid).getChildren()){
                    String name;
                    name=dsp.getKey();
                    lstfacsub.add(name);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        bckaddstdbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addbackstd();
            }
        });



    }


    public void addbackstd(){
        sname=Sname.getText().toString().trim();
        sid=Sid.getText().toString().trim();
        if (sname.isEmpty()){
            Sname.setError("Please Enter Valid Name");
            return;
        }
        else if (sid.isEmpty()){
            Sid.setError("Please Enter Enrollment Number");
            return;
        }
        else if (itemfacsublist=="Select Subject"){
            Toast.makeText(getApplicationContext(),"Please Select Sublect",Toast.LENGTH_SHORT).show();
            return;
        }

        mDialog.setTitle("Please Wait...");
        mDialog.setMessage(sname+" is adding");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        batchdetails batchdetailss=new batchdetails(sid,sname);
        studentenrolledsub.child(itemfacsublist).child(sid).setValue(batchdetailss).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    Subject subject=new Subject(itemfacsublist);
                    student.child(sid).child("Subject").child(itemfacsublist).setValue(subject);
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
