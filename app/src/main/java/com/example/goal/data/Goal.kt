package com.example.goal.data

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class GoalWithId(
    val id: String,
    val goal: Goal
)


data class Goal(
    val goal :String ?=null,
    val habit : String ?=null,
    val period : Int ?=0,
    val type : String ?=null,
    val created : Long ?=0,
    val completedDates : List<String>?=null,
    val completed : Boolean? = false,
)
{
    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        private val formatter = DateTimeFormatter.ISO_LOCAL_DATE

        @RequiresApi(Build.VERSION_CODES.O)
        fun fromLocalDates(dates: List<LocalDate>?): List<String>? {
            return dates?.map { it.format(formatter) }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun toLocalDates(dates: List<String>?): List<LocalDate>? {
            return dates?.map { LocalDate.parse(it, formatter) }
        }
    }
}