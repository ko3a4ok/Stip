package io.ololo.stip;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

public class CustomerDetailActivity extends AbstractStipActivity {

    JSONObject object;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_customer_detail);
        if (!getIntent().hasExtra("data")) {
            object = new JSONObject();
        } else {
            try {
                object = new JSONObject(getIntent().getStringExtra("data"));
                ((EditText)findViewById(R.id.customer_name)).setText(object.optString("name"));
                ((EditText)findViewById(R.id.customer_address)).setText(object.optString("address"));
                ((EditText)findViewById(R.id.customer_email)).setText(object.optString("email"));
                ((EditText)findViewById(R.id.customer_mobile)).setText(object.optString("mobile"));
                setTitle(object.optString("name"));
            } catch (JSONException e) {
            }
            ((Button)findViewById(R.id.save_btn)).setText(R.string.save);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_customer_detail, menu);
        return true;
    }


    public void onSave(View v) {
        showDialog(DIALOG_LOADING);
        try {
            object.put("name", ((TextView) findViewById(R.id.customer_name)).getText());
            object.put("photo", "http://www.homestayenglish.uk.com/wp-content/uploads/2013/12/no-photo.png");
            object.put("address", ((TextView) findViewById(R.id.customer_address)).getText());
            object.put("email", ((TextView) findViewById(R.id.customer_email)).getText());
            object.put("mobile", ((TextView) findViewById(R.id.customer_mobile)).getText());
        } catch (Exception ex) {}
        int method = object.has("_id") ? Request.Method.PUT: Request.Method.POST;
        String uri = StipRequest.CUSTOMERS + (object.has("_id") ? object.optString("_id") : "");
        queue.add(new StipRequest(method, uri, object, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                object = response;
                setTitle(object.optString("name"));
                try {
                    dismissDialog(DIALOG_LOADING);
                } catch (Exception ex) {}
                CustomerDetailActivity.this.finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.err.println("ERROR = " + error);
                try {
                    dismissDialog(DIALOG_LOADING);
                } catch (Exception ex) {}
                Toast.makeText(CustomerDetailActivity.this, R.string.error_save, Toast.LENGTH_LONG).show();
            }
        }, ((StipApplication)getApplication()).getToken()));
    }
}
