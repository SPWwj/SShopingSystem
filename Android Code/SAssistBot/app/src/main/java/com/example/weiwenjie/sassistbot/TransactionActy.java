package com.example.weiwenjie.sassistbot;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TransactionActy extends AppCompatActivity {
    private ArrayList<String> items;
    private ArrayAdapter<String> itemsAdapter;
    private ListView lvItems;

    FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_acty);

        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy");
        final String currentDate = sdf.format(new Date());
        myRef = database.getReference(user.getUid()+"Purchase").child(currentDate);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if(dataSnapshot.exists()){
                    itemsAdapter.clear();
                    items.add(currentDate);
                    int size =(int) dataSnapshot.getChildrenCount();
                    //String value = dataSnapshot.getValue(String.class);
                    for (int i=0 ; i < size;i++) {
                        items.add("--"+dataSnapshot.child(Integer.toString(i)).getValue(String.class));
                                //Log.d("Stockis ", dataSnapshot.child(Integer.toString(i)).getValue(String.class));
                    }
                    itemsAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Toast.makeText(TransactionActy.this, "Fail to read the value !" ,Toast.LENGTH_SHORT).show();
            }
        });


        lvItems = findViewById(R.id.lvItemsTrans);
        items = new ArrayList<>();
        itemsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, items);
        lvItems.setAdapter(itemsAdapter);


        //setupListViewListener();
    }

    private void setupListViewListener() {

        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   View item, int pos, long id) {


                        // Remove the item within array at position
                        items.remove(pos);

                        // Refresh the adapter
                        itemsAdapter.notifyDataSetChanged();
                        // Return true consumes the long click event (marks it handled)
                        //writeItems();
                        return true;
                    }


                });
    }
}
