package com.example.android.bookkeeping.firebase;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.android.bookkeeping.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseStorageActivity extends AppCompatActivity {

    private final static String LOG_TAG = "mystorage";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_firebase);

        Button buttonSave = findViewById(R.id.button_cloud_save);
        Button buttonLoad = findViewById(R.id.button_cloud_load);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              // ArrayList<AccountData> dataList = new ArrayList<>();

//                if(dataList.size() > 0 ){
//                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("data/");
//                    ref.push().setValue(dataList);
//                }

            }
        });

        buttonLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   ArrayList<AccountData> dataList = new ArrayList<>();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("data/");

                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                         HashMap<String ,  Object > value = (HashMap<String, Object >) dataSnapshot.getValue();

                         for(Map.Entry<String, Object > entry : value.entrySet()) {
                            String key = entry.getKey();
                             Object v = entry.getValue();
                             Log.i(LOG_TAG, v.getClass().getName());
                             if (v instanceof List) {                                                              //ArrayList <Account>
                                 ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String, Object>>)v;


                                 for (HashMap<String, Object> ac : list) {
                                     Log.i(LOG_TAG, "name " + ac.get("name") +
                                                            "value " +ac.get(" value") +
                                     " currency " + ac.get("currency") + " valueRUB " + ac.get("valueRUB"));


                                 }

                             } else if (v instanceof Map) {
                                 Log.i(LOG_TAG, "hmmmm ");
                                 HashMap<String, Object> map = (HashMap<String, Object>)v;

                                 for(Map.Entry<String, Object > en : map.entrySet()) {
                                     Log.i(LOG_TAG, en.getKey());     //    name    valueRUB  value  lastTransaction    currency

                                     }

                             } else {
                                 Log.i(LOG_TAG, "hmmmm ");
                             }

                          //   Log.i(LOG_TAG, "key " + key + "object " + list.get(0).getName() + " tr " +  list.get(0).getTransactions().get(0).getName());
                            // key -LH6wPM03-hBADMFHh7gobject [{name=account , valueRUB=1453,54, value=20, lastTransaction=Date 24-05-06, value 25, currency USD, comment comment , currency=EUR}]
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w(LOG_TAG, "Failed to read value.", error.toException());
                    }
                });

            }
        });

    }


}
