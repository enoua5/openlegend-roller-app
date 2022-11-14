package com.example.openlegendroller;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.openlegendroller.db.AppDatabase;
import com.example.openlegendroller.db.Character;

import java.util.List;

public class AllCharacterViewModel extends ViewModel {

    private LiveData<List<Character>> characterList;

    public LiveData<List<Character>> getCharacterList(Context ctx)
    {
        if(characterList != null)
            return characterList;
        return characterList = AppDatabase.getInstance(ctx).characterDAO().getAll();
    }
}
