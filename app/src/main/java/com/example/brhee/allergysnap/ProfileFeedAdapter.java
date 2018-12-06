package com.example.brhee.allergysnap;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
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
        // Setting each item in the ListView on the profile
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        final ImageView image = convertView.findViewById(R.id.feed_pic);
        TextView feedText = convertView.findViewById(R.id.item_desc);
        TextView timeStamp = convertView.findViewById(R.id.timestamp);
        if (getItem(position) instanceof Medication) {
            // if item is an Medication, set items accordingly
            Picasso.get().load(getItem(position).url).into(image, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError(Exception e) {
                    image.setImageResource(R.drawable.ic_medicine_placeholder);
                }
            });
            feedText.setText("Added " + getItem(position).name + " to medications!");

        } else if (getItem(position) instanceof Allergy) {
            // if item is an Allergy, set items accordingly
            Picasso.get().load(R.drawable.ic_allergy_placeholder).into(image);
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

        // Dialog box displayed to show more info about the medication if clicked
        if (getItem(position) instanceof Medication) {
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
                    LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View mView = inflater.inflate(R.layout.dialog_medication, null);
                    final ImageView image = mView.findViewById(R.id.med_img);
                    final TextView medName = mView.findViewById(R.id.med_name);
                    final TextView medInfo = mView.findViewById(R.id.med_info);
                    ProgressBar progressBar = mView.findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.VISIBLE);
                    Picasso.get().load(getItem(position).url).into(image, new Callback() {
                        @Override
                        public void onSuccess() {
                            medName.setText(getItem(position).name);
                            medInfo.setText(getItem(position).info);
                        }

                        @Override
                        public void onError(Exception e) {
                            image.setImageResource(R.drawable.ic_medicine_placeholder);
                            medName.setText(getItem(position).name);
                            medInfo.setText(getItem(position).info);
                        }
                    });
                    progressBar.setVisibility(View.GONE);
                    builder.setView(mView);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }

        return convertView;
    }
}
