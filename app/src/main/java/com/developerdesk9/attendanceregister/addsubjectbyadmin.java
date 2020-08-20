package com.developerdesk9.attendanceregister;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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


public class addsubjectbyadmin extends AppCompatActivity  {

    String item_batchname;
    Spinner spinnerfac,spinnerbatch;
    String itemfac;
    EditText subcodee;
    String subcode;
    DatabaseReference databaseFaculty;
    DatabaseReference facsubjectdetails;
    DatabaseReference dbbatchname;
    Button addsubbtnv;
    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addsubjectbyadmin);

        mDialog=new ProgressDialog(this);

        addsubbtnv=findViewById(R.id.adsubm_btn);
        subcodee=findViewById(R.id.subcode_et);
        spinnerfac=findViewById(R.id.choosefacspinner);
        spinnerbatch=findViewById(R.id.choosebatchspinner);

        databaseFaculty= FirebaseDatabase.getInstance().getReference("Faculty");
        facsubjectdetails =FirebaseDatabase.getInstance().getReference("FacultySubjectDetails");
        dbbatchname=FirebaseDatabase.getInstance().getReference("BatchName");


        //Spinner for batchname
        final List<String> lstbacthn=new ArrayList<String>();
        lstbacthn.add("Select Batch");

        ArrayAdapter<String> batchyarrayadapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,lstbacthn);
        batchyarrayadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerbatch.setAdapter(batchyarrayadapter);

        spinnerbatch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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


        //Spinner for faculty
        final List<String> lstfac=new ArrayList<String>();
        lstfac.add("Select Faculty");

        ArrayAdapter<String> facultyarrayadapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,lstfac);
        facultyarrayadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerfac.setAdapter(facultyarrayadapter);

        spinnerfac.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                itemfac=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        databaseFaculty.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dsp :dataSnapshot.getChildren()){
                    String name;
                    name=dsp.getKey().toLowerCase();
                    lstfac.add(name);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        addsubbtnv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addsubfn();
            }
        });


    }




    public void addsubfn(){

        subcode=subcodee.getText().toString().toUpperCase().trim();

        if (subcode.isEmpty()) {
            subcodee.setError("Enter Valid Subject Code");
            return;
        }
        else if (itemfac.equals("Select Faculty")){
            Toast.makeText(getApplicationContext(),"Select Faculty",Toast.LENGTH_SHORT).show();
            return;
        }
        else if (item_batchname.equals("Select Batch")){
            Toast.makeText(getApplicationContext(),"Select Batch",Toast.LENGTH_SHORT).show();
            return;
        }


        mDialog.setTitle("Adding..");
        mDialog.setMessage("Please wait..");
        mDialog.show();

        String subcodebatch;
        subcodebatch=(item_batchname+"-"+subcode);


        AddSubByAdmin addsub=new AddSubByAdmin(subcodebatch);

        facsubjectdetails.child(itemfac).child(subcodebatch).setValue(addsub).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){
                    mDialog.dismiss();
                    finish();
                    Toast.makeText(getApplicationContext(),"Subject Added Successfully",Toast.LENGTH_SHORT).show();
                }
                else {
                    mDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"oops...Failed",Toast.LENGTH_SHORT).show();
                }

            }
        });


    }





}
