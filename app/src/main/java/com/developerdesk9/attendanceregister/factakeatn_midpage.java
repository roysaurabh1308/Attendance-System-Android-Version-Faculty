package com.developerdesk9.attendanceregister;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class factakeatn_midpage extends AppCompatActivity {
    String tid;
    TextView dateet;
    DatePickerDialog datePickerDialog;
    String time;
    String datetimestamp;
    String finaldatetime;
    Button attenmid;

    Spinner spinnerfacsublist;
    String itemfacsublist;
    Spinner spinnerattvalue;
    String itemattenvalue;


    DatabaseReference facsubjectdetails;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_factakeatn_midpage);

        Intent intent=getIntent();
        tid=intent.getStringExtra("tid");

        facsubjectdetails= FirebaseDatabase.getInstance().getReference("FacultySubjectDetails");
        facsubjectdetails.keepSynced(true);


        attenmid=findViewById(R.id.takeatenmid_btn);
        spinnerfacsublist=findViewById(R.id.spinnerselsub);
        spinnerattvalue=findViewById(R.id.spinnerattenvalue);

        dateet=findViewById(R.id.date_et);

        dateet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // calender class's instance and get current date , month and year from calender
                final Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR); // current year
                int mMonth = c.get(Calendar.MONTH); // current month
                int mDay = c.get(Calendar.DAY_OF_MONTH); // current day
                // date picker dialog
                datePickerDialog = new DatePickerDialog(factakeatn_midpage.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // set day of month , month and year value in the edit text
                                datetimestamp=(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                dateet.setText(datetimestamp);


                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        Calendar calendar=Calendar.getInstance();
        SimpleDateFormat format=new SimpleDateFormat("hh-mm aa");
        time=format.format(calendar.getTime());


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

        //attendance value spinner array adapter
        final List<String> lstaddenval=new ArrayList<String>();
        lstaddenval.add("Select Attendance Value");
        lstaddenval.add("1");
        lstaddenval.add("2");
        lstaddenval.add("3");
        lstaddenval.add("4");
        ArrayAdapter<String> attenvaluesubarrayadapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,lstaddenval);
        attenvaluesubarrayadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerattvalue.setAdapter(attenvaluesubarrayadapter);

        spinnerattvalue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                itemattenvalue=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        attenmid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timestamppre;

                timestamppre=(datetimestamp+" ("+time+")");
                finaldatetime=timestamppre.toUpperCase();
                calltonextpage();

            }
        });


    }

    public void calltonextpage(){
        if (itemfacsublist=="Select Subject"){
            Toast.makeText(getApplicationContext(),"Please Select Valid Subject",Toast.LENGTH_SHORT).show();
        }
        else if (datetimestamp==null){
            dateet.setError("Please Select Date");
        }
        else if (itemattenvalue=="Select Attendance Value"){
            Toast.makeText(getApplicationContext(),"Please Select Attendance Value",Toast.LENGTH_SHORT).show();

        }
        else {
            Intent intent=new Intent(getApplicationContext(),takeattendancefinalbyfaculty.class);
            intent.putExtra("subid",itemfacsublist);
            intent.putExtra("timestamp",finaldatetime);
            intent.putExtra("attenvalue",itemattenvalue);
            startActivity(intent);
            finish();
        }
    }
}
