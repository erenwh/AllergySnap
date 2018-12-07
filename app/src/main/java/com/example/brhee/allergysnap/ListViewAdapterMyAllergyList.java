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
import com.medialablk.easytoast.EasyToast;

import java.util.ArrayList;

public class ListViewAdapterMyAllergyList extends ArrayAdapter<Allergy> {


    // Declare Variables
    Context mContext;
    private int mResource;
    LayoutInflater inflater;
    private User user;

//    public ListViewAdapterMyAllergyList(Context context, int resource, ArrayList<Allergy> objects, List<Allergy> allergyNamesList, User user){
    public ListViewAdapterMyAllergyList(Context context, int resource, ArrayList<Allergy> objects, User user){
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        this.user = user;

    }

    @NonNull
    @Override
    public View getView(final int position, View view, final ViewGroup parent) {
        final String allergy = user.allergies.get(position).getName();

        inflater = LayoutInflater.from(mContext);
        view = inflater.inflate(mResource, parent, false);

        TextView  allText = view.findViewById(R.id.myAllergyName);
        String allergy2 = allergy.substring(0, 1).toUpperCase() + allergy.substring(1).toLowerCase();
        allText.setText(allergy2);

        Button deleteBtn = (Button) view.findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
                builder.setMessage("Are you sure you want to delete " + allergy + " from your allergy list?").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        user.allergies.remove(position);
                        notifyDataSetChanged();
                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(user);
                        //Toast.makeText(parent.getContext(), "Deleted " + allergy + " from your allergy list", Toast.LENGTH_LONG).show();
                        EasyToast.custom(parent.getContext(), "Deleted " + allergy + " from your allergy list", R.drawable.ic_allergies_24dp, mContext.getResources().getColor(R.color.colorAlert), mContext.getResources().getColor(R.color.background_light), Toast.LENGTH_LONG);
                    }
                }).setNegativeButton("Cancel", null);
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        return view;
    }

}
