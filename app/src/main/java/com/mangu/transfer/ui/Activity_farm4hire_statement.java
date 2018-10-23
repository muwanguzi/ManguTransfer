package com.mangu.transfer.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mangu.transfer.R;
import com.mangu.transfer.adapter.CustomListAdapter;
import com.mangu.transfer.db.AppControllerList;
import com.mangu.transfer.db.Constant;
import com.mangu.transfer.db.SessionManager;
import com.mangu.transfer.model.Custom_list_model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Activity_farm4hire_statement extends AppCompatActivity {
    private static final String url = Constant.FARM4HIRESTATEMENT;
    private static int m_senderId;
    final Context context = this;
    private ProgressDialog pDialog;
    private List<Custom_list_model> FarmProjectList = new ArrayList<Custom_list_model>();
    private ListView listView;
    private CustomListAdapter adapter;
    private TextView emptyView;

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm4hire_statement);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Farm Statement");
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        emptyView = findViewById(R.id.empty_view);
        listView = findViewById(R.id.list);
        adapter = new CustomListAdapter(this, FarmProjectList);
        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();


        session = new SessionManager(getApplicationContext());

        HashMap<String, String> user = session.getUserDetails();

        // name

        String m_senderIdParam = user.get(SessionManager.KEY_SENDER_ID);
        String username = user.get(SessionManager.KEY_EMAIL);


        getFarmProject(m_senderIdParam);

    }

    public void getFarmProject(final String m_senderIdParam) {


        StringRequest mReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        pDialog.setMessage("Processing...");
                        if (response.isEmpty()) {
                            hidePDialog();
                            emptyView.setVisibility(View.VISIBLE);
                        }

                        try {

                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {


                                JSONObject obj = jsonArray.getJSONObject(i);
                                Custom_list_model custom_list_model = new Custom_list_model();
                                BigDecimal mBigtemp_str = new BigDecimal(obj.getString("AmountPaid"), MathContext.DECIMAL64);
                                DecimalFormat fmt = new DecimalFormat("0,000");
                                String formatedAmt = fmt.format(mBigtemp_str);
                                custom_list_model.setTitle(obj.getString("ManguProjectName"));
                                custom_list_model.setDescription(obj.getString("ReceivingOption"));
                                custom_list_model.setAdditional_info(formatedAmt);
                                custom_list_model.setOther_details(obj.getString("OrderStatus"));
                                hidePDialog();
                                // adding charityOrganisation to movies array
                                FarmProjectList.add(custom_list_model);
                            }

                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                        }
                        adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(context, context.getString(R.string.error_network_timeout),
                                    Toast.LENGTH_LONG).show();
                            hidePDialog();
                        } else if (error instanceof AuthFailureError) {
                            //TODO
                            Toast.makeText(context, context.getString(R.string.authentification),
                                    Toast.LENGTH_LONG).show();
                            hidePDialog();
                        } else if (error instanceof ServerError) {
                            //TODO
                            Toast.makeText(context, context.getString(R.string.servererror),
                                    Toast.LENGTH_LONG).show();
                            hidePDialog();
                        } else if (error instanceof NetworkError) {

                            //TODO
                            Toast.makeText(context, context.getString(R.string.networkerror),
                                    Toast.LENGTH_LONG).show();
                            hidePDialog();
                        } else if (error instanceof ParseError) {
                            //TODO
                            Toast.makeText(context, context.getString(R.string.parseerror),
                                    Toast.LENGTH_LONG).show();
                            hidePDialog();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("MoneySenderID", m_senderIdParam);
                return params;
            }

        };
        AppControllerList.getInstance().addToRequestQueue(mReq);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(Activity_farm4hire_statement.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                overridePendingTransition(R.anim.open_main, R.anim.close_next);
                startActivity(intent);
                return true;
        }

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.available_project) {
            Intent intent = new Intent(Activity_farm4hire_statement.this, Activity_AvailableFarm_projects.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            return true;
        } else if (id == R.id.farm_statement) {
            Intent intent = new Intent(Activity_farm4hire_statement.this, Activity_farm4hire_statement.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            return true;
        } else if (id == R.id.logout) {
            session.logoutUser();
            finish();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


}
