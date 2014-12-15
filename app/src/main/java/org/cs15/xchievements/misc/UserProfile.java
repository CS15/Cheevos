package org.cs15.xchievements.misc;

import com.parse.ParseUser;

public class UserProfile extends ParseUser {

    public static boolean isAnAdmin() {
        return (getCurrentUser() != null) && getCurrentUser().getBoolean("isAnAdmin");
    }
}
