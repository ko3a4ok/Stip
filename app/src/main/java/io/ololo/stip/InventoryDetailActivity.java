package io.ololo.stip;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class InventoryDetailActivity extends AbstractStipActivity {
    JSONObject object;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_inventory_detail);
        if (!getIntent().hasExtra("data")) {
            object = new JSONObject();
        } else
            try {
                object = new JSONObject(getIntent().getStringExtra("data"));
            } catch (JSONException e) {}
        setTitle(object.optString("name"));
        setAllEditable(findViewById(android.R.id.content), false);
        init();
    }

    private void init() {
        ((TextView)findViewById(R.id.last_modified)).setText("Last Modified " + new Date());
        ((EditText)findViewById(R.id.detail_name)).setText(object.optString("name"));
        ((EditText)findViewById(R.id.detail_barcode)).setText(object.optString("barcode"));
//        ((EditText)findViewById(R.id.detail_image)).setText(object.optString(""));
        ((EditText)findViewById(R.id.detail_description)).setText(object.optString("description"));
        ((EditText)findViewById(R.id.detail_category)).setText(object.optString("category"));
        ((EditText)findViewById(R.id.detail_internal_id)).setText(object.optString("internalId"));
        ((EditText)findViewById(R.id.detail_quantity)).setText(object.optInt("quantity") + " Pics");
        ((EditText)findViewById(R.id.detail_cost)).setText(object.optDouble("unitCost", 0) + " USD");
        ((EditText)findViewById(R.id.detail_price)).setText(object.optDouble("unitPrice", 0) + " USD");
    }

    Menu menu;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inventory_detail, menu);
        this.menu = menu;
        if (!getIntent().hasExtra("data")) {
            setAllEditable(findViewById(android.R.id.content), true);
            menu.findItem(R.id.action_edit).setVisible(false);
            menu.findItem(R.id.action_save).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {
            setAllEditable(findViewById(android.R.id.content), true);
            item.setVisible(false);
            menu.findItem(R.id.action_save).setVisible(true);
            return true;
        }
        if (id == R.id.action_save) {
            showDialog(DIALOG_LOADING);
            try {
                object.put("name", ((TextView) findViewById(R.id.detail_name)).getText());
                object.put("barcode", ((TextView) findViewById(R.id.detail_barcode)).getText());
                object.put("description", ((TextView) findViewById(R.id.detail_description)).getText());
                object.put("category", ((TextView) findViewById(R.id.detail_category)).getText());
                object.put("internalId", ((TextView) findViewById(R.id.detail_internal_id)).getText());
                object.put("quantity", Integer.parseInt(((TextView) findViewById(R.id.detail_quantity)).getText().toString().split(" ")[0]));
                object.put("unitCost", Double.parseDouble(((TextView) findViewById(R.id.detail_cost)).getText().toString().split(" ")[0]));
                object.put("unitPrice", Double.parseDouble(((TextView) findViewById(R.id.detail_price)).getText().toString().split(" ")[0]));
            } catch (Exception ex) {}
            int method = object.has("_id") ? Request.Method.PUT: Request.Method.POST;
            String uri = StipRequest.THINGS + (object.has("_id") ? object.optString("_id") : "");
            queue.add(new StipRequest(method, uri, object, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    menu.findItem(R.id.action_edit).setVisible(true);
                    setAllEditable(findViewById(android.R.id.content), false);
                    item.setVisible(false);
                    object = response;
                    setTitle(object.optString("name"));
                    try {
                        dismissDialog(DIALOG_LOADING);
                    } catch (Exception ex) {}
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.err.println("ERROR = " + error);
                    try {
                        dismissDialog(DIALOG_LOADING);
                    } catch (Exception ex) {}
                    Toast.makeText(InventoryDetailActivity.this, R.string.error_save, Toast.LENGTH_LONG).show();
                }
            }, ((StipApplication)getApplication()).getToken()));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setAllEditable(View view, boolean b) {
        if (view instanceof EditText) {
            view.setFocusable(b);
            view.setClickable(b);
            view.setEnabled(b);
            view.setFocusableInTouchMode(b);
            System.err.println("FUCK " + b);
            return;
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = ((ViewGroup)view);
            for (int i = 0; i < group.getChildCount(); i++)
                setAllEditable(group.getChildAt(i), b);
        }
    }

}
