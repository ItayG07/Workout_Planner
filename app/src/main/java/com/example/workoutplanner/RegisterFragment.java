package com.example.workoutplanner;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.workoutplanner.ui.theme.DatabaseHelper;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register2, container, false);
        Button btnRegister = view.findViewById(R.id.btnRegister);
        TextView tvLogin = view.findViewById(R.id.tvLogin);
        EditText etRegName = view.findViewById(R.id.etRegName);
        EditText etRegPassword = view.findViewById(R.id.etRegPassword);
        EditText etRegConfirmPassword = view.findViewById(R.id.etRegConfirmPassword);
        EditText etRegEmail = view.findViewById(R.id.etRegEmail);
        btnRegister.setOnClickListener(v -> {
            //Check if passwords are equal
            if (!etRegPassword.getText().toString().equals(etRegConfirmPassword.getText().toString())) {
                etRegConfirmPassword.setError("Passwords do not match");
                return;
            }
            // Check if the user already exists
            DatabaseHelper dbHelper = new DatabaseHelper(getContext());
            String userName = etRegName.getText().toString().trim();
            //check if user exists. if not - insert to db
            if (!dbHelper.checkUsernameExists(userName)) {
                // Save the user to the database
                String userPsd = etRegPassword.getText().toString().trim();
                String userEmail = etRegEmail.getText().toString().trim();
                UserDetails user = new UserDetails(userName, userPsd, userEmail);
                dbHelper.saveUserToDB(user);
                Intent intent = new Intent(getActivity(), Login.class);
                startActivity(intent);
            } else
                etRegName.setError("User already exists, try login or another user name");
        });
        // Set the click listener for the TextView to open the LoginFragment
        tvLogin.setOnClickListener(v -> {
            // Navigate to the LoginFragment
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new LoginFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }
}

