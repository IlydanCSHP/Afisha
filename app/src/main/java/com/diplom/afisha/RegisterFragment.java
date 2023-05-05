package com.diplom.afisha;

import static android.content.ContentValues.TAG;

import android.os.AsyncTask;
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
        Log.d(TAG, "RegisterUsers: " + users);
        Log.d(TAG, "RegisterUsersList: " + LoginActivity.usersList);
        return fragment;
    }

    private void registerNewUser(View view) {
        EditText registerName, registerPhone, registerEmail, registerPassword;
        registerName = fragment.findViewById(R.id.register_name);
        registerPhone = fragment.findViewById(R.id.register_phone);
        registerEmail = fragment.findViewById(R.id.register_email);
        registerPassword = fragment.findViewById(R.id.register_password);
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
            User user = new User(registerName.getText().toString(),
                    registerPhone.getText().toString(),
                    registerEmail.getText().toString(),
                    registerPassword.getText().toString());

            InsertAsyncTask insertAsyncTask = new InsertAsyncTask();
            insertAsyncTask.execute(user);
            errorMessage.setVisibility(View.GONE);
            errorMessage.setText("");
            users.add(user);
            Toast.makeText(getActivity(), "Успешная регистрация!", Toast.LENGTH_SHORT).show();
        } else {
            errorMessage.setVisibility(View.VISIBLE);
            errorMessage.setText(R.string.empty_field_error);
        }

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