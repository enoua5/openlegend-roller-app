package com.example.openlegendroller.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CharacterDAO {
    @Query("select * from Character")
    LiveData<List<Character>> getAll();

    @Query("select * from Character where id = :id")
    Character getById(int id);

    @Update
    void update(Character character);

    @Delete
    void delete(Character character);

    @Insert
    void insert(Character... characters);
}
