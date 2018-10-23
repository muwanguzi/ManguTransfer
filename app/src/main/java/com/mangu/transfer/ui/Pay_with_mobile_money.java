package com.mangu.transfer.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.mangu.transfer.R;
import com.mangu.transfer.db.Constant;
import com.mangu.transfer.db.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Pay_with_mobile_money extends AppCompatActivity {
    static String USessionID = "";
    public ProgressDialog pDialog;
    public StringRequest stringRequest;
    String phone = "", amount = "";
    Button btn_pay;
    JSONObject jObj;
    String m_contactNumber = "";
    Boolean isValid_phone;
    String st_phone_number;
    String wt3_ContactNumber = "";
    private EditText inputPhone;
    private EditText inputAmount;
    private SessionManager session;
    private String sessionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_with_mobile_money);
        //  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //  setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Pay Mobile Money");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();

        sessionId = user.get(SessionManager.KEY_SESSIONID);


        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Send For Charity");
        inputPhone = findViewById(R.id.phone_number);
        inputAmount = findViewById(R.id.amount);
        btn_pay = findViewById(R.id.pay_now);


        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            //phone = "256705303769";
            amount = extras.getString("Amount");
            sessionId = extras.getString("USessionID");

        }
        inputAmount.setEnabled(false);
        inputAmount.setText(amount);
        btn_pay.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                phone = inputPhone.getText().toString();
                amount = inputAmount.getText().toString();
                if (phone.isEmpty()) {
                    inputPhone.requestFocus();
                    inputPhone.setError("Please enter Phone");
                } else if (!isValid(phone)) {
                    inputPhone.setError("Invalid Phone");
                } else if (amount.isEmpty()) {
                    inputAmount.requestFocus();
                    inputAmount.setError("Please enter email");
                } else {


                    m_contactNumber = wt3_ContactNumber.replaceAll("[\\D]", "");


                    PaywithMobileMoney(sessionId, m_contactNumber, amount);

                }
            }

        });


    }

    private boolean isValid(String phone) {


        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

        /**Formatting the phone number **/
        try {
            Phonenumber.PhoneNumber wt2_ContactNumber = phoneUtil.parse(phone, "UG");
            /** phone  begin with '+'**/
            wt3_ContactNumber = phoneUtil.format(wt2_ContactNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);

            Phonenumber.PhoneNumber wt4_ContactNumber = phoneUtil.parse(wt3_ContactNumber, "UG");
            //m_countryCode = m_ccode.getCountryCode();
            isValid_phone = phoneUtil.isValidNumber(wt4_ContactNumber);

            String Code = String.valueOf(wt4_ContactNumber);


        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
        }

        return isValid_phone;

    }

    public void PaywithMobileMoney(final String sessionId, final String m_contactNumber, final String Amount) {
        //pDialog.setMessage("Processing...");
        showDialog();
        stringRequest = new StringRequest(Request.Method.POST, Constant.MobileMoney,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideDialog();
                        try {

                            JSONObject jObj = new JSONObject(response);
                            int msgs = jObj.getInt("ResultsID");
                            if (msgs == 1) {

                                Intent intent = new Intent(Pay_with_mobile_money.this, Activity_charity_statement.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                overridePendingTransition(R.anim.open_main, R.anim.close_next);
                                startActivity(intent);
                                finish();
                            } else {

                                Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.recordexistshere),
                                        Toast.LENGTH_LONG).show();
                            }

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

                            //TODO'
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
                params.put("USessionID", sessionId);
                params.put("PhoneNo", m_contactNumber);
                params.put("Amount", Amount);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        Intent intent = new Intent(Pay_with_mobile_money.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }

}
