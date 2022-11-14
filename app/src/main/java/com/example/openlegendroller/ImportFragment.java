package com.example.openlegendroller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.openlegendroller.db.AppDatabase;
import com.example.openlegendroller.db.Character;
import com.example.openlegendroller.db.CharacterDAO;

public class ImportFragment extends DialogFragment {

    View view;

    private GetHeroMusterCharacter task;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_import, container, false);

        Button submit_import_button = view.findViewById(R.id.btn_send_import);
        submit_import_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View button) {
                task = new GetHeroMusterCharacter();
                String char_id = ((EditText) view.findViewById(R.id.import_hero_id)).getText().toString();
                task.execute(char_id);

                task.setOnCharacterImportListener(new GetHeroMusterCharacter.OnCharacterImport() {
                    @Override
                    public void characterImportComplete(Character character) {
                        Log.d("GetCharacter", "Import complete");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                CharacterDAO dao = AppDatabase.getInstance(getActivity().getApplicationContext()).characterDAO();
                                dao.insert(character);
                            }
                        }).start();
                    }

                    @Override
                    public void characterImportFailed(String message) {
                        Log.d("GetCharacter", "Import failed");
                        new Thread() {
                            public void run() {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(getContext())
                                                .setTitle("Failed to import character")
                                                .setMessage(message)
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.cancel();
                                                    }
                                                })
                                                .show();
                                    }
                                });
                            }
                        }.start();
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Window window = getDialog().getWindow();
        if(window != null)
        {
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(window.getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);
        }
    }
}