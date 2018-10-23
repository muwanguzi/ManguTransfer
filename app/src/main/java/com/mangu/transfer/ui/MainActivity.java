package com.mangu.transfer.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
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
import com.mangu.transfer.adapter.AdapaterGridView;
import com.mangu.transfer.adapter.CustomHttpConnection;
import com.mangu.transfer.db.Constant;
import com.mangu.transfer.db.SessionManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;


    GridView gridview;
    TextView tv;
    AdapaterGridView gridviewAdapter;
    ArrayList<GridViewItem> data = new ArrayList<>();

    int m_senderId = 0;
    int mres = 0;
    String muser_name_Input = "";
    String mold_password_Input = "";
    String mnew_password_Input = "";
    String username = "";
    String mUser_rate = "", mLocalCurrencyCode = "",
            mForeignCurrencyCode = "", mLocalCurrencySign = "", sessionId = "",
            mForeignCurrencySign = "";
    double forex_rate = 0.0;

    SessionManager session;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setTitle("Home");

        gridview = findViewById(R.id.gridView1);
        tv = findViewById(R.id.username);
        // Session class instance
        session = new SessionManager(this);

        session.checkLogin();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();
        HashMap<String, String> us = session.getTransId();
        // name

        String m_senderParam = user.get(SessionManager.KEY_SENDER_ID);
        username = user.get(SessionManager.KEY_EMAIL);
        sessionId = us.get(SessionManager.KEY_SESSIONID);

        m_senderId = Integer.valueOf(m_senderParam);

        tv.setText(" " + username + " ");


        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> arg0, final View view, final int position, final long id) {

                switch (position) {
                    case 0:

                        if (Build.VERSION.SDK_INT < 23) {
                            //Do not need to check the permission
                            Intent intent1 = new Intent(MainActivity.this, Activity_send_to_bank.class);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent1);
                            finish();
                            overridePendingTransition(R.anim.open_main, R.anim.close_next);
                        } else if (checkAndRequestPermissions()) {
                            //If you have already permitted the permission
                            Intent intent1 = new Intent(MainActivity.this, Activity_send_to_bank.class);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent1);
                            finish();
                            overridePendingTransition(R.anim.open_main, R.anim.close_next);
                        }

                        break;

                    case 1:
                        if (Build.VERSION.SDK_INT < 23) {
                            //Do not need to check the permission
                            Intent intent1 = new Intent(MainActivity.this, Activity_send_to_many.class);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent1);
                            finish();
                            overridePendingTransition(R.anim.open_main, R.anim.close_next);
                        } else if (checkAndRequestPermissions()) {
                            //If you have already permitted the permission
                            Intent intent1 = new Intent(MainActivity.this, Activity_send_to_many.class);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent1);
                            finish();
                            overridePendingTransition(R.anim.open_main, R.anim.close_next);
                        }

                        break;
                    case 2:
                        if (Build.VERSION.SDK_INT < 23) {
                            //Do not need to check the permission
                            Intent intent1 = new Intent(MainActivity.this, SmartInvestProjectList.class);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent1);
                            finish();
                            overridePendingTransition(R.anim.open_main, R.anim.close_next);
                        } else if (checkAndRequestPermissions()) {
                            //If you have already permitted the permission
                            Intent intent1 = new Intent(MainActivity.this, SmartInvestProjectList.class);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent1);
                            finish();
                            overridePendingTransition(R.anim.open_main, R.anim.close_next);
                        }
                        break;

                    case 3:
                        if (Build.VERSION.SDK_INT < 23) {
                            //Do not need to check the permission
                            Intent intent1 = new Intent(MainActivity.this, ProductList.class);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent1);
                            finish();
                            overridePendingTransition(R.anim.open_main, R.anim.close_next);
                        } else if (checkAndRequestPermissions()) {
                            //If you have already permitted the permission
                            Intent intent1 = new Intent(MainActivity.this, ProductList.class);
                            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent1);
                            finish();
                            overridePendingTransition(R.anim.open_main, R.anim.close_next);
                        }
                        break;
                    default:

                        break;
                }


            }
        });


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);


        TextView nav_email = header.findViewById(R.id.textView);

        nav_email.setText(username);
        data.add(new GridViewItem(getResources().getString(R.string.send_to_bank), getResources().getDrawable(R.drawable.ic_send2one)));
        data.add(new GridViewItem(getResources().getString(R.string.send_to_many), getResources().getDrawable(R.drawable.ic_send2many)));
        data.add(new GridViewItem(getResources().getString(R.string.farm_for_hire), getResources().getDrawable(R.drawable.ic_firm4hire)));
        data.add(new GridViewItem(getResources().getString(R.string.charity), getResources().getDrawable(R.drawable.ic_charity)));

        setDataAdapter();
        getForexrate(username);
    }


    public void getForexrate(final String mEmail) {
        //pDialog.setMessage("Processing...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.RATEURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hideDialog();
                        try {
                            JSONObject jObj = new JSONObject(response);
                            mUser_rate = jObj.getString("Rate");
                            forex_rate = Double.valueOf(mUser_rate);
                            mLocalCurrencyCode = jObj.getString("currAbbrvA");
                            mForeignCurrencyCode = jObj.getString("currAbbrvB");
                            mLocalCurrencySign = jObj.getString("currCODEA");
                            mForeignCurrencySign = jObj.getString("currCODEB");
                            // AfterGetCurrency();
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
                            //hideDialog();
                        } else if (error instanceof NoConnectionError) {
                            //TODO
                            Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.nonetworkconection),
                                    Toast.LENGTH_LONG).show();
                            //hideDialog();
                        } else if (error instanceof AuthFailureError) {
                            //TODO
                            Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.authentification),
                                    Toast.LENGTH_LONG).show();
                            //hideDialog();
                        } else if (error instanceof ServerError) {
                            //TODO
                            Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.servererror),
                                    Toast.LENGTH_LONG).show();
                            //hideDialog();
                        } else if (error instanceof NetworkError) {

                            //TODO
                            Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.networkerror),
                                    Toast.LENGTH_LONG).show();
                            //hideDialog();
                        } else if (error instanceof ParseError) {
                            //TODO
                            Toast.makeText(getBaseContext(), getBaseContext().getString(R.string.parseerror),
                                    Toast.LENGTH_LONG).show();
                            //hideDialog();
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

    private boolean checkAndRequestPermissions() {
        int contactPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (contactPermission != PackageManager.PERMISSION_GRANTED) {

            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);

        }
        if (!listPermissionsNeeded.isEmpty()) {

            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_READ_CONTACTS);
            return false;
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case REQUEST_READ_CONTACTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //Permission Granted Successfully. Write working code here.

                } else {
                    //You did not accept the request can not use the functionality.
                }
                break;
        }
    }

    // Set the Data Adapter
    private void setDataAdapter() {
        gridviewAdapter = new AdapaterGridView(this, R.layout.fragment_list_item, data);
        gridview.setAdapter(gridviewAdapter);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.statement4people) {
            Intent intent = new Intent(MainActivity.this, Activity_statement_4_people.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            return true;
        } else if (id == R.id.charity_statement) {
            Intent intent = new Intent(MainActivity.this, Activity_charity_statement.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            return true;
        } else if (id == R.id.bank_statement) {
            Intent intent = new Intent(MainActivity.this, Activity_bank_statement.class);
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


    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_camera) {
            // Handle the camera action

            if (Build.VERSION.SDK_INT < 23) {
                //Do not need to check the permission
                Intent intent = new Intent(MainActivity.this, Activity_send_to_bank.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.open_main, R.anim.close_next);
            } else if (checkAndRequestPermissions()) {
                //If you have already permitted the permission
                Intent intent = new Intent(MainActivity.this, Activity_send_to_bank.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.open_main, R.anim.close_next);
            }

        } else if (id == R.id.nav_gallery) {
            if (Build.VERSION.SDK_INT < 23) {
                //Do not need to check the permission
                Intent intent = new Intent(MainActivity.this, Activity_send_to_many.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.open_main, R.anim.close_next);
            } else if (checkAndRequestPermissions()) {
                //If you have already permitted the permission
                Intent intent = new Intent(MainActivity.this, Activity_send_to_many.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.open_main, R.anim.close_next);
            }

        } else if (id == R.id.nav_slideshow) {
            if (Build.VERSION.SDK_INT < 23) {
                //Do not need to check the permission
                Intent intent = new Intent(MainActivity.this, SmartInvestProjectList.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.open_main, R.anim.close_next);
            } else if (checkAndRequestPermissions()) {
                //If you have already permitted the permission
                Intent intent = new Intent(MainActivity.this, SmartInvestProjectList.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.open_main, R.anim.close_next);
            }
        } else if (id == R.id.nav_manage) {
            if (Build.VERSION.SDK_INT < 23) {
                //Do not need to check the permission
                Intent intent = new Intent(MainActivity.this, ProductList.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.open_main, R.anim.close_next);
            } else if (checkAndRequestPermissions()) {
                //If you have already permitted the permission
                Intent intent = new Intent(MainActivity.this, ProductList.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.open_main, R.anim.close_next);
            }

        }/* else if (id == R.id.nav_project) {
            if (Build.VERSION.SDK_INT < 23) {
                //Do not need to check the permission
                Intent intent = new Intent(MainActivity.this, Activity_my_farm_project.class);
                intent.putExtra("mcurrency", mForeignCurrencyCode);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition (R.anim.open_main, R.anim.close_next);
            } else if (checkAndRequestPermissions()) {
                //If you have already permitted the permission
                Intent intent = new Intent(MainActivity.this, Activity_my_farm_project.class);
                intent.putExtra("mcurrency", mForeignCurrencyCode);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
                overridePendingTransition (R.anim.open_main, R.anim.close_next);
            }

        }*/ else if (id == R.id.nav_share) {
            if (Build.VERSION.SDK_INT < 23) {
                //Do not need to check the permission
                   /* try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
                    }*/

                String shareBody = "http://play.google.com/store/apps/details?id=" + getPackageName();
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "APP NAME (Open it in Google Play Store to Download the Application)");

                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));


            } else if (checkAndRequestPermissions()) {
                String shareBody = "http://play.google.com/store/apps/details?id=" + getPackageName();
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "APP NAME (Open it in Google Play Store to Download the Application)");

                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }

        } else if (id == R.id.nav_change_password) {

            // inflate alert dialog xml
            LayoutInflater li = LayoutInflater.from(this);
            View dialogView = li.inflate(R.layout.change_password_dialog, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            // set title
            alertDialogBuilder.setTitle("Change Password");
            // set custom dialog icon
            alertDialogBuilder.setIcon(R.drawable.change_password);
            // set custom_dialog.xml to alertdialog builder
            alertDialogBuilder.setView(dialogView);
            final EditText user_name_Input = dialogView
                    .findViewById(R.id.user_name);
            final EditText old_password_Input = dialogView
                    .findViewById(R.id.old_password);
            final EditText new_password_Input = dialogView
                    .findViewById(R.id.new_password);

            final CheckBox show_hide_old_password = findViewById(R.id.show_hide_old_password);
            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    // get user input and set it to etOutput
                                    // edit text old_password_Input
                                    muser_name_Input = user_name_Input.getText().toString();
                                    mold_password_Input = old_password_Input.getText().toString();
                                    mnew_password_Input = new_password_Input.getText().toString();

                                    if (mold_password_Input.isEmpty()) {
                                        old_password_Input.requestFocus();
                                        old_password_Input.setError("Please enter old password");
                                    } else if (mnew_password_Input.isEmpty()) {
                                        new_password_Input.requestFocus();
                                        new_password_Input.setError("Please enter new password");
                                    } else {

                                        MainActivity.validateUserTask task = new MainActivity.validateUserTask();
                                        task.execute(username, mold_password_Input, mnew_password_Input);

                                    }
                               /*     show_hide_old_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                        @Override
                                        public void onCheckedChanged(CompoundButton button,
                                                                     boolean isChecked) {

                                            // If it is checkec then show password else hide
                                            // password
                                            if (isChecked) {

                                                show_hide_old_password.setText(R.string.hide_pwd);// change
                                                // checkbox
                                                // text

                                                old_password_Input.setInputType(InputType.TYPE_CLASS_TEXT);
                                                old_password_Input.setTransformationMethod(HideReturnsTransformationMethod
                                                        .getInstance());// show password
                                            } else {
                                                show_hide_old_password.setText(R.string.show_pwd);// change
                                                // checkbox
                                                // text

                                                old_password_Input.setInputType(InputType.TYPE_CLASS_TEXT
                                                        | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                                                old_password_Input.setTransformationMethod(PasswordTransformationMethod
                                                        .getInstance());// hide password

                                            }

                                        }
                                    });*/
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    dialog.cancel();
                                }
                            });
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();


        } else if (id == R.id.nav_send) {
            session.logoutUser();
            finish();
            return true;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class validateUserTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Processing...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("username", params[0]));
            postParameters.add(new BasicNameValuePair("Oldpassword", params[1]));
            postParameters.add(new BasicNameValuePair("Newpassword", params[2]));

            String res = null;
            try {

                String response = null;
                response = CustomHttpConnection.executeHttpPost(Constant.CHANGE_PASSWORD, postParameters);
                res = response;
                res = res.replaceAll("\\s+", "");
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

                    mres = obj.getInt("ResultsID");
                    String msg = obj.getString("Message");

                    if (mres == 3) {
                        //navigate to Main Menu
                        Toast.makeText(MainActivity.this, "Your password has been successfully changed",
                                Toast.LENGTH_LONG).show();

                    } else if (mres == 2) {
                        Toast.makeText(MainActivity.this, "Old password is wrong!!",
                                Toast.LENGTH_LONG).show();

                    } else if (mres == 1) {
                        Toast.makeText(MainActivity.this, "Wrong Username",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                }
            } catch (Throwable t) {
                //Log.e("My App", "Could not parse malformed JSON: \"" + json + "\"");
            }

        }//close onPostExecute


    }
}
