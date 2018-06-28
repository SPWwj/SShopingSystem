package com.example.weiwenjie.sassistbot;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class todoist extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef;



    private ArrayList<String> items;
    private ArrayList<String> buysList;
    private int[] buyUnit = {0, 0, 0, 0, 0};
    private int[] boughtUnit = {0, 0, 0, 0, 0};
    private String[] buyName = {"Apple", "Pear", "Orange", "Grape", "Watermelon"};
    private ArrayAdapter<String> itemsAdapter;
    private ListView lvItems;
    TextAnalysis ta;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todoist);



        mAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = mAuth.getCurrentUser();
        assert user != null;
        myRef = database.getReference(user.getUid());


        lvItems = findViewById(R.id.lvItems);
        items = new ArrayList<>();
        buysList = new ArrayList<>();
        itemsAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, items);
        lvItems.setAdapter(itemsAdapter);
        // ... super, setContentView, define lvItems
        readItemsBought();

        readItemsToBuy();

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                if(value!=null)
                fireRead(value);
                //Toast.makeText(todoist.this, "Value is: " + value,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Toast.makeText(todoist.this, "Fail to read the value !" ,Toast.LENGTH_SHORT).show();
            }
        });

        setupListViewListener();



    }



    // Attaches a long click listener to the listview
    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter,
                                                   View item, int pos, long id) {

                        //remove buylist
                        ta = new TextAnalysis(tempList(items.get(pos)));
                        if (ta.getValid()) {
                            buyUnit[ta.getNamePosition()] -= ta.getQuantity();
                        }
                        // Remove the item within array at position
                        items.remove(pos);

                        // Refresh the adapter
                        itemsAdapter.notifyDataSetChanged();
                        // Return true consumes the long click event (marks it handled)
                        //writeItems();
                        myRef.setValue(items.toString());
                        writeItemsTobuy();
                        return true;
                    }


                });
    }

    // ...onCreate method

    public void onAddItem(View view) {
        EditText etNewItem = findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();
        if (itemText.equals("")) {
            Toast.makeText(this, "Please enter some text", Toast.LENGTH_SHORT).show();
        } else {
            ta = new TextAnalysis(tempList(itemText));
            if (ta.getValid()) {
                buyUnit[ta.getNamePosition()] += ta.getQuantity();
            }
            writeItemsTobuy();
            items.add(itemText.trim());
            etNewItem.setText("");
            //writeItems();
            myRef.setValue(items.toString());
            itemsAdapter.notifyDataSetChanged();

        }
    }

    // ...
//    private static final String FILE_NAME = "ItemsFile.txt";

//    private void readItems() {
//        FileInputStream fis = null;
//
//
//        try {
//            fis = openFileInput(FILE_NAME);
//            InputStreamReader isr = new InputStreamReader(fis);
//            BufferedReader br = new BufferedReader(isr);
//            StringBuilder sb = new StringBuilder();
//            String text;
//            while ((text = br.readLine()) != null) {
//                sb.append(text);
//            }
//
//            sb.deleteCharAt(0);
//            sb.deleteCharAt(sb.length() - 1);
//
//            String nTemp = "";
//            for (int i = 0; i < sb.length(); i++) {
//                if (sb.charAt(i) != ',') {
//                    nTemp += sb.charAt(i);
//                    if (i == (sb.length() - 1)) {//check last item
//
//                        ta = new TextAnalysis(tempList(nTemp));
//                        if (ta.getValid()) {
//                            if(ta.getQuantity()>boughtUnit[ta.getNamePosition()]) {
//                                ta.RemoveQuantity(boughtUnit[ta.getNamePosition()]);
//                                boughtUnit[ta.getNamePosition()]=0;
//                                nTemp=ta.txtBeforeQuantity.trim()+" "+Integer.toString(ta.getQuantity()).trim()+" "+ ta.txtAfterQuantity.trim();
//                            }
//                            else if(ta.getQuantity()==boughtUnit[ta.getNamePosition()]){
//                                //ta.RemoveQuantity(boughtUnit[ta.getNamePosition()]);
//                                boughtUnit[ta.getNamePosition()]=0;
//                                nTemp = "";
//                                continue;
//                            }
//                            else if (ta.getQuantity()<boughtUnit[ta.getNamePosition()]){
//                                boughtUnit[ta.getNamePosition()]-=ta.getQuantity();
//                                    nTemp = "";
//                                    continue;
//
//                            }
//                        }
//                        itemsAdapter.add(nTemp.trim());
//
//                        nTemp = "";
//                    }
//                } else {
//
//                    ta = new TextAnalysis(tempList(nTemp));
//                    if (ta.getValid()) {
//                        if(ta.getQuantity()>boughtUnit[ta.getNamePosition()]) {
//                            ta.RemoveQuantity(boughtUnit[ta.getNamePosition()]);
//                            boughtUnit[ta.getNamePosition()]=0;
//                            nTemp=ta.txtBeforeQuantity.trim()+" "+Integer.toString(ta.getQuantity()).trim()+" "+ ta.txtAfterQuantity.trim();
//                        }
//                        else if(ta.getQuantity()==boughtUnit[ta.getNamePosition()]){
//                            boughtUnit[ta.getNamePosition()]=0;
//                            nTemp = "";
//                            continue;
//                        }
//                        else if (ta.getQuantity()<boughtUnit[ta.getNamePosition()]){
//                            boughtUnit[ta.getNamePosition()]-=ta.getQuantity();
//                                nTemp = "";
//                                continue;
//                        }
//                    }
//                    itemsAdapter.add(nTemp.trim());
//                    nTemp = "";
//                }
//            }
//            writeBought();
//            //writeItems();
//
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (fis != null) {
//                try {
//                    fis.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//
//    }

//    private void writeItems() {
//        FileOutputStream fos = null;
//
//        try {
//            fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
//            fos.write(items.toString().getBytes());
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (fos != null) {
//                try {
//                    fos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }

    private static final String FILE_BUY = "buy.txt";

    private void writeItemsTobuy() {
        FileOutputStream fos = null;
        buysList.clear();
        for (int i = 0; i < buyUnit.length; i++) {
            buysList.add(buyName[i] + " " + buyUnit[i]);

        }
        try {
            fos = openFileOutput(FILE_BUY, MODE_PRIVATE);
            fos.write(buysList.toString().getBytes());

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

    private void readItemsToBuy() {
        FileInputStream fis = null;


        try {
            fis = openFileInput(FILE_BUY);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;
            while ((text = br.readLine()) != null) {
                sb.append(text);
            }
            if (!sb.toString().equals("")) {
                sb.deleteCharAt(0);
                sb.deleteCharAt(sb.length() - 1);
            }
            StringBuilder nTemp = new StringBuilder();
            //Toast.makeText(this, sb.toString(), Toast.LENGTH_SHORT).show();
            for (int i = 0; i < sb.length(); i++) {
                if (sb.charAt(i) != ',') {
                    nTemp.append(sb.charAt(i));
                    if (i == (sb.length() - 1)) {
                        ta = new TextAnalysis(tempList(nTemp.toString()));
                        if (ta.getValid()) {
                            buyUnit[ta.getNamePosition()] = buyUnit[ta.getNamePosition()] + ta.getQuantity();
                        }
                        nTemp = new StringBuilder();
                    }
                } else {
                    ta = new TextAnalysis(tempList(nTemp.toString()));
                    if (ta.getValid()) {
                        buyUnit[ta.getNamePosition()] = buyUnit[ta.getNamePosition()] + ta.getQuantity();
                    }
                    nTemp = new StringBuilder();
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }
    private static final String FILE_BOUGHT = "bought.txt";
    private void readItemsBought() {
        FileInputStream fis = null;


        try {
            fis = openFileInput(FILE_BOUGHT);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String text;
            while ((text = br.readLine()) != null) {
                sb.append(text);
            }
            if (!sb.toString().equals("")) {
                sb.deleteCharAt(0);
                sb.deleteCharAt(sb.length() - 1);
            }
            StringBuilder nTemp = new StringBuilder();

            for (int i = 0; i < sb.length(); i++) {
                if (sb.charAt(i) != ',') {
                    nTemp.append(sb.charAt(i));
                    if (i == (sb.length() - 1)) {
                        ta = new TextAnalysis(tempList(nTemp.toString()));
                        if (ta.getValid()) {
                            boughtUnit[ta.getNamePosition()] = boughtUnit[ta.getNamePosition()] + ta.getQuantity();
                        }
                        nTemp = new StringBuilder();
                    }
                } else {
                    ta = new TextAnalysis(tempList(nTemp.toString()));
                    if (ta.getValid()) {
                        boughtUnit[ta.getNamePosition()] = boughtUnit[ta.getNamePosition()] + ta.getQuantity();
                    }
                    nTemp = new StringBuilder();
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    private void writeBought() {
        FileOutputStream fos = null;
        buysList.clear();
        for (int i = 0; i < boughtUnit.length; i++) {
            buysList.add(buyName[i] + " " + boughtUnit[i]);

        }
        try {
            fos = openFileOutput(FILE_BOUGHT, MODE_PRIVATE);
            fos.write(buysList.toString().getBytes());

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




    private List<String> tempList(String s) {
        StringBuilder stemp = new StringBuilder();
        List<String> tempList = new ArrayList<>();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ' ' || (i == s.length() - 1)) {
                if (i == s.length() - 1) stemp.append(s.charAt(i));
                tempList.add(stemp.toString());
                stemp = new StringBuilder();
            } else stemp.append(s.charAt(i));
        }

        return tempList;
    }

    private void fireRead(String s){
        itemsAdapter.clear();
        StringBuilder sb = new StringBuilder();
        sb.append(s) ;
        sb.deleteCharAt(0);
        sb.deleteCharAt(sb.length() - 1);
        String nTemp = "";
        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) != ',') {
                nTemp += sb.charAt(i);
                if (i == (sb.length() - 1)) {//check last item

                    ta = new TextAnalysis(tempList(nTemp));
                    if (ta.getValid()) {
                        if(ta.getQuantity()>boughtUnit[ta.getNamePosition()]) {
                            ta.RemoveQuantity(boughtUnit[ta.getNamePosition()]);
                            boughtUnit[ta.getNamePosition()]=0;
                            nTemp=ta.txtBeforeQuantity.trim()+" "+Integer.toString(ta.getQuantity()).trim()+" "+ ta.txtAfterQuantity.trim();
                        }
                        else if(ta.getQuantity()==boughtUnit[ta.getNamePosition()]){
                            //ta.RemoveQuantity(boughtUnit[ta.getNamePosition()]);
                            boughtUnit[ta.getNamePosition()]=0;
                            nTemp = "";
                            continue;
                        }
                        else if (ta.getQuantity()<boughtUnit[ta.getNamePosition()]){
                            boughtUnit[ta.getNamePosition()]-=ta.getQuantity();
                            nTemp = "";
                            continue;

                        }
                    }
                    items.add(nTemp.trim());

                    nTemp = "";
                }
            } else {

                ta = new TextAnalysis(tempList(nTemp));
                if (ta.getValid()) {
                    if(ta.getQuantity()>boughtUnit[ta.getNamePosition()]) {
                        ta.RemoveQuantity(boughtUnit[ta.getNamePosition()]);
                        boughtUnit[ta.getNamePosition()]=0;
                        nTemp=ta.txtBeforeQuantity.trim()+" "+Integer.toString(ta.getQuantity()).trim()+" "+ ta.txtAfterQuantity.trim();
                    }
                    else if(ta.getQuantity()==boughtUnit[ta.getNamePosition()]){
                        boughtUnit[ta.getNamePosition()]=0;
                        nTemp = "";
                        continue;
                    }
                    else if (ta.getQuantity()<boughtUnit[ta.getNamePosition()]){
                        boughtUnit[ta.getNamePosition()]-=ta.getQuantity();
                        nTemp = "";
                        continue;
                    }
                }
                items.add(nTemp.trim());
                nTemp = "";
            }
        }
        writeBought();
        itemsAdapter.notifyDataSetChanged();
        myRef.setValue(items.toString());
        //clear bought.txt
        for(int i =0 ; i<boughtUnit.length;i++){
            boughtUnit[i]=0;
        }
        writeBought();
    }



}
