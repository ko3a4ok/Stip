package io.ololo.stip;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import io.ololo.stip.fragments.InventoryFragment;

public class ChooseInventoryActivity extends AbstractStipActivity implements InventoryFragment.OnFragmentInteractionListener {

    ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_inventory);
        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                if (position == 0) return InventoryFragment.newInstance(StipRequest.THINGS, false);
                return InventoryFragment.newInstance(StipRequest.BASKET, false);
            }

            @Override
            public int getCount() {
                return 2;
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        actionBar.removeAllTabs();
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                pager.setCurrentItem(tab.getPosition());
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
            }
        };
        for (int i = 0; i < 2; i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(i == 0 ? R.string.title_section3 : R.string.basket)
                            .setTabListener(tabListener));
        }

    }

    @Override
    public void onFragmentInteraction(String id) {

    }

    public void onInventoryClick(View v) {
        Intent i = new Intent();
        i.putExtra("data", ((TextView)v.findViewById(R.id.inventory_data)).getText());
        i.putExtra("basket", pager.getCurrentItem() == 1);
        setResult(RESULT_OK, i);
        finish();
    }
}
