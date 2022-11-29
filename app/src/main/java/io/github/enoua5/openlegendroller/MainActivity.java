package io.github.enoua5.openlegendroller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.ui.AppBarConfiguration;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    FragmentManager fm;

    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fm = getSupportFragmentManager();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button import_button = findViewById(R.id.btn_import_char);
        import_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImportFragment dialog = new ImportFragment();
                dialog.show(fm, null);
            }
        });

        Button add_button = findViewById(R.id.btn_add_char);
        add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fm.beginTransaction()
                        .add(android.R.id.content, new EditCharacterFragment())
                        .addToBackStack(null)
                        .commit()
                ;
            }
        });

        Button roll_button = findViewById(R.id.roll_btn);
        roll_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();

                DiceRollFragment diceRollFragment = new DiceRollFragment();
                diceRollFragment.setArguments(bundle);

                fm.beginTransaction()
                        .add(android.R.id.content, diceRollFragment)
                        .addToBackStack(null)
                        .commit()
                ;
            }
        });
    }
}