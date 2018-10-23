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
import android.widget.AdapterView;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Activity_cart_hire extends AppCompatActivity {

    private static final String TAG = Activity_cart_hire.class.getSimpleName();

    private static final String url = Constant.SESSIONCARTLIST;
    final Context context = this;
    String mCartCount = "";
    String cart_amount = "";
    String cartId;
    private ProgressDialog pDialog;
    private List<Custom_list_model> FarmProjectList = new ArrayList<Custom_list_model>();
    private ListView listView;
    private CustomListAdapter adapter;
    private SessionManager session;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_hire);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        getSupportActionBar().setTitle("Cart Hire");
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button m_master_button = findViewById(R.id.btn_continue);

        emptyView = findViewById(R.id.empty_view);
        listView = findViewById(R.id.list);
        adapter = new CustomListAdapter(this, FarmProjectList);
        session = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);

        pDialog.setMessage("Loading...");
        listView.setAdapter(adapter);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            mCartCount = extras.getString("Number_Of_Recipients");
            cart_amount = extras.getString("Amount");

        }
        HashMap<String, String> us = session.getTransId();
        // name
        String sessionId = us.get(SessionManager.KEY_SESSIONID);
        getFarmProject(sessionId);

        pDialog.show();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                String CartID = String.valueOf(position);
                HashMap<String, String> us = session.getTransId();
                String sessionId = us.get(SessionManager.KEY_SESSIONID);
                deleteList(CartID, sessionId);


            }
        });
        /** Setting the adapter to the ListView */
        View.OnClickListener continue_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                HashMap<String, String> us = session.getTransId();
                // name
                String sessionId = us.get(SessionManager.KEY_SESSIONID);

                // String mCartNumber = Integer.valueOf(mCartCount).toString();
                Intent intent = new Intent(Activity_cart_hire.this, Activity_connect_cart_hire.class);
                intent.putExtra("USessionID", sessionId);
                intent.putExtra("Number_Of_Recipients", mCartCount);
                intent.putExtra("Amount", cart_amount);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                finish();
                startActivity(intent);
            }
        };
        m_master_button.setOnClickListener(continue_listener);
    }

    public void getFarmProject(final String sessionId) {
        pDialog.setMessage("Processing...");
        StringRequest movieReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.isEmpty()) {
                            hidePDialog();
                            emptyView.setVisibility(View.VISIBLE);
                        }
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                Custom_list_model custom_list_model = new Custom_list_model();
                                custom_list_model.setTitle(obj.getString("MoneyRecipient"));
                                custom_list_model.setDescription(obj.getString("PhoneNo"));
                                custom_list_model.setOther_details("UGX " + obj.getString("Amount"));
                                custom_list_model.setAdditional_info(obj.getString("CartID"));
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
                params.put("USessionID", sessionId);
                return params;
            }
        };
        AppControllerList.getInstance().addToRequestQueue(movieReq);
    }

    public void deleteList(final String CartID, final String sessionId) {
        // pDialog.setMessage("Processing...");
        StringRequest movieReq = new StringRequest(Request.Method.POST, Constant.DELETECARTIEM,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                Custom_list_model custom_list_model = new Custom_list_model();
                                custom_list_model.setTitle(obj.getString("MoneyRecipient"));
                                custom_list_model.setDescription(obj.getString("PhoneNo"));
                                custom_list_model.setAdditional_info(obj.getString("Charge"));
                                cartId = obj.getString("CartID");
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
                params.put("USessionID", sessionId);
                params.put("CartID", CartID);
                return params;
            }
        };
        AppControllerList.getInstance().addToRequestQueue(movieReq);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /** Inflate the menu; this adds items to the action bar if it is present.
         *
         */
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
                Intent intent = new Intent(Activity_cart_hire.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.open_main, R.anim.close_next);
                finish();
                return true;
        }
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.statement4people) {
            Intent intent = new Intent(Activity_cart_hire.this, Activity_statement_4_people.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            overridePendingTransition(R.anim.open_main, R.anim.close_next);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.bank_statement) {
            Intent intent = new Intent(Activity_cart_hire.this, Activity_bank_statement.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            overridePendingTransition(R.anim.open_main, R.anim.close_next);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.charity_statement) {
            Intent intent = new Intent(Activity_cart_hire.this, Activity_charity_statement.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            overridePendingTransition(R.anim.open_main, R.anim.close_next);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.bank_statement) {
            Intent intent = new Intent(Activity_cart_hire.this, Activity_bank_statement.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            overridePendingTransition(R.anim.open_main, R.anim.close_next);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.logout) {
            session.logoutUser();
            finish();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();

        Intent intent = new Intent(Activity_cart_hire.this, Activity_send_to_farm_for_hire.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
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

}
