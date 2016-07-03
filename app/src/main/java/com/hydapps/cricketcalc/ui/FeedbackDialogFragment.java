package com.hydapps.cricketcalc.ui;

/**

 MIT License

 Copyright (c) [2016] [www.hydapps.com]

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.

 */

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
