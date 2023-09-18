package com.imaginato.homeworkmvvm.data.local.demo

import androidx.room.Database
import androidx.room.RoomDatabase
import com.imaginato.homeworkmvvm.data.local.login.User
import com.imaginato.homeworkmvvm.data.local.login.UserDao

@Database(entities = [Demo::class,User::class], version = 1, exportSchema = false)
abstract class DemoDatabase : RoomDatabase() {
    abstract val demoDao: DemoDao
    abstract val userDao: UserDao
}