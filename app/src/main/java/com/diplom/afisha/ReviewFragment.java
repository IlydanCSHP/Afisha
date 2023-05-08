package com.diplom.afisha;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.diplom.afisha.adapter.ReviewAdapter;
import com.diplom.afisha.dao.ReviewDao;
import com.diplom.afisha.database.AfishaRoomDatabase;
import com.diplom.afisha.model.Review;

import java.util.ArrayList;
import java.util.List;

public class ReviewFragment extends Fragment {

    RecyclerView reviewRecycler;
    ReviewAdapter adapter;
    List<Review> reviewList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment = inflater.inflate(R.layout.fragment_review, container, false);
        reviewList = new ArrayList<>();
        getUserReviews();
        reviewRecycler = fragment.findViewById(R.id.review_recycler);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(fragment.getContext());
        reviewRecycler.setLayoutManager(manager);
        adapter = new ReviewAdapter(getActivity(), reviewList, getActivity(), true);
        reviewRecycler.setAdapter(adapter);
        return fragment;
    }

    private void getUserReviews(){
        new Thread(() ->{
            ReviewDao reviewDao = AfishaRoomDatabase.getInstance(getActivity()).reviewDao();
            SharedPreferences sPref = getActivity().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
            reviewList = reviewDao.findByUserId(sPref.getLong("uid", 0L));
            getActivity().runOnUiThread(() -> {
                adapter = new ReviewAdapter(getActivity(), reviewList, getActivity(), true);
                reviewRecycler.setAdapter(adapter);
            });
        }).start();
    }
}