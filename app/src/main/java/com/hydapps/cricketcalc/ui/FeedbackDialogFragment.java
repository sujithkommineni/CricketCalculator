package com.hydapps.cricketcalc.ui;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.hydapps.cricketcalc.R;
import com.hydapps.cricketcalc.utils.Utils;

/**
 * Created by sujit on 22-06-2016.
 */
public class FeedbackDialogFragment extends android.support.v4.app.DialogFragment{

    private static String LOG_TAG = "FeedbackDialogFragment";

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.str_feedback_title)
                .setMessage(R.string.str_feedback_message)
                .setPositiveButton(R.string.str_review, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
                        Intent playIntent = new Intent(Intent.ACTION_VIEW, uri);
                        playIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY|
                                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                        if (Utils.isNetworkAvailable(getContext())) Utils.setAppReviewed(getContext(), true);
                        try {
                            startActivity(playIntent);
                        } catch(ActivityNotFoundException e) {
                            Log.e(LOG_TAG, e.getMessage());
                        }
                    }
                })
                .setNeutralButton(R.string.str_later, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // nothing to do.
                    }
                })
                .create();

        return dialog;
    }
}
