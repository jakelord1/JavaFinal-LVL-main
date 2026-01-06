package com.lvl.homecookai;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.SettableFuture;
import com.google.gson.Gson;
import com.lvl.homecookai.ApiSetup.ApiAccess;
import com.lvl.homecookai.ApiSetup.MethodsToApi;
import com.lvl.homecookai.database.Ingredient;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GeminiService {

    private static final Executor executor = Executors.newSingleThreadExecutor();
    private static boolean isApiKeyMissing(String key) {
        return key == null || key.trim().isEmpty();
    }

    public ListenableFuture<String> generateContent(String textPrompt) {
        if (isApiKeyMissing(BuildConfig.GEMINI_API_KEY)) {
            return Futures.immediateFailedFuture(
                    new IllegalStateException("Missing Gemini API key. Add GEMINI_API_KEY to local.properties and rebuild."));
        }

        GenerativeModel gm = new GenerativeModel(
                "gemini-2.5-flash-lite",
                BuildConfig.GEMINI_API_KEY
        );
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content content = new Content.Builder()
                .addText(textPrompt)
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

        return Futures.transform(response, it -> {
            if (it == null) {
                return "Error: Empty response.";
            }
            String text = it.getText();
            if (text == null) {
                return "Error: No text found in response.";
            }
            return text;
        }, Executors.newSingleThreadExecutor());
    }


    public ListenableFuture<String> generateContentWithImage(Bitmap bitmap, String textPrompt) {
        try {
            SettableFuture<String> finalFuture = SettableFuture.create();
            MethodsToApi api = ApiAccess.getClient().create(MethodsToApi.class);

            ListenableFuture<String> dictFuture = loadIngredientsJson(api);

            Futures.addCallback(dictFuture, new FutureCallback<String>() {
                @Override
                public void onSuccess(String dictJson) {

                    String finalPrompt = dictJson + "\n\n" + textPrompt;

                    if (isApiKeyMissing(BuildConfig.GEMINI_API_KEY)) {
                        finalFuture.setException(new IllegalStateException(
                                "Missing Gemini API key. Add GEMINI_API_KEY to local.properties and rebuild."));
                        return;
                    }

                    GenerativeModel gm = new GenerativeModel(
                            "gemini-2.5-flash",
                            BuildConfig.GEMINI_API_KEY
                    );
                    GenerativeModelFutures model = GenerativeModelFutures.from(gm);

                    Content content = new Content.Builder()
                            .addImage(bitmap)
                            .addText(finalPrompt)
                            .build();

                    SettableFuture<String> geminiFuture = SettableFuture.create();
                    ListenableFuture<GenerateContentResponse> responseFuture = model.generateContent(content);

                    Futures.addCallback(responseFuture, new FutureCallback<GenerateContentResponse>() {
                        @Override
                        public void onSuccess(GenerateContentResponse result) {
                            if (result != null && result.getText() != null) {
                                geminiFuture.set(result.getText());
                            } else {
                                geminiFuture.set("Error: No text found in response.");
                            }
                            finalFuture.setFuture(geminiFuture);
                        }

                        @Override
                        public void onFailure(Throwable t) {
                            finalFuture.setException(t);
                        }
                    }, executor);
                }

                @Override
                public void onFailure(Throwable t) {
                    finalFuture.setException(t);
                }
            }, executor);

            return finalFuture;

        } catch (Exception e) {
            return Futures.immediateFailedFuture(e);
        }
    }
    private ListenableFuture<String> loadIngredientsJson(MethodsToApi api) {
        SettableFuture<String> future = SettableFuture.create();

        api.getAllIngredients().enqueue(new Callback<List<Ingredient>>() {
            @Override
            public void onResponse(Call<List<Ingredient>> call, Response<List<Ingredient>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Ingredient> ingredients = response.body();
                    Map<Integer, String> compressedDict = new LinkedHashMap<>();
                    for (Ingredient ing : ingredients) {
                        compressedDict.put(ing.getId(), ing.getName());
                    }
                    Map<String, Object> wrapper = new HashMap<>();
                    wrapper.put("ingredients", compressedDict);
                    String ingredientsJson = new Gson().toJson(wrapper);
                    future.set(ingredientsJson);
                } else {
                    future.setException(new IllegalStateException("Failed to load ingredients"));
                }
            }

            @Override
            public void onFailure(Call<List<Ingredient>> call, Throwable t) {
                future.setException(t);
            }
        });

        return future;
    }




//                    api.getAllIngredients().enqueue(new Callback<List<Ingredient>>() {
//                @Override
//                public void onResponse(Call<List<Ingredient>> call, Response<List<Ingredient>> response) {
//                    if (response.isSuccessful() && response.body() != null) {
//                        List<Ingredient> ingredients = response.body();
//                        Map<Integer, String> compressedDict = new LinkedHashMap<>();
//                        for (Ingredient ing : ingredients) {
//                            compressedDict.put(ing.getId(), ing.getName());
//                        }
//                        Map<String, Object> wrapper = new HashMap<>();
//                        wrapper.put("ingredients", compressedDict);
//                        String ingredientsJson = new Gson().toJson(wrapper);
//                        Log.d("API", "Compressed JSON: " + ingredientsJson);
//                    }
//                }
//
//
//                @Override
//                public void onFailure(Call<List<Ingredient>> call, Throwable t) {
//                    Log.e("API", "Ошибка загрузки ингредиентов", t);
//                }
//            });
//
//
//
//
//            Content content = new Content.Builder()
//                    .addImage(bitmap)
//                    .addText(textPrompt)
//                    .build();
//
//            ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
//
//            return Futures.transform(response, it -> {
//                if (it == null) {
//                    return "Error: Empty response.";
//                }
//                String text = it.getText();
//                if (text == null) {
//                    return "Error: No text found in response.";
//                }
//                return text;
//            }, Executors.newSingleThreadExecutor());
//        } catch (Exception e) {
//            return Futures.immediateFailedFuture(e);
//        }

}
