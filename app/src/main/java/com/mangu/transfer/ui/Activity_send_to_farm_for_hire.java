package com.mangu.transfer.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleAdapter;
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
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.mangu.transfer.R;
import com.mangu.transfer.db.Constant;
import com.mangu.transfer.db.SessionManager;
import com.mangu.transfer.util.AutoCompleteTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Activity_send_to_farm_for_hire extends AppCompatActivity {


    static double mDoubleCartAmt = 0;
    static String mUser_rate = "";
    static int mCartCount;
    static String cart = "0", mStringCartAmt;
    static String projectAmount = "", mprojectId = "", project_title = "";
    final Context context = this;
    public ProgressDialog pDialog;
    public SimpleAdapter m_Adapter;
    public String mLocalCurrencyCode = "", mForeignCurrencyCode = "",
            mLocalCurrencySign = "", mForeignCurrencySign = "";
    Button m_next_button, m_send_button, m_btn_checkout, m_btn_cart_details, btn_trans_summary;
    android.widget.AutoCompleteTextView m_TxtPhoneNo;
    EditText m_receiveEditText, m_sendEditText;
    TextView m_rate_message, m_charge_info, m_txtCartAmount, m_txtProjectAmount;
    String m_contactName, m_contactNumber,
            m_formatIntContactNumber;
    Double mCartAmount = 0.00, m_amountEntered = 0.00, m_stringAmountCharged = 0.00;
    Double m_amountCharged = 0.0, forex_rate = 0.0, m_transferFees = 0.00;
    String m_moneySenderId = "";
    Double m_doubleAmountEntered;
    String m_rate;
    boolean isValid1;
    String mEmail = "";
    String paramName, paramPhone, paramAmount;
    double mStringDollarCartAmountfinal = 0.00;
    double mProjectAmount = 0.00;
    Dialog finalDialog;
    double mfivePercentCharge = 0.00;
    int m_senderId;
    String mStringAmountCharge = "";
    ArrayAdapter<String> adapter;
    JSONObject jObj;
    StringRequest stringRequest;
    TextView mplaceholdersend, mplaceholderreceive;
    String sessionId = "";
    String formatedRate = "", m_senderParam = "";
    AlertDialog dialog = null;
    String newDollarAmt = "";
    private ArrayList<Map<String, String>> m_ContactList;
    private SessionManager session;
    private TextView m_txtCartCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_to_farm_for_hire);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Pay For Farm and Send Money");

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        // session manager
        session = new SessionManager(getApplicationContext());

        dialog = new AlertDialog.Builder(this)
                .setTitle("Transaction Summary")
                .setView(R.layout.proceed_hire_dialog)
                .create();


        overridePendingTransition(R.anim.open_next, R.anim.close_next);
        m_next_button = findViewById(R.id.next_button);
        m_send_button = findViewById(R.id.send);
        m_btn_checkout = findViewById(R.id.viewcart);
        m_btn_cart_details = findViewById(R.id.cart_details);

        mplaceholdersend = findViewById(R.id.placeholder_send);
        mplaceholderreceive = findViewById(R.id.placeholder_receive);

        m_sendEditText = findViewById(R.id.editText_send);
        m_receiveEditText = findViewById(R.id.editText_receive);
        m_rate_message = findViewById(R.id.rate_message);
        m_charge_info = findViewById(R.id.lbl_explainerRate);
        m_txtCartCount = findViewById(R.id.text);
        m_txtCartAmount = findViewById(R.id.txtCartAmount);

        btn_trans_summary = findViewById(R.id.view_transaction_summary);

        m_TxtPhoneNo = (AutoCompleteTextView) findViewById(R.id.enter_recipient_field);

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        HashMap<String, String> us = session.getTransId();
        sessionId = us.get(SessionManager.KEY_SESSIONID);


        m_senderParam = user.get(SessionManager.KEY_SENDER_ID);
        mEmail = user.get(SessionManager.KEY_EMAIL);

        m_moneySenderId = m_senderParam;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            projectAmount = extras.getString("mMonthlyRemittance");
            mprojectId = extras.getString("mManguProjectMemberID");
            project_title = extras.getString("project_title");
        }
        getForexrate(mEmail);


    }

    private void afterGetForex() {
        mplaceholdersend.setText(mLocalCurrencyCode);
        mplaceholderreceive.setText(mForeignCurrencyCode);

        if (mForeignCurrencyCode.equalsIgnoreCase("UGX")) {
            mStringDollarCartAmountfinal = mCartAmount;
            mProjectAmount = (Double.valueOf(projectAmount) + Double.valueOf(projectAmount) * 0.05);
            mStringAmountCharge = String.valueOf(mProjectAmount);
            CartCount(sessionId);
            payfarm(sessionId, m_moneySenderId, mStringAmountCharge, mprojectId);
        } else if (mForeignCurrencyCode.equalsIgnoreCase("USD")) {

            mStringDollarCartAmountfinal = mCartAmount;
            mProjectAmount = (Double.valueOf(projectAmount) + Double.valueOf(projectAmount) * 0.05);

            mStringAmountCharge = String.valueOf(mProjectAmount);
            payfarm(sessionId, m_moneySenderId, mStringAmountCharge, mprojectId);
            CartCount(sessionId);

        }

        BigDecimal mBigmrate = new BigDecimal(forex_rate, MathContext.DECIMAL64);
        DecimalFormat fmt = new DecimalFormat("#,###");
        formatedRate = fmt.format(mBigmrate);
        m_rate_message.setText("The Amount sent includes transfer charges" + '\n' + "The exchange rate is " + mLocalCurrencyCode + "1 = " + formatedRate + " " + mForeignCurrencyCode);
        /** Defining the ArrayAdapter to set items to ListView */
        m_ContactList = new ArrayList<>();
        Thread t = new Thread(new Runnable() {
            public void run() {
                readContacts();
            }
        });
        t.start();
        m_Adapter = new SimpleAdapter(this, m_ContactList, R.layout.item_autocomplete,
                new String[]{"Name", "Phone", "Type"}, new int[]{
                R.id.contact_name, R.id.contact_phone, 0});
        m_TxtPhoneNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                m_contactName = s.toString();
                m_contactNumber = s.toString();

                String wt_contactNumber = m_contactNumber;
                PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

                /**Formatting the phone number **/
                try {
                    Phonenumber.PhoneNumber wt2_ContactNumber = phoneUtil.parse(wt_contactNumber, "UG");
                    /** phone  begin with '+'**/
                    String wt3_ContactNumber = phoneUtil.format(wt2_ContactNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);

                    Phonenumber.PhoneNumber wt4_ContactNumber = phoneUtil.parse(wt3_ContactNumber, "UG");
                    //m_countryCode = m_ccode.getCountryCode();
                    isValid1 = phoneUtil.isValidNumber(wt4_ContactNumber);
                    //m_stringCountryCode  = String.valueOf(m_countryCode);
                    if (isValid1) {

                        m_contactNumber = wt3_ContactNumber;
                    } else {

                        m_TxtPhoneNo.requestFocus();
                        m_TxtPhoneNo.setError("Please invalid phone number");
                    }
                } catch (NumberParseException e) {
                    System.err.println("NumberParseException was thrown: " + e.toString());
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
        m_TxtPhoneNo.setAdapter(m_Adapter);
        m_TxtPhoneNo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> av, View arg1, int index,
                                    long arg3) {
                Map<String, String> map = (Map<String, String>) av
                        .getItemAtPosition(index);
                m_contactName = map.get("Name");
                m_contactNumber = map.get("Phone");
                m_TxtPhoneNo.setText(m_contactName + " " + m_contactNumber);
                // TODO Auto-generated method stub
            }
        });


        btn_trans_summary.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub


                ShowDialog();
            }
        });


        /**
         * Auto convert the amount sent from foreign currency to local currency an vise versa
         * **/
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

                                    String DollarAmountLimit = ",1,000";

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

        }
        /** Defining a click event listener for the button "Add" */
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (m_amountEntered == 0) {
                    m_sendEditText.requestFocus();
                    m_sendEditText.setError("Please enter amount received");
                } else {

                    String amountBilled = m_receiveEditText.getText().toString();

                    //m_amountCharged = Double.valueOf(amountBilled);


                    HashMap<String, String> m_us = session.getTransId();
                    // name
                    //String sessionId = m_us.get(SessionManager.KEY_SESSIONID);

                    if (mCartCount > 4) {

                        Toast.makeText(Activity_send_to_farm_for_hire.this, "Limited number of recipients ", Toast.LENGTH_LONG).show();

                    } else if (mDoubleCartAmt > 4000000) {
                        Toast.makeText(Activity_send_to_farm_for_hire.this, "Amount has exceeded the limit ", Toast.LENGTH_LONG).show();

                        m_send_button.setVisibility(View.VISIBLE); //SHOW the button
                        m_next_button.setVisibility(View.GONE);
                    } else {

                        double mStringDollarCartAmount = mCartAmount;

                        double mfivePercentCharge = mStringDollarCartAmount * 0.05;
                        String mStringmfivePercentCharge = String.valueOf(mfivePercentCharge);
                        m_transferFees = (mfivePercentCharge);

                        if (mForeignCurrencyCode.equalsIgnoreCase("UGX")) {
                            mStringDollarCartAmountfinal = mCartAmount;
                            mProjectAmount = (Double.valueOf(projectAmount) + Double.valueOf(projectAmount) * 0.05);

                            mfivePercentCharge = mStringDollarCartAmount * 0.05;
                            mStringAmountCharge = String.valueOf(mProjectAmount);

                            payment(sessionId, m_moneySenderId, m_contactNumber, m_contactName, String.valueOf(m_amountEntered * forex_rate), forex_rate, mStringmfivePercentCharge, m_transferFees);
                        } else if (mForeignCurrencyCode.equalsIgnoreCase("USD")) {
                            mStringDollarCartAmountfinal = mCartAmount;
                            mProjectAmount = (Double.valueOf(projectAmount) + Double.valueOf(projectAmount) * 0.05);

                            mfivePercentCharge = mStringDollarCartAmount * 0.05;
                            mStringAmountCharge = String.valueOf(mProjectAmount);

                            payment(sessionId, m_moneySenderId, m_contactNumber, m_contactName, String.valueOf(m_amountEntered / forex_rate), forex_rate, mStringmfivePercentCharge, m_transferFees);

                        }

                    }
                    m_sendEditText.setText("");
                    m_receiveEditText.setText("");
                    m_TxtPhoneNo.setText("");

                }
            }
        };
        /** Setting the event listener for the add button */
        m_next_button.setOnClickListener(listener);
        /** Setting the adapter to the ListView */
        View.OnClickListener send_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCartCount <= 0) {
                    Toast.makeText(Activity_send_to_farm_for_hire.this, "please add at least one recipient", Toast.LENGTH_LONG).show();

                } else {
                    // String mCartNumber = Integer.valueOf(mCartCount).toString();
                    if (mForeignCurrencyCode.equalsIgnoreCase("UGX")) {

                        Intent intent = new Intent(Activity_send_to_farm_for_hire.this, Activity_connect_send_to_many.class);
                        String mStringCartCount = String.valueOf(mCartCount);
                        double mStringDollarCartAmount = mCartAmount;

                        double mStringDollarCartAmountfinal = mCartAmount + mCartAmount * 0.05;

                        double mfivePercentCharge = mStringDollarCartAmount * 0.05;
                        DecimalFormat fmt = new DecimalFormat("0.00");
                        String mStringCartAmount = fmt.format(mStringDollarCartAmountfinal);
                        intent.putExtra("USessionID", sessionId);
                        intent.putExtra("Number_Of_Recipients", mStringCartCount);
                        intent.putExtra("Amount", mStringCartAmount);
                        intent.putExtra("Fcurrency", mForeignCurrencyCode);
                        startActivity(intent);

                    } else if (mForeignCurrencyCode.equalsIgnoreCase("USD")) {
                        Intent intent = new Intent(Activity_send_to_farm_for_hire.this, Activity_connect_send_to_many.class);
                        String mStringCartCount = String.valueOf(mCartCount);
                        double mStringDollarCartAmount = mCartAmount;

                        double mStringDollarCartAmountfinal = mCartAmount + mCartAmount * 0.05;

                        double mfivePercentCharge = mStringDollarCartAmount * 0.05;
                        DecimalFormat fmt = new DecimalFormat("0.00");
                        String mStringCartAmount = fmt.format(mStringDollarCartAmountfinal);
                        intent.putExtra("USessionID", sessionId);
                        intent.putExtra("Number_Of_Recipients", mStringCartCount);
                        intent.putExtra("Amount", mStringCartAmount);
                        intent.putExtra("Fcurrency", mForeignCurrencyCode);
                        startActivity(intent);

                    }

                }

            }
        };
        /** Setting the event listener for the add button */
        m_send_button.setOnClickListener(send_listener);

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
                            Toast.makeText(getApplication(), getApplication().getString(R.string.error_network_timeout),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof NoConnectionError) {
                            //TODO
                            Toast.makeText(getApplication(), getApplication().getString(R.string.nonetworkconection),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof AuthFailureError) {
                            //TODO
                            Toast.makeText(getApplication(), getApplication().getString(R.string.authentification),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof ServerError) {
                            //TODO
                            Toast.makeText(getApplication(), getApplication().getString(R.string.servererror),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof NetworkError) {

                            //TODO
                            Toast.makeText(getApplication(), getApplication().getString(R.string.networkerror),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof ParseError) {
                            //TODO
                            Toast.makeText(getApplication(), getApplication().getString(R.string.parseerror),
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

    public void payment(final String sessionId, final String m_moneySenderId, final String m_contactNumber, final String m_contactName, final String m_amountEntered, final double forex_rate, final String mStringmfivePercentCharge, final double m_transferFees) {

        m_rate = String.valueOf(forex_rate);
        final String m_transferFees_param = String.valueOf(m_transferFees);

        pDialog.setMessage("Processing...");

        showDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.SEND_TO_MANY,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        hideDialog();
                        CartCount(sessionId);
                        CartAmount(sessionId);
                        try {

                            JSONArray jsonArray = new JSONArray(response);

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject obj = jsonArray.getJSONObject(i);

                                paramName = obj.getString("MoneyRecipient");
                                paramPhone = obj.getString("PhoneNo");
                                paramAmount = obj.getString("Amount");


                                mStringCartAmt = Double.valueOf(paramAmount).toString();
                                mDoubleCartAmt = Double.valueOf(paramAmount);
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
                            Toast.makeText(context, context.getString(R.string.error_network_timeout),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof NoConnectionError) {
                            //TODO
                            Toast.makeText(context, context.getString(R.string.nonetworkconection),
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
                params.put("USessionID", sessionId);
                params.put("MoneySenderID", m_moneySenderId);
                params.put("PhoneNo", m_contactNumber);
                params.put("RecipientName", m_contactName);
                params.put("Amount", m_amountEntered);
                params.put("Rate", m_rate);
                params.put("Charge", mStringmfivePercentCharge);
                params.put("TransferFees", m_transferFees_param);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void payfarm(final String sessionId, final String m_moneySenderId, final String mStringAmountCharge, final String mprojectId) {
        pDialog.setMessage("Processing...");
        showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.SENDTOFARM,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        hideDialog();
                        try {

                            JSONObject jObj = new JSONObject(response);
                            int error = jObj.getInt("ResultsID");
                            if (error == 1) {

                                dialog.show();

                                Button dialogButton = dialog.findViewById(R.id.dialogButtonPay);
                                Button dialogButtonAddRec = dialog.findViewById(R.id.dialogButtonAddRec);
                                Button dialogButtonCancel = dialog.findViewById(R.id.dialogButtonCancel);
                                m_txtCartCount = dialog.findViewById(R.id.text);
                                m_txtCartAmount = dialog.findViewById(R.id.txtCartAmount);
                                m_txtProjectAmount = dialog.findViewById(R.id.projectText);

                                m_txtCartAmount = dialog.findViewById(R.id.txtCartAmount);
                                m_txtCartCount.setText(cart);

                                m_txtProjectAmount.setText(mForeignCurrencyCode + " " + projectAmount);

                                m_txtCartAmount.setText(mForeignCurrencyCode + " " + mProjectAmount);

                                // if button is clicked, close the custom dialog
                                dialogButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (mCartCount >= 1) {

                                            post_then_checkout(m_moneySenderId);
                                        } else {

                                            Toast.makeText(getBaseContext(), "Please add at least one recipient to cart list", Toast.LENGTH_LONG).show();
                                        }

                                        dialog.dismiss();
                                    }
                                });

                                dialogButtonAddRec.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        btn_trans_summary.setVisibility(View.VISIBLE);
                                        dialog.dismiss();
                                    }
                                });

                                dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        dialog.dismiss();
                                    }
                                });


                                dialog.show();






/*

                                    Dialog dialog = null;
                                    final int CUSTOM_DIALOG_ID = 0;
                                    ListView dialog_ListView;
                                    String[] listContent = {"1. Add Another Recipient?","2. Continue ?","3. Cancel ?"};
                                    int id = 0;
                                    switch (id) {
                                        case CUSTOM_DIALOG_ID:
                                            dialog = new Dialog(context);
                                            dialog.setContentView(R.layout.dialog_layout);
                                            dialog.setTitle("Do you want to");
                                            dialog.setCancelable(false);
                                            dialog.setCanceledOnTouchOutside(false);
                                            //Prepare ListView in dialog
                                            dialog_ListView = (ListView) dialog.findViewById(R.id.dialoglist);
                                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,R.layout.spinner_item, listContent);
                                            dialog_ListView.setAdapter(adapter);
                                            finalDialog = dialog;
                                            dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view,
                                                                        int position, long id) {
                                                    // TODO Auto-generated method stub

                                                    switch (position) {
                                                        case 0:
                                                            finalDialog.dismiss();
                                                            break;
                                                        case 1:

                                                            Intent intent = new Intent(Activity_send_to_farm_for_hire.this, Activity_connect_cart_hire.class);
                                                            String mStringCartCount = String.valueOf(mCartCount);
                                                            double mStringDollarCartAmount = mCartAmount;
                                                            mStringDollarCartAmountfinal = mCartAmount;
                                                            mProjectAmount =  (Double.valueOf(projectAmount) +Double.valueOf(projectAmount)* 0.05) + mStringDollarCartAmountfinal;

                                                            mfivePercentCharge = mStringDollarCartAmount * 0.05;
                                                            String mStringAmountCharge = String.valueOf(mProjectAmount);
                                                            DecimalFormat fmt = new DecimalFormat("0.00");                                                            intent.putExtra("USessionID", sessionId);
                                                            intent.putExtra("Number_Of_Recipients", project_title);
                                                            intent.putExtra("Amount", mStringAmountCharge);
                                                            intent.putExtra("Fcurrency", mForeignCurrencyCode);
                                                            intent.putExtra("MoneySenderID", m_moneySenderId);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                            finish();
                                                            startActivity(intent);

                                                            break;
                                                   */
/* case 2:
                                                        if(mCartCount >=1) {
                                                            post_then_checkout(m_moneySenderId);
                                                        }else{
                                                            Toast.makeText(getBaseContext(),"Please add at least one recipient to cart list",Toast.LENGTH_LONG).show();
                                                        }
                                                        break;*//*

                                                        case 2:
                                                            finalDialog.cancel();
                                                            break;
                                                    }

                                                }
                                            });

                                            break;
                                    }dialog.show();
*/


                            } else if (error == 3) {
                                // Error in processing. Get the error message
                                error = jObj.getInt("ResultsID");
                                Toast.makeText(getApplicationContext(),
                                        "Error message " + error, Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Error occurred", Toast.LENGTH_LONG).show();

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
                params.put("USessionID", sessionId);
                params.put("MoneySenderID", m_moneySenderId);
                params.put("Amount", mStringAmountCharge);
                params.put("ManguProjectMemberID", mprojectId);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void ShowDialog() {

        dialog.show();

        Button dialogButton = dialog.findViewById(R.id.dialogButtonPay);
        Button dialogButtonAddRec = dialog.findViewById(R.id.dialogButtonAddRec);
        Button dialogButtonCancel = dialog.findViewById(R.id.dialogButtonCancel);
        m_txtCartCount = dialog.findViewById(R.id.text);
        m_txtCartAmount = dialog.findViewById(R.id.txtCartAmount);
        m_txtProjectAmount = dialog.findViewById(R.id.projectText);

        m_txtCartAmount = dialog.findViewById(R.id.txtCartAmount);
        m_txtCartCount.setText(cart);

        m_txtProjectAmount.setText(mForeignCurrencyCode + " " + projectAmount);

        m_txtCartAmount.setText(mForeignCurrencyCode + " " + mProjectAmount);

        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCartCount >= 1) {

                    post_then_checkout(m_moneySenderId);
                } else {

                    Toast.makeText(getBaseContext(), "Please add at least one recipient to cart list", Toast.LENGTH_LONG).show();
                }

                dialog.dismiss();
            }
        });

        dialogButtonAddRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btn_trans_summary.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });

        dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });


        dialog.show();


    }

    public void CartCount(final String sessionId) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.CARTCOUNT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {


                            JSONObject jObj = new JSONObject(response);
                            mCartCount = jObj.getInt("ResultsID");

                            cart = Integer.valueOf(mCartCount).toString();
                            //  m_txtCartCount.setText(cart);

                            if (mCartCount >= 1) {


                                dialog.show();

                                Button dialogButton = dialog.findViewById(R.id.dialogButtonPay);
                                Button dialogButtonAddRec = dialog.findViewById(R.id.dialogButtonAddRec);
                                Button dialogButtonCancel = dialog.findViewById(R.id.dialogButtonCancel);
                                m_txtCartCount = dialog.findViewById(R.id.text);
                                m_txtCartAmount = dialog.findViewById(R.id.txtCartAmount);
                                m_txtProjectAmount = dialog.findViewById(R.id.projectText);

                                m_txtCartAmount = dialog.findViewById(R.id.txtCartAmount);
                                m_txtCartCount.setText(cart);

                                m_txtProjectAmount.setText(mForeignCurrencyCode + " " + projectAmount);

                                m_txtCartAmount.setText(mForeignCurrencyCode + " " + mProjectAmount);

                                // if button is clicked, close the custom dialog
                                dialogButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (mCartCount >= 1) {

                                            post_then_checkout(m_moneySenderId);
                                        } else {

                                            Toast.makeText(getBaseContext(), "Please add at least one recipient to cart list", Toast.LENGTH_LONG).show();
                                        }

                                        dialog.dismiss();
                                    }
                                });

                                dialogButtonAddRec.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        btn_trans_summary.setVisibility(View.VISIBLE);
                                        dialog.dismiss();
                                    }
                                });

                                dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        dialog.dismiss();
                                    }
                                });


                                dialog.show();







                                /*

                                Dialog dialog = null;
                                final int CUSTOM_DIALOG_ID = 0;
                                ListView dialog_ListView;
                                String[] listContent = {"1. Add Another Recipient?","2. Continue ?","3. Cancel ?"};
                                int id = 0;
                                switch (id) {
                                    case CUSTOM_DIALOG_ID:
                                        dialog = new Dialog(context);
                                        dialog.setContentView(R.layout.dialog_layout);
                                        dialog.setTitle("Do you want to");
                                        dialog.setCancelable(false);
                                        dialog.setCanceledOnTouchOutside(false);
                                        //Prepare ListView in dialog
                                        dialog_ListView = (ListView) dialog.findViewById(R.id.dialoglist);
                                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,R.layout.spinner_item, listContent);
                                        dialog_ListView.setAdapter(adapter);
                                         finalDialog = dialog;
                                        dialog_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view,
                                                                    int position, long id) {
                                                // TODO Auto-generated method stub

                                                switch (position) {
                                                    case 0:
                                                        finalDialog.dismiss();
                                                        break;
                                                    case 1:
                                                        if(mCartCount >=1) {

                                                            post_then_checkout(m_moneySenderId);


                                                        }else{

                                                            Toast.makeText(getBaseContext(),"Please add at least one recipient to cart list",Toast.LENGTH_LONG).show();


                                                        }
                                                        break;
                                                   *//* case 2:
                                                        if(mCartCount >=1) {
                                                            post_then_checkout(m_moneySenderId);
                                                        }else{
                                                            Toast.makeText(getBaseContext(),"Please add at least one recipient to cart list",Toast.LENGTH_LONG).show();
                                                        }
                                                        break;*//*
                                                    case 2:
                                                        finalDialog.cancel();
                                                        break;
                                                }

                                            }
                                        });

                                        break;
                                }dialog.show();*/


                            } else if (mCartCount == 4) {

                                m_send_button.setVisibility(View.VISIBLE); //SHOW the button
                                m_next_button.setVisibility(View.GONE);

                                Toast.makeText(Activity_send_to_farm_for_hire.this, "You have reached max number recipients, please check out ", Toast.LENGTH_LONG).show();

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
                            Toast.makeText(context, context.getString(R.string.error_network_timeout),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof NoConnectionError) {
                            //TODO
                            Toast.makeText(context, context.getString(R.string.nonetworkconection),
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
                params.put("USessionID", sessionId);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void CartAmount(final String sessionId) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.CARTAMOUNT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {


                            JSONObject jObj = new JSONObject(response);
                            mCartAmount = jObj.getDouble("Message");
                            if (mCartAmount == 4000000) {
                                m_send_button.setVisibility(View.VISIBLE); //SHOW the button
                                m_next_button.setVisibility(View.GONE);
                                Toast.makeText(Activity_send_to_farm_for_hire.this, "You have reached max Amount to be transferred, please check out ", Toast.LENGTH_LONG).show();

                            }
                            mProjectAmount = (Double.valueOf(projectAmount) + Double.valueOf(projectAmount) * 0.05);
                            double mDollaCartAmt = mCartAmount + mProjectAmount;


                            BigDecimal mBigDecimalcartAmt = new BigDecimal(mDollaCartAmt, MathContext.DECIMAL64);
                            DecimalFormat fmt = new DecimalFormat("0.00");
                            String newDollarAmt = fmt.format(mBigDecimalcartAmt);
                            m_txtCartAmount.setText(mForeignCurrencyCode + " " + newDollarAmt);

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
                            hideDialog();
                        } else if (error instanceof NoConnectionError) {
                            //TODO
                            Toast.makeText(context, context.getString(R.string.nonetworkconection),
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
                params.put("USessionID", sessionId);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void post_then_checkout(final String m_moneySenderId) {

        pDialog.setMessage("Processing...");

        showDialog();

        stringRequest = new StringRequest(Request.Method.POST, Constant.SEND_TO_MANY_CHECKOUT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        hideDialog();
                        try {
                            JSONObject jObj = new JSONObject(response);
                            int msgs = jObj.getInt("ResultsID");
                            String error_msgs = jObj.getString("Message");
                            if (msgs == 1) {
                                //Toast.makeText(Activity_send_to_farm_for_hire.this, msgs, Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(Activity_send_to_farm_for_hire.this, Activity_connect_cart_hire.class);
                                String mStringCartCount = String.valueOf(mCartCount);
                                double mStringDollarCartAmount = mCartAmount;
                                mStringDollarCartAmountfinal = mCartAmount;
                                mProjectAmount = (Double.valueOf(projectAmount) + Double.valueOf(projectAmount) * 0.05) + mStringDollarCartAmountfinal;

                                mfivePercentCharge = mStringDollarCartAmount * 0.05;
                                String mStringAmountCharge = String.valueOf(mProjectAmount);
                                double mStringDollarCartAmountfinal = mCartAmount + mCartAmount * 0.05;
                                DecimalFormat fmt = new DecimalFormat("0.00");
                                String mStringCartAmount = fmt.format(mStringDollarCartAmountfinal);
                                intent.putExtra("USessionID", sessionId);
                                intent.putExtra("Number_Of_Recipients", project_title + " and " + mStringCartCount);
                                intent.putExtra("Amount", mStringAmountCharge);
                                intent.putExtra("Fcurrency", mForeignCurrencyCode);
                                intent.putExtra("MoneySenderID", m_moneySenderId);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                finish();
                                startActivity(intent);
                            } else if (msgs == 0) {
                                Toast.makeText(context, error_msgs,
                                        Toast.LENGTH_LONG).show();
                                finalDialog.dismiss();

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
                            Toast.makeText(context, context.getString(R.string.error_network_timeout),
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
                Map<String, String> params = new HashMap<String, String>();
                params.put("MoneySenderID", m_moneySenderId);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    /**
     * Reading contacts from the phone book
     **/
    public void readContacts() {
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        int colDisplayName = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int colPhoneNumber = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        int colPhoneType = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);

        while (phones.moveToNext()) {


            String temp_name = "", temp_number = "", temp_type = "";

            temp_name = phones.getString(colDisplayName);
            temp_number = phones.getString(colPhoneNumber);
            temp_type = phones.getString(colPhoneType);
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

            /**Formatting the phone number **/
            try {
                Phonenumber.PhoneNumber m_formatContactNumber = phoneUtil.parse(temp_number, "UG");
                /** phone  begin with '+'**/
                temp_number = phoneUtil.format(m_formatContactNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);

                Phonenumber.PhoneNumber m_ccode = phoneUtil.parse(m_formatIntContactNumber, "");
                //m_countryCode = m_ccode.getCountryCode();

                //m_stringCountryCode  = String.valueOf(m_countryCode);

            } catch (NumberParseException e) {
                System.err.println("NumberParseException was thrown: " + e.toString());
            }


            Map<String, String> NamePhoneType = new HashMap<String, String>();
            NamePhoneType.put("Name", temp_name);
            NamePhoneType.put("Phone", temp_number);
            NamePhoneType.put("Type", temp_type);

            m_ContactList.add(NamePhoneType); //add this map to the list.


        }
        phones.close();
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
                Intent intent = new Intent(Activity_send_to_farm_for_hire.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.open_main, R.anim.close_next);
                return true;
        }
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.available_project) {
            Intent intent = new Intent(Activity_send_to_farm_for_hire.this, Activity_AvailableFarm_projects.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            return true;
        } else if (id == R.id.farm_statement) {
            Intent intent = new Intent(Activity_send_to_farm_for_hire.this, Activity_farm4hire_statement.class);
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

        Intent intent = new Intent(Activity_send_to_farm_for_hire.this, Activity_my_farm_project.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
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

}
