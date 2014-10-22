package org.techteam.bashhappens.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.techteam.bashhappens.R;
import org.techteam.bashhappens.content.FeedOverflowException;
import org.techteam.bashhappens.content.IContent;
import org.techteam.bashhappens.content.bashorg.BashOrgFactory;
import org.techteam.bashhappens.content.bashorg.BashOrgList;

import java.io.IOException;
import java.util.Locale;


public class MainActivity extends Activity {

    BashOrgFactory factory = new BashOrgFactory(Locale.getDefault().toString());
    IContent content = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        content = factory.buildBashOrgNewest();

        Button b = (Button) findViewById(R.id.button_test);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread th = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            BashOrgList list = content.retrieveNextList();
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
        });
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
