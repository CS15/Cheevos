package org.cs15.xchievements.Repository;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.cs15.xchievements.R;
import org.cs15.xchievements.misc.Singleton;
import org.cs15.xchievements.misc.UserProfile;
import org.cs15.xchievements.objects.Achievement;
import org.cs15.xchievements.objects.GameDetails;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Database {
    private Context mContext;

    public Database() {
    }

    public Database(Context context) {
        mContext = context;
    }

    public void login(final String email, final String password, final ILogin callback) {
        ParseUser.logInInBackground(email, password, new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    callback.onSuccess("Successfully logged in");
                } else {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    public void logout() {
        ParseUser.logOut();
    }

    public void register(final String email, final String password, final String gamertag, final IRegister callback) {
        String url = "https://xboxapi.com/v2/xuid/" + gamertag;

        RequestQueue queue = Singleton.getRequestQueque();

        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                ParseUser user = new ParseUser();
                user.setUsername(email);
                user.setPassword(password);
                user.setEmail(email);
                user.put("gamertag", gamertag);
                user.put("xboxId", response);
                user.put("isAnAdmin", false);
                user.put("postCount", 0);

                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            callback.onSuccess("Successfully register and logged in");
                        } else {
                            callback.onError(e.getMessage());
                        }
                    }
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onError("Gamertag do not exist");
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("X-AUTH", "c298a7edee735d5559a219b0020a60fb9bb657dc");
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(20000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    public void getBannerInfo(final IBanner callback) {

        // get data from database
        final ParseQuery<ParseObject> parseObject = ParseQuery.getQuery("Screenshots");
        parseObject.orderByDescending("createdAt");
        parseObject.setLimit(1);
        parseObject.include("game");
        parseObject.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> data, ParseException e) {
                if (e == null) {
                    callback.onSuccess(data.get(0));
                } else {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    public void getLatestAchs(final ArrayList<GameDetails> list, final ILatestAchs callback) {

        // get data from database
        ParseQuery<ParseObject> parseObject = ParseQuery.getQuery("Games");
        parseObject.orderByDescending("updatedAt");
        parseObject.setLimit(25);
        parseObject.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> data, ParseException e) {
                if (e == null) {
                    for (ParseObject g : data) {
                        GameDetails game = new GameDetails();
                        game.setId(g.getInt("gameId"));
                        game.setCoverUrl(g.getString("coverUrl"));
                        game.setTitle(g.getString("title"));
                        game.setAchievementsAmount(g.getInt("achsAmount"));
                        list.add(game);
                    }

                    callback.onSuccess(list);
                } else {
                    callback.onError(e.getMessage());
                }

            }
        });
    }

    public void getApkVersion() {
        // get data from database
        ParseQuery<ParseObject> parseObject = ParseQuery.getQuery("Apks");
        parseObject.orderByDescending("updatedAt");
        parseObject.setLimit(1);
        parseObject.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> data, ParseException e) {
                if (e == null) {
                    try {
                        int apkVersionCode = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;

                        if (apkVersionCode < data.get(0).getInt("versionCode")) {
                            getApk(data.get(0));
                        }

                    } catch (PackageManager.NameNotFoundException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    Log.e("ParseObject", "Error: " + e.getMessage());
                }
            }
        });
    }

    private void getApk(final ParseObject parseObject) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(R.string.dialog_update_apk_message);
        builder.setIcon(R.drawable.ic_app_logo);
        builder.setTitle(R.string.dialog_update_apk_title);
        builder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                final ProgressDialog progressDialog = new ProgressDialog(mContext);
                progressDialog.setTitle("Downloading...");
                progressDialog.setMessage("Please wait.");
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();

                ParseFile apk = (ParseFile) parseObject.get("apk");
                apk.getDataInBackground(new GetDataCallback() {
                    public void done(byte[] data, ParseException e) {
                        if (e == null) {
                            try {
                                String path = Environment.getExternalStorageDirectory() + "/Download/xchievements.apk";

                                File file = new File(path);
                                file.getParentFile().mkdirs();
                                file.createNewFile();

                                BufferedOutputStream objectOut = new BufferedOutputStream(new FileOutputStream(file));

                                objectOut.write(data);

                                objectOut.close();

                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");

                                mContext.startActivity(intent);

                                progressDialog.dismiss();

                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }

                        }
                    }
                });
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        builder.create();
        builder.show();
    }

    public void getFavorites(final ArrayList<GameDetails> list, final IFavorites callback) {
        // get data from database
        ParseQuery<ParseObject> parseObject = UserProfile.getCurrentUser().getRelation("favorites").getQuery();
        parseObject.orderByAscending("title");
        parseObject.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> data, ParseException e) {
                if (e == null) {
                    list.clear();

                    for (ParseObject g : data) {
                        GameDetails game = new GameDetails();
                        game.setId(g.getInt("gameId"));
                        game.setCoverUrl(g.getString("coverUrl"));
                        game.setTitle(g.getString("title"));
                        game.setAchievementsAmount(g.getInt("achsAmount"));
                        list.add(game);
                    }

                    callback.onSuccess(list);
                } else {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    public void getGameImages(final String gameId, final List<String> list, final IGameImages callback) {
        String url = "http://www.giantbomb.com/api/game/3030-" + gameId + "/?api_key=" + mContext.getString(R.string.gb_api) + "&format=json&field_list=images";

        RequestQueue queue = Singleton.getRequestQueque();

        JsonObjectRequest request = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject data) {
                        try {
                            int size = data.getJSONObject("results").getJSONArray("images").length();

                            for (int i = 0; i < size; i++) {
                                list.add(data.getJSONObject("results").getJSONArray("images").getJSONObject(i).getString("super_url"));
                            }

                            callback.onSuccess(list);

                        } catch (JSONException e) {
                            callback.onError(e.getMessage());
                        }
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                callback.onError("Game images not found");
            }
        });

        request.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    public void getGameList(final String alphabetLetter, final List<GameDetails> list, final IGameList callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Games");
        if (!alphabetLetter.equals("all")) {
            query.whereStartsWith("title", alphabetLetter.toUpperCase());
        }
        query.orderByAscending("title");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    for (ParseObject obj : parseObjects) {
                        GameDetails game = new GameDetails();
                        game.setId(obj.getInt("gameId"));
                        game.setCoverUrl(obj.getString("coverUrl"));
                        game.setTitle(obj.getString("title"));
                        game.setAchievementsAmount(obj.getInt("achsAmount"));
                        game.setGamerscore(obj.getInt("gamerscore"));
                        game.setAchievementsPageUrl(obj.getString("achsUrl"));
                        list.add(game);
                    }

                    callback.onSuccess(list);
                } else {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    public void getComments(final String achParseId, final String newsFeedId, final IAchComments callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Comments");
        query.include("achievement");
        query.include("user");
        query.include("newsFeed");
        query.whereEqualTo("achievementId", achParseId);
        query.whereEqualTo("newsFeedId", newsFeedId);
        query.orderByDescending("createdAt");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    callback.onSuccess(parseObjects);
                } else {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    public void addComment(final String parseAchId, final String parseNewsFeedId, final String comment, final IAddComment callback) {

        ParseObject obj = new ParseObject("Comments");
        obj.put("user", ParseObject.createWithoutData("_User", ParseUser.getCurrentUser().getObjectId()));
        obj.put("comment", comment);

        if (parseAchId != null) {
            obj.put("achievement", ParseObject.createWithoutData("Achievements", parseAchId));
            obj.put("achievementId", parseAchId);
        }

        if (parseNewsFeedId != null ) {
            obj.put("newsFeed", ParseObject.createWithoutData("NewsFeed", parseNewsFeedId));
            obj.put("newsFeedId", parseNewsFeedId);
        }

        obj.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {

                    ParseUser.getCurrentUser().increment("postCount");
                    ParseUser.getCurrentUser().saveInBackground();

                    // update counter
                    if (parseAchId != null) {
                        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Achievements");
                        parseQuery.getInBackground(parseAchId, new GetCallback<ParseObject>() {
                            public void done(ParseObject parseObject, ParseException e) {
                                if (e == null) {
                                    parseObject.increment("commentCounts");

                                    parseObject.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                callback.onSuccess("Successfully added comment");
                                            } else {
                                                callback.onError(e.getMessage());
                                            }
                                        }
                                    });
                                } else {
                                    callback.onError(e.getMessage());
                                }
                            }
                        });
                    } else if (parseNewsFeedId != null) {
                        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("NewsFeed");
                        parseQuery.getInBackground(parseNewsFeedId, new GetCallback<ParseObject>() {
                            public void done(ParseObject parseObject, ParseException e) {
                                if (e == null) {
                                    parseObject.increment("commentCounts");

                                    parseObject.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                callback.onSuccess("Successfully added comment");
                                            } else {
                                                callback.onError(e.getMessage());
                                            }
                                        }
                                    });
                                } else {
                                    callback.onError(e.getMessage());
                                }
                            }
                        });
                    }
                } else {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    public void getGameDetails(final String gbGameId, final GameDetails gameDetails, final IGameDetails callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Games");
        query.whereEqualTo("gameId", gameDetails.getId());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> data, ParseException e) {
                if (e == null) {
                    if (data.size() > 0) {
                        // set data data
                        for (ParseObject game : data) {
                            gameDetails.setId(game.getInt("gameId"));
                            gameDetails.setGbGameId(game.getString("gbGameId"));
                            gameDetails.setParseId(game.getObjectId());
                            gameDetails.setAchievementsAmount(game.getInt("achsAmount"));
                            gameDetails.setGamerscore(game.getInt("gamerscore"));
                            gameDetails.setGame(game);
                        }

                        getGameDetailsFromGB(gameDetails.getGbGameId(), gameDetails, callback);

                    } else {
                        callback.onError("No game details available");
                    }
                } else {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    private void getGameDetailsFromGB(final String gbGameId, final GameDetails gameDetails, final IGameDetails callback) {
        String url = "http://www.giantbomb.com/api/game/3030-" + gbGameId + "/?api_key=" + mContext.getResources().getString(R.string.gb_api) + "&format=json&field_list=name,image,deck,original_release_date,developers,publishers,genres";

        RequestQueue queue = Singleton.getRequestQueque();

        JsonObjectRequest request = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject data) {
                        try {
                            JSONObject obj = data.getJSONObject("results");
                            String cover = (!obj.isNull("image")) ? obj.getJSONObject("image").getString("super_url") : "N/A";
                            String title = (!obj.isNull("name")) ? obj.getString("name") : "N/A";
                            String summary = (!obj.isNull("deck")) ? obj.getString("deck") : "N/A";
                            String developer = (!obj.isNull("developers")) ? obj.getJSONArray("developers").getJSONObject(0).getString("name") : "N/A";
                            String date = (!obj.isNull("original_release_date")) ? new SimpleDateFormat("MMM dd, yyyy").format(new Date(obj.getString("original_release_date").replace("-", "/"))) : "N/A";
                            String publisher = "";
                            String genres = "";

                            if (obj.getJSONArray("publishers") != null) {
                                for (int i = 0; i < obj.getJSONArray("publishers").length(); i++) {
                                    if (i < obj.getJSONArray("publishers").length() - 1) {
                                        publisher += obj.getJSONArray("publishers").getJSONObject(i).getString("name") + ", ";
                                        continue;
                                    }

                                    publisher += obj.getJSONArray("publishers").getJSONObject(i).getString("name");
                                }
                            } else {
                                publisher = "N/A";
                            }

                            if (obj.getJSONArray("genres") != null) {
                                for (int i = 0; i < obj.getJSONArray("genres").length(); i++) {
                                    if (i < obj.getJSONArray("genres").length() - 1) {
                                        genres += obj.getJSONArray("genres").getJSONObject(i).getString("name") + ", ";
                                        continue;
                                    }

                                    genres += obj.getJSONArray("genres").getJSONObject(i).getString("name");
                                }
                            } else {
                                genres = "N/A";
                            }

                            gameDetails.setTitle(title);
                            gameDetails.setCoverUrl(cover);
                            gameDetails.setDevelopers(developer);
                            gameDetails.setPublishers(publisher);
                            gameDetails.setGenre(genres);
                            gameDetails.setOriginalUsaReleasedDate(date);
                            gameDetails.setSummary(summary);

                            callback.onSuccess(gameDetails);

                        } catch (JSONException e) {
                            callback.onError(e.getMessage());
                        }
                    }

                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                callback.onError(volleyError.getMessage());
            }
        });

        queue.add(request);
    }

    public void getAchievements(final int gameId, final List<Achievement> list, final IAchievements callback) {

        ParseQuery<ParseObject> achievements = ParseQuery.getQuery("Achievements");
        achievements.whereEqualTo("gameId", gameId);
        achievements.orderByAscending("title");
        achievements.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> data, ParseException e) {
                if (e == null) {
                    if (data.size() > 0) {
                        if (UserProfile.getCurrentUser() != null) {

                            // get users completed achievements
                            final ParseQuery<ParseObject> relation = UserProfile.getCurrentUser().getRelation("achsCompleted").getQuery();
                            relation.whereEqualTo("gameId", gameId);
                            relation.findInBackground(new FindCallback<ParseObject>() {
                                @Override
                                public void done(List<ParseObject> parseObjects, ParseException e) {
                                    for(ParseObject achObj : data){
                                        Achievement ach = new Achievement();
                                        ach.setGameId(achObj.getInt("gameId"));
                                        ach.setCoverUrl(achObj.getString("coverUrl"));
                                        ach.setTitle(achObj.getString("title"));
                                        ach.setDescription(achObj.getString("description"));
                                        ach.setGamerscore(achObj.getInt("gamerscore"));
                                        ach.setCommentsCount(achObj.getInt("commentCounts"));
                                        ach.setParseId(achObj.getObjectId());
                                        ach.setCompleted(false);

                                        for(ParseObject obj : parseObjects){
                                            if(achObj.getObjectId().equals(obj.getObjectId())){
                                                ach.setCompleted(true);
                                            }
                                        }

                                        list.add(ach);
                                    }

                                    callback.onSuccess(list);
                                }
                            });
                        } else {
                            for(ParseObject achObj : data){
                                Achievement ach = new Achievement();
                                ach.setGameId(achObj.getInt("gameId"));
                                ach.setCoverUrl(achObj.getString("coverUrl"));
                                ach.setTitle(achObj.getString("title"));
                                ach.setDescription(achObj.getString("description"));
                                ach.setGamerscore(achObj.getInt("gamerscore"));
                                ach.setCommentsCount(achObj.getInt("commentCounts"));
                                ach.setParseId(achObj.getObjectId());
                                ach.setCompleted(false);
                                list.add(ach);
                            }
                            callback.onSuccess(list);
                        }
                    } else {
                        callback.onError("No achievements founded");
                    }
                } else {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    public void checkIfFavorite(final String parseId, final IIsFavorite callback) {

        ParseQuery<ParseObject> relation = UserProfile.getCurrentUser().getRelation("favorites").getQuery();
        relation.whereEqualTo("objectId", parseId);
        relation.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    callback.onSuccess(parseObjects.size() == 1);
                } else {

                }
            }
        });
    }

    public void addToFavorites(boolean isFavorite, final ParseObject game, final IAddToFavorites callback) {
        ParseRelation<ParseObject> relation = UserProfile.getCurrentUser().getRelation("favorites");

        if (!isFavorite) {
            relation.add(game);

            UserProfile.getCurrentUser().saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        callback.onSuccess("Successfully added to favorites!", true);
                    } else {
                        callback.onError(e.getMessage());
                    }
                }
            });
        } else {
            relation.remove(game);

            UserProfile.getCurrentUser().saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        callback.onSuccess("Successfully removed to favorites!", false);
                    } else {
                        callback.onError(e.getMessage());
                    }
                }
            });
        }
    }

    public void saveGameDetails(final GameDetails gameDetails, final ISaveGameDetails callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Games");
        query.whereEqualTo("gameId", gameDetails.getId());
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> data, ParseException e) {
                if (e == null) {
                    if (data.size() == 0) {
                        // upload game data
                        ParseObject game = new ParseObject("Games");
                        game.put("gameId", gameDetails.getId());
                        game.put("title", gameDetails.getTitle());
                        game.put("coverUrl", gameDetails.getCoverUrl());
                        game.put("achsAmount", gameDetails.getAchievementsAmount());
                        game.put("gamerscore", gameDetails.getGamerscore());
                        game.put("achsUrl", gameDetails.getAchievementsPageUrl());
                        game.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    ParseQuery<ParseObject> query = ParseQuery.getQuery("Games");
                                    query.whereEqualTo("gameId", gameDetails.getId());
                                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                                        @Override
                                        public void done(ParseObject parseObject, ParseException e) {
                                            if (e == null) {
                                                callback.onSuccess(parseObject.getObjectId());
                                            } else {
                                                callback.onError(e.getMessage());
                                            }
                                        }
                                    });
                                } else {
                                    callback.onError("Error saving the object: " + e.getMessage());
                                }
                            }
                        });
                    } else {
                        callback.onError("Game already exist");
                    }
                } else {
                    callback.onError("Error getting the object: " + e.getMessage());
                }
            }
        });
    }

    public void saveAchievements(final GameDetails gameDetails, final List<Achievement> list, final String parseGameId, final ISaveAchievements callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Achievements");
        query.whereEqualTo("gameId", gameDetails.getId());
        query.include("game");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> data, ParseException e) {

                int updatedCounter = 0;

                if (e == null) {
                    if (data.size() == 0) {
                        // pointer to game
                        ParseObject gamePointer = ParseObject.createWithoutData("Games", parseGameId);

                        // upload achievements
                        for (int i = 0; i < list.size(); i++) {
                            updatedCounter++;
                            final int finalI = i;
                            final int finalUpdatedCounter = updatedCounter;

                            ParseObject achievement = new ParseObject("Achievements");
                            achievement.put("game", gamePointer);
                            achievement.put("gameTitle", gameDetails.getTitle());
                            achievement.put("gameId", list.get(i).getGameId());
                            achievement.put("title", list.get(i).getTitle());
                            achievement.put("commentCounts", 0);
                            achievement.put("coverUrl", list.get(i).getCoverUrl());
                            achievement.put("description", list.get(i).getDescription());
                            achievement.put("gamerscore", list.get(i).getGamerscore());
                            achievement.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (finalI == list.size() - 1) {
                                        callback.onSuccess(parseGameId, "Successfully uploaded " + finalUpdatedCounter + " Achievements!");
                                    }
                                }
                            });
                        }
                    } else if (list.size() > data.size()) {
                        // get game parse id
                        final String newParseGameId = data.get(0).getParseObject("game").getObjectId();

                        // pointer to game
                        ParseObject gamePointer = ParseObject.createWithoutData("Games", newParseGameId);

                        // update achievements
                        for (int i = data.size(); i < list.size(); i++) {
                            updatedCounter++;
                            final int finalI = i;
                            final int finalUpdatedCounter = updatedCounter;

                            ParseObject achievement = new ParseObject("Achievements");
                            achievement.put("game", gamePointer);
                            achievement.put("gameTitle", gameDetails.getTitle());
                            achievement.put("gameId", list.get(i).getGameId());
                            achievement.put("title", list.get(i).getTitle());
                            achievement.put("commentCounts", 0);
                            achievement.put("coverUrl", list.get(i).getCoverUrl());
                            achievement.put("description", list.get(i).getDescription());
                            achievement.put("gamerscore", list.get(i).getGamerscore());
                            achievement.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (finalI == list.size() - 1) {
                                        callback.onSuccess(newParseGameId, "Successfully updated " + finalUpdatedCounter + " Achievements!");
                                    }
                                }
                            });
                        }

                        data.get(0).getParseObject("game").put("achsAmount", data.get(0).getParseObject("game").getInt("achsAmount") + updatedCounter);
                        data.get(0).saveInBackground();

                    } else {
                        callback.onSuccess(parseGameId, "Achievements up to date.");
                    }
                } else {
                    callback.onError("Error getting the object: " + e.getMessage());
                }
            }
        });
    }

    public void getNewsFeed(final INewsFeeds callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("NewsFeed");
        query.orderByDescending("createdAt");
        query.include("game");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    callback.onSuccess(parseObjects);
                } else {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    public void addToAchCompleted(final List<Achievement> mainList, final List<Achievement> list, final IAddAchComplete callback){
        final ParseRelation<ParseObject> relation = UserProfile.getCurrentUser().getRelation("achsCompleted");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Achievements");
        query.whereEqualTo("gameId", list.get(0).getGameId());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(e == null) {
                    for (ParseObject obj : parseObjects) {

                    }

                    callback.onSuccess(mainList, "Game Achievements size: " + parseObjects.size());
                } else {
                    callback.onError(e.getMessage());
                }
            }
        });

        for (int i = 0; i < list.size(); i++) {
            final int finalI = i;

//            ParseQuery<ParseObject> query = ParseQuery.getQuery("Achievements");
//            query.whereEqualTo("gameId", list.get(0).getGameId());
//            query.findInBackground(new FindCallback<ParseObject>() {
//                @Override
//                public void done(List<ParseObject> parseObjects, ParseException e) {
//                    if(e == null) {
//
//                    } else {
//                        callback.onError(e.getMessage());
//                    }
//                }
//            });




//            query.getInBackground(list.get(i).getParseId(), new GetCallback<ParseObject>() {
//                @Override
//                public void done(ParseObject parseObject, ParseException e) {
//                    relation.add(parseObject);
//
//                    UserProfile.getCurrentUser().saveInBackground(new SaveCallback() {
//                        @Override
//                        public void done(ParseException e) {
//                            if (e == null) {
//                                if (finalI == (list.size() - 1)) {
//                                    getAchievements(list.get(0).getGameId(), mainList, new IAchievements() {
//                                        @Override
//                                        public void onSuccess(List<Achievement> data) {
//                                            callback.onSuccess(data, "Achievements list updated");
//                                        }
//
//                                        @Override
//                                        public void onError(String error) {
//                                            callback.onError(error);
//                                        }
//                                    });
//                                }
//                            } else {
//                                callback.onError(e.getMessage());
//                            }
//                        }
//                    });
//                }
//            });
        }
    }

    /*
     * Database Callbacks
     */
    public interface ILogin {
        void onSuccess(String message);

        void onError(String error);
    }

    public interface IRegister {
        void onSuccess(String message);

        void onError(String error);
    }

    public interface IBanner {
        void onSuccess(ParseObject data);

        void onError(String error);
    }

    public interface ILatestAchs {
        void onSuccess(ArrayList<GameDetails> data);

        void onError(String error);
    }

    public interface IFavorites {
        void onSuccess(ArrayList<GameDetails> data);

        void onError(String error);
    }

    public interface IGameImages {
        void onSuccess(List<String> data);

        void onError(String error);
    }

    public interface IGameList {
        void onSuccess(List<GameDetails> data);

        void onError(String error);
    }

    public interface IAchComments {
        void onSuccess(List<ParseObject> data);

        void onError(String error);
    }

    public interface IAddComment {
        void onSuccess(String message);

        void onError(String error);
    }

    public interface IGameDetails {
        void onSuccess(GameDetails game);

        void onError(String error);
    }

    public interface IAchievements {
        void onSuccess(List<Achievement> data);

        void onError(String error);
    }

    public interface IIsFavorite {
        void onSuccess(boolean isFavorite);
    }

    public interface IAddToFavorites {
        void onSuccess(String message, boolean isFavorite);

        void onError(String error);
    }

    public interface ISaveGameDetails {
        void onSuccess(String parseObjectId);

        void onError(String error);
    }

    public interface ISaveAchievements {
        void onSuccess(String parseGameId, String message);

        void onError(String error);
    }

    public interface INewsFeeds {
        void onSuccess(List<ParseObject> data);
        void onError(String error);
    }

    public interface IAddAchComplete {
        void onSuccess(List<Achievement> data, String message);
        void onError(String error);
    }

}
