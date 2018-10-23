
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

public class Activity_connect_cart_hire extends AppCompatActivity {

    public ProgressDialog pDialog;
    public SessionManager session;
    WebView webView;
    String final_email = "";
    String transId;
    String recPhoneNumber = "";
    String final_amt = "";
    String mFcurrency = "";
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_send_to_many);
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
        /**
         * declare a string**/
        //progressBar = (ProgressBar) findViewById(R.id.my_progressBar);
        webView = findViewById(R.id.webkit);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new com.mangu.transfer.ui.Activity_connect_cart_hire.myWebClient());

        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            recPhoneNumber = extras.getString("Number_Of_Recipients");
            final_amt = extras.getString("Amount");
            transId = extras.getString("USessionID");
            mFcurrency = extras.getString("Fcurrency");

        }

        webView.addJavascriptInterface(new com.mangu.transfer.ui.Activity_connect_cart_hire.JavaScriptInterface(transId, recPhoneNumber, final_amt, mFcurrency), "JSInterface");
        webView.addJavascriptInterface(new com.mangu.transfer.ui.Activity_connect_cart_hire.JavaScriptAndroidInterface(this), "JSAInterface");

        webView.loadUrl(Constant.MASTERCARD_API_MANY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /** Inflate the menu; this adds items to the action bar if it is present.
         *
         */
        getMenuInflater().inflate(R.menu.farm_menu, menu);
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
                Intent intent = new Intent(Activity_connect_cart_hire.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.open_main, R.anim.close_next);
                return true;
        }
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.available_project) {
            Intent intent = new Intent(Activity_connect_cart_hire.this, Activity_AvailableFarm_projects.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            return true;
        } else if (id == R.id.farm_statement) {
            Intent intent = new Intent(Activity_connect_cart_hire.this, Activity_farm4hire_statement.class);
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

        Intent intent = new Intent(com.mangu.transfer.ui.Activity_connect_cart_hire.this, Activity_send_to_farm_for_hire.class);
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

            //progressBar.setVisibility(view.GONE);
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
        public void moveToNextScreen() {
            android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(context);
            // Setting Dialog Title
            //AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            // Setting Dialog Title
            alertDialog.setTitle("Alert");
            // Setting Dialog Message
            alertDialog.setMessage("You have successfully sent money to " + recPhoneNumber);
            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //Move to Next screen
                            /*Intent chnIntent = new Intent(Activity_connect_cart_hire.this, ActivityCharityOrgList.class);
                            startActivity(chnIntent);
                            finish();*/
                        }
                    });
            // Showing Alert Message
            alertDialog.show();
        }

    }

    private class GetId extends AsyncTask<String, String, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected JSONObject doInBackground(String... args) {
            JSONParser jParser = new JSONParser();

            /** Getting JSON from URL **/

            String url = String.format("http://www.mangutransfer.com/mangu_server/payment_id.php?final_email=%s", final_email);
            JSONObject json_id = jParser.getJSONFromUrl(url);
            return json_id;
        }

        @Override
        protected void onPostExecute(JSONObject Json) {
        }
    }

}
