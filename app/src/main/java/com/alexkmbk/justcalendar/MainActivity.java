package com.alexkmbk.justcalendar;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

//import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    private Calendar currentMonth;
    private TextView monthText;
    private GridView daysGrid;
    private DayAdapter adapter;

    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentMonth = Calendar.getInstance();

        monthText = findViewById(R.id.monthText);
        daysGrid = findViewById(R.id.daysGrid);
        adapter = new DayAdapter(this, currentMonth);
        daysGrid.setAdapter(adapter);

        ImageButton prevButton = findViewById(R.id.prevButton);
        ImageButton nextButton = findViewById(R.id.nextButton);

        prevButton.setOnClickListener(v -> {
            currentMonth.add(Calendar.MONTH, -1);
            updateCalendar();
        });

        nextButton.setOnClickListener(v -> {
            currentMonth.add(Calendar.MONTH, 1);
            updateCalendar();
        });

        // Days headers
        Locale currentLocale = Locale.getDefault();
        GridLayout weekHeader = findViewById(R.id.weekHeader);
        String[] weekDays = new DateFormatSymbols(currentLocale).getShortWeekdays();

        Calendar cal = Calendar.getInstance(currentLocale);
        int firstDay = cal.getFirstDayOfWeek(); // Usually MONDAY

        for (int i = 0; i < 7; i++) {
            int dayIndex = (firstDay + i - 1) % 7 + 1;
            String dayLabel = weekDays[dayIndex];

            TextView tv = new TextView(this);
            tv.setText(dayLabel);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

            if (dayIndex == Calendar.SATURDAY) {
                tv.setTextColor(Color.BLUE); // Выделяем выходные красным цветом
            }else if (dayIndex == Calendar.SUNDAY) {
                tv.setTextColor(Color.RED); // Выделяем выходные красным цветом
            } else {
                tv.setTextColor(Color.parseColor("#888888"));
            }
            tv.setGravity(Gravity.CENTER);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(i, 1f); // columnWeight=1
            tv.setLayoutParams(params);

            weekHeader.addView(tv);
        }

        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent event) {

                // don't return false here or else none of the other
                // gestures will work
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float diffX = e2.getX() - e1.getX();
                float diffY = e2.getY() - e1.getY();

                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            // Свайп вправо – предыдущий месяц
                            currentMonth.add(Calendar.MONTH, -1);
                        } else {
                            // Свайп влево – следующий месяц
                            currentMonth.add(Calendar.MONTH, 1);
                        }
                        updateCalendar();
                        return true;
                    }
                }else {
                    // Вертикальный свайп
                    if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            currentMonth.add(Calendar.MONTH, -1);     // свайп вниз
                        } else {
                            currentMonth.add(Calendar.MONTH, 1); // свайп вверх
                        }
                        updateCalendar();
                        return true;
                    }
                    return false;
                }
                return false;
            }
        });

        LinearLayout root = findViewById(R.id.rootLayout);
        root.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

        updateCalendar();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    private void updateCalendar() {

        Locale currentLocale = Locale.getDefault();

        String formattedDate = new SimpleDateFormat("LLLL yyyy", currentLocale)
                .format(currentMonth.getTime());
        monthText.setText(Character.toUpperCase(formattedDate.charAt(0)) + formattedDate.substring(1));

        // Days grid

        List<Calendar> days = new ArrayList<>();
        Calendar tempCal = (Calendar) currentMonth.clone();
        tempCal.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfWeek = tempCal.getFirstDayOfWeek(); // понедельник или воскресенье
        int dayOfWeekOfFirst = tempCal.get(Calendar.DAY_OF_WEEK);

        // Рассчитываем сдвиг
        int shift = (7 + (dayOfWeekOfFirst - firstDayOfWeek)) % 7;

        for (int i = 1; i <= shift; i++) {
            Calendar prevMonth = (Calendar) tempCal.clone();
            prevMonth.add(Calendar.MONTH, -1);
            prevMonth.set(Calendar.DAY_OF_MONTH, prevMonth.getActualMaximum(Calendar.DAY_OF_MONTH) - shift + i);
            days.add(prevMonth);
        }
        int maxDay = currentMonth.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 1; i <= maxDay; i++) {
            Calendar day = (Calendar) tempCal.clone();
            day.set(Calendar.DAY_OF_MONTH, i);
            days.add(day);
        }

        // Add the remaining days of the next month before the end of the week
        int remainingDays = 7 - (days.size() % 7);
        if (remainingDays < 7) {
            for (int i = 1; i <= remainingDays; i++) {
                Calendar nextMonth = (Calendar) tempCal.clone();
                nextMonth.add(Calendar.MONTH, 1);
                nextMonth.set(Calendar.DAY_OF_MONTH, i);
                days.add(nextMonth);
            }
        }
        adapter.setDays(days);
    }
}
