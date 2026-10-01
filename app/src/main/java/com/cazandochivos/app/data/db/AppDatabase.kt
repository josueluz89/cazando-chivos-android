package com.cazandochivos.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [EventoEntity::class, LocalEntity::class, MetaEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun eventoDao(): EventoDao
    abstract fun localDao(): LocalDao
    abstract fun metaDao(): MetaDao

    companion object {
        fun crear(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "chivos.db")
                .fallbackToDestructiveMigration()
                .build()
    }
}
