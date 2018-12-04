package com.example.brhee.allergysnap;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MedicationAdapter extends ArrayAdapter<Medication> {
    private static final String TAG = "MedicationAdapter";

    private Context mContext;
    private int mResource;
    private User user;

    /**
     * Holds variables in a View
     */


    /**
     * Default constructor for the MedicationAdapter
     * @param context
     * @param resource
     * @param objects
     */
    public MedicationAdapter(Context context, int resource, ArrayList<Medication> objects, User user) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        this.user = user;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        //get the persons information
        final String medication = user.medications.get(position).name;

        //medication = medication.substring(0, 1).toUpperCase() + medication.substring(1);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView medText = convertView.findViewById(R.id.med_name);
        medText.setText(medication);

        Button deleteBtn = (Button)convertView.findViewById(R.id.med_delete);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
                builder.setMessage("Are you sure you want to delete " + medication + " from your medications?").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        user.medications.remove(position);
                        notifyDataSetChanged();
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(user);
                        Toast.makeText(parent.getContext(), "Deleted " + medication + " from your medications", Toast.LENGTH_LONG).show();

                    }
                }).setNegativeButton("Cancel", null);
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        return convertView;

    }
}
