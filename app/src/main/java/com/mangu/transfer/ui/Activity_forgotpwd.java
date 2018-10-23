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
import android.view.View;
import android.widget.Button;
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
import com.mangu.transfer.db.Constant;
import com.mangu.transfer.db.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class Activity_forgotpwd extends AppCompatActivity {

    static int m_senderId;
    final Context context = this;
    public ProgressDialog pDialog;
    String m_email;
    Button btnFpass;
    TextView Email;
    String mEmail = "";
    String sessionId = "";
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpwd);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Recovery Password");
        pDialog = new ProgressDialog(this);
        pDialog.setTitle("Checking email...");
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        btnFpass = findViewById(R.id.btnPass);
        // name

        Email = findViewById(R.id.f_email);
// Link to Register Screen


        btnFpass.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                m_email = Email.getText().toString();
                ifemailExist(m_email);
            }
        });


    }

    public void ifemailExist(final String m_email) {

        pDialog.setMessage("Processing...");

        showDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.FORGOTTENPASS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(final String response) {

                        hideDialog();
                        try {
                            //Toast.makeText(Activity_forgotpwd.this,response,Toast.LENGTH_LONG).show();


                            JSONObject jObj = new JSONObject(response);
                            int error = jObj.getInt("ResultsID");
                            String Message = jObj.getString("Message");
                            if (error == 1) {
                                // Launch main activity
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Activity_forgotpwd.this);
                                alertDialogBuilder.setMessage(Message);
                                alertDialogBuilder.setPositiveButton("Got it",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                finish();
                                            }
                                        });


                                AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();
                                // Toast.makeText(getApplicationContext(),response, Toast.LENGTH_LONG).show();

                            } else if (error == 0) {
                                // Error in processing. Get the error message
                                error = jObj.getInt("ResultsID");
                                Toast.makeText(getApplicationContext(),
                                        Message, Toast.LENGTH_LONG).show();

                            } else if (error == 2) {
                                Toast.makeText(getApplicationContext(),
                                        Message, Toast.LENGTH_LONG).show();

                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "An error has occurred", Toast.LENGTH_LONG).show();

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
                params.put("Email", m_email);
                return params;
            }

        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();

        Intent intent = new Intent(Activity_forgotpwd.this, LoginActivity.class);
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