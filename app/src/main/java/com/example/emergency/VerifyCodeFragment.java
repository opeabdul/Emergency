package com.example.emergency;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;


public class VerifyCodeFragment extends Fragment {

    private static final String ARG_PHONENUMBER = "param1";
    private String mPhoneNumber;
    private String mVerificationId;


    private EditText codeEditText;
    private Button checkCodeButton;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null){
                codeEditText.setText(code);
                verifyNumberWithCode(code);
            }

            FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {

                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                                transaction.replace(R.id.main_frame, new CompleteProfileFragment());
                                transaction.commit();
                            }
                        }
                    });
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            mVerificationId = s;
        }
    };

    public VerifyCodeFragment() {
        // Required empty public constructor
    }


    public static VerifyCodeFragment newInstance(String phoneNumber) {
        VerifyCodeFragment fragment = new VerifyCodeFragment();

        Bundle args = new Bundle();
        args.putString(ARG_PHONENUMBER, phoneNumber);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userIsLoggedIn();
        if (getArguments() != null)
            mPhoneNumber = getArguments().getString(ARG_PHONENUMBER);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        sendVerificationCode();
        View view =  inflater.inflate(R.layout.fragment_verification_code, container, false);
        codeEditText = view.findViewById(R.id.verification_code_editText);
        checkCodeButton = view.findViewById(R.id.check_code_button);

        ImageView closeImageView = view.findViewById(R.id.close_imageView);
        closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.main_frame, new PhoneNumberFragment());
                transaction.commit();
            }
        });

        checkCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = codeEditText.getText().toString().trim();
                if(code.isEmpty() || code.length() < 6){
                    codeEditText.setError("Enter valid Code");
                    codeEditText.requestFocus();
                    return;
                }
                verifyNumberWithCode(code);
            }
        });
        return view;
    }

    private void sendVerificationCode(){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(mPhoneNumber,
                60, TimeUnit.SECONDS, getActivity(), mCallbacks);
    }

    private void verifyNumberWithCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

        signInWithPhoneAuthCredential(credential);

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential){
        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.main_frame, new CompleteProfileFragment());
                    transaction.commit();
                }else{
                    String message = "Something is wrong, we will fix it soon";

                    if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                        message = "Invalid Code Entered";
                    }

                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                }
            }
        });
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
