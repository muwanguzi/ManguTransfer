package com.mangu.transfer.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mangu.transfer.R;
import com.mangu.transfer.adapter.CustomHttpConnection;
import com.mangu.transfer.db.Constant;
import com.mangu.transfer.db.SessionManager;
import com.mangu.transfer.util.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A login screen that offers login via email/password.
 */


public class LoginActivity extends AppCompatActivity {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private static final String TAG = LoginActivity.class.getSimpleName();
    static String USessionID = "";
    static String m_email = "";
    static String phone = "";
    static int m_senderId = 0;
    final Context context = this;
    EditText emailpwd;
    String text, Emailpwd;
    String email, password;
    String mCountryCategory = "";
    TextView tv, tv_cartlist;
    String response = null;
    int mres = 0;
    CheckBox show_hide_password;
    private Button btnLogin, btnForgotPass;
    private Button btnLinkToRegister;
    //private TextView txtForgetpwd;
    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pDialog = new ProgressDialog(this);

        pDialog.setTitle("Logging in");
        pDialog.setCancelable(true);

        setContentView(R.layout.activity_login);
        pDialog.show();
        // Set up the login form.
        inputEmail = findViewById(R.id.email);
        inputPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);
        btnLinkToRegister = findViewById(R.id.btnLinkToRegisterScreen);
        tv = findViewById(R.id.forgotpassword);

        show_hide_password = findViewById(R.id.show_hide_password);


        pDialog.hide();
        // Session Manager
        session = new SessionManager(getApplicationContext());
        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        if (bundle != null) {

            mCountryCategory = (String) bundle.get("CountryCategory");
        }

        final Context context = this;
        // Progress dialog


        if (session.isLoggedIn()) {

            Intent intent = new Intent(LoginActivity.this,
                    MainActivity.class);

            startActivity(intent);
            finish();
        }

        // Set check listener over checkbox for showing and hiding password
        show_hide_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton button,
                                         boolean isChecked) {

                // If it is checkec then show password else hide
                // password
                if (isChecked) {

                    show_hide_password.setText(R.string.hide_pwd);// change
                    // checkbox
                    // text

                    inputPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                    inputPassword.setTransformationMethod(HideReturnsTransformationMethod
                            .getInstance());// show password
                } else {
                    show_hide_password.setText(R.string.show_pwd);// change
                    // checkbox
                    // text

                    inputPassword.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    inputPassword.setTransformationMethod(PasswordTransformationMethod
                            .getInstance());// hide password

                }

            }
        });
        // Check if user is already logged in or not

        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                email = inputEmail.getText().toString();
                password = inputPassword.getText().toString();
                if (email.isEmpty()) {
                    inputEmail.requestFocus();
                    inputEmail.setError("Please enter email");
                } else if (!isValidEmail(email)) {
                    inputEmail.setError("Invalid Email");
                } else if (password.isEmpty()) {
                    inputPassword.requestFocus();
                    inputPassword.setError("Please enter email");
                } else if (!isValidPassword(password)) {
                    inputPassword.setError("Invalid Password");
                } else {
                    // Prompt user to enter credentials

                    m_email = email;

                    validateUserTask task = new validateUserTask();
                    task.execute(email, password);
                    //checkLogin(email, password);

                    //  checkLogin(email, password);
                }
            }

        });


        try {

            JSONObject json = new LoginActivity.JSONParse().execute().get();
            USessionID = json.getString("Message");

            session.storeSessionID(USessionID);

        } catch (Exception e) {

        }
        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        SignupActivity.class);
                startActivity(i);
                finish();
            }
        });

        tv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent i = new Intent(getApplicationContext(),
                        Activity_forgotpwd.class);
                startActivity(i);
            }
        });

    }


    public String getData() {
        return m_email;
    }

    public int getSendId() {
        return m_senderId;
    }

    /**
     * function to verify login details in mysql db
     */

    // validating email id
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    // validating password with retype password
    private boolean isValidPassword(String password) {
        return password != null && password.length() > 2;
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
    public void onBackPressed() {
        finish();
        System.exit(0);


    }

    public void Regalert(String email) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setIcon(R.drawable.alert);
        alertDialogBuilder.setTitle("Your Account was not activated");
        alertDialogBuilder.setMessage("Hello, " + email + "\n" +
                "Please check your Inbox to activate your account. Check your Spam or Junk section in case you can not see the email in your Inbox"
        ).setCancelable(false);
        // set positive button: Yes message
        alertDialogBuilder.setPositiveButton("Got it", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // go to a new activity of the app


            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();
    }

    private class JSONParse extends AsyncTask<String, String, JSONObject> {


        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(LoginActivity.this);
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

    private class validateUserTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Login...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("username", params[0]));
            postParameters.add(new BasicNameValuePair("password", params[1]));
            String res = null;
            try {
                response = CustomHttpConnection.executeHttpPost(Constant.LOGIN, postParameters);
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
                        Toast.makeText(LoginActivity.this, "Sorry!! Incorrect Username or Password",
                                Toast.LENGTH_LONG).show();

                    } else if (mres == 4) {

                        Regalert(email);

                    } else {
                        Toast.makeText(LoginActivity.this, "Sorry!! an error has occurred ",
                                Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                }
                //mres = obj.getInt("MoneySenderID");
                if (obj.getInt("MoneySenderID") > 0) {
                    //navigate to Main Menu

                    m_senderId = obj.getInt("MoneySenderID");


                    if (m_senderId > 0) {
                        String m_senderParam = String.valueOf(m_senderId);
                        session.createLoginSession(m_senderParam, email);


                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    }
                } else {

                    mres = obj.getInt("ResultsID");
                    String msg = obj.getString("Message");

                    if (mres == 4) {
                        Toast.makeText(LoginActivity.this, msg,
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Sorry!! an error has occurred ",
                                Toast.LENGTH_LONG).show();
                    }
                }

            } catch (Throwable t) {
                //Log.e("My App", "Could not parse malformed JSON: \"" + json + "\"");
            }

        }//close onPostExecute


    }// close
}
