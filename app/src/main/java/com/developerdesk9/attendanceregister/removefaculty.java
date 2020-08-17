package com.developerdesk9.attendanceregister;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class removefaculty extends AppCompatActivity {

    EditText Tid;

    String tid;
    Button delBtn;
    DatabaseReference databaseFaculty;

    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_removefaculty);

        Tid = findViewById(R.id.del_facemail_et);
        delBtn= findViewById(R.id.fac_delete_btn);

        mProgress=new ProgressDialog(this);

        databaseFaculty= FirebaseDatabase.getInstance().getReference("Faculty");

        delBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delstart();
            }
        });
    }

    public void delstart(){

        tid = Tid.getText().toString().toLowerCase().trim();
        if (tid.isEmpty()){
            Tid.setError("Enter Valid Username");
            return;
        }

        mProgress.setTitle("Deleting Account");
        mProgress.setMessage("Please wait...");
        mProgress.setCanceledOnTouchOutside(false);
        mProgress.show();

        databaseFaculty.child(tid).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    mProgress.dismiss();
                    finish();
                    Toast.makeText(getApplicationContext(),"Deleted Successfully..",Toast.LENGTH_SHORT).show();
                }
                else {
                    mProgress.dismiss();
                    Toast.makeText(getApplicationContext(),"Oops...Failed.",Toast.LENGTH_SHORT).show();

                }
            }
        });

    }
}
