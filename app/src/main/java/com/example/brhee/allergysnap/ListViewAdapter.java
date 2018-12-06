package com.example.brhee.allergysnap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.medialablk.easytoast.EasyToast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// listview adapter for allergy
//public class ListViewAdapter extends ArrayAdapter<Allergy>{
public class ListViewAdapter extends BaseAdapter{


    // Declare Variables
    Context mContext;
    int mResource;
    private List<Allergy> allergyNamesList = null;
    private ArrayList<Allergy> arrayList;
    private User user;
    private int idcounter;
    LayoutInflater inflater;





    //    public ListViewAdapter(Context context, int resource, ArrayList<Allergy> objects, List<Allergy> allergyNamesList, User user){
    public ListViewAdapter(Context context, List<Allergy> allergyNamesList, User user){
        mContext = context;
        this.allergyNamesList = allergyNamesList;
        this.arrayList = new ArrayList<Allergy>();
        this.arrayList.addAll(allergyNamesList);
        inflater = LayoutInflater.from(mContext);
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
    public View getView(final int position, View view, final ViewGroup parent) {
        final ViewHolder holder;
        if (view == null){
            holder = new ViewHolder();
//            inflater = LayoutInflater.from(mContext);
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
        final View finalView = view;
        idcounter = user.allergies.size()-1;
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
                builder.setMessage("Adding " + allergyNamesList.get(position).getName() + " to my allergy list?").setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // add to firebase
                        boolean duplicate = false;
                        // no duplicate one -> add to firebase
                        if(user.allergies != null){
                            for(Allergy alle: user.allergies){
                                if(alle.getName().equalsIgnoreCase(allergyNamesList.get(position).getName())){
                                    AlertDialog.Builder adbuilder = new AlertDialog.Builder(parent.getContext());
                                    adbuilder.setMessage("Allergy already added")
                                            .setTitle("Duplicate Allergy");

                                    AlertDialog dialog1 = adbuilder.create();
                                    dialog1.show();
                                    duplicate = true;
                                }
                            }
                        }
                        if (!duplicate){
                            Allergy newAlle = new Allergy(allergyNamesList.get(position).getName(), ++idcounter);
                            if (user.allergies == null){
                                user.allergies = new ArrayList<>();
                            }
                            newAlle.timeAdded = System.currentTimeMillis();
                            user.allergies.add(newAlle);
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user);
                            //Toast.makeText(parent.getContext(), "Added " + allergyNamesList.get(position).getName() + " successfully!", Toast.LENGTH_LONG).show();
                            EasyToast.custom(parent.getContext(), "Added " + allergyNamesList.get(position).getName() + " successfully!", R.drawable.ic_allergies_24dp, mContext.getResources().getColor(R.color.colorAccent), mContext.getResources().getColor(R.color.colorText), Toast.LENGTH_LONG);

                        }

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
