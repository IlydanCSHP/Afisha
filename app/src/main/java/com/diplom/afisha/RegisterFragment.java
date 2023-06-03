package com.diplom.afisha;

import static android.content.ContentValues.TAG;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

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

import com.diplom.afisha.dao.UserDao;
import com.diplom.afisha.database.AfishaRoomDatabase;
import com.diplom.afisha.model.User;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class RegisterFragment extends Fragment {

    AppCompatButton registerButton;
    View fragment;
    TextView errorMessage;
    List<User> users;
    EditText registerPassword;
    EditText confirmPassword;
    ImageView showPassword;
    ImageView showConfirm;

    public RegisterFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.fragment_register, container, false);
        registerButton = fragment.findViewById(R.id.register_button);
        registerButton.setOnClickListener(view -> registerNewUser(view));
        errorMessage = fragment.findViewById(R.id.error_message);
        errorMessage.setVisibility(View.GONE);
        users = LoginActivity.usersList;

        registerPassword = fragment.findViewById(R.id.register_password);
        confirmPassword = fragment.findViewById(R.id.register_confirm);
        showPassword = fragment.findViewById(R.id.show_password_button);
        showConfirm = fragment.findViewById(R.id.show_confirm_button);

        showPassword.setOnClickListener(v -> togglePassword(registerPassword, showPassword));
        showConfirm.setOnClickListener(v -> togglePassword(confirmPassword, showConfirm));

        Log.d(TAG, "RegisterUsers: " + users);
        Log.d(TAG, "RegisterUsersList: " + LoginActivity.usersList);
        return fragment;
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

    private void registerNewUser(View view) {
        EditText registerName, registerPhone, registerEmail;
        registerName = fragment.findViewById(R.id.register_name);
        registerPhone = fragment.findViewById(R.id.register_phone);
        registerEmail = fragment.findViewById(R.id.register_email);
        AtomicBoolean alreadyExists = new AtomicBoolean(false);

        if (registerName.getText().length() > 0 &&
                registerPhone.getText().length() > 0 &&
                registerEmail.getText().length() > 0 &&
                registerPassword.getText().length() > 0) {
            users.forEach(user -> {
                if (user.getEmail().equals(registerEmail.getText().toString())) {
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText("Такой пользователь уже зарегистрирован");
                    alreadyExists.set(true);
                }
            });
            if (alreadyExists.get()) {
                return;
            }

            if (isValidEmail(registerEmail.getText().toString()) && registerPassword.getText().toString().equals(confirmPassword.getText().toString())) {

                User user = new User(registerName.getText().toString(),
                        registerPhone.getText().toString(),
                        registerEmail.getText().toString(),
                        registerPassword.getText().toString());

                InsertAsyncTask insertAsyncTask = new InsertAsyncTask();
                insertAsyncTask.execute(user);
                errorMessage.setVisibility(View.GONE);
                errorMessage.setText("");
                users.add(user);
                Toast.makeText(getActivity(),
                        "Успешная регистрация!" +
                                " \nПерейдите во вкладку \"Вход\"", Toast.LENGTH_LONG).show();
            } else if (!isValidEmail(registerEmail.getText().toString())) {
                errorMessage.setVisibility(View.VISIBLE);
                errorMessage.setText(R.string.invalid_email_error);
            } else if (!registerPassword.getText().toString().equals(confirmPassword.getText().toString())) {
                errorMessage.setVisibility(View.VISIBLE);
                errorMessage.setText(R.string.not_maching_error);
            }
        } else {
            errorMessage.setVisibility(View.VISIBLE);
            errorMessage.setText(R.string.empty_field_error);
        }

    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    class InsertAsyncTask extends AsyncTask<User, Void, Void> {

        @Override
        protected Void doInBackground(User... users) {
            UserDao userDao = AfishaRoomDatabase.getInstance(getActivity()).userDao();
            userDao.insertAll(users);
            return null;
        }
    }
}