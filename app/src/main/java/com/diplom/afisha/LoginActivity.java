package com.diplom.afisha;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.viewpager2.widget.ViewPager2;

import com.diplom.afisha.adapter.LoginViewPageAdapter;
import com.diplom.afisha.dao.UserDao;
import com.diplom.afisha.database.AfishaRoomDatabase;
import com.diplom.afisha.model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    ViewPager2 viewPager;
    TabLayout tabLayout;
    LoginViewPageAdapter loginAdapter;
    static List<User> usersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.white));

        UserDao userDao = AfishaRoomDatabase.getInstance(this).userDao();
        LiveData<List<User>> usersLive = userDao.getAllLive();

        usersLive.observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                Log.d(TAG, "onChanged: " + users);
                usersList = users;
                Log.d(TAG, "usersList: " + usersList);
            }
        });

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        loginAdapter = new LoginViewPageAdapter(getSupportFragmentManager(), getLifecycle());
        loginAdapter.addFragment(new SignInFragment(), "Вход");
        loginAdapter.addFragment(new RegisterFragment(), "Регистрация");

        Log.d(TAG, "onCreate: " + loginAdapter.getItemCount());
        viewPager.setAdapter(loginAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(loginAdapter.getPageTitle(position))).attach();

        ImageButton backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(view -> onBackPressed());
    }
}