package com.example.brhee.allergysnap;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProfileFeedAdapter extends ArrayAdapter<UserItem> {
    private static final String TAG = "ProfileFeedAdapter";

    private Context mContext;
    private int mResource;

    /**
     * Holds variables in a View
     */


    /**
     * Default constructor for the ProfileFeedAdapter
     * @param context
     * @param resource
     * @param objects
     */
    public ProfileFeedAdapter(Context context, int resource, ArrayList<UserItem> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        //Setting each item in the ListView on the profile
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        ImageView image = convertView.findViewById(R.id.feed_pic);
        TextView feedText = convertView.findViewById(R.id.item_desc);
        TextView timeStamp = convertView.findViewById(R.id.timestamp);
        if (getItem(position) instanceof Medication) {
            // if item is an Medication, set items accordingly
            if (getItem(position).url != null) {
                Picasso.get().load(getItem(position).url).into(image);
            } else {
                Picasso.get().load("https://banner2.kisspng.com/20180308/gjq/kisspng-drug-material-yellow-vector-medicine-pills-5aa0fcdf9ba139.9135045515204999356375.jpg").into(image);
            }
            feedText.setText("Added " + getItem(position).name + " to medications!");

        } else if (getItem(position) instanceof Allergy) {
            // if item is an Allergy, set items accordingly
            Picasso.get().load("https://cdn3.iconfinder.com/data/icons/food-allergens-3/47/allergens-512.png").into(image);
            feedText.setText("Added " + getItem(position).name + " to allergies!");
        }
        // set how long ago the item was added
        long elapsedTimeMillis = System.currentTimeMillis()-getItem(position).timeAdded;
        String plural = "s";
        if (elapsedTimeMillis/1000F < 60) {
            if ((int)(elapsedTimeMillis/1000F) == 1)
                plural = "";
            timeStamp.setText(String.valueOf((int)(elapsedTimeMillis/1000F)) + " sec" + plural + " ago");
        } else if (elapsedTimeMillis/(60*1000F) < 60) {
            if ((int)(elapsedTimeMillis/(60*1000F)) == 1)
                plural = "";
            timeStamp.setText(String.valueOf((int)(elapsedTimeMillis/(60*1000F))) + " min" + plural + " ago");
        } else if (elapsedTimeMillis/(60*60*1000F) < 24) {
            if ((int)(elapsedTimeMillis/(60*60*1000F)) == 1)
                plural = "";
            timeStamp.setText(String.valueOf((int)(elapsedTimeMillis/(60*60*1000F))) + " hour" + plural + " ago");
        } else {
            if ((int)(elapsedTimeMillis/(24*60*60*1000F)) == 1)
                plural = "";
            timeStamp.setText(String.valueOf((int)(elapsedTimeMillis/(24*60*60*1000F))) + " day" + plural + " ago");
        }

        return convertView;
    }
}
