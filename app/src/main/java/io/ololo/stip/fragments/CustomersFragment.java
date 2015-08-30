package io.ololo.stip.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleAdapter;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.ololo.stip.CustomerDetailActivity;
import io.ololo.stip.MainActivity;
import io.ololo.stip.R;
import io.ololo.stip.StipApplication;
import io.ololo.stip.StipArrayRequest;
import io.ololo.stip.StipRequest;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 */
public class CustomersFragment extends Fragment {
    private String token;

    private List<Map<String, String>> data = new ArrayList();


    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private SimpleAdapter adapter;

    // TODO: Rename and change types of parameters
    public static CustomersFragment newInstance() {
        CustomersFragment fragment = new CustomersFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CustomersFragment() {
    }
    ImageLoader imageLoader;
    private static final String[] FROM = {"id", "name", "photo", "address", "data"};
    private static final int[] TO = {R.id.customer_id, R.id.customer_name, R.id.customer_photo, R.id.customer_address, R.id.customer_data};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
        }
        token = ((StipApplication) getActivity().getApplication()).getToken();
        // TODO: Change Adapter to display your content
        adapter = new SimpleAdapter(getActivity(), data, R.layout.customer_item, FROM, TO);
    }

    private SimpleAdapter.ViewBinder viewBinder = new SimpleAdapter.ViewBinder() {
        @Override
        public boolean setViewValue(View view, Object data, String textRepresentation) {
            if (view.getId() == R.id.customer_photo) {
//                ((NetworkImageView)view).setDefaultImageResId(R.drawable.default_inventory);
                ((NetworkImageView)view).setImageUrl((String) data, imageLoader);
                return true;
            }
            return false;
        }
    };


    private void initImageLoader() {
        imageLoader = new ImageLoader(Volley.newRequestQueue(getActivity()), new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(40);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });
    }


    private TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // ignore
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // ignore
        }

        @Override
        public void afterTextChanged(Editable s) {
            System.err.println("*** Search value changed: " + s.toString());
            setEmptyText(getString(R.string.empty_msg, s.toString()));
            adapter.getFilter().filter(s.toString());
        }
    };

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        ((EditText)view.findViewById(R.id.search)).addTextChangedListener(searchTextWatcher);
        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        initImageLoader();
        mListView.setAdapter(adapter);
        adapter.setViewBinder(viewBinder);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(getActivity(), CustomerDetailActivity.class).putExtra("data", ((TextView) view.findViewById(R.id.customer_data)).getText()));
            }
        });
        // Set OnItemClickListener so we can be notified on item clicks
        return view;
    }

    private void addData(JSONObject o, int i) {
        Map<String, String> map = new HashMap();
        map.put(FROM[0], o.optString("_id"));
        map.put(FROM[1], o.optString("name"));
        map.put(FROM[2], o.optString("photo"));
        map.put(FROM[3], o.optString("address"));
        map.put(FROM[4], o.toString());
        data.add(map);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().showDialog(MainActivity.DIALOG_LOADING);
        Volley.newRequestQueue(getActivity()).add(new StipArrayRequest(Request.Method.GET, StipRequest.CUSTOMERS,
                null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray array) {
                if (getView() == null)return;
                mListView.setEmptyView(getView().findViewById(android.R.id.empty));
                getActivity().dismissDialog(MainActivity.DIALOG_LOADING);
                data.clear();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject o = array.optJSONObject(i);
                    addData(o, i);
                }
                adapter.notifyDataSetChanged();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.err.println("error = " + error);
                try {
                    getActivity().dismissDialog(MainActivity.DIALOG_LOADING);
                } catch (Exception ex) {
                }
                Toast.makeText(getActivity(), R.string.error_loading, Toast.LENGTH_LONG).show();
            }
        }, token));
    }


    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }


}
