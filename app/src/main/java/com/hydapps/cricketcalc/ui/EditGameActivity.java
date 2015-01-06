package com.hydapps.cricketcalc.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.hydapps.cricketcalc.R;

import static com.hydapps.cricketcalc.utils.Utils.DEBUG;

/**
 * Created by sujith on 1/1/15.
 */
public class EditGameActivity extends ActionBarActivity {

    private static final String EDIT_FRAG = "EDIT_FRAG";
    private static final String LOG_TAG = "EditGameActivity";

    public static final int RESULT_SAVE = 0;
    public static final int RESULT_CANCEL = 1;

    private EditGameFragment mEditFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DEBUG) Log.v(LOG_TAG, "onCreate()...");
        setContentView(R.layout.edit_activity_layout);
        if (savedInstanceState == null) {
            Log.d(LOG_TAG, "adding Fragment");
            getFragmentManager().beginTransaction().add(R.id.edit_layout_parent, new EditGameFragment(), EDIT_FRAG).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mEditFragment = (EditGameFragment) getFragmentManager().findFragmentByTag(EDIT_FRAG);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, RESULT_SAVE, 0, R.string.menu_save).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add(0, RESULT_CANCEL, 1, R.string.menu_cancel).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case RESULT_SAVE:
                Intent i = getIntent().putExtras(mEditFragment.getEditedGameDetails());
                setResult(RESULT_OK, i);
                finish();
                return true;
            case RESULT_CANCEL:
                setResult(RESULT_CANCELED);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
