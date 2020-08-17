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
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class fac_create_new_attendenceshet extends AppCompatActivity {

    String tid;
    Spinner spinnerfacsublist;
    String itemfacsublist;
    Button createsheetbtn;
    String batchextract;

    DatabaseReference batchdetails;
    DatabaseReference mbatchreference;
    DatabaseReference facsubjectdetails;
    DatabaseReference studentenrolledsub;
    DatabaseReference student;
    ProgressDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fac_create_new_attendenceshet);

        mDialog=new ProgressDialog(this);

        Intent intent=getIntent();
        tid=intent.getStringExtra("ttid");

        facsubjectdetails= FirebaseDatabase.getInstance().getReference("FacultySubjectDetails");
        batchdetails=FirebaseDatabase.getInstance().getReference("Batchdetails");
        studentenrolledsub=FirebaseDatabase.getInstance().getReference("StuEnSubDetails");
        student=FirebaseDatabase.getInstance().getReference("Student");



        spinnerfacsublist=findViewById(R.id.spinnerfac_cshhet);
        createsheetbtn=findViewById(R.id.fac_csheetbtn);

        final List<String> lstfacsub=new ArrayList<String>();
        lstfacsub.add("Select Allotted Subject");

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


        createsheetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startextsid();
            }
        });


    }

    public void startextsid(){

        if (itemfacsublist=="Select Allotted Subject"){
            Toast.makeText(getApplicationContext(),"Please Select Subject",Toast.LENGTH_SHORT).show();
            return;
        }

        mDialog.setTitle("Creating Sheet...");
        mDialog.setMessage("Please wait..");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        batchextract=itemfacsublist.substring(0,7);

        batchdetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dsp :dataSnapshot.child(batchextract).getChildren()){
                    String sid,sname;
                    sid=dsp.getKey();
                    sname=dsp.child("sname").getValue(String.class);
                    startcreatesheet(sid,sname);


                }
                mDialog.dismiss();
                finish();
                Toast.makeText(getApplicationContext(),"Sheet Created Successfully.",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void startcreatesheet(String ssid,String sname){
        batchdetails batchdetailss=new batchdetails(ssid,sname);
        studentenrolledsub.child(itemfacsublist).child(ssid).setValue(batchdetailss);
        Subject subject=new Subject(itemfacsublist);
        student.child(ssid).child("Subject").child(itemfacsublist).setValue(subject);
        return;

    }

}
