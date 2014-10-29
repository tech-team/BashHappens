package org.techteam.bashhappens.gui.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.techteam.bashhappens.R;
import org.techteam.bashhappens.gui.adapters.SectionsBuilder;
import org.techteam.bashhappens.content.bashorg.BashOrgEntry;
import org.techteam.bashhappens.gui.adapters.SectionsBuilder;
import org.techteam.bashhappens.gui.adapters.SectionsListAdapter;
import org.techteam.bashhappens.gui.fragments.PostsListFragment;
import org.techteam.bashhappens.gui.services.ServiceManager;
import org.techteam.bashhappens.gui.services.VoteServiceConstants;
import org.techteam.bashhappens.util.Toaster;

import java.util.List;


public class MainActivity extends ActionBarActivity {

    private List<SectionsBuilder.Section> sections;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;


    private ServiceManager serviceManager = new ServiceManager(MainActivity.this);
    private VoteBroadcastReceiver voteBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.content_frame, new PostsListFragment()).commit();
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        sections = SectionsBuilder.getSections();
        SectionsListAdapter sectionsListAdapter = new SectionsListAdapter(this.getBaseContext(), sections);
        mDrawerList.setAdapter(sectionsListAdapter);
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        //TODO: change icon, see Toolbar from v7
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                toolbar,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        registerBroadcastReceivers();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterBroadcastReceivers();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        SectionsBuilder.Section section = sections.get(position);
        Toaster.toast(getBaseContext(), section.getActionBarText());

        //TODO: change data source here via section.getContentSection()

        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true); //TODO: it does nothing
        setTitle(section.getActionBarText());
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle(title);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle your other action bar items...

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            showSettings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showSettings() {
        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(settingsIntent);
    }


    private void registerBroadcastReceivers() {
        IntentFilter translationIntentFilter = new IntentFilter(VoteServiceConstants.BROADCASTER_NAME);
        voteBroadcastReceiver = new VoteBroadcastReceiver();
        LocalBroadcastManager.getInstance(MainActivity.this)
                .registerReceiver(voteBroadcastReceiver, translationIntentFilter);
    }

    private void unregisterBroadcastReceivers() {
        LocalBroadcastManager.getInstance(MainActivity.this)
                .unregisterReceiver(voteBroadcastReceiver);
    }


    @Override
    public void onVote(BashOrgEntry entry, BashOrgEntry.VoteDirection direction) {
        serviceManager.startBashVoteService(entry, direction);
    }





    public final class VoteBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String id = intent.getStringExtra(VoteServiceConstants.ID);
            String newRating = intent.getStringExtra(VoteServiceConstants.NEW_RATING);

            if (newRating != null) {

                Toaster.toast(context.getApplicationContext(), "Changed rating for entry #" + id);

//                Translation translation = Translation.fromJsonString(data);
//
//                if (translation.getCode() != TranslateErrors.ERR_OK) {
//                    Toaster.toast(context.getApplicationContext(),
//                            TranslateErrors.getErrorMessage(translation.getCode()));
//                } else {
//                    TranslatorUI f = getTranslatorUIFragment();
//                    if (f != null) {
//                        f.setTranslatedText(translation.getText());
//                    } else {
//                        Toaster.toastLong(getBaseContext(), R.string.unexpected_error);
//                    }
//                }
            }
            else {
                String error = intent.getStringExtra(VoteServiceConstants.ERROR);
                Toaster.toast(context.getApplicationContext(), "Error for #" + id + ". " + error);
//                Translation translation = new Translation(exception);
//                Toaster.toastLong(MainActivity.this.getBaseContext(),
//                        translation.getException());
            }
        }
    }
}
