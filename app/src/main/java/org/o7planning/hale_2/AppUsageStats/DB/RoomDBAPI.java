package org.o7planning.hale_2.AppUsageStats.DB;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import org.o7planning.hale_2.AppUsageStats.DB.Dao.AppItemDao;
import org.o7planning.hale_2.AppUsageStats.DB.Dao.HistoryItemDao;
import org.o7planning.hale_2.AppUsageStats.DB.Dao.IgnoreItemDao;
import org.o7planning.hale_2.AppUsageStats.DB.Dao.ScreenItemDao;
import org.o7planning.hale_2.AppUsageStats.Models.HistoryItem;
import org.o7planning.hale_2.AppUsageStats.Models.IgnoreItem;
import org.o7planning.hale_2.AppUsageStats.Models.ScreenItem;

import java.util.List;


public class RoomDBAPI extends AndroidViewModel {

//    private static HomePageResponseDao mHomePageResponseDao;

    private static AppItemDao mAppItemDao;
    private static IgnoreItemDao mIgnoreItemDao;
    private static HistoryItemDao mHistoryItemDao;
    private static ScreenItemDao mScreenItemDao;

    public static RoomDatabase mRoomDatabase;

    public static void init(@NonNull Application application)
    {
        mRoomDatabase = RoomDatabase.getDatabase(application);

        mAppItemDao = mRoomDatabase.getAppItemDao();
        mHistoryItemDao = mRoomDatabase.getHistoryItemDao();
        mIgnoreItemDao = mRoomDatabase.getIgnoreItemDao();
        mScreenItemDao = mRoomDatabase.getScreenItemDao();
    }

    public RoomDBAPI(@NonNull Application application) {
        super(application);
//        RoomDatabase roomDatabase = RoomDatabase.getDatabase(application);
         mRoomDatabase = RoomDatabase.getDatabase(application);

        mAppItemDao = mRoomDatabase.getAppItemDao();
        mHistoryItemDao = mRoomDatabase.getHistoryItemDao();
        mIgnoreItemDao = mRoomDatabase.getIgnoreItemDao();
        mScreenItemDao = mRoomDatabase.getScreenItemDao();

    }


    public static void insertIgnoreItem(IgnoreItem ignoreItem)
    {
        RoomDatabase.databaseWriteExecutor.execute(()->{
            mIgnoreItemDao.insert(ignoreItem);
        });
    }

    public static void insertScreenItem(ScreenItem screenItem)
    {
        RoomDatabase.databaseWriteExecutor.execute(()->{
            mScreenItemDao.insert(screenItem);
        });
    }

    public static void insertHistoryItem(HistoryItem historyItem)
    {
        RoomDatabase.databaseWriteExecutor.execute(()->{
            mHistoryItemDao.insert(historyItem);
        });
    }

    public static void insertIgnoreItemList(List<IgnoreItem> ignoreItems)
    {
        RoomDatabase.databaseWriteExecutor.execute(()->{
            mIgnoreItemDao.insertAll(ignoreItems);
        });
    }

    public static void insertHistoryItemList(List<HistoryItem> historyItems)
    {
        RoomDatabase.databaseWriteExecutor.execute(()->{
            mHistoryItemDao.insertAll(historyItems);
        });
    }

    public static List<IgnoreItem> getIgnoreItemList()
    {
        return mIgnoreItemDao.getData();
    }

    public static List<HistoryItem> getHistoryItemList()
    {
        return mHistoryItemDao.getData();
    }

    public static List<ScreenItem> getScreenItemList()
    {
        return mScreenItemDao.getData();
    }

}

