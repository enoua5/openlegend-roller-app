package io.github.enoua5.openlegendroller;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import io.github.enoua5.openlegendroller.db.Character;

import java.util.List;

public class CharacterRecyclerViewAdapter extends RecyclerView.Adapter<CharacterRecyclerViewAdapter.ViewHolder> {

    public final List<Character> characterList;

    public CharacterRecyclerViewAdapter(List<Character> characterList)
    {
        this.characterList = characterList;
    }

    public void addItems(List<Character> characters)
    {
        this.characterList.clear();
        this.characterList.addAll(characters);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public View root;
        public Character character;

        public TextView characterNameBox, characterLevelBox, characterClassBox;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            root = itemView;

            characterNameBox = root.findViewById(R.id.riTxtCharacterName);
            characterLevelBox = root.findViewById(R.id.riTxtCharacterLevel);
            characterClassBox = root.findViewById(R.id.riTxtCharacterClass);
        }
    }


    @NonNull
    @Override
    public CharacterRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_item, parent, false)
                ;

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CharacterRecyclerViewAdapter.ViewHolder holder, int position) {
        final Character character = characterList.get(position);
        if(character != null)
        {
            holder.characterNameBox.setText(character.name);
            holder.characterLevelBox.setText("Level "+character.level);
            holder.characterClassBox.setText(character.archetype);

            holder.root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putInt("char_pk", character.id);
                    CharacterDetailsFragment characterDetailsFragment = new CharacterDetailsFragment();
                    characterDetailsFragment.setArguments(bundle);

                    AppCompatActivity activity = (AppCompatActivity) v.getContext();
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .add(android.R.id.content, characterDetailsFragment)
                            .addToBackStack(null)
                            .commit()
                    ;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return characterList.size();
    }
}