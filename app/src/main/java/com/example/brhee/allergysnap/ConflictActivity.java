package com.example.brhee.allergysnap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

public class ConflictActivity extends AppCompatActivity {

    private static final String TAG = "ConflictActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conflict);
        Log.d(TAG, "onCreate: Started");
        ListView mListView = (ListView) findViewById(R.id.listView);

        ArrayList<MedicationConflict> conflictList = new ArrayList<>();

        ArrayList<String> drug1 = new ArrayList<>();
        drug1.add("a");
        drug1.add("b");
        String des = "des";
        String sev = "DEADLY";

        MedicationConflict mc = new MedicationConflict(drug1, des, sev);

        ArrayList<String> drug2 = new ArrayList<>();
        drug2.add("b");
        drug2.add("c");
        String des2 = "des";
        String sev2 = "DEADLY";

        MedicationConflict mc2 = new MedicationConflict(drug2, des2, sev2);

        conflictList.add(mc);
        conflictList.add(mc2);

        ConflictListAdapter adapter = new ConflictListAdapter(this, R.layout.adapter_view_layout, conflictList);
        mListView.setAdapter(adapter);
    }
}
