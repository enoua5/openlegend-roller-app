package io.github.enoua5.openlegendroller;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.media.AudioAttributesCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;

import io.github.enoua5.openlegendroller.db.AppDatabase;
import io.github.enoua5.openlegendroller.db.Character;

public class EditCharacterFragment extends DialogFragment {

    View view;
    Toolbar toolbar;
    Character character;
    boolean already_exists;

    private enum Attribute
    {
        Agility, Fortitude, Might, Learning, Logic, Perception,
        Will, Deception, Persuasion, Presence, Alteration, Creation,
        Energy, Entropy, Influence, Movement, Prescience, Protection
    }

    Attribute[] sectionStarts = {Attribute.Agility, Attribute.Learning, Attribute.Deception, Attribute.Alteration};
    String[] sectionHeadings = {"Physical", "Mental", "Social", "Extraordinary"};

    private class AttributeInfo
    {
        TextView value_display;
        int value;
        Attribute for_attr;

        AttributeInfo(Attribute for_attr)
        {
            this.for_attr = for_attr;
            this.value = 0;
        }

        void increment()
        {
            if(value < 10)
                value ++;

            value_display.setText(String.valueOf(value));
        }

        void decrement()
        {
            if(value > 0)
                value --;

            value_display.setText(String.valueOf(value));
        }

        void setValue(int value)
        {
            this.value = value;
            value_display.setText(String.valueOf(value));
        }
    }

    final AttributeInfo[] stats = {
            new AttributeInfo(Attribute.Agility),
            new AttributeInfo(Attribute.Fortitude),
            new AttributeInfo(Attribute.Might),
            new AttributeInfo(Attribute.Learning),
            new AttributeInfo(Attribute.Logic),
            new AttributeInfo(Attribute.Perception),
            new AttributeInfo(Attribute.Will),
            new AttributeInfo(Attribute.Deception),
            new AttributeInfo(Attribute.Persuasion),
            new AttributeInfo(Attribute.Presence),
            new AttributeInfo(Attribute.Alteration),
            new AttributeInfo(Attribute.Creation),
            new AttributeInfo(Attribute.Energy),
            new AttributeInfo(Attribute.Entropy),
            new AttributeInfo(Attribute.Influence),
            new AttributeInfo(Attribute.Movement),
            new AttributeInfo(Attribute.Prescience),
            new AttributeInfo(Attribute.Protection)
    };

    private TextInputEditText txtName, txtClass, txtLevel;
    private CheckBox checkHasVS, checkHasDT;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_edit_character, container, false);

        toolbar = view.findViewById(R.id.editCharToolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        actionBar.setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);

        txtName = view.findViewById(R.id.newCharacterName);
        txtClass = view.findViewById(R.id.newCharacterClass);
        txtLevel = view.findViewById(R.id.newCharacterLevel);
        checkHasDT = view.findViewById(R.id.has_dt);
        checkHasVS = view.findViewById(R.id.has_vs);

        ViewGroup attr_list = view.findViewById(R.id.attribute_list_box);
        int section = 0;
        for(AttributeInfo attr : stats)
        {
            if(section < sectionStarts.length && attr.for_attr == sectionStarts[section])
            {
                TextView head = new TextView(getContext());
                head.setText(sectionHeadings[section]);
                TextViewCompat.setTextAppearance(head, androidx.appcompat.R.style.Base_TextAppearance_AppCompat_Subhead);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 100, 0, 20);
                head.setLayoutParams(params);

                attr_list.addView(head);

                section++;
            }

            View attr_view = inflater.inflate(R.layout.edit_attribute, null);

            TextView attr_name_box = attr_view.findViewById(R.id.attribute_name);
            attr_name_box.setText(attr.for_attr.name());

            attr.value_display = attr_view.findViewById(R.id.attribute_value);

            Button btn_inc = attr_view.findViewById(R.id.attribute_increment);
            btn_inc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    attr.increment();
                }
            });

            Button btn_dec = attr_view.findViewById(R.id.attribute_decrement);
            btn_dec.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    attr.decrement();
                }
            });

            attr_list.addView(attr_view);
        }

        Bundle bundle = this.getArguments();

        if(bundle == null)
        {
            toolbar.setTitle("New character");
            already_exists = false;
        }
        else
        {
            toolbar.setTitle("Edit character");
            already_exists = true;

            int char_pk = bundle.getInt("char_pk");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    character = AppDatabase.getInstance(getContext())
                            .characterDAO()
                            .getById(char_pk)
                    ;

                    getActivity().runOnUiThread(() ->{
                        txtName.setText(character.name);
                        txtClass.setText(character.archetype);
                        txtLevel.setText(String.valueOf(character.level));
                        checkHasDT.setChecked(character.destructive_trance);
                        checkHasVS.setChecked(character.vicious_strike);

                        for(AttributeInfo stat : stats)
                        {
                            stat.setValue(getAttrOnChar(character, stat.for_attr));
                        }
                    });
                }
            }).start();
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.menu_edit_character, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                dismiss();
                break;
            case R.id.menu_save:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Character newCharacter = new Character();

                        newCharacter.name = txtName.getText().toString();
                        newCharacter.archetype = txtClass.getText().toString();
                        newCharacter.level = Integer.parseInt(txtLevel.getText().toString());
                        newCharacter.destructive_trance = checkHasDT.isChecked();
                        newCharacter.vicious_strike = checkHasVS.isChecked();

                        for(AttributeInfo stat : stats)
                        {
                            setAttrOnChar(newCharacter, stat.for_attr, stat.value);
                        }

                        if(already_exists)
                        {
                            newCharacter.id = character.id;
                            newCharacter.heromuster_id = character.heromuster_id;

                            AppDatabase.getInstance(getContext())
                                    .characterDAO()
                                    .update(newCharacter)
                            ;
                        }
                        else
                        {
                            AppDatabase.getInstance(getContext())
                                    .characterDAO()
                                    .insert(newCharacter)
                            ;
                        }
                    }
                }).start();
                dismiss();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private int getAttrOnChar(Character character, Attribute attr)
    {
        switch (attr)
        {
            case Agility:
                return character.agility;
            case Fortitude:
                return character.fortitude;
            case Might:
                return character.might;
            case Learning:
                return character.learning;
            case Logic:
                return character.logic;
            case Perception:
                return character.perception;
            case Will:
                return character.will;
            case Deception:
                return character.deception;
            case Persuasion:
                return character.persuasion;
            case Presence:
                return character.presence;
            case Alteration:
                return character.alteration;
            case Creation:
                return character.creation;
            case Energy:
                return character.energy;
            case Entropy:
                return character.entropy;
            case Influence:
                return character.influence;
            case Movement:
                return character.movement;
            case Prescience:
                return character.prescience;
            case Protection:
                return character.protection;
        }
        return 0;
    }

    private void setAttrOnChar(Character character, Attribute attr, int value)
    {
        switch (attr)
        {
            case Agility:
                character.agility = value;
                break;
            case Fortitude:
                character.fortitude = value;
                break;
            case Might:
                character.might = value;
                break;
            case Learning:
                character.learning = value;
                break;
            case Logic:
                character.logic = value;
                break;
            case Perception:
                character.perception = value;
                break;
            case Will:
                character.will = value;
                break;
            case Deception:
                character.deception = value;
                break;
            case Persuasion:
                character.persuasion = value;
                break;
            case Presence:
                character.presence = value;
                break;
            case Alteration:
                character.alteration = value;
                break;
            case Creation:
                character.creation = value;
                break;
            case Energy:
                character.energy = value;
                break;
            case Entropy:
                character.entropy = value;
                break;
            case Influence:
                character.influence = value;
                break;
            case Movement:
                character.movement = value;
                break;
            case Prescience:
                character.prescience = value;
                break;
            case Protection:
                character.protection = value;
                break;
        }
    }
}