package com.mangu.transfer.db;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.io.InputStream;
import java.io.OutputStream;

public class Constant {

    // API URL configuration
    public static String GET_CHARITY_ACTIVITY = "https://mangumangu.com/json/CharityActivity.aspx";
    public static String MobileMoney = "https://mangumangu.com/json/SendYoMM.aspx";
    public static String REGISTRATION = "https://mangumangu.com/json/registration.aspx";
    public static String LOGIN = "https://mangumangu.com/json/Login.aspx";
    public static String SEND_TO_ONE = "https://mangumangu.com/json/Send2One.aspx";
    public static String SEND_TO_MANY = "https://mangumangu.com/json/SendManyToCart.aspx";
    public static String SEND_TO_MANY_CHECKOUT = "https://mangumangu.com/json/SendToMany.aspx";
    public static String SEND_TO_CHARITY = "https://mangumangu.com/json/CharityMoneyTransfer.aspx";
    public static String ADD_FIRM_FOR_HIRE = "https://mangumangu.com/json/AddFarmForHire.aspx";
    public static String RATEURL = "https://mangumangu.com/json/ForexRates.aspx";
    public static String ORGURL = "https://mangumangu.com/json/CharityOrganizations.aspx";
    public static String DELETESTPEOPLEITEM = "https://mangumangu.com/json/Delete4People.aspx";
    public static String DELETESTBANKITEM = "https://mangumangu.com/json/Delete4Bank.aspx";
    public static String DELETESTCHARITYITEM = "https://mangumangu.com/json/Delete4Charity.aspx";
    public static String PROJECTURL = "https://mangumangu.com/json/DataPosts.aspx";
    public static String MASTERCARD_API = "https://mangumangu.com/mangu_transfer/lightbox.html";
    public static String FORGOTTENPASS = "https://mangumangu.com/json/ForgotPassword.aspx";
    public static String USESSIONID = "https://mangumangu.com/json/session.aspx";
    public static String CARTCOUNT = "https://mangumangu.com/json/CartCount.aspx";
    public static String SESSIONCARTLIST = "https://mangumangu.com/json/GetCart.aspx";
    public static String FARM4HIRESTATEMENT = "https://mangumangu.com/json/Statements4FarmHire.aspx";
    public static String CHARITYSTATEMENT = "https://mangumangu.com/json/Statements4Charity.aspx";
    public static String BANKSTATEMENT = "https://mangumangu.com/json/Statements4Bank.aspx";
    public static String CARTAMOUNT = "https://mangumangu.com/json/CartAmount.aspx";
    public static String PEOPLESTATEMENT = "https://mangumangu.com/json/Statements4People.aspx";
    public static String SENDTOFARM = "https://mangumangu.com/json/SendToFirm.aspx";
    public static String DELETECARTIEM = "https://mangumangu.com/JSON/DeleteCartItem.aspx";
    public static String ADDFARMFORHIRE = " https://mangumangu.com/json/AddFarmForHire.aspx";
    public static String MYFARM = "https://mangumangu.com/json/MyFarmsForHire.aspx";
    public static String MASTERCARD_API_HIRE = "https://mangumangu.com/mangu_transfer/lightboxhire.html";
    public static String MASTERCARD_API_CHARITY = "https://mangumangu.com/mangu_transfer/lightboxcharitysessionid.html";
    public static String MASTERCARD_API_MANY = "https://mangumangu.com/mangu_transfer/lightboxmanysessionid.html";
    public static String MASTER_API_BANK = "https://mangumangu.com/mangu_transfer/lightboxbanksessionid.html";
    public static String FORGOTPWD = "https://mangumangu.com/json/GetUser.aspx";
    public static String GET_COUNTRY = "https://mangumangu.com/json/Countries.aspx";
    public static String GET_BANK = "https://mangumangu.com/json/Banks.aspx";
    public static String SEND_TO_BANK = "https://mangumangu.com/json/Send2Bank.aspx";
    public static String SAVE_IMAGE = "";
    public static String CHANGE_PASSWORD = "https://mangumangu.com/json/ChangePwd.aspx";
    public static String POSTEMAIL = "https://mangumangu.com/json/FarmEmails.aspx";
    public static String POST_PHONE_TO_SEND_SMS = "https://mangumangu.com/json/VerifyPhone.aspx";
    public static String POST_SMS_CODE = "https://mangumangu.com/json/CheckPhoneVerification.aspx";
    public static String GETSESSIONID = "https://mangumangu.com/json/GetSessionID.aspx";
    public static String GETADDCOUNTRY = "https://mangumangu.com/json/GetCountries.aspx";
    public static String GETADDCOUNTDISTRICT = "https://mangumangu.com/json/GetDistricts.aspx";
    public static String GETDEST = "https://mangumangu.com/json/GetDestinations.aspx";
    public static String DELIVERYADDRESS = "https://mangumangu.com/json/AddDeliveryAddress.aspx";
    public static String VIEWCARTLIST = "https://mangumangu.com/json/ViewManguProjectCart.aspx";
    public static String PRODUCTPAYOOMM = "https://mangumangu.com/json/SendYoMMManguProducts.aspx";
    public static String MASTERCARDPAY = "https://mangumangu.com/json/SendMasterCardManguProducts.aspx";
    public static String SMARTVIEWCARTLIST = "https://mangumangu.com/json/ViewSmartInvestCart.aspx";


    // change this access similar with accesskey in admin panel for security reason
    public static String AccessKey = "12345";

    // database path configuration
    public static String DBPath = "/data/data/com.mangu.transfer/databases/";

    // method to check internet connection
    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivity = (ConnectivityManager) activity
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // method to handle images from server
    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {

            ex.printStackTrace();
        }
    }

}
