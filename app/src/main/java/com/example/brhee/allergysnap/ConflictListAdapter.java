package com.example.brhee.allergysnap;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
    public View getView(int position, @Nullable View convertView, @NonNull final ViewGroup parent) {
        // Get medication's information
        final ArrayList<String> conflictDrugs = Objects.requireNonNull(getItem(position)).conflictDrugs;
        final String description = Objects.requireNonNull(getItem(position)).description;
        final String severity = Objects.requireNonNull(getItem(position)).severity;
        final String source = Objects.requireNonNull(getItem(position)).source;
        final boolean alle = getItem(position).isAllergy;
        // Create the MedicationConflict Object
        MedicationConflict mc = new MedicationConflict(conflictDrugs, description, severity);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView conflictDrugsView = convertView.findViewById(R.id.drugPair);
        TextView severityView = convertView.findViewById(R.id.severity);
        ImageView myImageView = convertView.findViewById(R.id.conflictImg);
        severityView.setText(Html.fromHtml("<b>Severity: </b>" + severity));

        if (!alle) {
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
            myImageView.setImageResource(R.drawable.medication_conflict);
            conflictDrugsView.setText(Html.fromHtml(drugs.toString()));
        } else {
            String allergy = conflictDrugs.get(1).substring(0, 1).toUpperCase() + conflictDrugs.get(1).substring(1);
            conflictDrugsView.setText(Html.fromHtml("<b>Drug :</b> " + conflictDrugs.get(0) + " <b>Allergy :</b> " + allergy));
            myImageView.setImageResource(R.drawable.allergy_conflict);
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View mView = inflater.inflate(R.layout.dialog_conflict, null);
                TextView conflictDrugsView = mView.findViewById(R.id.drugPair);
                TextView descriptionView = mView.findViewById(R.id.interactionDescription);
                TextView severityView = mView.findViewById(R.id.severity);
                TextView sourceView = mView.findViewById(R.id.source);
                ImageView myImageView = mView.findViewById(R.id.med_img);
                if (!alle) {
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
                    myImageView.setImageResource(R.drawable.ic_med_conflict);
                } else {
                    String allergy = conflictDrugs.get(1).substring(0, 1).toUpperCase() + conflictDrugs.get(1).substring(1);
                    conflictDrugsView.setText(Html.fromHtml("<b>Drug :</b> " + conflictDrugs.get(0) + " <b>Allergy :</b> " + allergy));
                    myImageView.setImageResource(R.drawable.allergy_conflict_2);
                }
                descriptionView.setText(Html.fromHtml(description));
                severityView.setText(Html.fromHtml("<b>Severity: </b>" + severity));
                sourceView.setText(Html.fromHtml("<b>Source: </b>" + source));

                builder.setView(mView);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        return convertView;
    }
}
