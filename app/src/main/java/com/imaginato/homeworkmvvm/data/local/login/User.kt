package com.imaginato.homeworkmvvm.data.local.login

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.imaginato.homeworkmvvm.exts.USER

@Entity(tableName = USER)
data class User(
    @PrimaryKey
    val userId: String,
    val xAcc: String? = null,
    var userName: String? = null,
    var isDeleted: Boolean? = null
)
