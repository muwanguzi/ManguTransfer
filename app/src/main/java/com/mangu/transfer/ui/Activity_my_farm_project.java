package com.mangu.transfer.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import com.mangu.transfer.adapter.CustomListAdapterMyProjects;
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


public class Activity_my_farm_project extends AppCompatActivity {


    private static final String TAG = Activity_my_farm_project.class.getSimpleName();
    static String mStringemittance;
    private static SessionManager session;
    final Context context = this;
    int m_moneySenderId, mprojectId, m_senderId;
    double mMonthlyRemittance = 0.0;
    String mStringeProjectId, mStringprojectId, mStringSenderId;
    String mProjectName = "", mProjectDescription = "";
    Button m_btn_project;
    TextView m_txt_instruction;
    String t_id = "", mRemittance = "";
    double forex_rate = 0.0;
    String mUser_rate = "", mLocalCurrencyCode = "",
            mForeignCurrencyCode = "", mLocalCurrencySign = "",
            mForeignCurrencySign = "", mManguProjectMemberID = "";
    private ProgressDialog pDialog;
    private List<Custom_list_model> FarmProjectList = new ArrayList<Custom_list_model>();
    private ListView listView;
    private CustomListAdapterMyProjects adapter;
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_farm_project);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Projects");

        Button fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Activity_my_farm_project.this,
                        Activity_AvailableFarm_projects.class);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                finish();
                startActivity(intent);
            }
        });
        emptyView = findViewById(R.id.empty_view);
        listView = findViewById(R.id.list);


        adapter = new CustomListAdapterMyProjects(this, FarmProjectList);
        listView.setAdapter(adapter);
        //m_btn_project =(Button) findViewById(R.id.btn_project);
        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();
        session = new SessionManager(getApplicationContext());
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        m_txt_instruction = findViewById(R.id.txt_instruction);

        m_txt_instruction.setText("To pay for a project click on it");

        // name

        String m_senderParam = user.get(SessionManager.KEY_SENDER_ID);
        String username = user.get(SessionManager.KEY_EMAIL);

        m_senderId = Integer.valueOf(m_senderParam);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            mStringprojectId = extras.getString("mManguProjectMemberID");
            mForeignCurrencyCode = extras.getString("mcurrency");
        }
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from ListView
                // Launch main activity
                TextView txtv_Title = view.findViewById(R.id.title);
                final String title = txtv_Title.getText().toString();

                TextView txtvDesc = view.findViewById(R.id.description);
                String desc = txtvDesc.getText().toString();

                TextView txtId = view.findViewById(R.id.id);
                t_id = txtId.getText().toString();

                TextView txtRemittance = view.findViewById(R.id.additional_text);
                mRemittance = String.valueOf(mMonthlyRemittance);


                getSupportActionBar().setHomeButtonEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

                builder.setTitle(title);
                builder.setMessage(desc);
                builder.setPositiveButton("CONTINUE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Activity_my_farm_project.this,
                                Activity_send_to_farm_for_hire.class);
                        intent.putExtra("mManguProjectMemberID", mManguProjectMemberID);
                        intent.putExtra("mMonthlyRemittance", mRemittance);
                        intent.putExtra("project_title", title);

                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //SOME CODE
                    }
                });
                builder.show();


            }
        });


        mStringSenderId = String.valueOf(m_senderId);
        getFarmProject(mStringprojectId, mStringSenderId);
        AfterGetCurrency();
    }


    public void getFarmProject(final String mStringprojectId, final String mStringSenderId) {
        pDialog.setMessage("Processing...");
        StringRequest movieReq = new StringRequest(Request.Method.POST, Constant.ADDFARMFORHIRE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.isEmpty()) {
                            hideDialog();
                            emptyView.setVisibility(View.VISIBLE);
                        }
                        try {

                            JSONObject jObj = new JSONObject(response);
                            int code = jObj.getInt("ResultsID");

                            if (code == 0) {
                                // Launch main activity
                                Toast.makeText(getApplicationContext(),
                                        "You have already subscribed to Project", Toast.LENGTH_LONG).show();
                                hideDialog();


                            } else {
                                // Error in login. Get the error message
                                Toast.makeText(getApplicationContext(),
                                        "Your project has ben successfully added ", Toast.LENGTH_LONG).show();
                                hideDialog();
                            }

                            hideDialog();
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
                            hideDialog();
                        } else if (error instanceof AuthFailureError) {
                            //TODO
                            Toast.makeText(context, context.getString(R.string.authentification),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof ServerError) {
                            //TODO
                            Toast.makeText(context, context.getString(R.string.servererror),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof NetworkError) {

                            //TODO
                            Toast.makeText(context, context.getString(R.string.networkerror),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof ParseError) {
                            //TODO
                            Toast.makeText(context, context.getString(R.string.parseerror),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("MoneySenderID", mStringSenderId);
                params.put("ManguProjectID", mStringprojectId);
                return params;
            }

        };
        AppControllerList.getInstance().addToRequestQueue(movieReq);
    }

    public void AfterGetCurrency() {
        if (mForeignCurrencyCode.equalsIgnoreCase("UGX")) {
            getMyFarmProjectUGX(mStringSenderId);
        } else if (mForeignCurrencyCode.equalsIgnoreCase("USD")) {
            getMyFarmProjectUSD(mStringSenderId);
        } else {

        }
    }

    public void getMyFarmProjectUGX(final String mStringSenderId) {
        pDialog.setMessage("Processing...");
        StringRequest movieReq = new StringRequest(Request.Method.POST, Constant.MYFARM,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {


                                JSONObject obj = jsonArray.getJSONObject(i);
                                Custom_list_model custom_list_model = new Custom_list_model();
                                //custom_list_model.setThumbnailUrl("https://mangumangu.com/ManguProjects/i/"+obj.getString("ProjectPhoto"));
                                BigDecimal mBigtemp_str = new BigDecimal(obj.getString("UGX"), MathContext.DECIMAL64);
                                DecimalFormat fmt = new DecimalFormat("#,###");
                                String formatedAmt = fmt.format(mBigtemp_str);
                                custom_list_model.setTitle(obj.getString("ManguProjectName"));
                                custom_list_model.setDescription(obj.getString("ManguProjectDescription"));
                                custom_list_model.setAdditional_info(mForeignCurrencyCode + " " + formatedAmt);
                                custom_list_model.setOther_details(obj.getString("ProjectPeriod"));
                                custom_list_model.setM_id(obj.getString("ManguProjectMemberID"));


                                mStringemittance = Double.valueOf(mMonthlyRemittance).toString();
                                mManguProjectMemberID = obj.getString("ManguProjectMemberID");
                                mProjectName = obj.getString("ManguProjectName");
                                mProjectDescription = obj.getString("ManguProjectDescription");
                                mMonthlyRemittance = obj.getDouble("UGX");

                                mStringeProjectId = Integer.valueOf(mprojectId).toString();
                                FarmProjectList.add(custom_list_model);
                                hideDialog();
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
                            hideDialog();
                        } else if (error instanceof AuthFailureError) {
                            //TODO
                            Toast.makeText(context, context.getString(R.string.authentification),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof ServerError) {
                            //TODO
                            Toast.makeText(context, context.getString(R.string.servererror),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof NetworkError) {

                            //TODO
                            Toast.makeText(context, context.getString(R.string.networkerror),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof ParseError) {
                            //TODO
                            Toast.makeText(context, context.getString(R.string.parseerror),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("MoneySenderID", mStringSenderId);
                return params;
            }

        };
        AppControllerList.getInstance().addToRequestQueue(movieReq);
    }

    public void getMyFarmProjectUSD(final String mStringSenderId) {
        pDialog.setMessage("Processing...");
        StringRequest movieReq = new StringRequest(Request.Method.POST, Constant.MYFARM,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {


                                JSONObject obj = jsonArray.getJSONObject(i);
                                Custom_list_model custom_list_model = new Custom_list_model();
                                //custom_list_model.setThumbnailUrl("https://mangumangu.com/ManguProjects/i/"+obj.getString("ProjectPhoto"));
                                BigDecimal mBigtemp_str = new BigDecimal(obj.getString("USD"), MathContext.DECIMAL64);
                                DecimalFormat fmt = new DecimalFormat("#,###");
                                String formatedAmt = fmt.format(mBigtemp_str);
                                custom_list_model.setTitle(obj.getString("ManguProjectName"));
                                custom_list_model.setDescription(obj.getString("ManguProjectDescription"));
                                custom_list_model.setAdditional_info(mForeignCurrencyCode + " " + formatedAmt);
                                custom_list_model.setOther_details(obj.getString("ProjectPeriod"));
                                custom_list_model.setM_id(obj.getString("ManguProjectID"));


                                mStringemittance = Double.valueOf(mMonthlyRemittance).toString();

                                mProjectName = obj.getString("ManguProjectName");
                                mProjectDescription = obj.getString("ManguProjectDescription");
                                mMonthlyRemittance = obj.getDouble("USD");

                                mStringeProjectId = Integer.valueOf(mprojectId).toString();
                                FarmProjectList.add(custom_list_model);
                                hideDialog();
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
                            hideDialog();
                        } else if (error instanceof AuthFailureError) {
                            //TODO
                            Toast.makeText(context, context.getString(R.string.authentification),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof ServerError) {
                            //TODO
                            Toast.makeText(context, context.getString(R.string.servererror),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof NetworkError) {

                            //TODO
                            Toast.makeText(context, context.getString(R.string.networkerror),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof ParseError) {
                            //TODO
                            Toast.makeText(context, context.getString(R.string.parseerror),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("MoneySenderID", mStringSenderId);
                return params;
            }

        };
        AppControllerList.getInstance().addToRequestQueue(movieReq);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(Activity_my_farm_project.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.open_main, R.anim.close_next);
                return true;
        }
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.available_project) {
            Intent intent = new Intent(Activity_my_farm_project.this, Activity_AvailableFarm_projects.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            return true;
        } else if (id == R.id.farm_statement) {
            Intent intent = new Intent(Activity_my_farm_project.this, Activity_farm4hire_statement.class);
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


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();

        Intent intent = new Intent(Activity_my_farm_project.this, Activity_AvailableFarm_projects.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideDialog();
    }

    private void hideDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.farm_menu, menu);
        return true;
    }

}
