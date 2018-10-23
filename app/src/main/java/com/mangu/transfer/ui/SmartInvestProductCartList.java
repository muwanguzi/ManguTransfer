package com.mangu.transfer.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
import com.mangu.transfer.db.Constant;
import com.mangu.transfer.db.SessionManager;
import com.mangu.transfer.model.Custom_list_model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SmartInvestProductCartList extends AppCompatActivity {

    private static final String TAG = SmartInvestProductCartList.class.getSimpleName();

    // Movies json url
    private static final String url = "";
    final Context context = this;
    public ProgressDialog pDialog;
    ImageView dialogButtonPayWithMm;
    ImageView dialogButtonPayWithMmPayWithVisa;
    AlertDialog dialog = null;
    String mCartCount = "";
    String cart_amount = "";
    String m_moneySenderId = "";
    double Doublecart_amount = 0.00;
    String mStrcart_amount = "";
    String sessionId = "";
    String phone_number = "", amount_x = "";
    String cartId;
    JSONObject jObj;
    String masterSession = "";
    private List<Custom_list_model> FarmProjectList = new ArrayList<Custom_list_model>();
    private ListView listView;
    private CustomListAdapter adapter;
    private SessionManager session;
    private TextView emptyView;
    private TextView checkout;
    private TextView clearCart;
    private String currency = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_cart_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(true);
        dialog = new AlertDialog.Builder(this)
                .setTitle("Pay with")
                .setView(R.layout.pay_with)
                .create();

        dialog.setCancelable(false);
        getSupportActionBar().setTitle("Cart List");

        session = new SessionManager(this);

        HashMap<String, String> us = session.getTransId();
        // name
        sessionId = us.get(SessionManager.KEY_SESSIONID);


        checkout = findViewById(R.id.Checkout);
        clearCart = findViewById(R.id.btnClear);
        emptyView = findViewById(R.id.empty_view);
        listView = findViewById(R.id.list);
        adapter = new CustomListAdapter(this, FarmProjectList);
        session = new SessionManager(getApplicationContext());
        pDialog = new ProgressDialog(this);

        pDialog.setMessage("Loading...");
        listView.setAdapter(adapter);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            mCartCount = extras.getString("Number_Of_Recipients");
            cart_amount = extras.getString("Amount");
            currency = extras.getString("Fcurrency");
            m_moneySenderId = extras.getString("MoneySenderID");
            //sessionId  = extras.getString("USessionID");
            masterSession = extras.getString("MasterSession");


        }

        checkout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                paywith(dialog);


            }
        });
        getFarmProject(sessionId);

        pDialog.show();
        /*listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener()
                {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view,int position, long arg3) {
                        String selected = listView.getItemAtPosition(position).toString();
                        TextView textView = (TextView) view.findViewById(R.id.id);
                        String CartID = textView.getText().toString();

                        HashMap<String, String> us = session.getTransId();
                        String sessionId = us.get(SessionManager.KEY_SESSIONID);

                        deleteListItem(CartID,sessionId);
                    }
                }
        );*/

        /** Setting the adapter to the ListView */
        View.OnClickListener continue_listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              /*  HashMap<String, String> us = session.getTransId();
                // name
                String sessionId = us.get(SessionManager.KEY_SESSIONID);
                Doublecart_amount = Double.valueOf(cart_amount) + Double.valueOf(cart_amount)*0.05;

                mStrcart_amount = String.valueOf((Math.floor(Doublecart_amount * 100) / 100));
                        // String mCartNumber = Integer.valueOf(mCartCount).toString();
                Intent intent = new Intent(ProductCartList.this,Activity_connect_send_to_many.class);
                intent.putExtra("USessionID", sessionId);
                intent.putExtra("Fcurrency", currency);
                intent.putExtra("Number_Of_Recipients",mCartCount);
                intent.putExtra("Amount", mStrcart_amount);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                finish();
                startActivity(intent);*/

                //post_then_checkout(m_moneySenderId);
            }
        };
//        m_master_button.setOnClickListener(continue_listener);
    }

    public void paywith(final AlertDialog mydialog) {
        mydialog.show();

        dialogButtonPayWithMm = dialog.findViewById(R.id.paymobilemoney);
        //   Button dialogButtonPayWithMmAddRec = (Button) dialog.findViewById(R.id.dialogButtonPayWithMmAddRec);
        dialogButtonPayWithMmPayWithVisa = dialog.findViewById(R.id.paywithvisa);


        // if button is clicked, close the custom dialog
        dialogButtonPayWithMm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DecimalFormat fmt = new DecimalFormat("0.00");

                Intent intent = new Intent(SmartInvestProductCartList.this, Pay_with_mobile_money.class);
                //double mDoubleAmountEntered = Double.valueOf(m_amountEntered);


                startActivity(intent);

                dialog.dismiss();
            }
        });


        dialogButtonPayWithMmPayWithVisa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DecimalFormat fmt = new DecimalFormat("0.00");

                Intent intent = new Intent(SmartInvestProductCartList.this, Activity_connect_send_to_org.class);

                startActivity(intent);

                dialog.dismiss();

            }
        });

    }

    public void post_then_checkout(final String m_moneySenderId) {

        pDialog.setMessage("Processing...");

        showDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.SEND_TO_MANY_CHECKOUT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        hideDialog();
                        try {
                            JSONObject jObj = new JSONObject(response);
                            int msgs = jObj.getInt("ResultsID");
                            if (msgs == 1) {
                                HashMap<String, String> us = session.getTransId();
                                // name
                                String sessionId = us.get(SessionManager.KEY_SESSIONID);
                                Doublecart_amount = Double.valueOf(cart_amount) + Double.valueOf(cart_amount) * 0.05;

                                mStrcart_amount = String.valueOf((Math.floor(Doublecart_amount * 100) / 100));
                                // String mCartNumber = Integer.valueOf(mCartCount).toString();
                                Intent intent = new Intent(SmartInvestProductCartList.this, Activity_connect_send_to_many.class);
                                intent.putExtra("USessionID", sessionId);
                                intent.putExtra("Fcurrency", currency);
                                intent.putExtra("Number_Of_Recipients", phone_number);
                                intent.putExtra("Amount", amount_x);
                                intent.putExtra("MasterSession", masterSession);

                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                finish();
                                startActivity(intent);


                            } else {
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

    public void getFarmProject(final String sessionId) {
        //pDialog.setMessage("Processing...");

        StringRequest cartlist = new StringRequest(Request.Method.POST, Constant.SMARTVIEWCARTLIST,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if (response.isEmpty() || response == "") {
                            hideDialog();
                            // emptyView.setVisibility(View.VISIBLE);
                        }

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            DecimalFormat formatter = new DecimalFormat("#,###.00");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                Custom_list_model custom_list_model = new Custom_list_model();


                                custom_list_model.setTitle("Tinned Fish");
                                custom_list_model.setDescription("Good staff");
                                custom_list_model.setAdditional_info("8000");
                                custom_list_model.setOther_details("Time");
                                custom_list_model.setM_id("nice");

                                FarmProjectList.add(custom_list_model);
                                adapter.notifyDataSetChanged();

                                hideDialog();

                               /* phone_number =obj.getString("PhoneNo");
                                amount_x = obj.getString("OverallTotal");
                                // adding charityOrganisation to movies array
                                FarmProjectList.add(custom_list_model);
                                adapter.notifyDataSetChanged();*/
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
                        Toast.makeText(SmartInvestProductCartList.this, error.toString(), Toast.LENGTH_LONG).show();
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
        requestQueue.add(cartlist);
    }

    public void deleteListItem(final String CartID, final String sessionId) {
        pDialog.setMessage("Processing...");
        showDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.DELETECARTIEM,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideDialog();
                        try {
                            jObj = new JSONObject(response);
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
                params.put("USessionID", sessionId);
                params.put("CartID", CartID);
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
        getMenuInflater().inflate(R.menu.cartlist, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        if (id == R.id.cartlist) {
            Intent intent = new Intent(SmartInvestProductCartList.this, SmartInvestProductCartList.class);
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

        Intent intent = new Intent(SmartInvestProductCartList.this, Activity_send_to_many.class);
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
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


}
