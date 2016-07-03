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
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;

import com.hydapps.cricketcalc.db.GameDetails;
import com.hydapps.cricketcalc.db.GamesDb;
import com.hydapps.cricketcalc.utils.Utils;

/**
 * This class is used to query DB on a worker thread. Also it shows
 * a progress dialog while query in progress to block any UI touches.
 */
public class UiAsyncQueryHandler implements Runnable {

    private static String DIALOG_TAG = "progress_dialog_async";
    private DialogFragment mDialogFragment;
    private Handler mWorkerThread;
    private static UiAsyncQueryHandler sUiAsyncQueryHandler;

    private UiAsyncQueryHandler() {
        mDialogFragment = new ProgressDialogFrag();
        HandlerThread handlerThread = new HandlerThread("UiAsyncQuery");
        handlerThread.start();
        mWorkerThread = new Handler(handlerThread.getLooper());
    }

    public static synchronized UiAsyncQueryHandler getInstance() {
        if (sUiAsyncQueryHandler == null) {
            sUiAsyncQueryHandler = new UiAsyncQueryHandler();
        }
        return sUiAsyncQueryHandler;
    }


    @Override
    public void run() {
        mDialogFragment.dismiss();
    }

    public interface AsyncOperation {
        public void asyncOperation();
    }

    public interface AsyncOperationDoneListener {
        public void onAsyncOperationDone(Object obj);
    }


    public void startAsyncOperation(final AsyncOperation asyncOperation, final AppCompatActivity activity) {
        mDialogFragment.show(activity.getSupportFragmentManager(), DIALOG_TAG);
        mWorkerThread.post(new Runnable() {
            @Override
            public void run() {
                asyncOperation.asyncOperation();
                activity.runOnUiThread(this);
            }
        });
    }
    public void startAsyncOperation(final AsyncOperation asyncOperation, final AppCompatActivity activity,
                                    final AsyncOperationDoneListener doneListener) {
        mDialogFragment.show(activity.getSupportFragmentManager(), DIALOG_TAG);
        mWorkerThread.post(new Runnable() {
            @Override
            public void run() {
                asyncOperation.asyncOperation();
                if (doneListener != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDialogFragment.dismiss();
                            doneListener.onAsyncOperationDone(null);
                        }
                    });
                } else activity.runOnUiThread(this);
            }
        });
    }

    public void insertAsync(final String game, final String side1, final String side2, final long startTime, final AppCompatActivity activity,
                                      final AsyncOperationDoneListener doneListener) {
        mDialogFragment.show(activity.getSupportFragmentManager(), DIALOG_TAG);
        mWorkerThread.post(new Runnable() {
            @Override
            public void run() {
                final long rowId = GamesDb.insertNewGame(activity, game, side1, side2, startTime);
                if (doneListener != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mDialogFragment.dismiss();
                            doneListener.onAsyncOperationDone(rowId);
                        }
                    });
                } else activity.runOnUiThread(this);
            }
        });
    }


    public void queryForAllGamesAsync(final AsyncOperationDoneListener result, final AppCompatActivity activity) {
        mDialogFragment.show(activity.getSupportFragmentManager(), DIALOG_TAG);
        mWorkerThread.post(new Runnable() {
            @Override
            public void run() {
                final Cursor cursor = GamesDb.getAllGames(activity);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mDialogFragment.dismiss();
                        result.onAsyncOperationDone(cursor);
                    }
                });
            }
        });
    }

    public void saveAsync(final Context context, final GameDetails game) {
        mWorkerThread.post(new Runnable() {
            @Override
            public void run() {
                GamesDb.updateGameDetails(context, game);
            }
        });
    }

    public static class ProgressDialogFrag extends DialogFragment {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            ProgressDialog dialog = new ProgressDialog(getActivity());
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            return dialog;
        }
    }



}
