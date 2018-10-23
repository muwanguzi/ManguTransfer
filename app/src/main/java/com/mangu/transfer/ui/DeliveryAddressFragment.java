////this the original


package com.mangu.transfer.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
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
import com.mangu.transfer.adapter.CustomHttpConnection;
import com.mangu.transfer.db.Constant;
import com.mangu.transfer.db.SessionManager;
import com.mangu.transfer.fragment.BaseFlagFragment;
import com.mangu.transfer.util.ServiceHandler;
import com.mangu.transfer.util.UserCountry;
import com.mangu.transfer.util.UserDistrict;
import com.mangu.transfer.util.UserTown;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by FRANCIS on 03/05/2017.
 */

public class DeliveryAddressFragment extends BaseFlagFragment implements AdapterView.OnItemSelectedListener {
    private static final String TAG = DeliveryAddressFragment.class.getSimpleName();
    public EditText inputFirstName, inputSecondName, inputPhone;
    public String m_first_name, m_second_name, m_other_name = "";
    public String m_addnote, m_address, m_city;
    public Context context;
    //private Button btnTakeImage;
    ImageView choose;
    ProgressDialog PCountDialog;
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> spinnerAdapter1;
    ArrayAdapter<String> spinnerAdapter2;
    ArrayAdapter<String> spinnerAdapter;
    Spinner spinner1, spinner2, spinner3;
    String mUserCountryId = "";
    String mCountryCategory = "";
    String m_sender_id = "";
    String mTownId = "";
    String mDistrictId = "";
    String session_Id = "";
    UserCountry cat;
    UserDistrict cat1;
    UserTown cat2;
    boolean isValid;
    String wt3_ContactNumber = "";
    String m_contactNumber = "";
    private Button btnAddAddress;
    private Button btnLinkToLogin;
    private EditText inputPhysicalAddress;
    private EditText inputAdditionalNotes;
    private String m_phone;
    private ProgressDialog pDialog;
    private SessionManager session;
    private RequestQueue queue;
    private String response;
    private ArrayList<UserCountry> UserCountryList;
    private ArrayList<UserDistrict> UserDistrictList;
    private ArrayList<UserTown> UserTownList;
    private File mediaFile = null;

    public DeliveryAddressFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity__delivery__address, container, false);


        queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        inputFirstName = rootView.findViewById(R.id.first_name);
        inputSecondName = rootView.findViewById(R.id.second_name);
        inputPhysicalAddress = rootView.findViewById(R.id.address);
        inputPhone = rootView.findViewById(R.id.phone);
        inputAdditionalNotes = rootView.findViewById(R.id.addnotes);
        btnAddAddress = rootView.findViewById(R.id.btnAddress);
        // btnLinkToLogin = (Button) rootView.findViewById(R.id.btnLinkToLoginScreen);
        spinner1 = rootView.findViewById(R.id.country);
        spinner2 = rootView.findViewById(R.id.district);
        spinner3 = rootView.findViewById(R.id.town);

        session = new SessionManager(getActivity());
        /** get user data from session*/
        HashMap<String, String> user = session.getUserDetails();
        m_sender_id = user.get(SessionManager.KEY_SENDER_ID);
        // name
        HashMap<String, String> us = session.getTransId();
        session_Id = us.get(SessionManager.KEY_SESSIONID);
        //mEmail = user.get(SessionManager.KEY_EMAIL);
        //m_senderId = Integer.valueOf(m_senderParam);

        UserCountryList = new ArrayList<>();
        UserDistrictList = new ArrayList<>();
        UserTownList = new ArrayList<>();
        // spinner item select listener
        spinner1.setOnItemSelectedListener(this);
        spinner2.setOnItemSelectedListener(this);
        spinner3.setOnItemSelectedListener(this);
        // adapter.add("Select Country");

        new GetCountry().execute();
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(true);
        pDialog.setMessage("Processing...");
        if (pDialog.isShowing()) {
            pDialog.dismiss();
        }
        initUI(rootView);
        initUI(rootView);

        btnAddAddress.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                m_first_name = inputFirstName.getText().toString();
                m_other_name = inputSecondName.getText().toString();
                m_address = inputPhysicalAddress.getText().toString();
                m_phone = inputPhone.getText().toString();
                m_addnote = inputAdditionalNotes.getText().toString();
                m_city = "";

                String wt_contactNumber = m_phone;
                PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();

                /**Formatting the phone number **/
                try {
                    Phonenumber.PhoneNumber wt2_ContactNumber = phoneUtil.parse(wt_contactNumber, "UG");
                    /** phone  begin with '+'**/
                    wt3_ContactNumber = phoneUtil.format(wt2_ContactNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);

                    Phonenumber.PhoneNumber wt4_ContactNumber = phoneUtil.parse(wt3_ContactNumber, "UG");
                    isValid = phoneUtil.isValidNumber(wt4_ContactNumber);

                } catch (NumberParseException e) {
                    System.err.println("NumberParseException was thrown: " + e.toString());
                }
                if (m_first_name.isEmpty()) {
                    inputFirstName.requestFocus();
                    inputFirstName.setError("Please enter First Name");
                } else if (m_first_name == null) {
                    inputFirstName.requestFocus();
                    inputFirstName.setError("First Name is null");
                } else if (m_other_name.isEmpty()) {
                    inputSecondName.requestFocus();
                    inputSecondName.setError("Please enter Second Name");
                } else if (m_other_name == null) {
                    inputSecondName.requestFocus();
                    inputSecondName.setError("Please enter Second Name");
                } else if (m_address.isEmpty()) {
                    inputPhysicalAddress.requestFocus();
                    inputPhysicalAddress.setError("Please enter Email");
                } else if (m_phone.isEmpty()) {
                    inputPhone.requestFocus();
                    inputPhone.setError("Please enter phone");
                } else if (m_addnote.isEmpty()) {
                    inputAdditionalNotes.requestFocus();
                    inputAdditionalNotes.setError("Please enter password");
                } else {
                    //send();CountryCategory
                    if (isValid) {
                        m_contactNumber = wt3_ContactNumber;
                        addDeliveryAdress(m_sender_id, mTownId, m_first_name, m_other_name, m_contactNumber, m_address, m_addnote);

                    }
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initCodes(getActivity());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int position, long id) {
        Spinner spinner = (Spinner) parent;
        if (spinner.getId() == R.id.country) {
            //do this
            mCountryCategory = UserCountryList.get(position).getCategory();

            mUserCountryId = UserCountryList.get(position).getId();
            if (mUserCountryId != null && !mUserCountryId.isEmpty()) {
                new GetDistrict().execute();
            }
        } else if (spinner.getId() == R.id.district) {
            //do this

            mDistrictId = UserDistrictList.get(position).getId();
            if (mDistrictId != null && !mDistrictId.isEmpty()) {
                new GetDest().execute();
            }
        } else if (spinner.getId() == R.id.town) {
            mTownId = UserTownList.get(position).getId();

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // save file url in bundle as it will be null on screen orientation
        // changes
    }

    /**
     * Adding spinner data
     */
    private void populateSpinner1() {
        List<String> lables = new ArrayList<String>();
        for (int i = 0; i < UserCountryList.size(); i++) {
            lables.add(UserCountryList.get(i).getName());
        }
        if (spinnerAdapter != null) {
            spinnerAdapter.notifyDataSetChanged();
        }
        // Creating adapter for spinner
        spinnerAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.spinner_item, lables);
        // Drop down layout style - list view with radio button
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item);
        // attaching data adapter to spinner
        spinner1.setAdapter(spinnerAdapter);
    }

    private void populateSpinner2() {
        List<String> lables = new ArrayList<String>();
        for (int i = 0; i < UserDistrictList.size(); i++) {
            lables.add(UserDistrictList.get(i).getName());
        }
        if (spinnerAdapter1 != null) {
            spinnerAdapter1.notifyDataSetChanged();
        }
        // Creating adapter for spinner
        spinnerAdapter1 = new ArrayAdapter<String>(getActivity(),
                R.layout.spinner_item, lables);
        // Drop down layout style - list view with radio button
        spinnerAdapter1.setDropDownViewResource(R.layout.spinner_item);
        // attaching data adapter to spinner
        spinner2.setAdapter(spinnerAdapter1);
    }

    private void populateSpinner3() {
        List<String> lables = new ArrayList<String>();
        for (int i = 0; i < UserTownList.size(); i++) {
            lables.add(UserTownList.get(i).getName());
        }
        if (spinnerAdapter2 != null) {
            spinnerAdapter2.notifyDataSetChanged();
        }
        // Creating adapter for spinner
        spinnerAdapter2 = new ArrayAdapter<String>(getActivity(),
                R.layout.spinner_item, lables);
        // Drop down layout style - list view with radio button
        spinnerAdapter2.setDropDownViewResource(R.layout.spinner_item);
        // attaching data adapter to spinner
        spinner3.setAdapter(spinnerAdapter2);
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

    public void onBackPressed() {
        // TODO Auto-generated method stub
        getActivity().onBackPressed();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private void addDeliveryAdress(final String m_sender_id, final String mTownId, final String m_first_name, final String m_other_name, final String m_contactNumber, final String m_address, final String m_addnote) {
        pDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.DELIVERYADDRESS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                pDialog.hide();
                try {

                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("ResultsID");
                    if (error > 0) {

                        String msg = jObj.getString("Message");
                        Toast.makeText(getActivity(),
                                msg, Toast.LENGTH_LONG).show();

                    } else {

                        String msg = jObj.getString("Message");
                        Toast.makeText(getActivity(),
                                msg, Toast.LENGTH_LONG).show();

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
                            Toast.makeText(getActivity(), getActivity().getString(R.string.error_network_timeout),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof NoConnectionError) {
                            //TODO
                            Toast.makeText(getActivity(), getActivity().getString(R.string.nonetworkconection),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof AuthFailureError) {
                            //TODO
                            Toast.makeText(getActivity(), getActivity().getString(R.string.authentification),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof ServerError) {
                            //TODO
                            Toast.makeText(getActivity(), error.getMessage(),
                                    Toast.LENGTH_LONG).show();
                            // Log.e("VOLLEY", error.getMessage());
                            hideDialog();
                        } else if (error instanceof NetworkError) {

                            //TODO
                            Toast.makeText(getActivity(), getActivity().getString(R.string.networkerror),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof ParseError) {
                            //TODO
                            Toast.makeText(getActivity(), getActivity().getString(R.string.parseerror),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        }
                    }
                }) {


            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                if (m_sender_id != null) params.put("MoneySenderID", m_sender_id);
                if (mTownId != null) params.put("DestinationID", mTownId);
                if (m_first_name != null) params.put("CFirstName", m_first_name);
                //if(m_second_name != null) params.put("LastName", m_second_name);
                if (m_other_name != null) params.put("COtherNames", m_other_name);
                if (m_address != null) params.put("PhysicalAddress", m_address);
                if (m_addnote != null) params.put("AdditionalNotes", m_addnote);
                if (m_contactNumber != null) params.put("ContactPhones", m_contactNumber);
                return params;
            }

        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        // stringRequest.setRetryPolicy(policy);
        stringRequest.setShouldCache(false);
        requestQueue.add(stringRequest);
    }

    public void getGetSMS(final String m_contactNumber) {

        pDialog.setMessage("Processing...");

        showDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.POST_PHONE_TO_SEND_SMS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        hideDialog();
                        try {

                            JSONObject jObj = new JSONObject(response);
                            int error = jObj.getInt("ResultsID");
                            if (error == 1) {

                                //RegSMSalert(m_first_name);


                            } else if (error == 3) {
                                // Error in processing. Get the error message
                                String msg = jObj.getString("Message");
                                Toast.makeText(getActivity(),
                                        msg + error, Toast.LENGTH_LONG).show();

                            } else if (error == 0) {

                                String msg = jObj.getString("Message");
                                Toast.makeText(getActivity(),
                                        msg, Toast.LENGTH_LONG).show();

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
                            Toast.makeText(getActivity(), getActivity().getString(R.string.error_network_timeout),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof NoConnectionError) {
                            //TODO
                            Toast.makeText(getActivity(), getActivity().getString(R.string.nonetworkconection),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof AuthFailureError) {
                            //TODO
                            Toast.makeText(getActivity(), getActivity().getString(R.string.authentification),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof ServerError) {
                            //TODO
                            Toast.makeText(getActivity(), getActivity().getString(R.string.servererror),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof NetworkError) {

                            //TODO
                            Toast.makeText(getActivity(), getActivity().getString(R.string.networkerror),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        } else if (error instanceof ParseError) {
                            //TODO
                            Toast.makeText(getActivity(), getActivity().getString(R.string.parseerror),
                                    Toast.LENGTH_LONG).show();
                            hideDialog();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("PhoneNo", m_contactNumber);
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

    /**
     * Async task to get all countries
     * *
     */
    private class GetCountry extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PCountDialog = new ProgressDialog(getActivity());
            PCountDialog.setMessage("loading data...");
            PCountDialog.setCancelable(true);
            PCountDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            ServiceHandler jsonParser = new ServiceHandler();
            String json = jsonParser.makeServiceCall(Constant.GET_COUNTRY, ServiceHandler.GET);
            Log.e("Response: ", "> " + json);
            if (json != null) {
                try {
                    JSONArray country_obj = new JSONArray(json);
                    for (int i = 0; i < country_obj.length(); i++) {
                        JSONObject catObj = (JSONObject) country_obj.get(i);
                        cat = new UserCountry(catObj.getString("CountryID"),
                                catObj.getString("CountryName"), catObj.getString("CountryCategory"));
                        UserCountryList.add(cat);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } else {
                Log.e("JSON Data", "Didn't receive any data from server!");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            populateSpinner1();
            PCountDialog.hide();
        }
    }

    /**
     * Async task to get all countries
     * *
     */
    private class GetDistrict extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PCountDialog = new ProgressDialog(getActivity());
            PCountDialog.setMessage("loading data...");
            PCountDialog.setCancelable(true);
            PCountDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.GETADDCOUNTDISTRICT,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            hideDialog();
                            try {
                                JSONArray country_obj = new JSONArray(response);

                                for (int i = 0; i < country_obj.length(); i++) {
                                    JSONObject catObj = (JSONObject) country_obj.get(i);
                                    cat1 = new UserDistrict(catObj.getString("DistrictID"),
                                            catObj.getString("District"), catObj.getString("CountryID"));
                                    UserDistrictList.add(cat1);
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
                                Toast.makeText(getActivity(), getActivity().getString(R.string.error_network_timeout),
                                        Toast.LENGTH_LONG).show();
                                hideDialog();
                            } else if (error instanceof NoConnectionError) {
                                //TODO
                                Toast.makeText(getActivity(), getActivity().getString(R.string.nonetworkconection),
                                        Toast.LENGTH_LONG).show();
                                hideDialog();
                            } else if (error instanceof ServerError) {
                                //TODO
                                Toast.makeText(getActivity(), context.getString(R.string.servererror),
                                        Toast.LENGTH_LONG).show();
                                hideDialog();
                            } else if (error instanceof NetworkError) {

                                //TODO
                                Toast.makeText(getActivity(), context.getString(R.string.networkerror),
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
                    params.put("CountryID", mUserCountryId);
                    return params;
                }

            };
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(stringRequest);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            populateSpinner2();
            // new GetDest().execute();
            PCountDialog.hide();
        }

    }

    /**
     * Async task to get all countries
     * *
     */
    private class GetDest extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PCountDialog = new ProgressDialog(getActivity());
            PCountDialog.setMessage("loading data...");
            PCountDialog.setCancelable(true);
            PCountDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.GETDEST,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            hideDialog();
                            try {
                                JSONArray country_obj = new JSONArray(response);
                                for (int i = 0; i < country_obj.length(); i++) {
                                    JSONObject catObj = (JSONObject) country_obj.get(i);
                                    cat2 = new UserTown(catObj.getString("DestinationID"),
                                            catObj.getString("Destination"), catObj.getString("District"));
                                    UserTownList.add(cat2);
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
                                Toast.makeText(getActivity(), getActivity().getString(R.string.error_network_timeout),
                                        Toast.LENGTH_LONG).show();
                                hideDialog();
                            } else if (error instanceof NoConnectionError) {
                                //TODO
                                Toast.makeText(getActivity(), getActivity().getString(R.string.nonetworkconection),
                                        Toast.LENGTH_LONG).show();
                                hideDialog();
                            } else if (error instanceof ServerError) {
                                //TODO
                                Toast.makeText(getActivity(), context.getString(R.string.servererror),
                                        Toast.LENGTH_LONG).show();
                                hideDialog();
                            } else if (error instanceof NetworkError) {

                                //TODO
                                Toast.makeText(getActivity(), context.getString(R.string.networkerror),
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
                    params.put("DistrictID", mDistrictId);
                    return params;
                }

            };
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(stringRequest);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            populateSpinner3();
            PCountDialog.hide();
        }
    }

    /*** task SMS ***/
    private class validateSMSTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Processing...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            ArrayList<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("VCode", params[0]));
            postParameters.add(new BasicNameValuePair("PhoneNo", params[1]));

            String res = null;
            try {

                String response = null;
                response = CustomHttpConnection.executeHttpPost(Constant.POST_SMS_CODE, postParameters);
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

                    int mres = obj.getInt("ResultsID");
                    String msg = obj.getString("Message");

                    if (mres == 1) {

                        //Regalert(m_first_name);
                    } else {
                        Toast.makeText(getActivity(), msg,
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
