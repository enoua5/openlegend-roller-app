package io.github.enoua5.openlegendroller;

import android.os.AsyncTask;
import android.util.Log;

import io.github.enoua5.openlegendroller.db.Character;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class GetHeroMusterCharacter extends AsyncTask<String, Integer, String> {

    // TODO check if an unedited version of the character already exists
    // If so, prompt the user asking if they want to save another copy, overwrite theirs, or cancel

    private String rawJSON;
    private String char_id;
    private  OnCharacterImport emitter;

    public interface OnCharacterImport
    {
        void characterImportComplete(Character character);
        void characterImportFailed(String message);
    }

    public void setOnCharacterImportListener(OnCharacterImport listener)
    {
        emitter = listener;
    }

    private int safe_get(Map character_map, String key, int def)
    {
        try
        {
            return Integer.parseInt((String) character_map.get(key));
        }
        catch (Exception e)
        {
            return def;
        }
    }
    
    @Override
    protected String doInBackground(String... strings) {
        try
        {
            char_id = strings[0];
            URL url = new URL("https://openlegend.heromuster.com/api/character/"+char_id);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            int status = connection.getResponseCode();

            if(status != 200)
                // normally I would worry about showing this to an end user,
                // but HeroMuster returns 200 even on a failure, so this should be rare
                throw new Exception("HeroMuster API returned response code: "+status);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            rawJSON = bufferedReader.readLine();
            Log.d("GetCharacter", "Got JSON: "+rawJSON);

        }
        catch(Exception e)
        {
            emitter.characterImportFailed(e.getMessage());
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        Character character;

        character = parseJson();
        if(character != null)
            emitter.characterImportComplete(character);

        super.onPostExecute(s);
    }

    private Character parseJson()
    {
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        Character character = null;

        try
        {
            Map data = gson.fromJson(rawJSON, Map.class);
            String status = (String)((Map)data.get("meta")).get("status");
            if(!status.equals("success"))
            {
                emitter.characterImportFailed((String)data.get("error"));
                return null;
            }

            Map character_map = (Map)((Map)data.get("success")).get("character");

            character = new Character();
            character.edited_in_app = false;
            character.heromuster_id = char_id;

            character.name = (String) character_map.get("charactername");
            character.archetype = (String) character_map.get("archetype");
            character.level = safe_get(character_map, "level", 1);

            character.agility = safe_get(character_map, "agility",0);
            character.fortitude = safe_get(character_map, "fortitude", 0);
            character.might = safe_get(character_map, "might", 0);
            character.learning = safe_get(character_map, "learning", 0);
            character.logic = safe_get(character_map, "logic", 0);
            character.perception = safe_get(character_map, "perception", 0);
            character.will = safe_get(character_map, "will", 0);
            character.deception = safe_get(character_map, "deception", 0);
            character.persuasion = safe_get(character_map, "persuasion", 0);
            character.presence = safe_get(character_map, "presence", 0);
            character.alteration = safe_get(character_map, "alteration", 0);
            character.creation = safe_get(character_map, "creation", 0);
            character.energy = safe_get(character_map, "energy", 0);
            character.entropy = safe_get(character_map, "entropy", 0);
            character.influence = safe_get(character_map, "influence", 0);
            character.movement = safe_get(character_map, "movement", 0);
            character.prescience = safe_get(character_map, "prescience", 0);
            character.protection = safe_get(character_map, "protection", 0);

            for(int i = 1; i <= 8; i++)
            {
                String feat = (String) character_map.get("feat"+i);
                if(feat.equals("Destructive Trance"))
                    character.destructive_trance = true;
                else if(feat.equals("Vicious Strike"))
                    character.vicious_strike = true;
            }

            return character;
        }
        catch (Exception e)
        {
            Log.e("GetCharacter", e.getMessage());
            emitter.characterImportFailed("Couldn't understand the data HeroMuster gave us.");
            return null;
        }
    }
}
