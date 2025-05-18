package  com.alexkmbk.justcalendar;

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

//import androidx.appcompat.app.AppCompatActivity;

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

        updateCalendar();
    }

    private void updateCalendar() {
       SimpleDateFormat sdf = new SimpleDateFormat("LLLL yyyy", new Locale("ru"));
        String formattedDate = sdf.format(currentMonth.getTime());
        monthText.setText(formattedDate.substring(0, 1).toUpperCase() + formattedDate.substring(1));

        List<Calendar> days = new ArrayList<>();
        Calendar tempCal = (Calendar) currentMonth.clone();
        tempCal.set(Calendar.DAY_OF_MONTH, 1);

int shift = (tempCal.get(Calendar.DAY_OF_WEEK) + 5) % 7;

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
