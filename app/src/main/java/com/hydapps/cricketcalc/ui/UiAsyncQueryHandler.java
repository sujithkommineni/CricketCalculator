package com.hydapps.cricketcalc.ui;

/**
 * Created by sujith on 12/2/16.
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

    public void insertAsync(final String game, final String side1, final String side2, final AppCompatActivity activity,
                                      final AsyncOperationDoneListener doneListener) {
        mDialogFragment.show(activity.getSupportFragmentManager(), DIALOG_TAG);
        mWorkerThread.post(new Runnable() {
            @Override
            public void run() {
                final long rowId = GamesDb.insertNewGame(activity, game, side1, side2);
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
