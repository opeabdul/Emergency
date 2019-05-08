package com.example.emergency;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.emergency.common.Common;
import com.example.emergency.common.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class CompleteProfileFragment extends Fragment {

    private  FirebaseUser mUser;
    public CompleteProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_complete_profile, container, false);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        final EditText nameEditText = view.findViewById(R.id.name_editText);
        final EditText occupationEditText = view.findViewById(R.id.occupation_editText);
        final EditText ageEditText = view.findViewById(R.id.age_editText);

        TextView skipTextView = view.findViewById(R.id.skip_TextView);
        skipTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(
                        new Intent(getContext(), MainActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                );
            }
        });

        Button sendInfoButton = view.findViewById(R.id.send_info_button);

        sendInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = nameEditText.getText().toString().trim();
                final String occupation = occupationEditText.getText().toString().trim();
                final String age = ageEditText.getText().toString().trim();

                if (name.isEmpty()) {
                    nameEditText.setError("enter a name");
                    return;
                }

                if (occupation.isEmpty()) {
                    occupationEditText.setError("enter your occupation");
                    return;
                }

                if (age.isEmpty() | age.length() > 3) {
                    ageEditText.setError("enter a valid age");
                    return;
                }

                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build();



                user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {


                        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());
                        User newUser = new User(name, user.getPhoneNumber(),null, null, occupation, age);

                        dbRef.setValue(newUser);
                        Common.currentUser = newUser;

                        startActivity(
                                new Intent(getContext(), MainActivity.class)
                                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        );

                    }
                });


            }
        });

        return view;
    }



}
