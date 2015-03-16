package com.cuberob.wearaccuracy.activities;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.cuberob.wearaccuracy.R;
import com.cuberob.wearaccuracy.adapters.DrawerAdapter;
import com.cuberob.wearaccuracy.fragments.VibrationFragment;

public class MainActivity extends BaseActivity implements ListView.OnItemClickListener {

    public static final String TAG = "MainActivity";
    Toolbar mToolbar;
    DrawerLayout mDrawerLayout;
    ListView mDrawerListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        setupDrawer();
    }

    private void setupDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerListView = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mDrawerListView.setAdapter(DrawerAdapter.newInstance(this));
        mDrawerListView.setOnItemClickListener(this);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name);
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);

        // enable ActionBar app icon to behave as action to toggle nav drawer and animate accordingly
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        android.app.Fragment fragment = null;
        switch(position){
            case 0:
                fragment = new VibrationFragment();
                break;
            case 1:
                fragment = new VibrationFragment();
                break;
            case 2:
                fragment = new VibrationFragment();
                break;
        }


        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerListView.setItemChecked(position, true);
        setTitle(((TextView)view.findViewById(R.id.navdrawer_textView)).getText().toString());
        mDrawerLayout.closeDrawer(mDrawerListView);
    }

}
