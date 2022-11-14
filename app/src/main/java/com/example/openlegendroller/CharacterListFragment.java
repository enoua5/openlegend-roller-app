package com.example.openlegendroller;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.openlegendroller.db.Character;

import java.util.ArrayList;
import java.util.List;

public class CharacterListFragment extends Fragment {

    View view;
    private RecyclerView recyclerView;
    private CharacterRecyclerViewAdapter characterRecyclerViewAdapter;
    private int columnCount = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        Context context = getContext();

        characterRecyclerViewAdapter = new CharacterRecyclerViewAdapter(new ArrayList<Character>());

        if(columnCount <= 1)
        {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        }
        else
        {
            recyclerView.setLayoutManager(new GridLayoutManager(context, columnCount));
        }

        recyclerView.setAdapter(characterRecyclerViewAdapter);
        recyclerView.setHasFixedSize(false);

        ViewModelProviders.of(this)
                .get(AllCharacterViewModel.class)
                .getCharacterList(context)
                .observe(this, new Observer<List<Character>>() {
                    @Override
                    public void onChanged(List<Character> characters) {
                        if(characters != null)
                            characterRecyclerViewAdapter.addItems(characters);
                    }
                });
    }
}