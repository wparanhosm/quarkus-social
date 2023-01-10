package io.github.wparanhosm.quarkussocial.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    public static String FOLLOWER_PATH_REGEX_PATTERN = "/users/[0-9]+/follower";
    public static Boolean matches (String text, String regex){
        final Pattern pattern = Pattern.compile(regex, Pattern.CANON_EQ);
        final Matcher matcher = pattern.matcher(text);
        return matcher.matches();
    }
}
