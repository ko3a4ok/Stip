package io.ololo.stip;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.ololo.stip.fragments.AppointmentsListFragment;
import io.ololo.stip.fragments.InventoryFragment;

public class DetailTaskActivity extends AbstractStipActivity implements InventoryFragment.OnFragmentInteractionListener {

    JSONObject task;
    JSONArray inventories;
    ImageLoader imageLoader;
    InventoryFragment inventoryFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_task);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imageLoader = new ImageLoader(Volley.newRequestQueue(this), new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(40);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });

        try {
            task = new JSONObject(getIntent().getStringExtra("data"));
            inventories = task.optJSONArray("inventories");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        init();
    }

    private void init() {
        setTitle(task.optString("title"));
        for (int i = 0; i < AppointmentsListFragment.FROM.length; i++) {
            int id = AppointmentsListFragment.TO[i];
            String data = task.optString(AppointmentsListFragment.FROM[i], "");
            View v = findViewById(id);
            if (v instanceof TextView) {
                ((TextView)v).setText(data);
            } else {
                ((NetworkImageView)v).setImageUrl(data, imageLoader);
            }
            String desc = String.format("- %d Items", inventories.length());
            double invCost = 0;
            for (int j = 0; j < inventories.length(); j++) {
                if (inventories.optJSONObject(j) != null)
                invCost += inventories.optJSONObject(j).optDouble("unitPrice", 0);
            }
            double serviceCharge = task.optDouble("cost", 0);
            ((TextView)findViewById(R.id.item_cost_desc)).setText(desc);
            ((TextView)findViewById(R.id.total_cost_desc)).setText(desc);
            ((TextView)findViewById(R.id.item_cost_value)).setText(String.format("$%.2f", invCost));
            ((TextView)findViewById(R.id.service_charge_value)).setText(String.format("$%.2f", serviceCharge));
            ((TextView)findViewById(R.id.total_cost_value)).setText(String.format("$%.2f", invCost + serviceCharge));
            inventoryFragment = InventoryFragment.newInstance(null, true);
            getSupportFragmentManager().beginTransaction().add(R.id.inventories, inventoryFragment).commit();
        }
    }

    public void onStop(View v) {
        showDialog(DIALOG_LOADING);
        JSONObject o = new JSONObject();
        try {
            o.put("status", "complete");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        queue.add(new StipRequest(Request.Method.PUT, StipRequest.TASKS + task.optString("_id"), o, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    dismissDialog(DIALOG_LOADING);
                } catch (Exception ex) {
                }
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.err.println("ERROR = " + error);
                try {
                    dismissDialog(DIALOG_LOADING);
                } catch (Exception ex) {
                }
                Toast.makeText(DetailTaskActivity.this, R.string.error_save, Toast.LENGTH_LONG).show();
            }
        }, ((StipApplication) getApplication()).getToken()));

    }

    public void onInventoryClick(View v) {

    }

    @Override
    public void onFragmentInteraction(String id) {
        try {
            inventoryFragment.updateData(inventories);
        }catch (Exception ex) {}
    }
}
