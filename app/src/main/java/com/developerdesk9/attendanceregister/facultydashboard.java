package com.developerdesk9.attendanceregister;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class facultydashboard extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    String tid;
    ImageButton factakeatntopage;
    ImageButton facviewattenbtn;
    private TextView nav_name;
    private TextView nav_username;
    DatabaseReference facultyrec;
    private FirebaseAuth defaultAuth;
    View headerViw;
    ProgressDialog mDialog;
    public TextView date_time,notify;
    private static final String FILE_NAME = "state.txt";

    CircleImageView fac_nav_image;
    FirebaseStorage firebaseStorage;
    private static int PICK_IMAGE=123;
    Uri imagepath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facultydashboard);

        Intent intent=getIntent();
        tid=intent.getStringExtra("tid");
        mDialog=new ProgressDialog(this);

        firebaseStorage=FirebaseStorage.getInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        factakeatntopage=findViewById(R.id.fac_take_atten_to_page_btn);
        facviewattenbtn=findViewById(R.id.fac_view_atten_dashboardpage_btn);

        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy hh:mm aa");
        String formattedDate = df.format(c.getTime());
        // formattedDate have current date/time
        date_time =findViewById(R.id.date_time);
        notify=findViewById(R.id.notify);
        date_time.setText(formattedDate);


        String notif="Hello, Dr. "+(tid.toUpperCase())+" welcome to Attendance Register.";
        notify.setText(notif);



        //nav_drawer variable

       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

       */

        facultyrec= FirebaseDatabase.getInstance().getReference("Faculty");
        facultyrec.keepSynced(true);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        headerViw=navigationView.getHeaderView(0);
        nav_name=(TextView)headerViw.findViewById(R.id.nav_name_fac);
        nav_username=(TextView)headerViw.findViewById(R.id.nav_username_fac);
        fac_nav_image=(CircleImageView)headerViw.findViewById(R.id.nav_fac_imageView);
        navigationView.setNavigationItemSelectedListener(this);

        facultyrec.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String musernam,mname;
                mname=dataSnapshot.child(tid).child("tname").getValue(String.class);
                nav_name.setText(mname);

                musernam=dataSnapshot.child(tid).child("tid").getValue(String.class);
                musernam=musernam+"@gmail.com";
                nav_username.setText(musernam);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        factakeatntopage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(getApplicationContext(),factakeatn_midpage.class);
                intent1.putExtra("tid",tid);
                startActivity(intent1);
            }
        });

        facviewattenbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2=new Intent(getApplicationContext(),facview_atten_mid1.class);
                intent2.putExtra("tid",tid);
                startActivity(intent2);
            }
        });

        fac_nav_image.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent=new Intent(getApplicationContext(),facultyprofile.class);
                intent.putExtra("tid",tid);
                startActivity(intent);
                return false;
            }
        });

        //naviagtion bar image

        StorageReference storageReference1=firebaseStorage.getReference("Faculty");
        storageReference1.child(tid).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    Picasso.get().load(uri).fit().centerCrop().into(fac_nav_image);
                }catch (Exception e){}

            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Alert!");
            builder.setMessage("Do you want to Exit?");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which)
                {

                    finish();
                    ActivityCompat.finishAffinity(facultydashboard.this);
                    System.exit(0);
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.facultydashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplicationContext(),about_app.class));
            return true;
        }
        else if(id == R.id.logout_title_facdash_btn) {
            signout("3999");
            Intent logout=new Intent(getApplicationContext(),MainActivity.class);
            logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(logout);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //if (id == R.id.nav_facprofile) {
            // Handle the camera action
        //}
        if (id == R.id.nav_fac_del_attendence_sheet) {

        }
        else if (id == R.id.nav_admin_profile) {
            Intent intent=new Intent(this,facultyprofile.class);
            intent.putExtra("tid",tid);
            startActivity(intent);
        }
        else if (id == R.id.nav_fac_backstdreg_btn) {
            Intent intent=new Intent(this,backlogstdregisteration_tosubject.class);
            intent.putExtra("ttid",tid);
            startActivity(intent);
        }
        else if (id == R.id.nav_fac_chnagepass) {

            forPassCustomDialog();

        } else if (id == R.id.nav_faclogout) {
            signout("3999");
           Intent logout=new Intent(getApplicationContext(),MainActivity.class);
           logout.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
           startActivity(logout);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void forPassCustomDialog()
    {
        final AlertDialog.Builder myDialog =new AlertDialog.Builder(facultydashboard.this);
        LayoutInflater inflater =LayoutInflater.from(facultydashboard.this);

        final View myview=inflater.inflate(R.layout.forgot_pass,null);

        final AlertDialog dialog=myDialog.create();
        dialog.setView(myview);

        final EditText forpassEmail=myview.findViewById(R.id.forpassemail_et);
        Button resetpass_btn=myview.findViewById(R.id.forpass_bt);

        resetpass_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mEmail=forpassEmail.getText().toString().trim();
                if(TextUtils.isEmpty(mEmail))
                {
                    forpassEmail.setError("Please Enter Valid Email..!");
                    return;
                }

                mDialog.setMessage("Sending Reset Link...");
                mDialog.show();
                defaultAuth.sendPasswordResetEmail(mEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            dialog.dismiss();
                            mDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Reset link sent to your Email..!", Toast.LENGTH_LONG).show();

                        }
                        else
                        {
                            mDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Error...in sending Password Reset Link", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        dialog.show();
    }

    public void signout(String statevar){

        FileOutputStream fos = null;

        try {
            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
            fos.write(statevar.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }



}
