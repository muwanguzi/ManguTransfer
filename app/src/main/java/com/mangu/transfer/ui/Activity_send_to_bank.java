package com.mangu.transfer.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
import com.mangu.transfer.adapter.CustomHttpConnection;
import com.mangu.transfer.db.Constant;
import com.mangu.transfer.db.SessionManager;
import com.mangu.transfer.util.ServiceHandler;
import com.mangu.transfer.util.UserCountry;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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

public class Activity_send_to_bank extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Spinner spinner;
    UserCountry cat;
    //EditText mAmount;
    EditText mSecondName;
    EditText mBankAccNo;
    EditText mBankAccName;
    EditText mContactPhone;
    EditText m_receiveEditText, m_sendEditText;
    TextView mplaceholdersend, mplaceholderreceive, m_rate_message;
    Button btn_send;
    Context context;
    String mBankName = "", mBankId = "", bankname = "";
    String mUser_rate = "", mLocalCurrencyCode = "",
            mForeignCurrencyCode = "", mLocalCurrencySign = "",
            mForeignCurrencySign = "";
    String m_contactNumber = "", m_accName = "", m_accNumber = "", m_amountTransfered = "", m_transferFees = "";
    int m_senderId;
    Double forex_rate = 0.00, m_amountEntered = 0.00, m_amountCharged = 0.00, m_stringAmountCharged = 0.00, m_doubleAmountEntered = 0.00;
    String formatedRate = "", mEmail;
    JSONObject jObj;
    StringRequest stringRequest;
    SessionManager session;
    String mastercard_session_id = "";
    ArrayAdapter<String> adapter;
    String sessionId = "";
    String m_moneySenderId = "";
    String mStringmfivePercentCharge = "";
    String mastersession_id = "";
    private ProgressDialog pDialog;
    private ArrayList<UserCountry> UserCountryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_to_bank);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Send To Bank");


        session = new SessionManager(getApplicationContext());
        /** get user data from session*/
        HashMap<String, String> user = session.getUserDetails();
        String m_senderParam = user.get(SessionManager.KEY_SENDER_ID);
        mEmail = user.get(SessionManager.KEY_EMAIL);
        m_senderId = Integer.valueOf(m_senderParam);

        mBankAccNo = findViewById(R.id.acc_number);
        mBankAccName = findViewById(R.id.acc_name);
        mContactPhone = findViewById(R.id.contact_phone);
        btn_send = findViewById(R.id.send);
        m_rate_message = findViewById(R.id.error_message);


        m_sendEditText = findViewById(R.id.editText_send);
        m_receiveEditText = findViewById(R.id.editText_receive);
        mplaceholdersend = findViewById(R.id.placeholder_send);
        mplaceholderreceive = findViewById(R.id.placeholder_receive);
        spinner = findViewById(R.id.myspinner);

        new GetCountry().execute();
        getForexrate(mEmail);

        UserCountryList = new ArrayList<UserCountry>();
        spinner.setOnItemSelectedListener(this);

    }

    private void populateSpinner() {
        List<String> lables = new ArrayList<String>();

        for (int i = 0; i < UserCountryList.size(); i++) {
            lables.add(UserCountryList.get(i).getName());
        }

        // Creating adapter for spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, lables);
        // Drop down layout style - list view with radio button
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
        // attaching data adapter to spinner
        spinner.setAdapter(spinnerAdapter);
    }

    private void afterGetForex() {

        mplaceholdersend.setText(mLocalCurrencyCode);
        mplaceholderreceive.setText(mForeignCurrencyCode);

        BigDecimal mBigmrate = new BigDecimal(forex_rate, MathContext.DECIMAL64);
        DecimalFormat fmt = new DecimalFormat("#,###.##");
        formatedRate = fmt.format(mBigmrate);

        m_rate_message.setText("The Amount sent includes transfer charges" + '\n' + "The exchange rate is " + mLocalCurrencyCode + "1 =  " + mForeignCurrencyCode + " " + formatedRate);

        /**pass contact  variable to an arrayList
         */
        if (forex_rate > 0) {


            if (mForeignCurrencyCode.equals("UGX")) {
                m_sendEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before,
                                              int count) {
                        try {
                            Double s1 = 0.0;
                            s1 = Double.parseDouble(s.toString());
                            Double short_temp = (Math.floor(m_amountEntered * 100) / 100);
                            //if text in box is just the short version
                            if (short_temp.doubleValue() != s1.doubleValue()) {
                                m_amountEntered = s1;
                            }

                        } catch (Exception e) {

                        }

                        if (m_amountEntered.doubleValue() != 0) {
                            try {
                                Double m_amountLimit = 4000000.00;
                                if (m_amountEntered.doubleValue() * forex_rate <= m_amountLimit) {
                                    /** setting charges */
                                    m_amountCharged = (m_amountEntered * forex_rate);
                                    //get the previous converted data
                                    Double previous_convert = m_stringAmountCharged / forex_rate;
                                    double amtchar = (m_amountEntered * forex_rate) + (m_amountEntered * forex_rate) * 0.05;
                                    DecimalFormat fmt1 = new DecimalFormat("#,###.##");
                                    double mStringmAmt_to_deducted = (Math.floor(amtchar * 100) / 100);
                                    m_rate_message.setText("I am sending: " + " " + mLocalCurrencyCode + " " + fmt1.format(m_amountEntered) + " " +
                                            '\n' + '\n' + "The exchange rate is " + mLocalCurrencyCode + "1 = " + formatedRate + " " + mForeignCurrencyCode +
                                            '\n' + '\n' + "Total Remittance: " + "" + mForeignCurrencyCode + " " + mStringmAmt_to_deducted);


                                    //incse changed by another controll
                                    if (previous_convert.doubleValue() != m_amountEntered.doubleValue()) {

                                        m_stringAmountCharged = m_amountCharged;

                                        Double temp_str = (Math.floor(m_stringAmountCharged * 100) / 100);
                                        //m_receiveEditText.setText(temp_str.toString());


                                        BigDecimal mBigtemp_str = new BigDecimal(temp_str, MathContext.DECIMAL64);
                                        DecimalFormat fmtstr = new DecimalFormat("#,###.##");
                                        String formatedmBigtemp_str = fmtstr.format(mBigtemp_str);
                                        m_receiveEditText.setText(formatedmBigtemp_str);

                                    }

                                } else {
                                    //reverse to previous figure
                                    //get the previous converted data
                                    String m_amountLimitx = "4,000,000";
                                    m_amountEntered = m_stringAmountCharged / forex_rate;

                                    Double temp_str = (Math.floor(m_amountEntered * 100) / 100);
                                    BigDecimal mBigtemp_str = new BigDecimal(temp_str, MathContext.DECIMAL64);
                                    DecimalFormat fmt = new DecimalFormat("#,###.##");
                                    String formatedmBigtemp_str = fmt.format(mBigtemp_str);
                                    m_sendEditText.setText(formatedmBigtemp_str);
                                    Toast.makeText(getBaseContext(), "Amount can not Exceed " + m_amountLimitx + " " + mForeignCurrencyCode, Toast.LENGTH_LONG).show();


                                }
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                //Toast.makeText(getApplicationContext(),error.getMessage(), Toast.LENGTH_LONG).show();
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "this a null pointer issue", Toast.LENGTH_LONG).show();
                            }
                        }

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {


                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // TODO Auto-generated method stub
                    }
                });

                m_receiveEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before,
                                              int count) {

                        DecimalFormat fmt = new DecimalFormat("#.##");
                        try {
                            Double s1 = 0.0;
                            s1 = Double.parseDouble(s.toString());

                            Double short_temp = (Math.floor(m_stringAmountCharged * 100) / 100);
                            //if text in box is just the short version
                            if (short_temp.doubleValue() != s1.doubleValue()) {
                                m_stringAmountCharged = s1;
                            }


                        } catch (Exception e) {

                        }

                        if (m_stringAmountCharged.doubleValue() != 0) {
                            try {

                                if (m_stringAmountCharged.doubleValue() <= 4000000.00) {
                                    /** setting charges */
                                    m_doubleAmountEntered = (m_stringAmountCharged / forex_rate);

                                    //get the previous converted data
                                    Double previous_convert = m_amountEntered * forex_rate;

                                    if (previous_convert.doubleValue() != m_stringAmountCharged.doubleValue()) {

                                        m_amountEntered = m_doubleAmountEntered;
                                        Double temp_str = (Math.floor(m_amountEntered * 100) / 100);

                                        BigDecimal mBigtemp_str = new BigDecimal(temp_str, MathContext.DECIMAL64);
                                        DecimalFormat fmtstr = new DecimalFormat("#,###.##");
                                        String formatedmBigtemp_str = fmtstr.format(mBigtemp_str);
                                        m_sendEditText.setText(formatedmBigtemp_str);

                                    }
                                    //rate_message.setText("You are sending" + " " + roundedAmt + " " + "at a rate of " + m_rate);
                                    //charge_info.setText("This includes all transfer charges");

                                } else {
                                    /**
                                     * open alter box for sending more that the maximum amount
                                     */

                                    String DollarAmountLimit = "4,000,000";

                                    Toast.makeText(getBaseContext(), "Amount can not Exceed " + DollarAmountLimit, Toast.LENGTH_LONG).show();

                                    //reverse to previous figure
                                    //get the previous converted data
                                    m_stringAmountCharged = m_amountEntered * forex_rate;

                                    Double temp_str = (Math.floor(m_stringAmountCharged * 100) / 100);

                                    m_amountCharged = temp_str;
                                    m_receiveEditText.setText(temp_str.toString());

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {

                        }


                    }

                    @Override
                    public void afterTextChanged(Editable editable) {


                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // TODO Auto-generated method stub

                    }
                });
            } else if (mForeignCurrencyCode.equals("USD")) {

                m_sendEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before,
                                              int count) {
                        try {
                            Double s1 = 0.0;
                            s1 = Double.parseDouble(s.toString());
                            Double short_temp = (Math.floor(m_amountEntered * 100) / 100);
                            //if text in box is just the short version
                            if (short_temp.doubleValue() != s1.doubleValue()) {
                                m_amountEntered = s1;
                            }

                        } catch (Exception e) {

                        }

                        if (m_amountEntered.doubleValue() != 0) {
                            try {
                                Double m_amountLimit = 1000.00;
                                if (m_amountEntered.doubleValue() * forex_rate <= m_amountLimit) {
                                    /** setting charges */
                                    m_amountCharged = (m_amountEntered * forex_rate);
                                    //get the previous converted data
                                    Double previous_convert = m_stringAmountCharged / forex_rate;
                                    double amtchar = (m_amountEntered * forex_rate) + (m_amountEntered * forex_rate) * 0.05;
                                    DecimalFormat fmt1 = new DecimalFormat("#,###.##");
                                    double mStringmAmt_to_deducted = (Math.floor(amtchar * 100) / 100);
                                    m_rate_message.setText("I am sending: " + " " + mLocalCurrencyCode + " " + fmt1.format(m_amountEntered) + " " +
                                            '\n' + '\n' + "The exchange rate is " + mLocalCurrencyCode + "1 = " + formatedRate + " " + mForeignCurrencyCode +
                                            '\n' + '\n' + "Total Remittance: " + "" + mForeignCurrencyCode + " " + mStringmAmt_to_deducted);


                                    //incse changed by another controll
                                    if (previous_convert.doubleValue() != m_amountEntered.doubleValue()) {

                                        m_stringAmountCharged = m_amountCharged;

                                        Double temp_str = (Math.floor(m_stringAmountCharged * 100) / 100);
                                        //m_receiveEditText.setText(temp_str.toString());


                                        BigDecimal mBigtemp_str = new BigDecimal(temp_str, MathContext.DECIMAL64);
                                        DecimalFormat fmtstr = new DecimalFormat("#,###.##");
                                        String formatedmBigtemp_str = fmtstr.format(mBigtemp_str);
                                        m_receiveEditText.setText(formatedmBigtemp_str);

                                    }

                                } else {
                                    //reverse to previous figure
                                    //get the previous converted data
                                    String m_amountLimitx = "1,000.00";
                                    m_amountEntered = m_stringAmountCharged / forex_rate;

                                    Double temp_str = (Math.floor(m_amountEntered * 100) / 100);
                                    BigDecimal mBigtemp_str = new BigDecimal(temp_str, MathContext.DECIMAL64);
                                    DecimalFormat fmt = new DecimalFormat("#,###.##");
                                    String formatedmBigtemp_str = fmt.format(mBigtemp_str);
                                    m_sendEditText.setText(formatedmBigtemp_str);
                                    Toast.makeText(getBaseContext(), "Amount can not Exceed " + m_amountLimitx + " " + mForeignCurrencyCode, Toast.LENGTH_LONG).show();


                                }
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                                //Toast.makeText(getApplicationContext(),error.getMessage(), Toast.LENGTH_LONG).show();
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                                Toast.makeText(getApplicationContext(), "this a null pointer issue", Toast.LENGTH_LONG).show();
                            }
                        }

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {


                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // TODO Auto-generated method stub
                    }
                });

                m_receiveEditText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence s, int start, int before,
                                              int count) {

                        DecimalFormat fmt = new DecimalFormat("#.##");
                        try {
                            Double s1 = 0.0;
                            s1 = Double.parseDouble(s.toString());

                            Double short_temp = (Math.floor(m_stringAmountCharged * 100) / 100);
                            //if text in box is just the short version
                            if (short_temp.doubleValue() != s1.doubleValue()) {
                                m_stringAmountCharged = s1;
                            }


                        } catch (Exception e) {

                        }

                        if (m_stringAmountCharged.doubleValue() != 0) {
                            try {

                                if (m_stringAmountCharged.doubleValue() <= 1000.00) {
                                    /** setting charges */
                                    m_doubleAmountEntered = (m_stringAmountCharged / forex_rate);

                                    //get the previous converted data
                                    Double previous_convert = m_amountEntered * forex_rate;

                                    if (previous_convert.doubleValue() != m_stringAmountCharged.doubleValue()) {

                                        m_amountEntered = m_doubleAmountEntered;
                                        Double temp_str = (Math.floor(m_amountEntered * 100) / 100);

                                        BigDecimal mBigtemp_str = new BigDecimal(temp_str, MathContext.DECIMAL64);
                                        DecimalFormat fmtstr = new DecimalFormat("#,###.##");
                                        String formatedmBigtemp_str = fmtstr.format(mBigtemp_str);
                                        m_sendEditText.setText(formatedmBigtemp_str);

                                    }
                                    //rate_message.setText("You are sending" + " " + roundedAmt + " " + "at a rate of " + m_rate);
                                    //charge_info.setText("This includes all transfer charges");

                                } else {
                                    /**
                                     * open alter box for sending more that the maximum amount
                                     */

                                    String DollarAmountLimit = "4,000,000";

                                    Toast.makeText(getBaseContext(), "Amount can not Exceed " + DollarAmountLimit, Toast.LENGTH_LONG).show();

                                    //reverse to previous figure
                                    //get the previous converted data
                                    m_stringAmountCharged = m_amountEntered * forex_rate;

                                    Double temp_str = (Math.floor(m_stringAmountCharged * 100) / 100);

                                    m_amountCharged = temp_str;
                                    m_receiveEditText.setText(temp_str.toString());

                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        } else {

                        }


                    }

                    @Override
                    public void afterTextChanged(Editable editable) {


                    }

                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // TODO Auto-generated method stub

                    }
                });
            }
        } else {

        }


        /**
         * button to start submits the payment**/
        btn_send.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {

                    m_moneySenderId = Integer.toString(m_senderId);
                    HashMap<String, String> us = session.getTransId();
                    // name
                    sessionId = us.get(SessionManager.KEY_SESSIONID);
                    m_accName = mBankAccName.getText().toString();
                    m_accNumber = mBankAccNo.getText().toString();
                    m_contactNumber = mContactPhone.getText().toString();
                    m_amountTransfered = m_sendEditText.getText().toString();


                    double mfivePercentCharge = Double.valueOf(m_amountTransfered) * forex_rate * 0.05;
                    mStringmfivePercentCharge = String.valueOf(mfivePercentCharge);
                    m_transferFees = String.valueOf(mfivePercentCharge);

                    if (m_accNumber.isEmpty()) {
                        mBankAccNo.requestFocus();
                        mBankAccNo.setError("Please enter Bank Account Number");

                    } else if (m_contactNumber.isEmpty()) {
                        mContactPhone.requestFocus();
                        mContactPhone.setError("Please enter Phone Number");

                    } else if (m_accName.isEmpty()) {
                        mBankAccName.requestFocus();
                        mBankAccName.setError("Please enter Bank Account Name");

                    } else if (m_amountTransfered.isEmpty()) {
                        m_sendEditText.requestFocus();
                        m_sendEditText.setError("Please enter Amount");

                    } else {


                        Activity_send_to_bank.GetSessionTask task = new Activity_send_to_bank.GetSessionTask();
                        task.execute(sessionId, mForeignCurrencyCode);

                    }

                } catch (NumberFormatException e) {
                    // e.printStackace();
                }
            }
        });

    }

    public void getForexrate(final String mEmail) {


        pDialog.setMessage("Processing...");


        showDialog();

        stringRequest = new StringRequest(Request.Method.POST, Constant.RATEURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        hideDialog();
                        try {

                            jObj = new JSONObject(response);
                            mUser_rate = jObj.getString("Rate");
                            forex_rate = Double.valueOf(mUser_rate);
                            mLocalCurrencyCode = jObj.getString("currAbbrvA");
                            mForeignCurrencyCode = jObj.getString("currAbbrvB");
                            mLocalCurrencySign = jObj.getString("currCODEA");
                            mForeignCurrencySign = jObj.getString("currCODEB");
                            afterGetForex();

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
                        } else if (error instanceof ServerError) {
                            //TODO
                            Toast.makeText(getBaseContext(), context.getString(R.string.servererror),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof NetworkError) {

                            //TODO
                            Toast.makeText(getBaseContext(), context.getString(R.string.networkerror),
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
                params.put("Email", mEmail);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void getSessionId(final String sessionId, final String mForeignCurrencyCode) {


        pDialog.setMessage("Processing...");


        showDialog();

        stringRequest = new StringRequest(Request.Method.POST, Constant.GETSESSIONID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        hideDialog();
                        try {
                            JSONObject jObj = new JSONObject(response);
                            int error = jObj.getInt("ResultsID");
                            if (error == 1) {

                                mastercard_session_id = jObj.getString("Message");
                            } else {

                                Toast.makeText(getBaseContext(), "Failed to get session id",
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
                        } else if (error instanceof ServerError) {
                            //TODO
                            Toast.makeText(getBaseContext(), context.getString(R.string.servererror),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof NetworkError) {

                            //TODO
                            Toast.makeText(getBaseContext(), context.getString(R.string.networkerror),
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
                params.put("OrderID", sessionId);
                params.put("Currency", mForeignCurrencyCode);

                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

        mBankName = UserCountryList.get(position).getCategory();
        bankname = UserCountryList.get(position).getName();

        mBankId = UserCountryList.get(position).getId();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    public void payment(final String sessionId, final String m_moneySenderId, final String mBankId, final String m_contactNumber,
                        final String m_accName, final String m_accNumber, final String mStringmfivePercentCharge,
                        final String mCharge, final String m_transferFees) {


        final String m_transferFees_param = String.valueOf(m_transferFees);

        pDialog.setMessage("Processing...");

        showDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.SEND_TO_BANK,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        hideDialog();
                        try {

                            JSONObject jObj = new JSONObject(response);
                            int error = jObj.getInt("ResultsID");
                            if (error == 1) {
                                // Launch main activity

                                if (mForeignCurrencyCode.equalsIgnoreCase("UGX")) {


                                    Intent intent = new Intent(Activity_send_to_bank.this, Activity_connect_send_to_bank.class);
                                    double mDoubleAmountEntered = Double.valueOf(m_amountEntered);

                                    double mDollarAmountEntered = mDoubleAmountEntered / forex_rate;
                                    double mfivePercentCharge = mDollarAmountEntered * 0.05;
                                    String mStringmfivePercentCharge = String.valueOf(mfivePercentCharge);
                                    DecimalFormat fmt = new DecimalFormat("0.00");

                                    BigDecimal mBigmStringDollarAmountEntered = new BigDecimal((Double.valueOf(m_amountEntered) * forex_rate) + (Double.valueOf(m_amountEntered) * forex_rate) * 0.05, MathContext.DECIMAL64);

                                    String mStringAmountEntered = fmt.format(mBigmStringDollarAmountEntered);
                                    // BigDecimal mBigmStringDollarAmountEntered  = new BigDecimal(mDollarAmountEntered + mDollarAmountEntered*0.05, MathContext.DECIMAL64);
                                    //
                                    //mStringAmountEntered = fmt.format(mBigmStringDollarAmountEntered);
                                    intent.putExtra("USessionID", sessionId);
                                    intent.putExtra("MoneySenderID", m_moneySenderId);
                                    intent.putExtra("PhoneNo", m_contactNumber);
                                    intent.putExtra("RecipientName", m_accName);
                                    intent.putExtra("Amount", mStringAmountEntered);
                                    intent.putExtra("Rate", forex_rate);
                                    intent.putExtra("Charge", mStringmfivePercentCharge);
                                    intent.putExtra("TransferFees", mStringmfivePercentCharge);
                                    intent.putExtra("Fcurrency", mForeignCurrencyCode);
                                    intent.putExtra("AccNo", m_accNumber);
                                    intent.putExtra("bankname", bankname);
                                    intent.putExtra("MasterSession", mastersession_id);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    finish();
                                    startActivity(intent);

                                } else if (mForeignCurrencyCode.equalsIgnoreCase("USD")) {
                                    Intent intent = new Intent(Activity_send_to_bank.this, Activity_connect_send_to_bank.class);
                                    double mDoubleAmountEntered = Double.valueOf(m_amountEntered);

                                    double mDollarAmountEntered = mDoubleAmountEntered / forex_rate;
                                    double mfivePercentCharge = mDollarAmountEntered * 0.05;
                                    String mStringmfivePercentCharge = String.valueOf(mfivePercentCharge);
                                    DecimalFormat fmt = new DecimalFormat("0.00");

                                    BigDecimal mBigmStringDollarAmountEntered = new BigDecimal((Double.valueOf(m_amountEntered) * forex_rate) + (Double.valueOf(m_amountEntered) * forex_rate) * 0.05, MathContext.DECIMAL64);
                                    String mStringAmountEntered = fmt.format(mBigmStringDollarAmountEntered);
                                    // BigDecimal mBigmStringDollarAmountEntered  = new BigDecimal(mDollarAmountEntered + mDollarAmountEntered*0.05, MathContext.DECIMAL64);
                                    //
                                    //mStringAmountEntered = fmt.format(mBigmStringDollarAmountEntered);
                                    intent.putExtra("USessionID", sessionId);
                                    intent.putExtra("MoneySenderID", m_moneySenderId);
                                    intent.putExtra("PhoneNo", m_contactNumber);
                                    intent.putExtra("RecipientName", m_accName);
                                    intent.putExtra("Amount", mStringAmountEntered);
                                    intent.putExtra("Rate", forex_rate);
                                    intent.putExtra("Charge", mStringmfivePercentCharge);
                                    intent.putExtra("TransferFees", mStringmfivePercentCharge);
                                    intent.putExtra("Fcurrency", mForeignCurrencyCode);
                                    intent.putExtra("AccNo", m_accNumber);
                                    intent.putExtra("bankname", bankname);
                                    intent.putExtra("MasterSession", mastersession_id);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    finish();
                                    startActivity(intent);

                                }


                            } else if (error == 3) {
                                // Error in processing. Get the error message
                                String msg = jObj.getString("Message");
                                Toast.makeText(getApplicationContext(),
                                        msg, Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "You transaction has failed please try again later", Toast.LENGTH_LONG).show();

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
                params.put("USessionID", sessionId);
                params.put("MoneySenderID", m_moneySenderId);
                params.put("BankID", mBankId);
                params.put("ContactPhones", m_contactNumber);
                params.put("AccountName", m_accName);
                params.put("AccountNumber", m_accNumber);
                params.put("Amount", m_amountEntered.toString());
                params.put("Charge", mCharge);
                params.put("TransferFees", mCharge);
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
                Intent intent = new Intent(Activity_send_to_bank.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                overridePendingTransition(R.anim.open_main, R.anim.close_next);
                startActivity(intent);
                return true;
        }
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.statement4people) {
            Intent intent = new Intent(Activity_send_to_bank.this, Activity_statement_4_people.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            overridePendingTransition(R.anim.open_main, R.anim.close_next);
            startActivity(intent);
            return true;
        } else if (id == R.id.charity_statement) {
            Intent intent = new Intent(Activity_send_to_bank.this, Activity_charity_statement.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            overridePendingTransition(R.anim.open_main, R.anim.close_next);
            startActivity(intent);
            return true;
        } else if (id == R.id.bank_statement) {
            Intent intent = new Intent(Activity_send_to_bank.this, Activity_bank_statement.class);
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

        Intent intent = new Intent(Activity_send_to_bank.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }

    private class GetSessionTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            pDialog = new ProgressDialog(Activity_send_to_bank.this);
            pDialog.setMessage("Processing...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("OrderID", params[0]));
            postParameters.add(new BasicNameValuePair("Currency", params[1]));

            String res = null;
            try {

                String response = null;
                response = CustomHttpConnection.executeHttpPost(Constant.GETSESSIONID, postParameters);
                res = response;
            } catch (Exception e) {
                //txt_Error.setText(e.toString());
            }
            return res;
        }//close doInBackground

        @Override
        protected void onPostExecute(String result) {


            try {

                JSONObject obj = new JSONObject(result);
                pDialog.dismiss();

                try {

                    int mres = obj.getInt("ResultsID");
                    String msg = obj.getString("Message");

                    if (mres == 1) {
                        mastersession_id = obj.getString("Message");
                        payment(sessionId, m_moneySenderId, mBankId, m_contactNumber, m_accName, m_accNumber, m_amountTransfered, mStringmfivePercentCharge, m_transferFees);


                    } else {
                        Toast.makeText(Activity_send_to_bank.this, msg,
                                Toast.LENGTH_LONG).show();

                    }
                } catch (Exception e) {
                }
            } catch (Throwable t) {
                //Log.e("My App", "Could not parse malformed JSON: \"" + json + "\"");
            }

        }//close onPostExecute


    }

    private class GetCountry extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Activity_send_to_bank.this);
            pDialog.setMessage("loading countries...");
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {


            ServiceHandler jsonParser = new ServiceHandler();
            String json = jsonParser.makeServiceCall(Constant.GET_BANK, ServiceHandler.GET);

            Log.e("Response: ", "> " + json);

            if (json != null) {
                try {

                    JSONArray country_obj = new JSONArray(json);

                    for (int i = 0; i < country_obj.length(); i++) {
                        JSONObject catObj = (JSONObject) country_obj.get(i);
                        cat = new UserCountry(catObj.getString("BankID"),
                                catObj.getString("Bank"), catObj.getString("BankCode"));
                        UserCountryList.add(cat);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("JSON Data", "Didn't receive any data from server!");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            populateSpinner();
        }

    }

}


