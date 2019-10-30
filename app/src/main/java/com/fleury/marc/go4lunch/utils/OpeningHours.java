package com.fleury.marc.go4lunch.utils;

import android.content.res.Resources;

import com.fleury.marc.go4lunch.R;
import com.google.android.libraries.places.api.model.DayOfWeek;
import com.google.android.libraries.places.api.model.Period;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class OpeningHours {

    private List<Period> periods2 = new ArrayList<>();

    public String getOpening(Resources resources, List<Period> periods) {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        String s = "";

        switch (day) {
            case Calendar.MONDAY:
                filterByDay(periods, DayOfWeek.MONDAY);
                break;
            case Calendar.TUESDAY:
                filterByDay(periods, DayOfWeek.TUESDAY);
                break;
            case Calendar.WEDNESDAY:
                filterByDay(periods, DayOfWeek.WEDNESDAY);
                break;
            case Calendar.THURSDAY:
                filterByDay(periods, DayOfWeek.THURSDAY);
                break;
            case Calendar.FRIDAY:
                filterByDay(periods, DayOfWeek.FRIDAY);
                break;
            case Calendar.SATURDAY:
                filterByDay(periods, DayOfWeek.SATURDAY);
                break;
            case Calendar.SUNDAY:
                filterByDay(periods, DayOfWeek.SUNDAY);
                break;
        }

        for (Period p : periods2) {
            if (p.getClose().getTime().getHours() < hour) {
                s = resources.getString(R.string.closed);
            } else if (p.getClose().getTime().getHours() - hour == 0) {
                if (p.getClose().getTime().getMinutes() < minute) {
                    s = resources.getString(R.string.closed);
                } else {
                    s = resources.getString(R.string.closing_soon);
                }
            } else {
                s = String.format(resources.getString(R.string.open_until), p.getClose().getTime().getHours(), p.getClose().getTime().getMinutes());
            }
        }
        return s;
    }

    private void filterByDay(List<Period> periods, DayOfWeek day) {
        for (Period p : periods) {
            if (p.getOpen().getDay() == day && p.getClose().getDay() == day) {
                periods2.add(p);
            }
        }
    }

}
