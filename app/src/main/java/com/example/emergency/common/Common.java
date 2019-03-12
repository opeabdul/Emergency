package com.example.emergency.common;

import com.google.firebase.auth.FirebaseUser;

public class Common {

    public static User currentUser;


    public static String handlePhoneNumber(String phoneNumber) {

        phoneNumber = phoneNumber.replaceAll(" ", "")
                .replaceAll("-", "")
                .replaceAll("\\(", "")
                .replaceAll("\\)", "");


        if (!String.valueOf(phoneNumber.charAt(0)).equals("+")) {
            if (phoneNumber.length() == 10 ){
                phoneNumber = "+234" + phoneNumber;
                return phoneNumber;
            }else if(String.valueOf(phoneNumber.charAt(0)).equals("0")){
                phoneNumber = "+234" + phoneNumber.substring(1, phoneNumber.length());
                return phoneNumber;
            }

        }
        return phoneNumber;
    }
}
