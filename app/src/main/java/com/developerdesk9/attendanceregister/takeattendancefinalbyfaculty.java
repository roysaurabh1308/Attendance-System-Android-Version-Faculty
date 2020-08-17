package com.developerdesk9.attendanceregister;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class takeattendancefinalbyfaculty extends AppCompatActivity {

    Toolbar toolbar;
    String subid;
    String timestamp;
    String attenvalue;

    ArrayList<String> selectedItems;
    ArrayList<String> nonselectedItems;

    ArrayList<String> ul;
    ListView listView;
    private ArrayAdapter adapter;
    ArrayList Userlist = new ArrayList<>();
    ArrayList Usernames = new ArrayList<>();

    DatabaseReference AttendenRecordSheet;
    DatabaseReference stdSubdetail;

    Button submitbtn;
    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takeattendancefinalbyfaculty);




        mDialog=new ProgressDialog(this);
        submitbtn=findViewById(R.id.submitatten_btn);

        Intent intent=getIntent();
        subid=intent.getStringExtra("subid");
        timestamp=intent.getStringExtra("timestamp");
        attenvalue=intent.getStringExtra("attenvalue");

        toolbar=findViewById(R.id.final_attendance_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Attendance :"+timestamp);


        selectedItems = new ArrayList<String>();

        AttendenRecordSheet= FirebaseDatabase.getInstance().getReference("AttendanceRecord");
        stdSubdetail=FirebaseDatabase.getInstance().getReference("StuEnSubDetails");
        stdSubdetail.keepSynced(true);

        stdSubdetail.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dsp :dataSnapshot.child(subid).getChildren()){
                    Userlist.add(dsp.getKey());
                    Usernames.add(dsp.child("sname").getValue(String.class));
                   // Toast.makeText(getApplicationContext(),ssubid+"...."+sname,Toast.LENGTH_SHORT).show();


                }
                OnStart(Userlist);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startuploadattendence();
                mDialog.setTitle("Submitting Attendance");
                mDialog.setMessage("Please wait..");
                mDialog.setCanceledOnTouchOutside(false);
                mDialog.show();
            }
        });

    }

    public void OnStart(ArrayList<String>userlist){
        nonselectedItems=userlist;
        ListView ch1=findViewById(R.id.checkable_list);
        ch1.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        ArrayAdapter<String> sidarrayadapter=new ArrayAdapter<String>(this,R.layout.checkable_list_layout,R.id.checkboxt,userlist);
        ch1.setAdapter(sidarrayadapter);

       /// ArrayList<String> namearrayadapter=new ArrayAdapter<String>(this,R.layout.checkable_list,R.id.check_name_tv,usernames);
       // ch1.setAdapter(namearrayadapter);

        ch1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = ((TextView) view).getText().toString();
                if (selectedItems.contains(selectedItem))
                    selectedItems.remove(selectedItem);
                else
                    selectedItems.add(selectedItem);

            }

        });
    }

    public void startuploadattendence(){

        for (String item :selectedItems){
            nonselectedItems.remove(item);
            Attendance attendance=new Attendance((attenvalue+"/"+attenvalue));
            AttendenRecordSheet.child(subid).child(timestamp).child(item).setValue(attendance);
        }

        for(String item :nonselectedItems){
            Attendance attendance=new Attendance(("0/"+attenvalue));
            AttendenRecordSheet.child(subid).child(timestamp).child(item).setValue(attendance);
        }
        mDialog.dismiss();

        Toast.makeText(this, "Attendance created successfully", Toast.LENGTH_LONG).show();
        startActivity(new Intent(getApplicationContext(),facultydashboard.class));
        finish();

    }


}
