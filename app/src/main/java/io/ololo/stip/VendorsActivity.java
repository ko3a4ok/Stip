package io.ololo.stip;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import io.ololo.stip.fragments.InventoryFragment;

public class VendorsActivity extends AbstractStipActivity implements InventoryFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendrors);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager().beginTransaction().add(
                R.id.container, InventoryFragment.newInstance(StipRequest.VENDORS, true)
        ).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_vendrors, menu);
        return true;
    }


    @Override
    public void onFragmentInteraction(String id) {

    }

    public void onInventoryClick(View v) {
        Intent i = new Intent();
        i.putExtra("data", ((TextView) v.findViewById(R.id.inventory_data)).getText());
        setResult(RESULT_OK, i);
        finish();
    }
}
