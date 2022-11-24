package io.github.enoua5.openlegendroller;

import android.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Die implements Comparable<Die> {

    public int value;
    public int max;
    public boolean crit;
    public boolean dropped = false;
    public int generation = 0;

    private Random rng = new Random();

    public Die(int max, boolean destructive_trance)
    {
        this.max = max;
        this.roll(destructive_trance);
    }

    public int roll(boolean destructive_trance)
    {
        value = rng.nextInt(max) + 1;

        if((value == max) || (destructive_trance && value - 1 == max))
            crit = true;

        return value;
    }

    @Override
    public int compareTo(Die other) {
        if(generation != other.generation)
            return generation - other.generation;
        if(max != other.max)
            return other.max - max;
        if(dropped != other.dropped)
            return dropped ? 1 : -1;
        return value - other.value;
    }

    public static ArrayList<Die> roll_nd(int n, int d, boolean dt, int generation) {
        ArrayList<Die> dice = new ArrayList<>();

        for(int i = 0; i < n; i++)
        {
            Die roll = new Die(d, dt);
            roll.generation = generation;
            dice.add(roll);
        }

        return dice;
    }

    public static ArrayList<Die> roll_nd_adv(int n, int d, int adv, boolean dt)
    {
        ArrayList<Die> dice = roll_nd(n+Math.abs(adv), d, dt, 0);
        Collections.sort(dice);

        if(adv > 0)
        {
            for(int i = 0; i < adv; i++)
                dice.get(i).dropped = true;
        }
        else
        {
            for(int i = 0; i < -adv; i++)
                dice.get(dice.size() - 1 - i).dropped = true;

        }

        return dice;
    }

    public static ArrayList<Die> explode_dice(ArrayList<Die> dice, boolean vs, boolean dt, int generation)
    {
        ArrayList<Die> addition_rolls = new ArrayList<>();

        for(Die die : dice)
        {
            if(die.crit && !die.dropped)
            {
                Die newDie = new Die(die.max, dt);
                if(die.max == 20 && vs)
                {
                    // vicious strike gives advantage to exploding d20s
                    Die advDie = new Die(die.max, dt);
                    if(advDie.value > newDie.value)
                        newDie = advDie;
                }

                newDie.generation = generation;
                addition_rolls.add(newDie);
            }
        }

        return addition_rolls;
    }

    public static ArrayList<Die> explode_all_dice(ArrayList<Die> dice, boolean vs, boolean dt)
    {
        ArrayList<Die> all_rolls = new ArrayList<>(dice);
        ArrayList<Die> left_to_explode = new ArrayList<>(dice);

        int generation = 1;

        while(!left_to_explode.isEmpty())
        {
            left_to_explode = explode_dice(left_to_explode, vs, dt, generation++);
            all_rolls.addAll(left_to_explode);
        }

        return all_rolls;
    }

    public static int total_of_dice(ArrayList<Die> dice)
    {
        int sum = 0;
        for(Die die : dice)
        {
            if(!die.dropped)
                sum += die.value;
        }
        return sum;
    }

    public static final int[][] ATTRIBUTE_DICE = {
        {1, 20},
        {1, 4},
        {1, 6},
        {1, 8},
        {1, 10},
        {2, 6},
        {2, 8},
        {2, 10},
        {3, 8},
        {3, 10},
        {4, 8}
    };

    public static ArrayList<Die> ol_roll(int attr, int adv, boolean dt, boolean vs, boolean ad_hoc)
    {
        if(attr > 0)
        {
            int[] attr_dice = ATTRIBUTE_DICE[attr];
            int n = attr_dice[0];
            int d = attr_dice[1];

            ArrayList<Die> attr_roll = explode_all_dice(roll_nd_adv(n, d, adv, dt), vs, dt);
            if(!ad_hoc)
            {
                ArrayList<Die> d20_roll = explode_all_dice(roll_nd_adv(1, 20, adv, dt), vs, dt);
                d20_roll.addAll(attr_roll);
                return d20_roll;
            }
            return attr_roll;
        }
        else
            return explode_all_dice(roll_nd_adv(1, 20, (int)Math.signum(adv), dt), vs, dt);
    }
}
