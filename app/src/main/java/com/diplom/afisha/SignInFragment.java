package com.diplom.afisha;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.diplom.afisha.model.User;

import java.util.List;

public class SignInFragment extends Fragment {

    AppCompatButton loginButton;
    List<User> users;
    View fragment;
    TextView errorMessage;
    public SignInFragment(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.fragment_sign_in, container, false);
        errorMessage = fragment.findViewById(R.id.error_message);
        errorMessage.setVisibility(View.GONE);
        loginButton = fragment.findViewById(R.id.login_button);
        loginButton.setOnClickListener(view -> loginUser(view));
        return fragment;
    }

    private void loginUser(View view) {
        users = LoginActivity.usersList;
        EditText loginEmail, loginPassword;

        loginEmail = fragment.findViewById(R.id.login_email);
        loginPassword = fragment.findViewById(R.id.login_password);
        
        users.forEach(user -> {
            if (user.getEmail().equals(loginEmail.getText().toString()) && user.getPassword().equals(loginPassword.getText().toString())){
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                getActivity().startActivity(intent);
                loginEmail.setText("");
                loginPassword.setText("");
                Toast.makeText(getActivity(), "Signed In!", Toast.LENGTH_SHORT).show();
            } else if (loginEmail.getText().length() <= 0 || loginPassword.getText().length() <= 0){
                errorMessage.setVisibility(View.VISIBLE);
                errorMessage.setText(R.string.empty_field_error);
            } else {
                errorMessage.setVisibility(View.VISIBLE);
                errorMessage.setText(R.string.invalid_sign_in);
            }
        });
    }
}