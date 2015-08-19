package io.ololo.stip;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ko3a4ok on 8/15/15.
 */
public class StipArrayRequest extends JsonArrayRequest {
    String token;
    public StipArrayRequest(int method, String path, JSONObject o, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener, String token) {
        super(method, StipRequest.URL+path, o == null ? null : o.toString(), listener, errorListener);
        this.token = token;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return new HashMap<String, String>() {{put("Authorization", "Bearer " + token);}};
    }
}
