package io.ololo.stip;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class AddTaskActivity extends AbstractStipActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    public static final int CUSTOMER = 123;
    public static final int DIALOG_DATE = 1001;
    public static final int DIALOG_TIME = 1002;
    JSONObject client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        findViewById(R.id.task_client).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClientChoose(null);
            }
        });
        findViewById(R.id.task_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DIALOG_DATE);
            }
        });
        findViewById(R.id.task_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(DIALOG_TIME);
            }
        });
    }

    public void onAddInventory(View v) {

    }
    public void onStart(View v) {
        // {data={"basketRef":[],
        // "__v":0,"
        // clientRef":"55e2c2dfba5528030019e1e7",
        // "inventoryRef":["55d47f98fe1f130300e3b70d","55d476c5fe1f130300e3b708"],
        // "date":"2015-08-31T22:02:13.854Z","_id":"55e2ea54ba5528030019e1eb","status":"TODO"}
        JSONObject o = new JSONObject();
        try {
            o.put("clientRef", client.optString("_id"));
            o.put("status", "TODO");
            o.put("date", String.format("%sT%s:00.000Z", ((TextView) findViewById(R.id.task_date)).getText().toString(), ((TextView) findViewById(R.id.task_time)).getText().toString()));
            o.put("name", ((EditText)findViewById(R.id.task_title)).getText().toString());
            o.put("cost", Double.parseDouble(((EditText)findViewById(R.id.task_cost)).getText().toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.err.println("POST: " + o);
        queue.add(new StipRequest(Request.Method.POST, StipRequest.TASKS, o, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    dismissDialog(DIALOG_LOADING);
                } catch (Exception ex) {}
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.err.println("ERROR = " + error);
                try {
                    dismissDialog(DIALOG_LOADING);
                } catch (Exception ex) {}
                Toast.makeText(AddTaskActivity.this, R.string.error_save, Toast.LENGTH_LONG).show();
            }
        }, ((StipApplication)getApplication()).getToken()));

    }


    public void onClientChoose(View v) {
        startActivityForResult(new Intent(this, ChooseCustomerActivity.class), CUSTOMER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CUSTOMER && resultCode == RESULT_OK) {
            try {
                client = new JSONObject(data.getStringExtra("client"));
                ((TextView)findViewById(R.id.task_client)).setText(client.optString("name"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_TIME) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            return new TimePickerDialog(this, this, hour, minute, DateFormat.is24HourFormat(this));
        }
        if (id == DIALOG_DATE) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(this, this, year, month, day);
        }
        return super.onCreateDialog(id);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int h, int m) {
        ((TextView)findViewById(R.id.task_time)).setText(String.format("%02d:%02d", h, m));
    }

    @Override
    public void onDateSet(DatePicker datePicker, int y, int m, int d) {
        ((TextView)findViewById(R.id.task_date)).setText(String.format("%d-%02d-%02d", y, m + 1, d));
    }
}
