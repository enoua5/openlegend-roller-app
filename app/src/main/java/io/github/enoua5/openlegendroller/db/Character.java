package io.github.enoua5.openlegendroller.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Character {
    // Meta
    @PrimaryKey(autoGenerate = true)
    public int id;
    public boolean edited_in_app = true;
    public String heromuster_id;

    // Character info
    public String name;
    public String archetype;
    public int level = 1;

    // Stats
    public int agility = 0;
    public int fortitude = 0;
    public int might = 0;
    public int learning = 0;
    public int logic = 0;
    public int perception = 0;
    public int will = 0;
    public int deception = 0;
    public int persuasion = 0;
    public int presence = 0;
    public int alteration = 0;
    public int creation = 0;
    public int energy = 0;
    public int entropy = 0;
    public int influence = 0;
    public int movement = 0;
    public int prescience = 0;
    public int protection = 0;

    // Feats relevant to rolls
    public boolean destructive_trance = false;
    public boolean vicious_strike = false;
}
