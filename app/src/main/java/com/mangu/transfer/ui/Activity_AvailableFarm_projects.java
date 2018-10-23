package com.mangu.transfer.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mangu.transfer.R;
import com.mangu.transfer.adapter.CustomListAdapterAvailablepjt;
import com.mangu.transfer.db.AppControllerList;
import com.mangu.transfer.db.Constant;
import com.mangu.transfer.db.SessionManager;
import com.mangu.transfer.model.Custom_list_model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Activity_AvailableFarm_projects extends AppCompatActivity {

    private static final String TAG = Activity_AvailableFarm_projects.class.getSimpleName();

    // Movies json url
    private static final String url = Constant.PROJECTURL;
    //final Context context = this;
    static int m_senderId;
    private static SessionManager session;
    final Context context = getBaseContext();
    int m_moneySenderId, mprojectId;
    String mProjectName = "", mProjectDescription = "";
    double mMonthlyRemittance = 0.0, forex_rate = 0.0;
    String mStringemittance, mStringeProjectId;
    Button m_btn_project;
    String t_id = "";
    String mUser_rate = "", mLocalCurrencyCode = "",
            mForeignCurrencyCode = "", mLocalCurrencySign = "",
            mForeignCurrencySign = "";
    TextView txtv_Title;
    String title = "";
    String m_email;
    Button btnFpass;
    TextView Email;
    TextView txtvDesc;
    String desc = "";
    TextView txtid;
    private ProgressDialog pDialog;
    private List<Custom_list_model> FarmProjectList = new ArrayList<>();
    private ListView listView;
    private CustomListAdapterAvailablepjt adapter;
    private TextView m_txt_instruction, emptyView;
    private Handler handler;
    private Runnable runnable;
    private TextView txtDay, txtHour, txtMinute, txtSecond;
    private TextView tvEventStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__available_farm_projects);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle("Available Projects");
        getSupportActionBar().setSubtitle(Html.fromHtml("<font color='#e8ecfa'>Please Subscribe</font>"));

        emptyView = findViewById(R.id.empty_view);
        listView = findViewById(R.id.list);
        adapter = new CustomListAdapterAvailablepjt(this, FarmProjectList);
        listView.setAdapter(adapter);
        m_txt_instruction = findViewById(R.id.txt_instruction);


        txtDay = findViewById(R.id.days);
        txtHour = findViewById(R.id.hours);
        txtMinute = findViewById(R.id.minute);
        txtSecond = findViewById(R.id.seconds);


        m_txt_instruction.setText("To subscribe to a project click on it");
        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();
        session = new SessionManager(getApplicationContext());
        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // name

        String m_senderParam = user.get(SessionManager.KEY_SENDER_ID);
        String username = user.get(SessionManager.KEY_EMAIL);

        m_senderId = Integer.valueOf(m_senderParam);

        countDownStart();
        // Creating volley request obj
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                txtv_Title = view.findViewById(R.id.title);
                title = txtv_Title.getText().toString();

                txtvDesc = view.findViewById(R.id.details);
                desc = txtvDesc.getText().toString();
                txtid = view.findViewById(R.id.id);
                t_id = txtid.getText().toString();

                dialogContinue();


            }
        });

        Button fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),
                        Activity_my_farm_project.class);
                intent.putExtra("mcurrency", mForeignCurrencyCode);

                intent.putExtra("mManguProjectMemberID", t_id);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                finish();
                startActivity(intent);
            }
        });

        getForexrate(username);


    }

    public void countDownStart() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1000);
                try {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(
                            "yyyy-MM-dd");
                    // Please here set your event date//YYYY-MM-DD
                    Date futureDate = dateFormat.parse("2018-5-30");
                    Date currentDate = new Date();
                    if (!currentDate.after(futureDate)) {
                        long diff = futureDate.getTime()
                                - currentDate.getTime();
                        long days = diff / (24 * 60 * 60 * 1000);
                        diff -= days * (24 * 60 * 60 * 1000);
                        long hours = diff / (60 * 60 * 1000);
                        diff -= hours * (60 * 60 * 1000);
                        long minutes = diff / (60 * 1000);
                        diff -= minutes * (60 * 1000);
                        long seconds = diff / 1000;
                        txtDay.setText("" + String.format("%02d", days));
                        txtHour.setText("" + String.format("%02d", hours));
                        txtMinute.setText(""
                                + String.format("%02d", minutes));
                        txtSecond.setText(""
                                + String.format("%02d", seconds));
                    } else {
                        tvEventStart.setVisibility(View.VISIBLE);
                        tvEventStart.setText("The event started!");

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 1 * 1000);
    }


    public void getForexrate(final String mEmail) {
        //pDialog.setMessage("Processing...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.RATEURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideDialog();
                        try {
                            JSONObject jObj = new JSONObject(response);
                            mUser_rate = jObj.getString("Rate");
                            forex_rate = Double.valueOf(mUser_rate);
                            mLocalCurrencyCode = jObj.getString("currAbbrvA");
                            mForeignCurrencyCode = jObj.getString("currAbbrvB");
                            mLocalCurrencySign = jObj.getString("currCODEA");
                            mForeignCurrencySign = jObj.getString("currCODEB");
                            AfterGetCurrency();
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError) {
                            Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.error_network_timeout),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof NoConnectionError) {
                            //TODO
                            Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.nonetworkconection),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof AuthFailureError) {
                            //TODO
                            Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.authentification),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof ServerError) {
                            //TODO
                            Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.servererror),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof NetworkError) {

                            //TODO
                            Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.networkerror),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof ParseError) {
                            //TODO
                            Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.parseerror),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Email", mEmail);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void dialogContinue() {
        if (mForeignCurrencyCode.equalsIgnoreCase("UGX")) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
            builder.setTitle(title);
            builder.setMessage(desc);
            builder.setPositiveButton("ADD TO MY PROJECT", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Activity_AvailableFarm_projects.this,
                            Activity_my_farm_project.class);
                    intent.putExtra("mManguProjectMemberID", t_id);
                    intent.putExtra("mcurrency", mForeignCurrencyCode);
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
        } else if (mForeignCurrencyCode.equalsIgnoreCase("USD")) {
            getFarmProjectUSD();
        } else {

        }
    }

    public void AfterGetCurrency() {
        if (mForeignCurrencyCode.equalsIgnoreCase("UGX")) {
            getFarmProjectUGX();
        } else if (mForeignCurrencyCode.equalsIgnoreCase("USD")) {
            getFarmProjectUSD();
        } else {

        }
    }

    public void getFarmProjectUSD() {
        // pDialog.setMessage("Processing...");
        StringRequest movieReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.isEmpty()) {
                            hideDialog();
                            emptyView.setVisibility(View.VISIBLE);
                        }
                        try {

                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);

                                Custom_list_model custom_list_model = new Custom_list_model();

                                custom_list_model.setThumbnailUrl("https://mangumangu.com/ManguProjects/i/" + obj.getString("ProjectPhoto"));
                                custom_list_model.setTitle("Project: " + obj.getString("ManguProjectName"));
                                custom_list_model.setImplementer(obj.getString("ManguProjectDescription"));
                                custom_list_model.setDescription("Expected Returns: " + mForeignCurrencyCode + " " + obj.getString("ExpectedReturn"));
                                custom_list_model.setAdditional_info("Monthly Charge:   " + mForeignCurrencyCode + " " + obj.getString("USD"));
                                custom_list_model.setOther_details(obj.getString("ProjectPeriod"));
                                custom_list_model.setM_id(obj.getString("ManguProjectMemberID"));
                                hideDialog();

                                  /*farmToHire.setm_manguProjectName(obj.getString("ManguProjectName"));
                                    farmToHire.setm_manguProjectDescription(obj.getString("ManguProjectDescription"));
                                    farmToHire.setExpectedReturn(obj.getString("ExpectedReturn"));
                                    farmToHire.setM_projectPeriod(obj.getString("ProjectPeriod"));
                                    farmToHire.setTitle(obj.getString("ManguProjectName"));
                                    farmToHire.setm_monthlyRemittance(obj.getString("MonthlyRemittance"));
                                  */


                                mProjectName = obj.getString("ManguProjectName");
                                mProjectDescription = obj.getString("ManguProjectDescription");


                                //Double mRemittance = mMonthlyRemittance;
                                mStringemittance = Double.valueOf(mMonthlyRemittance).toString();
                                mStringeProjectId = Integer.valueOf(mprojectId).toString();
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
                params.put("DataType", "ManguProjects");
                return params;
            }

        };
        AppControllerList.getInstance().addToRequestQueue(movieReq);
    }

    public void getFarmProjectUGX() {
        //showDialog();
        StringRequest movieReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.isEmpty()) {
                            hideDialog();
                            emptyView.setVisibility(View.VISIBLE);
                            LinearLayout linearLayout = findViewById(R.id.linearLayout);
                            linearLayout.setVisibility(View.VISIBLE);
                        }
                        try {

                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);

                                Custom_list_model custom_list_model = new Custom_list_model();

                                custom_list_model.setThumbnailUrl("https://mangumangu.com/ManguProjects/i/" + obj.getString("ProjectPhoto"));
                                custom_list_model.setTitle("Project: " + obj.getString("ManguProjectName"));
                                custom_list_model.setImplementer(obj.getString("ManguProjectDescription"));
                                //custom_list_model.setDescription("Expected Returns:  "+obj.getString("ExpectedReturn"));
                                //custom_list_model.setAdditional_info("Monthly Charge:    "+obj.getString("UGX"));
                                //custom_list_model.setOther_details(obj.getString("ProjectPeriod"));
                                //custom_list_model.setM_id(obj.getString("ManguProjectID"));
                                hideDialog();


                                mProjectName = obj.getString("ManguProjectName");
                                mProjectDescription = obj.getString("ManguProjectDescription");


                                //Double mRemittance = mMonthlyRemittance;
                                mStringemittance = Double.valueOf(mMonthlyRemittance).toString();
                                mStringeProjectId = Integer.valueOf(mprojectId).toString();
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
                params.put("DataType", "ManguProjects");
                return params;
            }

        };
        AppControllerList.getInstance().addToRequestQueue(movieReq);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();

        Intent intent = new Intent(Activity_AvailableFarm_projects.this, MainActivity.class);
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

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
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
                Intent intent = new Intent(Activity_AvailableFarm_projects.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.open_main, R.anim.close_next);
                finish();
                return true;
        }
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.statement4people) {
            Intent intent = new Intent(Activity_AvailableFarm_projects.this, Activity_statement_4_people.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            overridePendingTransition(R.anim.open_main, R.anim.close_next);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.bank_statement) {
            Intent intent = new Intent(Activity_AvailableFarm_projects.this, Activity_bank_statement.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            overridePendingTransition(R.anim.open_main, R.anim.close_next);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.charity_statement) {
            Intent intent = new Intent(Activity_AvailableFarm_projects.this, Activity_charity_statement.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            overridePendingTransition(R.anim.open_main, R.anim.close_next);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.bank_statement) {
            Intent intent = new Intent(Activity_AvailableFarm_projects.this, Activity_bank_statement.class);
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
