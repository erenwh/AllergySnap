package com.example.brhee.allergysnap;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ConflictListAdapter extends ArrayAdapter<MedicationConflict> {

    private static final String TAG = "ConflictListAdapter";

    private Context mContext;
    int mResource;

    public ConflictListAdapter(@NonNull Context context, int resource, @NonNull List<MedicationConflict> objects) {
        super(context, resource, objects);
        this.mContext = context;
        mResource = resource;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Get medication's information
        ArrayList<String> conflictDrugs = Objects.requireNonNull(getItem(position)).conflictDrugs;
        String description = Objects.requireNonNull(getItem(position)).description;
        String severity = Objects.requireNonNull(getItem(position)).severity;
        String source = Objects.requireNonNull(getItem(position)).source;
        // Create the MedicationConflict Object
        MedicationConflict mc = new MedicationConflict(conflictDrugs, description, severity);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView conflictDrugsView = convertView.findViewById(R.id.drugPair);
        TextView descriptionView = convertView.findViewById(R.id.interactionDescription);
        TextView severityView = convertView.findViewById(R.id.severity);
        TextView sourceView = convertView.findViewById(R.id.source);
        ImageView myImageView = convertView.findViewById(R.id.conflictImg);

        StringBuilder drugs = new StringBuilder();
        drugs.append("<b>Drugs:</b>");
        if (conflictDrugs.size() == 1) {
            drugs.append(" " + conflictDrugs.get(0));
        } else if (conflictDrugs.size() == 2) {
            drugs.append(" " + conflictDrugs.get(0) + " and " + conflictDrugs.get(1));
        } else {
            for (int i = 0; i < conflictDrugs.size(); i++) {
                String start = " ";
                if (i == conflictDrugs.size() - 1) {
                    start = " and ";
                } else if (i != 0) {
                    start = ", ";
                }
                drugs.append(start + conflictDrugs.get(i));
            }
        }


        conflictDrugsView.setText(Html.fromHtml(drugs.toString()));
        descriptionView.setText(Html.fromHtml(description));
        severityView.setText(Html.fromHtml("<b>Severity: </b>" + severity));
        sourceView.setText(Html.fromHtml("<b>Source: </b>" + source));
        myImageView.setImageResource(R.drawable.red_error);


        return convertView;
    }
}
