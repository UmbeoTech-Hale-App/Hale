package org.o7planning.hale_2.AppUsageStats.Models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "ScreenItem_table")
public class ScreenItem {


    String state;

    @PrimaryKey
    long time;

    public ScreenItem(){}

    public ScreenItem(String s, long t) {
        state = s;
        time = t;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

}
