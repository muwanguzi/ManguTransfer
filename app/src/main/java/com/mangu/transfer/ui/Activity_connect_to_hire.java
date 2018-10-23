package com.mangu.transfer.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.mangu.transfer.R;
import com.mangu.transfer.db.Constant;
import com.mangu.transfer.db.SessionManager;
import com.mangu.transfer.util.JSONParser;

import org.json.JSONObject;

import java.util.HashMap;

public class Activity_connect_to_hire extends AppCompatActivity {
    WebView webView;
    String final_email = "";
    String transId;
    String recPhoneNumber = "";
    String final_amt = "";
    String USessionID = "";
    String mFcurrency = "";
    ProgressBar progressBar;
    private ProgressDialog pDialog;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_send_to_org);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Payment Gateway");
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
        pDialog = new ProgressDialog(this);
        pDialog.setTitle("Handing over to Payment Gateway");
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);
        session = new SessionManager(getApplicationContext());

        /**
         * declare a string**/

        HashMap<String, String> user = session.getUserDetails();
        String m_senderParam = user.get(SessionManager.KEY_SENDER_ID);
        String username = user.get(SessionManager.KEY_EMAIL);

        progressBar = findViewById(R.id.my_progressBar);
        webView = findViewById(R.id.webkit);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new myWebClient());
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            recPhoneNumber = extras.getString("Number_Of_Recipients");
            final_amt = extras.getString("Amount");
            transId = extras.getString("USessionID");
            mFcurrency = extras.getString("Fcurrency");
        }


        try {

            JSONObject json = new Activity_connect_to_hire.JSONParse().execute().get();
            USessionID = json.getString("Message");


        } catch (Exception e) {

        }
        webView.addJavascriptInterface(new JavaScriptInterface(transId, recPhoneNumber, final_amt, mFcurrency), "JSInterface");
        webView.addJavascriptInterface(new JavaScriptAndroidInterface(this), "JSAInterface");

        webView.loadUrl(Constant.MASTERCARD_API_HIRE);
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
                Intent intent = new Intent(Activity_connect_to_hire.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                overridePendingTransition(R.anim.open_main, R.anim.close_next);
                startActivity(intent);
                finish();
                return true;
        }
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.statement4people) {
            Intent intent = new Intent(Activity_connect_to_hire.this, Activity_statement_4_people.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            overridePendingTransition(R.anim.open_main, R.anim.close_next);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.charity_statement) {
            Intent intent = new Intent(Activity_connect_to_hire.this, Activity_charity_statement.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            overridePendingTransition(R.anim.open_main, R.anim.close_next);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.bank_statement) {
            Intent intent = new Intent(Activity_connect_to_hire.this, Activity_bank_statement.class);
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

        Intent intent = new Intent(Activity_connect_to_hire.this, Activity_farm4hire_statement.class);
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

    public class myWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            showDialog();
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub

            progressBar.setVisibility(View.VISIBLE);

            webView.loadUrl(url);
            return true;

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            hideDialog();

            progressBar.setVisibility(View.GONE);
        }
    }

    public class JavaScriptInterface {

        String transId;
        String recPhoneNumber;
        String final_amt;
        String mFcurrency;
        Context context;

        JavaScriptInterface(String transId, String recPhoneNumber, String final_amt, String mFcurrency) {
            this.transId = transId;
            this.recPhoneNumber = recPhoneNumber;
            this.final_amt = final_amt;
            this.mFcurrency = mFcurrency;
        }

        @JavascriptInterface
        public String getTransactionId() {
            return transId;
        }

        @JavascriptInterface
        public String getPhoneNumber() {
            return recPhoneNumber;
        }

        @JavascriptInterface
        public String getFinal_amt() {
            return final_amt;
        }

        @JavascriptInterface
        public String getmFcurrency() {
            return mFcurrency;
        }

    }

    public class JavaScriptAndroidInterface {

        public Context context;
        ProgressDialog progress;

        JavaScriptAndroidInterface(Context c) {
            context = c;
            progress = new ProgressDialog(c);
        }

        @JavascriptInterface
        public void transactionMsg() {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            // Setting Dialog Title
            alertDialog.setTitle("Alert");
            // Setting Dialog Message
            alertDialog.setMessage("Your transaction has been successful");
            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //Move to Next screen
                            Intent chnIntent = new Intent(Activity_connect_to_hire.this, Activity_charity_statement.class);
                            startActivity(chnIntent);
                        }
                    });

            // Showing Alert Message
            alertDialog.show();
        }


        @JavascriptInterface
        public void transactionErrorMsg() {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            // Setting Dialog Title
            alertDialog.setTitle("Alert");
            // Setting Dialog Message
            alertDialog.setMessage("Your transaction has failed");
            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //Move to Next screen
                            session.clearTransId();
                            session.storeSessionID(USessionID);
                            Intent chnIntent = new Intent(Activity_connect_to_hire.this, Activity_farm4hire_statement.class);
                            startActivity(chnIntent);
                        }
                    });

            // Showing Alert Message
            alertDialog.show();
        }

    }

    private class JSONParse extends AsyncTask<String, String, JSONObject> {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(Activity_connect_to_hire.this);
            pDialog.setMessage("Loading session ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            /** Getting JSON from URL
             *
             */
            JSONObject json = jParser.getJSONFromUrl(Constant.USESSIONID);

            return json;
        }

        @Override
        protected void onPostExecute(JSONObject Json) {
            pDialog.dismiss();
        }
    }
}