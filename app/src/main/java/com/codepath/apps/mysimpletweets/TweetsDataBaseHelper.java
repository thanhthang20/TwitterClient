package com.codepath.apps.mysimpletweets;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thangjs on 3/28/16.
 */
public class TweetsDataBaseHelper extends SQLiteOpenHelper {
    // Database Info

    private static final String DATABASE_NAME = "tweetsDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_TWEETS = "tweets";
    private static final String TABLE_USERS = "users";

    // Post Table Columns
    private static final String KEY_TWEET_ID = "id";
    private static final String KEY_TWEET_USER_ID_FK = "userId";
    private static final String KEY_TWEET_TEXT = "text";

    // User Table Columns
    private static final String KEY_USER_ID = "id";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_PROFILE_PICTURE_URL = "profilePictureUrl";

    private static TweetsDataBaseHelper sInstance;

    public static synchronized TweetsDataBaseHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new TweetsDataBaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private TweetsDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Called when the database connection is being configured.
    // Configure database settings for things like foreign key support, write-ahead logging, etc.
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    // Called when the database is created for the FIRST time.
    // If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called.
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_POSTS_TABLE = "CREATE TABLE " + TABLE_TWEETS +
                "(" +
                KEY_TWEET_ID + " TEXT," + // Define a primary key
                KEY_TWEET_USER_ID_FK + " TEXT REFERENCES " + TABLE_USERS + "," + // Define a foreign key
                KEY_TWEET_TEXT + " TEXT" +
                ")";

        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS +
                "(" +
                KEY_USER_ID + " TEXT," +
                KEY_USER_NAME + " TEXT," +
                KEY_USER_PROFILE_PICTURE_URL + " TEXT" +
                ")";

        db.execSQL(CREATE_POSTS_TABLE);
        db.execSQL(CREATE_USERS_TABLE);
    }

    // Called when the database needs to be upgraded.
    // This method will only be called if a database already exists on disk with the same DATABASE_NAME,
    // but the DATABASE_VERSION is different than the version of the database that exists on disk.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TWEETS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            onCreate(db);
        }
    }

    // Insert a post into the database
    public void addTweet(Tweet tweet) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            // The user might already exist in the database (i.e. the same user created multiple posts).
            long userId = addOrUpdateUser(tweet.getUser());

            ContentValues values = new ContentValues();
            values.put(KEY_TWEET_USER_ID_FK, Long.toString(userId));
            values.put(KEY_TWEET_TEXT, tweet.getBody());
            values.put(KEY_TWEET_ID, tweet.getUid().toString());

            // Notice how we haven't specified the primary key. SQLite auto increments the primary key column.
            db.insertOrThrow(TABLE_TWEETS, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("TAG", "Error while trying to add post to database");
        } finally {
            db.endTransaction();
        }
    }

    // Insert or update a user in the database
    // Since SQLite doesn't support "upsert" we need to fall back on an attempt to UPDATE (in case the
    // user already exists) optionally followed by an INSERT (in case the user does not already exist).
    // Unfortunately, there is a bug with the insertOnConflict method
    // (https://code.google.com/p/android/issues/detail?id=13045) so we need to fall back to the more
    // verbose option of querying for the user's primary key if we did an update.
    public long addOrUpdateUser(User user) {
        // The database connection is cached so it's not expensive to call getWriteableDatabase() multiple times.
        SQLiteDatabase db = getWritableDatabase();
        long userId = user.getUid();

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_USER_ID, Long.toString(userId));
            values.put(KEY_USER_NAME, user.getScreenName());
            values.put(KEY_USER_PROFILE_PICTURE_URL, user.getProfileImageUrl());

            // First try to update the user in case the user already exists in the database
            // This assumes userNames are unique
            int rows = db.update(TABLE_USERS, values, KEY_USER_ID + "= ?", new String[]{Long.toString(userId)});

            // Check if update succeeded
            if (rows == 1) {
                // Get the primary key of the user we just updated
                Log.d("DEBUG", user.getScreenName());
                db.setTransactionSuccessful();
            } else {
                // user with this userName did not already exist, so insert new user
                db.insertOrThrow(TABLE_USERS, null, values);
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Log.d("TAG", "Error while trying to add or update user");
        } finally {
            db.endTransaction();
        }
        return userId;
    }

    // Get all posts in the database
    public List<Tweet> getAllTweets() {
        List<Tweet> tweets = new ArrayList<>();

        // SELECT * FROM POSTS
        // LEFT OUTER JOIN USERS
        // ON POSTS.KEY_POST_USER_ID_FK = USERS.KEY_USER_ID
        String POSTS_SELECT_QUERY =
                String.format("SELECT * FROM %s LEFT OUTER JOIN %s ON %s.%s = %s.%s",
                        TABLE_TWEETS,
                        TABLE_USERS,
                        TABLE_TWEETS, KEY_TWEET_USER_ID_FK,
                        TABLE_USERS, KEY_USER_ID);

        // "getReadableDatabase()" and "getWriteableDatabase()" return the same object (except under low
        // disk space scenarios)
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(POSTS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    User newUser = new User();
                    newUser.setScreenName(cursor.getString(cursor.getColumnIndex(KEY_USER_NAME)));
                    newUser.setProfileImageUrl(cursor.getString(cursor.getColumnIndex(KEY_USER_PROFILE_PICTURE_URL)));

                    Tweet newTweet = new Tweet();
                    newTweet.setBody(cursor.getString(cursor.getColumnIndex(KEY_TWEET_TEXT)));
                    newTweet.setUser(newUser);
                    tweets.add(newTweet);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d("TAG", "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return tweets;
    }

    public void deleteAllTweetsAndUsers() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            // Order of deletions is important when foreign key relationships exist.
            db.delete(TABLE_TWEETS, null, null);
            db.delete(TABLE_USERS, null, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d("TAG", "Error while trying to delete all posts and users");
        } finally {
            db.endTransaction();
        }
    }

}
