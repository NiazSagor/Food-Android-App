package com.angik.duodevloopers.food.Model;

import java.util.Calendar;

@SuppressWarnings("ALL")
public class Analytic {
    private long Count;
    private long Total;

    public Analytic() {

    }

    public Analytic(long count, long amount) {
        this.Count = count;
        this.Total = amount;
    }

    public long getCount() {
        return Count;
    }

    public void setCount(long count) {
        Count = count;
    }

    public long getTotal() {
        return Total;
    }

    public void setTotal(long total) {
        Total = total;
    }

    public String returnCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);

        //If minute part of the time is containing 1 digit
        if (String.valueOf(min).length() > 0 && String.valueOf(min).length() <= 1) {
            if (String.valueOf(hour).length() == 2) {
                return hour + ":" + "0" + min;
            } else {
                //If hour and min both part containing 1 digit each
                return "0" + hour + ":" + "0" + min;
            }
        } else if (String.valueOf(hour).length() > 0 && String.valueOf(hour).length() <= 1) { //If the hour part of the time is containing 1 digit
            if (String.valueOf(min).length() == 2) {
                return "0" + hour + ":" + min;
            } else {
                //If hour and min both part containing 1 digit each
                return "0" + hour + ":" + "0" + min;
            }
        } else {
            //If both min and hour part are 2 digits each
            return hour + ":" + min;
        }
    }

}
