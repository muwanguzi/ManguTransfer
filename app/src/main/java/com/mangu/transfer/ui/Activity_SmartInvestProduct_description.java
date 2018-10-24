package com.mangu.transfer.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.bumptech.glide.Glide;
import com.mangu.transfer.R;
import com.mangu.transfer.db.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Activity_SmartInvestProduct_description extends AppCompatActivity {
    TextView txtproject_name;
    TextView txtProject_Desc;
    TextView txtAmount_needed;
    TextView txtAmount_collected, txtAmount_balance;
    ImageView imagename;
    Button btn_donate;
    Context context;
    String itempriceid = "";
    String mUser_rate = "", mLocalCurrencyCode = "",
            mForeignCurrencyCode = "", mLocalCurrencySign = "",
            mForeignCurrencySign = "";
    String m_senderId;
    Double forex_rate = 0.00;
    String mEmail;
    int error;
    String imagenamestr = "";
    String project_name = "";
    String project_description = "";
    String amount_needed = "";
    String amount_collected = "";
    String amount_balance = "";
    StringRequest stringRequest;
    SessionManager session;
    JSONObject jObj = null;
    String stquantity = "";
    String sessionId = "";
    int quantity = 0;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_description);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Product Description");

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("loading ...");
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        if (bundle != null) {

            itempriceid = bundle.get("itempriceid").toString();
        }
        session = new SessionManager(getApplicationContext());
        /** get user data from session*/
        HashMap<String, String> user = session.getUserDetails();
        m_senderId = user.get(SessionManager.KEY_SENDER_ID);
        mEmail = user.get(SessionManager.KEY_EMAIL);


        imagename = findViewById(R.id.imgPreview);
        txtproject_name = findViewById(R.id.txtText);
        txtProject_Desc = findViewById(R.id.txtDescription);
        txtAmount_needed = findViewById(R.id.txtPrice);
        btn_donate = findViewById(R.id.btnAdd);
        txtAmount_collected = findViewById(R.id.status);
        txtAmount_balance = findViewById(R.id.text_ratings);

        payment(itempriceid);
        HashMap<String, String> us = session.getTransId();
        sessionId = us.get(SessionManager.KEY_SESSIONID);

        btn_donate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                inputDialog();

                //Intent intent = new Intent(Activity_product_description.this, DeliveryAddress.class);

               /* intent.putExtra("CharityOrganisation", project_name);

                intent.putExtra("OrganisationId", itempriceid);*/

                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                //finish();
                //startActivity(intent);
            }
        });


    }

    void inputDialog() {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Order Quantity");
        alert.setMessage("Number Order");
        alert.setCancelable(false);
        final EditText edtQuantity = new EditText(this);
        int maxLength = 3;
        edtQuantity.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        edtQuantity.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(edtQuantity);

        alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                stquantity = edtQuantity.getText().toString();
                // when add button clicked add menu to order table in database
                if (!stquantity.equalsIgnoreCase("")) {
                    //quantity = Integer.parseInt(stquantity);
                    addtocart(itempriceid, stquantity);
                } else {

                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // when cancel button clicked close dialog
                dialog.cancel();

            }
        });

        alert.show();
    }

    public void payment(final String itempriceid) {


        pDialog.setMessage("Processing...");

        showDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://mangumangu.com/json/getsmartinvestprojects.aspx",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        hideDialog();
                        try {
                            JSONArray json = new JSONArray(response);
// ...

                            for (int i = 0; i < json.length(); i++) {


                                jObj = json.getJSONObject(i);

                                imagenamestr = jObj.getString("ProjectPhoto");
                                project_name = jObj.getString("ManguProjectName");
                                project_description = jObj.getString("ManguProjectDescription");
                                amount_needed = jObj.getString("UGX");
                                //amount_balance =  jObj.getString("UnitCODE");


                            }

                            Glide.with(getApplication())
                                    .load("https://mangumangu.com/Shop/i/" + imagenamestr)
                                    .into(imagename);

                            txtproject_name.setText(project_name);
                            txtProject_Desc.setText(project_description);

                            /*BigDecimal fmt_amount_needed  = new BigDecimal(amount_needed, MathContext.DECIMAL64);
                            DecimalFormat fmt = new DecimalFormat("#,###.##");
                            String amount_needed = fmt.format(fmt_amount_needed);
                            txtAmount_needed.setText("$"+amount_needed);

                            BigDecimal fmt_amount_collected  = new BigDecimal(amount_collected, MathContext.DECIMAL64);
                            String amount_collected = fmt.format(fmt_amount_collected);
                            txtAmount_collected.setText("$"+amount_collected);
                            BigDecimal fmt_amount_balance  = new BigDecimal(amount_balance, MathContext.DECIMAL64);

                            String amount_balance = fmt.format(fmt_amount_balance);*/
                            txtAmount_balance.setText("$" + amount_balance);


                            error = jObj.getInt("ResultsID");
                            if (error == 1) {
                                // Launch main activity


                            } else if (error == 3) {
                                // Error in processing. Get the error message
                                String msg = jObj.getString("Message");
                                Toast.makeText(getApplicationContext(),
                                        msg, Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Server Error occurred", Toast.LENGTH_LONG).show();

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
                params.put("itempriceid", itempriceid);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    public void addtocart(final String itempriceid, final String Qty) {


        pDialog.setMessage("Processing...");

        showDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://mangumangu.com/json/AddSmartInvestProjectCart.aspx",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideDialog();
                        try {
                            JSONObject jObj = new JSONObject(response);
                            error = jObj.getInt("ResultsID");
                            if (error > 0) {
                                // Launch main activity
                                Intent intent = new Intent(Activity_SmartInvestProduct_description.this, DeliveryAddressFragment.class);

                                startActivity(intent);
                                finish();

                            } else {
                                // Error in processing. Get the error message
                                Toast.makeText(getApplicationContext(), "Some Error Occurred", Toast.LENGTH_LONG).show();
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
                params.put("ManguProjectID", itempriceid);
                params.put("MoneySenderID", m_senderId);
                params.put("Quantity ", Qty);
                params.put("UsessionID", sessionId);
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
        getMenuInflater().inflate(R.menu.product_details, menu);
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
                Intent intent = new Intent(Activity_SmartInvestProduct_description.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                overridePendingTransition(R.anim.open_main, R.anim.close_next);
                startActivity(intent);
                return true;
        }
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.cart) {
            Intent intent = new Intent(Activity_SmartInvestProduct_description.this, ProductCartList.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            overridePendingTransition(R.anim.open_main, R.anim.close_next);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        Intent intent = new Intent(Activity_SmartInvestProduct_description.this, SmartInvestProjectList.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }


}





