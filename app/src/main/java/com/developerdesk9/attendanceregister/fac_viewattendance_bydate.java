package com.developerdesk9.attendanceregister;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class fac_viewattendance_bydate extends AppCompatActivity {

    String subject,date;

    TextView subjectname;
    TextView datetv;
    ListView listViewbydate;
    ArrayList attendance= new ArrayList<>();
    DatabaseReference attendancerecord;

    ProgressDialog mDialog;

    String mrollno;

    int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fac_viewattendance_bydate);

        mDialog=new ProgressDialog(this);
        mDialog.setTitle("Loading...");
        mDialog.setMessage("Please wait...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        attendance.clear();//clearning arraylist

        listViewbydate=(ListView)findViewById(R.id.listviewbydate);
        subjectname=findViewById(R.id.subject);
        datetv=findViewById(R.id.date_tv);

        Intent intent=getIntent();
        subject=intent.getStringExtra("subid");
        date=intent.getStringExtra("date");
        subjectname.setText(subject);
        datetv.setText("Date/Time:"+date);

        //Toast.makeText(getApplicationContext(),subject+date,Toast.LENGTH_SHORT).show();

        attendancerecord= FirebaseDatabase.getInstance().getReference("AttendanceRecord");
        attendancerecord.keepSynced(true);

        attendance.add("Enrollment No"+"            "+"Attendance Value");
        attendancerecord.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dspp :dataSnapshot.child(subject).child(date).getChildren()){
                    String rollnumber,avalue;
                    rollnumber=dspp.getKey();
                    avalue=dspp.child("atvalue").getValue().toString();

                    attendance.add(rollnumber+"                             "+avalue);

                    count=count+1;
                }
                attendance.add("Total Submission=  "+count);
                listshow(attendance);//this is a function created by me
                mDialog.dismiss();
                count=0;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void listshow(ArrayList attendancelist){

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, attendancelist);
        listViewbydate.setAdapter(adapter);

        listViewbydate.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String dataupatevar=parent.getItemAtPosition(position).toString();
                updateattendence(dataupatevar);
                return false;
            }
        });
    }

    public void updateattendence(String dataupdate){

        mrollno=dataupdate.substring(0,11);
        mrollno=mrollno.trim();
        String atvalue=dataupdate.substring(14);
        atvalue=atvalue.trim();
        String atvaltrim=atvalue.substring(0,1);
        final String atvaluemax=atvalue.substring(2,3);


        final AlertDialog.Builder myDialog=new AlertDialog.Builder(fac_viewattendance_bydate.this);
        LayoutInflater inflater=LayoutInflater.from(fac_viewattendance_bydate.this);
        View mview=inflater.inflate(R.layout.upate_attandance,null);
        final AlertDialog dialog=myDialog.create();
        dialog.setView(mview);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        final TextView enrollmentno=mview.findViewById(R.id.rollno_update_tv);
        final TextView datetime=mview.findViewById(R.id.date_update_tv);
        final TextView subjecttv=mview.findViewById(R.id.subject_update_tv);
        final EditText atstatus=mview.findViewById(R.id.attendance_vale_update);
        Button updatebtn=mview.findViewById(R.id.update_btn);
        Button cancelbtn=mview.findViewById(R.id.cancel_btn);

        enrollmentno.setText(mrollno.trim());
        datetime.setText(date);
        subjecttv.setText(subject);
        atstatus.setText(atvaltrim);

        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String getatvalue=atstatus.getText().toString();
                String updates;
                updates=(getatvalue+"/"+atvaluemax);
                if(getatvalue.isEmpty()){
                    atstatus.setError("Can't leave Empty");
                    return;
                }
                else if((Integer.valueOf(getatvalue))>(Integer.valueOf(atvaluemax))){
                    Toast.makeText(getApplicationContext(),"Value can't be more than "+atvaluemax,Toast.LENGTH_SHORT).show();
                }
                else {

                    mDialog.show();
                    dialog.dismiss();
                    updatte(updates);
                }

            }
        });



    }

    public void updatte(String atval){

        attendancerecord.child(subject).child(date).child(mrollno).child("atvalue").setValue(atval).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mDialog.dismiss();
                    finish();
                    startActivity(getIntent());
                    Toast.makeText(getApplicationContext(),"Updated Successfully",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
