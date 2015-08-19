package io.ololo.stip;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by ko3a4ok on 8/19/15.
 */
public class AbstractStipActivity extends AppCompatActivity {
    public final static int DIALOG_LOADING = 1001;
    protected RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(this);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_LOADING) {
            ProgressDialog mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            return mProgressDialog;
        }
        return super.onCreateDialog(id);
    }




}
