package com.dicoding.habitapp.data

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.sqlite.db.SimpleSQLiteQuery

//TODO 2 : Define data access object (DAO)
@Dao
interface HabitDao {

    @Query("SELECT * FROM habits")
    fun getAllHabits(sortedQuery: SimpleSQLiteQuery): DataSource.Factory<Int, Habit>

    @Query("SELECT * FROM habits WHERE id = :habitId")
    fun getHabitById(habitId: Int): LiveData<Habit>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHabit(habit: Habit): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg habits: Habit)

    @Delete
    fun deleteHabit(habit: Habit)

    @Query("SELECT * FROM habits WHERE priorityLevel = :level ORDER BY RANDOM() LIMIT 1")
    fun getRandomHabitByPriorityLevel(level: String): LiveData<Habit>
}