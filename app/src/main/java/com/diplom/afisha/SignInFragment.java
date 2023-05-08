package com.diplom.afisha;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.diplom.afisha.model.User;

import java.util.List;

public class SignInFragment extends Fragment {

    AppCompatButton loginButton;
    List<User> users;
    View fragment;
    TextView errorMessage;
    SharedPreferences sPref;

    public SignInFragment() {

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

        if (loginEmail.getText().toString().equals("admin") && loginPassword.getText().toString().equals("admin")) {
            sPref = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor ed = sPref.edit();
            ed.putBoolean("isAdmin", true);
            ed.apply();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            getActivity().startActivity(intent);
            getActivity().finish();
        } else {
            for (User user : users) {
                if (user.getEmail().equals(loginEmail.getText().toString()) && user.getPassword().equals(loginPassword.getText().toString())) {
                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    getActivity().startActivity(intent);
                    errorMessage.setVisibility(View.GONE);
                    errorMessage.setText("");
                    loginEmail.setText("");
                    loginPassword.setText("");
                    sPref = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putBoolean("isSignedIn", true);
                    ed.putString("username", user.getUsername());
                    ed.putString("email", user.getEmail());
                    ed.putString("phone", user.getPhone());
                    ed.putLong("uid", user.getId());
                    ed.apply();
                    Toast.makeText(getActivity(), "Успешный вход!", Toast.LENGTH_SHORT).show();
                    break;
                } else if (loginEmail.getText().length() <= 0 || loginPassword.getText().length() <= 0) {
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText(R.string.empty_field_error);
                } else {
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText(R.string.invalid_sign_in);
                }
            }
        }
    }

}