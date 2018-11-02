package com.example.brhee.allergysnap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

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
//        this.myAllergyNamesList = allergyNamesList;
        inflater = LayoutInflater.from(mContext);
        this.myAllergyArraylist = new ArrayList<Allergy>();
//        this.myAllergyArraylist.addAll(allergyNamesList);
        this.user = user;
    }

    public class ViewHolder{
        TextView name;
    }

    @Override
    public int getCount() {
        return myAllergyNamesList .size();
    }


    @Override
    public Allergy getItem(int position) {
        return myAllergyNamesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        final ListViewAdapterMyAllergyList.ViewHolder holder;

        if (view == null){
            holder = new ListViewAdapterMyAllergyList.ViewHolder();
            view = inflater.inflate(R.layout.listview_allergy_myallergy, null);
            //locate the textviews in listview_item.xml
            holder.name = (TextView) view.findViewById(R.id.name);
            view.setTag(holder);
        }
        else {
            holder = (ListViewAdapterMyAllergyList.ViewHolder) view.getTag();
        }
        // set the results into textviews
        holder.name.setText(myAllergyNamesList.get(position).getName());

        Button deleteBtn = (Button) view.findViewById(R.id.delete_btn);
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
