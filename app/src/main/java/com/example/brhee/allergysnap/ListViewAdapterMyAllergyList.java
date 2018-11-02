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
import java.util.List;

public class ListViewAdapterMyAllergyList extends ArrayAdapter<Allergy> {


    // Declare Variables
    Context mContext;
    private int mResource;
    LayoutInflater inflater;
    private List<Allergy> myAllergyNamesList = null;
    private ArrayList<Allergy> myAllergyArraylist;
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
        allText.setText(allergy);

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
                        Toast.makeText(parent.getContext(), "Deleted " + allergy + " from your allergy list", Toast.LENGTH_LONG).show();

                    }
                }).setNegativeButton("Cancel", null);
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

//        final ListViewAdapterMyAllergyList.ViewHolder holder;
//
//        if (view == null){
//            holder = new ListViewAdapterMyAllergyList.ViewHolder();
//            view = inflater.inflate(R.layout.listview_allergy_myallergy, null);
//            //locate the textviews in listview_item.xml
//            holder.name = (TextView) view.findViewById(R.id.name);
//            view.setTag(holder);
//        }
//        else {
//            holder = (ListViewAdapterMyAllergyList.ViewHolder) view.getTag();
//        }
//        // set the results into textviews
//        holder.name.setText(myAllergyNamesList.get(position).getName());
//
//        Button deleteBtn = (Button) view.findViewById(R.id.delete_btn);
        return view;
    }

//    // Filter Class
//    public void filter(String charText){
//        charText = charText.toLowerCase(Locale.getDefault());
//        myAllergyNamesList.clear();
//        if (charText.length() == 0) {
//            myAllergyNamesList.addAll(myAllergyArraylist);
//        } else {
//            for (Allergy wp : myAllergyArraylist) {
//                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
//                    myAllergyNamesList.add(wp);
//                }
//            }
//        }
//        notifyDataSetChanged();
//    }
}
