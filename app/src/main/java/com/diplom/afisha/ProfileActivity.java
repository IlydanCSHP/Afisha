package com.diplom.afisha;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.diplom.afisha.adapter.AfishaViewPageAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ProfileActivity extends AppCompatActivity {

    TextView profileUsername;
    TextView profileEmail;
    AfishaViewPageAdapter profileAdapter;
    ViewPager2 viewPager;
    TabLayout tabLayout;
    AppCompatButton mainButton, signOutButton;
    SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sPref = getSharedPreferences("userInfo", MODE_PRIVATE);
        profileUsername = findViewById(R.id.profile_name);
        profileEmail = findViewById(R.id.profile_email);

        mainButton = findViewById(R.id.main_button);
        mainButton.setOnClickListener(view -> goToMain(view));

        signOutButton = findViewById(R.id.sign_out_button);
        signOutButton.setOnClickListener(view -> signOut(view));
        profileUsername.setText(sPref.getString("username", ""));
        profileEmail.setText(sPref.getString("email", ""));

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        profileAdapter = new AfishaViewPageAdapter(getSupportFragmentManager(), getLifecycle());
        profileAdapter.addFragment(new BookmarkFragment(), "Избранное");
        profileAdapter.addFragment(new ReviewFragment(), "Отзывы");
        viewPager.setAdapter(profileAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(profileAdapter.getPageTitle(position))).attach();
    }

    private void signOut(View view) {
        SharedPreferences.Editor ed = sPref.edit();
        ed.putBoolean("isSignedIn", false);
        ed.putBoolean("isAdmin", false);
        ed.apply();
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToMain(View view) {
        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
        startActivity(intent);
    }
}