package com.example.emergency;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.emergency.common.Common;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class PhoneNumberFragment extends Fragment {


    public PhoneNumberFragment() {
        // Required empty public constructor
    }


    public static PhoneNumberFragment newInstance(Activity activity) {

        PhoneNumberFragment fragment = new PhoneNumberFragment();
        return fragment;

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userIsLoggedIn();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_phone_number, container, false);

        final EditText phoneEditText = view.findViewById(R.id.phone_number_editText);

        Button sendButton = view.findViewById(R.id.send_button);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String phoneNumber = phoneEditText.getText().toString().trim();
                if (phoneNumber.isEmpty() || phoneNumber.length() < 11) {
                    phoneEditText.setError("Enter Valid Code");
                    phoneEditText.requestFocus();
                    Toast.makeText(getContext(), "incorrect phone number format", Toast.LENGTH_SHORT).show();
                    return;
                }
                phoneNumber = Common.handlePhoneNumber(phoneNumber);
                Fragment verifyCodeFragment = VerifyCodeFragment.newInstance(phoneNumber);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_frame, verifyCodeFragment);
                transaction.commit();
            }
        });

        return view;
    }

    private void userIsLoggedIn(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null){
            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
