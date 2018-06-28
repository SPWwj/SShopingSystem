package com.example.weiwenjie.sassistbot;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.weiwenjie.sassistbot.R.drawable.off;
import static com.example.weiwenjie.sassistbot.R.drawable.on;

public class SmartThings extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;
    boolean lightOn=false;
    Button btnSwitch;
    ImageView ivLight;
    int lightState=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smart_things);
        mAuth = FirebaseAuth.getInstance();
        myRef = database.getReference("LED_01");

        btnSwitch=findViewById(R.id.btnSwitch);
        ivLight = findViewById(R.id.ivLight);

        myRef.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Integer value = dataSnapshot.getValue(Integer.class);

                if(value==0){
                    btnSwitch.setBackground(getDrawable(off));
                    lightOn=false;
                    ivLight.setImageDrawable(getDrawable(R.drawable.lightbulboff));
                }
                else if (value==1){
                    btnSwitch.setBackground(getDrawable(on));
                    lightOn=true;
                    ivLight.setImageDrawable(getDrawable(R.drawable.lightbulb));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
            }
        });
    }

    public void ToggleLight(View view){
        if (lightOn){
            lightState=0;
            myRef.setValue(lightState).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SmartThings.this,"Fail to Off the light!",Toast.LENGTH_SHORT).show();
                }
            });

        }
        else{
            lightState=1;
            myRef.setValue(lightState).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(SmartThings.this,"Fail to On the light!",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
