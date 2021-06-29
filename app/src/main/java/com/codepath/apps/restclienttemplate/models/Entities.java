package com.codepath.apps.restclienttemplate.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;
@Parcel
public class Entities {
    public ArrayList<String> media;

    public Entities() {
        media = new ArrayList<>();
    }

    public static Entities fromJson(JSONObject jsonObject) throws JSONException {
        Entities entities = new Entities();
        if (jsonObject.has("media")) {
            JSONArray mediaArray = jsonObject.getJSONArray("media");
            for (int i = 0; i<mediaArray.length(); i++) {
                entities.media.add(mediaArray.getJSONObject(i).getString("media_url_https"));
            }
        }
        return entities;
    }
}
