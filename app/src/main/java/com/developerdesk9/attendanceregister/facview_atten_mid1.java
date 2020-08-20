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

public class facview_atten_mid1 extends AppCompatActivity {

    String tid;
    Spinner spinnerfacsublist;
    String itemfacsublist;
    Button viewattenproceedbtn;

    Spinner spinnerdate;
    String itemdate;
    Button viewdateproceedbtn;
    List<String> lstfacdate=new ArrayList<String>();
    List<String> lstfacsub=new ArrayList<String>();

    DatabaseReference facsubjectdetails;
    DatabaseReference attendencerec;
    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facview_atten_mid1);

        Intent intent=getIntent();
        tid=intent.getStringExtra("tid");

        facsubjectdetails= FirebaseDatabase.getInstance().getReference("FacultySubjectDetails");
        attendencerec=FirebaseDatabase.getInstance().getReference("AttendanceRecord");

        mDialog=new ProgressDialog(this);
        mDialog.setTitle("Loading...");
        mDialog.setMessage("Please wait...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        spinnerfacsublist=findViewById(R.id.spinner_viewatten_facsub);
        viewattenproceedbtn=findViewById(R.id.fac_viewatten_proceed_btn);

        spinnerdate=findViewById(R.id.spinner_viewa_selectdate_facsub);
        viewdateproceedbtn=findViewById(R.id.fac_viewatten_bydate_proceed_btn);


        lstfacsub.add("Select Subject");
        ArrayAdapter<String> facultysubarrayadapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,lstfacsub);
        facultysubarrayadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerfacsublist.setAdapter(facultysubarrayadapter);

        spinnerfacsublist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                itemfacsublist=parent.getItemAtPosition(position).toString();
                dateselect(itemfacsublist);
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

                mDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        viewattenproceedbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceeding();
            }
        });

        //spinner date


        viewdateproceedbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                proceedwithdate();
            }
        });

    }

    public void proceeding(){
        if (itemfacsublist=="Select Subject"){
            Toast.makeText(getApplicationContext(),"Please Select Subject",Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            Intent intent2=new Intent(getApplicationContext(),fac_attenview_mid2.class);
            intent2.putExtra("subid",itemfacsublist);
            startActivity(intent2);
        }
    }

    public void proceedwithdate(){

        if (itemfacsublist=="Select Subject"){
            Toast.makeText(getApplicationContext(),"Please Select Subject",Toast.LENGTH_SHORT).show();
            return;
        }

        else if(itemdate=="Select date"){
            Toast.makeText(getApplicationContext(),"Select Date",Toast.LENGTH_SHORT).show();
            return;
        }

        else {
            Intent intent3=new Intent(getApplicationContext(),fac_viewattendance_bydate.class);
            intent3.putExtra("subid",itemfacsublist);
            intent3.putExtra("date",itemdate);
            startActivity(intent3);
        }

    }

    public void dateselect(final String faclst){
        mDialog.show();
        lstfacdate.clear();
        lstfacdate.add("Select date");
        attendencerec.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dspp :dataSnapshot.child(faclst).getChildren()){
                    String datetime;
                    datetime=dspp.getKey();
                    lstfacdate.add(datetime);

                }
                mDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        ArrayAdapter<String> facultydaterrayadapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,lstfacdate);
        facultydaterrayadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerdate.setAdapter(facultydaterrayadapter);

        spinnerdate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                itemdate=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
