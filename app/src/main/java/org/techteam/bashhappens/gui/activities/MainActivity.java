package org.techteam.bashhappens.gui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.techteam.bashhappens.R;
import org.techteam.bashhappens.content.ContentFactory;
import org.techteam.bashhappens.content.ContentSection;
import org.techteam.bashhappens.content.ContentSource;
import org.techteam.bashhappens.gui.adapters.SectionsBuilder;
import org.techteam.bashhappens.gui.adapters.SectionsListAdapter;
import org.techteam.bashhappens.gui.fragments.PostsListFragment;
import org.techteam.bashhappens.util.Toaster;

import java.util.List;


public class MainActivity
        extends ActionBarActivity {

    private List<SectionsBuilder.Section> sections;
    private SectionsBuilder.Section section;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private ListView mDrawerList;

    private SectionsListAdapter sectionsListAdapter;

    private static final class BundleKeys {
        public static final String SECTION_ID = "SECTION_ID";
    }

    private static final class PrefKeys {
        public static final String SECTION_ID = "SECTION_ID";

        public static String contentSourceBySection(ContentSection section) {
            return "CONTENT_SOURCE_" + section.toString();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPrefs.getBoolean(getString(R.string.pref_night_mode_key), false))
            getApplication().setTheme(R.style.AppThemeNight);

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
        sections = SectionsBuilder.getSections(this);
        sectionsListAdapter = new SectionsListAdapter(this.getBaseContext(), sections);
        mDrawerList.setAdapter(sectionsListAdapter);

        if (savedInstanceState != null)
            selectItem(savedInstanceState.getInt(BundleKeys.SECTION_ID));
        else {
            int sectionId = sharedPrefs.getInt(PrefKeys.SECTION_ID, SectionsBuilder.getDefaultSectionId());
            selectItem(sectionId);
        }

        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(BundleKeys.SECTION_ID, sectionsListAdapter.getSelectedItemId());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(PrefKeys.SECTION_ID, sectionsListAdapter.getSelectedItemId());

        editor.apply();
    }

    public void saveContentSource(ContentSource contentSource) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PrefKeys.contentSourceBySection(contentSource.getSection()),
                         contentSource.getFootprint());
        editor.apply();
    }

    public ContentSource getContentSourceFromPrefs(ContentSource contentSource) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String footprint = prefs.getString(PrefKeys.contentSourceBySection(contentSource.getSection()), null);
        contentSource.loadFootprint(footprint);
        return contentSource;
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
        section = sections.get(position);
        Toaster.toast(getBaseContext(), section.getActionBarText());

        //TODO: change data source here via section.getContentSection()

        // Highlight the selected item, update the title, and close the drawer
        sectionsListAdapter.selectItem(position);
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

    public SectionsBuilder.Section getSection() {
        return section;
    }
}