package io.github.enoua5.openlegendroller.db;

import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {Character.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private  static AppDatabase instance;

    public static AppDatabase getInstance(Context context)
    {
        if(instance == null)
            instance = Room.databaseBuilder(context, AppDatabase.class, "user-database")
                    .fallbackToDestructiveMigration()
                    .build();
        return instance;
    }

    public abstract CharacterDAO characterDAO();
}
