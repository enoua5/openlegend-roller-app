package io.github.enoua5.openlegendroller;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.internal.VisibilityAwareImageButton;

import java.util.ArrayList;
import java.util.Collections;


public class DiceRollFragment extends DialogFragment {

    View view;
    Toolbar toolbar;

    int score = 0;
    int advantage = 0;
    String[] scores = new String[]{"0", "1 (1d4)", "2 (1d6)", "3 (1d8)", "4 (1d10)", "5 (2d6)", "6 (2d8)", "7 (2d10)", "8 (3d8)", "9 (3d10)", "10 (4d8)"};
    String[] advantages = new String[]{"-9","-8","-7","-6","-5","-4","-3","-2","-1","0","+1","+2","+3","+4","+5","+6","+7","+8","+9"};


    ViewGroup dice_box;
    Spinner score_dropdown, advantage_dropdown;
    TextView title, roll_total;
    CheckBox vs_check, dt_check, ah_check;
    Button roll_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_dice_roll, container, false);


        toolbar = view.findViewById(R.id.diceRollToolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        actionBar.setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);

        score_dropdown = view.findViewById(R.id.score_dropdown);
        ArrayAdapter<String> score_adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, scores);
        score_dropdown.setAdapter(score_adapter);
        advantage_dropdown = view.findViewById(R.id.advantage_dropdown);
        ArrayAdapter<String> advantage_adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, advantages);
        advantage_dropdown.setAdapter(advantage_adapter);
        // selection 9 is "0"
        advantage_dropdown.setSelection(9);

        score_dropdown.setOnItemSelectedListener(score_changed_listener);
        advantage_dropdown.setOnItemSelectedListener(advantage_changed_listener);

        vs_check = view.findViewById(R.id.use_vs);
        dt_check = view.findViewById(R.id.use_dt);
        ah_check = view.findViewById(R.id.is_ad_hoc);

        ah_check.setChecked(false);

        title = view.findViewById(R.id.roll_name);
        roll_total = view.findViewById(R.id.roll_total);

        roll_button = view.findViewById(R.id.roll_btn);
        roll_button.setOnClickListener(roll_listener);

        dice_box = view.findViewById(R.id.dice_box);


        Bundle bundle = this.getArguments();

        if(bundle != null)
        {
            score = bundle.getInt("attr_score", 0);
            boolean vicious_strike = bundle.getBoolean("has_vs", false);
            vs_check.setChecked(vicious_strike);
            boolean destructive_trance = bundle.getBoolean("has_dt", false);
            dt_check.setChecked(destructive_trance);

            title.setText("Roll " + bundle.getString("attr_name", ""));

            score_dropdown.setSelection(score);
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        getActivity().getMenuInflater().inflate(R.menu.menu_roll, menu);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().getSupportFragmentManager().popBackStack();
                dismiss();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    AdapterView.OnItemSelectedListener score_changed_listener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            score = i;
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    AdapterView.OnItemSelectedListener advantage_changed_listener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            // index 0 is score -9
            advantage = i - 9;
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    View.OnClickListener roll_listener = new View.OnClickListener() {
        @Override
        public void onClick(View button) {
            Log.d("rolling", "Score "+score+", adv: "+advantage);

            ArrayList<Die> dice = Die.ol_roll(score, advantage, dt_check.isChecked(), vs_check.isChecked(), ah_check.isChecked());
            Collections.sort(dice);
            int total = Die.total_of_dice(dice);

            roll_total.setText(String.valueOf(total));

            dice_box.removeAllViews();
            int generation = -1;

            LinearLayout row = null;
            for(Die die : dice)
            {
                if(die.generation != generation)
                {
                    generation = die.generation;

                    HorizontalScrollView sv = new HorizontalScrollView(getContext());

                    row = new LinearLayout(getContext());
                    row.setOrientation(LinearLayout.HORIZONTAL);
                    sv.setLayoutParams(new HorizontalScrollView.LayoutParams(HorizontalScrollView.LayoutParams.MATCH_PARENT, HorizontalScrollView.LayoutParams.WRAP_CONTENT));

                    sv.addView(row);
                    dice_box.addView(sv);
                }

                View die_view = getLayoutInflater().inflate(R.layout.die_display, null);

                TextView value_box = die_view.findViewById(R.id.die_value);
                value_box.setText(String.valueOf(die.value));


                ImageView die_img = die_view.findViewById(R.id.die_image);

                switch(die.max)
                {
                    case 4:
                        die_img.setImageResource(R.drawable.ic_d4);
                        break;
                    case 6:
                        die_img.setImageResource(R.drawable.ic_d6);
                        break;
                    case 8:
                        die_img.setImageResource(R.drawable.ic_d8);
                        break;
                    case 10:
                        die_img.setImageResource(R.drawable.ic_d10);
                        break;
                    case 20:
                        die_img.setImageResource(R.drawable.ic_d20);
                        break;
                }

                if(die.dropped)
                    die_img.setAlpha(0.5f);
                else if(die.crit)
                    value_box.setTextColor(getResources().getColor(R.color.crit));

                row.addView(die_view);
            }
        }
    };
}