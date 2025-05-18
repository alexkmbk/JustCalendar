package com.alexkmbk.justcalendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DayAdapter extends BaseAdapter {

    private final Context context;

    Calendar currentMonth;
    private List<Calendar> dayList = new ArrayList<>();
    private final Calendar today = Calendar.getInstance();

    public DayAdapter(Context context, Calendar currentMonth) {
        this.context = context;
        this.currentMonth = currentMonth;
    }

    public void setDays(List<Calendar> days) {
        this.dayList = days;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dayList.size();
    }

    @Override
    public Object getItem(int position) {
        return dayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public static boolean isSameMonth(Calendar c1, Calendar c2) {
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH);
    }
    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = new TextView(context);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(0, 20, 0, 20);

        Calendar day = dayList.get(position);

        // Пустая ячейка (если день = null)
        if (day == null) {
            textView.setText("");
            return textView;
        }

        // Проверка на "сегодня"
        if (day.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                day.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                day.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)) {
            textView.setBackgroundResource(R.drawable.circle_background); // Например, синий круг
            textView.setTextColor(Color.WHITE);
        } else {
            textView.setTextColor(Color.BLACK);
        }

        if (day.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || day.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            textView.setTextColor(Color.RED); // Выделяем выходные красным цветом
        }

        textView.setText(String.valueOf(day.get(Calendar.DAY_OF_MONTH)));

        if (!isSameMonth(currentMonth, day)) {
            textView.setAlpha(0.2f); // Устанавливаем полупрозрачность
        }
        return textView;
    }
}
