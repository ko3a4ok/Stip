package io.ololo.stip;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.TextView;


import org.json.JSONException;
import org.json.JSONObject;

import io.ololo.stip.fragments.AgendaFragment;
import io.ololo.stip.fragments.CustomersFragment;
import io.ololo.stip.fragments.InventoryFragment;

public class MainActivity extends AbstractStipActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, InventoryFragment.OnFragmentInteractionListener {


    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0);
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        onSectionAttached(1);

        mTitle = getTitle();
        restoreActionBar();
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    Fragment fragment = null;
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (search != null) search.setVisible(false);
        switch (position) {
            case 0:
                onSectionAttached(1);
                fragment = CustomersFragment.newInstance(false);
                break;
            case 1:
                onSectionAttached(2);
                search.setVisible(true);
                fragment = AgendaFragment.newInstance();
                break;
            case 2:
                onSectionAttached(3);
                fragment = InventoryFragment.newInstance(StipRequest.THINGS, false);
                break;
            case 3:
                onSectionAttached(4);
                fragment = InventoryFragment.newInstance(StipRequest.BASKET, false);
                break;
            default:
                fragment = PlaceholderFragment.newInstance(position+1);
        }
        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    private static int[] titles = {
            R.string.title_section1,
            R.string.title_section2,
            R.string.title_section3,
            R.string.basket,
            R.string.title_section4,
            R.string.title_section5,
            R.string.title_section6,
            R.string.title_section7,
    };

    public void onSectionAttached(int number) {
        mTitle = getString(titles[number-1]);
        restoreActionBar();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);

    }

    MenuItem search;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        this.search = menu.findItem(R.id.search);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            SearchManager manager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView search = (SearchView) MenuItemCompat.getActionView(this.search);
            search.setIconifiedByDefault(false);
            search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String query) {
                    ((AgendaFragment)fragment).setFilterText(query);
                    return true;

                }

            });


        }
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
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
    public void onFragmentInteraction(String id) {

    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    public void onInventoryClick(View v) {
        Intent i = new Intent(this, InventoryDetailActivity.class);
        i.putExtra("data", ((TextView)v.findViewById(R.id.inventory_data)).getText());
        startActivity(i);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == NavigationDrawerFragment.VENDOR_REQUEST) {
                try {
                    ((InventoryFragment)fragment).addToBasket(new JSONObject(data.getStringExtra("data")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
