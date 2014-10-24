package org.techteam.bashhappens.activities;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.techteam.bashhappens.R;
import org.techteam.bashhappens.content.ContentEntry;
import org.techteam.bashhappens.content.ContentFactory;
import org.techteam.bashhappens.content.ContentList;
import org.techteam.bashhappens.content.ContentResolution;
import org.techteam.bashhappens.content.FeedOverflowException;
import org.techteam.bashhappens.content.ContentSource;
import org.techteam.bashhappens.content.bashorg.BashOrgEntry;
import org.techteam.bashhappens.content.bashorg.BashOrgList;
import org.techteam.bashhappens.db.DatabaseHelper;
import org.techteam.bashhappens.db.providers.BashHappensDbProvider;
import org.techteam.bashhappens.db.tables.BashCache;

import java.io.IOException;
import java.util.Locale;


public class MainActivity extends Activity {

    ContentFactory factory = new ContentFactory(Locale.getDefault().toString());
    ContentSource content = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Button b = (Button) findViewById(R.id.button_test);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread th = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            content = factory.buildContent(ContentFactory.ContentSection.BASH_ORG_NEWEST);
                            ContentList<?> list = content.retrieveNextList();
                            for (ContentEntry e : list.getEntries()) {
                                switch (e.getContentType()) {
                                    case BASH_ORG:
                                        BashOrgEntry entry = e.toBashOrgEntry();
                                        break;
                                    case IT_HAPPENS:
                                        // BashOrgEntry entry = e.toBashOrgEntry();
                                        break;
                                }
                            }
                            System.out.println("hi");
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (FeedOverflowException e) {
                            e.printStackTrace();
                        }
                    }
                });
                th.start();
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
