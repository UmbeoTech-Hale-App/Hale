package org.o7planning.hale_2.AppUsageStats.DB.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import org.o7planning.hale_2.AppUsageStats.Models.ScreenItem;

import java.util.List;

@Dao
public interface ScreenItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ScreenItem screenItem);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ScreenItem> screenItems);

    @Query("select * from ScreenItem_table")
    List<ScreenItem> getData();

}
