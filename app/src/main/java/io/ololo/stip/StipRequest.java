package io.ololo.stip;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * Created by ko3a4ok on 8/15/15.
 */
public class StipRequest extends JsonObjectRequest{
    private static final String URL = "https://plumber-api.herokuapp.com/";
    public static final String LOGIN = "auth/local";
    public static final String SIGN_UP = "api/users";

    public StipRequest(int method, String path, JSONObject o, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, URL+path, o == null ? null : o.toString(), listener, errorListener);
    }
}
