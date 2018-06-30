package com.example.weiwenjie.sassistbot;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Transaction;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        final LinearLayout botview = findViewById(R.id.botView);
        botview.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                if(mAuth.getCurrentUser() != null) {
                Intent intent = new Intent(MainActivity.this, BotView.class);
                startActivity(intent);
                }
                else Toast.makeText(MainActivity.this,"Please Sign in first!",Toast.LENGTH_SHORT).show();
            }
        });

        final LinearLayout todoist = findViewById(R.id.todoist);
        todoist.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                if(mAuth.getCurrentUser() != null) {
                    Intent intent = new Intent(MainActivity.this, todoist.class);
                    startActivity(intent);
                }
                else Toast.makeText(MainActivity.this,"Please Sign in first!",Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void toAccMenu(View view){
        Intent intent = new Intent(MainActivity.this, AccMenu.class);
        startActivity(intent);
    }
    public void goSmartThings(View view){
        if(mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(MainActivity.this, SmartThings.class);
            startActivity(intent);
        }
        else Toast.makeText(MainActivity.this,"Please Sign in first!",Toast.LENGTH_SHORT).show();
    }

    public void GoImport(View view) {
        if(mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(MainActivity.this, ImportItems.class);
            startActivity(intent);
        }
        else Toast.makeText(MainActivity.this,"Please Sign in first!",Toast.LENGTH_SHORT).show();
    }

    public void GoManage(View view) {

        if(mAuth.getCurrentUser() != null) {
//            Intent intent = new Intent(MainActivity.this, Management.class);
           // startActivity(intent);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://s3-ap-southeast-1.amazonaws.com/sassitance/table.html"));
            startActivity(browserIntent);
        }
        else Toast.makeText(MainActivity.this,"Please Sign in first!",Toast.LENGTH_SHORT).show();
    }

    public void GoTrans(View view) {
        if(mAuth.getCurrentUser() != null) {
            Intent intent = new Intent(MainActivity.this, TransactionActy.class);
            startActivity(intent);
        }
        else Toast.makeText(MainActivity.this,"Please Sign in first!",Toast.LENGTH_SHORT).show();
    }
}

