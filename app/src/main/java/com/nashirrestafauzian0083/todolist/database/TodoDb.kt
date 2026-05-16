package com.nashirrestafauzian0083.todolist.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nashirrestafauzian0083.todolist.model.TodoItem

@Database(entities = [TodoItem::class], version = 2, exportSchema = false)
abstract class TodoDb : RoomDatabase() {

    abstract val dao: TodoDao

    companion object {

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE todo ADD COLUMN isDeleted INTEGER NOT NULL DEFAULT 0")
            }
        }

        @Volatile
        private var INSTANCE: TodoDb? = null

        fun getInstance(context: Context): TodoDb {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        TodoDb::class.java,
                        "todo.db"
                    )
                        .addMigrations(MIGRATION_1_2)
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
