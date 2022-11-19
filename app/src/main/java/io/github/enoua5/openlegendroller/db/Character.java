package io.github.enoua5.openlegendroller.db;

import android.widget.TextView;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Character {

    public enum Attribute
    {
        Agility, Fortitude, Might, Learning, Logic, Perception,
        Will, Deception, Persuasion, Presence, Alteration, Creation,
        Energy, Entropy, Influence, Movement, Prescience, Protection
    }

    public static final Attribute[] sectionStarts = {Attribute.Agility, Attribute.Learning, Attribute.Deception, Attribute.Alteration};
    public static final String[] sectionHeadings = {"Physical", "Mental", "Social", "Extraordinary"};

    public static class AttributeInfo
    {
        public TextView value_display;
        public int value;
        public Attribute for_attr;

        public AttributeInfo(Attribute for_attr)
        {
            this.for_attr = for_attr;
            this.value = 0;
        }

        public void increment()
        {
            if(value < 10)
                value ++;

            value_display.setText(String.valueOf(value));
        }

        public void decrement()
        {
            if(value > 0)
                value --;

            value_display.setText(String.valueOf(value));
        }

        public void setValue(int value)
        {
            this.value = value;
            value_display.setText(String.valueOf(value));
        }
    }


    // Meta
    @PrimaryKey(autoGenerate = true)
    public int id;
    public boolean edited_in_app = true;
    public String heromuster_id = null;
    public long last_updated;

    public Character()
    {
        last_updated = System.currentTimeMillis();
    }

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

    public int getAttr(Attribute attr)
    {
        switch (attr)
        {
            case Agility:
                return this.agility;
            case Fortitude:
                return this.fortitude;
            case Might:
                return this.might;
            case Learning:
                return this.learning;
            case Logic:
                return this.logic;
            case Perception:
                return this.perception;
            case Will:
                return this.will;
            case Deception:
                return this.deception;
            case Persuasion:
                return this.persuasion;
            case Presence:
                return this.presence;
            case Alteration:
                return this.alteration;
            case Creation:
                return this.creation;
            case Energy:
                return this.energy;
            case Entropy:
                return this.entropy;
            case Influence:
                return this.influence;
            case Movement:
                return this.movement;
            case Prescience:
                return this.prescience;
            case Protection:
                return this.protection;
        }
        return 0;
    }

    public void setAttr(Attribute attr, int value)
    {
        switch (attr)
        {
            case Agility:
                this.agility = value;
                break;
            case Fortitude:
                this.fortitude = value;
                break;
            case Might:
                this.might = value;
                break;
            case Learning:
                this.learning = value;
                break;
            case Logic:
                this.logic = value;
                break;
            case Perception:
                this.perception = value;
                break;
            case Will:
                this.will = value;
                break;
            case Deception:
                this.deception = value;
                break;
            case Persuasion:
                this.persuasion = value;
                break;
            case Presence:
                this.presence = value;
                break;
            case Alteration:
                this.alteration = value;
                break;
            case Creation:
                this.creation = value;
                break;
            case Energy:
                this.energy = value;
                break;
            case Entropy:
                this.entropy = value;
                break;
            case Influence:
                this.influence = value;
                break;
            case Movement:
                this.movement = value;
                break;
            case Prescience:
                this.prescience = value;
                break;
            case Protection:
                this.protection = value;
                break;
        }
    }
}
