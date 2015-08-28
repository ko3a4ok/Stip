package io.ololo.stip;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ko3a4ok on 8/15/15.
 */
public class StipRequest extends JsonObjectRequest{
    static final String URL = "https://new-plumber-api.herokuapp.com/";
    public static final String LOGIN = "auth/local";
    public static final String SIGN_UP = "api/users";
    public static final String THINGS = "api/inventory/";
    public static final String BASKET = "api/basket/";
    public static final String VENDORS = "api/vendors/";

    private String token;

    public StipRequest(int method, String path, JSONObject o, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener, String token) {
        super(method, URL+path, o == null ? null : o.toString(), listener, errorListener);
        System.err.println("URL: " + URL + path);
        System.err.println("DATA: " + o);
        this.token = token;
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        if (token == null) return super.getHeaders();
        return new HashMap<String, String>() {{put("Authorization", "Bearer " + token);}};
    }
}
