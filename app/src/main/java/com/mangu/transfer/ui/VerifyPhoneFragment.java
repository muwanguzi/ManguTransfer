////this the original


package com.mangu.transfer.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * Created by FRANCIS on 03/05/2017.
 */

public class VerifyPhoneFragment extends BaseFlagFragment implements AdapterView.OnItemSelectedListener {
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int REQUEST_CAMERA = 0;
    // LogCat tag
    private static final String TAG = VerifyPhoneFragment.class.getSimpleName();
    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public EditText inputFirstName, inputSecondName, inputPhone;
    public String m_first_name, m_second_name;
    public String m_email, m_address, m_city;
    public Context context;
    //private Button btnTakeImage;
    ImageView choose;
    ImageView imageView, imageView1;
    String m_password, m_confirm_password;
    ProgressDialog PCountDialog;
    ArrayAdapter<String> adapter;
    Spinner spinner;
    String mCountryId = "";
    String mCountryName = "";
    String mCountryCategory = "";
    String mCurrencyCode = "";
    String mUserCountryId = "";
    UserCountry cat;
    Bitmap bitmap;
    String imageString = "";
    CheckBox show_password;
    CheckBox agree;
    boolean isValid;
    String wt3_ContactNumber = "";
    String m_contactNumber = "";
    String mv_code = "";
    int PICK_IMAGE_REQUEST = 111;
    String filename = "";
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
    private ArrayList<UserCountry> UserCountryList;
    private File mediaFile = null;
    private Uri fileUri; // file url to store image/video
    private Button btnCapturePicture;


    public VerifyPhoneFragment() {
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
        spinner = rootView.findViewById(R.id.myspinner);
        imageView = rootView.findViewById(R.id.imageView);
        imageView1 = rootView.findViewById(R.id.imageView1);
        choose = rootView.findViewById(R.id.choose);
        agree = rootView.findViewById(R.id.agree);
        show_password = rootView.findViewById(R.id.show_hide_password);

        CheckBox checkbox = rootView.findViewById(R.id.agree);
        TextView textView = rootView.findViewById(R.id.textView2);

        checkbox.setText("");
        textView.setText(Html.fromHtml("I agree to Mangu Transfer's" +
                " <a href='https://www.google.com/policies/terms/archive/20070416/'>User agreement</a> and" + " <a href='https://www.google.com/policies/terms/archive/20070416/'>Privacy policy.</a>"));
        textView.setClickable(true);
        textView.setMovementMethod(LinkMovementMethod.getInstance());

        UserCountryList = new ArrayList<UserCountry>();

        // spinner item select listener
        spinner.setOnItemSelectedListener(this);
        // adapter.add("Select Country");

        new GetCountry().execute();
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(true);
        pDialog.setMessage("Registering...");
        initUI(rootView);
        show_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton button,
                                         boolean isChecked) {

                // If it is checkec then show password else hide
                // password
                if (isChecked) {

                    show_password.setText(R.string.hide_pwd);// change
                    // checkbox
                    // text

                    inputPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                    inputPassword.setTransformationMethod(HideReturnsTransformationMethod
                            .getInstance());// show password
                } else {
                    show_password.setText(R.string.show_pwd);// change
                    // checkbox
                    // text

                    inputPassword.setInputType(InputType.TYPE_CLASS_TEXT
                            | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    inputPassword.setTransformationMethod(PasswordTransformationMethod
                            .getInstance());// hide password

                }

            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                m_first_name = inputFirstName.getText().toString();
                m_second_name = inputSecondName.getText().toString();
                m_email = inputEmail.getText().toString();
                m_phone = inputPhone.getText().toString();
                m_password = inputPassword.getText().toString();
                m_confirm_password = confirmPassword.getText().toString();


                m_city = "";

                m_address = "";


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
                } else if (m_second_name.isEmpty()) {
                    inputSecondName.requestFocus();
                    inputSecondName.setError("Please enter Second Name");
                } else if (m_second_name == null) {
                    inputSecondName.requestFocus();
                    inputSecondName.setError("Please enter Second Name");
                } else if (m_email.isEmpty()) {
                    inputEmail.requestFocus();
                    inputEmail.setError("Please enter Email");
                } else if (!isValidEmail(m_email)) {
                    inputEmail.requestFocus();
                    inputEmail.setError("Invalid Email");
                } else if (m_email == null) {
                    inputEmail.requestFocus();

                    inputEmail.setError("Invalid Email");
                } else if (m_phone.isEmpty()) {
                    inputPhone.requestFocus();
                    inputPhone.setError("Please enter phone");
                } else if (!isValid) {

                    inputPhone.requestFocus();
                    inputPhone.setError("Phone number is invalid");
                } else if (m_password.isEmpty()) {
                    inputPassword.requestFocus();
                    inputPassword.setError("Please enter password");
                } else if (m_password == null) {
                    inputPassword.requestFocus();
                    inputPassword.setError("Please enter password");
                } else if (!(agree.isChecked())) {
                    agree.requestFocus();

                    agree.setError("Please check the terms and condition");
                    Toast.makeText(getActivity(), "Please check the terms and condition", Toast.LENGTH_LONG).show();
                } else {
                    //send();CountryCategory
                    if (isValid) {


                        m_contactNumber = wt3_ContactNumber;
                        registerUser(m_first_name, m_second_name, imageString, filename, m_email, mUserCountryId, m_city, m_address, m_contactNumber, m_password);

                    }
                }
            }
        });


        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getActivity(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            getActivity().finish();
        }
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // capture picture
                captureImage();
            }
        });
        //opening image chooser option
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
            }
        });


        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getActivity(), LoginActivity.class);

                startActivity(intent);
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

        mCountryCategory = UserCountryList.get(position).getCategory();

        mUserCountryId = UserCountryList.get(position).getId();


    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    /**
     * Checking device has camera hardware or not
     */
    private boolean isDeviceSupportCamera() {
        // this device has a camera
// no camera on this device
        return getActivity().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA);
    }

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);

    }

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
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
        outState.putParcelable("file_uri", fileUri);
    }

    /**
     * returning image / video
     */
    private File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Constant.SAVE_IMAGE);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + Constant.SAVE_IMAGE + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        mediaFile = null;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
            try {
                readFileToString(mediaFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return mediaFile;
    }

    public String readFileToString(File mediaFile) throws IOException {
        File dirs = new File(".");

        StringBuilder fileData = new StringBuilder(1000);//Constructs a string buffer with no characters in it and the specified initial capacity
        BufferedReader reader = new BufferedReader(new FileReader(mediaFile));

        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }

        filename = fileData.toString();
        //Toast.makeText(getActivity(), filename, Toast.LENGTH_SHORT).show();
        reader.close();

        return filename;
    }

    /**
     * Receiving activity result method will be called after closing the camera
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // successfully captured the image
                // launching upload activity
                //launchUploadActivity(true);
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    // down sizing image as it throws OutOfMemory Exception for larger
                    // images
                    options.inSampleSize = 8;
                    final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);

                    //converting image to base64 string
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageBytes = baos.toByteArray();
                    imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);


                    imageView1.setImageBitmap(bitmap);


                } catch (Exception e) {
                    e.printStackTrace();
                }


            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled Image capture
                Toast.makeText(getActivity(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to capture image
                Toast.makeText(getActivity(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }

        } else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            Uri filePath = data.getData();

            try {
                //getting image from gallery
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                //converting image to base64 string
                Bitmap converetdImage = getResizedBitmap(bitmap, 75);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                converetdImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] imageBytes = baos.toByteArray();
                imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

                //Setting image to ImageView
                imageView1.setImageBitmap(converetdImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Adding spinner data
     */
    private void populateSpinner() {
        List<String> lables = new ArrayList<String>();


        for (int i = 0; i < UserCountryList.size(); i++) {
            lables.add(UserCountryList.get(i).getName());
        }

        // Creating adapter for spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.spinner_item, lables);

        // Drop down layout style - list view with radio button
        spinnerAdapter
                .setDropDownViewResource(R.layout.spinner_item);

        // attaching data adapter to spinner
        spinner.setAdapter(spinnerAdapter);
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

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public void Regalert(String m_first_name) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setIcon(R.drawable.tick);
        alertDialogBuilder.setTitle("Registration message");
        alertDialogBuilder.setMessage("Welcome to MANGU TRANSFER,\n" +
                m_first_name +
                " your account registration\n" +
                "was successful and an email has \n" +
                "been sent to you! Please click on the\n" +
                "link to activate your account\n"
        ).setCancelable(false);
        // set positive button: Yes message
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // go to a new activity of the app

                Intent i = new Intent(getActivity(), LoginActivity.class);
                i.putExtra("CountryCategory", mCountryCategory);
                getActivity().finish();
                startActivity(i);

            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();
    }

    public void RegSMSalert(String m_first_name) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setIcon(R.drawable.tick);
        alertDialogBuilder.setTitle("Registration message");
        alertDialogBuilder.setMessage("Welcome to MANGU TRANSFER,\n" +
                m_first_name +
                " You are one step away from completing your account registration\n" +
                "a verification SMS code has\n" +
                "been sent to your Phone! \n"
        ).setCancelable(false);
        // set positive button: Yes message
        alertDialogBuilder.setPositiveButton("Enter Code", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // go to a new activity of the app

                // inflate alert dialog xml
                LayoutInflater li = LayoutInflater.from(getActivity());
                View dialogView = li.inflate(R.layout.post_sms_verification_code, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                // set title
                alertDialogBuilder.setTitle("Enter Code");
                // set custom dialog icon
                alertDialogBuilder.setIcon(R.drawable.change_password);
                // set custom_dialog.xml to alertdialog builder
                alertDialogBuilder.setView(dialogView);
                final TextView user_name_Input = dialogView
                        .findViewById(R.id.enter_code);
                final EditText v_code = dialogView
                        .findViewById(R.id.vcode);

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        // get user input and set it to etOutput
                                        // edit text old_password_Input

                                        mv_code = v_code.getText().toString();

                                        if (mv_code.isEmpty()) {
                                            v_code.requestFocus();
                                            v_code.setError("Please enter verification code");
                                        } else {

                                            VerifyPhoneFragment.validateSMSTask task = new VerifyPhoneFragment.validateSMSTask();
                                            task.execute(mv_code, m_phone);

                                        }

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


            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();
    }

    public void RegSMSalertRetry(String m_first_name) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setIcon(R.drawable.tick);
        alertDialogBuilder.setTitle("Registration message");
        alertDialogBuilder.setMessage("Dear,\n" +
                m_first_name +
                " there was an error please try again\n"
        ).setCancelable(false);
        // set positive button: Yes message
        alertDialogBuilder.setPositiveButton("Enter Code", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // go to a new activity of the app

                // inflate alert dialog xml
                LayoutInflater li = LayoutInflater.from(getActivity());
                View dialogView = li.inflate(R.layout.post_sms_verification_code, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                // set title
                alertDialogBuilder.setTitle("Enter Code");
                // set custom dialog icon
                alertDialogBuilder.setIcon(R.drawable.change_password);
                // set custom_dialog.xml to alertdialog builder
                alertDialogBuilder.setView(dialogView);
                final TextView user_name_Input = dialogView
                        .findViewById(R.id.enter_code);
                final EditText v_code = dialogView
                        .findViewById(R.id.vcode);

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        // get user input and set it to etOutput
                                        // edit text old_password_Input

                                        mv_code = v_code.getText().toString();

                                        if (mv_code.isEmpty()) {
                                            v_code.requestFocus();
                                            v_code.setError("Please enter verification code");
                                        } else {

                                            VerifyPhoneFragment.validateSMSTask task = new VerifyPhoneFragment.validateSMSTask();
                                            task.execute(mv_code, m_phone);

                                        }

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


            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show alert
        alertDialog.show();
    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public void onBackPressed() {
        // TODO Auto-generated method stub
        getActivity().onBackPressed();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    private void registerUser(final String m_first_name, final String m_second_name, final String imageString, final String filename, final String m_email, final String CountryID, final String m_city, final String m_address, final String m_phone, final String m_password) {
        pDialog.show();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        final String filname = "IMG" + timeStamp + ".JPG";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.REGISTRATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                pDialog.hide();
                try {

                    JSONObject jObj = new JSONObject(response);
                    int error = jObj.getInt("ResultsID");
                    if (error == 1) {

                        getGetSMS(m_contactNumber);


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
                            Toast.makeText(getActivity(), error.getMessage(),
                                    Toast.LENGTH_LONG).show();
                            Log.e("VOLLEY", error.getMessage());
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
                if (m_first_name != null) params.put("FirstName", m_first_name);
                if (m_second_name != null) params.put("LastName", m_second_name);
                if (m_email != null) params.put("Email", m_email);
                if (imageString != null) params.put("IDByteArray", imageString);
                if (filname != null) params.put("FileName", filname);
                if (CountryID != null) params.put("CountryID", CountryID);
                if (m_city != null) params.put("City", m_city);
                if (m_address != null) params.put("Address", m_address);
                if (m_contactNumber != null) params.put("PhoneNo", m_contactNumber);
                if (m_password != null) params.put("Password", m_password);
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

                                RegSMSalert(m_first_name);


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


            populateSpinner();

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
                        Regalert(m_first_name);
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
