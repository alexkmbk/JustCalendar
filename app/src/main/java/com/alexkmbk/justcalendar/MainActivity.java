package  com.alexkmbk.justcalendar;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
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

    private Calendar calendar;
    private TextView monthText;
    private GridView daysGrid;
    private DayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendar = Calendar.getInstance();

        monthText = findViewById(R.id.monthText);
        daysGrid = findViewById(R.id.daysGrid);
        adapter = new DayAdapter(this);
        daysGrid.setAdapter(adapter);

        ImageButton prevButton = findViewById(R.id.prevButton);
        ImageButton nextButton = findViewById(R.id.nextButton);

        prevButton.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, -1);
            updateCalendar();
        });

        nextButton.setOnClickListener(v -> {
            calendar.add(Calendar.MONTH, 1);
            updateCalendar();
        });

        updateCalendar();
    }

    private void updateCalendar() {
       SimpleDateFormat sdf = new SimpleDateFormat("LLLL yyyy", new Locale("ru"));
        String formattedDate = sdf.format(calendar.getTime());
        monthText.setText(formattedDate.substring(0, 1).toUpperCase() + formattedDate.substring(1));

        List<Calendar> days = new ArrayList<>();
        Calendar tempCal = (Calendar) calendar.clone();
        tempCal.set(Calendar.DAY_OF_MONTH, 1);

        int firstDayOfWeek = tempCal.get(Calendar.DAY_OF_WEEK);
        int shift = (firstDayOfWeek + 5) % 7;

        for (int i = 0; i < shift; i++) {
            days.add(null); // Пустые ячейки
        }

        int maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int i = 1; i <= maxDay; i++) {
            Calendar day = (Calendar) tempCal.clone();
            day.set(Calendar.DAY_OF_MONTH, i);
            days.add(day);
        }

        adapter.setDays(days);
    }
}
