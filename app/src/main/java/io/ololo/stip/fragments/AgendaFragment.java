package io.ololo.stip.fragments;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
 */
public class AgendaFragment extends Fragment {
    private String token;

    private ViewPager pager;

    private List<Map<String, String>> data = new ArrayList();


    public static AgendaFragment newInstance() {
        AgendaFragment fragment = new AgendaFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AgendaFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        token = ((StipApplication) getActivity().getApplication()).getToken();
    }






    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        pager = (ViewPager) inflater.inflate(R.layout.appointments, container, false);
        running = AppointmentsListFragment.newInstance();
        completed = AppointmentsListFragment.newInstance();
        pager.setAdapter(new AppointmentsTabAdapter(getChildFragmentManager()));
        final ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        pager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        // When swiping between pages, select the
                        // corresponding tab.
                        actionBar.setSelectedNavigationItem(position);
                    }
                });
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                pager.setCurrentItem(tab.getPosition());
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
            }
        };

        actionBar.removeAllTabs();
        for (int i = 0; i < 2; i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(i == 0 ? R.string.running : R.string.completed)
                            .setTabListener(tabListener));
        }
        initData();
        return pager;
    }

    private void initData() {
        loadUser();
    }

    Map<String, JSONObject> users = new HashMap();
    Map<String, JSONObject> inventories = new HashMap();
    Map<String, JSONObject> basket = new HashMap();
    private void loadUser() {
        getActivity().showDialog(MainActivity.DIALOG_LOADING);
        Volley.newRequestQueue(getActivity()).add(new StipArrayRequest(Request.Method.GET, StipRequest.CUSTOMERS,
                null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray array) {
                updateCollection(array, users);
                loadInventories();
            }
        }, ERROR_LISTENER, token));
    }

    private void loadInventories() {
        if (getActivity() == null) return;
        Volley.newRequestQueue(getActivity()).add(new StipArrayRequest(Request.Method.GET, StipRequest.THINGS,
                null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray array) {
                updateCollection(array, inventories);
                loadBasket();
            }
        }, ERROR_LISTENER, token));
    }

    private void loadBasket() {
        if (getActivity() == null) return;
        Volley.newRequestQueue(getActivity()).add(new StipArrayRequest(Request.Method.GET, StipRequest.BASKET,
                null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray array) {
                updateCollection(array, basket);
                loadTasks();
            }
        }, ERROR_LISTENER, token));
    }
    private void loadTasks() {
        if (getActivity() == null) return;
        Volley.newRequestQueue(getActivity()).add(new StipArrayRequest(Request.Method.GET, StipRequest.TASKS,
                null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray array) {
                try {
                    getActivity().dismissDialog(MainActivity.DIALOG_LOADING);
                } catch (Exception ex) {
                }
                updateItems(array);
            }
        }, ERROR_LISTENER, token));
    }

    private void updateItems(JSONArray arr) {
        List<Map<String, String>> running = new ArrayList();
        List<Map<String, String>> completed = new ArrayList();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject o = arr.optJSONObject(i);
            Map<String, String> m = new HashMap();
            m.put("id", o.optString("_id"));
            m.put("date", getDate(o.optString("date")));
            m.put("title", o.optString("name", "Web server sucks"));
            m.put("customer_photo", users.get(o.optString("clientRef")).optString("photo"));
            m.put("customer_name", users.get(o.optString("clientRef")).optString("name"));
            m.put("customer_address", users.get(o.optString("clientRef")).optString("address"));
            m.put("data", o.toString());
            (TextUtils.equals("complete", o.optString("status")) ? completed: running).add(m);
        }
        System.err.println(">!!< " + this.running + " " + this.completed);
        this.running.setData(running);
        this.completed.setData(completed);
    }

    SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("KK:mm a");
    SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy");
    private String getDate(String src) {

        try {
            Date d = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(src);
            return TIME_FORMAT.format(d) + "\n" + DATE_FORMAT.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void updateCollection(JSONArray arr, Map<String, JSONObject> map) {
        for (int i = 0; i < arr.length(); i++)
            map.put(arr.optJSONObject(i).optString("_id"), arr.optJSONObject(i));
    }

    private final Response.ErrorListener ERROR_LISTENER = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            System.err.println("error = " + error);
            try {
                getActivity().dismissDialog(MainActivity.DIALOG_LOADING);
            } catch (Exception ex) {
            }
            Toast.makeText(getActivity(), R.string.error_loading, Toast.LENGTH_LONG).show();
        }
    };
    private AppointmentsListFragment running;
    private AppointmentsListFragment completed;

    public void setFilterText(String query) {
        running.adapter.getFilter().filter(query);
        completed.adapter.getFilter().filter(query);
    }

    class AppointmentsTabAdapter extends FragmentPagerAdapter {

        public AppointmentsTabAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return position == 0 ? running : completed;
        }

        @Override
        public int getCount() {
            return 2;
        }

    }



}
