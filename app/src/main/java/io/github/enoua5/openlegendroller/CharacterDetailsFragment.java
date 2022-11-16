package io.github.enoua5.openlegendroller;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import io.github.enoua5.openlegendroller.R;

import io.github.enoua5.openlegendroller.db.AppDatabase;
import io.github.enoua5.openlegendroller.db.Character;

public class CharacterDetailsFragment extends DialogFragment {

    View view;
    Toolbar toolbar;
    private TextView txtName, txtLevel, txtClass, txtAgility, txtFortitude, txtMight, txtLearning,
                     txtLogic, txtPerception, txtWill, txtDeception, txtPersuasion, txtPresence,
                     txtAlteration, txtCreation, txtEnergy, txtEntropy, txtInfluence, txtMovement,
                     txtPrescience, txtProtection, txtVS, txtDT;
    Character character;
    int char_pk;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_character_details, container, false);

        toolbar = view.findViewById(R.id.characterDetailToolbar);
        txtName = view.findViewById(R.id.detailTxtName);
        txtLevel = view.findViewById(R.id.detailTxtLevel);
        txtClass = view.findViewById(R.id.detailTxtClass);
        txtAgility = view.findViewById(R.id.detailTxtAgility);
        txtFortitude = view.findViewById(R.id.detailTxtFortitude);
        txtMight = view.findViewById(R.id.detailTxtMight);
        txtLearning = view.findViewById(R.id.detailTxtLearning);
        txtLogic = view.findViewById(R.id.detailTxtLogic);
        txtPerception = view.findViewById(R.id.detailTxtPerception);
        txtWill = view.findViewById(R.id.detailTxtWill);
        txtDeception = view.findViewById(R.id.detailTxtDeception);
        txtPersuasion = view.findViewById(R.id.detailTxtPersuasion);
        txtPresence = view.findViewById(R.id.detailTxtPresence);
        txtAlteration = view.findViewById(R.id.detailTxtAlteration);
        txtCreation = view.findViewById(R.id.detailTxtCreation);
        txtEnergy = view.findViewById(R.id.detailTxtEnergy);
        txtEntropy = view.findViewById(R.id.detailTxtEntropy);
        txtInfluence = view.findViewById(R.id.detailTxtInfluence);
        txtMovement = view.findViewById(R.id.detailTxtMovement);
        txtPrescience = view.findViewById(R.id.detailTxtPrescience);
        txtProtection = view.findViewById(R.id.detailTxtProtection);
        txtVS = view.findViewById(R.id.detailTxtViciousStrike);
        txtDT = view.findViewById(R.id.detailTxtDestructiveTrance);

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
                    txtAgility.setText(character.agility+"");
                    txtFortitude.setText(character.fortitude+"");
                    txtMight.setText(character.might+"");
                    txtLearning.setText(character.learning+"");
                    txtLogic.setText(character.logic+"");
                    txtPerception.setText(character.perception+"");
                    txtWill.setText(character.will+"");
                    txtDeception.setText(character.deception+"");
                    txtPersuasion.setText(character.perception+"");
                    txtPresence.setText(character.presence+"");
                    txtAlteration.setText(character.alteration+"");
                    txtCreation.setText(character.creation+"");
                    txtEnergy.setText(character.energy+"");
                    txtEntropy.setText(character.entropy+"");
                    txtInfluence.setText(character.influence+"");
                    txtMovement.setText(character.movement+"");
                    txtPrescience.setText(character.prescience+"");
                    txtProtection.setText(character.protection+"");
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
                // TODO
                break;
        }


        return super.onOptionsItemSelected(item);
    }
}