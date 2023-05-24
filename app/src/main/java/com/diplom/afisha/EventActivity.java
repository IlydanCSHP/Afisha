package com.diplom.afisha;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.diplom.afisha.adapter.ReviewAdapter;
import com.diplom.afisha.dao.EventDao;
import com.diplom.afisha.dao.ReviewDao;
import com.diplom.afisha.dao.TicketDao;
import com.diplom.afisha.database.AfishaRoomDatabase;
import com.diplom.afisha.model.Event;
import com.diplom.afisha.model.Review;
import com.diplom.afisha.model.Ticket;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class EventActivity extends AppCompatActivity {

    TextView eventTitle;
    TextView reviewCount;
    TextView eventRating;
    TextView eventDescription;
    TextView eventAddress;
    TextView eventPrice;
    TextView addReviewButton;
    TextView ticketNumber;
    RecyclerView reviewsRecycler;
    ReviewAdapter adapter;
    List<Review> reviewList;
    ImageButton backButton;
    Integer rate = 0;
    Intent intent;
    AppCompatButton ticketButton;
    EditText searchBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        reviewList = new ArrayList<>();
        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(view -> onBackPressed());
        addReviewButton = findViewById(R.id.add_review);
        addReviewButton.setOnClickListener(view -> addReview());
        intent = getIntent();
        ticketButton = findViewById(R.id.ticket_button);
        SharedPreferences sPref = getSharedPreferences("userInfo", MODE_PRIVATE);
        if (sPref.getBoolean("isAdmin", false)) {
            ticketButton.setOnClickListener(view -> openTickets());
        } else if (sPref.getBoolean("isSignedIn", false)) {
            ticketButton.setOnClickListener(view -> showTicketDialog(view));
        } else {
            ticketButton.setOnClickListener(view -> Toast.makeText(this, "Войдите чтобы заказать билет", Toast.LENGTH_SHORT).show());
        }
        getReviews();
        setEventInfo();
        setReviewsRecycler();
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == 0) {
            deleteReview(item.getGroupId());
        }
        return super.onContextItemSelected(item);
    }

    private void deleteReview(int groupId) {
        new Thread(() -> {
            ReviewDao reviewDao = AfishaRoomDatabase.getInstance(this).reviewDao();
            Review review = reviewDao.findById(reviewList.get(groupId).getId());
            reviewDao.delete(review);
            runOnUiThread(() -> {
                getReviews();
            });
        }).start();
    }

    private void openTickets() {
        Intent ticketsIntent = new Intent(EventActivity.this, TicketActivity.class);
        ticketsIntent.putExtra("event_id", intent.getLongExtra("event_id", 0L));
        startActivity(ticketsIntent);
    }

    private void setTitle(TextView title) {
        new Thread(() -> {
            EventDao eventDao = AfishaRoomDatabase.getInstance(this).eventDao();
            Long id = intent.getLongExtra("event_id", 0L);
            Event event = eventDao.findById(id);
            runOnUiThread(() -> {
                if (event.getTicketsNumber() > 0)
                    title.setText("Бронирование билетов\n(" + event.getTicketsNumber() + " осталось)");
                else
                    title.setText("Бронирование билетов\n(Билетов не осталось)");
            });
        }).start();
    }

    private void showTicketDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View inflater = LayoutInflater.from(this).inflate(R.layout.add_ticket_dialog, null);
        builder.setView(inflater);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

        DatePicker datePicker = inflater.findViewById(R.id.ticket_date);
        EditText ticketTime = inflater.findViewById(R.id.ticket_time);
        TextView title = inflater.findViewById(R.id.dialog_title);
        Event event = new Event();
        setTitle(title);

        int year = datePicker.getYear();
        int month = datePicker.getMonth();
        int dayOfMonth = datePicker.getDayOfMonth();
        datePicker.setMaxDate(System.currentTimeMillis() + 604800000L);
        datePicker.setMinDate(System.currentTimeMillis());

        ViewGroup vg = (ViewGroup) datePicker.getChildAt(0);
        vg.getChildAt(0).setVisibility(View.GONE);
        datePicker.init(year, month, dayOfMonth, new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Оставить пустым для предотвращения изменения даты
            }
        });

        Field[] datePickerDialogFields = datePicker.getClass().getDeclaredFields();

        for (Field datePickerDialogField : datePickerDialogFields) {

            if (datePickerDialogField.getName().equals("mMonthPicker")) {
                datePickerDialogField.setAccessible(true);
                Object monthPicker = null;
                try {
                    monthPicker = datePickerDialogField.get(datePicker);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                ((View) monthPicker).setVisibility(View.GONE);
            }
        }
        AppCompatButton cancelButton = inflater.findViewById(R.id.cancel_button);
        AppCompatButton applyButton = inflater.findViewById(R.id.apply_button);
        cancelButton.setOnClickListener(v -> dialog.dismiss());
        applyButton.setOnClickListener(v -> {
            if (ticketTime.getText().toString().isEmpty()) {
                Toast.makeText(this, "ОШИБКА. Укажите время записи!", Toast.LENGTH_SHORT).show();
            } else {
                addTicket(year, month, dayOfMonth, ticketTime, title);
            }
        });
    }

    private void addTicket(int year, int month, int dayOfMonth, EditText ticketTime, TextView title) {
        new Thread(() -> {
            try {
                TicketDao ticketDao = AfishaRoomDatabase.getInstance(this).ticketDao();
                EventDao eventDao = AfishaRoomDatabase.getInstance(this).eventDao();
                Event event = eventDao.findById(intent.getLongExtra("event_id", 0L));
                Log.d(TAG, "addTicket: " + ticketDao.getAll());
                if (event.getTicketsNumber() > 0) {
                    SharedPreferences sPref = getSharedPreferences("userInfo", MODE_PRIVATE);
                    Integer ticketNumber = (new Random()).nextInt(900000) + 100000;
                    String ticketDate = dayOfMonth + "." + month + "." + year;
                    Ticket ticket = new Ticket(intent.getLongExtra("event_id", 0L),
                            sPref.getLong("uid", 0L),
                            ticketDate, ticketTime.getText().toString(),
                            ticketNumber);

                    ticketDao.insert(ticket);
                    event.setTicketsNumber(event.getTicketsNumber() - 1);
                    eventDao.update(event);
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Забронированно успшено!", Toast.LENGTH_SHORT).show();
                        setTitle(title);
                        setEventInfo();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "ОШИБКА. Билетов не осталось!", Toast.LENGTH_SHORT).show();
                        setTitle(title);
                    });
                }
            } catch (SQLiteConstraintException e) {
                runOnUiThread(() -> {
                    Toast.makeText(this, "ОШИБКА. Вы уже забронировали билет!", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void setRating(View view) {
        ImageView rate1 = view.findViewById(R.id.rating_1);
        ImageView rate2 = view.findViewById(R.id.rating_2);
        ImageView rate3 = view.findViewById(R.id.rating_3);
        ImageView rate4 = view.findViewById(R.id.rating_4);
        ImageView rate5 = view.findViewById(R.id.rating_5);

        rate1.setOnClickListener(v -> {
            rate1.setImageResource(R.drawable.rate_icon_big);
            rate2.setImageResource(R.drawable.rate_icon_empty);
            rate3.setImageResource(R.drawable.rate_icon_empty);
            rate4.setImageResource(R.drawable.rate_icon_empty);
            rate5.setImageResource(R.drawable.rate_icon_empty);
            rate = 1;
        });
        rate2.setOnClickListener(v -> {
            rate2.setImageResource(R.drawable.rate_icon_big);
            rate1.setImageResource(R.drawable.rate_icon_big);
            rate3.setImageResource(R.drawable.rate_icon_empty);
            rate4.setImageResource(R.drawable.rate_icon_empty);
            rate5.setImageResource(R.drawable.rate_icon_empty);
            rate = 2;
        });
        rate3.setOnClickListener(v -> {
            rate3.setImageResource(R.drawable.rate_icon_big);
            rate2.setImageResource(R.drawable.rate_icon_big);
            rate1.setImageResource(R.drawable.rate_icon_big);
            rate4.setImageResource(R.drawable.rate_icon_empty);
            rate5.setImageResource(R.drawable.rate_icon_empty);
            rate = 3;
        });
        rate4.setOnClickListener(v -> {
            rate4.setImageResource(R.drawable.rate_icon_big);
            rate2.setImageResource(R.drawable.rate_icon_big);
            rate3.setImageResource(R.drawable.rate_icon_big);
            rate1.setImageResource(R.drawable.rate_icon_big);
            rate5.setImageResource(R.drawable.rate_icon_empty);
            rate = 4;
        });
        rate5.setOnClickListener(v -> {
            rate5.setImageResource(R.drawable.rate_icon_big);
            rate2.setImageResource(R.drawable.rate_icon_big);
            rate3.setImageResource(R.drawable.rate_icon_big);
            rate4.setImageResource(R.drawable.rate_icon_big);
            rate1.setImageResource(R.drawable.rate_icon_big);
            rate = 5;
        });

    }

    private void getReviews() {
        new Thread(() -> {
            ReviewDao reviewDao = AfishaRoomDatabase.getInstance(this).reviewDao();
            reviewList = reviewDao.findByEventId(intent.getLongExtra("event_id", 0));
            runOnUiThread(() -> {
                if (reviewList != null) {
                    Log.d(TAG, "getReviews: " + reviewList);
                    adapter = new ReviewAdapter(this, reviewList, EventActivity.this, false);
                    reviewsRecycler.setAdapter(adapter);
                }
            });
        }).start();
    }

    private void addReview() {
        SharedPreferences sPref = getSharedPreferences("userInfo", MODE_PRIVATE);
        if (!sPref.getBoolean("isAdmin", false) && sPref.getBoolean("isSignedIn", false)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = LayoutInflater.from(this).inflate(R.layout.add_review_dialog, null);
            builder.setView(view);
            AlertDialog dialog = builder.create();
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.show();
            setRating(view);
            EditText reviewText = view.findViewById(R.id.review_description);
            AppCompatButton cancelButton = view.findViewById(R.id.cancel_button);
            AppCompatButton applyButton = view.findViewById(R.id.apply_button);

            cancelButton.setOnClickListener(v -> dialog.dismiss());
            applyButton.setOnClickListener(v -> {
                if (!reviewText.getText().toString().equals("") && rate > 0) {
                    new Thread(() -> {
                        ReviewDao reviewDao = AfishaRoomDatabase.getInstance(this).reviewDao();
                        Review review = new Review(reviewText.getText().toString(),
                                (new SimpleDateFormat("dd.MM.yyyy")).format((new Date())),
                                intent.getLongExtra("event_id", 0L),
                                sPref.getLong("uid", 0L), rate);
                        reviewDao.insert(review);
                        getReviews();
                        updateRating();
                        runOnUiThread(() -> {
                            setEventInfo();
                        });
                        dialog.dismiss();
                    }).start();
                    Toast.makeText(this, "Отзыв добавлен", Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (sPref.getBoolean("isAdmin", false))
            Toast.makeText(this, "Нельзя оставить отзыв в режиме администратора", Toast.LENGTH_SHORT).show();
        if (!sPref.getBoolean("isSignedIn", false) && !sPref.getBoolean("isAdmin", false))
            Toast.makeText(this, "Войдите в аккаунт чтобы оставить отзыв", Toast.LENGTH_SHORT).show();
    }

    private void updateRating() {
        new Thread(() -> {
            SharedPreferences sPref = getSharedPreferences("userInfo", MODE_PRIVATE);
            EventDao eventDao = AfishaRoomDatabase.getInstance(this).eventDao();
            ReviewDao reviewDao = AfishaRoomDatabase.getInstance(this).reviewDao();
            Event event = eventDao.findById(intent.getLongExtra("event_id", 0));
            Log.d(TAG, "updateRating: " + event);
            Log.d(TAG, "updateRating: " + intent.getLongExtra("event_id", 0));
            if (event != null) {
                event.setRating(reviewDao.getEventRating(event.getId()));
                eventDao.update(event);
            }
        }).start();
    }

    private void setReviewsRecycler() {

        reviewsRecycler = findViewById(R.id.review_recycler);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        reviewsRecycler.setLayoutManager(manager);
        adapter = new ReviewAdapter(this, reviewList, EventActivity.this, false);
        reviewsRecycler.setAdapter(adapter);
    }

    private void setEventInfo() {
        eventTitle = findViewById(R.id.event_title);
        reviewCount = findViewById(R.id.review_count);
        eventRating = findViewById(R.id.event_rating);
        eventDescription = findViewById(R.id.event_description);
        eventAddress = findViewById(R.id.event_address);
        eventPrice = findViewById(R.id.event_price);
        ticketNumber = findViewById(R.id.ticket_number);
        SharedPreferences sPref = getSharedPreferences("userInfo", MODE_PRIVATE);

        eventTitle.setText(intent.getStringExtra("event_title"));
        new Thread(() -> {
            ReviewDao reviewDao = AfishaRoomDatabase.getInstance(this).reviewDao();
            EventDao eventDao = AfishaRoomDatabase.getInstance(this).eventDao();
            TicketDao ticketDao = AfishaRoomDatabase.getInstance(this).ticketDao();
            List<Review> reviews = reviewDao.findByEventId(intent.getLongExtra("event_id", 0L));
            Double rating = reviewDao.getEventRating(intent.getLongExtra("event_id", 0L));
            Event event = eventDao.findById(intent.getLongExtra("event_id", 0L));
            Ticket ticket = ticketDao.findByUserEvent(intent.getLongExtra("event_id", 0L), sPref.getLong("uid", 0L));
            Log.d(TAG, "setEventInfo: " + sPref.getLong("uid", 0L) + intent.getLongExtra("event_id", 0L));
            Log.d(TAG, "setEventInfo: " + ticket);
            runOnUiThread(() -> {
                reviewCount.setText("Отзывов: " + reviews.size());
                eventAddress.setText("Адрес: " + event.getAddress());
                eventPrice.setText("Цена: " + event.getPrice());

                if (rating != null) {
                    eventRating.setText(String.format("%.1f", rating));
                }
                if (sPref.getBoolean("isSignedIn", false) && ticket != null) {
                    Log.d(TAG, "setEventInfo: " + ticket);
                    ticketNumber.setVisibility(View.VISIBLE);
                    ticketNumber.setText("Номер билета: №" + ticket.getNumber());
                }
            });
        }).start();

        eventDescription.setText(intent.getStringExtra("event_description"));

    }

}