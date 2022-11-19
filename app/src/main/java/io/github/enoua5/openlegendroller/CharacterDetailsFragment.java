package io.github.enoua5.openlegendroller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.TextViewCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import io.github.enoua5.openlegendroller.R;

import io.github.enoua5.openlegendroller.db.AppDatabase;
import io.github.enoua5.openlegendroller.db.Character;
import io.github.enoua5.openlegendroller.db.Character.Attribute;
import io.github.enoua5.openlegendroller.db.Character.AttributeInfo;

public class CharacterDetailsFragment extends DialogFragment {

    // TODO hook in the roll buttons to functionality
    // TODO for HeroMuster characters, perhaps have a "reimport" button

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
    
    View view;
    Toolbar toolbar;
    private TextView txtName, txtLevel, txtClass, txtVS, txtDT;
    Character character;
    int char_pk;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_character_details, container, false);

        toolbar = view.findViewById(R.id.characterDetailToolbar);
        txtName = view.findViewById(R.id.detailTxtName);
        txtLevel = view.findViewById(R.id.detailTxtLevel);
        txtClass = view.findViewById(R.id.detailTxtClass);
        txtVS = view.findViewById(R.id.detailTxtViciousStrike);
        txtDT = view.findViewById(R.id.detailTxtDestructiveTrance);

        ViewGroup attr_list = view.findViewById(R.id.attribute_list_box);
        int section = 0;
        for(AttributeInfo attr : stats)
        {
            if(section < Character.sectionStarts.length && attr.for_attr == Character.sectionStarts[section])
            {
                TextView head = new TextView(getContext());
                head.setText(Character.sectionHeadings[section]);
                TextViewCompat.setTextAppearance(head, androidx.appcompat.R.style.Base_TextAppearance_AppCompat_Subhead);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 100, 0, 20);
                head.setLayoutParams(params);

                attr_list.addView(head);

                section++;
            }

            View attr_view = inflater.inflate(R.layout.attribute_detail_item, null);

            TextView attr_name_box = attr_view.findViewById(R.id.attribute_name);
            attr_name_box.setText(attr.for_attr.name());

            attr.value_display = attr_view.findViewById(R.id.attribute_value);

            attr_list.addView(attr_view);
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        actionBar.setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);

        Bundle bundle = this.getArguments();

        if(bundle != null)
        {
            char_pk = bundle.getInt("char_pk");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    character = AppDatabase.getInstance(getContext())
                            .characterDAO()
                            .getById(char_pk)
                    ;

                    txtName.setText(character.name);
                    txtLevel.setText("Level "+character.level);
                    txtClass.setText(character.archetype+"");

                    for(AttributeInfo stat : stats)
                    {
                        stat.setValue(character.getAttr(stat.for_attr));
                    }
                    
                    
                    txtVS.setText((character.vicious_strike ? "Has" : "Does not have") + " Vicious Strike");
                    txtDT.setText((character.destructive_trance? "Has" : "Does not have") + " Destructive Trance");
                }
            }).start();
        }


        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.menu_character_details, menu);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case android.R.id.home:
                dismiss();
                break;
            case R.id.menu_delete:
                boolean deleteConfirm = false;

                new AlertDialog.Builder(getContext())
                        .setTitle("Delete Confirmation")
                        .setMessage("Are you sure you want to delete "+character.name+"?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AppDatabase.getInstance(getContext())
                                                   .characterDAO()
                                                   .delete(character)
                                        ;
                                        dismiss();
                                    }
                                }).start();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show()
                ;
                break;
            case R.id.menu_edit:
                Bundle bundle = new Bundle();
                bundle.putInt("char_pk", character.id);
                Log.d("CharEdit",character.id+"");

                EditCharacterFragment editCharacterFragment = new EditCharacterFragment();
                editCharacterFragment.setArguments(bundle);

                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .add(android.R.id.content, editCharacterFragment)
                        .addToBackStack(null)
                        .commit()
                ;
                dismiss();

                break;
        }


        return super.onOptionsItemSelected(item);
    }
}