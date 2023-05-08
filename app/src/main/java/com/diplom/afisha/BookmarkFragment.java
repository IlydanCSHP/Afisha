package com.diplom.afisha;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.diplom.afisha.adapter.EventAdapter;
import com.diplom.afisha.dao.EventDao;
import com.diplom.afisha.database.AfishaRoomDatabase;
import com.diplom.afisha.model.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BookmarkFragment extends Fragment {

    View fragment;
    EventAdapter adapter;
    List<Event> bookmarkedEvents;
    RecyclerView bookmarkRecycler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragment = inflater.inflate(R.layout.fragment_bookmark, container, false);

        bookmarkedEvents = new ArrayList<>();
        bookmarkRecycler = fragment.findViewById(R.id.bookmark_recycler);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getActivity());
        bookmarkRecycler.setLayoutManager(manager);
        adapter = new EventAdapter(getActivity(), bookmarkedEvents, getActivity());
        bookmarkRecycler.setAdapter(adapter);
        getBookmarkedEvents();
        return fragment;
    }

    private void getBookmarkedEvents() {
        new Thread(() -> {
            EventDao eventDao = AfishaRoomDatabase.getInstance(getActivity()).eventDao();
            SharedPreferences sPref = getActivity().getSharedPreferences("bookmarked_events", Context.MODE_PRIVATE);
            Log.d(TAG, "getBookmarkedEvents: " + sPref.getAll().entrySet());
            Log.d(TAG, "getBookmarkedEvents: " + sPref.getLong("event_id_1", 0L));
            for (Map.Entry<String, ?> entry : sPref.getAll().entrySet()) {
                Event event = eventDao.findById((long) entry.getValue());
                Log.d(TAG, "getBookmarkedEvents: " + event);
                if (event != null) {
                    bookmarkedEvents.add(event);
                }
            }
            getActivity().runOnUiThread(() -> {
                adapter = new EventAdapter(getActivity(), bookmarkedEvents, getActivity());
                bookmarkRecycler.setAdapter(adapter);

            });
        }).start();
    }
}