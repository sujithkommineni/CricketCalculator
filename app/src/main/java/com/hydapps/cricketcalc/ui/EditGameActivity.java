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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.hydapps.cricketcalc.R;
import com.hydapps.cricketcalc.db.GameDetails;
import com.hydapps.cricketcalc.utils.Utils;

import static com.hydapps.cricketcalc.utils.Utils.DEBUG;


public class EditGameActivity extends AppCompatActivity {

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
            mEditFragment = new EditGameFragment();
            Log.d(LOG_TAG, "adding Fragment");
            getSupportFragmentManager().beginTransaction().add(R.id.edit_layout_parent, mEditFragment, EDIT_FRAG).commit();
        } else {
            mEditFragment = (EditGameFragment) getSupportFragmentManager().findFragmentByTag(EDIT_FRAG);
        }
        mEditFragment.setArguments(getIntent().getExtras());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuSave = menu.add(0, RESULT_SAVE, 0, R.string.menu_save);
        MenuItem menuCancel = menu.add(0, RESULT_CANCEL, 1, R.string.menu_cancel);
        MenuItemCompat me = new MenuItemCompat();
        me.setShowAsAction(menuSave, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        me = new MenuItemCompat();
        me.setShowAsAction(menuCancel, MenuItemCompat.SHOW_AS_ACTION_ALWAYS);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case RESULT_SAVE:
                Intent i = getIntent().putExtra(Utils.EXTRA_GAME_DETAILS, mEditFragment.getEditedGameDetails());
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

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}

