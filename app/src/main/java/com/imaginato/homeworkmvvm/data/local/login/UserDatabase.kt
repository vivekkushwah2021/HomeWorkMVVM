package com.imaginato.homeworkmvvm.data.local.login

import androidx.room.Database
import androidx.room.RoomDatabase
import com.imaginato.homeworkmvvm.exts.VERSION

@Database(entities = [User::class], version = VERSION, exportSchema = false)
abstract class UserDatabase : RoomDatabase() {
    abstract val userDao: UserDao
}