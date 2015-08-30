package io.ololo.stip.fragments;

import android.annotation.TargetApi;
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
public class AppointmentsListFragment extends Fragment {
    private List<Map<String, String>> data = new ArrayList();


    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    SimpleAdapter adapter;

    // TODO: Rename and change types of parameters
    public static AppointmentsListFragment newInstance() {
        System.err.println("newInstance");
        AppointmentsListFragment fragment = new AppointmentsListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AppointmentsListFragment() {
    }
    ImageLoader imageLoader;
    private static final String[] FROM = {
            "id",
            "date",
            "title",
            "customer_name",
            "customer_photo",
            "customer_address",
            "data"};
    private static final int[] TO = {
            R.id.customer_id,
            R.id.task_time,
            R.id.task_title,
            R.id.customer_name,
            R.id.customer_photo,
            R.id.customer_address,
            R.id.customer_data};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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



    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_item_list, container, false);
        ((EditText)view.findViewById(R.id.search)).setVisibility(View.GONE);
        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((ViewGroup.MarginLayoutParams)mListView.getLayoutParams()).topMargin = 0;
        initImageLoader();
        adapter = new SimpleAdapter(getActivity(), data, R.layout.task_item, FROM, TO);
        System.err.println("INIT: " + adapter + "   || " + data);
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

    public void setData(List<Map<String, String>> data) {
        this.data.clear();
        this.data.addAll(data);
        System.err.println("DATA: " + data);
        System.err.println("ADAPTER: " + adapter);
        if (adapter != null)
            adapter.notifyDataSetChanged();
    }
}
