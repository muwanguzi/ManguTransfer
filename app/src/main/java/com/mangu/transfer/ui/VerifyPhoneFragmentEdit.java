package com.mangu.transfer.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.mangu.transfer.fragment.BaseFlagFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by FRANCIS on 03/05/2017.
 */

public class VerifyPhoneFragmentEdit extends BaseFlagFragment {
    private static final String TAG = VerifyPhoneFragment.class.getSimpleName();
    public EditText inputFirstName, inputSecondName, inputPhone;
    public String m_first_name, m_second_name;
    public String m_email, m_address, m_city;
    String m_password, m_confirm_password;
    private Button btnRegister;
    private Button btnLinkToLogin;
    private EditText inputEmail;
    private EditText confirmPassword;
    private EditText inputPassword;
    private String m_phone;
    private ProgressDialog pDialog;
    private SessionManager session;
    private RequestQueue queue;
    private String response;

    public VerifyPhoneFragmentEdit() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_fragment_flags, container, false);

        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        inputFirstName = rootView.findViewById(R.id.first_name);
        inputSecondName = rootView.findViewById(R.id.second_name);
        inputEmail = rootView.findViewById(R.id.email);
        inputPhone = rootView.findViewById(R.id.phone);
        inputPassword = rootView.findViewById(R.id.password);
        confirmPassword = rootView.findViewById(R.id.confirmpassword);
        btnRegister = rootView.findViewById(R.id.btnRegister);
        btnLinkToLogin = rootView.findViewById(R.id.btnLinkToLoginScreen);

        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);
        btnRegister.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                m_first_name = inputFirstName.getText().toString();
                m_second_name = inputSecondName.getText().toString();
                m_email = inputEmail.getText().toString();
                m_phone = inputPhone.getText().toString();
                m_password = inputPassword.getText().toString();
                m_confirm_password = confirmPassword.getText().toString();

                String CountryID = "1";
                m_city = " ";
                m_address = "";

                if (m_first_name.isEmpty()) {
                    inputFirstName.requestFocus();
                    inputFirstName.setError("Please Enter First Name");
                } else if (m_first_name == null) {
                    inputFirstName.requestFocus();
                    inputFirstName.setError("First Name Is Null");
                } else if (m_second_name.isEmpty()) {
                    inputSecondName.requestFocus();
                    inputSecondName.setError("Please Enter Second Name");
                } else if (m_second_name == null) {
                    inputSecondName.requestFocus();
                    inputSecondName.setError("Please Enter Second Name");
                } else if (m_email.isEmpty()) {
                    inputEmail.requestFocus();
                    inputEmail.setError("Please Enter Email");
                } else if (!isValidEmail(m_email)) {
                    inputEmail.requestFocus();
                    inputEmail.setError("Invalid Email");

                } else if (m_phone.isEmpty()) {
                    inputPhone.requestFocus();
                    inputPhone.setError("Please Enter Phone");
                } else if (m_phone == null) {

                    inputPhone.requestFocus();
                    inputPhone.setError("Phone Number Null");
                } else if (m_password.isEmpty() || m_password == null) {
                    inputPassword.requestFocus();
                    inputPassword.setError("Please Enter Password");
                } else if (m_confirm_password.isEmpty()) {
                    confirmPassword.requestFocus();
                    confirmPassword.setError("Please Confirm Password");
                } else if (m_password.equals(m_confirm_password)) {

                    confirmPassword.setError("Password Mismatch");

                } else {
                    //send();
                    registerUser(m_first_name, m_second_name, m_email, CountryID, m_city, m_address, m_phone, m_password);


                }
            }
        });
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), LoginActivity.class);

                startActivity(intent);
            }
        });
        initUI(rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initCodes(getActivity());
    }

    @Override
    protected void send() {
        //hideKeyboard(mPhoneEdit);
        mPhoneEdit.setError(null);
        m_phone = validate();
        if (m_phone == null) {
            mPhoneEdit.requestFocus();
            mPhoneEdit.setError(getString(R.string.label_error_incorrect_phone));
            return;
        }
    }

    /* public void Regalert(String m_first_name){

         AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
         alertDialogBuilder.setIcon(R.drawable.ic_action_refresh);
         alertDialogBuilder.setTitle("Registration message");
         alertDialogBuilder.setMessage("Welcome to MANGUMANGU,\n" +
                 m_first_name +
                 "Your account registration\n" +
                 "was successful and an\n" +
                 "email has been sent to\n" +
                 "you! Please click on the\n" +
                 "link in the email to complete\n" +
                 "your registration."
         ).setCancelable(false);
         // set positive button: Yes message
         alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int id) {
                 // go to a new activity of the app
             }
         });
         AlertDialog alertDialog = alertDialogBuilder.create();
         // show alert
         alertDialog.show();
     }
 */
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    //final String m_first_name,final String m_second_name, final String m_email,final String m_city,final String m_address,final String m_phone , final String m_password
    protected void registerUser(final String m_first_name, final String m_second_name, final String m_email, final String CountryID, final String m_city, final String m_address, final String m_phone, final String m_password
    ) {

     /*m_first_name = "muwa";
     m_second_name="fraanco"; m_email="fra@gmail.com";
     m_city="kamapala"; m_address="kampala";
     m_phone="078888"; m_password="pass";
*/

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.REGISTRATION,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        hideDialog();
                        try {
                            JSONObject jObj = new JSONObject(response);
                            int error = jObj.getInt("ResultsID");
                            if (error == 1) {
                                // Launch login activity
                                Intent i = new Intent(getActivity(),
                                        LoginActivity.class);
                                startActivity(i);
                                //finish();
                            } else {
                                // Error occurred in registration. Get the error
                                // message
                                String errorMsg = jObj.getString("Message");
                                Toast.makeText(getActivity(),
                                        "Error in registration", Toast.LENGTH_LONG).show();
                                hideDialog();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException exception) {
                            exception.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getActivity(), R.string.error_network_timeout, Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof AuthFailureError) {
                            //TODO
                            Toast.makeText(getActivity(), R.string.authentification, Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof ServerError) {
                            //TODO
                            Toast.makeText(getActivity(), R.string.servererror, Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof NetworkError) {

                            //TODO
                            Toast.makeText(getActivity(), R.string.networkerror, Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof ParseError) {
                            //TODO
                            Toast.makeText(getActivity(), R.string.parseerror, Toast.LENGTH_LONG).show();
                            hideDialog();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                if (m_first_name != null) params.put("FirstName", m_first_name);
                if (m_second_name != null) params.put("LastName", m_second_name);
                if (m_email != null) params.put("Email", m_email);
                if (CountryID != null) params.put("CountryID", CountryID);
                if (m_city != null) params.put("City", m_city);
                if (m_address != null) params.put("Address", m_address);
                if (m_phone != null) params.put("PhoneNo", m_phone);
                if (m_password != null) params.put("Password", m_password);
                return params;
            }

        };


        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    protected void postEmail(final String m_email) {

        pDialog.setMessage("Registering ...");
        showDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.FORGOTPWD,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        hideDialog();
                        try {
                            JSONObject jObj = new JSONObject(response);
                            int error = jObj.getInt("ResultsID");
                            if (error == 1) {
                                // Launch login activity
                                Intent i = new Intent(getActivity(),
                                        LoginActivity.class);
                                startActivity(i);
                                //finish();
                            } else {
                                // Error occurred in registration. Get the error
                                // message
                                String errorMsg = jObj.getString("Message");
                                Toast.makeText(getActivity(),
                                        "Error in registration", Toast.LENGTH_LONG).show();
                                hideDialog();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (NullPointerException exception) {
                            exception.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            Toast.makeText(getActivity(), R.string.error_network_timeout, Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof AuthFailureError) {
                            //TODO
                            Toast.makeText(getActivity(), R.string.authentification, Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof ServerError) {
                            //TODO
                            Toast.makeText(getActivity(), R.string.servererror, Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof NetworkError) {

                            //TODO
                            Toast.makeText(getActivity(), R.string.networkerror, Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof ParseError) {
                            //TODO
                            Toast.makeText(getActivity(), R.string.parseerror, Toast.LENGTH_LONG).show();
                            hideDialog();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                if (m_email != null) params.put("Email", m_email);
                return params;
            }

        };


        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
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
}
