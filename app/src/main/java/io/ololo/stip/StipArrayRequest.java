package io.ololo.stip;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by ko3a4ok on 8/15/15.
 */
public class StipArrayRequest extends JsonArrayRequest {

    public StipArrayRequest(int method, String path, JSONObject o, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        super(method, StipRequest.URL+path, o == null ? null : o.toString(), listener, errorListener);
    }
}
