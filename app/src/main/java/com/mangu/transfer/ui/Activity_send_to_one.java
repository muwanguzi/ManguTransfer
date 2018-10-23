package com.mangu.transfer.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Activity_send_to_one extends AppCompatActivity {


    public static Double forex_rate = 0.00;
    static String mStringAmountEntered = "";
    final Context context = this;
    public ProgressDialog pDialog;
    public int to, from;
    public String s, name;
    public SimpleAdapter m_Adapter;
    int m_senderId = 0;
    boolean isValid, isValid1;
    String m_moneySenderId;
    Button m_next_button;
    android.widget.AutoCompleteTextView m_TxtPhoneNo;
    EditText m_receiveEditText, m_sendEditText;
    TextView m_rate_message, m_charge_info, mplaceholdersend, mplaceholderreceive;
    String m_contactName = "", m_contactNumber = "", m_namePhone = "";
    Double m_stringAmountCharged = 0.00, m_amountEntered = 0.00;
    String m_rate;
    String mUser_rate = "", mLocalCurrencyCode = "",
            mForeignCurrencyCode = "", mLocalCurrencySign = "",
            mForeignCurrencySign = "";
    String formatedRate = "";
    String mEmail;
    Double m_doubleAmountEntered, m_amountCharged, m_transferFees = 0.00;
    JSONObject jObj;
    StringRequest stringRequest;
    private SessionManager session;
    private ArrayList<Map<String, String>> m_ContactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_to_one);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Send To One");
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(true);

        session = new SessionManager(getApplicationContext());
        /** get user data from session*/
        HashMap<String, String> user = session.getUserDetails();
        String m_senderParam = user.get(SessionManager.KEY_SENDER_ID);
        mEmail = user.get(SessionManager.KEY_EMAIL);
        m_senderId = Integer.valueOf(m_senderParam);
        mplaceholdersend = findViewById(R.id.placeholder_send);
        mplaceholderreceive = findViewById(R.id.placeholder_receive);

        m_sendEditText = findViewById(R.id.editText_send);
        m_receiveEditText = findViewById(R.id.editText_receive);
        m_next_button = findViewById(R.id.next_button);
        m_rate_message = findViewById(R.id.rate_message);
        m_charge_info = findViewById(R.id.lbl_explainerRate);
        m_TxtPhoneNo = (com.mangu.transfer.util.AutoCompleteTextView) findViewById(R.id.enter_recipient_field);


        overridePendingTransition(R.anim.open_next, R.anim.close_next);

        getForexrate(mEmail);

    }

    private void afterGetForex() {

        mplaceholdersend.setText(mLocalCurrencyCode);
        mplaceholderreceive.setText(mForeignCurrencyCode);
        m_ContactList = new ArrayList<>();
        Thread t = new Thread(new Runnable() {
            public void run() {
                readContacts();
            }
        });
        t.start();


        BigDecimal mBigmrate = new BigDecimal(forex_rate, MathContext.DECIMAL64);
        DecimalFormat fmt = new DecimalFormat("#,###");
        formatedRate = fmt.format(mBigmrate);

        //m_rate_message.setText(mStringAmountEntered +" Amount includes transfer charges"+ '\n'+"The exchange rate is $1 = "+formatedRate+" UGX");

        /**pass contact  variable to an arrayList
         */
        m_Adapter = new SimpleAdapter(this, m_ContactList, R.layout.item_autocomplete,
                new String[]{"Name", "Phone", "Type"}, new int[]{
                R.id.contact_name, R.id.contact_phone, 0});

        try {
            m_TxtPhoneNo.setAdapter(m_Adapter);

        } catch (NullPointerException e) {
        }

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
                        m_TxtPhoneNo.setError("Invalid phone number");
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


        // m_amountEntered = m_sendEditText.getText().toString();
        m_namePhone = m_TxtPhoneNo.getText().toString();
        /**
         * Auto convert the amount sent from foreign currency to local currency an vise versa
         * **/
        if (forex_rate >= 1) {
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
                            Double m_amountLimit = (forex_rate * 1000);
                            if (m_amountEntered.doubleValue() <= m_amountLimit) {
                                /** setting charges */
                                m_amountCharged = (m_amountEntered / forex_rate);
                                //get the previous converted data
                                Double previous_convert = m_stringAmountCharged * forex_rate;
                                double amtchar = (m_amountEntered / forex_rate) + (m_amountEntered / forex_rate) * 0.05;
                                DecimalFormat fmt1 = new DecimalFormat("#,###");
                                double mStringmAmt_to_deducted = (Math.floor(amtchar * 100) / 100);
                                m_rate_message.setText("I am sending: " + fmt1.format(m_amountEntered) + " UGX" +
                                        '\n' + '\n' + "The exchange rate is $1 = " + formatedRate + " UGX" +
                                        '\n' + '\n' + m_contactName + " will receive: " + fmt1.format(m_amountEntered) + " UGX" +
                                        '\n' + '\n' + "Total Remittance: " + "$ " + mStringmAmt_to_deducted);


                                //incse changed by another controll
                                if (previous_convert.doubleValue() != m_amountEntered.doubleValue()) {

                                    m_stringAmountCharged = m_amountCharged;

                                    Double temp_str = (Math.floor(m_stringAmountCharged * 100) / 100);
                                    m_receiveEditText.setText(temp_str.toString());

                                }

                            } else {
                                Toast.makeText(getBaseContext(), "Amount can not Exceed " + m_amountLimit + " UGX", Toast.LENGTH_LONG).show();
                                //reverse to previous figure
                                //get the previous converted data
                                m_amountEntered = m_stringAmountCharged * forex_rate;

                                Double temp_str = (Math.floor(m_amountEntered * 100) / 100);
                                BigDecimal mBigtemp_str = new BigDecimal(temp_str, MathContext.DECIMAL64);
                                DecimalFormat fmt = new DecimalFormat("#,###");
                                String formatedmBigtemp_str = fmt.format(mBigtemp_str);
                                m_sendEditText.setText(formatedmBigtemp_str);


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

                    DecimalFormat fmt = new DecimalFormat("0.00");
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

                            if (m_stringAmountCharged.doubleValue() <= 1000) {
                                /** setting charges */
                                m_doubleAmountEntered = (m_stringAmountCharged * forex_rate);

                                //get the previous converted data
                                Double previous_convert = m_amountEntered / forex_rate;

                                if (previous_convert.doubleValue() != m_stringAmountCharged.doubleValue()) {

                                    m_amountEntered = m_doubleAmountEntered;
                                    Double temp_str = (Math.floor(m_amountEntered * 100) / 100);

                                    BigDecimal mBigtemp_str = new BigDecimal(temp_str, MathContext.DECIMAL64);
                                    DecimalFormat fmtstr = new DecimalFormat("#,###");
                                    String formatedmBigtemp_str = fmtstr.format(mBigtemp_str);
                                    m_sendEditText.setText(formatedmBigtemp_str);

                                }
                                //rate_message.setText("You are sending" + " " + roundedAmt + " " + "at a rate of " + m_rate);
                                //charge_info.setText("This includes all transfer charges");

                            } else {
                                /**
                                 * open alter box for sending more that the maximum amount
                                 */

                                String DollarAmountLimit = "$1,000";

                                Toast.makeText(getBaseContext(), "Amount can not Exceed " + DollarAmountLimit, Toast.LENGTH_LONG).show();

                                //reverse to previous figure
                                //get the previous converted data
                                m_stringAmountCharged = m_amountEntered / forex_rate;

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
        } else {

        }


        /**
         * button to start submits the payment**/
        m_next_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {

                    if (m_amountEntered == 0) {
                        m_sendEditText.requestFocus();
                        m_sendEditText.setError("Please enter amount received");

                    }/*else if(!(isValid1)){

                        m_TxtPhoneNo.requestFocus();
                        m_TxtPhoneNo.setError("Please invalid phone number");
                    }else*/
                    {

                        pDialog.setMessage("processing...");
                        showDialog();
                        hideDialog();
                        m_moneySenderId = Integer.toString(m_senderId);
                        HashMap<String, String> us = session.getTransId();
                        // name
                        String sessionId = us.get(SessionManager.KEY_SESSIONID);


                        double mfivePercentCharge = m_amountEntered * 0.05;
                        String mStringmfivePercentCharge = String.valueOf(mfivePercentCharge);
                        m_transferFees = (mfivePercentCharge);

                        payment(sessionId, m_moneySenderId, m_contactNumber, m_contactName, m_amountEntered.toString(), forex_rate, mStringmfivePercentCharge, m_transferFees);

                        m_sendEditText.setText("");
                        m_receiveEditText.setText("");
                        m_TxtPhoneNo.setText("");

                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
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
                params.put("Email", mEmail);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    public void payment(final String sessionId, final String m_moneySenderId, final String m_contactNumber, final String m_contactName, final String m_amountEntered, final double forex_rate, final String m_formattedAmountCharged, final double m_transferFees) {

        m_rate = String.valueOf(forex_rate);
        final String m_transferFees_param = String.valueOf(m_transferFees);

        pDialog.setMessage("Processing...");

        showDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.SEND_TO_ONE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        hideDialog();
                        try {

                            JSONObject jObj = new JSONObject(response);
                            int error = jObj.getInt("ResultsID");
                            if (error == 1) {
                                // Launch main activity
                                Intent intent = new Intent(Activity_send_to_one.this, Activity_connect_send_2_one.class);
                                double mDoubleAmountEntered = Double.valueOf(m_amountEntered);

                                double mDollarAmountEntered = mDoubleAmountEntered / Double.valueOf(m_rate);
                                double mfivePercentCharge = mDollarAmountEntered * 0.05;
                                String mStringmfivePercentCharge = String.valueOf(mfivePercentCharge);


                                BigDecimal mBigmStringDollarAmountEntered = new BigDecimal(mDollarAmountEntered + mDollarAmountEntered * 0.05, MathContext.DECIMAL64);
                                DecimalFormat fmt = new DecimalFormat("0.00");
                                mStringAmountEntered = fmt.format(mBigmStringDollarAmountEntered);
                                intent.putExtra("USessionID", sessionId);
                                intent.putExtra("MoneySenderID", m_moneySenderId);
                                intent.putExtra("PhoneNo", m_contactNumber);
                                intent.putExtra("RecipientName", m_contactName);
                                intent.putExtra("Amount", mStringAmountEntered);
                                intent.putExtra("Rate", m_rate);
                                intent.putExtra("Charge", mStringmfivePercentCharge);
                                intent.putExtra("TransferFees", mStringmfivePercentCharge);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                finish();
                                startActivity(intent);

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
                params.put("PhoneNo", m_contactNumber);
                params.put("RecipientName", m_contactName);
                params.put("Amount", m_amountEntered);
                params.put("Rate", m_rate);
                params.put("Charge", m_formattedAmountCharged);
                params.put("TransferFees", m_transferFees_param);
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

                Phonenumber.PhoneNumber m_ugPhoneNumber = phoneUtil.parse(temp_number, "UG");
                //m_countryCode = m_ccode.getCountryCode();
                isValid = phoneUtil.isValidNumber(m_ugPhoneNumber);
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
                Intent intent = new Intent(Activity_send_to_one.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.open_main, R.anim.close_next);
                return true;
        }
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.statement4people) {
            Intent intent = new Intent(Activity_send_to_one.this, Activity_statement_4_people.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            return true;
        } else if (id == R.id.bank_statement) {
            Intent intent = new Intent(Activity_send_to_one.this, Activity_bank_statement.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            return true;
        } else if (id == R.id.charity_statement) {
            Intent intent = new Intent(Activity_send_to_one.this, Activity_charity_statement.class);
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

        Intent intent = new Intent(Activity_send_to_one.this, MainActivity.class);
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
