package com.mangu.transfer.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.mangu.transfer.R;
import com.mangu.transfer.adapter.CustomListAdapterCharityProject;
import com.mangu.transfer.db.AppControllerList;
import com.mangu.transfer.db.SessionManager;
import com.mangu.transfer.model.Custom_list_model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Activity_charity_project_list extends AppCompatActivity {

    private static final String TAG = Activity_charity_project_list.class.getSimpleName();
    private static final String url = "https://mangumangu.com/json/CharityOrganizations.aspx";
    private static SessionManager session;
    String mOrganisationName, mCharity_Activity;
    int m_organizationId = 0, m_senderId = 0;
    TextView m_txt_instruction;
    String mDescription = "";
    String project_desc = "", project_name = "", amount_needed = "", amount_collected = "", amount_balance = "", ogranisation_id = "",
            phone_number = "";
    Dialog dialog;
    private ProgressDialog pDialog;
    private List<Custom_list_model> charityProjectList = new ArrayList<Custom_list_model>();
    private ListView listView;
    private CustomListAdapterCharityProject adapter;
    private TextView emptyView;

    public Activity_charity_project_list() {
        dialog = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charity_project_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        emptyView = findViewById(R.id.empty_view);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Charity Projects");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        listView = findViewById(R.id.list);
        adapter = new CustomListAdapterCharityProject(this, charityProjectList);
        listView.setAdapter(adapter);
        session = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        m_txt_instruction = findViewById(R.id.txt_instruction);

        //  m_txt_instruction.setText("Click on an organisation you want to send money");


        // name

        String m_senderParam = user.get(SessionManager.KEY_SENDER_ID);
        String username = user.get(SessionManager.KEY_EMAIL);

        m_senderId = Integer.valueOf(m_senderParam);

        Activity_charity_project_list activity = Activity_charity_project_list.this;
        //String data= activity.getIntent().getExtras().getString("username");
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.description);
        dialog.setTitle("Pay or Delete");


        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                        TextView txt_org_id = view.findViewById(R.id.id);
                        TextView txt_project_name = view.findViewById(R.id.title);
                        TextView txtamount = view.findViewById(R.id.additional_text);

                        TextView description = view.findViewById(R.id.details);
                        TextView txt_phone_number = view.findViewById(R.id.description);
                        ogranisation_id = txt_org_id.getText().toString();
                        project_name = txt_project_name.getText().toString();
                        project_desc = description.getText().toString();
                        phone_number = txt_phone_number.getText().toString();


                        Intent intent = new Intent(Activity_charity_project_list.this, Activity_project_description.class);

                        intent.putExtra("OrganizationID", m_organizationId);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        finish();
                        startActivity(intent);

                    }
                }
        );
        // Creating volley request obj
        JsonArrayRequest projectReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());
                if (response.toString().isEmpty()) {
                    hidePDialog();
                    emptyView.setVisibility(View.VISIBLE);
                }

                // Parsing json
                for (int i = 0; i < response.length(); i++) {
                    try {

                        JSONObject obj = response.getJSONObject(i);
                        Custom_list_model custom_list_model = new Custom_list_model();
                        custom_list_model.setTitle(obj.getString("Charity_Activity"));
                        custom_list_model.setImplementer(obj.getString("Description"));
                        custom_list_model.setDescription(obj.getString("Organization_name"));
                        custom_list_model.setAdditional_info(obj.getString("Primary_Phone"));
                        custom_list_model.setM_id(obj.getString("OrganizationID"));
                        hidePDialog();

                        mOrganisationName = obj.getString("Organization_name");
                        m_organizationId = obj.getInt("OrganizationID");
                        mDescription = obj.getString("Description");
                        charityProjectList.add(custom_list_model);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                // notifying list adapter about data changes
                // so that it renders the list view with updated data
                adapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();

            }
        });

        // Adding request to request queue
        AppControllerList.getInstance().addToRequestQueue(projectReq);
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
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();

        Intent intent = new Intent(Activity_charity_project_list.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(Activity_charity_project_list.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.open_main, R.anim.close_next);
                finish();
                return true;
        }
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.statement4people) {
            Intent intent = new Intent(Activity_charity_project_list.this, Activity_statement_4_people.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            overridePendingTransition(R.anim.open_main, R.anim.close_next);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.bank_statement) {
            Intent intent = new Intent(Activity_charity_project_list.this, Activity_bank_statement.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            overridePendingTransition(R.anim.open_main, R.anim.close_next);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.charity_statement) {
            Intent intent = new Intent(Activity_charity_project_list.this, Activity_charity_statement.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            overridePendingTransition(R.anim.open_main, R.anim.close_next);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.bank_statement) {
            Intent intent = new Intent(Activity_charity_project_list.this, Activity_bank_statement.class);
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

}



