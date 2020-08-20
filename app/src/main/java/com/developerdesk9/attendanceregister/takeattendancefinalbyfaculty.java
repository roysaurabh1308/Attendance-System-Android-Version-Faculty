package com.developerdesk9.attendanceregister;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class takeattendancefinalbyfaculty extends AppCompatActivity {

    Toolbar toolbar;
    String subid;
    String timestamp;
    String attenvalue;
    boolean internetsts;

    int count1=0,count2=0,present=0;

    ArrayList<String> selectedItems;
    ArrayList<String> nonselectedItems;

    private ArrayAdapter adapter;
    ArrayList Userlist = new ArrayList<>();
    ArrayList Usernames = new ArrayList<>();

    DatabaseReference AttendenRecordSheet;
    DatabaseReference stdSubdetail;

    Button submitbtn;
    ProgressDialog mDialog;

    CircleImageView studentimagee;

    FirebaseStorage firebaseStorage;
    private static int PICK_IMAGE=123;
    Uri imagepath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_takeattendancefinalbyfaculty);


        //receiving data from previous activity
        Intent intent=getIntent();
        subid=intent.getStringExtra("subid");
        timestamp=intent.getStringExtra("timestamp");
        attenvalue=intent.getStringExtra("attenvalue");


        submitbtn=findViewById(R.id.submitatten_btn);


        toolbar=findViewById(R.id.view_toolbar);
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
                    count1=count1+1;

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

                internetsts=isNetworkAvailable();

                if (internetsts!=true){
                    AlertDialog.Builder builder= new AlertDialog.Builder(takeattendancefinalbyfaculty.this);
                    builder.setMessage("Internet is not available");
                    builder.setTitle("Alert !");
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    return;
                }
                else {
                    assurance();
                }

            }
        });

    }

    public void OnStart(ArrayList<String> userlist){
        nonselectedItems=userlist;
        final ListView ch1=findViewById(R.id.checkable_list);
        ch1.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        ArrayAdapter<String> sidarrayadapter=new ArrayAdapter<String>(this,R.layout.checkable_list_layout,R.id.checkboxt,userlist);
        ch1.setAdapter(sidarrayadapter);

        ch1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = ((TextView) view).getText().toString();
                if (selectedItems.contains(selectedItem))
                    selectedItems.remove(selectedItem);
                else
                    selectedItems.add(selectedItem);

            }

        });

        ch1.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = ((TextView) view).getText().toString();
                studentprofileCustomDialog(selectedItem);
                //Toast.makeText(getApplicationContext(),selectedItem,Toast.LENGTH_LONG).show();
                return false;
            }
        });
    }


    private void studentprofileCustomDialog(final String enroll)
    {
        final AlertDialog.Builder myDialog =new AlertDialog.Builder(takeattendancefinalbyfaculty.this);
        LayoutInflater inflater =LayoutInflater.from(takeattendancefinalbyfaculty.this);

        final View myview=inflater.inflate(R.layout.stuprofileatattendance,null);

        final AlertDialog dialog=myDialog.create();
        dialog.setView(myview);

        final TextView studentname=myview.findViewById(R.id.student_name_atten_et);
        final TextView studentenroll=myview.findViewById(R.id.student_enroll_atten_et);
        final Button okkbtn=myview.findViewById(R.id.atten_stu_ok_bt);
         studentimagee=myview.findViewWithTag(R.id.student_image_attenn);

         /*
        //naviagtion bar image
        firebaseStorage=FirebaseStorage.getInstance();
        StorageReference storageReference1=firebaseStorage.getReference("Student");
        storageReference1.child(enroll).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    Picasso.get().load(uri).fit().centerCrop().into(studentimagee);
                }catch (Exception e){}

            }
        });

        //end nav bar image


        */



        stdSubdetail.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name;
                name=dataSnapshot.child(subid).child(enroll).child("sname").getValue(String.class);
                studentname.setText(name);
                studentenroll.setText(enroll);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        okkbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void assurance(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation!");
        builder.setMessage("Do you want to Submit?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                startuploadattendence();

            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();  // Show the Alert Dialog box

    }

    public void startuploadattendence(){

        mDialog=new ProgressDialog(this);
        mDialog.setTitle("Submitting Attendance");
        mDialog.setMessage("Please wait..");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        for (String item :selectedItems){
            nonselectedItems.remove(item);
            Attendance attendance=new Attendance((attenvalue+"/"+attenvalue));
            AttendenRecordSheet.child(subid).child(timestamp).child(item).setValue(attendance);
            count2=count2+1;
            present=present+1;
        }

        for(String item :nonselectedItems){
            Attendance attendance=new Attendance(("0/"+attenvalue));
            AttendenRecordSheet.child(subid).child(timestamp).child(item).setValue(attendance);
            count2=count2+1;
        }

        mDialog.dismiss();

        if (count1==count2)
        {
            count1=count1-present;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Submitted Successfully!");
            builder.setMessage("Present="+present+"  Absent="+count1+" Total Student="+count2);
            builder.setCancelable(false);

            builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {

                    dialog.cancel();
                    finish();
                }
            });

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            count1=0;
            count2=0;

            //under if statement
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Submission Failed");
            builder.setMessage("Something went wrong !");
            builder.setCancelable(false);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alert!");
        builder.setMessage("Do you want to Exit?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

                finish();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

                dialog.cancel();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();  // Show the Alert Dialog box
        super.onBackPressed();
    }
}


