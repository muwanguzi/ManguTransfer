package com.mangu.transfer.ui;

import android.app.Dialog;
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
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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


public class Activity_bank_statement extends AppCompatActivity {

    private static final String url = Constant.BANKSTATEMENT;
    private static int m_senderId;
    final Context context = this;
    JSONObject jObj;
    StringRequest stringRequest;
    String mLocalCurrencyCode = "", mForeignCurrencyCode = "",
            mLocalCurrencySign = "", mForeignCurrencySign = "";
    Double forex_rate = 0.0;
    String m_senderIdParam = "";
    String formatedRate = "", mUser_rate = "";
    String BankName = "";
    String mStringAmount = "";
    String m_senderParam = "";
    double amountwithcharges = 0.00;
    String amountwithchargesString = "";
    String TransactionID = "";
    String sessionId = "";
    TextView textView;
    String stringAccName = "";
    String stringAccNo = "";
    TextView datetextview;
    private ProgressDialog pDialog;
    private List<Custom_list_model> FarmProjectList = new ArrayList<Custom_list_model>();
    private ListView listView;
    private CustomListAdapter adapter;
    private TextView emptyView;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_statement);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

        m_senderIdParam = user.get(SessionManager.KEY_SENDER_ID);
        String username = user.get(SessionManager.KEY_EMAIL);

        getForexrate(username);

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.proceed_dialog);
        dialog.setTitle("Pay or Delete");

        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                        textView = view.findViewById(R.id.id);
                        TextView txtorg = view.findViewById(R.id.title);
                        TextView txtamount = view.findViewById(R.id.additional_text);

                        TextView txtAccNo = view.findViewById(R.id.details);
                        TextView txtAccName = view.findViewById(R.id.description);
                        TransactionID = textView.getText().toString();
                        BankName = txtorg.getText().toString();
                        mStringAmount = txtamount.getText().toString();
                        stringAccName = txtAccName.getText().toString();
                        stringAccNo = txtAccNo.getText().toString();
                        datetextview = view.findViewById(R.id.date);

                        datetextview.setVisibility(View.VISIBLE);
                        //  String tex   = datetextview.getText().toString();

                        String amount_to_split = mStringAmount;
                        String[] split = amount_to_split.split("\\s+");
                        String currency = split[0];
                        String mStringAmount = split[1];
                        mStringAmount = mStringAmount.replace(",", "");


                        amountwithcharges = Double.parseDouble(mStringAmount);

                        amountwithchargesString = String.valueOf(amountwithcharges);


                        // custom dialog

                        TextView text = dialog.findViewById(R.id.text);
                        text.setText("Delete will remove your statement record from the list and pay will take you to the check ");
                        Button dialogButton = dialog.findViewById(R.id.dialogButtonPay);
                        Button dialogButtonDelete = dialog.findViewById(R.id.dialogButtonDelete);
                        // if button is clicked, close the custom dialog
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Activity_bank_statement.this, Activity_connect_send_to_bank.class);


                                intent.putExtra("RecipientName", stringAccName);
                                intent.putExtra("AccNo", stringAccNo);
                                intent.putExtra("bankname", BankName);
                                intent.putExtra("USessionID", TransactionID);
                                intent.putExtra("bankname", BankName);
                                intent.putExtra("Amount", amountwithchargesString);
                                intent.putExtra("Fcurrency", mForeignCurrencyCode);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                finish();
                                startActivity(intent);

                                dialog.dismiss();
                            }
                        });

                        dialogButtonDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                deleteListItem(TransactionID);
                                dialog.dismiss();
                            }
                        });


                        dialog.show();
                    }
                }
        );
    }

    public void deleteListItem(final String TransactionID) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.DELETESTBANKITEM,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        hideDialog();
                        try {

                            jObj = new JSONObject(response);
                            int mres = jObj.getInt("ResultsID");
                            String msg = jObj.getString("Message");

                            if (mres == 1) {
                                //navigate to Main Menu


                                Intent intent = new Intent(Activity_bank_statement.this, Activity_bank_statement.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(Activity_bank_statement.this, msg,
                                        Toast.LENGTH_LONG).show();
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
                params.put("TransactionID", TransactionID);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void getForexrate(final String mEmail) {


        stringRequest = new StringRequest(Request.Method.POST, Constant.RATEURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //pDialog.setMessage("Processing...");

                        if (response.isEmpty()) {
                            hideDialog();
                            emptyView.setVisibility(View.VISIBLE);
                        }

                        try {

                            jObj = new JSONObject(response);
                            mUser_rate = jObj.getString("Rate");
                            forex_rate = Double.valueOf(mUser_rate);
                            mLocalCurrencyCode = jObj.getString("currAbbrvA");
                            mForeignCurrencyCode = jObj.getString("currAbbrvB");
                            mLocalCurrencySign = jObj.getString("currCODEA");
                            mForeignCurrencySign = jObj.getString("currCODEB");
                            getBankStatement(m_senderIdParam);
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
                            Toast.makeText(context, context.getString(R.string.error_network_timeout),
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof NoConnectionError) {
                            //TODO
                            Toast.makeText(context, context.getString(R.string.nonetworkconection),
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof AuthFailureError) {
                            //TODO
                            Toast.makeText(context, context.getString(R.string.authentification),
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof ServerError) {
                            //TODO
                            Toast.makeText(context, context.getString(R.string.servererror),
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof NetworkError) {

                            //TODO
                            Toast.makeText(context, context.getString(R.string.networkerror),
                                    Toast.LENGTH_LONG).show();
                        } else if (error instanceof ParseError) {
                            //TODO
                            Toast.makeText(context, context.getString(R.string.parseerror),
                                    Toast.LENGTH_LONG).show();
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


    public void getBankStatement(final String m_senderIdParam) {


        pDialog.setMessage("Processing...");

        StringRequest movieReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        pDialog.setMessage("Processing...");
                        if (response.isEmpty()) {
                            hideDialog();
                            emptyView.setVisibility(View.VISIBLE);
                        }

                        try {

                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {


                                JSONObject obj = jsonArray.getJSONObject(i);
                                Custom_list_model custom_list_model = new Custom_list_model();
                                BigDecimal mBigtemp_str = new BigDecimal(obj.getString("OverallTotal"), MathContext.DECIMAL64);
                                DecimalFormat fmt = new DecimalFormat("#,###.00");
                                String formatedAmt = fmt.format(mBigtemp_str);
                                custom_list_model.setTitle(obj.getString("Bank"));
                                custom_list_model.setDescription(obj.getString("AccountName"));
                                custom_list_model.setAdditional_info(mForeignCurrencyCode + " " + formatedAmt);
                                custom_list_model.setOther_details(obj.getString("OrderStatus"));
                                custom_list_model.setM_id(obj.getString("TransactionID"));
                                custom_list_model.setImplementer(obj.getString("AccountNumber"));
                                custom_list_model.setDate(obj.getString("DateTransfered"));
                                hideDialog();
                                // adding charityOrganisation to movies array
                                FarmProjectList.add(custom_list_model);

                                adapter.notifyDataSetChanged();
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
                            Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.error_network_timeout),
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
                params.put("MoneySenderID", m_senderIdParam);
                return params;
            }

        };
        AppControllerList.getInstance().addToRequestQueue(movieReq);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hideDialog();
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
        Intent intent = new Intent(Activity_bank_statement.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
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
                Intent intent = new Intent(Activity_bank_statement.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.open_main, R.anim.close_next);
                finish();
                return true;
        }
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.statement4people) {
            Intent intent = new Intent(Activity_bank_statement.this, Activity_statement_4_people.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            overridePendingTransition(R.anim.open_main, R.anim.close_next);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.bank_statement) {
            Intent intent = new Intent(Activity_bank_statement.this, Activity_bank_statement.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            overridePendingTransition(R.anim.open_main, R.anim.close_next);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.charity_statement) {
            Intent intent = new Intent(Activity_bank_statement.this, Activity_charity_statement.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            overridePendingTransition(R.anim.open_main, R.anim.close_next);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.bank_statement) {
            Intent intent = new Intent(Activity_bank_statement.this, Activity_bank_statement.class);
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

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
