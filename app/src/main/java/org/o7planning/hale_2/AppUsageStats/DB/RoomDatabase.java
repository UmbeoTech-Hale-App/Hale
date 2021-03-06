package org.o7planning.hale_2.AppUsageStats.DB;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import org.o7planning.hale_2.AppUsageStats.DB.Dao.AppItemDao;
import org.o7planning.hale_2.AppUsageStats.DB.Dao.HistoryItemDao;
import org.o7planning.hale_2.AppUsageStats.DB.Dao.IgnoreItemDao;
import org.o7planning.hale_2.AppUsageStats.DB.Dao.ScreenItemDao;
import org.o7planning.hale_2.AppUsageStats.Models.AppItem;
import org.o7planning.hale_2.AppUsageStats.Models.HistoryItem;
import org.o7planning.hale_2.AppUsageStats.Models.IgnoreItem;
import org.o7planning.hale_2.AppUsageStats.Models.ScreenItem;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {AppItem.class, HistoryItem.class, IgnoreItem.class, ScreenItem.class},
        version = 3, exportSchema = false)
@TypeConverters(Converter.class)
public abstract class RoomDatabase extends androidx.room.RoomDatabase {

    public abstract AppItemDao getAppItemDao();
    public abstract HistoryItemDao getHistoryItemDao();
    public abstract IgnoreItemDao getIgnoreItemDao();
    public abstract ScreenItemDao getScreenItemDao();

//    public abstract UserProfileResponseDao getUserProfileResponseDao();

    private static volatile RoomDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 5;
    static final ExecutorService databaseWriteExecutor =
//            Executors.newSingleThreadExecutor();
//            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
            Executors.newScheduledThreadPool(NUMBER_OF_THREADS);

    static RoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
//            ToastUtil.makeShortToast(context, "Database created");
            synchronized (RoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RoomDatabase.class, "database")
                            .allowMainThreadQueries()
                            .addCallback(sRoomDatabaseCallback)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        } else {
//            ToastUtil.makeShortToast(context, "Database Already exists");
        }
        return INSTANCE;
    }

    private static Callback sRoomDatabaseCallback = new Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            // If you want to keep data through app restarts,
            // comment out the following block
//            databaseWriteExecutor.execute(() -> {
//                // Populate the database in the background.
//                // If you want to start with more words, just add them.
//                WordDao dao = INSTANCE.getWordDao();
//                dao.deleteAll();
//
//                Word word = new Word("Hello");
//                dao.insert(word);
//                word = new Word("World");
//                dao.insert(word);
//            });
        }
    };
}

