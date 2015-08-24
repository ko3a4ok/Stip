package io.ololo.stip.fragments;

import android.annotation.TargetApi;
import android.app.Activity;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class InventoryFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String token;

    private List<Map<String, String>> data = new ArrayList();

    private OnFragmentInteractionListener mListener;

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
    public static InventoryFragment newInstance(String param1, String param2) {
        InventoryFragment fragment = new InventoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public InventoryFragment() {
    }
    ImageLoader imageLoader;
    private static final String[] FROM = {"id", "name", "logo", "quantity", "data", "add"};
    private static final int[] TO = {R.id.inventory_id, R.id.inventory_title, R.id.inventory_logo, R.id.inventory_quantity, R.id.inventory_data, R.id.inventory_add};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        token = ((StipApplication) getActivity().getApplication()).getToken();
        // TODO: Change Adapter to display your content
        adapter = new SimpleAdapter(getActivity(), data, R.layout.inventory_item, FROM, TO);
    }

    private SimpleAdapter.ViewBinder viewBinder = new SimpleAdapter.ViewBinder() {
        @Override
        public boolean setViewValue(View view, Object data, String textRepresentation) {
            if (view.getId() == R.id.inventory_logo) {
                ((NetworkImageView)view).setDefaultImageResId(R.drawable.default_inventory);
                ((NetworkImageView)view).setImageUrl((String) data, imageLoader);
                return true;
            }
            if (view.getId() == R.id.inventory_add) {
                view.setTag(data);
                view.setOnClickListener(onAddQuantityListener);
                return true;
            }
            return false;
        }
    };

    private View.OnClickListener onAddQuantityListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final int pos = Integer.parseInt((String) view.getTag());
            final int q = Integer.parseInt(data.get(pos).get(FROM[3]).split(" ")[1]);
            data.get(pos).put(FROM[3], getString(R.string.quanity_pattern, q + 1));
            String id = data.get(pos).get(FROM[0]);
            JSONObject o = new JSONObject();
            try {
                o.put("_id", id);
                o.put("quantity", q+1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            adapter.notifyDataSetChanged();
            Volley.newRequestQueue(getActivity()).add(new StipRequest(Request.Method.PUT, StipRequest.THINGS + id, o, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getActivity(), R.string.error_add, Toast.LENGTH_SHORT).show();
                    data.get(pos).put(FROM[3], "Quantity: " + (q));
                    adapter.notifyDataSetChanged();
                }
            }, token));
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
        final View view = inflater.inflate(R.layout.fragment_item, container, false);
        ((EditText)view.findViewById(R.id.search)).addTextChangedListener(searchTextWatcher);
        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        initImageLoader();
        mListView.setAdapter(adapter);
        adapter.setViewBinder(viewBinder);
        // Set OnItemClickListener so we can be notified on item clicks
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().showDialog(MainActivity.DIALOG_LOADING);
        Volley.newRequestQueue(getActivity()).add(new StipArrayRequest(Request.Method.GET, StipRequest.THINGS,
                null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray array) {
                mListView.setEmptyView(getView().findViewById(android.R.id.empty));
                getActivity().dismissDialog(MainActivity.DIALOG_LOADING);
                data.clear();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject o = array.optJSONObject(i);
                    Map<String, String> map = new HashMap();
                    map.put(FROM[0], o.optString("_id"));
                    map.put(FROM[1], o.optString("name"));
                    map.put(FROM[2], o.optString("image"));
                    map.put(FROM[3], getString(R.string.quanity_pattern, o.optInt("quantity")));
                    map.put(FROM[4], o.toString());
                    map.put(FROM[5], "" + i);
                    data.add(map);
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

}
