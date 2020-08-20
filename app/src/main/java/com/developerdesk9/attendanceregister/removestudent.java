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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class removestudent extends AppCompatActivity{

    String item_batchname;
    EditText Sid;
    String sid;
    Button delstdbtn;
    DatabaseReference databaseStudent;
    DatabaseReference batchdetails;
    DatabaseReference dbbatchname;
    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_removestudent);


        Sid =  findViewById(R.id.del_stdenrollno_et);
        delstdbtn=findViewById(R.id.std_delete_btn);

        mDialog=new ProgressDialog(this);

        databaseStudent = FirebaseDatabase.getInstance().getReference("Student");
        dbbatchname=FirebaseDatabase.getInstance().getReference("BatchName");
        //will be used later while updating things currently unused
        batchdetails=FirebaseDatabase.getInstance().getReference("Batchdetails");


        Spinner spinner = (Spinner) findViewById(R.id.spinner2);
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


        delstdbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removestd();
            }
        });
    }


    public void removestd(){

        sid = Sid.getText().toString().trim();

        if(sid.isEmpty()){
            Sid.setError("Enter Enrollment Number");
            return;
        }
        else if(item_batchname=="Select Batch")
        {
            Toast.makeText(getApplicationContext(),"Please Select Batch",Toast.LENGTH_SHORT).show();
            return;
        }

        mDialog.setTitle("Deleting Account");
        mDialog.setMessage("Please wait...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        databaseStudent.child(sid).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    batchdetails.child(item_batchname).child(sid).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                mDialog.dismiss();
                                finish();
                                Toast.makeText(getApplicationContext(),"Deleted Successfully..",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
                else {
                    mDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Oops...Failed.",Toast.LENGTH_SHORT).show();

                }
            }
        });

    }
}
