package com.developerdesk9.attendanceregister;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class fac_attenview_mid2 extends AppCompatActivity {

    String subid;
    int tpresent = 0;
    int tattencount = 0;

    Toolbar hometoolbar;

    DatabaseReference stuensbdetails;
    DatabaseReference attenrecord;
    ProgressDialog mDialog;

    //variable deceleration
    ArrayList<Details> data = new ArrayList<>();
    RecyclerView recyclerView;
    RAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fac_attenview_mid2);


        Intent intent = getIntent();
        subid = intent.getStringExtra("subid");

        hometoolbar=findViewById(R.id.view_attencollec_toolbar);
        setSupportActionBar(hometoolbar);
        getSupportActionBar().setTitle(subid);



        //implementation of recycler view

        recyclerView = findViewById(R.id.recycler_collective_attendance_view);
        RecyclerView.LayoutManager layoutManager= new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RAdapter(data, getApplicationContext());

        stuensbdetails = FirebaseDatabase.getInstance().getReference("StuEnSubDetails").child(subid);
        attenrecord = FirebaseDatabase.getInstance().getReference("AttendanceRecord").child(subid);
        stuensbdetails.keepSynced(true);
        attenrecord.keepSynced(true);


        recyclerView.setAdapter(adapter);


        //ending of recycler view


        mDialog = new ProgressDialog(this);
        mDialog.setTitle("Please Wait");
        mDialog.setMessage("Data is loading...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();




        //start quaring with student step 1 getting name and sid from here
        stuensbdetails.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    String id, name;
                    id = dsp.getKey();
                    name = dsp.child("sname").getValue(String.class);
                    gotoattendencedatabase(id, name);
                    adapter.notifyDataSetChanged();


                }
                mDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    public void gotoattendencedatabase(final String id, final String name) {

        attenrecord.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    String aval;
                    aval = dsp.child(id).child("atvalue").getValue(String.class);
                    //Toast.makeText(getApplicationContext(),name+"--"+aval,Toast.LENGTH_SHORT).show();
                    try {
                        tpresent += Integer.valueOf(aval.substring(0, 1));
                        tattencount += Integer.valueOf(aval.substring(2, 3));
                    }
                    catch (Exception e){

                        AlertDialog.Builder builder = new AlertDialog.Builder(fac_attenview_mid2.this);
                        builder.setTitle("Alert !");
                        builder.setMessage("Something went wrong please contact Admin");
                        builder.setCancelable(false);
                        builder.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {

                                dialog.cancel();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                    }


                }
                int abs = tattencount - tpresent;
                float percent;
                percent = (((float) tpresent) / ((float) tattencount)) * 100;
                percent=Math.round(percent*1000)/1000;

                String attendence=(String.valueOf(tpresent)+"/"+String.valueOf(tattencount)) ;
                String absent=String.valueOf(abs);
                String percentage=String.valueOf(percent);

                Details details=new Details(id,name,attendence,absent,percentage);
                data.add(details);
                adapter.notifyDataSetChanged();
                tpresent = 0;
                tattencount = 0;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    class RAdapter extends RecyclerView.Adapter<RAdapter.RViewHolder>{
        ArrayList<Details> details;
        Context context;
        @NonNull
        @Override
        public RViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v  = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fac_view_attendance_item_data_mid2, viewGroup, false);
            RViewHolder holder = new RViewHolder(v);
            return  holder;
        }

        @Override
        public void onBindViewHolder(@NonNull RViewHolder v, int i) {
            View view = v.itemView;
            String percent = details.get(i).percent;
            if(Float.valueOf(percent)<=75){
                v.percenttv.setTextColor(Color.RED);
            }
            else if(Float.valueOf(percent)>75 && Float.valueOf(percent)<85 ){
                v.percenttv.setTextColor(Color.BLUE);
            }
            else if(Float.valueOf(percent)>=85){
                v.percenttv.setTextColor(Color.MAGENTA);
            }
            v.enrollmenttv.setText(details.get(i).getEnrollment());
            v.nametv.setText(details.get(i).getName());
            v.absenttv.setText(details.get(i).getAbsent());
            v.percenttv.setText(percent);
            v.attendancetv.setText(details.get(i).getAttendance());
        }

        @Override
        public int getItemCount() {
            return details.size();
        }

        RAdapter(ArrayList<Details> details, Context context){
            this.details = details;
            this.context  = context;
        }
        class RViewHolder extends RecyclerView.ViewHolder{
            TextView enrollmenttv;
            TextView nametv;
            TextView attendancetv;
            TextView absenttv;
            TextView percenttv;
            public RViewHolder(@NonNull View v) {
                super(v);
                enrollmenttv = v.findViewById(R.id.enno_input_tv);
                nametv = v.findViewById(R.id.name_input_tv);
                attendancetv = v.findViewById(R.id.present_input_tv);
                absenttv = v.findViewById(R.id.absent_input_tv);
                percenttv = v.findViewById(R.id.percentage_input_tv);
            }
        }
    }


}




