package com.mangu.transfer.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
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
import android.widget.ImageView;
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


public class Activity_send_to_charity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    final Context context = this;
    public ProgressDialog pDialog;
    public int to, from;
    public String s, name;
    Button m_send_button, m_btn_checkout, m_btn_cart_details, btn_trans_summary;
    ImageView dialogButtonPayWithMm;
    ImageView dialogButtonPayWithMmPayWithVisa;
    String m_moneySenderId = "";
    Button m_next_button;
    TextView m_TxtPhoneNo;
    EditText m_receiveEditText, m_sendEditText, enter_charity;
    TextView m_rate_message, m_charge_info;
    String m_rate;
    String m_organizationId = "";
    int m_senderId = 0;
    String formatedRate = "";
    String organisation_id = "";
    String sessionId = "";
    String mastersession_id = "";
    Double m_doubleAmountEntered, m_amountCharged, forex_rate = 0.00;
    String m_transferFees = "";
    Double m_stringAmountCharged = 0.00, m_amountEntered = 0.00;
    String OrganisationName;
    Spinner spinner;
    AlertDialog dialog = null;
    JSONObject jObj;
    StringRequest stringRequest;
    String mUser_rate = "", mLocalCurrencyCode = "",
            mForeignCurrencyCode = "", mLocalCurrencySign = "",
            mForeignCurrencySign = "", project_name = "";
    TextView mplaceholdersend, mplaceholderreceive;
    UserCountry cat;
    ArrayAdapter<String> adapter;
    private SessionManager session;
    private ArrayList<UserCountry> UserCountryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_to_charity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Send For Charity");

        m_next_button = findViewById(R.id.next_button);
        m_send_button = findViewById(R.id.send);
        m_btn_checkout = findViewById(R.id.viewcart);
        m_btn_cart_details = findViewById(R.id.cart_details);

        mplaceholdersend = findViewById(R.id.placeholder_send);
        mplaceholderreceive = findViewById(R.id.placeholder_receive);
        session = new SessionManager(getApplicationContext());


        dialog = new AlertDialog.Builder(this)
                .setTitle("Pay with")
                .setView(R.layout.pay_with)
                .create();

        dialog.setCancelable(false);
        UserCountryList = new ArrayList<UserCountry>();
        spinner = findViewById(R.id.myspinner);

        //get user data from session
        HashMap<String, String> user = session.getUserDetails();


        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        if (bundle != null) {

            project_name = bundle.get("CharityOrganisation").toString();
            m_organizationId = bundle.get("OrganisationId").toString();
        }

        // spinner item select listener
        spinner.setOnItemSelectedListener(this);
        spinner.setPrompt("Select Organisation");

        //name
        String m_senderParam = user.get(SessionManager.KEY_SENDER_ID);
        String mEmail = user.get(SessionManager.KEY_EMAIL);
        m_senderId = Integer.valueOf(m_senderParam);

        new GetCharityProject().execute();
        getForexrate(mEmail);
        enter_charity = findViewById(R.id.enter_charity);
        m_sendEditText = findViewById(R.id.editText_send);
        m_receiveEditText = findViewById(R.id.editText_receive);
        m_next_button = findViewById(R.id.next_button);
        m_rate_message = findViewById(R.id.rate_message);
        m_charge_info = findViewById(R.id.lbl_explainerRate);
        m_TxtPhoneNo = findViewById(R.id.enter_recipient_field);

        enter_charity.setText(project_name);
        enter_charity.setEnabled(false);

        overridePendingTransition(R.anim.open_next, R.anim.close_next);
    }

    private void afterGetForex() {

        mplaceholdersend.setText(mLocalCurrencyCode);
        mplaceholderreceive.setText(mForeignCurrencyCode);

        BigDecimal mBigmrate = new BigDecimal(forex_rate, MathContext.DECIMAL64);
        DecimalFormat fmt = new DecimalFormat("#,###.##");

        formatedRate = fmt.format(mBigmrate);

        m_rate_message.setText("I am sending: " + " " + mLocalCurrencyCode + " " + fmt.format(m_amountEntered) + " " +
                '\n' + '\n' + "The exchange rate is " + mLocalCurrencyCode + "1 = " + forex_rate + " " + mForeignCurrencyCode +
                '\n' + '\n' + "Total Remittance: " + "" + mForeignCurrencyCode + " " + m_stringAmountCharged);

        /**pass contact  variable to an arrayList
         */
        if (forex_rate > 0) {
            if (mForeignCurrencyCode.equalsIgnoreCase("UGX")) {
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
                                Toast.makeText(getBaseContext(), "this a null pointer issue", Toast.LENGTH_LONG).show();
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
            } else if (mForeignCurrencyCode.equalsIgnoreCase("USD")) {
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
                                            '\n' + '\n' + "The exchange rate is " + mLocalCurrencyCode + "1 = " + fmt1.format(forex_rate) + " " + mForeignCurrencyCode +
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
                                Toast.makeText(getBaseContext(), "this a null pointer issue", Toast.LENGTH_LONG).show();
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

        m_next_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    if (m_amountEntered == 0) {
                        m_sendEditText.requestFocus();
                        m_sendEditText.setError("Please enter amount received");
                    } else {

                        m_moneySenderId = Integer.toString(m_senderId);

                        HashMap<String, String> us = session.getTransId();
                        // name
                        sessionId = us.get(SessionManager.KEY_SESSIONID);

                        double mfivePercentCharge = m_amountEntered * forex_rate * 0.05;
                        String mStringmfivePercentCharge = String.valueOf(mfivePercentCharge);
                        m_transferFees = mStringmfivePercentCharge;
                        Activity_send_to_charity.GetSessionTask task = new Activity_send_to_charity.GetSessionTask();
                        task.execute(sessionId, mForeignCurrencyCode);
                       
                       /* m_sendEditText.setText("");
                        m_receiveEditText.setText("");
                        m_TxtPhoneNo.setText("");*/

                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    public void payment(final AlertDialog mydialog, final String sessionId, final String m_moneySenderId, final String m_amountEntered, final Double forex_rate, final String m_formattedAmountCharged, final String m_transferFees, final String m_organizationId) {


        //  dialog.show();

        m_rate = String.valueOf(forex_rate);
        final String m_transferFees_param = String.valueOf(m_transferFees);
        //final String m_organizationId_param = String.valueOf(m_organizationId);

        pDialog.setMessage("Processing...");

        showDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.SEND_TO_CHARITY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        hideDialog();
                        try {
                            if (response != null || response.length() > 0) {

                                JSONObject jObj = new JSONObject(response);
                                int error = jObj.getInt("ResultsID");
                                if (error == 1) {
                                    // Launch main activity
                                    if (mForeignCurrencyCode.equalsIgnoreCase("UGX")) {
                                        mydialog.show();

                                        dialogButtonPayWithMm = dialog.findViewById(R.id.paymobilemoney);
                                        //   Button dialogButtonPayWithMmAddRec = (Button) dialog.findViewById(R.id.dialogButtonPayWithMmAddRec);
                                        dialogButtonPayWithMmPayWithVisa = dialog.findViewById(R.id.paywithvisa);


                                        // if button is clicked, close the custom dialog
                                        dialogButtonPayWithMm.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                DecimalFormat fmt = new DecimalFormat("0.00");

                                                Intent intent = new Intent(Activity_send_to_charity.this, Pay_with_mobile_money.class);
                                                double mDoubleAmountEntered = Double.valueOf(m_amountEntered);


                                                double mfivePercentCharge = mDoubleAmountEntered * 0.05;
                                                String mStringmfivePercentCharge = String.valueOf(mfivePercentCharge);
                                                BigDecimal mBigmStringDollarAmountEntered = new BigDecimal((Double.valueOf(m_amountEntered) * forex_rate) + (Double.valueOf(m_amountEntered) * forex_rate) * 0.05, MathContext.DECIMAL64);

                                                String mStringAmountEntered = fmt.format(mBigmStringDollarAmountEntered);
                                                //no phone number json returned
                                                //OrganisationPhoneNumber = jObj.getString("PhoneNo");

                                                intent.putExtra("USessionID", sessionId);
                                                intent.putExtra("MoneySenderID", m_moneySenderId);
                                                intent.putExtra("RecipientName", OrganisationName);
                                                intent.putExtra("Amount", mStringAmountEntered);
                                                intent.putExtra("PhoneNo", " ");
                                                intent.putExtra("Rate", m_rate);
                                                intent.putExtra("Charge", mStringmfivePercentCharge);
                                                intent.putExtra("TransferFees", mStringmfivePercentCharge);
                                                intent.putExtra("OrganizationID", m_organizationId);
                                                intent.putExtra("Fcurrency", mForeignCurrencyCode);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                finish();
                                                startActivity(intent);

                                                dialog.dismiss();
                                            }
                                        });


                                        dialogButtonPayWithMmPayWithVisa.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                DecimalFormat fmt = new DecimalFormat("0.00");

                                                Intent intent = new Intent(Activity_send_to_charity.this, Activity_connect_send_to_org.class);
                                                double mDoubleAmountEntered = Double.valueOf(m_amountEntered);


                                                double mfivePercentCharge = mDoubleAmountEntered * 0.05;
                                                String mStringmfivePercentCharge = String.valueOf(mfivePercentCharge);
                                                BigDecimal mBigmStringDollarAmountEntered = new BigDecimal((Double.valueOf(m_amountEntered) * forex_rate) + (Double.valueOf(m_amountEntered) * forex_rate) * 0.05, MathContext.DECIMAL64);

                                                String mStringAmountEntered = fmt.format(mBigmStringDollarAmountEntered);
                                                //no phone number json returned
                                                //OrganisationPhoneNumber = jObj.getString("PhoneNo");

                                                intent.putExtra("USessionID", sessionId);
                                                intent.putExtra("MoneySenderID", m_moneySenderId);
                                                intent.putExtra("RecipientName", project_name);
                                                intent.putExtra("Amount", mStringAmountEntered);
                                                intent.putExtra("PhoneNo", " ");
                                                intent.putExtra("Rate", m_rate);
                                                intent.putExtra("Charge", mStringmfivePercentCharge);
                                                intent.putExtra("TransferFees", mStringmfivePercentCharge);
                                                intent.putExtra("OrganizationID", m_organizationId);
                                                intent.putExtra("Fcurrency", mForeignCurrencyCode);
                                                intent.putExtra("MasterSession", mastersession_id);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                finish();
                                                startActivity(intent);

                                                dialog.dismiss();

                                            }
                                        });


                                    }


                                    if (mForeignCurrencyCode.equalsIgnoreCase("USD")) {


                                        // if button is clicked, close th
                                        DecimalFormat fmt = new DecimalFormat("0.00");

                                        Intent intent = new Intent(Activity_send_to_charity.this, Activity_connect_send_to_org.class);
                                        double mDoubleAmountEntered = Double.valueOf(m_amountEntered);


                                        double mfivePercentCharge = mDoubleAmountEntered * 0.05;
                                        String mStringmfivePercentCharge = String.valueOf(mfivePercentCharge);
                                        BigDecimal mBigmStringDollarAmountEntered = new BigDecimal((Double.valueOf(m_amountEntered) * forex_rate) + (Double.valueOf(m_amountEntered) * forex_rate) * 0.05, MathContext.DECIMAL64);

                                        String mStringAmountEntered = fmt.format(mBigmStringDollarAmountEntered);
                                        //no phone number json returned
                                        //OrganisationPhoneNumber = jObj.getString("PhoneNo");

                                        intent.putExtra("USessionID", sessionId);
                                        intent.putExtra("MoneySenderID", m_moneySenderId);
                                        intent.putExtra("RecipientName", project_name);
                                        intent.putExtra("Amount", mStringAmountEntered);
                                        intent.putExtra("PhoneNo", " ");
                                        intent.putExtra("Rate", m_rate);
                                        intent.putExtra("Charge", mStringmfivePercentCharge);
                                        intent.putExtra("TransferFees", mStringmfivePercentCharge);
                                        intent.putExtra("OrganizationID", m_organizationId);
                                        intent.putExtra("Fcurrency", mForeignCurrencyCode);
                                        intent.putExtra("MasterSession", mastersession_id);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                        finish();
                                        startActivity(intent);


                                    }
                                } else if (error == 3) {
                                    // Error in processing. Get the error message
                                    String msg = jObj.getString("Message");
                                    Toast.makeText(getApplication(),
                                            msg, Toast.LENGTH_LONG).show();

                                } else if (error == 0) {
                                    // Error in processing. Get the error message
                                    String msg = jObj.getString("Message");
                                    Toast.makeText(getApplication(),
                                            msg, Toast.LENGTH_LONG).show();
                                }


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
                            Toast.makeText(getApplication(), getApplication().getString(R.string.error_network_timeout),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof NoConnectionError) {
                            //TODO
                            Toast.makeText(context, context.getString(R.string.nonetworkconection),
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
                Map<String, String> params = new HashMap<>();

                params.put("USessionID", sessionId);
                params.put("MoneySenderID", m_moneySenderId);
                params.put("Amount", m_amountEntered);
                params.put("Rate", m_rate);
                params.put("Charge", m_formattedAmountCharged);
                params.put("TransferFees", m_transferFees_param);
                params.put("OrganizationID", m_organizationId);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void getForexrate(final String mEmail) {
        //pDialog.setMessage("Processing...");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /** Inflate the menu; this adds items to the action bar if it is present.
         *
         */
        getMenuInflater().inflate(R.menu.menu_charity, menu);
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
                Intent intent = new Intent(Activity_send_to_charity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                overridePendingTransition(R.anim.open_main, R.anim.close_next);
                startActivity(intent);
                return true;
        }
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.charity_statement) {
            Intent intent = new Intent(Activity_send_to_charity.this, Activity_charity_statement.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            overridePendingTransition(R.anim.open_main, R.anim.close_next);
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
        Intent intent = new Intent(Activity_send_to_charity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
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
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

        m_organizationId = UserCountryList.get(position).getId();

        OrganisationName = UserCountryList.get(position).getName();

        //String m_organizationId11 = UserCountryList.get(position).getId();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private class GetSessionTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            pDialog = new ProgressDialog(Activity_send_to_charity.this);
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
                        payment(dialog, sessionId, m_moneySenderId, m_amountEntered.toString(), forex_rate, m_transferFees, m_transferFees, m_organizationId);


                    } else {
                        Toast.makeText(Activity_send_to_charity.this, msg,
                                Toast.LENGTH_LONG).show();

                    }
                } catch (Exception e) {
                }
            } catch (Throwable t) {
                //Log.e("My App", "Could not parse malformed JSON: \"" + json + "\"");
            }

        }//close onPostExecute


    }

    private class GetCharityProject extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Activity_send_to_charity.this);
            pDialog.setMessage("loading data...");
            pDialog.setCancelable(true);
            pDialog.show();

        }

        private void populateSpinner() {
            List<String> lables = new ArrayList<String>();

            for (int i = 0; i < UserCountryList.size(); i++) {
                lables.add(UserCountryList.get(i).getName());
                //lables.add(UserCountryList.get(i).getId());
            }
            int charity_project[] = {R.drawable.waterharvest};

            // Creating adapter for spinner
            ArrayAdapter<String> spinnerAdapter;
            spinnerAdapter = new ArrayAdapter<String>(context, R.layout.spinner_item, lables);
            // Drop down layout style - list view with radio button
            spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
            // attaching data adapter to spinner
            spinner.setAdapter(spinnerAdapter);
        }

        @Override
        protected Void doInBackground(Void... arg0) {


            ServiceHandler jsonParser = new ServiceHandler();
            String json = jsonParser.makeServiceCall(Constant.ORGURL, ServiceHandler.GET);

            Log.e("Response: ", "> " + json);

            if (json != null) {
                try {


                    JSONArray country_obj = new JSONArray(json);

                    for (int i = 0; i < country_obj.length(); i++) {
                        JSONObject catObj = (JSONObject) country_obj.get(i);
                        cat = new UserCountry(catObj.getString("OrganizationID"),
                                catObj.getString("Organization_name"), catObj.getString("Description"));
                        UserCountryList.add(cat);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {

                //Toast.makeText(getApplicationContext(), "Didn't receive any data from server!",Toast.LENGTH_LONG);
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
