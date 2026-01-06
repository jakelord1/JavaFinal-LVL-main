package com.lvl.homecookai;

import android.content.Context;
import android.content.SharedPreferences;

public class PromptManager {
    private static final String PREFS_NAME = "prompt_prefs";
    private static final String PROMPT_KEY = "gemini_prompt";
    
    private final SharedPreferences sharedPreferences;

    public PromptManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public String getDefaultPrompt() {
        return sharedPreferences.getString(PROMPT_KEY,
                "Analyze this image and identify ALL the ingredients you can see. " +
                "Return ONLY a valid JSON object (no markdown, no extra text) with this exact structure:\n" +
                "{\n" +
                "  \"ingredients\": [\"ingredient1\", \"ingredient2\", \"ingredient3\", ...]\n" +
                "}\n" +
                "List each ingredient once, use common names (e.g., 'tomato' instead of 'ripe red tomato'). " +
                "If no ingredients found, return: {\"ingredients\": []}");

//                "Analyze this image and identify ALL ingredients you can see. " +
//                        "You have this list of known ingredients from the database:  \n" +
//                        "{ \"ingredients\": [ { \"id\": 1, \"name\": \"Tomato\" }, { \"id\": 2, \"name\": \"Onion\" }, ... ] }  \n" +
//                        "\n" +
//                        "Return ONLY a valid JSON object (no markdown, no extra text) with this exact structure:\n" +
//                        "[\n" +
//                        "  {\n" +
//                        "    \"ingredientId\": <id from database>,\n" +
//                        "    \"amount\": <number>,\n" +
//                        "    \"unit\": \"<g|pcs|ml>\",\n" +
//                        "    \"notes\": \"<optional notes>\"\n" +
//                        "  },\n" +
//                        "  ...\n" +
//                        "]\n" +
//                        "\n" +
//                        "Match detected ingredients to the provided database IDs.  \n" +
//                        "If no ingredients found, return: []"
    }

    public void savePrompt(String prompt) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PROMPT_KEY, prompt);
        editor.apply();
    }

    public void resetToDefault() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(PROMPT_KEY);
        editor.apply();
    }
}
