package io.ololo.stip;

import android.os.Bundle;

import io.ololo.stip.fragments.CustomersFragment;

public class ChooseCustomerActivity extends AbstractStipActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_customer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager().beginTransaction().add(R.id.container, CustomersFragment.newInstance(true)).commit();
    }

}
