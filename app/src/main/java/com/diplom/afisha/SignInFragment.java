package com.diplom.afisha;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
    EditText loginEmail, loginPassword;
    ImageView showPassword;

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
        showPassword = fragment.findViewById(R.id.show_password_button);
        loginEmail = fragment.findViewById(R.id.login_email);
        loginPassword = fragment.findViewById(R.id.login_password);
        showPassword.setOnClickListener(v -> togglePassword(loginPassword, showPassword));
        return fragment;
    }

    private void loginUser(View view) {
        users = LoginActivity.usersList;

        if (loginEmail.getText().toString().equals("admin") && loginPassword.getText().toString().equals("admin")) {
            sPref = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor ed = sPref.edit();
            ed.putBoolean("isAdmin", true);
            ed.apply();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            getActivity().startActivity(intent);
            getActivity().finish();
        } else if ((!isValidEmail(loginEmail.getText().toString()))) {
            errorMessage.setVisibility(View.VISIBLE);
            errorMessage.setText(R.string.invalid_email_error);
        } else {
            for (User user : users) {
                if (user.getEmail().equals(loginEmail.getText().toString()) && user.getPassword().equals(loginPassword.getText().toString())) {
                    errorMessage.setVisibility(View.GONE);
                    sPref = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                    SharedPreferences.Editor ed = sPref.edit();
                    ed.putBoolean("isSignedIn", true);
                    ed.putString("username", user.getUsername());
                    ed.putString("email", user.getEmail());
                    ed.putString("phone", user.getPhone());
                    ed.putLong("uid", user.getId());
                    ed.apply();
                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                    getActivity().startActivity(intent);
                    getActivity().finish();
                    break;
                } else {
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText(R.string.invalid_sign_in);
                }
            }
            if (loginEmail.getText().toString().isEmpty() || loginPassword.getText().toString().isEmpty()) {
                errorMessage.setVisibility(View.VISIBLE);
                errorMessage.setText(R.string.empty_field_error);
            }
        }
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void togglePassword(EditText password, ImageView showPassword) {
        if (password.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            showPassword.setImageResource(R.drawable.visible_password_icon);
        } else {
            password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            showPassword.setImageResource(R.drawable.hidden_password_icon);
        }
    }

}