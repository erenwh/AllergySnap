package com.example.brhee.allergysnap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// listview adapter for allergy
public class ListViewAdapter extends ArrayAdapter<Allergy>{


    // Declare Variables
    Context mContext;
    int mResource;
    private List<Allergy> allergyNamesList = null;
    private ArrayList<Allergy> arrayList;
    private User user;

    LayoutInflater inflater;


    public ListViewAdapter(Context context, int resource, ArrayList<Allergy> objects, List<Allergy> allergyNamesList, User user){
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
        this.allergyNamesList = allergyNamesList;
        inflater = LayoutInflater.from(mContext);
        this.arrayList = new ArrayList<Allergy>();
        this.arrayList.addAll(allergyNamesList);
        this.user = user;
    }

    public class ViewHolder{
        TextView name;
    }

    @Override
    public int getCount() {
        return allergyNamesList.size();
    }


    @Override
    public Allergy getItem(int position) {
        return allergyNamesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, final ViewGroup parent) {
//        final String allergy = user.allergies.get(position).getName().substring(0, 1).toUpperCase() + user.allergies.get(position).getName().substring(1);
        final String searchedAllergy = allergyNamesList.get(position).getName();

        final ViewHolder holder;
        if (view == null){
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.listview_allergy_search, null);
            //locate the textviews in listview_item.xml
            holder.name = (TextView) view.findViewById(R.id.name);
            view.setTag(holder);
        }
        else {
            holder = (ViewHolder) view.getTag();
        }
        // set the results into textviews
        holder.name.setText(allergyNamesList.get(position).getName());

        // add to my allergylist
        Button addBtn = (Button) view.findViewById(R.id.add_btn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
                builder.setMessage("Adding " + searchedAllergy + "to my allergy list?").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // add to firebase

                    }
                }).setNegativeButton("Cancel", null);
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        return view;
    }

    // Filter Class
    public void filter(String charText){
        charText = charText.toLowerCase(Locale.getDefault());
        allergyNamesList.clear();
        if (charText.length() == 0) {
            allergyNamesList.addAll(arrayList);
        } else {
            for (Allergy wp : arrayList) {
                if (wp.getName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    allergyNamesList.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
