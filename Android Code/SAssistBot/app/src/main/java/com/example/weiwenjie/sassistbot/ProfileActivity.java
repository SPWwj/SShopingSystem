package com.example.weiwenjie.sassistbot;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;


public class ProfileActivity extends AppCompatActivity {

    private static final int CHOOSE_IMAGE = 101;//can be any code

    TextView textView;//veri
    ImageView imageView;
    TextView tvName;
    TextView tvBalance;
    EditText etTopUp;

    ProgressBar progressBar;


    FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;

    private static DecimalFormat df2 = new DecimalFormat(".##");
    static ProfileActivity pa;
    private double Bal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        pa=this;

        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etTopUp=findViewById(R.id.etTopUp);
        tvBalance=findViewById(R.id.tvBalance);
        myRef=database.getReference("Balance");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Bal = dataSnapshot.getValue(Double.class);
                tvBalance.setText("$"+df2.format(Bal));

            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(ProfileActivity.this,"Fail to read to Balance",Toast.LENGTH_SHORT).show();
            }
        });


        tvName = findViewById(R.id.tvName);
        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressbar);
        textView = findViewById(R.id.textViewVerified);

    }



    @Override
    protected void onStart() {
        super.onStart();
        loadUserInformation();
        if (mAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, AccMenu.class));
        }
    }

    private void loadUserInformation() {
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl().toString())
                        .into(imageView);
            }

            if (user.getDisplayName() != null) {
                tvName.setText(user.getDisplayName());
            }

            if (user.isEmailVerified()) {
                textView.setText("Email Verified");
            } else {
                textView.setText("Email Not Verified (Click to Verify)");
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(ProfileActivity.this, "Verification Email Sent", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuLogout:

                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(this, AccMenu.class));
                break;

            case R.id.userEdit:
                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                Bitmap bmap;
                try {
                    bmap = drawable.getBitmap();
                }
                catch (Exception e) {
                 bmap = BitmapFactory.decodeResource(getResources(), R.drawable.icon_camera);
                }
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                Intent intent = new Intent(ProfileActivity.this, AccEditActivity.class);
                intent.putExtra("Picture",byteArray).putExtra("Name",tvName.getText().toString());
                startActivity(intent);


        }

        return true;
    }


    public void TopUp(View view) {
        String s = etTopUp.getText().toString();
        double value;
        if(!s.isEmpty())
        {
            try
            {
                value= Double.parseDouble(s);
                // it means it is double
                myRef.setValue((Bal+value));
            } catch (Exception e1) {
                // this means it is not double
                Toast.makeText(ProfileActivity.this, "Pleas enter corrent Amount", Toast.LENGTH_SHORT).show();
                e1.printStackTrace();
            }

        }
        else  Toast.makeText(ProfileActivity.this, "Pleas enter corrent Amount", Toast.LENGTH_SHORT).show();
    }
}